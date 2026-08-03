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

package com.spenego.Obidos.client.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * Just declare the methods here. Define the messages in the appropriate properties file.
 * For example.
 * ObidosMessages.properties for English
 * ObodosMessages_ar.properties for Arabic
 * ObodosMessages_bn.properties for Bangla
 * ObodosMessages_de.properties for German
 *
 * If a new properties file is created, make sure to add the language in "locale" in Delegator.gwt.xml
 *
 * Look at ApplicationView.ui.xml for example on how to i18n in UI binder
 *
 * To programmatically use a message, do something like
 *  TextBox textBox = new TextBox();
 *  textBox.setValue(ObidosMessages.LANG.loginName());
 *
 *  Note: if @DefaultMessage() is not specified, the message must be defined in the properties file.
 *
 * @author spgdev@spenego.com - Jan 27, 2018
 */
public interface ObidosMessages extends Messages
{
    public static final ObidosMessages LANG = GWT.create(ObidosMessages.class);

    // Login Dialog
    String welcome();
    String pleaseSignIn ();
    String loginName();
    String password();
    String newPassword();
    String signin();
    String authenticating();
    String forgotPassword();
    String forgotLogin();
    String specifyLoginName();
    String specifyLoginPassword();
    String invalidLoginOrPassword();
    String specifyLoginAndPassword();

    // Sort
    String sort();
    String sortNotes();
    String sortByUpdatedDate();
    String sortReverseByUpdatedDate();
    String sortByAlphabetAZ();
    String sortByAlphabetZA();
    String sortByNoteAZ();
    String sortByNoteZA();
    String sortByItemAZ();
    String sortByItemZA();
    String sortByContainerAZ();
    String sortByContainerZA();
    String sortByTemplateAZ();
    String sortByTemplateZA();
    String sortByGroupAZ();
    String sortByGroupZA();

    // General
    // String productName();
    String productNameShort();
    String productNameLabel();
    String productUrlLabel();
    String helpButtonTitle();
    String hideHelpButtonTitle();
    String submitButtonTitle();
    String resetButtonTitle();
    String searchButtonTitle();
    String search();
    String editButtonTitle();
    String testButtonTitle();
    String listButtonTitle();
    String deleteButtonTitle();
    String deleteButtonTitleEllipsis();
    String viewButtonTitle();
    String updateButtonTitle();
    String shareButtonTitle();
    String saveButtonTitle();
    String sharedBy();
    String sharedOnLabel();
    String unshareButtonTitle();
    String removeButtonTitle();
    String revokeButtonTitle();
    String revoke();
    String redAsterisk();
    String sessionHasExpired();
    String passwordStrength();
    String strengthWeak();
    String strengthModerate();
    String strengthStrong();
    String strengthVeryStrong();
    String strengthMilitaryGrade();
    String administrator();
    String lockedUser();
    String locked();
    String lockColor();
    String unlockColor();
    String restoreTombstonedUser();
    String no();
    String yes();
    String unlimited();
    String entropy();
    String info();
    String higerAdjustedEntropyHint();
    String higerRawEntropyHint();
    String adjustedEntropy();
    String rawEntropy();
    String longerPasswordHint();
    String generationAlgorithm();
    String passwordLength();
    String capitalLetter();
    String number();
    String specialCharacter();
    String strength();
    String crackingSpeed();
    String clickOnGenerateButton();
    String estimatedCrackingTime();
    String okButtonTitle();
    String copyButtonTitle();
    String copyTemplateTitle();
    String clearSelectionButtonTitle();
    String clearSelectionButtonText();
    String clearSelection();
    String newUserName();
    String newUserNameOptional();
    String undeleteUser();
    String questionMark();
    String notEncrypted();
    String encrypted();
    String crashed();
    String list();
    String areYouSure ();
    String canNotBeUndone();
    String create();
    String next();
    String update();
    String share();
    String add();
    String show();
    String hide();
    String on();
    String off();
    String error();
    String na();
    String none();
    String notification();
    String loading();
    String action();
    String actions();
    String shared();
    String owned();
    String deleted();
    String revoked();
    String renamed();
    String relinguised();
    String relinquish();
    String container();
    String containers();
    String multipleItems();
    String type();
    String itemType();
    String name();
    String userGroupName();
    String date();
    String owner();
    String email();
    String serverErrorCode500();
    String serverIsTemporarilyUnavailable();
    String serverErrorCode(int code);
    String user();
    String admin();
    String edit();
    String users();
    String distributionList();
    String reply();
    String respond();
    String messages();
    String delete();
    String view();
    String read();
    String own();
    String unread();
    String personal();
    String global();
    String refresh();
    String specify();
    String weak();
    String good();
    String soso();
    String strong();
    String veryStrong();
    String status();
    String attention();
    String about();
    String bottomMargin();
    String object();
    String copy();
    String copied();
    String copyTooltipTimerSchedule();
    String install();
    String stage();
    String organization();
    String never();
    String product();
    String clear();
    String select();
    String httpSchemeLabel();
    String httpSchemeValue();
    String httpsFqdn();
    String httpsPortLabel();
    String httpsPort();
    String exampleDotCom();
    String shares();
    String sharedWith();
    String sharedWithUser();
    String sharedWithGroup();
    String office();
    String details();
    String ownerDetails();
    String call();
    String phone();
    String back();
    String preview();
    String close();
    String closeC();
    String help();
    String to();
    String from();
    String url();
    String generate();
    String checkCompromisedList();
    String testUrl();
    String sendEmail();
    String cancel();
    String is();
    String are();
    String threeSixtyFive();
    String in();
    String days();
    String grant();
    String https();
    String today();
    String yesterday();
    String expired();
    String time();
    String decode();
    String upload();
    String download();
    String language();
    String english();
    String bangla();

    String pageNotFound();
    String pageNotFoundMessage();
	String userGuide();
	String adminGuide();
	String changeLog();


    // Menu Help
    String adminMenuHelp();
    String usersMenuHelp();
    String settingsMenuHelp();
    String notesMenuHelpTitle();
    String notesMenuHelp();
    String itemsMenuHelpTitle();
    String itemsMenuHelp();
    String containersMenuHelpTitle();
    String containersMenuHelp();
    String templatesMenuHelpTitle();
    String templatesMenuHelp();
    String groupsMenuHelpTitle();
    String groupsMenuHelp();
    String historyMenuHelp();
    String auditMenuHelp();
    String auditLogAdminHelp();
    String containersSharedWithMeHelp();

    // Numbers
    String zero();
    String one();
    String two();
    String three();
    String four();
    String five();
    String six();
    String seven();
    String eight();
    String nine();

    String publicStr();
    String privateStr();
    String publicStrUpper();
    String privateStrUpper();

    // Menu
    String obidos();
    String enterPassphrase();
    String forgotPassphrase();

    // Notes menu
    String notes();
    String createNewNote();
    String listMyNotes();
    String listNotes();

