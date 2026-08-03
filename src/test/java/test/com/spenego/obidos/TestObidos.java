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

import static com.spenego.Obidos.shared.SharedSetQuality.ONLY_SHARED_WITH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import com.allen_sauer.gwt.log.client.Log;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.SodiumUtils;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.spenego.Obidos.client.rpc.LoginService;
import com.spenego.Obidos.server.actions.AuditActions;
import com.spenego.Obidos.server.actions.ContainerActions;
import com.spenego.Obidos.server.actions.ItemActions;
import com.spenego.Obidos.server.actions.LdapConfigActions;
import com.spenego.Obidos.server.actions.LoginActions;
import com.spenego.Obidos.server.actions.SmtpConfigActions;
import com.spenego.Obidos.server.actions.UserActions;
import com.spenego.Obidos.server.actions.UserDefinedFieldActions;
import com.spenego.Obidos.server.actions.UserDefinedTypeActions;
import com.spenego.Obidos.server.actions.UserDefinedTypeValueActions;
import com.spenego.Obidos.server.actions.UserManagementActions;
import com.spenego.Obidos.server.model.Capability;
import com.spenego.Obidos.server.model.Container;
import com.spenego.Obidos.server.model.ContainerAssignment;
import com.spenego.Obidos.server.model.ContainerGroupAssignment;
import com.spenego.Obidos.server.model.Document;
import com.spenego.Obidos.server.model.Group;
import com.spenego.Obidos.server.model.Item;
import com.spenego.Obidos.server.model.ItemAssignment;
import com.spenego.Obidos.server.model.Ldap;
import com.spenego.Obidos.server.model.LimitedUser;
import com.spenego.Obidos.server.model.NotificationTemplate;
import com.spenego.Obidos.server.model.SharedItem;
import com.spenego.Obidos.server.model.SystemConfig;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.CapabilityOperations;
import com.spenego.Obidos.server.operations.ContainerAssignmentOperations;
import com.spenego.Obidos.server.operations.ContainerGroupAssignmentOperations;
import com.spenego.Obidos.server.operations.ContainerOperations;
import com.spenego.Obidos.server.operations.DocumentOperations;
import com.spenego.Obidos.server.operations.ItemAssignmentOperations;
import com.spenego.Obidos.server.operations.ItemGroupOperations;
import com.spenego.Obidos.server.operations.ItemOperations;
import com.spenego.Obidos.server.operations.LdapConfigOperations;
import com.spenego.Obidos.server.operations.NotificationTemplateOperations;
import com.spenego.Obidos.server.operations.SystemConfigOperations;
import com.spenego.Obidos.server.operations.UserDefinedTypeValueOperations;
import com.spenego.Obidos.server.operations.UserOperations;
import com.spenego.Obidos.server.security.Authenticator;
import com.spenego.Obidos.server.security.Encryption;
import com.spenego.Obidos.server.security.Encryption.PublicKeyDecryptor;
import com.spenego.Obidos.server.security.Encryption.PublicKeyEncryptor;
import com.spenego.Obidos.server.security.Encryption.SecretKeyEncryptor;
import com.spenego.Obidos.server.security.LDAPSecurity;
import com.spenego.Obidos.server.security.LibSodiumPasswordSecurity;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.security.PasswordSecurity;
import com.spenego.Obidos.server.springconfig.scanner.SpringAppConfigScanner;
import com.spenego.Obidos.server.utils.MemoryWiper;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.server.utils.StreamSupplier;
import com.spenego.Obidos.server.utils.TemplateInfo;
import com.spenego.Obidos.server.utils.TemplateInfoBuilder;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.SharedSetQuality;
import com.spenego.Obidos.shared.dto.CapabilityDTO;
import com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.ContainerResult;
import com.spenego.Obidos.shared.dto.DocumentDTO;
import com.spenego.Obidos.shared.dto.GroupDTO;
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.dto.ItemsResult;
import com.spenego.Obidos.shared.dto.LdapConfigurationResult;
import com.spenego.Obidos.shared.dto.LdapDTO;
import com.spenego.Obidos.shared.dto.LimitedSmtpConfigDTO;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;
import com.spenego.Obidos.shared.dto.LimitedUserForAdminDTO;
import com.spenego.Obidos.shared.dto.LimitedUserResult;
import com.spenego.Obidos.shared.dto.LoginActionDTO;
import com.spenego.Obidos.shared.dto.NotificationTemplateJSONDTO;
import com.spenego.Obidos.shared.dto.PasswordAnalysisResults;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.dto.SecurityClassificationDTO;
import com.spenego.Obidos.shared.dto.SharedContainerDTO;
import com.spenego.Obidos.shared.dto.SharedContainerResult;
import com.spenego.Obidos.shared.dto.SharedItemDTO;
import com.spenego.Obidos.shared.dto.SharedItemsResult;
import com.spenego.Obidos.shared.dto.SmtpConfigDTO;
import com.spenego.Obidos.shared.dto.SmtpConfigResult;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldValueDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeResult;
import com.spenego.Obidos.shared.dto.UserDefinedTypeValueDTO;
import com.spenego.Obidos.shared.dto.UserGroupComboResult;
import com.spenego.Obidos.shared.dto.UsersResult;
import com.spenego.Obidos.shared.exceptions.DuplicateRecordException;
import com.spenego.Obidos.shared.exceptions.ItemOwnershipException;
import com.spenego.Obidos.shared.exceptions.LdapNotConfiguredException;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;
import com.spenego.Obidos.shared.exceptions.ObidosAuthenticationException;
import com.spenego.Obidos.shared.exceptions.PassphraseRequiredException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.RecordModifiedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;
import com.spenego.Obidos.shared.exceptions.SharingProhibitedException;
import com.spenego.Obidos.shared.exceptions.UsernameExistsException;

