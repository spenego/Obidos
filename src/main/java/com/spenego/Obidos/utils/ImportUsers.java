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

/*
 * Import bulk users to Obidos from LDIF or CSV file
 * Bug #91, add support for csv
 */

package com.spenego.Obidos.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.ldif.LdapLdifException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.spenego.Obidos.server.actions.ContainerActions;
import com.spenego.Obidos.server.actions.SystemConfigActions;
import com.spenego.Obidos.server.actions.UserDefinedFieldActions;
import com.spenego.Obidos.server.actions.UserManagementActions;
import com.spenego.Obidos.server.model.Ldap;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.LdapConfigOperations;
import com.spenego.Obidos.server.operations.UserOperations;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.CapabilityDTO;
import com.spenego.Obidos.shared.dto.LicenseKeyDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * A CLI to bulk import users from Active Directory
 * 
 * @author spgdev@spenego.com - May-20-2020
 */

class CSVUser
{
	private String username;
	private String fullname;
	private String password;
	private String email;
	private String phone;
	private boolean requires2FA;

	// Generate password using pattern: username.emailPrefix!year
	private String generatePassword()
	{
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		String emailPrefix = "";
		if (email != null && !email.isEmpty())
		{
			int atIndex = email.indexOf('@');
			if (atIndex > 0)
			{
				emailPrefix = email.substring(0, Math.min(atIndex, 4));
			} else
			{
				emailPrefix = email.substring(0, Math.min(email.length(), 4));
			}
		}
		return String.format("%s.%s!%d", username.toLowerCase(), emailPrefix.toLowerCase(), currentYear);
	}

	// Getters and setters
	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getFullname()
	{
		return fullname;
	}

	public void setFullname(String fullname)
	{
		this.fullname = fullname;
	}

	public String getPassword()
	{
		if (password == null || password.trim().isEmpty())
		{
			password = generatePassword();
		}
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public boolean isRequires2FA()
	{
		return requires2FA;
	}

	public void setRequires2FA(boolean requires2FA)
	{
		this.requires2FA = requires2FA;
	}
}

public class ImportUsers
{

	private final static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
	private final static String VERSION = "1.0.3";

	private final static String DEFAULT_ADMIN_USERNAME = "admin";
	
	private final static String LDAP_CONFIG_NAME = "ldapConfigName";
	private final static String LDIF_FILE = "ldifFile";
	private final static String CSV_FILE = "csvFile";
	private final static String REQUIRES_2FA = "requiers2FA";
	private final static String DELETE_ALL = "deleteAll";
	private final static String UID_ATTR = "uidAttr";
	private final static String DEFAULT_UID = "sAMAccountName";
	private final static String NO_EMAIL = "noEmail";
	private final static String DRY_RUN="dryRun";
	private final static String EXAMPLE="example";
	private final static String ADMIN_USERNAME="adminUsername";

	private static ContainerActions containerActions;
	private static UserDefinedFieldActions userDefinedFieldActions;
	private static User admin;
	private static UserManagementActions userManagementActions;
	private static UserOperations userOperations;
	private static LdapConfigOperations ldapConfigOperations;
	private static SystemConfigActions systemConfigActions;

	private static String uidAttrDefault;

	private static void log(String msg)
	{
		System.err.println(msg);
	}
	
	// Instantiate spring beans
	private static void instantiateBeans(final CommandLine cmd)
	{
		context.scan("com.spenego.Obidos.server");
		context.refresh();

		userOperations = (UserOperations) context.getBean("userOperations");
		containerActions = (ContainerActions) context.getBean("containerActions");
		userManagementActions = (UserManagementActions) context.getBean("userManagementActions");
		userDefinedFieldActions = (UserDefinedFieldActions) context.getBean("userDefinedFieldActions");
		ldapConfigOperations = (LdapConfigOperations) context.getBean("ldapConfigOperations");
		systemConfigActions = (SystemConfigActions) context.getBean("systemConfigActions");
		userManagementActions.setBlockAudit(true);
		containerActions.setBlockAudit(true);
		userDefinedFieldActions.setBlockAudit(true);
		String adminUsername = DEFAULT_ADMIN_USERNAME;
		if (cmd.hasOption(ADMIN_USERNAME))
		{
			adminUsername = cmd.getOptionValue(ADMIN_USERNAME);
		}
		admin = userOperations.getUserByUsername(adminUsername);
	}