    String searchNoteName();
    String noteName();
    String shareNote();
    String shareNoteWith();
    String shareNoteWithUsers();
    String shareNoteWithGroups();
    String editNote();
    String deleteNote();
    String viewNote();
    String noteSharedWithOthers();
    String viewMyNote();
    String viewMyItem();

    String copyTemplateHelp();
    String editTemplateHelp();
    String sharewithHelp();
    String shareContainerHelp();
    String notificationMessagesHelp();
    String viewItemHelp();

    String viewNoteSharedWithMe();
    String listMyNotesSharedWithOthers();
    String myNotesSharedWithOthers();
    String listNotesSharedWithMe();
    String itemsSharedWithSomeone();
    String sharedItemHasExpired ();
    String sharedItemHasExpiration();
    String listItemsInThisContainer();
    String noContainersFound();
    String saveNote();
    String createNote();
    String selectNoteType();
    String noteCreatedSuccessFully(String date);
    String noteUpdatedSuccessFully(String date);
    String couldNotSaveNote(String errorMessage);
    String updateNote();
    String revokeNote();
    String areYouSureToRevokeNote();
    String couldNoteRevokeNote(String errorMessage);
    String delNoteWarning();
    String couldNotDeleteNote(String errorMessage);
    String couldNotDeleteItems();
    String couldNotDeleteNotes();
    String noItemsSelected();
    String noTempaltesSelected();
    String noNotesSelected();
    String deleteSharedItem();
    String deleteSharedItemWarning(String owner);
    String deleteSharedNote();
    String deleteSharedNoteWarning(String ownerName);
    String deleteOneSharedNoteWarning();
    String deleteSharedNotesWarning(int n);
    String deleteOneSharedItemWarning();
    String deleteSharedItemsWarning(int n);
    String couldNotDeleteSharedNotes();
    String couldNotDeleteSharedNote();
    String noEditToolTip();
    String spenegoSuppliednoEditToolTip();
    String noDeleteToolTip();
    String noNotesFound();
    String couldNotRelinqushNote(String name, String errorMessage);
    String couldNotRelinqushItem(String name, String errorMessage);
    String couldNotRelinqushSomething(String type);
    
    // Audit Log
    String history();
    String actionHistory();
    String actionHistoryHelp();
    String searchFilters();
    String searchFilter();
	String searchUsernames();
	String searchUsernamesPlaceholder();
	String searchActions();
	String searchActionsPlaceholder();
	String searchObjects();
	String searchString();
	String searchObjectsPlaceholder();
	String dateRange();
	String startDate();
	String endDate();
	String searchDateFormat();
	String noLogsFound();
	String actionSearchLabelHtml();
	String invalidTimeValue();
	String invalildStartTimeValue();
	String invalidEndTimeValue();
	String invalidDate();
	String invalidStartDate();
	String invalidEndDate();
	String auditLogItem();
	String auditLogContainer();
	String auditLogGroup();
	String auditLogCreate();
	String auditLogCreateItem();
	String auditLogCreateContainer();
	String auditLogUpdate();
	String auditLogUpdateItem();
	String auditLogUpdateContainer();
	String auditLogDelete();
	String auditLogDeleteItem();
	String auditLogDeleteContainer();
	String auditLogDeleteGroup();
	String auditLogRevoke();
	String auditLogRevokeContainer();
	String auditLogRevokeItem();
	String auditLogShare();
	String auditLogShareContainer();
	String auditLogShareItem();
	String auditLogRelinquish();
	String auditLogRelinquishContainer();
	String auditLogRelinquishItem();
	String auditLogUser();
	String auditLogSystem();
	String auditLogLogin();
	String auditLogLockUser();
	String auditLogUnLockUser();
	String auditLogTombstoneUser();
	String couldNotTombstone(String usersOrAdmins, String errorMessage);
	String auditLogRestoreUser();
	String auditLogPasswordManagement();
    
    // Audit Report
    String auditReport();
    String auditLogs();
    String audit();
    String report();
    String reportPDF();
    String logs();
    
    // Activity Report
    String activityReport();
    String activityLogs();
    String activities();
    
    String utils();
    String algorithm();
    String diceware();
    String numberOfDiceRolls();
    String secureRandom();
    String traditional();

    // Admin menu
    String adminAccounts();
    String admins();
    String activeAdmins();
    String activeUsers();
    String createNewAdmin();
    String listAdmins();
    String userAccounts();
    String createNewUser();
    String lockUser();
    String unlockUser();
    String unlockAdmin();
    String unlockAdmins();
    String editUser();
    String editAdmin();
    String thisIsYou();
    String listUsers();
    String listMarksAsDeletedUsers();
    String listLoggedInUsers();
    String tombstonedUsers();
    String tombStoned();
    String tombstonedAdmins();
    String userIsLocked(String fullname);
    String userIsMarkedDeleted(String fullname);
    String markAsDeleted();
    String markAsLocked();
    String couldNotDetermineMarkedType();
    String restore();
    String restoreUser();
    String restoreAdmin();
    String userRestored(String fullname, String oldUsername, String newUsername);
    String userRestoredNoUsernameChange(String fullname, String username);
    String newUsername();
    String couldNotRestoreUser();
    String unlock();
    String unlockUsers();
    String updateAdministrators();
    String listAdministrators();
    String updateUser();
    String userUpdatedAt(String date);
    String updateAdmin();
    String listLockedUsers();
    String listLockedAdmins();
    String listDeletedUsers();
    String listDeletedAdmins();
    String settings();
    String editSettings();
    String editProfile();
    String advancedSearch();
    String reports();
    String advancedUserSearch();
    String editUserHelp();
    String couldNotDetermineLoggedInUser();
    String newUserHelp();
    String createADLDAPSettings();
    String createSMTPSettings();
    String listADLDAPSettings();
    String listADLDAPSettingsHelp();
    String listSMTPSettings();
    String deleteUser();
    String deleteTheUser(String username);
    String deleteAdmin();
    String deleteTheAdmin(String adminName);
    String passwordIsExternal();
    
    // User Details
    String userInfo();

    String emailNotificationTemplatePreview();

//  Admin Capabilities
    String capabilitiesAdmin();
    String adminsCapababilites();
    String canCreateUser();
    String canCreateAdmin();
    String canDeleteUser();
    String canDeleteAdmin();
    String canLockUser();
    String canLockAdmin();
    String canChangeUserCredentials();
    String canChangeAdminCredentials();
    String canModifyEmailTemplates();
    String canChangeSystemSettings();
    String couldNotDetermineAdminsCapabilites();
    String myCapabilities();
    String nocapToUnlockUsers();
    String nocapToUnlockAdmins();
    String capabilities();
    String capability();
    String noCapabilitySet();
    String enabled();
    String youAreARootAdmin();
    String youAreNotARootAdmin();
    String donotHaveLockCapability();
    String donotHaveDeleteCapability();
    String donotHaveDeleteOrLockCapability();
    String promoteToRootAdmin();
    
