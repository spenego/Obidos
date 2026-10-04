/*
 * Copyright (C) 2016 - 2026 Spenego Software LLC. All rights reserved.
 *
 * This file is part of Obidos from Spenego Software LLC
 *
 * Obidos is dual-licensed under a commercial license and the GNU
 * Affero General Public License (AGPL) v3.0. For commercial licensing,
 * contact Spenego Software LLC at https://spenego.com/contacts.html.
 *
 * For AGPL licensing terms, see the LICENSE file in the project root
 * or <https://www.gnu.org/licenses/>.
 */

package test.com.spenego.obidos;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spenego.Obidos.server.security.LDAPSecurity;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * Tests for Issue #2
 * <p>
 * Requires real LDAP server with credentials. Copy the env examples
 * files  at the base of the project:
 *  env_ldap.example
 *  env_ldaps.example
 *  env_ldap_starttls.example
 *
 * as
 *
 *  env_ldap          - plain, unencrypted ldap://
 *  env_ldaps         - implicit-TLS ldaps://
 *  env_ldap_starttls - plain ldap:// upgraded via StartTLS
 *
 * Edit the files and update LDAP information
 *
 * I tested with JDK 17
 *
 * sh ./compile.sh dev
 * mvn test -Dtest=TestLDAP
 * ...
 * -------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running test.com.spenego.obidos.TestLDAP
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.307 sec

Results :

Tests run: 7, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.680 s
[INFO] Finished at: 2026-10-04T14:33:39-04:00
[INFO] ------------------------------------------------------------------------

 * Test with the scripts:
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
 *
 * Sep 28, 2026 - first cut
 */

public class TestLDAP
{
  private static final Logger logger = LoggerFactory.getLogger(TestLDAP.class);

  // These live at the project root; `mvn test` runs with the project
  // root as the working directory.
  private static final String ENV_FILE_LDAP = "env_ldap";
  private static final String ENV_FILE_LDAPS = "env_ldaps";
  private static final String ENV_FILE_STARTTLS = "env_ldap_starttls";

  private static final int CONNECT_TIMEOUT = 5000;
  private static final int READ_TIMEOUT = 5000;

  private static final String WRONG_PASSWORD = "definitely-the-wrong-password";

  private static boolean envLdapFound;
  private static Map<String, String> envLdap;

  private static boolean envLdapsFound;
  private static Map<String, String> envLdaps;

  private static boolean envStartTlsFound;
  private static Map<String, String> envStartTls;

  @BeforeClass
  public static void loadEnv() throws IOException
  {
    File ldapFile = new File(ENV_FILE_LDAP);
    envLdapFound = ldapFile.isFile();
    envLdap = loadEnvFile(ldapFile);
    warnIfMissing(ENV_FILE_LDAP, envLdapFound);

    File ldapsFile = new File(ENV_FILE_LDAPS);
    envLdapsFound = ldapsFile.isFile();
    envLdaps = loadEnvFile(ldapsFile);
    warnIfMissing(ENV_FILE_LDAPS, envLdapsFound);

    File startTlsFile = new File(ENV_FILE_STARTTLS);
    envStartTlsFound = startTlsFile.isFile();
    envStartTls = loadEnvFile(startTlsFile);
    warnIfMissing(ENV_FILE_STARTTLS, envStartTlsFound);
  }

  private static void warnIfMissing(String fileName, boolean found)
  {
    if (!found)
    {
      logger.warn(fileName + " not found at project root -- its tests will be skipped. "
          + "Copy " + fileName + ".example to " + fileName + " and fill it in to run them.");
    }
  }

 /**
  * Minimal KEY=VALUE parser -- shared shape with what a shell "source"
  * of the same file would see. Blank lines and lines starting with '#'
  * are ignored; values may optionally be wrapped in matching single or
  * double quotes.
  */
  private static Map<String, String> loadEnvFile(File file) throws IOException
  {
    Map<String, String> map = new HashMap<>();
    if (!file.isFile())
    {
      return map;
    }
    try (BufferedReader reader = new BufferedReader(new FileReader(file)))
    {
      String line;
      while ((line = reader.readLine()) != null)
      {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#"))
        {
          continue;
        }
        int eq = trimmed.indexOf('=');
        if (eq < 0)
        {
          continue;
        }
        String key = trimmed.substring(0, eq).trim();
        String value = trimmed.substring(eq + 1).trim();
        if (value.length() >= 2)
        {
          char first = value.charAt(0);
          char last = value.charAt(value.length() - 1);
          if ((first == '"' && last == '"') || (first == '\'' && last == '\''))
          {
            value = value.substring(1, value.length() - 1);
          }
        }
        map.put(key, value);
      }
    }
    return map;
  }