	public static void main(String[] args) throws ParseException, ParserConfigurationException, LdapLdifException, IOException
	{
		log("Version: " + VERSION);
		Options options = new Options();
		// --ldifFile
		Option ldif = Option.builder().longOpt(LDIF_FILE).argName(LDIF_FILE).hasArg().desc("Path of LDIF file.")
				.build();
		options.addOption(ldif);

		// --csvFile
		Option csv = Option.builder().longOpt(CSV_FILE).argName(CSV_FILE).hasArg().desc("Path of CSV file.").build();
		options.addOption(csv);

		// --ldapConfigName
		Option ldapConfigName = Option.builder().longOpt(LDAP_CONFIG_NAME).argName(LDAP_CONFIG_NAME).hasArg().desc(
				"AD/LDAP Config name. Find it out from Settings > List AD/DLAP Settings. Requires for adding AD/LDAP users.")
				.build();
		options.addOption(ldapConfigName);
		
		// --adminUsername
		Option adminName = Option.builder().longOpt(ADMIN_USERNAME).argName(ADMIN_USERNAME).hasArg().desc(
				"Admin username. Default is " + DEFAULT_ADMIN_USERNAME)
				.build();
		options.addOption(adminName);

		// --requiers2FA
		Option addTwoFA = Option.builder().longOpt(REQUIRES_2FA).argName(REQUIRES_2FA).hasArg(false).desc("2FA requires for password and passphrase reset").build();
		options.addOption(addTwoFA);
		
		// --dryRun
		Option dryRun = Option.builder().longOpt(DRY_RUN).argName(DRY_RUN).hasArg(false).desc("Dry run, do not add any users").build();
		options.addOption(dryRun);

		// --example
		Option example = Option.builder().longOpt(EXAMPLE).argName(EXAMPLE).hasArg(false).desc("Show example of LDIF and CSV file").build();
		options.addOption(example);

		// --noEmail
		Option noEmail = Option.builder().longOpt(NO_EMAIL).argName(NO_EMAIL).hasArg(false)
				.desc("Don't send initial password in notification email").build();
		options.addOption(noEmail);

		// --deleteAll
		Option deleteAllOption = Option.builder().longOpt(DELETE_ALL).argName(DELETE_ALL).hasArg(false)
				.desc("Delete all added users").build();
		options.addOption(deleteAllOption);

		// --uidAttr
		Option uidAttr = Option.builder().longOpt(UID_ATTR).argName(UID_ATTR).hasArg()
				.desc("uid attribute. The default is " + DEFAULT_UID + " in AD/LDAP").build();
		options.addOption(uidAttr);

		HelpFormatter formatter = new HelpFormatter();

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption(EXAMPLE))
		{
			showInputFilesExample();
			System.exit(0);
		}

		if (cmd.hasOption(LDIF_FILE) && cmd.hasOption(CSV_FILE))
		{
			log("ERROR: The options --ldifFile and --csvFile are mutually exclusive. Please specify only one");
			formatter.printHelp("ImportUsers", options);
			System.exit(1);
		}

		if (!cmd.hasOption(LDIF_FILE) && !cmd.hasOption(CSV_FILE))
		{
			log("\nERROR: --ldifFile or --csvFile must be specified.\nUse the option --example for syntax of LDIF or CSV file.\n");
			formatter.printHelp("ImportUsers", options);
			System.exit(1);
		}

		if (cmd.hasOption(LDIF_FILE))
		{
			uidAttrDefault = cmd.getOptionValue(UID_ATTR);
			if (uidAttrDefault == null)
			{
				uidAttrDefault = DEFAULT_UID;
			}
		}

		// instantiate Spring beans
		
		instantiateBeans(cmd);
		
		// check admin 
		if (! admin.getAdministrator())
		{
			log("ERROR: user " + admin.getUsername() + " is not an admin");
			System.exit(1);
		}
		if (! admin.getCreateUser())
		{
			log("ERROR: admin user " + admin.getUsername() + " does not have capability to create users");
			System.exit(1);
		}

