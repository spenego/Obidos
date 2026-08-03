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

package com.spenego.Obidos.shared.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import com.spenego.Obidos.shared.Selectable;

public final class AuditDTO implements HasCreatedAt, HasAuditInfo, HasId, Serializable, Clearable, Selectable {
	private static final long serialVersionUID = 1L;

	public static final int EXCEPTION						= 0;	// regular exception from failed service call
	public static final int META_EXCEPTION					= 1;	// exception generated while attempting to create audit entry
	public static final int VIEW_ITEM		 				= 2;	// user viewed an item that was shared with them
	public static final int ITEM_GROUP_EXCEPTION			= 3;	// An Item Group Share Action created an exception
	public static final int PASS_RESET_EXCEPTION			= 4;	// An Item Action created an exception
	public static final int SUBVERSIVE_ITEM_VIEW			= 5;	// A user attempted to view an item by copying the item ID from the URL
	public static final int SUBVERSIVE_CONTAINER_VIEW		= 6;	// A user attempted to view a container by copying the container ID from the URL
	public static final int ACTION_DENIED_BY_CAPABILITY		= 7;	// An admin attempted an action they did not have the capability to perform
	public static final int LICENSE_INSTALLED				= 8;	// A license has been installed
	public static final int UPDATE_SYSTEM_CONFIG			= 9;	// Something in system config updated

	public static final int LOGIN							= 10;
	public static final int LOGOUT							= 11;
	public static final int FAILED_LOGIN_ATTEMPT			= 12;
	public static final int LICENSE_LIMIT_REACHED			= 13;
	public static final int CERTIFICATE_INSTALLED			= 14;	// A certificate has been installed

	public static final int CREATE_CONTAINER				= 20;
	public static final int CREATE_GROUP					= 22;
	public static final int CREATE_LDAP						= 23;
	public static final int CREATE_USER 					= 25;
	public static final int CREATE_SMTP_CONFIG 				= 26;
	public static final int CREATE_PASSWORD_RESET 			= 27;
	public static final int CREATE_ITEM		 				= 28;
	public static final int CREATE_USER_DEFINED_TYPE		= 29;
	public static final int CREATE_USER_DEFINED_FIELD		= 30;
	public static final int CREATE_USER_DEFINED_FIELD_VALUE	= 31;

	public static final int CAPABILITY_GRANTED				= 40;
	public static final int CAPABILITY_RESCINDED			= 41;

	public static final int ADDED_USER_TO_GROUP				= 50;
	public static final int REMOVED_USER_FROM_GROUP			= 52;
	public static final int UPDATE_CONTAINER				= 54;
	public static final int UPDATE_GROUP					= 55;
	public static final int UPDATE_LDAP						= 56;
	public static final int UPDATE_USER						= 58;
	public static final int USER_TOMBSTONED					= 59;
	public static final int USER_UNTOMBSTONED				= 60;

	public static final int UPDATE_SMTP_CONFIG				= 62;
	public static final int UPDATE_CAPABILITY				= 63;
	public static final int UPDATE_PASSWORD_RESET			= 64;
	public static final int UPDATE_ITEM						= 65;
	public static final int UPDATE_USER_DEFINED_TYPE		= 66;
	public static final int UPDATE_USER_DEFINED_FIELD		= 67;
	public static final int UPDATE_USER_DEFINED_FIELD_VALUE	= 68;
	public static final int USER_RESTORED					= 69;
	public static final int USER_LOCKED						= 70;
	public static final int USER_UNLOCKED					= 71;
	public static final int RENAMED_ITEM					= 72;