 /**
  * Skips the current test (reported by JUnit as skipped, not failed)
  * unless the given env file was found AND every one of the given
  * keys is present in it.
  */
  private static void assumeEnv(String fileName, boolean found, Map<String, String> map, String... keys)
  {
    Assume.assumeTrue(fileName + " not found at project root -- copy " + fileName
        + ".example and fill it in to run this test", found);
    for (String key : keys)
    {
      Assume.assumeNotNull("Missing " + key + " in " + fileName, map.get(key));
    }
  }

  @Test
  public void testPlainLdapCorrectPassword() throws ServerSideException
  {
    assumeEnv(ENV_FILE_LDAP, envLdapFound, envLdap, "LDAP_URI", "LDAP_BASE_DN", "LDAP_BIND_DN",
        "LDAP_BIND_PASSWORD", "LDAP_AUTH_ATTR", "LDAP_TEST_USERNAME", "LDAP_TEST_PASSWORD");

    boolean authenticated = LDAPSecurity.authenticate(
        envLdap.get("LDAP_URI"), envLdap.get("LDAP_BASE_DN"), envLdap.get("LDAP_BIND_DN"),
        envLdap.get("LDAP_BIND_PASSWORD"), envLdap.get("LDAP_AUTH_ATTR"), envLdap.get("LDAP_TEST_USERNAME"),
        envLdap.get("LDAP_TEST_PASSWORD"), Boolean.FALSE, CONNECT_TIMEOUT, READ_TIMEOUT);

    assertTrue("Correct password over plain ldap:// should authenticate", authenticated);
  }

  @Test(expected = ServerSideException.class)
  public void testPlainLdapWrongPasswordIsRejected() throws ServerSideException
  {
    assumeEnv(ENV_FILE_LDAP, envLdapFound, envLdap, "LDAP_URI", "LDAP_BASE_DN", "LDAP_BIND_DN",
        "LDAP_BIND_PASSWORD", "LDAP_AUTH_ATTR", "LDAP_TEST_USERNAME");

    LDAPSecurity.authenticate(
        envLdap.get("LDAP_URI"), envLdap.get("LDAP_BASE_DN"), envLdap.get("LDAP_BIND_DN"),
        envLdap.get("LDAP_BIND_PASSWORD"), envLdap.get("LDAP_AUTH_ATTR"), envLdap.get("LDAP_TEST_USERNAME"),
        WRONG_PASSWORD, Boolean.FALSE, CONNECT_TIMEOUT, READ_TIMEOUT);
  }

  @Test
  public void testLdapsCorrectPassword() throws ServerSideException
  {
    assumeEnv(ENV_FILE_LDAPS, envLdapsFound, envLdaps, "LDAPS_URI", "LDAP_BASE_DN", "LDAP_BIND_DN",
        "LDAP_BIND_PASSWORD", "LDAP_AUTH_ATTR", "LDAP_TEST_USERNAME", "LDAP_TEST_PASSWORD");

    boolean authenticated = LDAPSecurity.authenticate(
        envLdaps.get("LDAPS_URI"), envLdaps.get("LDAP_BASE_DN"), envLdaps.get("LDAP_BIND_DN"),
        envLdaps.get("LDAP_BIND_PASSWORD"), envLdaps.get("LDAP_AUTH_ATTR"), envLdaps.get("LDAP_TEST_USERNAME"),
        envLdaps.get("LDAP_TEST_PASSWORD"), Boolean.FALSE, CONNECT_TIMEOUT, READ_TIMEOUT);

    assertTrue("Correct password over ldaps:// should authenticate", authenticated);
  }

  @Test(expected = ServerSideException.class)
  public void testLdapsWrongPasswordIsRejected() throws ServerSideException
  {
    assumeEnv(ENV_FILE_LDAPS, envLdapsFound, envLdaps, "LDAPS_URI", "LDAP_BASE_DN", "LDAP_BIND_DN",
        "LDAP_BIND_PASSWORD", "LDAP_AUTH_ATTR", "LDAP_TEST_USERNAME");

    LDAPSecurity.authenticate(
        envLdaps.get("LDAPS_URI"), envLdaps.get("LDAP_BASE_DN"), envLdaps.get("LDAP_BIND_DN"),
        envLdaps.get("LDAP_BIND_PASSWORD"), envLdaps.get("LDAP_AUTH_ATTR"), envLdaps.get("LDAP_TEST_USERNAME"),
        WRONG_PASSWORD, Boolean.FALSE, CONNECT_TIMEOUT, READ_TIMEOUT);
  }