		if (cmd.hasOption(LDIF_FILE))
		{
			String ldifFile = cmd.getOptionValue(LDIF_FILE);
			String configName = cmd.getOptionValue(LDAP_CONFIG_NAME);
			try (LdifReader reader = new LdifReader())
			{
				List<LdifEntry> entries = reader.parseLdifFile(ldifFile);

				if (cmd.hasOption(DELETE_ALL))
				{
					log("Delete all users...");
					deleteLDAPUsers(reader, entries, cmd);
					System.exit(0);
				}

				// LDAP configuration in system
				if (configName != null)
				{
					Ldap ldap = ldapConfigOperations.get(configName);
					log("LDAP auth: " + ldap.getName());
				}
				log("Import AD/LDAP users ...");
				importLDAPUsers(reader, configName, entries, cmd.hasOption(REQUIRES_2FA), cmd);
			}
		}
		else
		{
			log("Import users from CSV file ...");
			String csvFilePath = cmd.getOptionValue(CSV_FILE);
			importCSVUsers(csvFilePath, cmd);
		}
		System.exit(0);
	}
	
	private static void enforceUsersInLicense(final int nUsersToImport, final CommandLine cmd)
	{
		final LicenseKeyDTO licenseDTO = systemConfigActions.getCurrentLicense();
		final int maxUsersSupportedByLicense = licenseDTO.getMaxUsers();
		final int totalActiveUsers = userManagementActions.getTotalUserCount(admin);

		if (maxUsersSupportedByLicense > 0)
		{
			int nUsersCanbeAdded = maxUsersSupportedByLicense - totalActiveUsers;
			if (nUsersToImport > nUsersCanbeAdded)
			{
				if (nUsersCanbeAdded < 0)
				{
					nUsersCanbeAdded = 0;
				}
				log("ERROR:");
				log(" Number of users supported by license: " + maxUsersSupportedByLicense);
				log(" Total Active users: " + totalActiveUsers);
				log(" Remaining number of user licenses: " + nUsersCanbeAdded);
				log(" Number of users to import: " + nUsersToImport);
				log("Aborting ...");
				if (! cmd.hasOption(DRY_RUN))
				{
					System.exit(1);
				}
				else
				{
					log("Dry run, not aborting ...");
				}
			}
		}
	}

	private static void deleteLDAPUsers(final LdifReader reader, List<LdifEntry> entries, CommandLine cmd)
	{
		ArrayList<String> users = new ArrayList<>();
		for (LdifEntry entry : entries)
		{
			Attribute usernameAttr = entry.get("sAMAccountName");
			String username = null;
			if (usernameAttr != null)
			{
				username = Objects.toString(usernameAttr.get());
				users.add(username);
			}
		}
		if (users.size() > 0)
		{
			if ( ! cmd.hasOption(DRY_RUN))
			{
				try
				{
					log("Delete all LDAP users ...");
					// we need to call it twice, first one to tombstone. It's a
					// restriction of the API
					userManagementActions.deleteUsersByUsername(admin, users, false);
					userManagementActions.deleteUsersByUsername(admin, users, true);
				} catch (ServerSideException e)
				{
					e.printStackTrace();
					log("Exception: " + e.getMessage());
				}
			}
			else
			{
				log("Dry run, will not Delete all LDAP users ...");
			}

		}
	}

	private static void deleteCSVUsers(List<CSVUser> csvUsers, CommandLine cmd)
	{
		ArrayList<String> users = new ArrayList<>();
		for (CSVUser user : csvUsers)
		{
			String username = user.getUsername();
			users.add(username);
			log("Delete user: " + username);
		}
		if (users.size() > 0)
		{
			if ( ! cmd.hasOption(DRY_RUN))
			{
				try
				{
					log("Delete all CSV users ...");
					// we need to call it twice, first one to tombstone. It's a
					// restriction of the API
					userManagementActions.deleteUsersByUsername(admin, users, false);
					userManagementActions.deleteUsersByUsername(admin, users, true);
				} catch (ServerSideException e)
				{
					e.printStackTrace();
					log("Exception: " + e.getMessage());
				}
			}
			else
			{
				log("Dry run, will not Delete all CSV users ...");
			}

		}
	}

	// Ref: https://www.baeldung.com/java-generate-secure-password
	private static String generateCommonLangPassword()
	{
		String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
		String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
		String numbers = RandomStringUtils.randomNumeric(2);
		String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
		String totalChars = RandomStringUtils.randomAlphanumeric(2);
		String combinedChars = upperCaseLetters.concat(lowerCaseLetters).concat(numbers).concat(specialChar)
				.concat(totalChars);
		List<Character> pwdChars = combinedChars.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
		Collections.shuffle(pwdChars);
		String password = pwdChars.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
				.toString();
		return password;
	}