    String createUserHelp();
    String createAdminHelp();
    String editAdminHelp();
    String uploadProfilePicHelp();
    String listUsersHelp();
    String listLoggedInUsersHelp();

    String searchAdmins();
    String searchLockedUsers();

    // Admin Console
    String adminConsole();
    String manageAdminAccounts();
    String createAdminAccounts();
    String modifyAdminAccounts();
    String listAdminAccounts();

    String manageUserAccounts();
    String createUserAccounts();
    String modifyUserAccounts();
    String listUserAccounts();

    String manageSettings();
    String adLdapSettings();
    String smtpSettings();
    String generalSettings();
    
    // User Console
    String userConsole();
    String manageNotes();
    String createNotes();
    String modifyNotes();
    String viewNotes();
    String viewSharedNotes();
    String viewSharedNote();
    String shareNotes();
    
    String mangeContainers();
    String createShareableAndPrivateContainers();
    String modifyContainers();
    String createItemInContainers();
    String shareContainersItems();
    
    String manageGroupsAndTemplates();
    String createPersonalGroups();
    String personalGlobalTemplates();
    String listTemplatesGroups();

    String createAdminAccount();
    String adminUsername();
    String adminUsernameRequried();
    String adminFullname();
    String adminFullnameRequired();
    String adminPassword();
    String changeAdminPassword();
    String adminPasswordRequired();
    String primaryEmailAddress();
    String adminEmailRequried();
    String adminPrimaryPhone();
    String adminPhoreRequired();
    String pleaseSpecifyAdminUsername();
    String pleaseSpecifyFullname();
    String pleaseSpecifyPassword();
    String pleaseSpecifyPrimaryEmail();
    String pleaseSpecifyPrimaryPhone();
    String couldnotCreateAdmin();
    String adminAccountCreatedOn(String date);
    String noPermissionToModifyUsername();
    String noPermissionToModifyPassword();
    String noPermissionToLock();
   	String noPermissionToDelete();
    // User Settings
    String mySettings();
    String selectProfilePic();
    String allowShareNotication();
    String acceptNotification();
    String acceptSmsNotification();
    String sendNotificationSMS();
    String smsPhoneNumberError();
    String acceptSMSNotification();
    String acceptShareNotification();
    String acceptRevokeNotification();
    String pageToViewAfterLogin();
    String hideHelpButton();
    String uploadProfilePic();
    String uploadNewProfilePic();
    String imageFilters();
    String applyToRegion();
    String annotate();
    String annotateText();
    String grayFilter();
    String sepiaFilter();
    String negativeFilter();
    String solarizeFilter();
    String noiseFilter();
    String brightenFilter();
    String darkenFilter();
    String downloadImage();
    String downloadRegion();
    String histogramImage();
    String redChannel();
    String greenChannel();
    String blueChannel();
    String revertFilters();
    String deleteProfilePic();
    String crop();
    String uploadImage();
    String annotateWarning();
    String imageNotSaved();
    String confirmProfilePicUpload();
    
    // Password expired 
    String passwordHasExpired();
    String passwordChangeRequired();
    
    // System Settings
    String systemSettings();
    String twoFAIssuer();
    String twoFAAuthenticator();
    String show2FACode();
    String showQRCodeImage();
    String add2FAInfo();
    String addAttachment();
    String chooseFile();
    String noFileChosen();
    String totoURI();
    String enter2FAQRCodeInfoManually();
    String updatedSuccessfully(String date);
    String specifyHttpsFqdn();
    String specifyHttpsPort();
    String specify2FAIssuer();
    String couldNotUpdateSystemSettings(String exeption);
    String formResetSuccessfully();
    String couldNotRetrieveFormValues(String exception);
    String systemSettingsHelp();
    String passwordExpires();
    String passwordWillExpireIn();
    String passwordAgeInDays();
    String passwordExpires2();
    String passwordExpired();
    String passwordLengthWarning(int minLength);
    String acceptablePassLengthWarning(int minLength);
    String passwordEntropyLengthWarning(int minLength);
    String passwordExpiredDays(int days);

    // File upload
    String selectFile();
    String fileUploadServlet();
    String uploadFile();
	String uploadingFile();
	String filename();
	String filesize();
	String bytesUploaded();
	String averageSpeed();
	String speed();
	String secondsElapsed();
	String timeElapsed();
	String timeRemaining();

	String secondsRemaining(); 
	String cancelUpload();

    String uploadProgressTitle();
    String uploadProgressMessage();
    String cancelFileUploadWarning();
    // QR Code image upload
    String selectQRCodeImageFile();

    // License
    String notSupportedByLicense();
    String notSupportedByLicenseMessage();
    String aboutLicense();
    String license();
    String installLicense();
    String installObidosCertificate();
    String installAdLdapCertificate();
	String viewObidosCertificate();
    String viewAdLdapCertificate();
	String noAdLdapCertificateInstalled();
	String noObidosCertificateInstalled();

    String certificates();
    String viewCertificates();
    String decodeCertificates();
    String privateKey();
    String pasteCertificates();
    String pastePrivateKey();
    String certificateHelp();
    String certificateStaged();
    String adLdapCertificateHelp();
    String invalidBase64Pasted();
    String invalidObidosLicense();
    String currentLicense();
    String displayLicense();
    String pasteLicense();
    String  pasteLicensePlaceholder();
    String invalidBase64(String errorMessage);
    String installLicenseHelp();
    String licenseInstalledSuccessfully(String date);
    String failedToInstallLicense(String errorMessage);
    String companyName();
    String customerId();
    String companyEmail();
    String companyPhone();
    String maxUsers();
    String licenseTerms();
    String yourLicense();
	String yourLicenseOpensource();
	String yourLicenseCommunity();
	String yourLicenseStandard();
	String yourLicenseEnterprise(); 
	String yourLicenseSapphireDev();
    String expiresOn();
    String signedOn();
    String expiredOn();
    String agreeCheckBox();
    String expiresIn();
    String expiresInDays(int days);
    String licenseIsInvalid();
    String licenseWillExpireInOneMonth();
    String licenseGracePeriodFirstMonthAfterExpiry();
    String licenseGracePeriodSecondMonthAfterExpiry();
    String licenseBeyondGracePeriodsAfterExpiry();
    String licenseHasExpiredAndBeyondGracePeriod();
    String supportEmail();

