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

package com.spenego.Obidos.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.javafaker.Faker;
import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.spenego.Obidos.server.actions.LdapConfigActions;
import com.spenego.Obidos.server.actions.SmtpConfigActions;
import com.spenego.Obidos.server.actions.UserDefinedTypeActions;
import com.spenego.Obidos.server.actions.UserManagementActions;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.UserOperations;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.CapabilityDTO;
import com.spenego.Obidos.shared.dto.LdapDTO;
import com.spenego.Obidos.shared.dto.SmtpConfigDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.exceptions.PassphraseRequiredException;
import com.spenego.Obidos.shared.exceptions.RecordModifiedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * Populate database with test data
 * Before running it:
 * ./compile.sh
 * ./db/init.sh
 * 
 * - If from Eclipse:
 *      Right click on CreateTestData.java and Run As -> Java Application
 * - From command line, run:
 *      ./db/create_test_data.sh
 *
 * @author spgdev@spenego.com - Copenhagen, Dec 25, 2016
 */
public class CreateTestData
{
	private final static Logger logger = LoggerFactory.getLogger(CreateTestData.class);
	private final static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
	private final static String passphrase ="this is a big secret";  // NOSONAR -- yes this is a credential

	private static UserManagementActions	userManagementActions;
	private static UserOperations			userOperations;
	private static LdapConfigActions		ldapConfigActions;
	private static SmtpConfigActions		smtpConfigActions;
	private static UserDefinedTypeActions	userDefinedTypeActions;
	private static User admin;

	/*
	 * private static void createUser(String username, String fullname, String
	 * authSource) throws SodiumLibraryException { UserOperations userOperations
	 * = (UserOperations) context.getBean("userOperations");
	 * logger.info("userOperations: " + userOperations); }
	 */

	/**
	 * We bootstrap the initial admin user. He is a special case. All regular
	 * operations require an admin or user to complete.
	 *
	 * @return
	 * @throws ServerSideException
	 */
	private static User getAdmin() throws ServerSideException
	{
		return userOperations.getUserById(ObidosConstants.FIRST_ROOT_ADMIN_ID);
//		return userOperations.getUserByUsername("admin");
	}


	private static UserDTO createUser(String username, String fullname, String email, Boolean isAdmin,
			Boolean canCreateGlobalTemplate)
					throws ServerSideException
	{
		UserDTO userDTO = new UserDTO();
		userDTO.setAdministrator(isAdmin);
		userDTO.setUsername(username);
		userDTO.setFullname(fullname);
		userDTO.setPassword(username);
		userDTO.setAuthSource(ObidosConstants.AUTH_SOURCE_LOCAL);
		userDTO.setEmail1(email);

		Long userId = null;
		CapabilityDTO capabilityDTO = new CapabilityDTO(userId);
		capabilityDTO.setCreateGlobalTemplate(canCreateGlobalTemplate);
		Boolean notifyUser = false;
		String emailComment = null;
		final Long id = userManagementActions.createUser(admin, userDTO, capabilityDTO, notifyUser, emailComment);
		userDTO.setId(id);
		return userDTO;
	}

	private static Long createRandomUser(boolean lockUser, String authSource) throws ServerSideException
	{
		// use Java Faker to create users
		Faker faker = new Faker();
		String fullname = faker.name().fullName();
		String username = faker.name().username();
		String email = username + "@" + faker.internet().domainName();
		System.out.println(username + " " + fullname);

		UserDTO userDTO = new UserDTO();
		userDTO.setAdministrator(Boolean.FALSE);
		userDTO.setUsername(username);
		userDTO.setFullname(fullname);
		userDTO.setPassword(username);
		if (authSource != null)
		{
			userDTO.setAuthSource(authSource);
		}
		else
		{
			userDTO.setAuthSource(ObidosConstants.AUTH_SOURCE_LOCAL);
		}
		userDTO.setEmail1(email);
		// user international format otherwise validator will catch
		String phoneNumber = "+1 " + faker.phoneNumber().cellPhone();
		userDTO.setPhone(phoneNumber);

		Boolean notifyUser = false;
		String emailComment = null;
		final Long id = userManagementActions.createUser(admin, userDTO, null, notifyUser, emailComment); // call
		// restricted
		// createUser
		// call
		userDTO.setId(id);
		try
		{
			if (lockUser)
			{
				userDTO.setLocked(true);
			}
			userManagementActions.modifyUser(admin, userDTO);
		} catch (ServerSideException ex)
		{
			userManagementActions.delete(admin, Arrays.asList(id));
			throw ex;
		}

		// create key pair
		userManagementActions.createKeypair(userOperations.get(id), passphrase.getBytes());
		return id;

	}