	private static void importLDAPUsers(final LdifReader reader, final String ldapConfigName, List<LdifEntry> entries,boolean requires2FA, final CommandLine cmd)
	{
		enforceUsersInLicense(entries.size(), cmd);
		
		boolean notifyUsers = false;
		String emailComment = null;
		String type = "AD/DLAP";
		for (LdifEntry entry : entries)
		{
			Attribute dnAttr = entry.get("dn");
			// String dn = dnAttr.toString();

			// check for required attribues
			Attribute usernameAttr = entry.get(uidAttrDefault);
			if (usernameAttr == null)
			{
				log("ERROR: No '" + uidAttrDefault + "' attribute found for  " + dnAttr + ". Ignoring ...");
				continue;
			}
			Attribute fullnameAttr = entry.get("cn");
			if (fullnameAttr == null)
			{
				log("ERROR: No 'CN' attribute found for  " + dnAttr + ". Ignoring ...");
				continue;
			}

			Attribute emailAttr = entry.get("mail");
			if (emailAttr == null)
			{
				log("ERROR: No 'mail' attribute found for  " + dnAttr + ". Ignoring ...");
				continue;
			}

			UserDTO dto = new UserDTO();
			dto.setAdministrator(false);
			if (ldapConfigName != null)
			{
				dto.setAuthSource(ldapConfigName);
			} else
			{
				type = "Local";
				// local users
				dto.setAuthSource(ObidosConstants.AUTH_SOURCE_LOCAL);
				notifyUsers = true;
				long passwordExpireDays = 0;
				dto.setMaximumPasswordAge(passwordExpireDays);
				// check password: is specified in ldif
				// if specified, use it, otherwise generate it
				Attribute passwordAttr = entry.get("password");
				String password = null;
				if (passwordAttr != null)
				{
					password = Objects.toString(passwordAttr.get());
				} else
				{
					// generate a random password
					password = generateCommonLangPassword();
				}
				dto.setPassword(password);
				emailComment = "Your initial password is '" + password + "'."
						+ "You will be prompted to change the password at first login";
			}

			usernameAttr = entry.get(uidAttrDefault);
			String username = null;
			if (usernameAttr != null)
			{
				username = Objects.toString(usernameAttr.get());
			} else
			{
				log("ERROR: No " + uidAttrDefault + " found. Exting ...");
				System.exit(1);
			}

			fullnameAttr = entry.get("cn");
			String fullname = null;
			if (fullnameAttr != null)
			{
				fullname = Objects.toString(fullnameAttr.get());
			}

			emailAttr = entry.get("mail");
			String email = null;
			if (emailAttr != null)
			{
				email = Objects.toString(emailAttr.get());
			}

			if (username != null && fullname != null && email != null)
			{
				dto.setUsername(username);
				dto.setFullname(fullname);
				dto.setEmail1(email);
				CapabilityDTO capabilityDTO = new CapabilityDTO(null);
				capabilityDTO.setCreateGlobalTemplate(false);
				if (requires2FA)
				{
					dto.setTwoFARequired(true);
				}
				if (! cmd.hasOption(DRY_RUN))
				{
					log(">>>> Importing " + type + " user: " + username);
					try
					{
						userManagementActions.createUser(admin, dto, capabilityDTO, notifyUsers, emailComment);
					} catch (ServerSideException e)
					{
						log("ERROR: Could not import user " + username + ":" + e.getMessage());
					}
				}
				else
				{
					log(">>>> Dry run: import " + type + " user: " + username);
				}
			}
			else
			{
				log("Ignoring user " + username + " . username, fullname and email are required");
			}
		}
	}
	
	private static void validateHeaders(CSVRecord headerRow, String[] expectedHeaders)
	{
		if (headerRow.size() != expectedHeaders.length)
		{
			throw new IllegalArgumentException(
					"CSV file has " + headerRow.size() + " columns, expected " + expectedHeaders.length);
		}

		for (int i = 0; i < expectedHeaders.length; i++)
		{
			if (!headerRow.get(i).trim().equalsIgnoreCase(expectedHeaders[i]))
			{
				throw new IllegalArgumentException("Expected column '" + expectedHeaders[i] + "' but found '"
						+ headerRow.get(i) + "' at position " + (i + 1));
			}
		}
	}