    // Items
    String item();
    String createdAndAdded();
    String couldNotAddItem();
    String items();
    String itemName();
    String shareItem();
    String revokeSharingItemFromUsers();
    String revokeSharingItemFromGroups();
    String revokeFromUsers();
    String revokeFromGroups();
    String revokeSharingNote();
    String shareItemWith();
    String createNewItem();
    String createNewItemHelp();
    String listMyItemsSharedWithOthers();
    String listItemsSharedWithMe();
    String itemsSharedWithMeHelp();
    String shareItemWithUsers();
    String selectUsersAndClickToShare();
    String selectGroupsAndClickToShare();
    String shareItemWithGroups();
    String listMyItems();
    String listItemsInContainer();
    String listUsersContainerIsSharedWith();
    String listUsersItemIsSharedWith();
    String listUsersGroupsNoteIsSharedWith();
    String listUsersNoteIsSharedWith();
    String itemIsNotSharedWithAnyone();
    String noteIsNotSharedWithAnyone();
    String containerIsNotSharedWithAnyone();
    String itemsInSharedContainer();
    String shareItemWithGroup();
    String listItems();
    String listLabel();
    String revokeSharingAItemFromUsers();
	String revokeSharingAItemFromGroups();
	String revokeSharingANoteFromUsers();
	String revokeSharingANoteFromGroups();
	String newItem();
	String couldNotFetchItems(String errorMessage);
	String couldNotFetchContainers(String errorMessage);
	String couldNotFetchTemplates(String errorMessage);
	String couldNotFetchGroups(String errorMessage);
	String couldNotFetchHistory(String errorMessage);
	String couldNotFetchNotes(String errorMessage);
	String couldNotFetchAuditLogs(String errorMessage);
	String itemUpdatedSuccessfully(String date);
	String xUpdatedSuccessfully(String what, String date);
	String couldNotUpdateItem(String errorMessage);
	String couldNotUpdateItemNoMessage();
	String couldNotRetrieveUser();
	String containerIsEmpty();
	
	// tooltips
    String clickToSeeUserInfo();
    String clickToSendEmail();
    String clickToListUsers();
    String clickToSeeOwnerInfo();

    @DefaultMessage("Could not create template")
    String couldNotCreateTemplate();

    @DefaultMessage("Object is not unique XX")
    String objectIsNotUnique();

    // List Containers
    String searchContainerName();
    String listOfUsersContainerIsSharedWith();
	String shareAContainer();
	String addItemToAContainer();
	String addItemToContainer();
	String editAContainer();
	String deleteAContainer();
	String revokeSharingAContainerFromUsers();
	String revokeSharingAContainerFromGroups();
	String viewItems();
	String viewSharedItems();
	String viewSharedItem();
	String shareContainer();
	String unshareContainer();
	String addItem();
	String addButtonTitle();
	String editContainer();
	String searchToListUsers();
	String searchToListGroups();
	String notSharedWithUsers();
    String notSharedWithGroups();
    String notShared();
    String onlySharedContainerCanBeRevoked();
    String onlySharedNoteCanBeRevoked();
    String listContainersHelp();
    String createContainerHelp();
    String createNewContainer();
    
    // Select a Container
    String selectAContainer();
    String selectAContainerHelp();

	// Share Containers
	String shareContainerWithUsers();
	String shareContainerWithUsersHelp();
	String shareContainerWithGroups();
	String shareContainerWithGroupsHelp();
	String shareWithUsers();
	String sendNotificationEmail();
	String shareWithUsersHelp();
	String listMyContainers();
	String listMyContainersSharedWithOthers();
	String listItemsInContainerSharedWithOthers();
	String sharingCommentPlaceHolder();
	String searchUsersToShare();
	String searchUsersToSharePlaceHolder();
	String searchGroupsToSharePlaceHolder();
	String searchUsersPlaceHolder();
	String searchGroupsToShare();
	String couldNotFindGroupsToShare();

	// Revoke Shared Container
	String revokeContainer();
	String revokeContainerFromUsersHelp();
	String revokeContainerFromGroups();
	String revokeContainerFromGroupsHelp();
	String revokeContainerPlaceHolder();
	String notifyUser();
	String notifyUsers();
	String commentLabel();

    String pleaseRegister();
    String specifyPassphrase();
    String specifyCurrentPassword();
	String registerPassphraseHelpHtml();
	String couldnotChangePassword();
	String passwordChanged(String date);
	String registerPassphrasePanelHeading();
	String registerPassphraseSubmitButton();
	String registerPassphraseLaterButton();
	String registerPassphraseLabel();
	String registerPassphraseTextPlaceHolder();
	String invalidPassphrase();
	// dialog
	String registerPassphraseDialogTitle();
	String registerPassphraseDialogMessage(String action);
	String registerPassphraseCancelButtonTitle();
	String registerPassphraseGoToRegisterPageButtonTitle();


	// Pick Item Type
	String selectItemType();
	String selectItemTypeTemplate();
	String selectItemTypeTemplateHelp();

	// Add Item To Container
	String createAndAddItemToAContainer();
	String containerNameLabel();
	String containerNamePlaceHolder();
	String specifyContainerName();
	String selectContainerType();
	String couldNotCreateContainer();
	String couldNotCreateUser(String emsg);
	String couldNotUpdateContainer();
	String containerTypeLabel();
	String containerStatus();
	String templateNameLabel();
	String newTemplateNameLabel();
	String itemNameLabel();
	String noteNameLabel();
	String noteNameLabelHtml();
	String noteHtml();
	String sharedByLabel();
	String deleteContainer();
	String thisCannotbeUndone();
	String deleteContainerWarning();
	String relinquishContainer();
	String relinquishContainerWarning(String containerName);
	String relinquishItemWarning(String itemName);
	String relinquishNoteWarning (String noteName);
	String revokeSharingFrom();
	String whatIsAContainer();
	String whatIsAContainerHelp();
	String newContainer();
	String newContainerPlaceHolder();
	String shareableContainer();
	String privateContainer();
	String sharedContainer();
	String sharedContainerNoLongerAvailable(String containerName);
	String sharedItemNoLongerAvailable(String itemName);
	String privateContainersCannotbeShared();
	String createContainer();
	String listContainers();
	String myContainers();
	String couldNotFindAnyContainersSharedWithMe();

	String addItemToPublicContainerHelp();
	String addItemToPrivateContainerHelp();
	String addItemToContainerButton();
	String addNoteToContainerButton();
	String itemTypeWarning();
	String noteTypeWarning();
	String specifyItemName();
	String specifyNoteName();
	String shareableItem();
	String privateItem();
	String privateItemCannotBeShared();
	String privateNoteCannotBeShared();
	String onlySharedItemCanBeRevoked();
	String shareableNote();
	String privateNote();
	String notifyRecipient();
	String notifyRecipients();
	String shareable();
	String privateX();
	// https://beautifuldingbats.com/punctuation/whitespace
	// using it to align with Shareable above
	String privateXWithInvisibleSapces();

	// Edit Item
	String note();
	String editItem();
	String editSharedItem();
	String editSharedNoted();
	String editItemPanelHeading();
	String viewtemPanelHeading();
	String viewItemSharedWithMe();
	String viewItemSharedWithOthers();
	String viewNoteSharedWithOthers();
	String saveItemButtonTitle();
	String listItemsButtonTitle();
	String couldNotGetItemId();
	String formChagned();
	String confirmSaveForm();
	String editItemHelp();
	String takeOwnership();
	String ownContainer();
	String addModifyItem();
	String ownershipOfContainerTaken(final String containerName);
	String takeOwnershipHelp();
	String takeOwnershipDialogMessage();
	String takeOwnershipOfItemDialogMessage(String containerName);
	String takeOwnershipOfContainer();
	String takeOwnershipOfContainerDialogMessage(String onwerFullname);
	String ownershipOfItemAquired();
	String couldNotTakeOwnershipOfItem();
	String publicToPrivateEditAlreadySharedWarning(String type, String name);
	String publicToPrivateEditNotSharedWarning(String type, String type2, String name);
	String selectContainerBeforeTakingOwnership();

