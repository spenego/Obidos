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

import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.EXTRA_CHECK_CONTAINS_EMAIL1;
import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.EXTRA_CHECK_CONTAINS_FULLNAME;
import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.EXTRA_CHECK_CONTAINS_PHONE;
import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.EXTRA_CHECK_CONTAINS_USERNAME;
import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.PASSPHRASE_COMPLEXITY_REQUIREMENTS;
import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.PASSWORD_COMPLEXITY_REQUIREMENTS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.spenego.Obidos.server.actions.ContainerActions;
import com.spenego.Obidos.server.actions.UserDefinedFieldActions;
import com.spenego.Obidos.server.actions.UserManagementActions;
import com.spenego.Obidos.server.model.Capability;
import com.spenego.Obidos.server.model.ComplexityRequirements;
import com.spenego.Obidos.server.model.Container;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.model.UserDefinedType;
import com.spenego.Obidos.server.operations.CapabilityOperations;
import com.spenego.Obidos.server.operations.ComplexityRequirementsOperations;
import com.spenego.Obidos.server.operations.ContainerOperations;
import com.spenego.Obidos.server.operations.UserDefinedTypeOperations;
import com.spenego.Obidos.server.operations.UserOperations;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.ParameterStringsDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public class CreateInitialData {
	private static final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

	private static CapabilityOperations				capabilityOperations;
	private static ComplexityRequirementsOperations	complexityRequirementsOperations;
	private static ContainerActions					containerActions;
	private static ContainerOperations				containerOperations;
	private static UserDefinedFieldActions			userDefinedFieldActions;
	private static UserDefinedTypeOperations		userDefinedTypeOperations;
	private static User								admin;
	private static UserManagementActions			userManagementActions;
	private static UserOperations					userOperations;


	private static void log(String msg) {
		System.out.println(msg); // NOSONAR
	}

	private static void createAdminCapability(final Long uid) {
		Capability capability = new Capability();
		capability.setChangeAdminCredentials(true);
		capability.setChangeUserCredentials(true);
		capability.setCreateAdmin(true);
		capability.setCreateUser(true);
		capability.setDeleteAdmin(true);
		capability.setDeleteUser(true);
		capability.setLockAdmin(true);
		capability.setLockUser(true);
		capability.setRootAdmin(true);
		capability.setModifyEmailTemplates(true);
		capability.setUserId(uid);
		capability.setCreatedAt(new Date());
		capability.setVersion(1);
		capability.setCreateGlobalTemplate(true);
		capability.setModifySettings(true);

		capabilityOperations.create(capability);
	}

	/**
	 * We bootstrap the initial admin user. He is a special case. All regular
	 * operations require an admin or user to complete.
	 *
	 * @return
	 * @throws ServerSideException
	 */
	private static User createAdmin() throws ServerSideException {
		log("Creating Administator");
		final User newAdmin = new User(ObidosConstants.FIRST_ROOT_ADMIN_ID, "admin");
		newAdmin.setFullname("Obidos Administrator");
		newAdmin.setAuthSource(ObidosConstants.AUTH_SOURCE_LOCAL);
		newAdmin.setEmail1("admin@obidos.local");
		newAdmin.setAdministrator(Boolean.TRUE);
		newAdmin.setSecurityClearance(100);
		newAdmin.setPreferenceFlags(0L);
		newAdmin.setLoginCount(0);
		newAdmin.setUnsuccessfulLoginAttempts(0);

		final Long uid = userOperations.create(newAdmin);
		newAdmin.setAdministrator(Boolean.TRUE);
		createAdminCapability(uid);
		log("Updating user " + uid);
		userOperations.updateSelective(newAdmin);

		try {
			admin = userOperations.get(uid);
		} catch(Exception ex) {
			log("Caught " + ex);
			throw ex;
		}

		final UserDTO adminDTO = new UserDTO(newAdmin.getId(), Boolean.TRUE);
		adminDTO.setAdministrator(Boolean.TRUE);
		adminDTO.setPassword("admin");
		adminDTO.setPasswordChangeRequired(true);

		try {
			log("Updating capabilities for admin " + adminDTO.getId());
			userManagementActions.modifyUser(admin, adminDTO);
		} catch (ServerSideException ex) {
			log("Caught " + ex);
			userManagementActions.delete(admin, Arrays.asList(newAdmin.getId()));
			throw ex;
		}

		// Need to create default container manually for admin
		containerActions.createDefaultPrivateContainer(admin);

		return userOperations.get(newAdmin.getId());
	}

	private static void createEncryptedType(final User user, final UserDefinedTypeDTO userDefinedType, final List<String> nameList, final List<Long> idList) {
		for (int i = 0; i < nameList.size(); i++) {
			final UserDefinedFieldDTO field = idList == null ? new UserDefinedFieldDTO() : new UserDefinedFieldDTO(idList.get(i));
			field.setName(nameList.get(i));
			field.setType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
			field.setPosition(i+1);
			userDefinedType.addField(field);
		}
		try {
			final Long id = userDefinedTypeOperations.create(new UserDefinedType(userDefinedType.getId(), userDefinedType.getName(), user.getId(), userDefinedType.getPersonal()));
			if (userDefinedType.getFields() != null && !userDefinedType.getFields().isEmpty()) {
				for(final UserDefinedFieldDTO field : userDefinedType.getFields()) {
					field.setTypeId(id);
					userDefinedFieldActions.create(id, field, user.getId(), user.getUsername(), true); // pass true for isAdHoc since we don't care to Audit initial data
				}
			}
		} catch (final ServerSideException e) {
			e.printStackTrace();
		}
	}

	private static void createEncryptedType2(final User user, final UserDefinedTypeDTO userDefinedType, final List<ParameterStringsDTO> nameList, final List<Long> idList) {
		for (int i = 0; i < nameList.size(); i++) {
			final UserDefinedFieldDTO field = idList == null ? new UserDefinedFieldDTO() : new UserDefinedFieldDTO(idList.get(i));
			field.setName(nameList.get(i).getFieldName());
			field.setType(nameList.get(i).getFieldType());
			field.setPosition(i+1);
			userDefinedType.addField(field);
		}
		try {
			final Long id = userDefinedTypeOperations.create(new UserDefinedType(userDefinedType.getId(), userDefinedType.getName(), user.getId(), userDefinedType.getPersonal()));
			if (userDefinedType.getFields() != null && !userDefinedType.getFields().isEmpty()) {
				for(final UserDefinedFieldDTO field : userDefinedType.getFields()) {
					field.setTypeId(id);
					userDefinedFieldActions.create(id, field, user.getId(), user.getUsername(), true); // pass true for isAdHoc since we don't care to Audit initial data
				}
			}
		} catch (final ServerSideException e) {
			e.printStackTrace();
		}
	}

	private static void createCredentialsTemplate(final User user) {
		final UserDefinedTypeDTO userDefinedType = new UserDefinedTypeDTO(UserDefinedTypeDTO.CREDENTIALS_ID, "Login Credentials");
		userDefinedType.setPersonal(false);

		final List<String> list = new ArrayList<>();
		list.add("Target");
		list.add("Username");
		list.add("Password");
		list.add("Details");

		createEncryptedType(user, userDefinedType, list, null);
	}

	private static void createWindowsActivationKeyTemplate(final User user) {
		final UserDefinedTypeDTO userDefinedType = new UserDefinedTypeDTO(UserDefinedTypeDTO.SOFTWARE_LICENSE_ID, "Software License Key");
		userDefinedType.setPersonal(false);

		final List<String> list = new ArrayList<>();
		list.add("Vendor");
		list.add("Product");
		list.add("License/Key");
		list.add("Expiration");
		list.add("Details");

		createEncryptedType(user, userDefinedType, list, null);
	}

	private static void createWiFiTemplate(final User user) {
		final UserDefinedTypeDTO userDefinedType = new UserDefinedTypeDTO(UserDefinedTypeDTO.WIFI_PASSWORD_ID, "WiFi Credentials");
		userDefinedType.setPersonal(false);

		final List<String> list = new ArrayList<>();
		list.add("Venue/Location");
		list.add("SSID");
		list.add("WiFi Password");
		list.add("Details");

		createEncryptedType(user, userDefinedType, list, null);
	}

	private static void createBankingTemplate(final User user) {
		final UserDefinedTypeDTO userDefinedType = new UserDefinedTypeDTO(UserDefinedTypeDTO.BANK_ACCOUNT_ID, "Bank Account");
		userDefinedType.setPersonal(false);

		final List<String> list = new ArrayList<>();
		list.add("Bank Name");
		list.add("Routing Number");
		list.add("Account Number");
		list.add("Account Type");
		list.add("Account Owner");
		list.add("ATM/Debit card PIN");
		list.add("Swift/IFSC Code");
		list.add("Bank Web Site");
		list.add("Bank Phone Number");
		list.add("Other Details");

		createEncryptedType(user, userDefinedType, list, null);
	}

	private static void createCreditCardTemplate(final User user) {
		final UserDefinedTypeDTO userDefinedType = new UserDefinedTypeDTO(UserDefinedTypeDTO.CREDIT_CARD_ID, "Credit Card");
		userDefinedType.setPersonal(false);

		final List<String> list = new ArrayList<>();
		list.add("Brand");
		list.add("Card Number");
		list.add("Expiration");
		list.add("Security Code");
		list.add("Issuing Bank");
		list.add("Support Phone#");
		list.add("Details");

		createEncryptedType(user, userDefinedType, list, null);
	}

	private static void createContactInfoTemplate(final User user) {
		final UserDefinedTypeDTO userDefinedType = new UserDefinedTypeDTO(UserDefinedTypeDTO.CONTACT_INFO_ID, "Contact Information");
		userDefinedType.setPersonal(false);

		final List<String> list = new ArrayList<>();
		list.add("Full Name");
		list.add("Title");
		list.add("Address");
		list.add("Email");
		list.add("Phone (M)");
		list.add("Phone (V)");
		list.add("Phone (H)");
		list.add("Fax");
		list.add("Twitter");
		list.add("Facebook");
		list.add("Notes");

		createEncryptedType(user, userDefinedType, list, null);
	}

	private static void createInsuranceDetailsTemplate(final User user) {
		final UserDefinedTypeDTO userDefinedType = new UserDefinedTypeDTO(UserDefinedTypeDTO.INSURANCE_DETAILS_ID, "Insurance Details");
		userDefinedType.setPersonal(false);

		final List<String> list = new ArrayList<>();
		list.add("Insurance Company");
		list.add("Policy#");
		list.add("Type (Auto/Building/..)");
		list.add("Expiration Date");
		list.add("Coverage Amount");
		list.add("Phone (Claims)");
		list.add("Notes");

		createEncryptedType(user, userDefinedType, list, null);
	}

	private static void createSubscriptionTemplate(final User user) {
		final UserDefinedTypeDTO userDefinedType = new UserDefinedTypeDTO(UserDefinedTypeDTO.SUBSCRIPTION_ID, "Membership/Subscription");
		userDefinedType.setPersonal(false);

		final List<String> list = new ArrayList<>();
		list.add("Institution");
		list.add("Membership Number");
		list.add("Subscriber Name");
		list.add("Contact Phone#");
		list.add("Expiration Date");
		list.add("Dues");
		list.add("Payment Method");
		list.add("Notes");

		createEncryptedType(user, userDefinedType, list, null);
	}

	private static void createSupportContractTemplate(final User user) {
		final UserDefinedTypeDTO userDefinedType = new UserDefinedTypeDTO(UserDefinedTypeDTO.SUPPORT_CONTRACT_ID, "Support Contract");
		final List<ParameterStringsDTO> list = new ArrayList<>();

		userDefinedType.setPersonal(false);

		ParameterStringsDTO parameterStrings1 = new ParameterStringsDTO();
		parameterStrings1.setFieldName("Vendor");
		parameterStrings1.setFieldType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
		list.add(parameterStrings1);
		ParameterStringsDTO parameterStrings2 = new ParameterStringsDTO();
		parameterStrings2.setFieldName("Product/Item");
		parameterStrings2.setFieldType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
		list.add(parameterStrings2);
		ParameterStringsDTO parameterStrings3 = new ParameterStringsDTO();
		parameterStrings3.setFieldName("Customer Id");
		parameterStrings3.setFieldType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
		list.add(parameterStrings3);
		ParameterStringsDTO parameterStrings4 = new ParameterStringsDTO();
		parameterStrings4.setFieldName("Contract#");
		parameterStrings4.setFieldType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
		list.add(parameterStrings4);
		ParameterStringsDTO parameterStrings5 = new ParameterStringsDTO();
		parameterStrings5.setFieldName("Support Phone#");
		parameterStrings5.setFieldType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
		list.add(parameterStrings5);
		ParameterStringsDTO parameterStrings6 = new ParameterStringsDTO();
		parameterStrings6.setFieldName("Expiration Date");
		parameterStrings6.setFieldType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
		list.add(parameterStrings6);
		ParameterStringsDTO parameterStrings7 = new ParameterStringsDTO();
		parameterStrings7.setFieldName("Notes");
		parameterStrings7.setFieldType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
		list.add(parameterStrings7);
		ParameterStringsDTO parameterStrings8 = new ParameterStringsDTO();
		parameterStrings8.setFieldName("Attachment");
		parameterStrings8.setFieldType(UserDefinedFieldDTO.TYPE_DOCUMENT);
		list.add(parameterStrings8);

		createEncryptedType2(user, userDefinedType, list, null);
	}

	private static void create2FA_QRCodeTemplate(final User user) {
		final UserDefinedTypeDTO userDefinedType = new UserDefinedTypeDTO(UserDefinedTypeDTO.QRCODE_2FA_ID, "2FA QR Code");
		final List<ParameterStringsDTO> list = new ArrayList<>();

		userDefinedType.setPersonal(false);

		ParameterStringsDTO parameterStrings1 = new ParameterStringsDTO();
		parameterStrings1.setFieldName("Issuer");
		parameterStrings1.setFieldType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
		list.add(parameterStrings1);
		ParameterStringsDTO parameterStrings2 = new ParameterStringsDTO();
		parameterStrings2.setFieldName("Account");
		parameterStrings2.setFieldType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
		list.add(parameterStrings2);
		ParameterStringsDTO parameterStrings3 = new ParameterStringsDTO();
		parameterStrings3.setFieldName("Comment 1");
		parameterStrings3.setFieldType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
		list.add(parameterStrings3);
		ParameterStringsDTO parameterStrings4 = new ParameterStringsDTO();
		parameterStrings4.setFieldName("Comment 2");
		parameterStrings4.setFieldType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
		list.add(parameterStrings4);
		ParameterStringsDTO parameterStrings5 = new ParameterStringsDTO();
		parameterStrings5.setFieldName("Add 2FA QR Code");
		parameterStrings5.setFieldType(UserDefinedFieldDTO.TYPE_QRCODE);
		list.add(parameterStrings5);

		createEncryptedType2(user, userDefinedType, list, null);
	}


	private static void createFileUploadTemplate(final User user) {
		final UserDefinedTypeDTO userDefinedType = new UserDefinedTypeDTO(UserDefinedTypeDTO.FILE_UPLOAD_ID, "File Share");
		final List<ParameterStringsDTO> list = new ArrayList<>();

		userDefinedType.setPersonal(false);

		ParameterStringsDTO parameterStrings1 = new ParameterStringsDTO();
		parameterStrings1.setFieldName("Info");
		parameterStrings1.setFieldType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
		list.add(parameterStrings1);
		ParameterStringsDTO parameterStrings2 = new ParameterStringsDTO();
		parameterStrings2.setFieldName("Comment");
		parameterStrings2.setFieldType(UserDefinedFieldDTO.TYPE_ENCRYPTED);
		list.add(parameterStrings2);
		ParameterStringsDTO parameterStrings3 = new ParameterStringsDTO();
		parameterStrings3.setFieldName("Attachment");
		parameterStrings3.setFieldType(UserDefinedFieldDTO.TYPE_DOCUMENT);
		list.add(parameterStrings3);

		createEncryptedType2(user, userDefinedType, list, null);
	}

	private static void createWebsiteTemplate(final User user) {
		final UserDefinedTypeDTO userDefinedType = new UserDefinedTypeDTO(UserDefinedTypeDTO.WEBSITE_ID, "Web Site");
		userDefinedType.setPersonal(false);

		final List<String> list = new ArrayList<>();
		list.add("Title");
		list.add("Web Url");
		list.add("Description");
		list.add("Comments");

		createEncryptedType(user, userDefinedType, list, null);
	}

	private static void createNotesTemplate(final User user) {
		final UserDefinedTypeDTO userDefinedType = new UserDefinedTypeDTO(UserDefinedTypeDTO.NOTES_ID, "Notes");
		userDefinedType.setPersonal(false);
		createEncryptedType(user, userDefinedType, Arrays.asList("Notes"), Arrays.asList(UserDefinedFieldDTO.TYPE_NOTES_FIELD_ID));
	}

	private static void exWrapper(final Consumer<Void> consumer) {
		try {
			consumer.accept(null);
		} catch (final ServerSideException e) {
			e.printStackTrace();
		}		
	}

	private static void createNotesContainer(final User user) {
		exWrapper(v -> containerOperations.create(new Container(ContainerDTO.NOTEBOOK_ID, user.getId(), "Notebook", false)));
	}

	/*
	private static void createSystemConfig() {
		exWrapper(v -> systemConfigOperations.create(new SystemConfig(1L, "example.com", "spenego.com", "default", 3000)));
	} unused */

	private static void createPasswordComplexitySet() {
		final long extraChecks = EXTRA_CHECK_CONTAINS_EMAIL1 | EXTRA_CHECK_CONTAINS_FULLNAME | EXTRA_CHECK_CONTAINS_PHONE | EXTRA_CHECK_CONTAINS_USERNAME;

		ComplexityRequirements cr = new ComplexityRequirements(1L, admin, "default", 1, PASSWORD_COMPLEXITY_REQUIREMENTS,   14, 18, extraChecks);
		cr.setMinimumUppercase(1);
		cr.setMinimumLowercase(1);
		
		int minimumLength = 14;
		int minimumEntropy = 18;
		int minimumLowercase = 0;
		int minimumUppercase = 0;
		int minimumSpecial = 0;
		int maxAgeInDays = 0;
		int minimumNumbers = 0;
		Integer minAgeInSecondsBeforeReset = 0;
		ComplexityRequirements passComplexityRequirements =
				new ComplexityRequirements(1L, 
						admin, "default", 1, 
						PASSWORD_COMPLEXITY_REQUIREMENTS,
						minimumLength,
						minimumEntropy,
						minimumLowercase,
						minimumUppercase,
						minimumSpecial,
						maxAgeInDays,
						minimumNumbers,
						minAgeInSecondsBeforeReset,
						extraChecks);

		complexityRequirementsOperations.create(passComplexityRequirements);

		minimumLength = 18;
		minimumEntropy = 28;
		ComplexityRequirements passPhraseComplexityRequirements =
				new ComplexityRequirements(2L, 
						admin, "default", 1, 
						PASSPHRASE_COMPLEXITY_REQUIREMENTS,
						minimumLength,
						minimumEntropy,
						minimumLowercase,
						minimumUppercase,
						minimumSpecial,
						maxAgeInDays,
						minimumNumbers,
						minAgeInSecondsBeforeReset,
						extraChecks);

		complexityRequirementsOperations.create(passPhraseComplexityRequirements);
	}

	public static void main(String[] args) throws SodiumLibraryException, ServerSideException {
		log("Starting");
		context.scan("com.spenego.Obidos.server");
		context.refresh();

		userOperations					= (UserOperations) context.getBean("userOperations");
		containerActions				= (ContainerActions) context.getBean("containerActions");
		containerOperations				= (ContainerOperations) context.getBean("containerOperations");
		capabilityOperations			= (CapabilityOperations) context.getBean("capabilityOperations");
		complexityRequirementsOperations= (ComplexityRequirementsOperations) context.getBean("complexityRequirementsOperations");
		userManagementActions			= (UserManagementActions) context.getBean("userManagementActions");
		userDefinedFieldActions			= (UserDefinedFieldActions) context.getBean("userDefinedFieldActions");
		userDefinedTypeOperations		= (UserDefinedTypeOperations) context.getBean("userDefinedTypeOperations");

		userManagementActions.setBlockAudit(true);
		containerActions.setBlockAudit(true);
		userDefinedFieldActions.setBlockAudit(true);

		log("Creating admin");
		admin = createAdmin();

//		log("Creating Notes Template");
		createNotesTemplate(admin);

		log("Creating Web Site Template");
        createWebsiteTemplate(admin);

		log("Creating WiFi Password Template");
		createWiFiTemplate(admin);

		log("Creating Support Contract Template");
	       createSupportContractTemplate(admin); 

		log("Creating Windows Actionvation Key Template");
		createWindowsActivationKeyTemplate(admin);

		log("2FA QR Code Template");
	        create2FA_QRCodeTemplate(admin);

		log("Creating Subscription Template");
	        createSubscriptionTemplate(admin); 

		log("Creating Credentials Template");
		createCredentialsTemplate(admin);

		log("Creating Insurance Details Template");
	    createInsuranceDetailsTemplate(admin);

		log("Creating Contact Information Template");
	    createContactInfoTemplate(admin);

		log("Creating Credit Card Template");
		createCreditCardTemplate(admin);

		log("Creating Banking Template");
		createBankingTemplate(admin);

		log("Creating File Upload Template");
	        createFileUploadTemplate(admin);

  		log("Creating Notes Container");
  		createNotesContainer(admin);

// System config is done in the db/init.sh script by loading values in a SQL statement.
//		log("Creating system config");
//		createSystemConfig();

		log("Creating Password Complexity Set");
		createPasswordComplexitySet();

		// create jdoe and mjane user
//		createUsers();

		/*
		createNotificationTemplate(ObidosConstants.ACCOUNT_CREATED_NOTIFICATION_TEMPLATE, admin);
		createNotificationTemplate(ObidosConstants.ITEM_SHARED_NOTIFICATION_TEMPLATE, admin);
		createNotificationTemplate(ObidosConstants.PASSWORD_RESET_NOTIFICATION_TEMPALTE, admin);
		createNotificationTemplate(ObidosConstants.PASSWORD_RESET_WARNING_NOTIFICATION_TEMPALTE, admin);
		createNotificationTemplate(ObidosConstants.ITEM_REVOKED_NOTIFICATION_TEMPLATE, admin);
		createNotificationTemplate(ObidosConstants.CONTAINER_SHARED_NOTIFICATION_TEMPLATE, admin);
		createNotificationTemplate(ObidosConstants.CONTAINER_REVOKED_NOTIFICATION_TEMPLATE, admin);
		createNotificationTemplate(ObidosConstants.PASSPHRASE_RESET_NOTIFICATION_TEMPALTE, admin);
		createNotificationTemplate(ObidosConstants.PASSPHRASE_RESET_WARNING_NOTIFICATION_TEMPALTE, admin);
		*/
		log("Complete");
	}
}
