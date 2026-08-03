# Installataion
## Install using Docker or Podman

* The easiest way to try out @OBIDOS@ is to install it using @DOCKER@ or @PODMAN@
* Please check out the sister repo @OBIDOS_DOCKER@ for details on how to
install using @DOCKER@ or @PODMAN@

## Manual Installation from source

**Note: manuall installation process is quite involved. Support and installer
is available under commercial license. Please contact @SPENEGO@ at @CONTACT@ for
details.**

This guide walks through building @OBIDOS@ from source and running it on a
single Ubuntu 24.04 machine. No nginx, no dedicated service user, just Jetty
terminating TLS directly with a self-signed certificate. It's meant for
developers and evaluators who want a running @OBIDOS@ with the least ceremony
possible.

**Note:** This is not the production setup. Commercial customers get the
packaged installer (TLS via nginx, hardened service accounts, Cockpit, cron
watchdogs, etc). None of that is covered here.

Tested against:

* Ubuntu 24.04 server
* OpenJDK 17
* MariaDB 11.4
* Jetty 9.4.56

All commands assume you're logged in as a regular user with `sudo` access,
unless a step says otherwise.

**TLS is required**. The GWT client code
reads/writes the system clipboard (`navigator.clipboard`) for copying
secrets, and browsers only expose that API in a secure context: HTTPS.
During logout, @OBIDOS@ clears system clipboard and if transport is not
HTTPS, a JavaScript exception is thrown and logout from the app fails.

A self-signed cert is enough to satisfy the browser's secure context check,
it does not need to be CA-signed for this guide.

### Compiling Obidos from source

```bash
sudo apt update
# Install jdk 17 first, otherwise maven will pull a different version of java
sudo apt install -y git openjdk-17-jdk
sudo apt install -y maven

git clone https://github.com/spenego/Obidos.git
cd Obidos

# Remember where the repo lives, later steps cd elsewhere (jetty_base,
# scripts/) and need to refer back to it
export OBIDOS_SRC="$PWD"

# Must set JAVA_HOME 
export JAVA_HOME=$(readlink -f /usr/bin/javac | sed "s:/bin/javac::")

./compile.sh prod
```