	private static void importCSVUsers(final String csvFilePath, final CommandLine cmd)
	{
		List<CSVUser> users = readUsersFromCsv(csvFilePath);

		enforceUsersInLicense(users.size(), cmd);
		if (cmd.hasOption(DELETE_ALL))
		{
			log("Delete all users...");
			deleteCSVUsers(users, cmd);
			System.exit(0);
		}

		for (CSVUser user : users)
		{
			UserDTO dto = new UserDTO();
			dto.setAdministrator(false);
			dto.setAuthSource(ObidosConstants.AUTH_SOURCE_LOCAL);
			long passwordExpireDays = 0;
			dto.setMaximumPasswordAge(passwordExpireDays);
			dto.setPassword(user.getPassword());
			final String username = user.getUsername();
			final String fullname = user.getFullname();
			final String email = user.getEmail();
			final String phone = user.getPhone();
			final String password = user.getPassword();
			dto.setPhone(phone);

			if (username != null && fullname != null && email != null)
			{
				dto.setUsername(username);
				dto.setFullname(fullname);
				dto.setEmail1(email);
				CapabilityDTO capabilityDTO = new CapabilityDTO(null);
				capabilityDTO.setCreateGlobalTemplate(false);
				if (cmd.hasOption(REQUIRES_2FA))
				{
					dto.setTwoFARequired(true);
				}
				if ( ! cmd.hasOption(DRY_RUN))
				{
					try
					{
						log(">>>> Importing  user: " + username);
						log("Initial password of usser " + username + " is '" + password + "' The user will be prompted to change the password at first login");
						boolean notifyUsers = false;
						final String emailComment = "Your initial password is '" + password + "'."
							+ "You will be prompted to change the password at first login";
						userManagementActions.createUser(admin, dto, capabilityDTO, notifyUsers, emailComment);
					} catch (ServerSideException e)
					{
						log("ERROR: Could not import user " + username + ":" + e.getMessage());
					}
				}
				else
				{
					log("Initial password of usser " + username + " is '" + password + "' The user will be prompted to change the password at first login");
					log(">>>> Dry run: not importing CSV usr: " + username +"," + fullname + "," + email +"," + phone);
				}
			}
			else
			{
				log("Ignoring user " + username + " . username, fullname and email are required");
			}

		}
	}

	private static List<CSVUser> readUsersFromCsv(final String fileName) {
		final List<CSVUser> users = new ArrayList<>();

		try(Reader in = new FileReader(fileName)) {
			try (final CSVParser records = CSVFormat.RFC4180.parse(in)) {
				boolean firstRow = true;
	            final String[] expectedHeaders = {"username", "fullname", "password", "email", "phone"};
				for (CSVRecord record : records) {
					if (firstRow)
					{
	                    validateHeaders(record, expectedHeaders);
	                    firstRow = false;
	                    continue;
					}
					CSVUser user = new CSVUser();
					user.setUsername(record.get(0));
					user.setFullname(record.get(1));
					user.setPassword(record.get(2)); // will be generated if empty
					user.setEmail(record.get(3));
					user.setPhone(record.get(4));
					users.add(user);
				}
			} catch (IOException e) {
				log("Error reading CSV: " + e.getMessage());
			}
		} catch (FileNotFoundException e) {
			log("Error reading CSV: " + e.getMessage());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return users;
	}

	private static void showInputFilesExample()
	{
		System.err.println("=============================================");
		System.err.println("LDIF entries in the LDIF file are shown below");
		System.err.println("cn, sAMAccountName, mail are required");
		System.err.println("=============================================");
		System.err.println("");
		System.err.println("dn: CN=John Snow,CN=Users,DC=example,DC=com");
		System.err.println("cn: John Snow");
		System.err.println("sAMAccountName: jsnow");
		System.err.println("mail: John.Snow@example.com");
		System.err.println("");
		System.err.println("dn: CN=Mary Jane,CN=Users,DC=example,DC=com");
		System.err.println("cn: Mary Jane");
		System.err.println("sAMAccountName: mjane");
		System.err.println("mail: Mary.Jane@example.com");
		System.err.println("");
		System.err.println("======================================");
		System.err.println("Entries in the CSV File is shown below");
		System.err.println("username, fullanme and email are required");
		System.err.println("password will be generated if empty");
		System.err.println("======================================");
		System.err.println("");
		System.err.println("username,fullname,password,email,phone");
		System.err.println("jdnow,Jodn Snow,test,John.Snow@exmaple.com,214-232-22323");
		System.err.println("mjane,Mary Jane,,Mary.Jane@exmaple.com,214-232-22323");
		System.err.println("");
	}

	public static String trim(String input)
	{
		BufferedReader reader = new BufferedReader(new StringReader(input));
		StringBuffer result = new StringBuffer();
		try
		{
			String line;
			while ((line = reader.readLine()) != null)
				result.append(line.trim());
			return result.toString();
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