	public static final int DELETE_CONTAINER 				= 80;
	public static final int DELETE_GROUP					= 82;
	public static final int DELETE_LDAP						= 83;
	public static final int DELETE_USER 					= 85;
	public static final int DELETE_SMTP_CONFIG 				= 86;
	public static final int DELETE_CAPABILITY	 			= 87;
	public static final int DELETE_PASSWORD_RESET	 		= 88;
	public static final int DELETE_ITEM	 					= 89;
	public static final int DELETE_USER_DEFINED_TYPE		= 90;
	public static final int DELETE_USER_DEFINED_FIELD		= 91;
	public static final int DELETE_USER_DEFINED_FIELD_VALUE	= 92;
	public static final int DELETE_USER_DEFINED_TYPE_VALUE 	= 93;
	public static final int DELETE_NOTIFICATION				= 94;
	public static final int DELETE_DOCUMENT	 				= 95;

	public static final int SHARE_ITEM  					= 110;
	public static final int REVOKE_ITEM						= 111;
	public static final int REVOKE_ITEM_FROM_GROUP			= 112;
	public static final int SHARE_CONTAINER_WITH_GROUP		= 113;
	public static final int SHARE_CONTAINER_WITH_USER		= 114;
	public static final int REVOKE_CONTAINER_FROM_USER		= 115;
	public static final int REVOKE_CONTAINER_FROM_GROUP		= 116;
	public static final int ITEM_OWNERSHIP_TRANSFER  		= 117;
	public static final int ITEM_RELINQUISHED		  		= 118;
	public static final int CONTAINER_RELINQUISHED			= 119;

	public static final int PASSWORD_EXPIRED				= 120;
	public static final int PASSPHRASE_RESET_REQUEST		= 121;
	public static final int SENT_PASSPHRASE_RESET_EMAIL		= 122;
	public static final int TWO_FACTOR_CODE_NOT_SUPPLIED	= 123;
	public static final int TWO_FACTOR_NOT_ENABLED			= 124;
	public static final int PASSWORD_RESET_REQUEST			= 125;
	public static final int SENT_PASSWORD_RESET_EMAIL		= 126;
	public static final int SENT_WARNING_EMAIL				= 127;
	public static final int PASSWORD_RESET_REQUEST_IGNORED	= 128;
	public static final int PASSWORD_RESET_IGNORED			= 129;

	public static final int CONTAINER_OWNERSHIP_TRANSFER  	= 130;
	public static final int GRANT_ITEM_READ_ONLY  			= 131;
	public static final int GRANT_ITEM_OWNERSHIP  			= 132;
	public static final int GRANT_ITEM_UPDATE	  			= 133;
	public static final int GRANT_ITEM_UPDATE_AND_OWNERSHIP	= 134;


	private static final HashMap<Integer, String> actionMap = new HashMap<>(257);