  @Test
  public void testStartTLSCorrectPassword() throws ServerSideException
  {
    assumeEnv(ENV_FILE_STARTTLS, envStartTlsFound, envStartTls, "LDAP_URI", "LDAP_BASE_DN", "LDAP_BIND_DN",
        "LDAP_BIND_PASSWORD", "LDAP_AUTH_ATTR", "LDAP_TEST_USERNAME", "LDAP_TEST_PASSWORD");

    boolean authenticated = LDAPSecurity.authenticate(
        envStartTls.get("LDAP_URI"), envStartTls.get("LDAP_BASE_DN"), envStartTls.get("LDAP_BIND_DN"),
        envStartTls.get("LDAP_BIND_PASSWORD"), envStartTls.get("LDAP_AUTH_ATTR"),
        envStartTls.get("LDAP_TEST_USERNAME"), envStartTls.get("LDAP_TEST_PASSWORD"), Boolean.TRUE,
        CONNECT_TIMEOUT, READ_TIMEOUT);

    assertTrue("Correct password over StartTLS should authenticate", authenticated);
  }

  @Test
  public void testStartTLSWrongPasswordIsRejected_AuthBypassRegression() throws ServerSideException
  {
    assumeEnv(ENV_FILE_STARTTLS, envStartTlsFound, envStartTls, "LDAP_URI", "LDAP_BASE_DN", "LDAP_BIND_DN",
        "LDAP_BIND_PASSWORD", "LDAP_AUTH_ATTR", "LDAP_TEST_USERNAME");

    boolean authenticated = LDAPSecurity.authenticate(
        envStartTls.get("LDAP_URI"), envStartTls.get("LDAP_BASE_DN"), envStartTls.get("LDAP_BIND_DN"),
        envStartTls.get("LDAP_BIND_PASSWORD"), envStartTls.get("LDAP_AUTH_ATTR"),
        envStartTls.get("LDAP_TEST_USERNAME"), WRONG_PASSWORD, Boolean.TRUE, CONNECT_TIMEOUT, READ_TIMEOUT);

    assertFalse("A wrong password over StartTLS must NOT authenticate -- this is the exact "
        + "auth-bypass fixed in LDAPSecurity.java, see ldap_fixes.txt section 2.1",
        authenticated);
  }

  @Test
  public void testStartTLSFilterInjectionIsRejected() throws ServerSideException
  {
    assumeEnv(ENV_FILE_STARTTLS, envStartTlsFound, envStartTls, "LDAP_URI", "LDAP_BASE_DN", "LDAP_BIND_DN",
        "LDAP_BIND_PASSWORD", "LDAP_AUTH_ATTR");

    String maliciousUsername = "*)(|(" + envStartTls.get("LDAP_AUTH_ATTR") + "=*";

    boolean authenticated = LDAPSecurity.authenticate(
        envStartTls.get("LDAP_URI"), envStartTls.get("LDAP_BASE_DN"), envStartTls.get("LDAP_BIND_DN"),
        envStartTls.get("LDAP_BIND_PASSWORD"), envStartTls.get("LDAP_AUTH_ATTR"), maliciousUsername,
        "anything", Boolean.TRUE, CONNECT_TIMEOUT, READ_TIMEOUT);

    assertFalse("An LDAP filter-injection username must not match an arbitrary directory entry over StartTLS",
        authenticated);
  }

  @Test
  public void testPlainLdapFilterInjectionIsRejected() throws ServerSideException
  {
    assumeEnv(ENV_FILE_LDAP, envLdapFound, envLdap, "LDAP_URI", "LDAP_BASE_DN", "LDAP_BIND_DN",
        "LDAP_BIND_PASSWORD", "LDAP_AUTH_ATTR");

    assertNotAuthenticated("filter-injection username over plain ldap://",
        () -> authenticate(envLdap, "LDAP_URI", Boolean.FALSE, injectionUsername(envLdap), "anything"));
  }

  @Test
  public void testLdapsFilterInjectionIsRejected() throws ServerSideException
  {
    assumeEnv(ENV_FILE_LDAPS, envLdapsFound, envLdaps, "LDAPS_URI", "LDAP_BASE_DN", "LDAP_BIND_DN",
        "LDAP_BIND_PASSWORD", "LDAP_AUTH_ATTR");

    assertNotAuthenticated("filter-injection username over ldaps://",
        () -> authenticate(envLdaps, "LDAPS_URI", Boolean.FALSE, injectionUsername(envLdaps), "anything"));
  }

  // An LDAP simple bind with a DN and an empty password is treated by many
  // servers as an unauthenticated (anonymous) bind that "succeeds".