	// List All My Items List of Items
	String searchItemName();
	String listOfUsersItemIsSharedWith();
	String listOfUsersItemIsSharedWithHelp();
	String listOfUsersNoteIsSharedWith();
	String listOfItemsInContainer();
	String containerHasNoItems();
	String shareAnItem();
	String editAnItem();
	String deleteAnItem();
	String viewAnItem();


	// List Items in Shared Container
	String listItemsInSharedContainer();
	String listContainersSharedWithMe();
	String noPermissionToTakeOwnership();
	String relinquishItem();
	String relinquishNote();
	String relinquishLabel();

	// List Users Item is Shared with
	String itemSharedWithUsers();
	String itemSharedWithGroups();
	String noteSharedWithUsers();
	String noteSharedWithGroups();
	String containerSharedWithUsers();
	String containerSharedWithGroups();
	String unshareThisItem();
	String selectLabel();
	String selectClearLabel();

	// List TemplateeditTemplate = Edit Template
	String templates();
	String template();
	String editTemplate();
	String deleteTemplate();
	String viewTemplate();
	String personalTemplate();
	String copyPersonalTemplate();
	String copyGlobalTemplate();
	String selectPersonalTemplate();
	String selectTemplate();
	String selectGlobalTemplate();
	String createNewPersonalTemplate();
	String createNewGlobalTemplate();
	String couldNotDeleteTemplate();
	String listPersonalTemplates();
	String listGlobalTemplates();
	String saveTemplate();
	String savePersonalTemplate();
	String saveGlobalTemplate();
	String nameOfPersonalTemplate();
	String nameOfGlobalemplate();
	String nameOfField();
	String searchTemplateName();
	String searchPersonalTemplateName();
	String createPersonalTemplate();
	String personalTemplateCreatedSuccessFully(String name);
	String createGlobalTemplate();
	String globalTemplateCreatedSuccessFully(String name);
	String createPersonalTemplateHelp();
	String addTemplateFieldMessage();
	String createGlobalTemplateHelp();
	String listPersonalTemplatesHelp();
	String listGlobalTemplatesHelp();
	String deletePersonalTemplate();
	String deletePersonalTemplateWarning(String templateName);
	String deleteGlobalTemplate();
	String deleteGlobalTemplateWarning(String tempalteName);
	String canAttachDocument();
	String canAddTwoFAInfo();
	String documentDirectory();
	String replaceDocument();
	String downloadDocument();
	String fileUploadStatus();
	String fileUploadInProgress();
	String fileUploadCancelled();
	String downloadHelpMessage();
	String encryptedFileToolTip();
	String clickToDownload();
	
	String createNewGroup();
	String groupNamePlaceHolder();
	String createNewGroupHelp();
	String updateGroup();

	// Global Templates
	String globalTemplate();
	String copyToPersonalTemplate();
	String editGlobalTemplate();
	String viewGlobalTemplate();
	String globalTemplateHelpHeading();
	String globalTemplateHelpMessage();
	String searchGlobalTemplateName();

	String positionMismatchError();

	// Edit Template
	String nameOfTemplate();
	String editPersonalTemplate();
	String viewPersonalTemplate();
	String templateFieldNames();
	String displayOrder();
	String displayOrderPlaceHolder();
	String displayOrderHTML();
	String fieldName();
	String fieldvalue();
	String listTemplates();
	String templateUpdatedSucessFully(String templateName);
	String couldnotUpdateTemplate();
	String noDisplayOrderSpecified();
	String templateFieldDeleted();
	String personalTemplateFieldRemovedWarning(final int nFieldsDeleted);

	// Delete Template
	String deleteTemplateWarningMessage();

	String deleteItem();
	String deleteItemWarningMessage();
	// Note Shared with me
	String unshareNote();
	String removeNote();
	String relinquishNoteSharedWithMe();
	String relinquishSharedNote();
	String createNoteHelp();
	String listNotesHelp();
	String notesSharedWithMeHelp();

	// Item Shared with me
	String itemSharedWithMe();
	String itemsSharedWithMe();
	String removeItem();
	String revokeItem();
	String revokeContainerSharingUserGroup();
	String revokeItemSharingUserGroup();
	String revokeItemsWarning();
	String revokeItemFromUsersHelp();
	String revokeItemFromUsersGroupHelp();
	String revokeItemSharedWithGroups();
	String revokeNoteSharedWithGroups();
	String revokeItemSharedWithGroupsHelp();
	String viewItem();
	String removeSharedItemPropmptMessage(String ownerFullname);
	String couldNotRemoveItem();
	String listItemsHelp();
	String listItemsAddHelp();
	String viewNoteHelp();

	String hideItemTimer();
	String refreshTimer();
	String profilePicPreviewArea();
	String profilePicture();
	String willHideAt(String date);
	String hideViewItemInSeconds();
	String editSettingsHelp();
	String fullnameCannotBeDeleted();
	String profileImageSizeWarning(int maxSize,int selectedSize, String dimension, String dimension2);


	// AD/LDAP Settings
	String ldapSettingHelp();
	String ldapConfiguration();
	String ldapPanelHeading();
	String ldapConfigurationName();
	String ldapConfigurationNameHtml();
	String ldapConfigurationNamePlaceHolder();
	String ldapUri();
	String enableStartTLS();
	String ldapUriHtml();
	String testingLdapConnection();
	String testingLdapAuthentication();
	String ldapConnectionSuccessful();
	String ldapAuthenticationSuccessful();
	String ldapUriPlaceHolder();
	String baseDn();
	String baseDnHtml();
	String baseDnPlaceHolder();
	String bindDn();
	String bindPassword();
	String authAttribute();
	String authAttributeHtml();
	String testConnection();
	String testAuthentication();
	String testConnectionAuthentication();
	String specifyAUsername();
	String specifyAPassword();


	// PASSWORD_CHANGED view
	String passwordChangedPanelHeading();
	String passwordResetPanelHeading();
	String passwordChangedMessage();
	String passwordChangedButtonTitle();
	String keyPairGeneratedMessage();
	String keyPairGenerated();

	// Passphrase caching in session
	String couldNoteCachePassphrase();
	String passphraseCached();