	static {
		actionMap.put( EXCEPTION, 						 "generated exception");
		actionMap.put( META_EXCEPTION, 					 "generated exception during audit processing");
		actionMap.put( ITEM_GROUP_EXCEPTION, 			 "item group exception");
		actionMap.put( PASS_RESET_EXCEPTION, 			 "password reset exception");
		actionMap.put( SUBVERSIVE_ITEM_VIEW, 			 "attempted to view item they do not own");
		actionMap.put( SUBVERSIVE_CONTAINER_VIEW, 		 "attempted to view container they do not own");
		actionMap.put( VIEW_ITEM, 						 "viewed item");
		actionMap.put( LICENSE_INSTALLED, 				 "installed a license");
		actionMap.put( UPDATE_SYSTEM_CONFIG, 			 "updated system config");
		actionMap.put( LOGIN, 							 "login");
		actionMap.put( LOGOUT, 							 "logout");
		actionMap.put( FAILED_LOGIN_ATTEMPT, 			 "failed login attempt");

		actionMap.put( CREATE_CONTAINER, 				 "created container");
		actionMap.put( CREATE_GROUP, 					 "created group");
		actionMap.put( CREATE_LDAP, 					 "created LDAP entry");
		actionMap.put( CREATE_USER, 					 "created");
		actionMap.put( CREATE_SMTP_CONFIG, 			 	 "created SMTP configuration");
		actionMap.put( CREATE_PASSWORD_RESET, 			 "created a password reset request");
		actionMap.put( CREATE_ITEM, 					 "created an item");
		actionMap.put( CREATE_USER_DEFINED_TYPE, 		 "created a template");
		actionMap.put( CREATE_USER_DEFINED_FIELD, 		 "created a template field");
		actionMap.put( CREATE_USER_DEFINED_FIELD_VALUE,  "created item value ");

		actionMap.put( ADDED_USER_TO_GROUP, 			 "added user %u to group");
		actionMap.put( REMOVED_USER_FROM_GROUP, 		 "removed user %u from group");
		actionMap.put( UPDATE_CONTAINER, 				 "updated container");
		actionMap.put( UPDATE_GROUP, 					 "updated group");
		actionMap.put( UPDATE_LDAP, 					 "updated LDAP entry");
		actionMap.put( UPDATE_USER, 					 "updated user");
		actionMap.put( USER_TOMBSTONED, 				 "tombstoned user");
		actionMap.put( UPDATE_SMTP_CONFIG, 				 "updated SMTP config");
		actionMap.put( UPDATE_CAPABILITY, 				 "updated capability");
		actionMap.put( UPDATE_PASSWORD_RESET, 			 "password reset record updated");
		actionMap.put( UPDATE_ITEM, 					 "updated item");
		actionMap.put( RENAMED_ITEM, 					 "renamed item %i to");
		actionMap.put( UPDATE_USER_DEFINED_TYPE, 		 "template updated");
		actionMap.put( UPDATE_USER_DEFINED_FIELD, 		 "template field updated");
		actionMap.put( UPDATE_USER_DEFINED_FIELD_VALUE,  "item value updated");
		actionMap.put( USER_RESTORED, 					 "restored user");
		actionMap.put( USER_LOCKED, 					 "locked user");
		actionMap.put( USER_UNLOCKED, 					 "unlocked user");

		actionMap.put( DELETE_CONTAINER, 				 "deleted container");
		actionMap.put( DELETE_GROUP, 					 "deleted group");
		actionMap.put( DELETE_LDAP, 					 "deleted LDAP record");
		actionMap.put( DELETE_USER, 					 "deleted user");
		actionMap.put( DELETE_SMTP_CONFIG, 			 	 "deleted SMTP config");
		actionMap.put( DELETE_CAPABILITY, 				 "deleted capability");
		actionMap.put( DELETE_PASSWORD_RESET, 			 "deleted password reset request");
		actionMap.put( DELETE_ITEM, 					 "deleted item");
		actionMap.put( DELETE_USER_DEFINED_TYPE, 		 "deleted template");
		actionMap.put( DELETE_USER_DEFINED_FIELD, 		 "deleted template");
		actionMap.put( DELETE_USER_DEFINED_FIELD_VALUE,  "deleted item value");
		actionMap.put( DELETE_USER_DEFINED_TYPE_VALUE,   "deleted template value");
		actionMap.put( DELETE_NOTIFICATION, 			 "deleted notification");

		actionMap.put( SHARE_ITEM, 					 	 "shared item");
		actionMap.put( REVOKE_ITEM_FROM_GROUP,			 "revoked item");
		actionMap.put( REVOKE_ITEM, 					 "revoked item");
		actionMap.put( SHARE_CONTAINER_WITH_GROUP, 	 	 "shared container %c with group");
		actionMap.put( SHARE_CONTAINER_WITH_USER, 		 "shared container %c with user");
		actionMap.put( REVOKE_CONTAINER_FROM_USER, 	 	 "revoked container %c from user");
		actionMap.put( REVOKE_CONTAINER_FROM_GROUP, 	 "revoked container %c from group");
		actionMap.put( ITEM_OWNERSHIP_TRANSFER, 		 "took ownership of item");
		actionMap.put( CONTAINER_OWNERSHIP_TRANSFER, 	 "took ownership of container");
		actionMap.put( ITEM_RELINQUISHED, 				 "relinquished item");
		actionMap.put( CONTAINER_RELINQUISHED, 		 	 "relinquished container");

		actionMap.put( PASSWORD_EXPIRED, 		 	 	 "password expired");

		actionMap.put( PASSPHRASE_RESET_REQUEST, 		 "passphrase reset request");
		actionMap.put( SENT_PASSPHRASE_RESET_EMAIL, 	 "sent passphrase reset email");
		actionMap.put( TWO_FACTOR_CODE_NOT_SUPPLIED, 	 "two factor code not supplied");
		actionMap.put( TWO_FACTOR_NOT_ENABLED, 		 	 "two factor auth not enabled");
		actionMap.put( PASSWORD_RESET_REQUEST, 		 	 "password reset request");
		actionMap.put( PASSWORD_RESET_REQUEST_IGNORED,   "password reset request ignored");
		actionMap.put( PASSWORD_RESET_IGNORED, 		 	 "password reset ignored");
		actionMap.put( SENT_PASSWORD_RESET_EMAIL, 		 "send password reset email");
		actionMap.put( SENT_WARNING_EMAIL, 			 	 "sent a warning email");
		actionMap.put( LICENSE_LIMIT_REACHED, 			 "license limit reached");
	}

