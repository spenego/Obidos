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

package com.spenego.Obidos.shared;

import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class ObidosConstants
{
	// must match with the one in web.xml
	public static final String XSRF_COOKIE_NAME				= "X-OBIDOS-XSRF-COOKIE";
	public static final String OBIDOS_LOGIN_COOKIE			= "X-OBIDOS-LOGIN-COOKIE";
	public static final String OBIDOS_EBE_COOKIE            = "X-OBIDOS-EBE-COOKIE";
	public static final String OBIDOS_LANGUAGE_COOKIE       = "X-OBIDOS-LANG-COOKIE";
	public static final String LOGIN_STAGE					= "stage";
	public static final String LOGIN						= "login";

	public static final String SPENEGO_DIR_LINUX			= "/usr/local/spenego";
	public static final String SPENEGO_DIR_WINDOWS          = "c:/spenego";

	public static final String CONFIG_DIR_LINUX				= SPENEGO_DIR_LINUX + "/obidos";
	public static final String CONFIG_DIR_WINDOWS			= SPENEGO_DIR_WINDOWS + "/obidos";

	public static final String COPY_CERT_PROG               = CONFIG_DIR_LINUX + "/bin/copy_cert";

	public static final String CONFIG_FILE_NAME             = "obidos.properties";
	public static final String LIBDIR                       = SPENEGO_DIR_LINUX + "/lib";
	public static final String LIBDIR_WINDOWS               = SPENEGO_DIR_WINDOWS + "/lib";

	public static final String CONFIG_FILE_PATH             = CONFIG_DIR_LINUX + "/" + CONFIG_FILE_NAME;
	public static final String CONFIG_FILE_PATH_WINDOWS		= CONFIG_DIR_WINDOWS + "/" + CONFIG_FILE_NAME;

	public static final String SODIUM_LIB_PATH_LINUX		= LIBDIR + "/libsodium.so";
	public static final String SODIUM_LIB_PATH_WINDOWS      = LIBDIR_WINDOWS + "/libsodium.dll";
	public static final String SODIUM_LIB_PATH_MAC          = LIBDIR + "/libsodium.dylib";

	public static final String JDBC_XML_FILE_UNIX           = CONFIG_DIR_LINUX + "/jdbc.xml";
	public static final String JDBC_XML_FILE_WINDOWS        = CONFIG_DIR_WINDOWS + "/jdbc.xml";
	public static final String JDBC_XMLM_FILE_UNIX          = CONFIG_DIR_LINUX + "/jdbcm.xml";
	public static final String JDBC_XMLM_FILE_WINDOWS       = CONFIG_DIR_WINDOWS + "/jdbcm.xml";

	public static final String AD_LDAP_PROPERTIES_UNIX      = CONFIG_DIR_LINUX + "/adldap.properties";
	public static final String AD_LDAP_PROPERTIES_WINDOWS   = CONFIG_DIR_WINDOWS + "/adldap.properties";

	public static final String FERNET_ROTATING_UNIX = CONFIG_DIR_LINUX + "/fernet_rotating.properties";
	public static final String FERNET_ROTATING_WINDOWS = CONFIG_DIR_WINDOWS + "/fernet_rotating.properties";

	public static final int FILE_UPLOAD_FERNET_KEY_FILE_AGE = 30; // rotate the key file if older than 30 seconds

	public static final String AD_LDAP_ALIAS_STARTS_WITH          = "obidos_ldap";
	public static final String INSTALL_NGINX_CERTS                = "obidos";
	public static final String INSTALL_ADLAP_CERTS                = "adldap";
	
	public static final Long PASSWORD_CRACKING_GUESSES_PER_SECOND = 1000000000L;

	// certificates
	// nginx default file is parsed for the location of certificate
	public static final String NGINX_DEFAULT_FILE      = "/usr/local/spenego/nginx/etc/sites-enabled/default";
	public static final String SYSTEM_CACERTS          = "/usr/local/spenego/jdk/lib/security/cacerts";


	// path of the pasted certificate and key file
	// cronjob or some other program watches for these files and installs elsewhere
	// for nginx
	public static final String NGINX_TEMP_CERT_PATH_UNIX     			= CONFIG_DIR_LINUX + "/tmp/nginx_certs.pem";
	public static final String NGINX_TEMP_CERT_PATH_WINDOWS  			= CONFIG_DIR_WINDOWS + "/tmp/nginx_certs.pem";
	public static final String NGINX_TEMP_PRIVATE_KEY_PATH_UNIX  		= CONFIG_DIR_LINUX + "/tmp/nginx_key.pem";
	public static final String NGINX_TEMP_PRIVATE_KEY_PATH_WINDOWS  	= CONFIG_DIR_WINDOWS + "/tmp/nginx_key.pem";

	// saved in server side as session attribute
	public static final String OBIDOS_SESSION				= "X-OBIDOS-SESSION";

	// instead of JSESSIONID, defined in web.xml
	// Note: it is important to use Session cookie name and path exactly the
	// in code as well
	public static final String OBIDOS_SESSION_COOKIE		= "OBIDOS-SESSION-COOKIE";
	public static final String OBIDOS_COOKIE_PATH			= "/Obidos/rpc";
	public static final String OBIDOS_USERNAME_COOKIE		= "OBIDOS-USERNAME-COOKIE";
	public static final String OBIDOS_USERID_COOKIE			= "OBIDOS-USERID-COOKIE";
	public static final String OBIDOS_USERTYPE_COOKIE		= "OBIDOS-USERTYPE-COOKIE";

	public static final String AUTH_SOURCE_LOCAL			= "local";
	public static final String AUTH_SOURCE_LDAP				= "ldap";

	public static final String DICEWARE_ENGLISH				= "English";
	public static final String DICEWARE_GERMAN				= "Deutsch";

	public static final String DEFAULT_DATEPICKER_DATE_FORMAT = "yyyy/mm/dd";

	public static final int CREDENTIAL_TYPE_USERNAME_PASSWORD     = 0x01;
	public static final int CREDENTIAL_TYPE_PUBLICKEY_PRIVATEKEY  = 0x02;

	public static final int VISIBLE_GRID_COUNT  = 30;

	public static final int SMTP_AUTH_STARTTLS = 0;
	public static final int SMTP_AUTH_SSL = 1;
	public static final int SMTP_AUTH_NONE = 2;

	public static final int SESSION_TIMEOUT_MILLIS = 1800 * 1000; // 30 minutes
	public static final int SESSION_CHECK_MILLIS   = 60 * 1000; // 10 minutes
	public static final int SESSION_GRACE_MILLIS   = 60 * 1000;  // 1 minute

	public static final String REFRESH_SESSION = "refresh session";
	public static final String GET_SESSION     = "get session";
	public static final String VIEW_USERS_NOTE_IS_SHARED_WITH     = "VIEW_USERS_NOTE_IS_SHARED_WITH";

	public static final String TWO_FACTOR_CODE_REQUIRED = "twoFACodeRequired";
	public static final int SEARCHBOX_PLACEHOLDER 			= 100;
	public static final int SEARCHBOX_TEXT        			= 101;
	public static final int LOGOUT                			= 102;
	public static final int SESSION_TIMED_OUT     			= 103;
	public static final int PASSPHRASE_CACHED     			= 104;
	public static final int TWO_FACTOR_ENABLED    			= 105;
	public static final int TWO_FACTOR_DISABLED   			= 106;
	public static final int REFRESH_PASSPHRASE_CACHED_ICON  = 107;

	public static final String SHARE_WITH_USERS            = "shareWithUsers";
	public static final String SHARE_WITH_GROUPS           = "shareWithGroups";
	public static final String SHARE_NOTE_WITH_USERS       = "shareNoteWithUsers";
	public static final String SHARE_NOTE_WITH_GROUPS      = "shareNoteWithGroups";
	public static final String SHARE_ITEM_WITH_USERS       = "shareItemWithUsers";
	public static final String SHARE_ITEM_WITH_GROUPS      = "shareItemWithGroups";
	public static final String SHARE_CONTAINER_WITH_USERS  = "sahreContainerWithUsers";
	public static final String SHARE_CONTAINER_WITH_GROUPS = "shareContainerWithGroups";
	public static final String SHOW_TEMPLATE_UPDATE_MESSAGE = "showTemplateUpdateMessage";

	public static final int NOTE_ID_N 					= 1;
	public static final int ITEM_ID_N 					= 2;
	public static final int CONTAINER_ID_N 				= 3;
	public static final int GROUP_ID_N 					= 4;
	public static final int TEMPLATE_ID_N 				= 5;
	public static final int RELINQUISH_ITEM_ID_N 		= 6;
	public static final int RELINQUISH_NOTE_ID_N 		= 7;
	public static final int USER_ID_N 			    	= 8;
	public static final int ADMIN_ID_N 			    	= 9;
	public static final int NOTIFICATION_MESSAGE_ID_N   = 10;

	public static final int MJANE = 100;
	public static final int ADMIN = 102;
	public static final int LOGGED_IN_USER_TYPE = MJANE;

	public static final String SESSION_ERROR    = "Session Error";
	public static final String ADMIN_DENIED     = "This action may not be performed by an admin";

	public static final long FIRST_ROOT_ADMIN_ID                            = 0;
	public static final long ACCOUNT_CREATED_NOTIFICATION_TEMPLATE          = 171;
	public static final long PASSWORD_RESET_NOTIFICATION_TEMPALTE           = 209;
	public static final long PASSWORD_RESET_WARNING_NOTIFICATION_TEMPALTE   = 312;
	public static final long ITEM_SHARED_NOTIFICATION_TEMPLATE              = 417;
	public static final long ITEM_REVOKED_NOTIFICATION_TEMPLATE             = 524;
	public static final long ITEM_DELETED_NOTIFICATION_TEMPLATE             = 525;
	public static final long CONTAINER_SHARED_NOTIFICATION_TEMPLATE         = 633;
	public static final long CONTAINER_REVOKED_NOTIFICATION_TEMPLATE        = 744;
	public static final long UNIT_TEST_TEMPLATE                             = 857;
	public static final long PASSPHRASE_RESET_NOTIFICATION_TEMPALTE         = 867;
	public static final long PASSPHRASE_RESET_WARNING_NOTIFICATION_TEMPALTE = 869;

	// Issue #585
	public static final int FIELD_NAME_LENGTH = 32;
	public static final int TEXTFIELD_MAX_LENGTH = 64;
	public static final int TEXTAREA_MAX_LENGTH  = 255;

	public static final String ITEM_SHARED      = "Itemshared";
	public static final String ACCOUNT_CREATED  = "accountcreated";
	public static final String PASSWORD_RESET   = "passwordreset";
	public static final String PASSWORD_RESET_WARNING   = "passwordresetwarning";

	public static final String PASSAY_MESSAGE_PROPERTIES_FILE   = "passay/PassComplexityMessageCatalog.properties";


	// message type
	public static final String PASSWORD_CHANGED_MESSAGE = "Your password was changed successfully. Please sign in with the new password";

	public static final String ACTION           = "action";
	public static final String SHARE_NOTE       = "sharenote";
	public static final String VIEW_NOTE        = "viewnote";
	public static final String EDIT_PERSONAL_TEMPLATE        = "editPersonalTemplate";
	public static final String EDIT_NOTE        = "editnote";
	public static final String EDIT_CONTAINER   = "editContainer";
	public static final String DELETE_CONTAINER   = "deleteContainer";
	public static final String PLACE            = "place";
	public static final String FROM            = "from";
	public static final String NAME_TOKEN       = "nameToken";
	public static final String RESET_TOKEN      = "token";
	public static final String REASON           = "reason";
	public static final String OWNERID           = "ownerid";
	public static final String ALL_MY_NOTES      = "allmynotes";
	public static final String SHARED_WITH_OTHERS  = "sharedwithothers";
	public static final String CONTAINERS_SHARED_WITH_OTHERS  = "containersSharedwithothers";
	public static final String CREDENTIALS_SHARED_WITH_OTHERS  = "credentialsSharedwithothers";
	public static final String SHARED_WITH_ME      = "sharedwithme";
	public static final String OWNER            = "owner";
	public static final String SHARED_BY           = "sharedby";
	public static final String SHARED_BY_ID        = "sharedbyid";
	public static final String SESSION_EXPIRED  = "sessionexpired";
	public static final String SHARED           = "shared";
	public static final String REDIRTECT_TO     = "redirect-to";
	public static final String SHARE            = "share";
	public static final String ITEM             = "item";
	public static final String NOTE             = "note";
	public static final String ITEM_ID          = "itemId";
	public static final String ID          		= "id";
	public static final String CONTAINER        = "container";
	public static final String TYPE             = "type";
	public static final String SHARE_TYPE       = "sharetype";
	public static final String SEARCH           = "search";
	public static final String SHOW             = "show";
	public static final String NAME             = "name";
	public static final String EDIT             = "edit";
	public static final String VIEW             = "view";
	public static final String ADD              = "add";
	public static final String CREATE_SYSTEM    = "createsystem";
	public static final String CREATE_GROUP     = "creategroup";
	public static final String NUMBER_OF_GROUP  = "ng";
	public static final String ZERO             = "zero";
	public static final String CONTAINER_TYPE   = "containertype";

	public static final String TEMPLATE_TYPE    = "templatetype";
	public static final String PERSONAL         = "personal";
	public static final String GLOBAL           = "global";
	public static final String FREE_FORMAT      = "freeformat";
	public static final String NOTEBOOK         = "notebook";
	public static final String ITEM_TYPE        = "itemtype";
	public static final String LOCKED           = "locked";
	public static final String DELETED          = "deleted";
	public static final String TRUE             = "true";
	public static final String FALSE            = "false";
	public static final String MARK_TYPE        = "marktype";
	public static final String PASSWORD_IS_RESET = "passwordreset";


	public static final int TEMPLATE_TYPE_PERSONAL = 100;
	public static final int TEMPLATE_TYPE_GLOBAL   =   101;
	public static final int TEMPLATE_TYPE_FREE_FORMAT =   102;
	public static final int TEMPLATE_TYPE_UNKNOWN =   -1;

	public static final String USER_ID          = "userid";
	public static final String CREDENTIALS_ID   = "credid";
	public static final String NOTE_ID          = "noteid";
	public static final String PERSONAL_TEMPLATE_ID        = "personalTempalteId";
	public static final String GLOBAL_TEMPLATE_ID          = "globalTempalteId";
	public static final String TEMPLATE_ID          = "templateId";
	public static final String CONTAINER_ID     = "containerid";
	public static final String SYSTEM_ID        = "systemid";
	public static final String GROUP_ID         = "groupid";
	public static final String LDAP_ID          = "ldapid";

	public static final int VISIBLE_LINES_IN_NOTES = 6;

	public static final String LIST = "list";
	public static final String LIST_LOCKED_USERS = "lockedusers";
	public static final String LIST_USERS = "users";
	public static final String TYPE_USERS = "users";
	public static final String LIST_CREDENTIALS = "credentials";
	public static final String LIST_ADMINS = "admins";
	public static final String TYPE_ADMINS = "admins";
	public static final String LIST_DELETED_ADMINS = "deletedadmins";
	public static final String LIST_DELETED_USERS = "deletedusers";

	// If show password icon is pressed, hide the password i specified seconds
	public static final int HIDE_PASSWORD_BOX_SECONDS = 60; // seconds

	public static final String INFO_ICON    = "<i class=\"fa fa-info-circle\"></i>&nbsp;";
	public static final String WARNING_ICON = "<i class=\"fa fa-warning\"></i>&nbsp;";
	public static final String SUCCESS_ICON = "<i class=\"fa fa-check\"></i>&nbsp;";
	public static final String ERROR_ICON   = "<i class=\"fa fa-times-circle\"></i>&nbsp;";

	public static final String BOOTSTRAP3_PRAMRY_BGCOLOR	= "#337AB7";
	public static final String BOOTSTRAP3_BLACK_BGCOLOR		= "#222";
	public static final String BOOTSTRAP3_BLACK_FGCOLOR		= "#333";
	// bootstrap colors
	public static final String BLACK_COLOR   = "#000000";
	public static final String PRIMARY_COLOR = "#2F79B9";
	public static final String INFO_COLOR    = "#56C0E0";
	public static final String SUCCESS_COLOR = "#58B957";
//	public static final String WARNING_COLOR = "#F2AE43";
	public static final String WARNING_COLOR = "#F65D3C";
	public static final String DANGER_COLOR  = "#B0433F";
	public static final String MEDIUM_AQUAMARINE_COLOR  = "#66CDAA";
	public static final String PURPLE_COLOR = "#9900ff";
	public static final String RED_COLOR = "red";
	public static final String GREEN_COLOR = "green";

	public static final String NOTIFICATION_BELL_COLOR_ZERO = "#337AB7";
	public static final String NOTIFICATION_BELL_COLOR = "green";


	public static final String ADD_ICON_COLOR    = "#008000";
	public static final String REVOKE_ICON_COLOR = WARNING_COLOR;
	public static final String RELINQUISH_ICON_COLOR = WARNING_COLOR;
//	public static final String EDIT_ICON_COLOR   = "#F99533";
	public static final String EDIT_ICON_COLOR   = "#008000";
	public static final String DELETE_ICON_COLOR = "#FF0000";
	public static final String VIEW_ICON_COLOR   = "#1E8CFF";
	public static final String GRAY_COLOR        = "#BBB";

	// revoke
	public static final String REVOKE_CONTAINER_SHARING = "revokeContainer";
	public static final String REVOKE_ITEM_SHARING = "revokeItem";
	public static final String REVOKE_NOTE_SHARING = "revokeNote";

	public static final int ENCRYPTION_MODE_RAW			= 0; // we initially stored values like this, but, it allows
	public static final int ENCRYPTION_MODE_PADDED		= 1; // black-hats to see value length, now we pad values
	public static final int ENCRYPTION_MODE_PADDED2		= 2; // do not store a partially known value at a known location (pad length is masked)
	public static final int ENCRYPTION_BLOCK_OVERHEAD	= 48; // Size that each buffer is increased when encrypted
	public static final int ENCRYPTION_BLOCK_SIZE		= (64 * 1024) - ENCRYPTION_BLOCK_OVERHEAD; // generated block has 48 bytes of overhead, try to write 64K
	public static final int NOTE_HEIGHT = 200;


	public static final int CELL_TYPE_SHARE       				   	   = 1;
	public static final int CELL_TYPE_REVOKE      				   	   = 2;
	public static final int CELL_TYPE_ADD         				   	   = 3;
	public static final int CELL_TYPE_DELETE      				   	   = 4;
	public static final int CELL_TYPE_EDIT        				   	   = 5;
	public static final int CELL_TYPE_LIST        				   	   = 6;
	public static final int CELL_TYPE_RELINQUISH  				   	   = 7;
	public static final int CELL_TYPE_VIEW       			       	   = 8;
	public static final int CELL_TYPE_REMOVE      				       = 9;
	public static final int CELL_TYPE_CONTAINER_SHAREABLE_NO_TOOLTIP   = 10;
	public static final int CELL_TYPE_NOTE_SHARED_NO_TOOLTIP       	   = 11;
	public static final int CELL_TYPE_CONTAINER_SHARED_NO_TOOLTIP  	   = 12;
	public static final int CELL_TYPE_SHARED_WITH_OTHERS         	   = 13;
	public static final int CELL_TYPE_EDIT_SHARED  				   	   = 14;
	public static final int CELL_TYPE_SHARE_SHARED  			       = 15;
	public static final int CELL_TYPE_VIEW_NOTIFICATION_SHARED  	   = 16;
	public static final int CELL_TYPE_NOTIFICATION_DATE         	   = 17;
	public static final int CELL_TYPE_ITEM_CONTAINER_SHARED_REVOKED    = 18;
	public static final int CELL_TYPE_CONTAINER_NAME_IN_LIST_ITEMS     = 19;
	public static final int CELL_TYPE_LOCKED_USER                      = 20;
	public static final int CELL_TYPE_MARKED_DELETED_USER              = 21;
	public static final int CELL_TYPE_COLORED                          = 22;
	public static final int CELL_TYPE_EDIT_ADMIN                       = 23;
	public static final int CELL_TYPE_EDIT_USER                        = 24;
	public static final int CELL_TYPE_AUDIT_LOG                        = 25;
	public static final int CELL_TYPE_GENERAL                          = 26;
	public static final int CELL_TYPE_COPY                             = 27;
	public static final int CELL_TYPE_TEST                             = 28;
	public static final int CELL_TYPE_CONTAINER_NAME                   = 29;
	public static final int CELL_TYPE_CONTAINER_SHARED_WITH_USERS_GROUPS = 30;
	public static final int CELL_TYPE_ITEM_NAME						   = 31;
	public static final int CELL_TYPE_ITEM_SHARED_WITH_USERS_GROUPS	   = 32;
	public static final int CELL_TYPE_NOTE_NAME						   = 33;
	public static final int CELL_TYPE_NOTE_SHARED_WITH_USERS		   = 34;
	public static final int CELL_TYPE_ITEM_USER_GROUP				   = 35;
	public static final int CELL_TYPE_CONTAINER_USER_GROUP			   = 36;
	public static final int CELL_TYPE_USERS							   = 38;
	public static final int CELL_TYPE_GROUP							   = 39;
	public static final int CELL_TYPE_NOTE_SHARED_WITH_USERS_GROUPS	   = 40;
	public static final int CELL_TYPE_TEMPLATE_NAME					   = 41;
	public static final int CELL_TYPE_USERS_IN_GROUP 				   = 42;
	public static final int CELL_TYPE_EMAIL	 				   		   = 43;
	public static final int CELL_TYPE_FULLNAME						   = 44;
	public static final int CELL_TYPE_ITEM_SHAREABLE_OR_PRIVATE        = 45;
	public static final int CELL_TYPE_EDIT_GLOBAL_TEMPLATE             = 46;
	public static final int CELL_TYPE_TAKE_CONTAINER_OWNERSHIP         = 47;
	public static final int CELL_TYPE_SIMPLE_TEXT                      = 48; // not used
	public static final int CELL_TYPE_PHONE_NUMBER                     = 49;


	public static final String PROFILE_PLACEHOLDER_IMAGE = "128x128.png";
	public static final int PROFILE_PIC_SIZE_BYTES       = 20480;
	public static final int PROFILE_PIC_MAX_WIDTH 	     = 128;
	public static final int PROFILE_PIC_MAX_HEIGHT 	     = 128;

	public static final String DEFAULT_PROFILE_IMAGE_STYPE = "1px solid #ddd";
	public static final String LOADED_PROFILE_IMAGE_STYPE  = "2px solid " + ObidosConstants.PURPLE_COLOR;

	// password policy etc
	public static final int MIN_PASSWORD_LENGTH 				= 14;
	public static final int ALLOWABLE_MIN_PASSWORD_LENGTH 	= 12;
	public static final int MIN_PASSPHRASE_LENGTH 			= 20;
	public static final int ALLOWABLE_MIN_PASSPHRASE_LENGTH 	= 18;
	public static final int MAX_PASSWORD_AGE_IN_DAYS			= 0;
	public static final int ACCEPTABLE_PASSWORD_SCORE			= 16;
	public static final int PASSWORD_ENTROPY_STRONG           = 16;
	public static final int PASSWORD_ENTROPY_VERY_STRONG      = 18;
	public static final int PASSPHRASE_ENTROPY_STRONG         = 26;
	public static final int PASSPHRASE_ENTROPY_VERY_STRONG    = 28;
	public static final int PASSWORD_NEVER_EXPIRES            = 9999998;
	public static final long MS_PER_DAY 				= 86400000;

	public static final String DOCUMENT_STORE_DIR = "/usr/local/spenego/obidos/DataStore";

	// wise upload button text
	public static String disableUploadButonText = "<button type=\"button\" class=\"btn btn-primary disabled\"><i class=\"fa fa-upload\"></i> Choose File</button>";
	public static String enableUploadButonText = "<button type=\"button\" class=\"btn btn-primary\"><i class=\"fa fa-upload\"></i> Choose File</button>";
	/**
	 *
	 * @param templateType
	 * @return the base pathname of the template file (sans extension)
	 * @throws ServerSideException
	 */
	public static String getTemplatePathname(final long templateType) throws ServerSideException {
		switch ((int) templateType) {
			case (int) ACCOUNT_CREATED_NOTIFICATION_TEMPLATE:			return "email_templates/account_created";
			case (int) PASSWORD_RESET_NOTIFICATION_TEMPALTE:			return "email_templates/password_reset";
			case (int) PASSWORD_RESET_WARNING_NOTIFICATION_TEMPALTE:	return "email_templates/password_reset_warning";
			case (int) ITEM_SHARED_NOTIFICATION_TEMPLATE:				return "email_templates/item_shared";
			case (int) ITEM_REVOKED_NOTIFICATION_TEMPLATE:				return "email_templates/item_revoked";
			case (int) ITEM_DELETED_NOTIFICATION_TEMPLATE:				return "email_templates/item_deleted";
			case (int) CONTAINER_SHARED_NOTIFICATION_TEMPLATE:			return "email_templates/container_shared";
			case (int) CONTAINER_REVOKED_NOTIFICATION_TEMPLATE:			return "email_templates/container_revoked";
			case (int) PASSPHRASE_RESET_NOTIFICATION_TEMPALTE:			return "email_templates/passphrase_reset";
			case (int) PASSPHRASE_RESET_WARNING_NOTIFICATION_TEMPALTE:	return "email_templates/passphrase_reset_warning";

			default: throw new ServerSideException("Unknown Notification Template type");
		}
	}
	
	// Bug #81
	public static int KEY_STROKE_DELAY_FOR_STRENGTH_CHECK = 1000; // 1 sec
	
	// No reply email
	public static String NO_REPLY_FROM = "no-reply";

	// used by ObidosTimeBox
	public static String TIME_FORMAT = "hh:mm a";
	
	// Document upload/download type. will in the payload inside fernet token
	public static final int ACTION_TYPE_NA                  = 0;
	public static final int ACTION_TYPE_DOWNLOAD_USERGUIDE  = 1;
	public static final int ACTION_TYPE_DOWNLOAD_ADMINGUIDE = 2;
	
	public static final int SMS_PROVIDER_TWILIO = 1;
	public static final int SMS_PROVIDER_VONAGE = 2;
	
	public static final String INPUT_GROUP_ADDON_WIDTH = "38px";
	
	// Shorten file names if more than this for display purpose
	// Bug #74
	// Nov-24, 2024
	public static final int SHORTEN_STRING_AFTER_LENGTH = 64;
	
	// QRCodes constants
	public static final String WIFI_QRCODE  = "776966697172636f6465";
	public static final String VCARD_QRCODE = "76636172647172636f6465";

}