	// KEY_PAIR view
	String keyPairPanelHeading();
	String keyPairMessage();
	String keyPairHeading();
	String keyPairSuggestionsForStringPassphrase();
	String keyPairStrongPassphraseHelp1();
	String keyPairStrongPassphraseHelp2();
	String keyPairStrongPassphraseHelp3();
	String keyPairStrongPassphraseHelp4();
	String keyPairEnterPassphraseLabel();
	String keyPairPssphraseStrengthLabel();
	String keyPairPlaceHolder();
	String keyPairConfirmPassphraseLabel();
	String keyPairConfirmPassphrasePlaceHolder();
	String keyPairGenerateAndSaveButtonTitle();
	String keyPairHelpGenerateButtonTitle();
	String keyPairGeneratePassphraseButtonLabel();
	String keyPairLanguageLabel();
	String keyPairLanguageEnglish();
	String keyPairLanguageGerman();
	String keyPairNumberOfWordsLabel();
	String keyPairNumbeOfOnes6();
	String keyPairNumbeOfOnes7();
	String keyPairNumbeOfOnes8();
	String keyPairNumbeOfOnes9();
	String keyPairNumbeOfOnes10();
	String keyPairUpperChaseWords();
	String keyPairUpperNoSpaces();
	String keyPairAddSpecialChar();
	String keyPairHelpGenerateStrongPassphraseButtonTitle();
	String keyPairPanelFooterTitle();
	String passphraseEmptyWarning();
	String confirmPassphraseWarning();
	String passphraseMismatchWarning();
	String errorCreatingKeyPair();
	String keyPairCreatedSuccessfully();
	String keyPairReCreatedSuccessfully();
	String keyPairNotCreated();
	String createKeypairFirst();
	String generatePassword();
	String generatePasswordPassphrase();
	String passwordPassphrase();

	// Password reset view
	String forgotLoginNameText();
	String forgotPasswordText();
	String resetPasswordPleaseEnterYourEmail();
	String resetPasswordPanelHeading();
	String resetPasswordEmailAddressLabel();
	String resetPasswordEmailAddressPlaceHolder();
	String resetPasswordButtonTitle();
	String resetPasswordHeading();
	String resetPasswordWarning();
	String resetPasswordError();
	String resetPasswordAnEmailIsSent();
	String resetPasswordTooManyRequestsWarning();
	String resetPasswordLoginWithNewPassword();
	String resetPasswordConfirmPassword();
	String sendResetPasswordInstructions();

	String resetPasswordsuggestionForStringPassword();
	String resetPasswordStrongPasswordHelp1();
	String resetPasswordStrongPasswordHelp2();
	String resetPasswordStrongPasswordHelp3();
	String resetPasswordStrongPasswordHelp4();
	String resetPasswordStrongPasswordHelp5();
	
	String changePassphraseHelp1();
	String changePassphraseHel2();
	String changePassphraseHelp3();
	String changePassphraseHelp4();
	String changePassphraseHelp();

	// Reset Password view
	String specifyPassword();
	String specifyUsersPrimaryEmailAddress();
	String confirmPassword();
	String passwordMismatch();
	String specifyANewPassword();
	String couldNotFindResetToken();
	String passwordResetSuccessful();
	String couldNotResetPassword();
	String enter2FaVerificationCode();
	// password complexity
	String passwordStrengthN(String label, String s);
    String minimumPasswordLengthN(String label, int n);
    String minimumPasswordUppercaseN(String label, int n);
    String minimumPasswordLowercaseN(String label, int n);
    String minimumPasswordSpecialN(String label, int n);
    String minimumPasswordNumbersN(String label, int n);
    String minimumPasswordEntropyN(String label, int n);
    String maxPasswordAgeDaysN(String label, int n);
    String passwordRowDouble(String label1, String value1, String label2, String value2);
    // passphrase complexity
	String passphraseStrengthN(String label, String s);
    String minimumPassphraseLengthN(String label, int n);
    String minimumPassphraseUppercaseN(String label, int n);
    String minimumPassphraseLowercaseN(String label, int n);
    String minimumPassphraseSpecialN(String label, int n);
    String minimumPassphraseNumbersN(String label, int n);
    String minimumPassphraseEntropyN(String label, int n);


	// Email Notification
	String accountCreatedSubject();
	String passwordResetRequestSubject();

	// Change password
	String changePassword();
	String changeMyPassword();
	String passwordRequirementsLabel();
	String passwordRequirementHelp1();
	String generatePasswordHelp();
	String changePasswordHelp();
	String resetPasswordHelp();
	String checkWithHaveIBeenPwned();
	String specifyNewPassword();

	String currentPassphrase();
	String newPassphrase();
	String passphraseStrength();
	String confirmPassphrase();
	String changePassphrase();
	String buildInformation();
	String sessionInformation();
	String logout();
	String passphraseResetInstructionWillbeSent();
	String passphraseRequirementsLabel();
	String couldNotSendPassphraseResetInstructions(String errorMessage);
	
	// Reset Passphrase
	String resetPassphrase();
	String sendMeResetPassphraseInstructions();
	String resetPassphraseHelp();
	
	// Advanced User Search View
    String listOfUsers();
    String searchUsers();
    String searchGroups();
    String username();
    String groupname();
    String specifyGroupName();
    String removeUserFromGroup();
    String createGroup();
    String groupCreatedSuccessFully(String name);
    String myGroup();
    String listGroups();
    String listGroupsHelp();
    String listOfGroups();
    String groupNameLabelHtml();
    String groupnameRequired();
    String searchByUsername();
    String fullname();
    String searchByFullname();
    String primaryEmail();
    String onlyAdminCanChangePrimaryEmail();
    String onlyAdminCanChangeFullname();
    String secondaryEmail();
    String mobilePhone();
    String mobilePhoneSimple();
    String mobilePhonePlaceHolder();
    String mobilePhoneSMS();
    String mobilePhoneWithoutCountryCode();
    String searchByPrimaryEmail();
    String primaryPhone();
    String facebook();
    String twitter();
    String authenticationSourceLabel();
    String deletedUser();
    String countryCode();
    String removeUsersFromGroup();
    String removeUsersFromGroupWarning(String groupName);

    // New User Account
    String usernameLabelHtml();
    String usernameTextBoxPlaceHolder();
    String fullnameLabelHtml();
    String fullnameTextBoxPlaceHolder();
    String authenticationSourceLabelHtml();
    String passwordLabelHtml();
    String passwordTextBoxPlaceHolder();
    String primaryEmailLabelHtml();
    String primaryEmailTextBoxPlaceHolder();
    String primaryPhoneLabelHtml();
    String primaryPhoneTextBoxPlaceHolder();
    String sendUserCreationEmailLabel();
    String sendAdminCreationEmailLabel();
    String userMustChangePassword();
    String mustChangePassword(String useType);
    String adminMustChangePassword();
    String createUserAccount();
    String newUserAccount();
    String newAdminAccount();
    String specifyUsername();
    String specifyFullname();
    String emailComment();
    String twoFactorForPasswordReset();
    String twoFactorForPassphraseReset();
    String twoFactorForLogin();
    String canManageGlobalTemplates();
    String resetTtwoFactorLabel();
    String twoFAEnabledLabel();
    String youEnabled2FALabel();
    String requires2FA();
    String getVerificationCode();
    String enterValidationCodeFromGoogleAuthenticator();
    String createAsRootAdmin();
    String allCapabilitiesAreAvailable();
    String daysUntilPasswordExpiration();
    String maxPasswordAgeInDays();
    String noUserCanbeCreatedLicenseHasExpired(String date);