/**
 * Test spring services
 *
 * @author spgdev@spenego.com
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringAppConfigScanner.class }, loader = AnnotationConfigContextLoader.class)
public class TestObidos extends AbstractTransactionalJUnit4SpringContextTests {
	private final static Logger logger = LoggerFactory.getLogger(TestObidos.class);
	private final static String ADMIN_NAME = "adminDTO.unittest";
	// NOTE: userService can not be tested from here anymore as the service
	// enforces login credentials and checks HTTP sessions
	@Autowired private final AuditActions					auditActions = null;
	@Autowired private final Authenticator					authenticator = null;
	@Autowired private final CapabilityOperations			capabilityOperations = null;
	@Autowired final ContainerActions				containerActions = null;
	@Autowired final ContainerAssignmentOperations	containerAssignmentOperations = null;
	@Autowired private final ContainerGroupAssignmentOperations containerGroupAssignmentOperations = null;
	@Autowired final ContainerOperations			containerOperations = null;
	@Autowired private final DocumentOperations 			documentOperations = null;
	@Autowired private final ItemActions					itemActions = null;
	@Autowired private final ItemAssignmentOperations		itemAssignmentOperations = null;
	@Autowired private final ItemGroupOperations			itemGroupOperations = null;
	@Autowired private final ItemOperations					itemOperations = null;
	@Autowired private final LdapConfigActions				ldapConfigActions = null;
	@Autowired private final LdapConfigOperations			ldapConfigOperations = null;
	@Autowired private final LoginActions					loginActions = null;
	@Autowired private final LoginService					loginService = null;
	@Autowired private final MemoryWiper					memoryWiper = null;
	@Autowired private final NotificationTemplateOperations	notificationTemplateOperations = null;
	@Autowired private final UserActions					userActions = null;
	@Autowired private final UserDefinedTypeActions			userDefinedTypeActions = null;
	@Autowired private final UserDefinedTypeValueActions	userDefinedTypeValueActions = null;
	@Autowired private final UserDefinedTypeValueOperations	userDefinedTypeValueOperations = null;
	@Autowired private final UserDefinedFieldActions		userDefinedFieldActions = null;
	@Autowired private final UserManagementActions			userManagementActions = null;
	@Autowired private final UserOperations					userOperations = null;
	@Autowired private final SmtpConfigActions				smtpConfigActions = null;
	@Autowired private final SystemConfigOperations			systemConfigOperationsOperations = null;
	@Autowired
	private Encryption encryption;
	private User admin;
	private UserDTO adminDTO;
	@Autowired
	LDAPSecurity ldapSecurity;

	protected class MockHttpSession implements HttpSession {
		private HashMap<String, Object> map = new HashMap<String, Object>();
		private Vector<String> enums = new Vector<String>();

		public Object getAttribute(String key) {
			return map.get(key);
		}

		public Enumeration<String> getAttributeNames() {
			return enums.elements();
		}

		public long getCreationTime() {
			return 1492613052L;
		}

		public String getId() {
			return "29482904820840238402382984203984230984280423842083";
		}

		public long getLastAccessedTime() {
			return 1492655555L;
		}

		public int getMaxInactiveInterval() {
			return 2220;
		}

		public ServletContext getServletContext() {
			return null;
		}

		public HttpSessionContext getSessionContext() {
			return null;
		}

		public Object getValue(String arg0) {
			return map.get(arg0);
		}

		public String[] getValueNames() {
			String[] arr = null;
			return map.keySet().toArray(arr);
		}

		public void invalidate() { /* no op */ }

		public boolean isNew() {
			return false;
		}

		public void putValue(String key, Object value) {
			map.put(key, value);
		}

		public void removeAttribute(String arg0) {
			map.remove(arg0);
		}

		public void removeValue(String arg0) { /* no impl */ }

		public void setAttribute(String key, Object value) {
			map.put(key, value);
		}

		public void setMaxInactiveInterval(int arg0) { /* no op */ }
	}

	public String[] Colors = new String[]{"purple", "red", "blue", "green", "grey", "black", "yellow"};
	public String[] Animals = new String[]{"monkey", "dog", "cat", "mouse", "cow", "horse", "pig"};

	/**
	 * Various fields are often required of a user.  This class creates everything user related.
	 *
	 */
	public class UserCombo {
		public String passphrase;
		public UserDTO dto;
		public Long id;
		public User user;
		public PassphraseHash passHash;

		UserCombo(String name) {
			String s1 = Colors[(int) ServerUtils.nextRandomLong(Colors.length)];
			String s2 = Animals[(int) ServerUtils.nextRandomLong(Animals.length)];
			passphrase = "This is not a passphrase " + name + " should use=" + s1 + "-" + s2;
			dto = createUserDTO("junit-" + name, "test", "JUnit: Mr " + user, passphrase);
			id = dto.getId();
			user = getUser(id);
			passHash = summonPWHash(passphrase.getBytes(), user);
			
		}
	}

	User getUser(Long id) throws NoSuchRecordException {
		return userOperations.get(id);
	}

	private User createAdmin(String username, String fullname) {
		User u;
		try {
			u = userOperations.getUserByUsername(username);
		} catch(ServerSideException ex) {
			u = new User(username);
			u.setHideItemDelay(30);
			u.setDefaultPage(1);
			u.setEmail1(username + "@example.com");
			u.setPreferenceFlags(0L);
			u.setLoginCount(0);
			u.setUnsuccessfulLoginAttempts(0);

			userOperations.create(u);			
		}
		u.setAdministrator(Boolean.TRUE);
		u.setFullname(fullname);
		userOperations.updateSelective(u);

		Capability c = new Capability(u.getId());
		c.setAll(true);
		capabilityOperations.create(c);

		return getUser(u.getId());
	}

	@Before
	public void before() throws RecordModifiedException, ServerSideException {
		userDefinedTypeValueActions.setRunningInUnitTest();
		itemActions.setRunningInUnitTest();
		userDefinedTypeActions.setRunningInUnitTest();
		userDefinedFieldActions.setRunningInUnitTest();
		auditActions.setRunningInUnitTest();
		containerActions.setRunningInUnitTest();
		userActions.setRunningInUnitTest();
		memoryWiper.setMemoryWipeDelay(0);

		MockHttpSession session = new MockHttpSession();
		authenticator.setSessionSupplier(() -> session);

		try {
			systemConfigOperationsOperations.get(1L);
		} catch(final Exception ex) {
			systemConfigOperationsOperations.create(new SystemConfig(1L, "test.example.com", "xxx.com", "default", 0));
		}

		admin = createAdmin(ADMIN_NAME, "JUnit:Raster");
		containerActions.createDefaultPrivateContainer(admin);
		containerOperations.getContainer(admin.getId(), "Private");
		adminDTO = userManagementActions.getUser(admin, admin.getId());
		authenticator.insertMockState();
	}

	public interface SimpleLambda {
		void lambda() throws ServerSideException;
	}

	public <T> boolean isOf(final Class<T> clazz, final Object obj) {
		return clazz.isInstance(obj);
	}

	private <T> void expectException(final SimpleLambda l, final Class<T> clazz, final String failMsg) {
		try {
			l.lambda();
			fail(failMsg);
		} catch (final ServerSideException ex) {
			if (!isOf(clazz, ex)) {
				fail("Unexpected exception: " + ex);
			}
		}
	}

	private LdapDTO configureLdap(String configName) {
		String url = "ldap://127.0.0.1:3390";
		String baseDN = "dc=spenego,dc=com";
		String userDN = "CN=Administrator,CN=Users," + baseDN;
		String bindPass = "delegator758%";
		LdapDTO ldap = new LdapDTO();
		ldap.setName(configName);
		ldap.setLdapuri(url); // required
		ldap.setBaseDn(baseDN); // required
		ldap.setBindDn(userDN);
		ldap.setBindPass(bindPass);
		ldap.setAuthAttr("CN");
		try {
			ldapConfigActions.create(admin, ldap);
		} catch (RecordModifiedException e1) {
			logger.error("XXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			e1.printStackTrace();
			return null;
		} catch (ServerSideException e1) {
			logger.error("YYYYYYYYYYYYYYYYYYYYYYYYYYYY " + e1);
			e1.printStackTrace();
			return null;
		}
		return ldap;
	}

	@Test
	public void testSaveLdapSettings() throws ServerSideException {
		String name = "LDAP for testing saving";
		LdapDTO dto = configureLdap(name);
		assertNotNull(dto);
		try {
			Ldap l = ldapConfigOperations.get(name);
			assertNotNull(l);
			assertEquals(dto.getLdapuri(), l.getLdapuri());
			assertEquals(dto.getBaseDn(), l.getBaseDn());
			assertEquals(dto.getBindDn(), l.getBindDn());
		} catch (LdapNotConfiguredException e) {
			e.printStackTrace();
			throw e;
		}
		// now get the settings with actions
		try {
			LdapDTO ldapDTO = ldapConfigActions.get(name);
			assertEquals(dto.getLdapuri(), ldapDTO.getLdapuri());
			assertEquals(dto.getBaseDn(), ldapDTO.getBaseDn());
			assertEquals(dto.getBindDn(), ldapDTO.getBindDn());
		} catch (LdapNotConfiguredException e) {
			e.printStackTrace();
			throw e;
		}
	}

	// don't test
	@Test
	public void testZSpgDevLdap() {
		String name = "Spgdev ldaps tunnel";
		// broken, doesn't decrypt bind pass
		// Ldap ldap = ldapConfigOperations.get(name);
		// logger.info("ldap pass: " + ldap.getBindPass());
		LdapDTO dto;
		try {
			dto = ldapConfigActions.get(name);
			logger.info("ldap pass: " + dto.getBindPass());
		} catch (LdapNotConfiguredException e1) {
			logger.info("Exception: " + e1.getMessage());
		} catch (ServerSideException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String username = "administrator";
		String password = "secret";
		boolean rc;
		try {
			rc = ldapSecurity.authenticate(username, password, name);
			logger.info("rc: " + rc);
		} catch (ServerSideException e) {
			// TODO Auto-generated catch block
			logger.info("Authentication failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testGetLdapSettings() throws ServerSideException {
		configureLdap("LDAP Testx-1");
		configureLdap("LDAP Testx-2");
		configureLdap("LDAP Testx-3");
		LdapConfigurationResult result = ldapConfigActions.getAllConfigs(admin, "LDAP Testx-", 0, 100);
		logger.info("number of ldap settings: " + result.getTotal());
		assertEquals(3, (int) result.getTotal());
	}

	private void modifyUser(User user, UserDTO dto) throws ServerSideException {
		userManagementActions.modifyUser(user, dto);
	}

	private static User clearPasswordReset(final Long userId) {
		final User user = new User(userId, (String) null);
		user.setPasswordChangeRequired(false);
		return user;
	}

	private Long clearPasswordResetRequired(final Long userId) {
		userOperations.updateSelective(clearPasswordReset(userId));
		return userId;
	}

	private Long createUser(UserDTO userInfo) throws ServerSideException {
		if (userInfo.getEmail1() == null) {
			userInfo.setEmail1("joe@example.com");
		}
		Boolean notifyUser = false;
		String emailComment = null;
		final Long id = clearPasswordResetRequired(userManagementActions.createUser(admin, userInfo, null, notifyUser, emailComment));
		userInfo.setId(id);
		return id;
	}

	private Long createUser(String username, String password, String authSource, String fullname, String emailAddr) throws ServerSideException {
		final UserDTO userInfo = new UserDTO();
		userInfo.setUsername(username);
		userInfo.setPassword(password);
		userInfo.setAuthSource(authSource);
		userInfo.setFullname(fullname);
		userInfo.setEmail1(emailAddr == null ? username + "@example.com" : emailAddr);
		Boolean notifyUser = false;
		String emailComment = null;
		return clearPasswordResetRequired(userManagementActions.createUser(admin, userInfo, new CapabilityDTO(1L, Boolean.TRUE), notifyUser, emailComment));
	}

	private Long createUser(String username, String password, String authSource, String fullname) throws ServerSideException {
		return createUser(username, password, authSource, fullname, null);
	}

	private List<LimitedUserForAdminDTO> getAdmins(final User caller, final String search, final Integer first, final Integer count, final Integer expected_total, final OrderBy orderby) throws ServerSideException {
		logger.info("Getting " + (count == null ? "unlimited" : count.toString()) + " admins " + (search != null ? ("matching " + search + " ") : "")
				+ (count == null ? "without skipping any" : ", skipping the first " + first) + ", order by = " + orderby);
		UserDTO template = new UserDTO();
		template.setAdministrator(true);
		template.setUsername(search);
		UsersResult<LimitedUserForAdminDTO> result = userManagementActions.getUsers(caller, template, null, first, count, toList(orderby));
		List<LimitedUserForAdminDTO> list = result.getUsers();
		assertEquals(expected_total, result.getTotalUsers());
		return list;
	}

	private List<LimitedUserForAdminDTO> getUsers(User caller, UserDTO template, List<Long> preSelectedUsers, Integer first, Integer count, Integer expected_total, OrderBy orderby) throws ServerSideException {
		UsersResult<LimitedUserForAdminDTO> result = userManagementActions.getUsers(caller, template, preSelectedUsers, first, count, toList(orderby));
		List<LimitedUserForAdminDTO> list = result.getUsers();
		if (!expected_total.equals(result.getTotalUsers()) && result.getTotal() != 0) {
			for (LimitedUserForAdminDTO u : result.getUsers()) {
				logger.error("User " + u.getFullname() + ", username = " + u.getUsername());
			}
		}

		assertEquals(expected_total, result.getTotalUsers());
		return list;
	}
	
	private List<LimitedUserForAdminDTO> getUsers(User caller, String search, Integer first, Integer count, Integer expected_total, OrderBy orderby) throws ServerSideException {
		logger.info("Getting " + (count == null ? "unlimited" : count.toString()) + " users " + (search != null ? ("matching " + search + " ") : "")
				+ (count == null ? "without skipping any" : ", skipping the first " + first) + ", order by = " + orderby);
		UserDTO template = new UserDTO();
		template.setFullname(search);
		template.setAdministrator(false);
		return getUsers(caller, template, null, first, count, expected_total, orderby);
	}

	private List<LimitedUserForAdminDTO> getUsers(User caller, String search, Integer first, Integer count, Integer expected_total) throws ServerSideException {
		logger.info("Getting " + (count == null ? "unlimited" : count.toString()) + " users " + (search != null ? ("matching " + search + " ") : "")
				+ (count == null ? "without skipping any" : ", skipping the first " + first));
		UserDTO template = new UserDTO();
		template.setFullname(search);
		template.setAdministrator(false);
		final List<LimitedUserForAdminDTO> users = getUsers(caller, template, null, first, count, expected_total, null);
		for (LimitedUserForAdminDTO u : users) {
			logger.info("Loaded user: " + u.getUsername());
		}
		return users;
	}

	private List<LimitedUserForAdminDTO> getUsers(User caller, UserDTO pattern, Integer first, Integer count, Integer expected_total, OrderBy orderby) throws ServerSideException {
		logger.info("Getting " + (count == null ? "unlimited" : count.toString()) + " users " + (pattern != null ? ("matching " + pattern + " ") : "")
				+ (count == null ? "without skipping any" : ", skipping the first " + first) + ", order by = " + orderby);
		UsersResult<LimitedUserForAdminDTO> result = userManagementActions.getUsers(caller, pattern, null, first, count, toList(orderby));
		List<LimitedUserForAdminDTO> list = result.getUsers();
		logger.info("expected total = " + expected_total);
		logger.info("actual total = " + result.getTotal());
		if (!expected_total.equals(result.getTotalUsers()) && result.getTotal() != 0) {
			for (LimitedUserDTO u : result.getUsers()) {
				logger.error("User " + u.getFullname() + ", locked = " + u.getLocked());
			}
		}
		assertEquals(expected_total, result.getTotalUsers());
		return list;
	}

	private void lockUser(User u) throws RecordModifiedException, ServerSideException {
		User newUser = new User(u.getId(), u.getVersion());
		newUser.setLocked(true);
		userOperations.updateSelective(newUser);
	}

	private UsersResult<LimitedUserForAdminDTO> getAdmins(User caller, Boolean deleted) {
		UserDTO template = new UserDTO();
		template.setAdministrator(true);
		template.setDeleted(deleted);
		return userManagementActions.getUsers(caller, template, null, null, null, null);
	}

	@Test
	public void testGetUserList() throws ServerSideException {
		String authSource = "local";
		Long aliceId = createUser("junit-alice", "(@#(*@&#(*@&", authSource, "JUnit: Mrs Alice Blaster");
		Long rasterId = createUser("junit-raster", "zippy123", authSource, "JUnit: Dr Raster Blaster");
		Long zoidbergId = createUser("junit-zoidberg", "frizzle&^%", authSource, "JUnit: Dr Zoidberg");
		createUser("junit-SomeOtherUser", "*(&^!@&!^%$@&^%*!@$#$  24sisudfh", authSource, "JUnit: And Some Other User");
		Long zoidBlumId = createUser("junit-zoidblum", "sizzle&^%", authSource, "JUnit: Mr Zoidblum");
		createUser("licenseuser", "drizzle&^%", authSource, "JUnit: License User");
		User admin2 = createAdmin(ADMIN_NAME + "xxx", "JUnit:RasterXXX");
		assertTrue(admin2.getAdministrator());
		assertFalse(admin2.getDeleted());
		// ensure we only create MAX_USERS users
		User alice = getUser(aliceId);
		User raster = getUser(rasterId);
		User zoidberg = getUser(zoidbergId);
		assertTrue(adminDTO.getAdministrator());
		assertFalse(alice.getAdministrator());
		assertFalse(raster.getAdministrator());
		assertFalse(zoidberg.getAdministrator());
		assertFalse(adminDTO.getDeleted());
		assertFalse(alice.getDeleted());
		assertFalse(raster.getDeleted());
		assertFalse(zoidberg.getDeleted());

		final UserDTO zoidbergDTO = new UserDTO(zoidbergId, false);
		zoidbergDTO.setInstantMessageId("zoidberg256");
		zoidbergDTO.setEmail3("zoidberg@yahoo.com");

		userManagementActions.modifyUser(zoidberg, zoidbergDTO);
		final UserDTO zberg1 = userManagementActions.getUser(zoidberg, zoidbergId);
		assertEquals(zoidbergDTO.getInstantMessageId(), zberg1.getInstantMessageId());
		assertEquals(zoidbergDTO.getEmail3(), zberg1.getEmail3());

		final UserDTO zberg = userManagementActions.getUser(alice, zoidbergId);
		assertEquals(zoidbergDTO.getInstantMessageId(), zberg.getInstantMessageId());
		assertEquals(zoidbergDTO.getEmail3(), zberg.getEmail3());
	
		try {
			// Test pagination
			logger.info("Getting admins");
			List<LimitedUserForAdminDTO> admins = getAdmins(admin, false).getUsers();
			logger.info("Found " + admins.size() + " admins");
			for(final LimitedUserForAdminDTO user : admins) {
				logger.info("User " + user.getFullname()  + ", id = " + user.getId());
				if (!getUser(user.getId()).getAdministrator()) {
					fail("User " + user.getFullname() + " is NOT an administrator");
				}
			}

			assertTrue(admins.size() > 1);
			assertNull(getAdmins(admin, "junit-zoid", null, null, 0, null)); // get all results, total = 0 no match on search
			UserDTO template = new UserDTO();
			template.setUsername("junit-");
			assertEquals(5, getUsers(admin, template, null, null, 5, null).size()); // get all results, total = 5
			template.setFullname("JUnit:");
			template.setUsername(null);
			template.setAdministrator(false);
			expectException(() -> userManagementActions.getUsers(alice, template, null, null, 5, null), ServerSideException.class, "User can't call this method. getUsers should have thrown exception.");
			assertEquals(6, getUsers(admin, template, null, null, 6, null).size()); // get all results, total = 6

			for(final LimitedUserForAdminDTO user : getUsers(admin, template, null, null, 6, null)) {
				logger.info("User " + user.getFullname()  + ", id = " + user.getId());
				if (getUser(user.getId()).getAdministrator()) {
					fail("User " + user.getFullname() + " IS an administrator");
				}
			}

			template.setAdministrator(true);
			// Should get one admin: admin2
			assertEquals(1, getUsers(admin, template, null, null, 1, null).size()); // get all results, total = 5
			template.setAdministrator(null);

			assertEquals(6, getUsers(admin, "JUnit:", null, null, 6).size()); // get all results, total = 6
			assertEquals(5, getUsers(admin, "JUnit:", 1, null, 6).size()); // total 4 but skipping first (result 4)
			assertEquals(2, getUsers(admin, "JUnit:", null, 2, 6).size()); // total of 4 but count = 2
			assertEquals(2, getUsers(admin, "JUnit:", 4, 4, 6).size()); // total of 4 but skipped 4
			assertEquals(2, getUsers(admin, "JUnit:", 1, 2, 6).size()); // total of 6 results, but count = 2
			template.setFullname(null);
			template.setUsername("junit-raster");
			assertEquals(1, getUsers(admin, template, null, null, 1, null).size()); // only 1 raster
			assertEquals(1, getUsers(admin, "Raster", null, null, 1).size()); // only 1 raster
			assertEquals(1, getUsers(admin, "blaster", 1, 4, 2).size()); // skip 1
			assertEquals(1, getUsers(admin, "blaster", 1, 4, 2).size()); // total 2 but first skipped
			assertEquals(1, getUsers(admin, "alic", null, null, 1).size()); // only 1 alic (Alice)
			assertEquals(2, getUsers(admin, "lice", null, null, 2).size()); // two lice users (Alice and licenseuser)
			template.setUsername(null);
			template.setFullname("lice");
			assertEquals(3, getUsers(admin, template, toList(rasterId), null, null, 3, null).size()); // Just licenseuser, Raster and Alice
			assertEquals(1, getUsers(admin, "other", null, null, 1).size()); // case insensitive search of 'Other'
			assertEquals(2, getUsers(admin, "Zoid", null, null, 2).size()); // There are 2 Zoids
			assertEquals(2, getUsers(admin, "JUnit: Dr", null, null, 2).size());
			template.setFullname("JUnit: Dr");
			final List<LimitedUserForAdminDTO> users = getUsers(admin, template, toList(zoidBlumId), null, null, 3, OrderBy.FULLNAME_ASC);		// The Drs and ZoidBlum listed first
			assertEquals("JUnit: Mr Zoidblum", users.get(0).getFullname());				// ZoidBlum listed first since it is specified in 'pre-selected list'
			assertEquals("JUnit: Dr Raster Blaster", users.get(1).getFullname());		// The Drs are last; included because they match the template
			assertEquals("JUnit: Dr Zoidberg", users.get(2).getFullname());
			assertTrue(users.get(0).getSelected());		// ZoidBlum is marked 'selected'
			assertFalse(users.get(1).getSelected());		// next is NOT marked 'selected'
			assertEquals("JUnit: And Some Other User", getUsers(admin, "JUnit:", null, null, 6, OrderBy.FULLNAME_ASC).get(0).getFullname());
			assertEquals("JUnit: Mrs Alice Blaster", getUsers(admin, "JUnit:", null, null, 6, OrderBy.FULLNAME_DESC).get(0).getFullname());
			lockUser(alice);
			UserDTO matchCriteria = new UserDTO();
			matchCriteria.setLocked(true);
			matchCriteria.setAdministrator(false);
			matchCriteria.setDeleted(false);
			matchCriteria.setFullname("JUnit:");

			assertEquals(1, getUsers(admin, matchCriteria, null, null, 1, null).size()); // get locked user list, total = 1
			matchCriteria.setLocked(null);
			assertEquals(6, getUsers(admin, matchCriteria, null, null, 6, null).size()); // get all users, total = 6
			matchCriteria.setLocked(false);
			assertEquals(5, getUsers(admin, matchCriteria, null, null, 5, null).size()); // get all non-locked users, total = 4
			matchCriteria.setUsername("Zoid");
			assertEquals(2, getUsers(admin, matchCriteria, null, null, 2, null).size()); // get all the non-locked Zoid users, total = 2
			matchCriteria.setUsername("Blaster");
			assertEquals(1, getUsers(admin, matchCriteria, null, null, 1, null).size()); // All non-locked Blaster users (Alice is locked), total = 1
			matchCriteria.setLocked(null);
			assertEquals(2, getUsers(admin, matchCriteria, null, null, 2, null).size()); // This will get Alice too, total = 2
			matchCriteria.setUsername("And");
			assertEquals(1, getUsers(admin, matchCriteria, null, null, 1, null).size()); // And Some other user
			matchCriteria.setUsername(null);
			matchCriteria.setAdministrator(true);
			matchCriteria.setLocked(true);
			List<LimitedUserForAdminDTO> list = getUsers(admin, matchCriteria, null, null, 0, null); // Zero locked admins
			assertNull(list);
		} catch (ServerSideException e) {
			e.printStackTrace();
			throw e;
		}
	}

	// userService can not be tested this way anymore as
	// service will enforce check on login credential
	@Test
	public void testDeleteUser() throws ServerSideException {
		Long id1 = createUser("user1", "pass", "local", "Full name of user 1");
		Long id2 = createUser("user2", "pass", "local", "Full name of user 1");
		Long id3 = createUser("user3", "pass", "local", "Full name of user 1");
		try {
			UserDTO u1 = userManagementActions.getUser(admin, id1);
			assertFalse(u1.getDeleted());
			UserDTO u2 = userManagementActions.getUser(admin, id2);
			assertFalse(u2.getDeleted());
			UserDTO u3 = userManagementActions.getUser(admin, id3);
			assertFalse(u3.getDeleted());
			userManagementActions.deleteUsers(admin, toList(id1), false);
			u1 = userManagementActions.getUser(admin, id1);
			assertTrue(u1.getDeleted());
			
			userManagementActions.restoreUsers(admin, toList(id1), null);
			u1 = userManagementActions.getUser(admin, id1);
			assertFalse(u1.getDeleted());

			userManagementActions.delete(admin, toList(id2));
			expectException(() -> userManagementActions.getUser(admin, id2), ServerSideException.class, "User should have been deleted. Load should have thrown exception.");
		} catch (ServerSideException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void testUserCreateFailures() throws ServerSideException {
		String longusername = "this_is_a_username_that_is_over_sixty_characters_long_1234567890";
		String password = "test";
		String authSource = "local";
		String fullname = "This Is My Fullname";
		expectException(() -> createUser(null, password, authSource, fullname), ServerSideException.class, "Expected exception when creating user with null username.");
		expectException(() -> createUser("", password, authSource, fullname), ServerSideException.class, "Expected exception when creating user with empty username.");
		expectException(() -> createUser(longusername, password, authSource, fullname), ServerSideException.class, "Expected exception when creating user with long username.");
	}

	@Test
	public void testUserModifyFailures() throws ServerSideException {
		String username = "eraserhead";
		String password = "there should not be any real password length restriction (*&#(@&(^&#(^@(^#^6q734632q1704783246876^%*&^%*&6";
		String authSource = "local";
		String fullname = "Johnny Smithersonjen";
		Long id = createUser(username, password, authSource, fullname);
		UserDTO dto = new UserDTO();
		dto.setId(id);
		dto.setUsername("");
		// Since updates use a selective update, if username is null, it is by
		// definition, not updated
		expectException(() -> modifyUser(admin, dto), ServerSideException.class, "Expected exception when creating user with empty username.");
		dto.setUsername("this_is_a_username_that_is_over_sixty_characters_long_1234567890");
		expectException(() -> modifyUser(admin, dto), ServerSideException.class, "Expected exception when creating user with long username.");
	}

	// userService can not be tested this way anymore as
	// service will enforce check on login credential
	@SuppressWarnings("unused")
	@Test
	public void testCreateUser() throws ServerSideException {
		String username = "testUser";
		String emailAddr = "betty@example.com";
		String password = "test";
		String authSource = "local";
		String fullname = "This Is My Fullname";
		Long id = createUser(username, password, authSource, fullname, emailAddr);
		try {
			new UserDTO(id, Boolean.TRUE); // ensure contructor does not generate exceptions
			UserDTO u = userManagementActions.getUser(admin, id);
			User user = getUser(id);
			userManagementActions.getUser(user, id);
			assertNotNull(u.getUsername());
			assertEquals(fullname, u.getFullname());
		} catch (ServerSideException e) {
			e.printStackTrace();
			throw e;
		}
		// check hashed password
		User u = userOperations.getUserByUsername(username);
		PasswordSecurity ps = new LibSodiumPasswordSecurity();
		String passwordHash = u.getPasswordDigest();
		assertTrue(ps.verifyPassword(password, passwordHash));
		// test duplicate email address
		Long id2 = createUser("betty", password, authSource, fullname, emailAddr);
		UserDTO u2 = userManagementActions.getUser(admin, id2);
	}

	@Test
	public void testCreateLdapUser() throws ServerSideException {
		String username = "jdoeldap";
		String authSource = "ldap";
		String password = null;
		createUser(username, password, authSource, "Full Name");
		// check auth source
		User u = userOperations.getUserByUsername(username);
		assertEquals("ldap", u.getAuthSource());
	}

	@Test
	public void testChangePassword() throws ServerSideException {
		final String username = "zed";
		final String authSource = ObidosConstants.AUTH_SOURCE_LOCAL;
		final String password = "myPassword65%$1A";				// initial password is not checked for strength
		final String newPassword = "myNewPassword*!@&^%($%";
		final String longPassword = "myL0ngPassword*!@&^%($%*&!@^@_)(_+_=-][}{\\|';';><??.,?><123123;12;::jkhg(8123hg8234g*T*&%^@$#)(*)%$@:''?><?mnzxbcmnMNBikskjhGA876t123g";
		final Long userId = createUser(username, password, authSource, "Zed");
		final User zed = getUser(userId);
		userManagementActions.changePassword(zed, password, newPassword);
		final User z1 = getUser(userId);
		assertNotNull(z1.getUsername()); // thought I saw username being set to null on update
		expectException(() -> userManagementActions.changePassword(z1, password, newPassword),		ServerSideException.class, "Password does not match current password.");
		expectException(() -> userManagementActions.changePassword(z1, newPassword, newPassword),	ServerSideException.class, "Set password to same value");
		expectException(() -> userManagementActions.changePassword(z1, newPassword, password),		ServerSideException.class, "Set password to insufficiently strong");
		userManagementActions.changePassword(z1, newPassword, longPassword);
	}

	private int numberOfUsersSharingContainer(final User owner, final Long containerAssignmentId) throws ServerSideException {
		return userActions.getUsersForContainer(owner, containerAssignmentId, null, SharedSetQuality.ONLY_SHARED_WITH, null, null, null, null).getTotal();
	}

	private int numberOfGroupsSharingContainer(final User owner, final Long containerId) throws ServerSideException {
		return userActions.getGroupsForContainer(owner, containerId, null, SharedSetQuality.ONLY_SHARED_WITH, null, null, null, null).getTotal();
	}

	private int numberOfContainersSharedWithUser(final User caller) throws ServerSideException {
		return containerActions.getContainersSharedWithMe(caller, null, null, null, null, null).getTotal();
	}

	private int numberOfItemsSharedWithUser(final User caller, final Long containerId) throws ServerSideException {
		return itemActions.getItemsSharedWithUser(caller, containerId, null, null, null, null, null).getTotal();
	}

	private int numberOfItemsSharedWithUser(final User caller) throws ServerSideException {
		return numberOfItemsSharedWithUser(caller, null);
	}

	private int numberOfItemsInContainer(final User caller, final Long containerId) throws ServerSideException {
		return itemActions.getMyItems(caller, containerId, null, null, null, null, null, null).getTotal();
	}

	private Long getContainerId(final Long id) throws ServerSideException {
		return containerAssignmentOperations.get(id).getContainerId();
	}

	private int containerAssignmentCount(final Long containerAssignmentId) throws ServerSideException {
		return containerAssignmentOperations.getAll(getContainerId(containerAssignmentId)).collect(Collectors.toList()).size();
	}

	private ContainerResult getContainers(final User caller, final Boolean shared, final OrderBy orderBy) throws ServerSideException {
		return containerActions.getMyContainers(caller, "JUnit:", null, shared, null, null, null, toList(orderBy));
	}

	private static <T> List<T> toList(T t) {
		return Arrays.asList(t);
	}

	private static <T> StreamSupplier<T> toStreamSupplier(T t) {
		return StreamSupplier.create(() -> toList(t));
	}

	private Void shareContainerWithUser(final User user, final Long containerAssignmentId, final Long recipientId, final PassphraseHash hash) throws ServerSideException {
		return containerActions.shareContainerWithUsers(user, containerAssignmentId, toStreamSupplier(recipientId), hash, null, null);
	}

	private Void revokeContainerFromUser(final User user, final Long containerId, final Long recipientId, final PassphraseHash hash) throws ServerSideException {
		return containerActions.revokeContainerFromUsers(user, containerId, toStreamSupplier(recipientId), hash, null, null);
	}

	/**
	 * Computes the passphrase hash but also zeros out the passphrase.
	 *
	 * @param passphrase
	 * @param salt
	 * @return
	 * @throws SodiumLibraryException
	 */
	private static byte[] pwhash(final byte[] passphrase, final byte[] salt) throws SodiumLibraryException {
		return SodiumLibrary.cryptoPwhashArgon2i(passphrase, salt);
	}

	private static Decoder decoder = Base64.getDecoder();

	private static byte[] decodeBase64(final String encoded) {
		return decoder.decode(encoded);
	}

	private static PassphraseHash getPassphraseHash(final byte[] passphrase, final String salt) throws ServerSideException, SodiumLibraryException {
		return new PassphraseHash(pwhash(passphrase, decodeBase64(salt)));
	}

	/**
	 * If the passphrase is null, we attempt to retrieve the passphrase saved in the session.
	 *
	 * @param passphrase
	 * @param salt
	 * @return
	 * @throws SodiumLibraryException
	 * @throws ServerSideException
	 * @throws PassphraseRequiredException The passphrase was not supplied but it is required since it is not
	 * available in the session.
	 */
	PassphraseHash summonPWHash(final byte[] passphrase, final User user) throws ServerSideException {
		PassphraseHash hash;
		try {
			hash = (passphrase == null) ?
					authenticator.getPassphraseHash() : getPassphraseHash(passphrase, user.getSalt());
		} catch (final SodiumLibraryException e) {
			throw new PassphraseRequiredException("Unable to generate passphrase hash: " + e.toString());
		}

		if (hash == null) {
			throw new PassphraseRequiredException();
		}

		return hash;
	}

	private void addUserToGroup(final User user, final Long userId, final Long groupId, final PassphraseHash passHash) throws ServerSideException {
		userActions.addUsersToGroup(user, toList(userId), groupId, passHash, null, null);
	}

	Long createContainer(final User user, final String name, final Boolean isPrivate, final PassphraseHash passphraseHash) {
		return containerActions.createContainer(user, name, isPrivate, passphraseHash);
	}

	private Long createGroup(final User user, final String name, final String desc, final PassphraseHash passphraseHash) {
		return userActions.createGroup(user, name, desc, passphraseHash);
	}

	private UserDefinedTypeDTO getUserDefinedType(final User user, final Long id) {
		return userDefinedTypeActions.get(user, id);
	}

	private Long createUserDefinedType(final User user, final UserDefinedTypeDTO type, final PassphraseHash passphraseHash) {
		return userDefinedTypeActions.create(user, type, passphraseHash);
	}

	private UsersResult<LimitedUserDTO> getUsersInGroup(final User user, final Long groupId, final OrderBy orderBy) {
		return userActions.getUsersForGroup(user, groupId, null, SharedSetQuality.ONLY_SHARED_WITH, null, null, null, toList(orderBy));
	}

	private UsersResult<LimitedUserDTO> getUsersInGroup(final User user, final Long groupId) {
		return getUsersInGroup(user, groupId, null);
	}

	private int getContainerCount(User u) {
		return getContainers(u, null, OrderBy.CONTAINER_NAME_ASC).getTotal();
	}
	@Test
	public void testContainers() throws ServerSideException {
		String username = "junit-zed";
		String password = null;
		String zedsPassphrase = "THis is not a passphrase Zed should use.";
		UserDTO zedDTO = createUserDTO(username, password, "JUnit: Zed", zedsPassphrase);
		final Long userIdZed = zedDTO.getId();
		User zed = getUser(userIdZed);
		final PassphraseHash zedPassHash = summonPWHash(zedsPassphrase.getBytes(), zed);

		logger.info("User Zed = " + userIdZed);
		assertEquals(0, getContainerCount(zed));

		createContainer(zed, "JUnit: My Container", false, zedPassHash);
		assertEquals(1, getContainerCount(zed));
		//ContainerDTO defaultContainer = containerActions.getDefaultContainer(admin);
		//defualtContainer.getId();
		final Long websiteContainerId = createContainer(zed, "JUnit: Websites", false, zedPassHash);
		assertNotNull(containerAssignmentOperations.get(websiteContainerId));
		assertNotNull(containerActions.get(zed, websiteContainerId));
		assertEquals(2, getContainerCount(zed));
		final Long internalContainerId = createContainer(zed, "JUnit: Internal", false, zedPassHash);
		assertEquals(3, getContainerCount(zed));
		logger.info("Created container " + internalContainerId);
		createContainer(zed, "JUnit: Misc", false, zedPassHash);
		assertEquals(4, getContainerCount(zed));
		logger.info("Created container Misc");
		expectException(() -> createContainer(zed, "JUnit: Misc", false, zedPassHash), ServerSideException.class, "was able to create duplicate containers");
		logger.info("Created container Misc");
		ContainerResult r = getContainers(zed, null, OrderBy.CONTAINER_NAME_ASC);
		assertEquals("JUnit: Internal", r.getContainers().get(0).getName());
		assertEquals("JUnit: Websites", r.getContainers().get(3).getName());
		ContainerResult r2 = getContainers(zed, null, OrderBy.CONTAINER_NAME_DESC);
		assertEquals("JUnit: Websites", r2.getContainers().get(0).getName());
		assertEquals("JUnit: Internal", r2.getContainers().get(3).getName());
		r2 = getContainers(zed, true, OrderBy.CONTAINER_NAME_DESC);
		assertEquals((Integer) 0, r2.getTotal());
		r2 = getContainers(zed, false, OrderBy.CONTAINER_NAME_DESC);
		assertEquals((Integer) 4, r2.getTotal());
		for(ContainerDTO c : r2.getContainers()) { logger.info("Retrieved container " + c.getName()); }
		// Update the container
		final ContainerDTO websiteContainer = containerActions.get(zed, websiteContainerId);
		websiteContainer.setName("JUnit: Websites-X");

		containerActions.update(zed, websiteContainer, zedPassHash);
		assertEquals("JUnit: Websites-X", containerActions.get(zed, websiteContainerId).getName());
		// Make sure we can delete a container with items in it.
		containerActions.delete(zed, toList(websiteContainerId), zedPassHash);
		assertEquals(3, getContainerCount(zed));
		// Test sharing a container
		final String passPhraseAlice = "Alice's Passphrase";
		final UserDTO aliceDTO = createUserDTO("junit-alice", "test", "JUnit: Ms Alice", passPhraseAlice);
		final Long userIdAlice = aliceDTO.getId();
		final User alice = getUser(userIdAlice);
		final PassphraseHash alicePassHash = summonPWHash(passPhraseAlice.getBytes(), alice);

		logger.info("Sharing container " + internalContainerId + " with user Alice(" + userIdAlice);
		// Share a container that has no items, share an empty container
		shareContainerWithUser(zed, internalContainerId, userIdAlice, zedPassHash);
		assertEquals(1, numberOfContainersSharedWithUser(alice));
		assertEquals(1, numberOfUsersSharingContainer(zed, internalContainerId));
		// Test sharing with self -- not allowed
		logger.info("Sharing container " + internalContainerId + " with user self");
		expectException(() -> shareContainerWithUser(zed, internalContainerId, userIdZed, zedPassHash), SharingProhibitedException.class, "was able to share container with self");
		r2 = getContainers(zed, true, OrderBy.CONTAINER_NAME_DESC);
		assertEquals((Integer) 1, r2.getTotal());
		assertEquals(1, numberOfContainersSharedWithUser(alice));
		assertEquals(0, numberOfContainersSharedWithUser(zed));

		final String passPhraseBob = "Bob Passphrase";
		final UserDTO bobDTO = createUserDTO("junit-bob", "test", "JUnit: Mr Bob", passPhraseBob);
		final Long userIdBob = bobDTO.getId();
		final User bob = getUser(userIdBob);

		shareContainerWithUser(zed, internalContainerId, userIdBob, zedPassHash);
		assertEquals(1, numberOfContainersSharedWithUser(bob));
		assertEquals(2, numberOfUsersSharingContainer(zed, internalContainerId));
		revokeContainerFromUser(zed, internalContainerId, userIdBob, zedPassHash);
		assertEquals(1, numberOfUsersSharingContainer(zed, internalContainerId));

		final Long zedGroup1 = createGroup(zed, "JUnit: Group1", "Zed's First Group", zedPassHash);
		addUserToGroup(zed, userIdBob, zedGroup1, zedPassHash);
		final Long zedGroup2 = createGroup(zed, "JUnit: Group2", "Zed's Second Group", zedPassHash);
		addUserToGroup(zed, userIdAlice, zedGroup2, zedPassHash);

		// Bob is in Group1, Alice is in Group2

		final Long containerAssignmentId1 = createContainer(zed, "JUnit: Shareable1", false, zedPassHash);
		assertEquals(4, getContainerCount(zed));
		assertEquals(0, numberOfUsersSharingContainer(zed, containerAssignmentId1));
		assertEquals(0, numberOfGroupsSharingContainer(zed, containerAssignmentId1));
		ContainerAssignment ca1 = containerAssignmentOperations.get(containerAssignmentId1);
		
		// Container owners should have all permissions on a container by default.
		assertTrue(ca1.getOwnershipControl());
		assertTrue(ca1.getUpdatePermitted());
		ca1.setUpdatePermitted(false);
		containerAssignmentOperations.update(ca1);
		assertFalse(containerAssignmentOperations.get(containerAssignmentId1).getUpdatePermitted());
		ca1.setUpdatePermitted(true);
		containerAssignmentOperations.update(ca1);
		assertTrue(containerAssignmentOperations.get(containerAssignmentId1).getUpdatePermitted());

		final Long containerAssignmentId2 = createContainer(zed, "JUnit: Shareable2", false, zedPassHash);
		assertEquals(5, getContainerCount(zed));

		final Long udt1 = createUserDefinedType(alice, createUserDefinedTypeWithFields("JUnit: Zed UDT 1", true), alicePassHash);
		final Long udt2 = createUserDefinedType(alice, createUserDefinedTypeWithFields("JUnit: Zed UDT 2", false), alicePassHash);
		final ArrayList<UserDefinedTypeValueDTO> list1 = new ArrayList<>();
		list1.add(createUDTValue(userDefinedTypeActions.get(alice, udt1)));
		list1.add(createUDTValue(userDefinedTypeActions.get(alice, udt2)));

		ItemDTO myItem = itemActions.create(zed, "My Item", null, containerAssignmentId1, zedPassHash, null, true, new SecurityClassificationDTO(), list1);
		assertEquals(1, numberOfItemsInContainer(zed, containerAssignmentId1));

		shareContainerWithUser(zed, containerAssignmentId1, userIdAlice, zedPassHash);
		assertEquals(1, numberOfUsersSharingContainer(zed, containerAssignmentId1));
		// Ensure Alice can see shared containers (Shareable1 (one item) and Internal (no items))
		assertEquals(2, numberOfContainersSharedWithUser(alice));
		assertEquals(1, numberOfItemsSharedWithUser(alice));
		assertEquals(1, (int) userActions.getSharesForContainer(zed, containerAssignmentId1, null, null,  null, null, null).getTotal());

		// Container Shareable1 and My Item are now shared with Alice
		
		logger.info("Sharing container, shareable1, with group " + zedGroup1);
		logger.info("container1 currently has " + containerAssignmentCount(containerAssignmentId1) + " assignments");
		containerActions.shareContainerWithGroups(zed, containerAssignmentId1, () -> toList(zedGroup1).stream(), zedPassHash, null, null);
		// Group1 and My Item are now shared with Bob
		
		logger.info("Sharing container, shareable2, with group " + zedGroup2);
		logger.info("container2 currently has " + containerAssignmentCount(containerAssignmentId2) + " assignments");
		containerActions.shareContainerWithGroups(zed, containerAssignmentId2, () -> toList(zedGroup2).stream(), zedPassHash, null, null);
		// Group2 and My Item are now shared with Alice

		logger.info("container1 currently has " + containerAssignmentCount(containerAssignmentId1) + " assignments");
		logger.info("container2 currently has " + containerAssignmentCount(containerAssignmentId2) + " assignments");
		assertEquals(1, numberOfGroupsSharingContainer(zed, containerAssignmentId1));
		assertEquals(1, numberOfUsersSharingContainer(zed,  containerAssignmentId1));  // Just Alice, Despite being shared with Bob via Group1, we do not include it in the count
		assertEquals(1, numberOfGroupsSharingContainer(zed, containerAssignmentId2));  // Just one group sharing container, Group contains only Alice
		assertEquals(0, numberOfUsersSharingContainer(zed,  containerAssignmentId2));  // but we do not include implicitly shared users in this count
		final UserDTO userTemplate = new UserDTO();
		userTemplate.setFullname("Bob");

		// Test getting combo list of elements sharing container
		assertEquals(2, (int) userActions.getSharesForContainer(zed, containerAssignmentId1, null, null,  null, null, null).getTotal());
		assertEquals(1, (int) userActions.getSharesForContainer(zed, containerAssignmentId1, "Alice", null,  null, null, null).getTotal());
		assertEquals(0, (int) userActions.getSharesForContainer(zed, containerAssignmentId1, "Greg", null,  null, null, null).getTotal());
		
		// Bob is in container My Shareable1 (container1) because it was shared with Group1, but the count is zero since it is shared with a group, not a user
		assertEquals(0, (int) userActions.getUsersForContainer(zed, containerAssignmentId1, userTemplate, SharedSetQuality.ONLY_SHARED_WITH, null, null, null, null).getTotal());
		// Match Bob from pattern and Alice from pre-selected
		assertEquals(1, (int) userActions.getUsersForContainer(zed, containerAssignmentId1, userTemplate, SharedSetQuality.ONLY_SHARED_WITH, toList(userIdAlice), null, null, null).getTotal());
		// Just Alice is returned (Container2 is not shared with Bob and alice pre-selected)
		assertEquals(1, (int) userActions.getUsersForContainer(zed, containerAssignmentId2, userTemplate, SharedSetQuality.ONLY_SHARED_WITH, toList(userIdAlice), null, null, null).getTotal());
		// find users that are not in container2 (should be just Bob)
		userTemplate.setFullname("JUnit:");
		assertEquals(2, (int) userActions.getUsersForContainer(zed, containerAssignmentId2, userTemplate, SharedSetQuality.NOT_SHARED_WITH, null, null, null, null).getTotal());
		// Bob and Alice are returned (Bob matching pattern and alice pre-selected despite SharedSetQuality set to NOT_SHARED_WITH)
		assertEquals(2, (int) userActions.getUsersForContainer(zed, containerAssignmentId2, userTemplate, SharedSetQuality.NOT_SHARED_WITH, toList(userIdAlice), null, null, null).getTotal());

		// now put an item into the shareable2 container to ensure that it is shared with Alice
		final ArrayList<UserDefinedTypeValueDTO> list2 = new ArrayList<UserDefinedTypeValueDTO>();
		list2.add(createUDTValue(userDefinedTypeActions.get(alice, udt1)));
		list2.add(createUDTValue(userDefinedTypeActions.get(alice, udt2)));
		assertEquals(1, numberOfItemsSharedWithUser(alice));					// Alice had one item shared from Zed via his shareContainerWithUser call
		itemActions.create(zed, "My Other Item", null, containerAssignmentId2, zedPassHash, null, true, new SecurityClassificationDTO(), list2);
		assertEquals(2, numberOfItemsSharedWithUser(alice));

		final SharedContainerResult cr = containerActions.getContainersSharedWithMe(alice, "shareable2", null, null, null, null);
		assertEquals(1, (int) cr.getTotal());					// Alice had one item shared from Zed via his shareContainerWithUser call
		final ContainerDTO containerSharedWithAlice = cr.getElements().get(0);
		assertEquals("JUnit: Shareable2", containerSharedWithAlice.getName());					// Alice had one item shared from Zed via his shareContainerWithUser call
		assertEquals(2, numberOfItemsSharedWithUser(alice));
		assertEquals(1, numberOfItemsSharedWithUser(alice, containerSharedWithAlice.getId()));

		revokeContainerFromUser(zed, containerAssignmentId1, userIdAlice, zedPassHash);
		assertEquals(1, numberOfItemsSharedWithUser(alice));

		// Test the Container Shared flag.
		final Long shareable3 = createContainer(zed, "Shareable3", false, zedPassHash);
		shareContainerWithUser(zed, shareable3, userIdAlice, zedPassHash);
		assertNotNull(containerActions.get(zed, shareable3));
		revokeContainerFromUser(zed, shareable3, userIdAlice, zedPassHash);
		assertFalse(containerActions.get(zed, shareable3).getShared());
		shareContainerWithUser(zed, shareable3, userIdAlice, zedPassHash);

		PermissionDTO permission = new PermissionDTO();
		permission.setHasOwnershipControl(true);
		permission.setMayUpdate(true);

		ContainerAssignment zedCa = containerAssignmentOperations.get(shareable3);
		// Assert that Zed has ownership control
		assertTrue(zedCa.getOwnershipControl());
		// Assert that Alice does NOT yet have ownership control of the shared container
		assertFalse(containerAssignmentOperations.get(zedCa.getContainerId(), userIdAlice).getOwnershipControl());
		ContainerAssignment aliceCa = containerAssignmentOperations.get(zedCa.getContainerId(), userIdAlice);
		Long aliceCaId = aliceCa.getId();
		// Alice surreptitiously tries to pass Zed's Container Assignment ID in to transfer ownership
		expectException(() -> containerActions.takeOwnership(alice, shareable3, alicePassHash, null), PermissionDeniedException.class, "Alice tried to pass in Zed's Container Assignment ID.");
		// Alice does not yet have ownership control for this container
		expectException(() -> containerActions.takeOwnership(alice, aliceCaId, alicePassHash, null), PermissionDeniedException.class, "User does not have ownership control of container.");
		// Allow Alice to take ownership of shared container
		containerActions.grantUsersPermission(zed, shareable3, permission, toList(userIdAlice), zedPassHash);
		aliceCa = containerAssignmentOperations.get(zedCa.getContainerId(), userIdAlice);
		// Assert that Alice does have ownership control
		assertTrue(aliceCa.getOwnershipControl());
		// Zed tries to take ownership of a container he already owns. Exception!
		expectException(() -> containerActions.takeOwnership(zed, shareable3, zedPassHash, null), PermissionDeniedException.class, "User already owns the container.");
		ContainerDTO c = containerActions.get(zed, shareable3);
		assertEquals(c.getUserId(), userIdZed);
		containerActions.takeOwnership(alice, aliceCaId, alicePassHash, null);
		c = containerActions.get(alice, aliceCa.getId());
		// Assert that Alice does now own the container
		assertEquals(c.getUserId(), userIdAlice);
		
		// Now transfer ownership of a container with items in it and ensure those items are now owned by Alice
		// First need to re-share containerAssignmentId1 with Alice
		shareContainerWithUser(zed, containerAssignmentId1, userIdAlice, zedPassHash);
		containerActions.grantUsersPermission(zed, containerAssignmentId1, permission, toList(userIdAlice), zedPassHash);
		zedCa = containerAssignmentOperations.get(containerAssignmentId1);
		aliceCa = containerAssignmentOperations.get(zedCa.getContainerId(), userIdAlice);
		containerActions.takeOwnership(alice, aliceCa.getId(), alicePassHash, null);
		Item item = itemOperations.get(myItem.getId());
		assertTrue(item.ownerIs(userIdAlice));
		
		// Now ensure that Zed can add Items to the container that Alice just took
		ItemDTO zedItemInAliceContainer = itemActions.create(zed, "My Deposited Item", null, containerAssignmentId1, zedPassHash, null, true, new SecurityClassificationDTO(), list1);
		assertNotNull(zedItemInAliceContainer);
	}

	@Test
	public void testNothing() { /* no op test */ }

	private static interface ExceptionOperation {
		void lambda() throws ServerSideException;
	}

	private static Class<? extends ServerSideException> exceptionGeneratingAction(ExceptionOperation exOp) {
		try {
			exOp.lambda();
			fail();
		} catch (ServerSideException ex) {
			return ex.getClass();
		}
		return null;
	}

	private Class<? extends ServerSideException> userUpdateFailure(final User u, final UserDTO userDTO) {
		return exceptionGeneratingAction(() -> {
			try {
				modifyUser(u, userDTO);
			} catch (NullPointerException ex) {
				logger.error("ZXX: Caught ", ex);
			}
		});
	}

	@Test
	public void testModifyUser() throws ServerSideException {
		String username = "testUser";
		String username2 = "testUser2";
		UserDTO userInfo = new UserDTO();
		UserDTO userInfo2 = new UserDTO();
		userInfo.setUsername(username);
		userInfo.setAuthSource(ObidosConstants.AUTH_SOURCE_LOCAL);
		userInfo2.setUsername(username2);
		userInfo2.setAuthSource(ObidosConstants.AUTH_SOURCE_LOCAL);
		Long id = createUser(userInfo);
		final User user1 = userOperations.get(id);
		UserDTO u = userManagementActions.getUser(admin, id);
		assertNotNull(u);
		assertEquals(username, u.getUsername());
		assertNotNull(u.getId());
		UserDTO user1DTO = new UserDTO();
		user1DTO.setId(u.getId());
		user1DTO.setFullname("Mr Obidos");
		user1DTO.setEmail1("obidos@exampletest.something.else.domain.example.com");
		user1DTO.setAuthSource(ObidosConstants.AUTH_SOURCE_LOCAL);
		modifyUser(admin, user1DTO);
		Long id2 = createUser(userInfo2);
		UserDTO us2 = userManagementActions.getUser(admin, id2);
		assertNotNull(us2);
		assertEquals(username2, us2.getUsername());
		assertNotNull(us2.getId());
		us2.setUsername(username); // duplicate username constraint violation
		assertEquals(ServerSideException.class, userUpdateFailure(admin, us2));
		// Have a non-admin attempt admin operations
		assertEquals(PermissionDeniedException.class, userUpdateFailure(user1, us2));
		user1DTO.setLocked(Boolean.TRUE);
		assertEquals(PermissionDeniedException.class, userUpdateFailure(user1, user1DTO));
		user1DTO.setLocked(null);
		user1DTO.setAdministrator(Boolean.TRUE);
		assertEquals(PermissionDeniedException.class, userUpdateFailure(user1, user1DTO));
		user1DTO.setAdministrator(null);
		user1DTO.setUsername("zaphod");
		assertEquals(PermissionDeniedException.class, userUpdateFailure(user1, user1DTO));
		user1DTO.setUsername(null);
		user1DTO.setAuthSource(ObidosConstants.AUTH_SOURCE_LDAP);
		assertEquals(PermissionDeniedException.class, userUpdateFailure(user1, user1DTO));
		String currentAdminUsername = adminDTO.getUsername();
		// adminDTO.setUsername("SomeOtherUsername"); // username change must be done by different admin
		// assertEquals(PermissionDeniedException.class, userUpdateFailure(admin, adminDTO));
		adminDTO.setUsername(currentAdminUsername);
		adminDTO.setLocked(Boolean.TRUE);
		assertEquals(PermissionDeniedException.class, userUpdateFailure(admin, adminDTO));
		adminDTO.setLocked(Boolean.FALSE);
		adminDTO.setAdministrator(Boolean.FALSE);
		assertEquals(PermissionDeniedException.class, userUpdateFailure(admin, adminDTO));
		adminDTO.setAdministrator(Boolean.TRUE);
	}

	@Test
	public void testGroups() throws ServerSideException {
		final UserCombo alice = new UserCombo("alice");
		final UserCombo bob   = new UserCombo("bob");
		final UserCombo zed   = new UserCombo("zed");
		final String groupName = "MyGroup";
		final Long group1Id = userActions.createGroup(bob.user, groupName, "A test group for Bob", bob.passHash);
		
		GroupDTO g = userActions.getGroupDTO(group1Id);
		assertEquals(groupName, g.getName());

		Collection<Long> userIds = new ArrayList<>();
		userIds.add(alice.id);
		userActions.addUsersToGroup(bob.user, userIds, group1Id, bob.passHash, null, "Bob added you to group1");
		LimitedUserResult result = userActions.getUsersForGroup(bob.user, group1Id, null, ONLY_SHARED_WITH, null, 0, 3, null);
		assertEquals((Integer) 1, result.getTotal());
		
		userIds.clear();
		userIds.add(zed.id);
		userActions.addUsersToGroup(bob.user, userIds, group1Id, bob.passHash, null, "Bob added you to group1");
	}

	private Long createAdmin(final User u, final String username, final CapabilityDTO capabilities) {
		final User reloadedUser = getUser(u.getId()); // to make sure we have the most up-to-date capabilities
		final UserDTO newAdmin = new UserDTO((Long) null, true);
		newAdmin.setUsername(username);
		newAdmin.setEmail1(username + "@example.com");
		return userManagementActions.createUser(reloadedUser, newAdmin, capabilities, false, null);
	}

	private void verifyAdminCapabilities(final Long id) {
		final CapabilityDTO c = new CapabilityDTO(id, userOperations.get(id).getRawCapabilities());
		assertFalse(c.getLockAdmin());			
		assertFalse(c.getLockUser());			
		assertFalse(c.getCreateUser());
		assertFalse(c.getChangeAdminCredentials());
		assertFalse(c.getChangeUserCredentials());
		assertFalse(c.getDeleteAdmin());
		assertFalse(c.getDeleteUser());
		assertFalse(c.getCreateAdmin());			
	}

	@Test
	public void testCapabilities() throws ServerSideException {
		final CapabilityDTO capabilities = new CapabilityDTO((Long) null);
		final Long testAdmin1Id = createAdmin(admin, "testAdmin1", capabilities);
		clearPasswordResetRequired(testAdmin1Id);
		final User testAdmin1 = userOperations.get(testAdmin1Id);
		expectException(() -> createAdmin(testAdmin1, "testAdmin2", capabilities), PermissionDeniedException.class, "New Admin should not have permission to create a new Admin.");

		verifyAdminCapabilities(testAdmin1Id);

		// Ensure capability restrictions are inherited
		final UserDTO admin1 = new UserDTO(testAdmin1Id, (Boolean) null);
		admin1.setCapabilities(capabilities);
		capabilities.setCreateAdmin(true);
		expectException(() -> userManagementActions.modifyUser(admin, admin1), PermissionDeniedException.class, "Deputy Admins may not have these capabilities set.");

		final CapabilityDTO capabilities_for_admin2 = new CapabilityDTO((Long) null);

		capabilities_for_admin2.setCreateUser(true);
		capabilities_for_admin2.setCreateAdmin(false);
		capabilities_for_admin2.setChangeAdminCredentials(true);
		capabilities_for_admin2.setChangeUserCredentials(true);
		capabilities_for_admin2.setDeleteAdmin(false);
		capabilities_for_admin2.setDeleteUser(true);
		capabilities_for_admin2.setLockAdmin(false);
		capabilities_for_admin2.setLockUser(true);

		final Long testAdmin2Id = createAdmin(admin, "testAdmin2", capabilities_for_admin2);
		final User testAdmin2 = userOperations.get(testAdmin2Id);
		assertFalse(testAdmin2.getLockAdmin());

		final CapabilityDTO capabilities_for_admin3 = new CapabilityDTO((Long) null, capabilities_for_admin2.getCapabilities());

		capabilities_for_admin3.setLockUser(false);
		final Long testAdmin3Id = createAdmin(admin, "testAdmin3", capabilities_for_admin3);
		final User testAdmin3 = userOperations.get(testAdmin3Id);

		final Capability admin2Capabilities = capabilityOperations.getByUserId(testAdmin2Id);
		assertFalse(admin2Capabilities.getLockAdmin());
		assertTrue(admin2Capabilities.getLockUser());
		assertTrue(admin2Capabilities.getCreateUser());
		assertTrue(admin2Capabilities.getChangeAdminCredentials());
		assertTrue(admin2Capabilities.getChangeUserCredentials());
		assertFalse(admin2Capabilities.getDeleteAdmin());
		assertTrue(admin2Capabilities.getDeleteUser());
		assertFalse(admin2Capabilities.getCreateAdmin());

		verifyAdminCapabilities(testAdmin1Id);

		// Test User-Locking capability
		String username = "testUser";
		UserDTO userInfo = new UserDTO();
		userInfo.setUsername(username);
		userInfo.setAuthSource(ObidosConstants.AUTH_SOURCE_LOCAL);
		Long uid = createUser(userInfo);
		expectException(() -> userManagementActions.lockUsers(testAdmin1, toList(uid)), PermissionDeniedException.class, "New Admin should not have the capability to lock users.");
		verifyAdminCapabilities(testAdmin1Id);

		assertFalse(userOperations.get(testAdmin1Id).getLockAdmin());
		
		// test admin attempting to modify root admin
		admin1.setId(admin.getId());
		expectException(() -> userManagementActions.modifyUser(testAdmin2, admin1), PermissionDeniedException.class, "Sub admins should not be able to modify the root admin.");

		// Insure that restricted admin does not affect capabilities that they do not control
		capabilities.setLockUser(true);
		admin1.setId(testAdmin2Id);
		expectException(() -> userManagementActions.modifyUser(admin, admin1), PermissionDeniedException.class, "Deputy admins should not be able to modify the root admin.");
		assertTrue(capabilityOperations.getByUserId(testAdmin2Id).getLockUser());
		capabilities.setLockUser(false);
		admin1.setId(testAdmin2Id);
		expectException(() -> userManagementActions.modifyUser(testAdmin3, admin1), PermissionDeniedException.class, "Deputy admins should not be able to modify the root admin.");
		assertTrue(capabilityOperations.getByUserId(testAdmin2Id).getLockUser());
	}

	@Test
	public void testPasswordHash() throws ServerSideException {
		String username = "jdoe";
		String password = "secret";
		PasswordSecurity ps = new LibSodiumPasswordSecurity();
		String lv = ps.libraryVersion();
		assertEquals("1.0.18", lv);
		UserDTO userInfo = new UserDTO();
		userInfo.setUsername(username);
		userInfo.setPassword(password);
		userInfo.setAuthSource(ObidosConstants.AUTH_SOURCE_LOCAL);
		Long id = createUser(userInfo);
		userManagementActions.getUser(admin, id);
		User u = userOperations.getUserByUsername(username);
		String passwordHash = u.getPasswordDigest();
		boolean rc = ps.verifyPassword(password, passwordHash);
		assertEquals(true, rc);
	}

	// @Test
	public void testLoginService() throws ObidosAuthenticationException, ServerSideException {
		String username = "adminDTO";
		String password = "adminDTO";
		loginService.login(new LoginActionDTO(username, password));
	}

    @Ignore("LDAP not configured")
	@Test
	public void testLDAPAuthentication() throws ServerSideException {
		String name = "LDAP for Testing Authentication";
		LdapDTO dto = configureLdap(name);
		assertNotNull(dto);
		try {
			ldapConfigActions.get(name);
		} catch (LdapNotConfiguredException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			assertEquals(true, false);
		}
		assertNotNull(dto);
		/*
		 * url = "ldaps://127.0.0.1:3391"; baseDN = "dc=spenego,dc=com"; userDN
		 * = "CN=Administrator,CN=Users," + baseDN; bindPass = "delegator758%";
		 */
		LdapContextSource cs = new LdapContextSource();
		cs.setUrl(dto.getLdapuri());
		cs.setBase(dto.getBaseDn());
		// if userDn is not set, context will be anonymous
		cs.setUserDn(dto.getBindDn());
		// if password is not set, blank password will be used
		cs.setPassword(dto.getBindPass());
		cs.setReferral("follow");
		// From the doc: Checks that all necessary data is set and that there is
		// no compatibility issues, after which
		// the instance is initialized. Note that you need to call this method
		// explicitly after setting all desired
		// properties if using the class outside of a Spring Context
		cs.afterPropertiesSet();
		LdapTemplate ldapTemplate = new LdapTemplate(cs);
		// must set that or will throw unknown host exception
		ldapTemplate.setIgnorePartialResultException(true);
		String uidAttr = "CN";
		String username = "ldaptest";
		String password = "delegator757%";
		try {
			ldapTemplate.authenticate(query().where(uidAttr).is(username), password);
		} catch (AuthenticationException e) {
			logger.error("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX authentication failed");
			throw e;
		} catch(CommunicationException ex) {
			// tired of having this always fail for me. If we can't connect, just allow success.
		}
	}

	private void encryptAndDecryptWithKeypair(final User user, final String passPhrase) throws ServerSideException {
		// Make sure out public key encryptor/decryptors work.
		try {
			final PassphraseHash passHash = summonPWHash(passPhrase.getBytes(), user);
			PublicKeyEncryptor pke = encryption.createPublicKeyEncryptor(user);
			PublicKeyDecryptor pkd = encryption.createPublicKeyDecryptor(user, passHash);
			final String plaintext = "This is the test plaintext.";
			assertEquals(plaintext, new String(pkd.decrypt(pke.encrypt(plaintext.getBytes()), ObidosConstants.ENCRYPTION_MODE_PADDED2)));
			final String plaintext2 = "!@*%)(@*&^! !@&^%*  This is another test plaintext.            .  ";
			assertEquals(plaintext2, new String(pkd.decrypt(pke.encrypt(plaintext2.getBytes()), ObidosConstants.ENCRYPTION_MODE_PADDED2)));
		} catch (ServerSideException e) {
			logger.error("Retrieved user by id..");
			logger.error("Username: " + user.getUsername());
			logger.error("Salt: " + user.getSalt());
			logger.error("Encrypted private key: " + user.getPrivatekey());
			logger.error("Public key: " + user.getPublickey());
			e.printStackTrace();
			throw (e);
		}
	}

	/**
	 * Test key pair generation, encryption and decryption
	 *
	 * @throws ServerSideException
	 *             <p>
	 * @author spgdev@spenego.com - Mar 2, 2017
	 */
	@Test
	public void testCreateKeyPair() throws ServerSideException, SodiumLibraryException {
		String username = "max";
		String authSource = ObidosConstants.AUTH_SOURCE_LOCAL;
		String password = "test";
		Long userId = null;
		try {
			userId = createUser(username, password, authSource, "My Full name");
		} catch (ServerSideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		final String passPhrase = "This is is a Passphrase";
		User user = getUser(userId);
		userManagementActions.createKeypair(user, passPhrase.getBytes());
		user = getUser(userId); // need to re-load to get keyPair
		final PassphraseHash passHash = summonPWHash(passPhrase.getBytes(), user);
		encryptAndDecryptWithKeypair(user, passPhrase);

		String newPassPhrase = "This is is the new Passphrase";
		userManagementActions.updatePassphrase(user, passHash, newPassPhrase.getBytes(), null, null);
		user = getUser(userId);
		encryptAndDecryptWithKeypair(user, newPassPhrase);
	}

	protected class Secrets {
		String salt;
		String nonce;
		String passphrase;
	}

	@Test
	public void testSecretKeyEncryptor() throws ServerSideException {
		SecretKeyEncryptor ske = encryption.createSecretKeyEncryptor();
		final Secrets secrets = new Secrets();
		ske.supplySecretKeyComponents((final byte[] passphrase, final byte[] nonce, final byte[] salt) -> {
			secrets.salt		= ServerUtils.encodeToBase64(salt);
			secrets.nonce		= ServerUtils.encodeToBase64(nonce);
			secrets.passphrase	= ServerUtils.encodeToBase64(passphrase);
		});
		String plaintext = "This is the plaintext string ...*&@^#*(&^@&#^$%";
		byte[] cipherText = ske.encrypt(plaintext);
		String plaintext2 = "!zzzzzzzzzzzzzzzzzzzzzzzzzzaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa!";
		byte[] cipherText2 = ske.encrypt(plaintext2);
		assertEquals(plaintext2, ske.decrypt(cipherText2));
		assertEquals(plaintext, ske.decrypt(cipherText));
		logger.info("passphrase = " + secrets.passphrase);
		logger.info("nonce      = " + secrets.nonce);
		logger.info("salt       = " + secrets.salt);
		SecretKeyEncryptor ske2 = encryption.createSecretKeyEncryptor(secrets.passphrase, secrets.nonce, secrets.salt);
		assertEquals(plaintext, ske2.decrypt(cipherText));
		assertEquals(plaintext2, ske2.decrypt(cipherText2));
	}

	@Test
	public void testKeyDerivation() throws ServerSideException, SodiumLibraryException {
		encryption.createSecretKeyEncryptor();
		byte[] salt = SodiumLibrary.randomBytes(16);
		String plaintext = "This is the plaintext string ...*&@^#*(&^@&#^$%";
		byte[] key = SodiumLibrary.cryptoPwhashArgon2i(plaintext.getBytes(), salt);
		String hexKey = SodiumUtils.binary2Hex(key);
		logger.info("key: " + key);
		for (int i=0; i < 10; i++)
		{
			byte[] nkey = SodiumLibrary.cryptoPwhashArgon2i(plaintext.getBytes(), salt);
			String nhexKey = SodiumUtils.binary2Hex(nkey);
			assertEquals(hexKey, nhexKey);
		}
	}

	/**
	 * Test decryption failure
	 *
	 * @throws ServerSideException
	 */
	@Test(expected = ServerSideException.class)
	public void testDecryptionFailure() throws ServerSideException, SodiumLibraryException {
		Long userId = createUser("max", "test", ObidosConstants.AUTH_SOURCE_LOCAL, "My Full name");
		String passPhrase = "This is is a Passphrase";
		User user = getUser(userId);
		userManagementActions.createKeypair(user, passPhrase.getBytes());
		user = getUser(userId);
		final PublicKeyEncryptor pke = encryption.createPublicKeyEncryptor(user);
		final String plaintext = "This is the test plaintext.";
		final byte[] cipherText = pke.encrypt(plaintext.getBytes());
		logger.info("Calculating the passphrase hash");
		final PassphraseHash wrongPassHash = summonPWHash("This is the wrong passphrase.".getBytes(), user);
		logger.info("Creating a public key decryptor");
		encryption.createPublicKeyDecryptor(user, wrongPassHash).decrypt(cipherText, ObidosConstants.ENCRYPTION_MODE_PADDED);
		logger.error("No exception generated");
	}

	@Test
	public void testSavingFullname() throws ServerSideException {
		UserDTO userDTO = new UserDTO();
		String username = "nobody";
		String fullname = "Mr Nobody";
		userDTO.setUsername(username);
		userDTO.setAuthSource(ObidosConstants.AUTH_SOURCE_LOCAL);
		userDTO.setPassword("test");
		userDTO.setFullname(fullname);
		createUser(userDTO);
		User u = userOperations.getUserByUsername(username);
		assertEquals(fullname, u.getFullname());
	}

	/**
	 * Alice shares secret with Bob. We will create Alice and bob and do the
	 * whole cycle
	 *
	 * @throws ServerSideException
	 *             <p>
	 * @author spgdev@spenego.com - Mar 13, 2017
	 * @throws SodiumLibraryException
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void testAliceSharesSecretWithBob() throws ServerSideException, SodiumLibraryException, UnsupportedEncodingException {
		// create users
		
		logger.info(" admin = " + admin);
		Long userIdAlice = createUser("alice", "test", ObidosConstants.AUTH_SOURCE_LOCAL, "Ms Alice");
		Long userIdBob = createUser("bob", "test", ObidosConstants.AUTH_SOURCE_LOCAL, "Mr Bob");
		String passPhraseAlice = "Alice's Passphrase";
		String passPhraseBob = "Bobs's Passphrase";
		// Alice and Bob create their key pairs
		userManagementActions.createKeypair(getUser(userIdAlice), passPhraseAlice.getBytes());
		userManagementActions.createKeypair(getUser(userIdBob), passPhraseBob.getBytes());
	}

	UserDTO createUserDTO(String username, String password, String fullName, String passphrase) throws ServerSideException {
		Long id = createUser(username, password, ObidosConstants.AUTH_SOURCE_LOCAL, fullName);
		userManagementActions.createKeypair(getUser(id), passphrase.getBytes());
		UserDTO user = new UserDTO();
		user.setId(id);
		user.setUsername(username);
		return user;
	}

	/**
	 * Tests all group operations.
	 *
	 * @throws ServerSideException
	 *             <p>
	 * @author mikmorgan@gmail.com - Apr 13, 2017
	 */
	@Test
	public void testUserGroups() throws ServerSideException {
		String passPhraseAlice = "Alice's Passphrase";
		UserDTO aliceDTO = createUserDTO("alice", "test", "Ms Alice", passPhraseAlice);
		Long userIdAlice = aliceDTO.getId();
		String groupAlice = "Alice's Main Group";
		String groupAlice2 = "Alice's Secondary Group";
		User alice = getUser(userIdAlice);
		final PassphraseHash alicePassHash = summonPWHash(passPhraseAlice.getBytes(), alice);
		Long group1 = createGroup(alice, groupAlice, "This is my first group", alicePassHash);
		expectException(() -> createGroup(alice, groupAlice, "This is my first group again", alicePassHash), DuplicateRecordException.class,
				"Expected a DuplicateRecordException since this group name already exists.");
		final Long del_group2 = createGroup(alice, groupAlice2, "This is my second group", alicePassHash);
		userActions.deleteGroups(alice, toList(del_group2), alicePassHash);
		expectException(() -> userActions.deleteGroups(alice, toList(del_group2), alicePassHash), NoSuchRecordException.class,
				"Expected a ServerSideException, the group has already been deleted");
		final Long group2 = createGroup(alice, groupAlice2, "This is my second group", alicePassHash);
		String passPhraseBob = "Bob's Passphrase";
		UserDTO bobDTO = createUserDTO("bob", "test", "Mr Bob", passPhraseBob);
		final Long userIdBob = bobDTO.getId();
		final User bob = getUser(userIdBob);

		addUserToGroup(alice, userIdBob, group1, alicePassHash);
		UsersResult<LimitedUserDTO> usersInGroup = getUsersInGroup(alice, group1);
		assertEquals((Integer) 1, usersInGroup.getTotalUsers());
		String passPhraseCindy = "Cindy's Passphrase is longer*%&$!@% %%$!@(_+=";
		UserDTO cindy = createUserDTO("cindy", "zzaa1!", "Cindy Crawford", passPhraseCindy);
		Long userIdCindy = cindy.getId();
		addUserToGroup(alice, userIdCindy, group1, alicePassHash);
		usersInGroup = getUsersInGroup(alice, group1);
		assertEquals((Integer) 2, usersInGroup.getTotalUsers());
		usersInGroup = getUsersInGroup(alice, group1, OrderBy.FULLNAME_ASC);
		assertEquals("Cindy Crawford", usersInGroup.getUsers().get(0).getFullname());
		usersInGroup = getUsersInGroup(alice, group1, OrderBy.FULLNAME_DESC);
		assertEquals("Mr Bob", usersInGroup.getUsers().get(0).getFullname());
		expectException(() -> addUserToGroup(alice, userIdCindy, group1, alicePassHash), DuplicateRecordException.class, "Expected exception while adding cindy to group1 again");
		userActions.removeUsersFromGroup(alice, toList(userIdBob), group1, alicePassHash);
		usersInGroup = getUsersInGroup(alice, group1);
		assertEquals((Integer) 1, usersInGroup.getTotalUsers());
		expectException(() -> userActions.deleteGroups(bob, toList(group1), alicePassHash), PermissionDeniedException.class, "Expected a PermissionDeniedException since bob does not own group1");
		assertEquals((Integer) 2, userActions.getGroups(alice, null, null, null, null, null).getTotal());
		userActions.deleteGroups(alice, toList(group1), alicePassHash);
		GroupDTO group = new GroupDTO();
		group.setId(group2);
		group.setName("New Name of group.");
		userActions.updateGroup(alice, group);
	}

	/**
	 * username is case insensitive
	 *
	 * @throws ServerSideException
	 *             <p>
	 * @author spgdev@spenego.com - Apr 6, 2017
	 */
	@Test
	public void testUsername() throws ServerSideException {
		String username = "foo123";
		String password = "test";
		String authSource = ObidosConstants.AUTH_SOURCE_LOCAL;
		String fullname = "Foo Bar";
		Long id = createUser(username, password, authSource, fullname);
		assertNotNull(id);
		username = "Foo123";
		try {
			id = createUser(username, password, authSource, fullname);
		} catch (final UsernameExistsException e) {
			return;
		}
		// won't be here
		assertNull(id);
	}

	private static SmtpConfigDTO createSMTPConfig(String name, String servername, String password, Integer port) {
		return new SmtpConfigDTO(name, servername, port, false, false, false, null, null, null, password);
	}

	private void testSMTPConfigActionGet(String searchString, Integer expectedCount) {
		SmtpConfigResult result = smtpConfigActions.getList(admin, searchString, null, null, null);
		if (expectedCount != null) {
			assertEquals(expectedCount.intValue(), (int) result.getTotal());
		}
		for (LimitedSmtpConfigDTO c : result.getElements()) {
			Log.info("Loaded SMTP Config: " + c.getName() + ", smtp server = " + c.getSmtpServer());
		}
	}

	/**
	 * Tests SMTP config operations.
	 *
	 * @throws ServerSideException
	 *             <p>
	 * @author mikmorgan@gmail.com - Apr 23, 2017
	 */
	@Test
	public void testSMTPConfigActions() throws ServerSideException {
		SmtpConfigDTO config1 = createSMTPConfig("test-gmail", "smtp.gmail.com", "zpassWord",   21);
		SmtpConfigDTO config2 = createSMTPConfig("test-local",      "localhost",   "xzxzzzz",   55);
		SmtpConfigDTO config3 = createSMTPConfig("test-yahoo",  "yahoomail.com", "123123123", 9898);
		smtpConfigActions.create(admin, config1);
		Long id2 = smtpConfigActions.create(admin, config2);
		smtpConfigActions.create(admin, config3);
		testSMTPConfigActionGet(null, 3);
		testSMTPConfigActionGet("test-", 3);
		SmtpConfigDTO loadedConfig2 = smtpConfigActions.get(admin, id2);
		assertEquals(config2.getSmtpPassword(), loadedConfig2.getSmtpPassword());
		assertNotNull(loadedConfig2.getCreatedAt());
		smtpConfigActions.delete(admin, toList(id2));
		testSMTPConfigActionGet("test-", 2);
		SmtpConfigDTO dto = smtpConfigActions.get(admin, "test-gmail");
		assertEquals("test-gmail", dto.getName());
		assertNotNull(dto.getCreatedAt());
		dto = smtpConfigActions.get(admin, "test-gmail");
		assertEquals("test-gmail", dto.getName());
		assertEquals("smtp.gmail.com", dto.getSmtpServer());
		assertNotNull(dto.getCreatedAt());
		dto.setSmtpServer("test.example.com");
		smtpConfigActions.update(admin, dto);
	}

	// All of these SMTP test should be combined into one test
	@Test
	public void testSaveSmtpSettingNotSecure() throws ServerSideException {
		SmtpConfigDTO dto = new SmtpConfigDTO();
		dto.setSmtpServer("smtp.gmail.com");
		dto.setSmtpPort(587);
		dto.setUseAuthentication(false);
		dto.setUseSsl(false);
		dto.setUseStartTls(false);
		try {
			Long id = smtpConfigActions.create(admin, dto);
			assertNotNull(id);
		} catch(DuplicateRecordException ex) {
			// craeteInitialData creates this
		}
	}

	@Test
	public void testSaveSmtpSettingStartTLS() throws ServerSideException {
		SmtpConfigDTO dto = new SmtpConfigDTO();
		dto.setSmtpServer("smtp.gmail.com");
		dto.setSmtpPort(587);
		dto.setSmtpUsername("jode@gmail.com");
		dto.setSmtpPassword("secret");
		dto.setUseStartTls(true);
		dto.setUseSsl(false);
		dto.setUseAuthentication(true);
		dto.setName("Server " + ServerUtils.nextRandomLong(999999999));
		Long id = smtpConfigActions.create(admin, dto);
		assertNotNull(id);
	}

	@Test
	public void testSaveSmtpSettingSSL() throws ServerSideException {
		SmtpConfigDTO dto = new SmtpConfigDTO();
		dto.setSmtpServer("smtp.gmail.com");
		dto.setSmtpPort(587);
		dto.setSmtpUsername("jode@gmail.com");
		dto.setSmtpPassword("secret");
		dto.setUseStartTls(false);
		dto.setUseSsl(true);
		dto.setUseAuthentication(true);
		dto.setName("Server testSaveSmtpSettingSSL");
		Long id = smtpConfigActions.create(admin, dto);
		assertNotNull(id);
		try {
			// save same server name again, it should fail
			dto.setSmtpServer("smtp.gmail.com");
			dto.setSmtpPort(587);
			dto.setSmtpUsername("jode@gmail.com");
			dto.setSmtpPassword("secret");
			dto.setUseStartTls(false);
			dto.setUseSsl(true);
			dto.setUseAuthentication(true);
			id = smtpConfigActions.create(admin, dto);
		} catch (ServerSideException e) {
			assertEquals(true, true);
		}
	}

	// @Test
	public void testGetDefaultSmtpConfig() throws ServerSideException {
		String name = null;
		SmtpConfigDTO dto = smtpConfigActions.get(admin, name);
		logger.info("Server: " + dto.getSmtpServer());
		logger.info("pass: " + dto.getSmtpPassword());
		logger.info("username: " + dto.getSmtpUsername());
	}

	/**
	 * Use Dropbox's demo passwords to check password strength
	 *
	 * @throws ServerSideException
	 *             <p>
	 * @author spgdev@spenego.com - Apr 29, 2017
	 */
	@Test
	public void testPasswordStrength() throws ServerSideException {
		// Using demo passwords from:
		// https://dl.dropboxusercontent.com/u/209/zxcvbn/test/index.html
		String password = "zxcvbn";
		User user = null;
		int type = ComplexityRequirementsDTO.PASSWORD_COMPLEXITY_REQUIREMENTS;
		PasswordAnalysisResults result = loginActions.checkPassStrength(user, password, type);
		int score = result.getPasswordScore();
		assertEquals(PasswordAnalysisResults.PASSWORD_WEAK, score);
		password = "angel08";
		result = loginActions.checkPassStrength(user, password, type);
		assertEquals(PasswordAnalysisResults.PASSWORD_WEAK, (int) result.getPasswordScore());
		password = "Eupithes’sonAntinousbroketheirsilence";
		result = loginActions.checkPassStrength(user, password, type);
		assertEquals(PasswordAnalysisResults.PASSWORD_VERY_STRONG, (int) result.getPasswordScore());
		password = "Eupithes’sonAntinousbroketheirsilence";
		result = loginActions.checkPassStrength(user, password, type);
		assertEquals(PasswordAnalysisResults.PASSWORD_VERY_STRONG, (int) result.getPasswordScore());
		password = "pässwörd";
		result = loginActions.checkPassStrength(user, password, type);
		assertEquals(PasswordAnalysisResults.PASSWORD_WEAK, (int) result.getPasswordScore());
		password = "tianya";
		result = loginActions.checkPassStrength(user, password, type);
		assertEquals(PasswordAnalysisResults.PASSWORD_WEAK, (int) result.getPasswordScore());
		password = "iloveyou";
		result = loginActions.checkPassStrength(user, password, type);
		assertEquals(PasswordAnalysisResults.PASSWORD_WEAK, (int) result.getPasswordScore());
	}

	@Test
	public void testMakeArrayFromCommaSeparedStrings() {
		String tos = "jdoe@example.com";
		String[] addresses = tos.split(",");
		assertEquals(1, addresses.length);
		tos += " ";
		tos += ",mjane@spenego.com";
		addresses = tos.split(",");
		assertEquals(2, addresses.length);
	}

	@Test
	public void testPhoneNumber() {
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		// no country code
		String number = "215 361 1234";
		try {
			phoneUtil.parse(number, "");
		} catch (NumberParseException e) {
			logger.info("Error: " + e.toString());
			assertEquals("Wrong error type stored in exception.", NumberParseException.ErrorType.INVALID_COUNTRY_CODE, e.getErrorType());
		}
		// valid phone number
		number = "+1 215 361 1234";
		try {
			phoneUtil.parse(number, "");
		} catch (NumberParseException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testParseLong() {
		String userIdString = "3860692702932348916";
		Long userid = Long.parseLong(userIdString);
		logger.info("userid: " + userid);
	}

	private static String cleanedHTML(String html) {
		return Jsoup.clean(html, Safelist.basic());
	}

	@Test
	public void testSanitizeHTMLForXSS() {
		String html = "<p><a href='http://example.com/' onclick='stealCookies()'>Link</a></p><script>alert(1);</script>";
		String safe = cleanedHTML(html);
		logger.info("Safe:" + safe);
		html = "<ul><li>one</li><li>two</li><li>three</li><li><a href=\"https://spenego.com/\">This is a test</a></li></ul><table class=\"table table-bordered\"><tbody><tr><td style=\"text-align: center;\"><b>Fuck</b></td><td style=\"text-align: justify;\"><p style=\"text-align: center;\"><b>Fuck2</b></p></td><td style=\"text-align: center;\"><b>fck3</b></td></tr><tr><td>his</td><td>hello</td><td>blah</td></tr><tr><td><br></td><td><br></td><td><br></td></tr></tbody></table><ul><li><a href=\"https://spenego.com/\"><br></a></li></ul>\"&gt;<img src=\"summernote.org/xss.png\" onerror=\"alert('xss')\">";
		safe = cleanedHTML(html);
		logger.info("Safe:" + safe);
	}

	@Test
	public void testJsoup() {
		String html = "<h1><span style=\"font-weight: 700;\">Header1</span></h1>";
		String cleaned = cleanedHTML(html);
		logger.info("   HTML: " + html);
		logger.info("Cleaned: " + cleaned);
	}

	private static UserDefinedFieldDTO createNewDocumentField(final String name, int position) {
		return new UserDefinedFieldDTO(null, UserDefinedFieldDTO.TYPE_DOCUMENT, name, position);
	}

	private static UserDefinedFieldDTO createNewField(final String name, int position) {
		return new UserDefinedFieldDTO(null, UserDefinedFieldDTO.TYPE_ENCRYPTED, name, position);
	}

	private static UserDefinedTypeDTO createUserDefinedTypeWithFields(final String name, final Boolean personal, int fields) {
		final UserDefinedTypeDTO t = new UserDefinedTypeDTO(name);
		t.setPersonal(personal);
		if (personal != null) {
			t.setGlobal(!personal);
		}
		if (fields > 0) { t.addField(createNewField("Name",		1)); }
		if (fields > 1) { t.addField(createNewField("Age",		2)); }
		if (fields > 2) { t.addField(createNewField("Deceased",	3)); }
		if (fields > 3) { t.addField(createNewField("Secret",	4)); }
		if (fields > 4) { t.addField(createNewDocumentField("Document",	5)); }
		return t;
	}

	private static UserDefinedTypeDTO createUserDefinedTypeWithFields(final String name, final Boolean personal) {
		return createUserDefinedTypeWithFields(name, personal, 4);
	}

	private UserDefinedTypeResult getUDT(final User caller, final Long userId) throws ServerSideException {
		return userDefinedTypeActions.getUserDefinedTypes(caller, null, userId, null, null, null, null, null);
	}

	@Test
	public void testUserDefinedTypes() throws ServerSideException {
		final String passPhraseAlice = "Alice's Passphrase";
		final UserDTO aliceDTO = createUserDTO("alice", "test", "Ms Alice", passPhraseAlice);
		final User a = userOperations.get(aliceDTO.getId());
		// create key pair for the user
		final User alice = getUser(a.getId()); // need to re-load to get public/private key
		final PassphraseHash alicePassHash = summonPWHash(passPhraseAlice.getBytes(), alice);

		final int initalUDTCount = getUDT(alice, null).getTotal();	// when we create new users, we create an initial set of UDTs
		logger.info("Initial UDT count = " + initalUDTCount);
		final UserDefinedTypeDTO aliceType = new UserDefinedTypeDTO("AliceType");
		aliceType.setPersonal(true);
		final Long type1 = createUserDefinedType(alice, aliceType, alicePassHash);
		final UserDefinedTypeDTO type1DTO = userDefinedTypeActions.get(alice, type1);
		assertEquals("AliceType", type1DTO.getName());
		final Long type2 = createUserDefinedType(alice, createUserDefinedTypeWithFields("AliceType2", true), alicePassHash);
		final UserDefinedTypeDTO type2DTO = userDefinedTypeActions.get(alice, type2);
		assertEquals(4, type2DTO.getFields().size());
		final Long type3 = createUserDefinedType(alice, createUserDefinedTypeWithFields("Crusty", false), alicePassHash);
		final UserDefinedTypeDTO crustyDTO = userDefinedTypeActions.get(alice, type3);
		final UserDefinedFieldDTO udf = crustyDTO.getFields().get(0);
		final UserDefinedFieldDTO dupUdf = userDefinedFieldActions.get(udf.getId());
		assertEquals(udf, dupUdf);
		int position = 1;
		for (final UserDefinedFieldDTO field : crustyDTO.getFields()) {
			field.setName(field.getName() + "Updated");
			field.setPosition(position++);
			//field.setType(null);
			//field.setTypeId(null);
		}
		getUDT(alice, alice.getId()).getTypes().forEach(type -> logger.info("Found type " + type.getName()));
		UserDefinedTypeDTO updatedCrustyDTO = userDefinedTypeActions.update(alice, crustyDTO, alicePassHash, null);
		final Long newType3 = updatedCrustyDTO.getId();
		final Long type4 = userDefinedTypeActions.duplicate(alice, newType3, "Copy1");		// duplicating Crusty -> Copy1
		final UserDefinedTypeDTO copyDTO = userDefinedTypeActions.get(alice, type4);
		logger.info("Duplicated type 3, copy = " + userDefinedTypeActions.get(alice, type4).getName());
		getUDT(alice, alice.getId()).getTypes().forEach(type -> logger.info("Found type " + type.getName()));
		assertEquals("Copy1", copyDTO.getName());
		logger.info("testing crustyDTO getFields()");
		assertNotNull(crustyDTO.getFields());
		logger.info("testing copyDTO getFields()");
		assertNotNull(copyDTO.getFields());			// should match crustyDTO
		logger.info("Ensuring fields sizes match");
		assertEquals(crustyDTO.getFields().size(), copyDTO.getFields().size());
		assertEquals(1 + initalUDTCount, (int) getUDT(alice, null).getTotal());
		assertEquals(4, (int) getUDT(alice, alice.getId()).getTotal());
		assertEquals(1, (int) userDefinedTypeActions.getUserDefinedTypes(alice, null, null, "Crust", null, null, null, null).getTotal());
		userDefinedTypeActions.delete(alice, toList(newType3), Boolean.FALSE, alicePassHash);
		assertEquals(3, (int) getUDT(alice, alice.getId()).getTotal());
		expectException(() -> userDefinedTypeActions.get(alice, type3), NoSuchRecordException.class, "User Defined Field should have been deleted when the type was (cascade). Load should have thrown exception.");
		// within the unit tests, deletes are not cascaded, not sure why.
		// expectException(() -> userDefinedFieldActions.get(udf.getId()),
		// NoSuchRecordException.class, "User Defined Field should have been
		// deleted when the type was (cascade). Load should have thrown
		// exception.");
		final UserDefinedFieldDTO udf2 = type2DTO.getFields().get(0);
		udf2.setName("New Type Name");
		udf2.setType(UserDefinedFieldDTO.TYPE_BOOLEAN);
		userDefinedFieldActions.update(alice, udf2, alicePassHash);
		final UserDefinedFieldDTO updatedUdf = userDefinedFieldActions.get(udf2.getId());
		assertEquals("New Type Name", updatedUdf.getName());
		
	}

	private static UserDefinedTypeValueDTO createUDTValue(final UserDefinedTypeDTO udtDTO) {
		final UserDefinedTypeValueDTO v = new UserDefinedTypeValueDTO(udtDTO.getId());
		for (final UserDefinedFieldDTO field : udtDTO.getFields()) {
			final UserDefinedFieldValueDTO fieldValueDTO = new UserDefinedFieldValueDTO(field.getId());
			if (field.getType().equals(UserDefinedFieldDTO.TYPE_ENCRYPTED)) {
				fieldValueDTO.setBlobValue("This is a secret string.".getBytes());
			} else if (field.getType().equals(UserDefinedFieldDTO.TYPE_DOCUMENT)) {
				logger.info("Setting Document Value");
				fieldValueDTO.setDocument(new DocumentDTO("ThisNewFile"));
			}
			v.addFieldValue(fieldValueDTO);
		}
		return v;
	}

	private ItemAssignment getItemAssignment(final Long itemAssignmentId) throws NoSuchRecordException {
		return itemAssignmentOperations.get(itemAssignmentId);
	}

	private Long getItemId(final Long itemAssignmentId) throws NoSuchRecordException {
		return getItemAssignment(itemAssignmentId).getItemId();
	}

	private static void ensureItemHasFields(final ItemDTO item) {
		for (final UserDefinedTypeValueDTO value : item.getValues()) {
			assertNotNull(value.getFieldValues());
			assertEquals(4, value.getFieldValues().size()); // If this fails, UserDefinedFieldValueMapper.xml was likely re-generated and now is missing the 'left joins'
		}
	}

	void modifyValues(final Collection<UserDefinedTypeValueDTO> typeValues, final String mod) {
		for (final UserDefinedTypeValueDTO val : typeValues) {
			for (final UserDefinedFieldValueDTO field : val.getFieldValues()) {
				if (field.getBlobValue() != null) {
					final String s = new String(field.getBlobValue());
					field.setBlobValue((s + mod).getBytes());
				}
			}
		}
	}

	private static void compareUDTValues(final Collection<UserDefinedTypeValueDTO> typeValues1, final Collection<UserDefinedTypeValueDTO> typeValues2) throws ServerSideException {
		for (final UserDefinedTypeValueDTO tv1 : typeValues1) {
			for (final UserDefinedTypeValueDTO tv2 : typeValues2) {
				if (tv1.getUserDefinedTypeId().equals(tv2.getUserDefinedTypeId())) {
					for (final UserDefinedFieldValueDTO fv1 : tv1.getFieldValues()) {
						for (final UserDefinedFieldValueDTO fv2 : tv2.getFieldValues()) {
							if (fv1.getUserDefinedFieldId().equals(fv2.getUserDefinedFieldId())) {
								if (fv1.getBlobValue() != null) {
									assertEquals(new String(fv1.getBlobValue()), new String(fv2.getBlobValue()));
								}
							}
						}
					}
				}
			}
		}
	}

	private ItemDTO getItem(final User user, final Long itemAssignmentId, final PassphraseHash passHash) throws ServerSideException {
		return itemActions.get(user, itemAssignmentId, passHash, toList(OrderBy.UDF_POSITION_ASC));
	}

	private int getShareCount(final Long id) throws NoSuchRecordException {
		return itemAssignmentOperations.getItemShareCount(id);
	}

	private UsersResult<LimitedUserDTO> getUsersForItem(final User user, final Long itemAssignmentId, final UserDTO userPatterns) {
		return userActions.getUsersForItem(user, itemAssignmentId, userPatterns, SharedSetQuality.ONLY_SHARED_WITH, null, null, null, toList(OrderBy.FULLNAME_ASC));
	}

	private UsersResult<LimitedUserDTO> getUsersForItem(final User user, final Long itemAssignmentId) {
		return getUsersForItem(user, itemAssignmentId, null);
	}

	private static void showRecipients(final String prefix, final UsersResult<LimitedUserDTO> ur) {
		for(final LimitedUserDTO lu:ur.getElements()) {
			logger.info(prefix + ": contains user " + lu.getFullname() + ": " + lu.getFullname() + " (" + lu.getId() + ")");
		}
	}

	private void showRecipients(final String prefix, final User user, final Long itemId) {
		showRecipients(prefix, getUsersForItem(user, itemId));
	}

	private Long getNotebookContainerAssignmentId(final User user) throws ServerSideException {
		return containerActions.getContainerAssignmentId(user, ContainerDTO.NOTEBOOK_ID);
	}

	@Test
	public void testNotes() throws ServerSideException {
		userDefinedTypeValueActions.setRunningInUnitTest();
		// create user
		final String aliceFullName = "Ms Alice";
		final Long userIdAlice = createUser("alice", "test", ObidosConstants.AUTH_SOURCE_LOCAL, aliceFullName);
		final String passPhraseAlice = "This is not a passphrase!";
		// create key pair for the user
		final User alice1 = getUser(userIdAlice);
		userManagementActions.createKeypair(alice1, passPhraseAlice.getBytes());
		final User alice = getUser(userIdAlice); // need to re-load to get public/private key
		final PassphraseHash alicePassHash = summonPWHash(passPhraseAlice.getBytes(), alice);
		assertNotNull(alicePassHash);
		byte[] notes = "This is the test note".getBytes();
		final ItemDTO item = itemActions.create(alice, "Note1", null, getNotebookContainerAssignmentId(alice1), alicePassHash, null, Boolean.TRUE, new SecurityClassificationDTO(), itemActions.convertToItemValues(alice1.getId(), null, notes));
		assertNotNull(item);
		List<UserDefinedTypeValueDTO> note_values = item.getValues();
		assertNotNull(item.getValues());
		assertEquals(1, item.getValues().size());
		note_values.forEach(value -> assertEquals(new String(notes), new String(value.getFieldValues().get(0).getBlobValue())));
	}

	
	private ItemDTO createTestItem(final User user, final PassphraseHash passphraseHash, final Long containerId, final String itemName) {
		final Random rand = new Random();
		final String udtPrefix = "Test UDT ";
		final String udt1Name = udtPrefix + rand.nextInt();
		final String udt2Name = udtPrefix + rand.nextInt();
		final Long udt1 = createUserDefinedType(user, createUserDefinedTypeWithFields(udt1Name, true), passphraseHash);
		final Long udt2 = createUserDefinedType(user, createUserDefinedTypeWithFields(udt2Name, false), passphraseHash);
		final ArrayList<UserDefinedTypeValueDTO> list1 = new ArrayList<UserDefinedTypeValueDTO>();

		expectException(() -> createUserDefinedType(user, createUserDefinedTypeWithFields(udt1Name, true), passphraseHash), DuplicateRecordException.class, "duplicate names are not allowed. User defined Type create should have thrown exception.");
		list1.add(createUDTValue(userDefinedTypeActions.get(user, udt1)));
		list1.add(createUDTValue(userDefinedTypeActions.get(user, udt2)));
		assertEquals(udt1, list1.get(0).getUserDefinedTypeId());
		assertEquals(udt2, list1.get(1).getUserDefinedTypeId());

		return itemActions.create(user, itemName, null, containerId, passphraseHash, null, true, new SecurityClassificationDTO(), list1);
	}

	private class UserWrapper {
		public User user;
		public Long userId;
		public String fullname;
		private Long containerId;
		private Long containerAssignmentId;
		public PassphraseHash passphraseHash;

		public Long getContainerId() { return containerId; }
		public Long getContainerAssignmentId() { return containerAssignmentId; }

		public UserWrapper(final String fullname) {
			this.fullname = fullname;
		}

		public void setUserId(final Long userId) {
			this.userId = userId;
			this.user = getUser(userId); // need to re-load to get public/private key			
		}

		public void createWrapperContainer(String name) {
			containerAssignmentId = createContainer(user, name, false, passphraseHash);
			containerId = containerAssignmentOperations.get(containerAssignmentId).getContainerId();
		}
	}

	private class UserItemWrapper {
		public UserWrapper userWrapper;
		public ItemDTO item;
		public Long itemId;
		public ItemAssignment itemAssignment;
		public Long itemAssignmentId;
		public Long groupId;

		
		public UserItemWrapper(final UserWrapper userWrapper, final ItemDTO item, final Long groupId) {
			this.userWrapper = userWrapper;
			this.item = item;
			
			this.itemId = item.getId();
			this.itemAssignment = itemActions.getItemAssignment(item.getId(), userWrapper.userId);
			this.itemAssignmentId = itemAssignment.getId();
			this.groupId = groupId;

		}
	}

	private UserWrapper createUserWrapper(final String username, final String fullname, final String passPhrase, final String initialContainerName) {
		final UserWrapper wrapper = new UserWrapper(fullname);
		final User user = getUser(createUser(username, "test", ObidosConstants.AUTH_SOURCE_LOCAL, fullname));

		userManagementActions.createKeypair(user, passPhrase.getBytes());
		wrapper.setUserId(user.getId());	
		wrapper.passphraseHash = summonPWHash(passPhrase.getBytes(), wrapper.user);
		assertNotNull(wrapper.passphraseHash);
		assertNotEquals(0L, wrapper.passphraseHash.get().length);
		wrapper.createWrapperContainer(initialContainerName);

		return wrapper;
	}

	private void failedItemAdd(final User user, final PassphraseHash passphraseHash, final Long containerId, final String name, final String failureDescription) {
		expectException(() -> createTestItem(user, passphraseHash, containerId, name), PermissionDeniedException.class, failureDescription);
	}

	private void failedItemUpdate(final User user, final PassphraseHash passphraseHash, final ItemDTO item, final String failureDescription) {
		expectException(() -> itemActions.update(user, item, passphraseHash), PermissionDeniedException.class, failureDescription);
	}

	private void grantContainerUpdatePermission(final UserWrapper owner, final UserWrapper recipient, final Long ownerContainerAssignmentId) {
		final PermissionDTO permission = new PermissionDTO();
		permission.setMayUpdate(true);
		containerActions.grantUsersPermission(owner.user, ownerContainerAssignmentId, permission, toList(recipient.userId), owner.passphraseHash);
	}

	private void grantContainerAddPermission(final UserWrapper owner, final UserWrapper recipient, final Long ownerContainerAssignmentId) {
		final PermissionDTO permission = new PermissionDTO();
		permission.setMayAdd(true);
		containerActions.grantUsersPermission(owner.user, ownerContainerAssignmentId, permission, toList(recipient.userId), owner.passphraseHash);
	}

	private void grantContainerUpdatePermissionToGroup(final UserWrapper owner, final Long groupId, final Long ownerContainerAssignmentId) {
		final PermissionDTO permission = new PermissionDTO();
		permission.setMayUpdate(true);
		containerActions.grantGroupsPermission(owner.user, ownerContainerAssignmentId, permission, toList(groupId), owner.passphraseHash);
	}

	public UserItemWrapper createTestUser(String username, String fullname, String passphrase, String containerName, String groupName, String groupDesc) {
		final UserWrapper user = createUserWrapper(username, fullname, passphrase, containerName);
		
		final Long nullTest = createUserDefinedType(user.user, createUserDefinedTypeWithFields("Alice UDT x", null), user.passphraseHash);
		assertNotNull(getUserDefinedType(user.user, nullTest).getPersonal());
		final ItemDTO item = createTestItem(user.user, user.passphraseHash, user.getContainerAssignmentId(), "My Item");
		final Long groupId = createGroup(user.user, groupName, groupDesc, user.passphraseHash);

		return new UserItemWrapper(user, item, groupId);
	}

	private void tryItemAddThatExpectsFailure(final UserWrapper owner, final UserWrapper recipient) {
		final String newItemName = "Item in Container Shared by owner";
		final Long bobsSharedContainerId = containerActions.getContainerAssignmentId(recipient.user, owner.getContainerId());

		failedItemAdd(recipient.user, recipient.passphraseHash, bobsSharedContainerId, newItemName, "Bob was not granted ADD permission for this container. Add should have thrown exception.");
	}

	private void tryItemUpdate(final UserItemWrapper owner, final UserWrapper recipient) {
		final List<UserDefinedTypeValueDTO> typeList2 = itemActions.get(owner.userWrapper.user, owner.itemAssignmentId, owner.userWrapper.passphraseHash, toList(OrderBy.UDF_POSITION_ASC)).getValues();
		modifyValues(typeList2, "-mod2");
		itemActions.update(recipient.user,  new ItemDTO(owner.itemId, null, typeList2), recipient.passphraseHash);
	}

	private void assertContainerPermission(final SharedContainerResult sharedContainerResult, final Boolean addPermission, final Boolean updatePermission) {
		assertNotNull(sharedContainerResult);
		assertTrue(sharedContainerResult.getTotal() == 1);
		final SharedContainerDTO sharedContainerDTO = sharedContainerResult.getElements().get(0);
		assertNotNull(sharedContainerDTO);
		
		if (addPermission != null) {
			if (addPermission) {
				assertTrue(sharedContainerDTO.getAddPermitted());
			} else {
				assertFalse(sharedContainerDTO.getAddPermitted());				
			}
		}
		
		if (updatePermission != null) {
			if (updatePermission) {
				assertTrue(sharedContainerDTO.getUpdatePermitted());
			} else {
				assertFalse(sharedContainerDTO.getUpdatePermitted());				
			}
		}
	}

	private void ensureContainerPermissionIsSetTo(final User user, final Boolean addPermission, final Boolean updatePermission) {
		assertContainerPermission(containerActions.getContainersSharedWithMe(user, null, null, null, null, null), addPermission, updatePermission);
	}

	private void ensureContainerGroupPermissionIsSetTo(final Group group, final Boolean addPermission, final Boolean updatePermission) {
		//assertContainerPermission(containerActions.getContainersSharedWithGroup(group, null, null, null, null, null), addPermission, updatePermission);
	}

	/**
	 * Test is a user who is not the owner of an Item may update the Item via permissions granted though the Container directly to that user.
	 * @throws ServerSideException
	 */
	@Test
	public void testItemUpdateViaContainerGrantToUser() throws ServerSideException {
		userDefinedTypeValueActions.setRunningInUnitTest();

		final UserItemWrapper alice = createTestUser("alice", "Ms Alice", "This is Alice's passphrase!", "Alice's container of Items", "ContainerGroup", "This is Alice's group for Containers");

		final UserWrapper bob = createUserWrapper("bob", "Mr Bob", "This is bob's passphrase!", "Bob's Container of Items");
		final List<Long> aliceGroupList = new ArrayList<Long>(2);
		aliceGroupList.add(alice.groupId);

		// First have Alice share a container with Bob explicitly
		final Long aliceContainerAssignmentId = alice.userWrapper.getContainerAssignmentId();
		shareContainerWithUser(alice.userWrapper.user, aliceContainerAssignmentId, bob.userId, alice.userWrapper.passphraseHash);

		// Have Bob ensure that the container is shared with him
		ensureContainerPermissionIsSetTo(bob.user, false, false);
		tryItemAddThatExpectsFailure(alice.userWrapper, bob);
		expectException(() -> tryItemUpdate(alice, bob), ServerSideException.class, "Bob does not yet have update permission. Exception expected.");
		grantContainerUpdatePermission(alice.userWrapper, bob, aliceContainerAssignmentId);
		ensureContainerPermissionIsSetTo(bob.user, false, true);

		// Have Bob update the Item. He should have permission via the container
		final ItemAssignment bobItemAssignment = itemAssignmentOperations.get(alice.itemId, bob.userId);
		// Ensure Bob does not have permission to modify or own the item via item permissions
		assertFalse(bobItemAssignment.getUpdatePermitted());
		assertFalse(bobItemAssignment.getOwnershipControl());
		tryItemUpdate(alice, bob);

		grantContainerAddPermission(alice.userWrapper, bob, aliceContainerAssignmentId);
		ensureContainerPermissionIsSetTo(bob.user, true, true);
	}

	/**
	 * Test is a user who is not the owner of an Item may update the Item via permissions granted though the Container to a Group.
	 * @throws ServerSideException
	 */
	@Test
	public void testItemUpdateViaContainerGrantToGroup() throws ServerSideException {
		userDefinedTypeValueActions.setRunningInUnitTest();

		final UserItemWrapper alice = createTestUser("alice", "Ms Alice", "This is Alice's passphrase!", "Alice's container of Items", "ContainerGroup", "This is Alice's group for Containers");

		final UserWrapper bob = createUserWrapper("bob", "Mr Bob", "This is bob's passphrase!", "Bob's Container of Items");
		final List<Long> userIds = new ArrayList<Long>(2);
		userIds.add(bob.userId);
		userActions.addUsersToGroup(alice.userWrapper.user, userIds, alice.groupId, alice.userWrapper.passphraseHash, null, "Share Comment");

		// First have Alice share a container with a group Bob is a member of
		final Long aliceContainerAssignmentId = alice.userWrapper.getContainerAssignmentId();
		containerActions.shareContainerWithGroups(alice.userWrapper.user, aliceContainerAssignmentId, () -> toList(alice.groupId).stream(), alice.userWrapper.passphraseHash, null, null);
		tryItemAddThatExpectsFailure(alice.userWrapper, bob);
		expectException(() -> tryItemUpdate(alice, bob), ServerSideException.class, "Bob does not yet have update permission. Exception expected.");
		grantContainerUpdatePermissionToGroup(alice.userWrapper, alice.groupId, aliceContainerAssignmentId);

		// Have Bob update the Item. He should have permission via the container from the group he is a member of
		final ItemAssignment bobItemAssignment = itemAssignmentOperations.get(alice.itemId, bob.userId);
		// Ensure Bob does not have permission to modify or own the item via item permissions
		assertFalse(bobItemAssignment.getUpdatePermitted());
		assertFalse(bobItemAssignment.getOwnershipControl());
		final List<UserDefinedTypeValueDTO> typeList2 = itemActions.get(alice.userWrapper.user, alice.itemAssignmentId, alice.userWrapper.passphraseHash, toList(OrderBy.UDF_POSITION_ASC)).getValues();
		modifyValues(typeList2, "-mod2");
		itemActions.update(bob.user,  new ItemDTO(alice.itemId, null, typeList2), bob.passphraseHash);
	}
	
	@Test
	public void testItems() throws ServerSideException {
		userDefinedTypeValueActions.setRunningInUnitTest();

		final UserWrapper alice = createUserWrapper("alice", "Ms Alice", "This is Alice's passphrase!", "Alice's container of Items");
		final Long nullTest = createUserDefinedType(alice.user, createUserDefinedTypeWithFields("Alice UDT x", null), alice.passphraseHash);
		assertNotNull(getUserDefinedType(alice.user, nullTest).getPersonal());
		final ItemDTO newItem1 = createTestItem(alice.user, alice.passphraseHash, alice.getContainerAssignmentId(), "My Item");
		final ItemDTO newItem2 = createTestItem(alice.user, alice.passphraseHash, alice.getContainerAssignmentId(), "The Other Item");
		final Long aliceItem1 = newItem1.getId();

		final ItemAssignment ia1 = itemActions.getItemAssignment(newItem1.getId(), alice.userId);
		final ItemAssignment ia2 = itemActions.getItemAssignment(newItem2.getId(), alice.userId);
		final Long itemAssignmentId1 = ia1.getId();
		final Long itemAssignmentId2 = ia2.getId();
		assertTrue(ia2.getUpdatePermitted());
		assertTrue(ia2.getOwnershipControl());
		final Long itemId2 = getItemId(ia2.getId());
		final ItemDTO item = getItem(alice.user, itemAssignmentId1, alice.passphraseHash);
		assertEquals(itemAssignmentId2, itemAssignmentOperations.get(itemId2, alice.userId).getId());
		assertNotNull(item.getValues());
		assertEquals(2, item.getValues().size());
		ensureItemHasFields(item);
		final ItemsResult result = itemActions.getMyItems(alice.user, alice.getContainerAssignmentId(), null, null, null, null, null, null);
		assertEquals(2, (int) result.getTotal());
		assertEquals(2, userDefinedTypeValueOperations.getUserDefinedTypeValues(alice.userId, aliceItem1).collect(Collectors.toList()).size());
		final List<UserDefinedTypeValueDTO> typeList = getItem(alice.user, itemAssignmentId1, alice.passphraseHash).getValues();
		assertEquals(2, typeList.size());
		assertEquals(2, getItem(alice.user, itemAssignmentId2, alice.passphraseHash).getValues().size());
		modifyValues(typeList, "-mod1");
		logger.info("Updating values for item " + itemAssignmentId1);
		itemActions.update(alice.user, new ItemDTO(aliceItem1, null, typeList), alice.passphraseHash);
		
		final UserWrapper bob = createUserWrapper("bob", "Mr Bob", "This is bob's passphrase!", "Bob's Container of Items");

		itemActions.shareItemWithUsers(alice.user, aliceItem1, alice.passphraseHash, null, toStreamSupplier(bob.userId), "", Boolean.TRUE);
		assertEquals(1, numberOfItemsSharedWithUser(bob.user));
		itemActions.shareItemWithUsers(alice.user, itemId2, alice.passphraseHash, null, toStreamSupplier(bob.userId), "", Boolean.TRUE);
		showRecipients("After Sharing with Bob", alice.user, itemAssignmentId1);
		assertEquals(2, numberOfItemsSharedWithUser(bob.user));
		assertEquals(0, numberOfItemsSharedWithUser(alice.user));
		final UsersResult<LimitedUserDTO> ur = getUsersForItem(alice.user, itemAssignmentId1);
		assertNotNull(ur);
		assertEquals((Integer) 1, ur.getTotal());
		assertTrue(bob.userId.equals(ur.getUsers().get(0).getId()));
		assertEquals(1, (int) userActions.getSharesForItem(alice.user, itemAssignmentId1, null, null,  null, null, null).getTotal());
		
		logger.info("Getting item shared with Bob.");
		// Test Group Operations with Items
		final String groupAlice = "Alice's Main Group";
		final Long groupId = createGroup(alice.user, groupAlice, "This is Alice's group", alice.passphraseHash);
		assertNotNull(userActions.getGroupDTO(groupId));

		final UserWrapper cindy = createUserWrapper("cindy", "Cindy McCindyFace", "This is cindy's passphrase!", "Cindy's Container of Items");
		final List<LimitedUser> users = new ArrayList<>();
		final List<Long> userIds = new ArrayList<Long>(2);
		userIds.add(cindy.userId);
		userIds.add(bob.userId);
		logger.info("Adding Bob to group.");
		userActions.addUsersToGroup(alice.user, userIds, groupId, alice.passphraseHash, null, "Share Comment");
		assertEquals(2, getShareCount(aliceItem1));
		logger.info("Sharing item with group.");
		itemActions.shareItemWithGroups(alice.user, itemAssignmentId1, alice.passphraseHash, null, toStreamSupplier(groupId), u -> users.add(u), Boolean.TRUE, "");
		assertEquals(2, users.size());
		assertEquals(2, numberOfItemsSharedWithUser(bob.user));
		assertEquals(1, numberOfItemsSharedWithUser(cindy.user));
		assertEquals(3, getShareCount(aliceItem1));
		itemActions.revokeItemFromGroups(alice.user, itemAssignmentId1, toStreamSupplier(groupId), alice.passphraseHash, null, null);
		assertEquals(2, getShareCount(aliceItem1));
		assertEquals(2, numberOfItemsSharedWithUser(bob.user));
		assertEquals(0, numberOfItemsSharedWithUser(cindy.user));
		assertEquals(1, (int) userActions.getSharesForItem(alice.user, itemAssignmentId1, null, null,  null, null, null).getTotal());

		// Test adding and removing users from group to ensure the appropriate
		// items are shared with all users in the group.
		final String groupAlice2 = "Alice's Secondary Group";
		final Long groupId2 = createGroup(alice.user, groupAlice2, "This is Alice's other group", alice.passphraseHash);
		logger.info("Alice, user id " + alice.userId + " created group " + groupId2);
		users.clear();
		itemActions.shareItemWithGroups(alice.user, itemAssignmentId1, alice.passphraseHash, null, toStreamSupplier(groupId2), u -> users.add(u), Boolean.TRUE, "");
		assertEquals((Integer) 0, (Integer) users.size());
		assertEquals((Integer) 2, itemAssignmentOperations.getItemShareCount(aliceItem1));
		assertEquals(2, numberOfItemsSharedWithUser(bob.user));
		assertEquals(0, numberOfItemsSharedWithUser(cindy.user));
		logger.info("item Group Count = " + itemGroupOperations.getItemGroupCount(groupId2));
		final List<SharedItem> sharedItemList = itemOperations.getItemsSharedWithGroup(groupId2, null, null, null, null, null, null).collect(Collectors.toList());
		logger.info("Secondary group has " + sharedItemList.size() + " items.");
		assertEquals(0, numberOfItemsSharedWithUser(cindy.user));
		logger.info("Adding cindy to group " + groupId2);
		addUserToGroup(alice.user, cindy.userId, groupId2, alice.passphraseHash);
		assertEquals(1, numberOfItemsSharedWithUser(cindy.user));
		assertEquals(2, numberOfItemsSharedWithUser(bob.user));

		// Make sure getRecipientsOfSharedItem now includes Cindy
		final UsersResult<LimitedUserDTO> ur2 = getUsersForItem(alice.user, itemAssignmentId1);
		assertNotNull(ur2);
		assertEquals((Integer) 1, ur2.getTotal());	// the action of sharing items via group does not confer census on users in those groups 
		showRecipients("After adding cindy", ur2);
		assertEquals("Mr Bob", ur2.getUsers().get(0).getFullname());
		// Search for recipients with name containing Bob
		final UserDTO userPatterns = new UserDTO();
		userPatterns.setFullname("Bob");
		getUsersForItem(alice.user, itemAssignmentId1, userPatterns).getElements().forEach(lu -> logger.info("Loaded user " + lu.getFullname()));
		assertEquals((Integer) 1, getUsersForItem(alice.user, itemAssignmentId1, userPatterns).getTotal());
		// Search for recipients with name containing Bob, plus user ID of cindy
		logger.info("Getting recipients of shared item " + aliceItem1 + ", preselecing user " + cindy.userId);
		final UsersResult<LimitedUserDTO> ur3 = userActions.getUsersForItem(alice.user, itemAssignmentId1, userPatterns, SharedSetQuality.ONLY_SHARED_WITH, toList(cindy.userId), null, null, toList(OrderBy.FULLNAME_ASC));
		assertNotNull(ur3);
		assertEquals((Integer) 2, ur3.getTotal());
		showRecipients("With Cindy pre-selected ordered first", ur3);
		assertEquals("Cindy McCindyFace", ur3.getUsers().get(0).getFullname());
		addUserToGroup(alice.user, bob.userId, groupId2, alice.passphraseHash);
		assertEquals(2, numberOfItemsSharedWithUser(bob.user));
		userActions.removeUsersFromGroup(alice.user, toList(cindy.userId), groupId2, alice.passphraseHash);
		assertEquals(0, numberOfItemsSharedWithUser(cindy.user));
		itemActions.delete(alice.user, toList(itemAssignmentId2), alice.passphraseHash);
		logger.info("Updating values for item " + itemAssignmentId1 + " again (with shared values)");
		final List<UserDefinedTypeValueDTO> typeList2 = itemActions.get(alice.user, itemAssignmentId1, alice.passphraseHash, toList(OrderBy.UDF_POSITION_ASC)).getValues();
		modifyValues(typeList2, "-mod2");
		itemActions.update(alice.user,  new ItemDTO(aliceItem1, null, typeList2), alice.passphraseHash);
		ItemDTO i2 = itemActions.get(alice.user, itemAssignmentId1, alice.passphraseHash, null);
		for(UserDefinedTypeValueDTO v : i2.getValues()) {
			for(UserDefinedFieldValueDTO fv : v.getFieldValues()) {
				final String s = new String(fv.getBlobValue());
				assertTrue(s.contains("-mod"));
			}
		}
		final ItemAssignment ia = itemAssignmentOperations.get(itemAssignmentId1);
		final Item item1 = itemOperations.get(ia.getItemId());
		final SharedItemsResult sir = itemActions.getItemsSharedWithUser(bob.user, null, null, null, null, null, null);
		for (final SharedItemDTO si : sir.getElements()) {
			final ItemAssignment sia = itemAssignmentOperations.get(si.getId());
			if (item1.getId().equals(sia.getItemId())) {
				final SharedItemDTO sharedItemDTO = itemActions.getSharedItem(bob.user, si.getId(), bob.passphraseHash, toList(OrderBy.UDF_POSITION_ASC));
				compareUDTValues(typeList2, sharedItemDTO.getValues());
				assertEquals(alice.fullname, sharedItemDTO.getOwnerFullname());
				assertNotEquals(si.getId(), sharedItemDTO.getId());
			}
		}
		final PermissionDTO permission = new PermissionDTO();
		permission.setMayUpdate(true);
		expectException(() -> itemActions.grantUsersPermission(cindy.user, itemAssignmentId1, permission, toList(bob.userId), cindy.passphraseHash), ItemOwnershipException.class, "Cindy does not own this item. Grant should have thrown exception.");
		expectException(() -> itemActions.update(bob.user,  new ItemDTO(aliceItem1, null, typeList2), bob.passphraseHash), PermissionDeniedException.class, "Bob does not own this item. Update should have thrown exception.");
		showRecipients("At start of permission testing", alice.user, itemAssignmentId1);
		itemActions.grantUsersPermission(alice.user, itemAssignmentId1, permission, toList(bob.userId), alice.passphraseHash);
		final ItemAssignment bobItem1Assignment = itemAssignmentOperations.get(aliceItem1, bob.userId);
		final Long bobItem1AssignmentId = bobItem1Assignment.getId();
		assertTrue(bobItem1Assignment.getUpdatePermitted());
		assertFalse(bobItem1Assignment.getOwnershipControl());
		itemActions.update(bob.user,  new ItemDTO(aliceItem1, null, typeList2), bob.passphraseHash);
		expectException(() -> itemActions.takeOwnership(bob.user, itemAssignmentId1, bob.getContainerAssignmentId(), bob.passphraseHash, null), ItemOwnershipException.class, "Wrong item assignment ID specified. Take should have thrown exception.");
		expectException(() -> itemActions.takeOwnership(bob.user, bobItem1AssignmentId, bob.getContainerAssignmentId(), bob.passphraseHash, null), PermissionDeniedException.class, "Bob does not have ownership control for this item. Take should have thrown exception.");
		permission.setHasOwnershipControl(true);
		itemActions.grantUsersPermission(alice.user, itemAssignmentId1, permission, toList(bob.userId), alice.passphraseHash);
		logger.info("Bob is taking ownership of item1 " + aliceItem1);
		itemActions.takeOwnership(bob.user, bobItem1AssignmentId, bob.getContainerAssignmentId(), bob.passphraseHash, null); // now Bob has ownership control, should work
		expectException(() -> showRecipients("After Bob has taken ownership", bob.user, itemAssignmentId1), PermissionDeniedException.class, "Wrong item assignment ID specified. showRecipients should have thrown exception.");
		showRecipients("After Bob has taken ownership", bob.user, bobItem1AssignmentId);
		itemActions.grantUsersPermission(bob.user, bobItem1AssignmentId, permission, toList(alice.userId), bob.passphraseHash);
		userPatterns.setFullname("Alice");
		final UsersResult<?> ur4 = getUsersForItem(bob.user, bobItem1AssignmentId, userPatterns);
		assertFalse(ur4.noResults());
		final ItemAssignment aliceUpdatedItemAssignment = itemAssignmentOperations.get(itemAssignmentId1);
		assertTrue(aliceUpdatedItemAssignment.getUpdatePermitted());	      	// Alice can still update
		assertTrue(aliceUpdatedItemAssignment.getOwnershipControl());	      	// Alice can still own
		itemActions.grantUsersPermission(bob.user, bobItem1AssignmentId, permission, toList(alice.userId), bob.passphraseHash);

		// Test ability of user to add item to shared container when granted access via container shared with group.
		final Long aliceContainerForGroupTest = createContainer(alice.user, "Container of Items for Group Add Test", false, alice.passphraseHash);
		final Long aliceGroup = createGroup(alice.user, "ContainerGroup", "This is Alice's group for Containers", alice.passphraseHash);
		final List<Long> userIds2 = new ArrayList<Long>(2);
		final List<Long> groupList = toList(aliceGroup);

		final ContainerAssignment ca = containerAssignmentOperations.get(aliceContainerForGroupTest);
		logger.info("Loaded Container Assignment" + ca.getName());
		final Long containerId = ca.getContainerId();
		final Container c = containerOperations.get(containerId);
		logger.info("Loaded Container " + c.getName());

		userIds2.add(cindy.userId);
		userIds2.add(bob.userId);
		userActions.addUsersToGroup(alice.user, userIds2, aliceGroup, alice.passphraseHash, null, "Share Comment");

		// Now have Alice share a container with a group that Bob is a member of.
		containerActions.shareContainerWithGroups(alice.user, aliceContainerForGroupTest, () -> groupList.stream(), alice.passphraseHash, null, null);
		final ContainerDTO groupContainerDTO = containerActions.get(alice.user, aliceContainerForGroupTest);
		logger.info("Container Assignment ID: " + aliceContainerForGroupTest);
		logger.info("Container ID: " + groupContainerDTO.getId());

		final ContainerGroupAssignment cga = containerGroupAssignmentOperations.get(containerId, aliceGroup);
		logger.info("Loaded Container Group Assignment " + cga.getName());

		// Now see if users in group3
		final Long bobsSharedContainerId = containerActions.getContainerAssignmentId(bob.user, containerId);
		final String newItemName = "Bobs Item in Container Shared by Alice";

		// Have Bob attempt to add something to the container WITHOUT group permission.
		failedItemAdd(bob.user, bob.passphraseHash, bobsSharedContainerId, newItemName, "Bob was not granted ANY permission for this container. Add should have thrown exception.");
		// Give members of the group permission to Add items to the container.
		final PermissionDTO groupPermissions = new PermissionDTO(false, false, false);

		containerActions.grantGroupsPermission(alice.user, aliceContainerForGroupTest, groupPermissions, groupList, alice.passphraseHash);
		
		final UserGroupComboResult comboResult = userActions.getSharesForContainer(alice.user, aliceContainerForGroupTest, null, null, 0, null, null);
		assertFalse(comboResult.getElements().get(0).getAddPermitted());
		
		final Integer shareCount = comboResult.getTotal();
		System.out.println(" Share count = " + shareCount);

		failedItemAdd(bob.user, bob.passphraseHash, bobsSharedContainerId, newItemName, "Bob was not granted ADD permission for this container. Add should have thrown exception.");

		// The group may now add items
		groupPermissions.setMayAdd(true);
		containerActions.grantGroupsPermission(alice.user, aliceContainerForGroupTest, groupPermissions, groupList, alice.passphraseHash);
		final UserGroupComboResult comboResult2 = userActions.getSharesForContainer(alice.user, aliceContainerForGroupTest, null, null, 0, null, null);
		System.out.println(" Share count = " + comboResult2.getTotal());
		assertTrue(comboResult2.getElements().get(0).getAddPermitted());

		// Have Bob attempt to add something to the container.
		final ItemDTO bobsItem = createTestItem(bob.user, bob.passphraseHash, bobsSharedContainerId, newItemName);		
		final ItemAssignment cindyIA = itemActions.getItemAssignment(bobsItem.getId(), cindy.userId);
		
		// Ensure Cindy can load the item in the shared container
		final ItemDTO cindyItem = itemActions.get(cindy.user, cindyIA.getId(), cindy.passphraseHash, null);
		failedItemUpdate(cindy.user, cindy.passphraseHash, cindyItem, "Cindy should not have permission to update an item.");

		// Allow group members to update items now
		groupPermissions.setMayUpdate(true);
		containerActions.grantGroupsPermission(alice.user, aliceContainerForGroupTest, groupPermissions, groupList, alice.passphraseHash);
		itemActions.update(cindy.user, cindyItem, cindy.passphraseHash);
	}

	private static Long getDocumentId(final ItemDTO itemDTO) {
		for(UserDefinedTypeValueDTO v : itemDTO.getValues()) {
			for(UserDefinedFieldValueDTO f : v.getFieldValues()) {
				if (f.getDocument() != null) {
					return f.getDocument().getId();
				}
			}
		}
		throw new ServerSideException("Unable to find document ID");
	}

	@Test
	public void testDocuments() throws ServerSideException {
		final String aliceFullName = "Ms Alice";
		final Long userIdAlice = createUser("alice", "test", ObidosConstants.AUTH_SOURCE_LOCAL, aliceFullName);
		final String passPhraseAlice = "This is not a passphrase!";
		final User alice1 = getUser(userIdAlice);
		userManagementActions.createKeypair(alice1, passPhraseAlice.getBytes());
		final User alice = getUser(userIdAlice); // need to re-load to get public/private key
		final PassphraseHash alicePassHash = summonPWHash(passPhraseAlice.getBytes(), alice);
		final Long containerId = createContainer(alice, "Container of Items", false, alicePassHash);

		// Create Item with a Document
		final Long typeWithDocument = createUserDefinedType(alice, createUserDefinedTypeWithFields("DocumentTest", false, 5), alicePassHash);
		final UserDefinedTypeDTO docTypeDTO = userDefinedTypeActions.get(alice, typeWithDocument);
		assertEquals(5, docTypeDTO.getFields().size());
		final ArrayList<UserDefinedTypeValueDTO> list3 = new ArrayList<UserDefinedTypeValueDTO>();
		list3.add(createUDTValue(userDefinedTypeActions.get(alice, typeWithDocument)));
		final ItemDTO aliceDocumentItem = itemActions.create(alice, "My Item with a Document", null, containerId, alicePassHash, null, true, null, list3);
		final Long documentId = getDocumentId(aliceDocumentItem);
		final ItemAssignment ia = itemAssignmentOperations.getByDocumentId(userIdAlice, documentId);
		assertTrue(ia.getUpdatePermitted());
		documentOperations.updateSelective(new Document(documentId, "filename".getBytes(), "GUID Filename", "decryption key".getBytes(), "public key".getBytes(), "file length".getBytes(), "compressed length".getBytes()));
	}

	@Test
	public void testNotificationTemplates() throws ServerSideException {
		final Long userIdAlice = createUser("alice", "test", ObidosConstants.AUTH_SOURCE_LOCAL, "Ms Alice");
		final Capability capability = capabilityOperations.getByUserId(userIdAlice);
		final NotificationTemplate notificationTemplate = new NotificationTemplate(ObidosConstants.UNIT_TEST_TEMPLATE);
		capability.setModifyEmailTemplates(true);
		capabilityOperations.update(capability);
		notificationTemplate.setSubject("This is the subject");
		notificationTemplate.setMessage("This is the item sharing template".getBytes());
		notificationTemplate.setHtml_message("This is the item sharing template".getBytes());
		notificationTemplateOperations.create(notificationTemplate);
	}

	private static String readFile(String filePath) {
		String fileName = filePath;
		File file = new File(fileName);
		try (FileReader fr = new FileReader(file)) {
			try(BufferedReader br = new BufferedReader(fr)) {
				String line;
				StringBuilder sb = new StringBuilder();
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				return sb.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Test
	public void testCreateAccountCreatedEmailTemplate() throws ServerSideException {

		/*
		 * // load json template file URL url=this.getClass().getResource(
		 * "/test/com/spenego/obidos/account_created.json"); String
		 * filePath=url.getFile(); logger.info(">>>>>>>>>>>>>>>> File: " +
		 * filePath); String jsonStr=readFile(filePath);
		 *
		 * final Long userId=createUser("jsnow","test", ObidosConstants.
		 * AUTH_SOURCE_LOCAL,"John Snow"); final User user=getUser(userId);
		 * final Capability capability=capabilityOperations.getByUserId(userId);
		 * final NotificationTemplate template=new
		 * NotificationTemplate(NotificationTemplate.
		 * NEW_USER_CREATION_NOTIFICATION_ID);
		 * capability.setModifyEmailTemplates(true);
		 * capabilityOperations.update(capability);
		 *
		 * template .setMessage(jsonStr.getBytes());
		 * notificationTemplateOperations.create(template);
		 */
	}

	// read a JSON file from unit test directory
	private String readJSONFile(String jSONFilename) {
		URL url = this.getClass().getResource("/test/com/spenego/obidos/" + jSONFilename);
		if (url != null) {
			String filePath = url.getFile();
			logger.info(">>>>>>>>>>>>>>>> File: " + filePath);
			String jsonStr = readFile(filePath);
			return jsonStr;
		}
		return null;
	}

	@Test
	public void testCreateItemSharedJSONTesmplate() throws ServerSideException {
		// read JSON template file
		URL url = this.getClass().getResource("/test/com/spenego/obidos/item_shared.json");
		if (url != null) {
			String filePath = url.getFile();
			logger.info(">>>>>>>>>>>>>>>> File: " + filePath);
			String jsonStr = readFile(filePath);

			// save the JSON template to database
			final Long userId = createUser("jflow", "test", ObidosConstants.AUTH_SOURCE_LOCAL, "John Flow");
			getUser(userId);
			final Capability capability = capabilityOperations.getByUserId(userId);
			capability.setModifyEmailTemplates(true);
			capabilityOperations.update(capability);

			final NotificationTemplate notificationTemplate = new NotificationTemplate(ObidosConstants.UNIT_TEST_TEMPLATE);

			// looks like subject is needed otherwise unit test fails
			notificationTemplate.setSubject("Subject is not used");
			// notificationTemplate.setHtmlMessage("note used".getBytes());

			notificationTemplate.setMessage(jsonStr.getBytes());
			notificationTemplateOperations.create(notificationTemplate);
		}
	}

	@Test
	public void testStringReplace() {
		String str = "hi {{name}}";
		String rStr = str.replace("{{name}}", "John Snow");
		assertEquals(rStr, "hi John Snow");
	}

	@Test
	public void testTemplateInfoBuilder() {
		TemplateInfo emailTemlateInfo = TemplateInfoBuilder.Utility.getInstance().withName("Mary Jane").withAction_url("https://spenego.com?token=deadbeefcafe")
				.withAccount("An Spenego Obidos") // should come from database
				.withProduct_name("Spenego Obidos Privacy Management And Sharing") // should come from database
				.build();
		assertEquals("Mary Jane", emailTemlateInfo.getName());
	}

	private int numberOfUDTFields(final User caller, final Long typeId) {
		return userDefinedTypeActions.get(caller, typeId).getFields().size();
	}

	private int itemFieldCound(final User caller, final Long itemAssignmentId1, final PassphraseHash passHash) {
		return itemActions.get(caller, itemAssignmentId1, passHash, null).getValues().get(0).getFieldValues().size();
	}

	private UserDefinedTypeDTO updateUserDefinedType(UserCombo user, UserDefinedTypeDTO type) {
		return userDefinedTypeActions.update(user.user, type, user.passHash, true);
	}

	@Test
 	@Transactional(readOnly=false)
	public void testTemplateActions() {
		final UserCombo zed   = new UserCombo("zed");
		final UserCombo alice = new UserCombo("alice");
		final UserCombo bob   = new UserCombo("bob");
		final Long personalUDTID = createUserDefinedType(zed.user, createUserDefinedTypeWithFields("JUnit: Zed UDT 1 zxc", true), zed.passHash);
		final Long globalUDTID   = createUserDefinedType(zed.user, createUserDefinedTypeWithFields("JUnit: Zed UDT 2 aaa", false), zed.passHash);

		assertEquals(4, numberOfUDTFields(zed.user, personalUDTID));
		assertEquals(4, numberOfUDTFields(zed.user, globalUDTID));

		
		final UserDefinedTypeDTO personalUDT = userDefinedTypeActions.get(zed.user, personalUDTID);
		personalUDT.addField(createNewField("Field 5", 5));
		final UserDefinedTypeDTO globalUDT = userDefinedTypeActions.get(zed.user, globalUDTID);
		globalUDT.addField(createNewField("Field 5", 5));
		globalUDT.addField(createNewField("Field 6", 6));

		final UserDefinedTypeDTO updatedPersonalUDT = updateUserDefinedType(zed, personalUDT);
		final Long updatedPersonalUDTID = updatedPersonalUDT.getId();
		assertEquals(5, numberOfUDTFields(zed.user, updatedPersonalUDTID));
		final UserDefinedTypeDTO updatedGlobalUDT = updateUserDefinedType(zed, globalUDT);
		final Long updatedGlobalUDTID = updatedGlobalUDT.getId();
		assertEquals(6, numberOfUDTFields(zed.user, updatedGlobalUDTID));

		final ArrayList<UserDefinedTypeValueDTO> list1 = new ArrayList<>(1);
		final ArrayList<UserDefinedTypeValueDTO> list2 = new ArrayList<>(1);
		list1.add(createUDTValue(userDefinedTypeActions.get(zed.user, updatedPersonalUDTID)));
		list2.add(createUDTValue(userDefinedTypeActions.get(zed.user, updatedGlobalUDTID)));

		final Long containerAssignmentId1 = createContainer(zed.user, "JUnit: Websites", false, zed.passHash);
		createContainer(zed.user, "JUnit: Internal", false, zed.passHash);

		ItemDTO item1 = itemActions.create(zed.user, "My Item with Personal UDT", null, containerAssignmentId1, zed.passHash, null, true, new SecurityClassificationDTO(), list1);
		ItemDTO item2 = itemActions.create(zed.user, "My Item with Global UDT", null, containerAssignmentId1, zed.passHash, null, true, new SecurityClassificationDTO(), list2);

		Long itemAssignmentId1 = itemAssignmentOperations.get(item1.getId(), zed.id).getId();
		assertEquals(5, itemFieldCound(zed.user, itemAssignmentId1, zed.passHash));
		Long itemAssignmentId2 = itemAssignmentOperations.get(item2.getId(), zed.id).getId();
		assertEquals(6, itemFieldCound(zed.user, itemAssignmentId2, zed.passHash));

		final Long group1 = createGroup(zed.user, "JUnit: Group1", "Zed's First Group", zed.passHash);
		final Long group2 = createGroup(zed.user, "JUnit: Group2", "Zed's Secpmd Group", zed.passHash);

		addUserToGroup(zed.user, bob.id,   group1, zed.passHash);
		addUserToGroup(zed.user, alice.id, group1, zed.passHash);
		addUserToGroup(zed.user, bob.id,   group2, zed.passHash);
		addUserToGroup(zed.user, alice.id, group2, zed.passHash);

		containerActions.shareContainerWithGroups(zed.user, containerAssignmentId1, StreamSupplier.create(toList(group1)), zed.passHash, null, null);

		final UserDefinedTypeDTO personalUDT2 = userDefinedTypeActions.get(zed.user, updatedPersonalUDTID);

		personalUDT2.addField(createNewField("Field 6", 6));
		final UserDefinedTypeDTO globalUDT2 = userDefinedTypeActions.get(zed.user, updatedGlobalUDTID);
		globalUDT2.addField(createNewField("Field 7", 7));
		globalUDT2.addField(createNewField("Field 8", 8));

		final UserDefinedTypeDTO personalUDT3 = updateUserDefinedType(zed, personalUDT2);
		assertEquals(6, numberOfUDTFields(zed.user, personalUDT3.getId()));
		final UserDefinedTypeDTO globalUDT3 = updateUserDefinedType(zed, globalUDT2);
		assertEquals(8, numberOfUDTFields(zed.user, globalUDT3.getId()));
		assertEquals(5, itemFieldCound(zed.user, itemAssignmentId1, zed.passHash));
		assertEquals(6, itemFieldCound(zed.user, itemAssignmentId2, zed.passHash));
	}

	@SuppressWarnings("unused")
	private static TemplateInfo generateTemplateInfo(final String url, final String fullName, String c) {
		final String comment = (c == null || c.length() == 0) ? "" : c;

		TemplateInfo emailTemlateInfo = TemplateInfoBuilder.Utility.getInstance().withName(fullName).withAction_url(url).withComment(comment).withAccount("An Spenego Obidos") // should
				.withProduct_name("Spenego Obidos Privacy Management And Sharing") // should come from database
				.build();
		return emailTemlateInfo;
	}

	private static void deleteFile(String path) {
		// must call gc() otherwise file can not be deleted on Window$ even
		// though file is closed but apparently it still can be in use until
		// gc finalized things. Fucking hate windows.
		System.gc();
		File file = new File(path);
		file.delete();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testModifyJSON() throws ParseException, IOException {
		// read JSON file from unit tetst directory
		String jsonStr = readJSONFile("item_shared.json");
		if (jsonStr != null) {
			logger.info("JSON read: " + jsonStr);
			// we'll use json-simple to read and modify a JSON file
			JSONParser parser = new JSONParser();

			Object obj = parser.parse(jsonStr);
			JSONObject jsonObject = (JSONObject) obj;
			// get "subject"
			String subject = (String) jsonObject.get("subject");
			assertEquals("A secured Item is shared with you", subject);

			String doesNotExist = (String) jsonObject.get("none");
			logger.info("doesNotExist: " + doesNotExist);

			// modify subject
			jsonObject.put("subject", "foo");
			assertEquals("foo", jsonObject.get("subject"));

			String jFPath = "foo.json";
			try (final FileWriter file = new FileWriter(jFPath)) {
				file.write(obj.toString());
				file.flush();
			}

			jsonStr = readFile(jFPath);
			logger.info("Modified JSON: " + jsonStr);
			obj = parser.parse(jsonStr);
			jsonObject = (JSONObject) obj;
			subject = (String) jsonObject.get("subject");
			assertEquals("foo", subject);
			deleteFile(jFPath);
		}
	}

	private void pirntJsonTemplateInfo(String jsonFile) throws ServerSideException {
		String jsonStr = readJSONFile(jsonFile);
		if (jsonStr != null) {
			NotificationTemplateJSONDTO dto = ServerUtils.parseJSONTemplate(jsonStr, NotificationTemplateJSONDTO.class);
			logger.info("---------- " + jsonFile + " -----------------");
			logger.info("Subject: " + dto.getSubject());
			logger.info("Hello: " + dto.getHello());
			logger.info("HTLM message: " + dto.getHtml_message());
			logger.info("  button title: " + dto.getButton_title());
			logger.info("  wbutton trouble: " + dto.getButton_trouble());
			logger.info("Text Message: " + dto.getText_message());
			logger.info("contact: " + dto.getContact());
			logger.info("footer: " + dto.getFooter());
			logger.info("========================================================");
		}
	}

	@Test
	public void testReadingJSONTemplate() throws ServerSideException {
		pirntJsonTemplateInfo("item_shared.json");
		pirntJsonTemplateInfo("account_created.json");
		pirntJsonTemplateInfo("password_reset.json");
		pirntJsonTemplateInfo("password_reset_warning.json");
	}

	@After
	public void after() {
		logger.info("in after..");
	}
}