This produces `target/Obidos-<version>.war`. This is the file we'll deploy
as `ROOT.war` further down in [Installing Jetty and deploying the WAR](#installing-jetty-and-deploying-the-war).

### Installing runtime dependencies

@OBIDOS@ uses libsodium (via JNI/libsodium-jna) for its crypto. At
initialization, @OBIDOS@ registers `/usr/local/spenego/lib/` with
libsodium-jna as the only place to look for the native library. This is
intentional, not a fallback, so it doesn't matter that apt's own copy is
elsewhere and registered with `ldconfig`. libsodium-jna never searches the
standard system library paths here, the `.so` file has to actually exist at
that exact path.

```bash
sudo apt install -y libsodium-dev unzip

sudo mkdir -p /usr/local/spenego/lib
sudo cp /usr/lib/x86_64-linux-gnu/libsodium.so /usr/local/spenego/lib/libsodium.so
```

(`libsodium-dev` is what provides that unversioned `libsodium.so` symlink,
`libsodium23`, which actually holds the code, comes along as its
dependency.)

### Setting up the database

```bash
cd "$OBIDOS_SRC/scripts"
sudo ./install-mariadb
```

What this does:

* Creates `/usr/local/spenego/obidos`, then `chown`s all of
  `/usr/local/spenego` to `nobody` with `go+rx` permissions.
* Runs `./GenObidosJdbc_linux` (no arguments). This writes the encrypted
  connection file to `/usr/local/spenego/obidos/jdbc.xml` (a path
  hardcoded inside the binary, not something you can relocate) and prints
  the generated (random) password and connection details to stdout in the
  form `hostname:...|port:...|dbname:...|username:...|password:...`.
* Installs `mariadb-server` via `apt`, enables and starts the service.
* Grants privileges to the generated DB user (`obidos`/`obidos`) and loads
  `init_db.sql` + `local.sql` as `root` (MariaDB's default unix_socket auth
  lets `root` connect passwordlessly when run as root).

You don't need to write down the password it prints. The app reads it back
out of `jdbc.xml` at runtime. If you ever need to see it again later, e.g.
to poke the database by hand with the `mariadb` CLI, run
`./scripts/DecryptObidosJdbc_linux`, which decrypts the existing `jdbc.xml`
without generating a new password.

**Default login after setup:** username `admin`, password `admin`, with a
forced password change on first login (this comes from `local.sql`).

**Note:** `local.sql` sets `system_config.server_port = 443` and
`fqdn = "localhost"`. This guide serves HTTPS on `8443` instead (see
[Installing Jetty and deploying the WAR](#installing-jetty-and-deploying-the-war)),
so any feature that builds an absolute URL (e.g. password reset emails)
will reference the wrong port until you update the **Https Port** field
under **Settings > System Settings** after logging in. Update `fqdn` too
if you're accessing the server by IP or a real hostname rather than
`localhost`.

### Reclaiming ownership of the install directory

`install-mariadb` deliberately locks `/usr/local/spenego` down to `nobody`
after generating `jdbc.xml`, so nothing else needs write access to it by
default. For this dev setup, Jetty will run as your own user, so reclaim it:

```bash
sudo chown -R "$USER":"$USER" /usr/local/spenego
```

### Installing Jetty and deploying the WAR

```bash
export JETTY_HOME=/usr/local/spenego/jetty_home
export JETTY_BASE=/usr/local/spenego/obidos/jetty_base

mkdir -p "$JETTY_HOME"
tar -xzf "$OBIDOS_SRC/dist/obidos/jetty_dist.tar.gz" -C "$JETTY_HOME" --strip-components=1

mkdir -p "$JETTY_BASE"
cd "$JETTY_BASE"
# start.d/ must exist *before* --add-to-start runs, otherwise Jetty falls
# back to writing everything into one monolithic start.ini instead of
# per-module files in start.d/, and start.d/ never gets created at all
mkdir -p "$JETTY_BASE/start.d"
java -jar "$JETTY_HOME/start.jar" --add-to-start=ssl,https,deploy,jsp,ext,resources,console-capture
```

That last command is Jetty's own bootstrap mechanism. It creates
`webapps/`, `lib/ext/`, `logs/`, `etc/`, and the `start.d/*.ini` files for
the modules you asked for: TLS connector, WAR auto-deploy, JSP support, an
extra jars directory, and console log capture. No plain HTTP module is
enabled, see the TLS note in the [Install](#install) section for why.

#### Generating a self-signed certificate

Find the machine's IP address, this is what you'll put in the certificate
and later type into the browser's address bar:

```bash
ip a
```

Look for the `inet` line under your active network interface (e.g. `eth0`
or `enp0s3`), not `lo` (the loopback interface, `127.0.0.1`). For example,
`inet 192.168.1.213/24 ...` means your IP address is `192.168.1.213`.

The `--add-to-start=ssl` step above already copied Jetty's own demo
keystore to `$JETTY_BASE/etc/keystore`. Remove it first, otherwise
`keytool` will try to open it with the demo keystore's own password
instead of creating a fresh one, and fail with "Keystore was tampered
with, or password was incorrect":

```bash
rm -f "$JETTY_BASE/etc/keystore"
```

```bash
keytool -genkeypair -alias jetty -keyalg RSA -keysize 2048 -validity 3650 \
  -storetype PKCS12 -storepass changeit -keypass changeit \
  -keystore "$JETTY_BASE/etc/keystore" \
  -dname "CN=<IP-address>, OU=Obidos Eval, O=Obidos, L=NA, ST=NA, C=US"
```

Replace `<IP-address>` with the IP address you found above. Browsers will
still show a "connection is not private" warning for a self-signed cert,
that's expected, click through it (e.g. Advanced, then Proceed).

Point Jetty's SSL module at this keystore and bind it for remote access, the
module's default only binds `127.0.0.1`:

```bash
cat > "$JETTY_BASE/start.d/99-obidos-ssl.ini" <<'EOF'
jetty.ssl.host=0.0.0.0
jetty.ssl.port=8443
jetty.sslContext.keyStorePassword=changeit
jetty.sslContext.keyManagerPassword=changeit
EOF
```

Now add the JVM flags @OBIDOS@ needs, and deploy the WAR:

```bash
cat > "$JETTY_BASE/start.d/99-obidos.ini" <<'EOF'
--exec
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED
EOF
```

**Note:** Jetty's `start.jar` forks a *child* JVM to actually run the
server. If you pass `--add-opens` on the command line instead, the child
JVM never sees it. These flags only take effect when they come from a
`start.ini`/`start.d` file combined with `--exec`, which tells Jetty to
build the child JVM's command line to include them.

```bash
cp "$OBIDOS_SRC"/target/Obidos-*.war "$JETTY_BASE/webapps/ROOT.war"
cp "$OBIDOS_SRC/dist/obidos/lib/javax.servlet-api-3.1.0.jar" "$JETTY_BASE/lib/ext/"

mkdir -p /usr/local/spenego/obidos/DataStore
```

Notice, `scripts/fix_war.bash` is not needed here. It patches the WAR's
cookie domain to work around a double-cookie bug that only happens when
both Jetty and a reverse proxy (nginx) set cookies. Since this guide talks
to Jetty directly with no proxy in front, skip it, only run it if you later
put this behind nginx.

**Note:** If Jetty fails to start or logging looks broken, the production
`jetty_base` bundles a specific `log4j` + `slf4j-log4j12` binding
(`lib/log4j/log4j-1.2.17.jar`, `lib/slf4j/slf4j-log4j12-1.7.25.jar`) instead
of Jetty's default logging module. If you hit `NoClassDefFoundError`
related to log4j/slf4j, copy those two jars into `$JETTY_BASE/lib/ext/` too.

### Running Jetty as a systemd service

```bash
sudo tee /etc/systemd/system/obidos.service > /dev/null <<EOF
[Unit]
Description=Obidos (Jetty)
After=network.target mariadb.service
Requires=mariadb.service

[Service]
Type=simple
User=$USER
Environment=JETTY_HOME=/usr/local/spenego/jetty_home
Environment=JETTY_BASE=/usr/local/spenego/obidos/jetty_base
WorkingDirectory=/usr/local/spenego/obidos/jetty_base
ExecStart=/usr/bin/java -jar \${JETTY_HOME}/start.jar
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF
```

* Start the service

```
sudo sed -i 's/sslMode=trust/sslMode=disable/g' /usr/local/spenego/obidos/jdbc.xml
sudo systemctl daemon-reload
sudo systemctl enable --now obidos
```

* Check status

```
$ systemctl --no-pager status obidos
obidos.service - Obidos (Jetty)
     Loaded: loaded (/etc/systemd/system/obidos.service; enabled; preset: enabled)
     Active: active (running) since Tue 2026-07-14 14:50:58 EDT; 22h ago
   Main PID: 447478 (java)
      Tasks: 77 (limit: 18510)
     Memory: 562.8M (peak: 615.8M)
        CPU: 6min 39.945s
     CGroup: /system.slice/obidos.service
             ├─447478 /usr/bin/java -jar /usr/local/spenego/jetty_home/start.jar
             └─447499 /usr/lib/jvm/java-17-openjdk-amd64/bin/java -Djava.io.tmpdir=/tmp -Djetty.home=/usr/local/spenego/jetty_home -Djetty.base=/usr/local/spenego/obidos/jetty_base --add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED -cp /usr/local/spenego/jetty_home/lib/mail/javax.mail.glassfish-1.4.1.v201005082020.jar:/usr/local/spenego/obidos/jetty_base/lib/ext/javax.servlet-api-3.1.0.jar:/usr/local/spenego/obidos/jetty_base/resources:/usr/local/spenego/jetty_home/lib/servlet-api-3.1.jar:/usr/local/spenego/jetty_home/lib/jetty-schemas-3.1.jar:/usr/local/spenego/jetty_home/lib/jetty-http-9.4.46.v20220331.jar:/usr/local/spenego/jetty_home/lib/jetty-server-9.4.46.v20220331.jar:/usr/local/spenego/jetty_home/lib/jetty-xml-9.4.46.v20220331.jar:/usr/local/spenego/jetty_home/lib/jetty-util-9.4.46.v20220331.jar:/usr/local/spenego/jetty_home/lib/jetty-io-9.4.46.v20220331.jar:/usr/local/spenego/jetty_home/lib/jetty-jndi-9.4.46.v20220331.jar:/usr/local/spenego/jetty_home/lib/jetty-security-9.4.46.v20220331.jar:/usr/local/spenego/jetty_home/lib/transactions/javax.transaction-api-1.3.jar:/usr/local/spenego/jetty_home/lib/jetty-servlet-9.4.46.v20220331.jar:/usr/local/spenego/jetty_home/lib/jetty-webapp-9.4.46.v20220331.jar:/usr/local/spenego/jetty_home/lib/jetty-plus-9.4.46.v20220331.jar:/usr/local/spenego/jetty_home/lib/jetty-annotations-9.4.46.v20220331.jar:/usr/local/spenego/jetty_home/lib/annotations/asm-9.2.jar:/usr/local/spenego/jetty_home/lib/annotations/asm-analysis-9.2.jar:/usr/local/spenego/jetty_home/lib/annotations/asm-commons-9.2.jar:/usr/local/spenego/jetty_home/lib/annotations/asm-tree-9.2.jar:/usr/local/spenego/jetty_home/lib/annotations/javax.annotation-api-1.3.2.jar:/usr/local/spenego/jetty_home/lib/apache-jsp/org.eclipse.jdt.ecj-3.19.0.jar:/usr/local/spenego/jetty_home/lib/apache-jsp/org.eclipse.jetty.apache-jsp-9.4.46.v20220331.jar:/usr/local/spenego/jetty_home/lib/apache-jsp/org.mortbay.jasper.apache-el-8.5.70.jar:/usr/local/spenego/jetty_home/lib/apache-jsp/org.mortbay.jasper.apache-jsp-8.5.70.jar:/usr/local/spenego/jetty_home/lib/jetty-deploy-9.4.46.v20220331.jar org.eclipse.jetty.xml.XmlConfiguration /tmp/start_12468102937353830621.properties /usr/local/spenego/jetty_home/etc/jetty-bytebufferpool.xml /usr/local/spenego/jetty_home/etc/jetty-threadpool.xml /usr/local/spenego/jetty_home/etc/jetty.xml /usr/local/spenego/jetty_home/etc/jetty-webapp.xml /usr/local/spenego/jetty_home/etc/jetty-plus.xml /usr/local/spenego/jetty_home/etc/jetty-annotations.xml /usr/local/spenego/jetty_home/etc/console-capture.xml /usr/local/spenego/jetty_home/etc/jetty-deploy.xml /usr/local/spenego/jetty_home/etc/jetty-ssl.xml /usr/local/spenego/jetty_home/etc/jetty-ssl-context.xml /usr/local/spenego/jetty_home/etc/jetty-https.xml

Jul 14 14:50:58 test systemd[1]: Started obidos.service - Obidos (Jetty).
Jul 14 14:50:59 test java[447478]: 2026-07-14 14:50:59.366:INFO::main: Logging initialized @86ms to org.eclipse.jetty.util.log.StdErrLog
Jul 14 14:50:59 test java[447478]: 2026-07-14 14:50:59.556:INFO::main: Console stderr/stdout captured to /usr/local/spenego/obidos/jetty_base/logs/2026_07_14.jetty.log
```        


Check logs with `journalctl -u obidos -f` or under `$JETTY_BASE/logs/`.

### Logging in

Point a browser at:

```
https://<IP-address>:8443/
```

Use the same IP address you put in the certificate's `CN` earlier. Accept the
self-signed certificate warning, then:

* Username: `admin`
* Password: `admin`
* You'll be forced to set a new password on first login.

For an actual production deployment (CA-signed cert via nginx, hardened
service account, Cockpit, cron watchdogs, etc), use the commercial installer
instead of extending this guide.

### Changing the Jetty port

To serve on a port other than `8443`, edit `jetty.ssl.port` in
`$JETTY_BASE/start.d/99-obidos-ssl.ini` (created in
[Generating a self-signed certificate](#generating-a-self-signed-certificate)):

```bash
sudo sed -i 's/jetty.ssl.port=8443/jetty.ssl.port=<new-port>/' "$JETTY_BASE/start.d/99-obidos-ssl.ini"
sudo systemctl restart obidos
```

`local.sql` also set `system_config.server_port = 443`, which this guide
already overrides to `8443` (see the note in
[Setting up the database](#setting-up-the-database)). Update the **Https
Port** field under **Settings > System Settings** to match your new port,
otherwise features that build absolute URLs (e.g. password reset emails)
will reference the wrong port.