    // Forgot Passphrase view
    String forgotPassphraseRegenerateKeyPair();
    String forgotPassphraseMessage();
    String pleaseEnterCurrentPassword();
   	String pleaseEnter2FACode();

    // Copy Template
    String templateId();
    String couldNoteGetTemplateIdFromUrl();
    String specifyTemplateName();
    String specifyNewTemplateName();
	String templateNamesAreTheSame();
    String couldNotFetchTemplate();
    String failedToCopyTemplate();
    String copyPersonalTemplateHelp();
    String copyGlobalTemplateToPersonalTemplateHelp();
    String couldNoteGetTemplateTypeFromURL();
    String couldNoteGetTemplateIdFromURL();
    String noTemplateNameSpecified();
    String duplicateDisplayOrder(int n);
    String specifyDisplayOrderAtRow(int rowNum);
    String templateSavedSuccessfully(String type, String name, String date);

    // Pick Notification Template
    String whatIsANotificationTemplate();
    String pickNotificationTemplate();
    String selectANotificationTemplate();
    String accountCreatedTemplate();
    String itemSharedTemplate();
    String passwordResetTemplate();
    String passwordResetWarning();
    String gotoEditNotificationTemplate();
    String editAccountCreatedTemplate();
    String editItemSharedTemplate();
    String editPasswordResetTemplate();
    String editPasswordResetWarning();
    String editPassphraseResetTemplate();
    String editPassphraseResetWarning();
    String editContainerSharedTemplate();
    String editContainerRevokedTemplate();
    String editItemRevokedTemplate();
    String sendTestEmailWithTemplate();

    // Edit Notification Templates
	String editNotificationTemplate();
    String editAccountCreatedNotificationTemplatePanelHeader();
    String editItemSharedNotificationTemplatePanelHeader();
    String editItemRevokedNotificationTemplatePanelHeader();
    String editContainerSharedNotificationTemplatePanelHeader();
    String editContainerRevokedNotificationTemplatePanelHeader();
    String editPasswordResetNotificationTemplatePanelHeader();
    String editPasswordResetWarningNotificationTemplatePanelHeader();
    String editAccountCreatedNotificationTemplateHelp();
    String editPassphraseResetNotificationTemplatePanelHeader();
    String editPassphraseResetWarningNotificationTemplatePanelHeader();
    String subject();
    String hello();
    String helloPlaceHolder();
    String htmlMessage();
    String warningMessage();
    String buttonTitle();
    String helpLabel();
    String textMessage();
    String contact();
    String footer();
    String header();
    String defaultTemplate();
    String defaultAlg();
    String pickTemplateToEdit();
    String couldNotGetNotificationTemplateType();
    String unknownNotificatoinTemplateType();
    String unknown();
    String notificationTestMailHelp();

    String testAccountCreatedSubject();
    String testItemSharedSubject();
    String testItemRevokedSubject();
    String testContainerSharedSubject();
    String testContainerRevokedSubject();
    String testPasswordResetRequestSubject();
    String testPassphraseResetRequestSubject();
    
    
    // 2FA
    String twoFactorAuthentication();
    String twoFactorAuthenticator();
    String twoFactorResetHelp();
    String twoFactorAlreadyConfigured();
    String configure2FA();
    String enable2FA();
    String reConfigure2FA();
    String qrCodeLabel();
    String secretLabel();
    String willbeCreatedPlaceHolder();
    String twoFactorAuthenticationHelp();
    String enable2FAGoogleAuthentication();
    String configure2FAButtonTitle();
    String codeFromGoogleAuthenticator();
    String enable2FAButtonTitle();
    String badge1Text();
    String badge2Text();
    String badge3Text();
    String badge1Body();
    String badge2Body();
    String badge3Body();
    String useGoogleAuthenticator();
    String enterVaidationCodeLabel();
    String specifyCodeError();
    String twoFactorAutenticationIsEnabled();
    String couldNotEnabletwoFactorAutentication();
    String twoFactorReConfigureHelp();
    String twoFactorNotEnabledWarning();
    String twoFactorNotEnabledAdmniWarning();
    String twoFactorRequiredButNotEnabledWarning();
    String enable2FANow();
    String enable2FALater();
    String twoFactorModalHelp();
    String warning();
    // generate 2fA uri in adhoc template
    String twoFAissuer();
    String twoFAAccount();
    String twoFASecret();

    String createdAt();
    String updatedAt();

    // Session
    String loggedOutMsg();
    String loggedOutAt(String date);
    String idleLogoutWarningTitle();
    String idleLogoutWarningMessage();
    String idleLogoutMessage();
    String keepMeLoggedInButtonTitle();

    // Add Users to Group
    String group();
    String groups();
    String addUsersToGroup();
    String userExitsInGroup();
    String couldNotAddUserToGroup(String errorMessage);
    String usersAddedToGroup(int n, String group);
    String addUsersToGroupHelp();
    String addUsers();
    String editGroup();
    String deleteGroup();
    String deleteGroups(String groups);
    String deleteX(String s);
    String listUsersInGroup();
    String viewUserInfo();
    String listOfUsersInGroup();
    String listOfUsersInGroupHelp();
    String searchUsersToAddToGroup();
    String areYouSureToDeleteTheGroup();
    String deleteGroupsWarning(int nGroup, String groups, String groups2);
    String deleteSomethingWarning(int nX, String x, String x2, String x3);
    String deleteTemplatesWarning (int nX,  String x, String x2, String x3);
    String deleteContainersWarning(int n, String s, String s2, String s3);
    String relinquishContainersWarning(int n, String s, String s2, String s3);
    String couldNotDeleteGroups(String groups, String errorMessage);
    String couldNotDelete(String s, String errorMessage);
    String relinquishSomethingWarning(int n, String s, String s2, String s3, String s4);
    String deleteUsersWarning(int n, String s1, String s2, String s3, String s4, String s5);
    String lockUsersWarning(int n, String s1, String s2, String s3, String s4, String s5);
    String lockAdminsWarning(int n, String s1, String s2, String s3);
    String deleteAdminsWarning(int n, String s1, String s2, String s3);

    // Passphrase Cache Status icon on navbar
    String passphraseIsInServerCache();
    String passphraseIsNotInIServerCache();

    //Pick Revoke Sharing
    String revokeSharing();
    String revokeSharingItem();
    String revokeSharingContainer();
	String revokeContainerSharing();
	String revokeItemSharing();
	String revokeNoteSharing();
	String pickRevokeContainerSharing();
	String pickRevokeSharingHelp();
	String revokeSharingFromUsers();
	String revokeSharingFromGroups();
	String gotoRevokeContainerSharingFromUsersPage();
	String gotoRevokeContainerSharingFromGroups();
	String revokeContainerSharingHelp();
	String couldNotGetActionFromUrl();
	String couldNotGetContainerIdFromUrl();
    String revokeSomethingWarning(int n, String itemOrNote, String itemOrNote2, 
    		String usersGroups, String itemOrNote3, 
    		String isAre, String itemNote4);

