# Table Of Contents
- [v1.0.2](#v102)
- [v1.0.1](#v101)

# v1.0.2

* Fixes Issue #2. 
  - LDAP StartTLS auth bypass. The password of the user was never verified
    when StartTLS was used. The user is now re-bound on the same
    TLS-protected connection.
  - LDAP filter injection. The username is now escaped in the search filter
    for ldap://, ldaps:// and StartTLS.
  - Re-enable hostname verification during StartTLS negotiation. Also removed
    the JVM-wide `com.sun.jndi.ldap.object.disableEndpointIdentification=true`
    setting, so hostname verification is now on for ldaps:// too.
  - Prevent protocol downgrade for StartTLS. Only TLSv1.2 and TLSv1.3 are
    used. TLSv1.1 and older are no longer tried.
  - AD/LDAP configuration page: the StartTLS checkbox now marks the form as
    changed and enables the Update button.

> [!IMPORTANT]
> Hostname verification is now enforced. The AD/LDAP URI must use a host
> name (or IP address) that is present in the server certificate's Subject
> Alternative Name (or CN). If you connect by an IP address or alias that is
> not in the certificate, authentication will fail after upgrade. Use the
> name in the certificate, or reissue the certificate with the right SAN.
> The CA that signed the certificate must be in the JVM trust store.

> [!NOTE]
> When a user logs in with a password and no passphrase specified, the user can
> list item names, container names etc., but cannot create, modify, share or
> display encrypted notes or items, because that requires the user's
> passphrase. A user who logs in via LDAP cannot change the password, so the
> account cannot be taken over by a password change.
>
> Note, item, container and group names are not encrypted in the database,
> and anyone with database access can read them. This is by design. The GUI
> shows an open lock icon next to such labels, and a tooltip appears when you
> hover over it.


To test:

- Configure a AD/LDAP server with ldap, ldaps and StartTLS.
- Copy the env examples files  at the base of the project:

```
  cp env_ldap.example env_ldap                   # plain ldap://
  cp env_ldaps.example env_ldaps                 # TLS   ldaps://
  cp env_ldap_starttls.example env_ldap_starttls # plain ldap:// upgraded via StartTLS
```
- Edit the files and update LDAP information

- Optional, to enable the hostname verification tests: set
  `LDAP_BAD_HOST_URI` in `env_ldap_starttls` and `LDAPS_BAD_HOST_URI` in
  `env_ldaps` to the same server reached via a name that is NOT in its
  certificate (e.g. an `/etc/hosts` alias). The tests expect authentication
  to fail. Without these keys, those two tests are skipped.

- I tested with JDK 17

- Compile

```  
sh ./compile.sh dev
```

**Java Unit Test**

The tests cover correct and wrong passwords, empty password, LDAP filter
injection and hostname mismatch over ldap://, ldaps:// and StartTLS.

```
mvn test -Dtest=TestLDAP
...
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running test.com.spenego.obidos.TestLDAP
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.91 sec

Results :

Tests run: 14, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.680 s
[INFO] Finished at: 2026-10-04T14:33:39-04:00
[INFO] ------------------------------------------------------------------------
```

**Run Test Scripts**

Requires: OpenLDAP client tools (ldapsearch, ldapwhoami)

These scripts use the OpenLDAP client, not the Obidos code. They verify
that the server and the env files are set up correctly.


**test ldap://**

```
./test_scripts/test_ldap.sh

=== Step 1: bind as service account and search for user ===
  LDAP URL   : ldap://mp50:389
  Base DN    : dc=example,dc=com
  Bind DN    : cn=admin,dc=example,dc=com
  StartTLS   : no
  Filter     : (uid=jdoe)
  Found DN   : uid=jdoe,ou=people,dc=example,dc=com

=== Step 2: bind as the found user DN with the supplied password ===

PASS: authenticated successfully as uid=jdoe,ou=people,dc=example,dc=com
  dn:uid=jdoe,ou=people,dc=example,dc=com
```

**test ldaps://**

```
./test_scripts/test_ldaps.sh
=== Step 1: bind as service account and search for user ===
  LDAP URL   : ldaps://mp50:636
  Base DN    : dc=example,dc=com
  Bind DN    : cn=admin,dc=example,dc=com
  StartTLS   : no
  Filter     : (uid=jdoe)
  Found DN   : uid=jdoe,ou=people,dc=example,dc=com

=== Step 2: bind as the found user DN with the supplied password ===

PASS: authenticated successfully as uid=jdoe,ou=people,dc=example,dc=com
  dn:uid=jdoe,ou=people,dc=example,dc=com
```

**test ldap StartTLS**

```
./test_scripts/test_ldap_starttls.sh
=== Step 1: bind as service account and search for user ===
  LDAP URL   : ldap://mp50:389
  Base DN    : dc=example,dc=com
  Bind DN    : cn=admin,dc=example,dc=com
  StartTLS   : yes
  Filter     : (uid=jdoe)
  Found DN   : uid=jdoe,ou=people,dc=example,dc=com

=== Step 2: bind as the found user DN with the supplied password ===

PASS: authenticated successfully as uid=jdoe,ou=people,dc=example,dc=com
  dn:uid=jdoe,ou=people,dc=example,dc=com
```

(Oct-04-2026)

# v1.0.1

* Initial release

(Aug-02-2026)