	public static final String action(final int actionCode) {
		return actionMap.computeIfAbsent(actionCode, a -> "unknown code " + a);
	}

	public void clear() {
		recipientFullname = fullname =  message = details = objectName = username = null;
		newItemId = recipientId = objectId = userId = null;
		action = null;
		createdAt = null;
		id = null;
	}

	@Override
	public String getActionStr() {
		return action(action);
	}

	private Long id;
	private Date createdAt;
	private Integer action;
	private Long userId;
	private Long objectId;
	private Long recipientId;
	private Long newItemId;
	private String username;
	private String objectName;
	private String details;
	private String message;
	private String fullname;
	private String recipientFullname;

	public Long getId()					{ return id; }
	public Long getObjectId()			{ return objectId; }
	public Long getNewItemId()			{ return newItemId; }
	public String getObjectName()		{ return objectName; }
	public String getDetails()			{ return details; }
	public String getMessage()			{ return message; }
	public Long getRecipientId()		{ return recipientId; }
	public String getFullname()			{ return fullname; }
	public String getRecipientFullname(){ return recipientFullname; }
	@Override
	public Integer getAction()			{ return action; }
	@Override
	public Long getUserId()				{ return userId; }
	@Override
	public String getUsername()			{ return username; }
	@Override
	public Date getCreatedAt()			{ return createdAt; }
	
	public void setId(final Long id)				{ this.id = id; }
	public void setAction(final Integer action)		{ this.action = action; }
	public void setUserId(final Long userId)		{ this.userId = userId; }
	public void setObjectId(final Long objectId)	{ this.objectId = objectId; }
	public void setNewItemId(final Long newItemId)	{ this.newItemId = newItemId; }
	public void setUsername(final String username)	{ this.username = username; }
	public void setObjectName(String objectName)	{ this.objectName = objectName; }
	public void setDetails(final String details)	{ this.details = details; }
	public void setMessage(final String message)	{ this.message = message; }
	@Override
	public void setCreatedAt(final Date createdAt)	{ this.createdAt = createdAt; }
	public void setRecipientId(Long recipientId)	{ this.recipientId = recipientId; }
	public void setFullname(String fullname) { this.fullname = fullname; }
	public void setRecipientFullname(String recipientFullname) { this.recipientFullname = recipientFullname; }
	@Override
	public void setSelected(Boolean selected) {
		// TODO Auto-generated method stub
	}

	@Override
	public Boolean getSelected() {
		// TODO Auto-generated method stub
		return null;
	}
}