	// SMTP Settings
	String updateSMTPSettings();
	String smtpServer();
	String smtpServerPlaceHolder();
	String smtpConnection();
	String startTLS();
	String ssl();
	String notSecure();
	String smtpPort();
	String smtpPortPlaceHolder();
	String smtpAuthUser();
	String smtpAuthPassword();
	String smtpAuthUserRequired();
	String smtpAuthPasswordRequired();
	String testSMTPSettings();
	String fromAddress();
	String fromAddressPlaceHolder();
	String toAddress();
	String toAddressPlaceHolder();
	String message();
	String updateSMTPConfiguration();
	String sendTestMail();
	String specifyFromAddress();
	String specifyToAddress();
	String specifySubject();
	String specifyMessage();
	String sendingEmail();
	String testMailSent(String date);
	String testSubject();
	String testMessage();
	String couldnotSendTestMail();
	String smtpSettingsHelp();
	String smtpConfigurationUpdated();
	String couldNotUpdateSmtpConfiguration();
	String smtpConfigurationSaved();
	String couldNotSaveSmtpConfiguration();
	String smtpSettingsDeleteWarning(String serverName);
	String smtpSettingsDeleteWarningSimple();
	String shareWithGroups();
	String shareWithGroupsHelp();
	String containerSharedWithOthers();

	// Pick Template Type
	String pickAnItemType();
	String pickItemTypeHelp();
	String asNote();
	String useGlobalTemplate();
	String usePersonalTemplate();
	String freeFormat();
	String adHoc();
	String gotoAddItemToContainerPage();

	// Ad-Hoc
	String adhocItem();
	String itemLabels();
	String itemValues();
	String adhocItemHelp();

	String permissions();
	String currentPermissions();
	String grantPermissions();
	String grantPermissionsOnItem();
	String grantPermissionOnContainer();
	String grantPermissionsToUsers();
	String grantPermissionOnContainerHelp();
	String grantPermissionsToUsersHelp();
	String grantPermissionsToGroups();
	String mayUpdate();
	String mayUpdateOwn();
	String mayDelete();
	String mayTakeOwnershipControl();
	String couldNotFindAnyUsers();
	String selectToGrantPermissions();

	// Items
	String setExpiration();
	String expiration();
	String setExpirationPlaceHolder();
	String daysUntilShareExpires();
	String expirationDays();
	String expirationInDays();
	String shareExpirationDate();
	String shareExpirationDateOptional();
	String dateFormat();
	String dateFormatLabel();
	String expirationDateinPast();
	String expirationDateMustBeTomorrow();
	String expirationDate();
	String dateFormGWT();
	String dateFormatmmddyyyy();
	String dateFormatddmmyyyy();
	String dateFormatyyyymmdd();
	
	// Notification Messages
	String notificationMessages();
	String notificationMessage();
	String newNotificationMessages();
	String notifications();
	String markRead();
	String markUnread();
	String viewNotificationMessages();
	String listNotificationMessages();
	String containerName();
	String containerCreated(String date);
	String containerNameUpdated(String date);
	String itemIsNoLongerAvailable(String name);
	String containerIsNoLongerAvailable(String name);
	String ownerHasRevokedTheItem();
	String ownerHasRevokedTheContainer();
	String userHasRelinquishedTheContainer(String containerName);
	String userHasDeletedItem(String itemName);
	String userHasRelinquishedTheItem(String itemName);
	String notificationTooltip(String date);
	String showUnreadMessages();
	String showReadMessages();
	String unreadMessages();
	String readMessages();
	String unreadMessage();
	String readMessage();
	String showUnreadMessageCount(int n);
	String showReadMessageCount(int n);
	String selectUnreadMessages();
	String selectReadMessages();
	String selectMessages();
	String selectedRows(int n);
	String readMessageSelected(int n);
	String unreadMessageSelected(int n);
	String messageSelected(int n);
	String logSelected(int n);
	String rowSelected(int n, String type);
	String couldNotFetchAnyUnreadMessage();
	String couldNotFetchAnyReadMessage();
	String couldNotMarkMessageAsRead(String errorMessage);
	String couldNotMarkMessageAsUnread(String errorMessage);
	String youGotANotification();
	String messageType();
	String deleteNotificationMessage();
	String markAsReadAfterView();
	
	String notificationTemplateUpdatedAt(String date);
	String couldNotUpdateNotificationTemplate(String errorMessage);

	// Change initial password
	String changeInitialPasswordHelp();
	String changeInitialPassword();
	String currentPassword();
	String confirmNewPassword();
	
	
	// Session
	String sessionInfo();
	String sessionId();
	String sessionCreatedAt();
	String sessionLastAccessed();
	String sessionExpiresAt();
	String sessionDialogPopsAround();
	String sessionTimeout();
	String sessionTimerRunning();
	String sessionGraceTimerRunning();
	String sessionUpdatedAt();
	String sessionInfoButtonTitle();
	String sessionExtendButtonTitle();
	String sessionHelpHtml();
	
	// Navbar Admin help
	String navAdminsHelp();
	String navUsersHelp();
	String navSettingsHelp();
	String navReportsHelp();
	String navSearchHelp();
	
	String whatIsIt();
	String aboutObidos();
	
	// Password/Passphrase Complexity
    String passwordComplexity();
	String passwordComplexityHelp();
	String minimumLength();
	String minimumLowercaseCharacters();
	String minimumUppercaseCharacters();
	String minimumNumericCharacters();
	String minimumSpecialCharacters();
	String minimumPasswordLength();
	String minimumPassphraseLength();
	String minimumEntropy();
	String minimumPasswordEntropy();
	String minimumPassphraseEntropy();
	
	// Passphrase Complexity
	String passphraseComplexity();

	String licenseInfoHelp();

	String tombstonedHelp();
	String lockedHelp();
	String pickNotificationTemplateHelp();

	String smsSettings();
	String deleteSmsSettings();
	String deleteSmsSettingsWaringMessage();
	String twilio();
	String vonage();
	String smsProvider();

	String twilioHelp();
	String smsHelp();
	String configureTwilioSms();
	String twilioAccountSid();
	String twilioAuthToken();
	String twilioPhoneNumber();

	String vonageApiSecret();
	String vonageApiKey();
	String vonagePhoneNumber();

	String sendaTestSms();
	String sendTestSms();
	String phoneNumber();
	String smsMessage();
	String sendSMS();
	String twilioSmsSettings();
	String smsCostWarning();
    String atleastOneCapitalLetter();
    String atleastOneNumber();
    String atleastOnceSpecialCharacter();

    String generatingPleaseWait();

    String wpa();
    String wep();
    String nopass();
}