  @Test
  public void testPlainLdapEmptyPasswordIsRejected() throws ServerSideException
  {
    assumeEnv(ENV_FILE_LDAP, envLdapFound, envLdap, "LDAP_URI", "LDAP_BASE_DN", "LDAP_BIND_DN",
        "LDAP_BIND_PASSWORD", "LDAP_AUTH_ATTR", "LDAP_TEST_USERNAME");

    assertNotAuthenticated("empty password over plain ldap://",
        () -> authenticate(envLdap, "LDAP_URI", Boolean.FALSE, envLdap.get("LDAP_TEST_USERNAME"), ""));
  }

  @Test
  public void testLdapsEmptyPasswordIsRejected() throws ServerSideException
  {
    assumeEnv(ENV_FILE_LDAPS, envLdapsFound, envLdaps, "LDAPS_URI", "LDAP_BASE_DN", "LDAP_BIND_DN",
        "LDAP_BIND_PASSWORD", "LDAP_AUTH_ATTR", "LDAP_TEST_USERNAME");

    assertNotAuthenticated("empty password over ldaps://",
        () -> authenticate(envLdaps, "LDAPS_URI", Boolean.FALSE, envLdaps.get("LDAP_TEST_USERNAME"), ""));
  }

  @Test
  public void testStartTLSEmptyPasswordIsRejected() throws ServerSideException
  {
    assumeEnv(ENV_FILE_STARTTLS, envStartTlsFound, envStartTls, "LDAP_URI", "LDAP_BASE_DN", "LDAP_BIND_DN",
        "LDAP_BIND_PASSWORD", "LDAP_AUTH_ATTR", "LDAP_TEST_USERNAME");

    assertNotAuthenticated("empty password over StartTLS",
        () -> authenticate(envStartTls, "LDAP_URI", Boolean.TRUE, envStartTls.get("LDAP_TEST_USERNAME"), ""));
  }

  // Hostname verification. LDAP_BAD_HOST_URI / LDAPS_BAD_HOST_URI must point
  // at the same server as the working URI, but via a name or address that is
  // NOT in the server certificate (e.g. an /etc/hosts alias). The correct
  // password is used, so the only reason to fail is the certificate check.

  @Test
  public void testStartTLSHostnameMismatchIsRejected() throws ServerSideException
  {
    assumeEnv(ENV_FILE_STARTTLS, envStartTlsFound, envStartTls, "LDAP_BAD_HOST_URI", "LDAP_BASE_DN",
        "LDAP_BIND_DN", "LDAP_BIND_PASSWORD", "LDAP_AUTH_ATTR", "LDAP_TEST_USERNAME", "LDAP_TEST_PASSWORD");

    assertNotAuthenticated("StartTLS to a host name not in the server certificate",
        () -> authenticate(envStartTls, "LDAP_BAD_HOST_URI", Boolean.TRUE,
            envStartTls.get("LDAP_TEST_USERNAME"), envStartTls.get("LDAP_TEST_PASSWORD")));
  }

  @Test
  public void testLdapsHostnameMismatchIsRejected() throws ServerSideException
  {
    assumeEnv(ENV_FILE_LDAPS, envLdapsFound, envLdaps, "LDAPS_BAD_HOST_URI", "LDAP_BASE_DN",
        "LDAP_BIND_DN", "LDAP_BIND_PASSWORD", "LDAP_AUTH_ATTR", "LDAP_TEST_USERNAME", "LDAP_TEST_PASSWORD");

    assertNotAuthenticated("ldaps:// to a host name not in the server certificate",
        () -> authenticate(envLdaps, "LDAPS_BAD_HOST_URI", Boolean.FALSE,
            envLdaps.get("LDAP_TEST_USERNAME"), envLdaps.get("LDAP_TEST_PASSWORD")));
  }

  private interface AuthCall
  {
    boolean run() throws ServerSideException;
  }

  private static boolean authenticate(Map<String, String> env, String uriKey, Boolean startTls,
      String username, String password) throws ServerSideException
  {
    return LDAPSecurity.authenticate(
        env.get(uriKey), env.get("LDAP_BASE_DN"), env.get("LDAP_BIND_DN"), env.get("LDAP_BIND_PASSWORD"),
        env.get("LDAP_AUTH_ATTR"), username, password, startTls, CONNECT_TIMEOUT, READ_TIMEOUT);
  }

  private static String injectionUsername(Map<String, String> env)
  {
    return "*)(|(" + env.get("LDAP_AUTH_ATTR") + "=*";
  }

  /**
   * Passes if authentication either returns false or throws
   * ServerSideException; fails only if it reports success.
   */
  private static void assertNotAuthenticated(String what, AuthCall call)
  {
    boolean authenticated;
    try
    {
      authenticated = call.run();
    }
    catch (ServerSideException e)
    {
      return;
    }
    assertFalse("Must NOT authenticate: " + what, authenticated);
  }
}