	private static void createCreditCardsPersonalTemplate(User user, PassphraseHash passphraseHash)
	{
		UserDefinedTypeDTO typeDTO = new UserDefinedTypeDTO();
		typeDTO.setPersonal(true);
		typeDTO.setName("Credit Cards");
		typeDTO.setPersonal(true);

		List<String> list = new ArrayList<String>();
		list.add("Credit Card Number");
		list.add("Name on Credit Card");
		list.add("Credir Card Security Code (CVV)");
		list.add("Expiration Date");
		list.add("Website URL");
		list.add("Website Login Name");
		list.add("Website Password");
		list.add("Bank Phone Number");

		for (int i = 0; i < list.size(); i++)
		{
			UserDefinedFieldDTO field = new UserDefinedFieldDTO();
			field.setName(list.get(i));
			field.setType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
			field.setPosition(i + 1);
			typeDTO.addField(field);
		}
		try
		{
			userDefinedTypeActions.create(user,typeDTO, passphraseHash);
		} catch (ServerSideException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static Long createCreditCardsGlobalTemplate(User user, PassphraseHash passphraseHash)
	{
		UserDefinedTypeDTO typeDTO = new UserDefinedTypeDTO();
		typeDTO.setPersonal(true);
		typeDTO.setName("Credit Cards(GT)");
		typeDTO.setPersonal(false); // <<<--

		List<String> list = new ArrayList<String>();
		list.add("Credit Card Number");
		list.add("Name on Credit Card");
		list.add("Credir Card Security Code (CVV)");
		list.add("Expiration Date");
		list.add("Website URL");
		list.add("Website Login Name");
		list.add("Website Password");
		list.add("Bank Phone Number");

		for (int i = 0; i < list.size(); i++)
		{
			UserDefinedFieldDTO field = new UserDefinedFieldDTO();
			field.setName(list.get(i));
			field.setType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
			field.setPosition(i + 1);
			typeDTO.addField(field);
		}
		try
		{
			return userDefinedTypeActions.create(user, typeDTO, passphraseHash);
		} catch (final ServerSideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


	private static void createBanksPersonalTemplate(User user, PassphraseHash passphraseHash)
	{
		UserDefinedTypeDTO typeDTO = new UserDefinedTypeDTO();
		typeDTO.setName("Banks");
		typeDTO.setPersonal(true);
		typeDTO.setPersonal(true);

		List<String> list = new ArrayList<String>();
		list.add("Account Number");
		list.add("Routing Number");
		list.add("ATM PIN");
		list.add("Website URL");
		list.add("Website Login Name");
		list.add("Website Password");
		list.add("Bank Phone Number");

		for (int i = 0; i < list.size(); i++)
		{
			UserDefinedFieldDTO field = new UserDefinedFieldDTO();
			field.setName(list.get(i));
			field.setType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
			field.setPosition(i + 1);
			typeDTO.addField(field);
		}
		try
		{
			userDefinedTypeActions.create(user, typeDTO, passphraseHash);
		} catch (ServerSideException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void log(String msg)
	{
		System.out.println(msg);
	}

	private static void deleteUser(String username)
	{
		try
		{
			userOperations.deleteUserByUsername(username);
		} catch (ServerSideException ex)
		{
			// admin is not there yet.
		}
	}

	private static void saveLdapSettings(LdapDTO ldap)
	{
		try
		{
			ldapConfigActions.create(admin, ldap);
		} catch (RecordModifiedException e1)
		{
			e1.printStackTrace();
		} catch (ServerSideException e1)
		{
			e1.printStackTrace();
		}
	}

	private static void saveLdapSettinsDeployHostOverTunnel()
	{
		String url = "ldap://127.0.0.1:8089";
		String baseDN = "dc=spenego,dc=com";
		String userDN = "cn=admin," + baseDN;
		String bindPass = "test";

		LdapDTO ldap = new LdapDTO();
		ldap.setName("LDAP@deploy->tunnel");
		ldap.setLdapuri(url); // required
		ldap.setBaseDn(baseDN); // required
		ldap.setBindDn(userDN);
		ldap.setBindPass(bindPass);
		ldap.setAuthAttr("CN");
		saveLdapSettings(ldap);
	}

	private static void saveLdapSettinsDeployHost()
	{
		String url = "ldap://127.0.0.1:389";
		String baseDN = "dc=spenego,dc=com";
		String userDN = "cn=admin," + baseDN;
		String bindPass = "test";

		LdapDTO ldap = new LdapDTO();
		ldap.setName("LDAP@deploy->local");
		ldap.setLdapuri(url); // required
		ldap.setBaseDn(baseDN); // required
		ldap.setBindDn(userDN);
		ldap.setBindPass(bindPass);
		ldap.setAuthAttr("CN");
		saveLdapSettings(ldap);

	}

	private static void createLdapConfigurations()
	{
		saveLdapSettinsDeployHost();
		saveLdapSettinsDeployHostOverTunnel();
	}

	// don't use gmail for testing anymore, use it when we really need to
	// test authentication again. Use MailHog instead
	private static void createSmtpConfigurationMailHog() throws ServerSideException
	{
		final SmtpConfigDTO dto = new SmtpConfigDTO();

		dto.setName("default");

		dto.setSmtpServer("localhost");

		dto.setSmtpPort(1025);
		dto.setUseStartTls(true);
		dto.setUseSsl(false);

		dto.setSmtpUsername("Privacy.Schutz@gmail.com");
		dto.setSmtpPassword("none");
		dto.setUseAuthentication(true);

		smtpConfigActions.create(admin, dto);
	}

	/*
	private static void createSmtpConfigurationsStartTLS() throws ServerSideException {
		final SmtpConfigDTO dto = new SmtpConfigDTO();

		dto.setName("default");
		dto.setSmtpServer("smtp.gmail.com");
		dto.setSmtpPort(587);
		dto.setUseStartTls(true);
		dto.setUseSsl(false);
		dto.setSmtpUsername("Privacy.Schutz@gmail.com");
		dto.setSmtpPassword("IcevarbAx2$");
		dto.setUseAuthentication(true);

		Long id = smtpConfigActions.create(admin, dto);
		logger.info(() -> "Created SMTP StartTLS config: id=" + id);
	}
*/
	/*
	private static void createSmtpConfigurationsSSL() throws ServerSideException
	{
		SmtpConfigDTO dto = new SmtpConfigDTO();

		dto.setName("SSL");

		dto.setSmtpServer("smtp.gmail.com");

		dto.setSmtpPort(465);
		dto.setUseSsl(true);
		dto.setUseStartTls(false);

		dto.setSmtpUsername("Privacy.Schutz@gmail.com");
		dto.setSmtpPassword("IcevarbAx2$");
		dto.setUseAuthentication(true);

		Long id = smtpConfigActions.create(admin, dto);
		logger.info(() -> "Created SMTP SSL config: id=" + id);
	}
*/
	
	private static void createSmtpConfigurations() throws ServerSideException
	{
		// use configuration for MailHog running at localhost. It listens on
		// port 1025 for SMTP and and 8025 for web interface
		// spgdev, Oct-03-2018
		createSmtpConfigurationMailHog();

		// use the following if needed to test real SMTP auth
//		createSmtpConfigurationsStartTLS();
//		createSmtpConfigurationsSSL();
	}

	/**
	 * Computes the passphrase hash but also zeros out the passphrase.
	 *
	 * @param passphrase
	 * @param salt
	 * @return
	 * @throws SodiumLibraryException
	 */
	private static byte[] pwhash(final byte[] passphrase, final byte[] salt) throws ServerSideException {
		try {
			return SodiumLibrary.cryptoPwhashArgon2i(passphrase, salt);
		} catch (SodiumLibraryException e) {
			throw new ServerSideException("Unable to create passphrase hash");
		}
	}

	private static Decoder decoder = Base64.getDecoder();

	private static byte[] decodeBase64(final String encoded) {
		return decoder.decode(encoded);
	}

	private static byte[] getPassphraseHash(final byte[] passphrase, final String salt) throws ServerSideException {
		return pwhash(passphrase, decodeBase64(salt));
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
	private static PassphraseHash summonPWHash(final byte[] passphrase, final User user) throws ServerSideException {
		if (passphrase == null) {
			throw new PassphraseRequiredException();
		}
		return new PassphraseHash(getPassphraseHash(passphrase, user.getSalt()));
	}

	public static void main(String[] args) throws SodiumLibraryException, ServerSideException
	{
		boolean debug = false;
		context.scan("com.spenego.Obidos.server");
		context.refresh();

		String passPhrase = passphrase; // NOSONAR -- we are not concerned about test data security

		userManagementActions		= (UserManagementActions) context.getBean("userManagementActions");
		ldapConfigActions			= (LdapConfigActions) context.getBean("ldapActions");
		userOperations				= (UserOperations) context.getBean("userOperations");
		smtpConfigActions			= (SmtpConfigActions) context.getBean("smtpConfigActions");
		userDefinedTypeActions		= (UserDefinedTypeActions) context.getBean("userDefinedTypeActions");

		admin = getAdmin();
		if (debug)
		{
			logger.info(() -> "MMM debug is set not doing anything..");
			return;
		}

		deleteUser("admin2");
		boolean canCreateGlobalTemplate = false;
		createUser("admin2", "Administrator Two", "admin2@spenego.com", Boolean.TRUE, canCreateGlobalTemplate);

		deleteUser("adminfoo");
		createUser("adminfoo", "Administrator", "adminfoo@spenego.com", Boolean.TRUE, canCreateGlobalTemplate);

		deleteUser("spgdev");
		canCreateGlobalTemplate = true;
		UserDTO userDTO = createUser("spgdev", "SPG Dev", "spgdev@spenego.com", Boolean.FALSE, canCreateGlobalTemplate);
		userManagementActions.createKeypair(userOperations.get(userDTO.getId()), passPhrase.getBytes());

		User spgdev = userOperations.get(userDTO.getId());
		final PassphraseHash spgdevPWHash = summonPWHash(passPhrase.getBytes(), spgdev);
		
		// Change initial password to passphrase
		final String newPassword = passphrase;
		String oldPassword = "spgdev";
		userManagementActions.changePassword(spgdev, oldPassword, newPassword);

		// create personal templates
		createCreditCardsPersonalTemplate(spgdev, spgdevPWHash);
		createBanksPersonalTemplate(spgdev, spgdevPWHash);
		createCreditCardsGlobalTemplate(spgdev, spgdevPWHash);


		deleteUser("test1");
		canCreateGlobalTemplate = true;
		userDTO = createUser("test1", "Test User1", "test1@obidos.test", Boolean.FALSE, canCreateGlobalTemplate);
		userManagementActions.createKeypair(userOperations.get(userDTO.getId()), passPhrase.getBytes());
		final User test1 = userOperations.get(userDTO.getId());
		final PassphraseHash test1PWHash = summonPWHash(passPhrase.getBytes(), test1);

		createCreditCardsPersonalTemplate(test1, test1PWHash);
		createBanksPersonalTemplate(test1, test1PWHash);

		deleteUser("test2");
		canCreateGlobalTemplate = true;
		userDTO = createUser("test2", "Test User2", "test2@obidos.test", Boolean.FALSE, canCreateGlobalTemplate);
		userManagementActions.createKeypair(userOperations.get(userDTO.getId()), passPhrase.getBytes());
		final User test2 = userOperations.get(userDTO.getId());
		final PassphraseHash test2PWHash = summonPWHash(passPhrase.getBytes(), test2);

		createCreditCardsPersonalTemplate(test2, test2PWHash);
		createBanksPersonalTemplate(test2, test2PWHash);

		int numberOfUsers=5;
		String authSource = null;
		for (int i = 0; i < numberOfUsers; i++)
		{
			boolean lockUser = false;
			Long userId = createRandomUser(lockUser, authSource);
			userOperations.get(userId);
		}

		// Create 20 users and mark them deleted
		for (int i = 0; i < 20; i++)
		{
			boolean lockUser = true;
			createRandomUser(lockUser, authSource);
		}

		// create AD/LDAP configurations
		createLdapConfigurations();

		authSource = "LDAP@deploy->tunnel";
		createRandomUser(false, authSource);
		createRandomUser(false, authSource);
		createRandomUser(false, authSource);
		createRandomUser(false, authSource);
		createRandomUser(false, authSource);

		authSource = "LDAP@deploy->local";
		createRandomUser(false, authSource);
		createRandomUser(false, authSource);
		createRandomUser(false, authSource);
		createRandomUser(false, authSource);

		// create SMTP configurations
		createSmtpConfigurations();

		log ("Done");

		// create some ad.println("----------- Done creating test data------------");
	}
}
