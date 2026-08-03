# Install

## Install via Docker or Podman

The easiest way to try out Obidos is to install it using @DOCKER@ or @PODMAN@, please check out the sister
repo @OBIDOS_DOCKER@

## Install from Source

**Note: manuall installation process is quite involved. Support and installer
is available under commercial license. Please contact @SPENEGO@ at @CONTACT@ for
details.**


These installation instructions of Obidos are based on:
```
    A.  Ubuntu 24.04 server.
        (https://releases.ubuntu.com/noble)
    B.  mariadb 11.4
        (https://mariadb.org/download)
    C.  openjdk 17 LTS
        (https://adoptium.net)
    D.  jetty 9.4.56
        (https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-distribution/9.4.56.v20240826/)
```

Stage I : Compile Obidos code for production

```
    Step 1. Install OpenJDK 17
    Step 2. Install git and maven   (apt update; apt install -y git maven)
    Step 3. Clone the code from github (git clone )
            git clone https://github.com/spenego/Obidos.git
    Step 4. cd Obidos; ./compile.sh prod
            (See the file HOW-TO-COMPILE.txt for more details)
    Step 5. After step 4, you will get the war file as target/Obidos-1.0.1.war
            This war file is to be installed on the Jetty server.
```

Stage II : Set up the system to install Obidos

```
    Step 1. Install Ubuntu 24.04 server.
    (Ubuntu Server minmized option will work).
    Step 2. Install openjdk 17 and set up JAVA_HOME for the system.
    Step 3. Install mariadb 11.4
    Step 4. Install jetty 9.4.56
```

Stage III : Set up database schema for Obidos

```
    Step 0. As 'root' start 'mariadb' command line.
    Step 1. Create a database in mariadb (eg: obidos)
    Step 2. Create a database user in mariadb (eg: obidos) and give full
            privileges to the user for the database created in Step 1.
    Step 3. Log in as the user created in Step 2.
    Step 4. Change to the database created in Step 1.
    Step 5. Load the database schema and exit.
            The database dump is in the file scripts/init_db.sql.
            Also run the script scripts/local.sql. This script will set
            password as 'admin' for the root admin with username 'admin'.
    Step 6. Create JDBC xml file for Obidos with the database details.
            The go program to do this: scripts/GenObidosJdbc_linux
```
Stage IV : Install servlet and start Obidos

```
    Step 1. Copy the target/Obidos-1.0.1.war file as ROOT.war file to webapps directory.
    Step 2. Restart jetty
```

Stage V : Log into Obidos using http://<ip address of server>:8080 (this is the default for jetty)

```
    Note that you should set up jetty with https and not use http for Obidos in production.
```
# Development mode

Obidos is written completely in Java. The frontend is  developed with @GWT@

TODO
