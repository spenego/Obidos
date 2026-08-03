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

package com.spenego.Obidos.server.model;

import static java.util.Arrays.asList;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.spenego.Obidos.shared.dto.AuditDTO;
import com.spenego.Obidos.shared.dto.Clearable;
import com.spenego.Obidos.shared.dto.HasAuditInfo;

public final class Audit implements HasAuditInfo, Clearable, Model, Serializable {
	private static final long serialVersionUID = 1L;

	public static final int EXCEPTION						= AuditDTO.EXCEPTION;					// regular exception from failed service call
	public static final int META_EXCEPTION					= AuditDTO.META_EXCEPTION;				// exception generated while attempting to create audit entry
	public static final int ITEM_GROUP_EXCEPTION			= AuditDTO.ITEM_GROUP_EXCEPTION;		// An Item Group Share Action created an exception
	public static final int PASS_RESET_EXCEPTION			= AuditDTO.PASS_RESET_EXCEPTION;		// An Item Action created an exception
	public static final int SUBVERSIVE_ITEM_VIEW			= AuditDTO.SUBVERSIVE_ITEM_VIEW;		// A user attempted to view an item by copying the item ID from the URL
	public static final int SUBVERSIVE_CONTAINER_VIEW		= AuditDTO.SUBVERSIVE_CONTAINER_VIEW;	// A user attempted to view a container by copying the container ID from the URL
	public static final int ACTION_DENIED_BY_CAPABILITY		= AuditDTO.ACTION_DENIED_BY_CAPABILITY;	// An admin attempted an action they did not have the capability to perform
	public static final int VIEW_ITEM		 				= AuditDTO.VIEW_ITEM;					// user viewed an item that was shared with them
	public static final int LICENSE_INSTALLED		 		= AuditDTO.LICENSE_INSTALLED;			// a license has been installed
	public static final int UPDATE_SYSTEM_CONFIG		 	= AuditDTO.UPDATE_SYSTEM_CONFIG;		// system config updated
	public static final int CERTIFICATE_INSTALLED		 	= AuditDTO.CERTIFICATE_INSTALLED;		// an SSL certificate has been installed

	public static final int LOGIN							= AuditDTO.LOGIN;
	public static final int LOGOUT							= AuditDTO.LOGOUT;
	public static final int FAILED_LOGIN_ATTEMPT			= AuditDTO.FAILED_LOGIN_ATTEMPT;
	public static final int LICENSE_LIMIT_REACHED			= AuditDTO.LICENSE_LIMIT_REACHED;

	public static final int CREATE_CONTAINER				= AuditDTO.CREATE_CONTAINER;
	public static final int CREATE_GROUP					= AuditDTO.CREATE_GROUP;
	public static final int CREATE_LDAP						= AuditDTO.CREATE_LDAP;
	public static final int CREATE_USER 					= AuditDTO.CREATE_USER;
	public static final int CREATE_SMTP_CONFIG 				= AuditDTO.CREATE_SMTP_CONFIG;
	public static final int CREATE_PASSWORD_RESET 			= AuditDTO.CREATE_PASSWORD_RESET;
	public static final int CREATE_ITEM		 				= AuditDTO.CREATE_ITEM;
	public static final int CREATE_USER_DEFINED_TYPE		= AuditDTO.CREATE_USER_DEFINED_TYPE;
	public static final int CREATE_USER_DEFINED_FIELD		= AuditDTO.CREATE_USER_DEFINED_FIELD;
	public static final int CREATE_USER_DEFINED_FIELD_VALUE	= AuditDTO.CREATE_USER_DEFINED_FIELD_VALUE;

	public static final int CAPABILITY_GRANTED				= AuditDTO.CAPABILITY_GRANTED;
	public static final int CAPABILITY_RESCINDED			= AuditDTO.CAPABILITY_RESCINDED;

	public static final int ADDED_USER_TO_GROUP				= AuditDTO.ADDED_USER_TO_GROUP;
	public static final int REMOVED_USER_FROM_GROUP			= AuditDTO.REMOVED_USER_FROM_GROUP;
	public static final int UPDATE_CONTAINER				= AuditDTO.UPDATE_CONTAINER;
	public static final int UPDATE_GROUP					= AuditDTO.UPDATE_GROUP;
	public static final int UPDATE_LDAP						= AuditDTO.UPDATE_LDAP;
	public static final int UPDATE_USER						= AuditDTO.UPDATE_USER;
	public static final int USER_TOMBSTONED					= AuditDTO.USER_TOMBSTONED;
	public static final int USER_UNTOMBSTONED				= AuditDTO.USER_UNTOMBSTONED;
	public static final int UPDATE_SMTP_CONFIG				= AuditDTO.UPDATE_SMTP_CONFIG;
	public static final int UPDATE_CAPABILITY				= AuditDTO.UPDATE_CAPABILITY;
	public static final int UPDATE_PASSWORD_RESET			= AuditDTO.UPDATE_PASSWORD_RESET;
	public static final int UPDATE_ITEM						= AuditDTO.UPDATE_ITEM;
	public static final int UPDATE_USER_DEFINED_TYPE		= AuditDTO.UPDATE_USER_DEFINED_TYPE;
	public static final int UPDATE_USER_DEFINED_FIELD		= AuditDTO.UPDATE_USER_DEFINED_FIELD;
	public static final int UPDATE_USER_DEFINED_FIELD_VALUE	= AuditDTO.UPDATE_USER_DEFINED_FIELD_VALUE;
	public static final int USER_RESTORED					= AuditDTO.USER_RESTORED;
	public static final int USER_LOCKED						= AuditDTO.USER_LOCKED;
	public static final int USER_UNLOCKED					= AuditDTO.USER_UNLOCKED;
	public static final int RENAMED_ITEM					= AuditDTO.RENAMED_ITEM;

	public static final int DELETE_CONTAINER 				= AuditDTO.DELETE_CONTAINER;
	public static final int DELETE_GROUP					= AuditDTO.DELETE_GROUP;
	public static final int DELETE_LDAP						= AuditDTO.DELETE_LDAP;
	public static final int DELETE_USER 					= AuditDTO.DELETE_USER;
	public static final int DELETE_SMTP_CONFIG 				= AuditDTO.DELETE_SMTP_CONFIG;
	public static final int DELETE_CAPABILITY	 			= AuditDTO.DELETE_CAPABILITY;
	public static final int DELETE_PASSWORD_RESET	 		= AuditDTO.DELETE_PASSWORD_RESET;
	public static final int DELETE_ITEM	 					= AuditDTO.DELETE_ITEM;
	public static final int DELETE_USER_DEFINED_TYPE		= AuditDTO.DELETE_USER_DEFINED_TYPE;
	public static final int DELETE_USER_DEFINED_FIELD		= AuditDTO.DELETE_USER_DEFINED_FIELD;
	public static final int DELETE_USER_DEFINED_FIELD_VALUE	= AuditDTO.DELETE_USER_DEFINED_FIELD_VALUE;
	public static final int DELETE_USER_DEFINED_TYPE_VALUE 	= AuditDTO.DELETE_USER_DEFINED_TYPE_VALUE;
	public static final int DELETE_NOTIFICATION				= AuditDTO.DELETE_NOTIFICATION;
	public static final int DELETE_DOCUMENT					= AuditDTO.DELETE_DOCUMENT;

	public static final int SHARE_ITEM  					= AuditDTO.SHARE_ITEM;
	public static final int REVOKE_ITEM						= AuditDTO.REVOKE_ITEM;
	public static final int REVOKE_ITEM_FROM_GROUP			= AuditDTO.REVOKE_ITEM_FROM_GROUP;
	public static final int SHARE_CONTAINER_WITH_GROUP		= AuditDTO.SHARE_CONTAINER_WITH_GROUP;
	public static final int SHARE_CONTAINER_WITH_USER		= AuditDTO.SHARE_CONTAINER_WITH_USER;
	public static final int REVOKE_CONTAINER_FROM_USER		= AuditDTO.REVOKE_CONTAINER_FROM_USER;
	public static final int REVOKE_CONTAINER_FROM_GROUP		= AuditDTO.REVOKE_CONTAINER_FROM_GROUP;
	public static final int ITEM_OWNERSHIP_TRANSFER  		= AuditDTO.ITEM_OWNERSHIP_TRANSFER;
	public static final int CONTAINER_OWNERSHIP_TRANSFER  	= AuditDTO.CONTAINER_OWNERSHIP_TRANSFER;
	public static final int ITEM_RELINQUISHED		  		= AuditDTO.ITEM_RELINQUISHED;
	public static final int CONTAINER_RELINQUISHED			= AuditDTO.CONTAINER_RELINQUISHED;
	public static final int GRANT_ITEM_READ_ONLY			= AuditDTO.GRANT_ITEM_READ_ONLY;
	public static final int GRANT_ITEM_OWNERSHIP			= AuditDTO.GRANT_ITEM_OWNERSHIP;
	public static final int GRANT_ITEM_UPDATE				= AuditDTO.GRANT_ITEM_UPDATE;
	public static final int GRANT_ITEM_UPDATE_AND_OWNERSHIP = AuditDTO.GRANT_ITEM_UPDATE_AND_OWNERSHIP;
	
	public static final int PASSWORD_EXPIRED				= AuditDTO.PASSWORD_EXPIRED;
	public static final int PASSPHRASE_RESET_REQUEST		= AuditDTO.PASSPHRASE_RESET_REQUEST;
	public static final int SENT_PASSPHRASE_RESET_EMAIL		= AuditDTO.SENT_PASSPHRASE_RESET_EMAIL;
	public static final int TWO_FACTOR_CODE_NOT_SUPPLIED	= AuditDTO.TWO_FACTOR_CODE_NOT_SUPPLIED;
	public static final int TWO_FACTOR_NOT_ENABLED			= AuditDTO.TWO_FACTOR_NOT_ENABLED;
	public static final int PASSWORD_RESET_REQUEST			= AuditDTO.PASSWORD_RESET_REQUEST;
	public static final int PASSWORD_RESET_REQUEST_IGNORED	= AuditDTO.PASSWORD_RESET_REQUEST_IGNORED;
	public static final int PASSWORD_RESET_IGNORED			= AuditDTO.PASSWORD_RESET_IGNORED;
	public static final int SENT_PASSWORD_RESET_EMAIL		= AuditDTO.SENT_PASSWORD_RESET_EMAIL;
	public static final int SENT_WARNING_EMAIL				= AuditDTO.SENT_WARNING_EMAIL;

	private static final Integer[] USER_ADMIN_ACTIONS			= {CREATE_USER, USER_UNLOCKED, USER_LOCKED, USER_RESTORED, USER_TOMBSTONED, UPDATE_USER, CAPABILITY_GRANTED, CAPABILITY_RESCINDED};
	private static final List<Integer> USER_ADMIN_ACTION_LIST	= asList(USER_ADMIN_ACTIONS);

	private static final Integer[] ITEM_ACTIONS					= {VIEW_ITEM, CREATE_ITEM, UPDATE_ITEM, DELETE_ITEM, SHARE_ITEM, REVOKE_ITEM, ITEM_OWNERSHIP_TRANSFER, ITEM_RELINQUISHED};
	private static final List<Integer> ITEM_ACTION_LIST 		= asList(ITEM_ACTIONS);

	private static final Integer[] CONTAINER_ACTIONS			= {CREATE_CONTAINER, UPDATE_CONTAINER, DELETE_CONTAINER, SHARE_CONTAINER_WITH_GROUP, SHARE_CONTAINER_WITH_USER, REVOKE_CONTAINER_FROM_USER, REVOKE_CONTAINER_FROM_GROUP, CONTAINER_RELINQUISHED};
	private static final List<Integer> CONTAINER_ACTION_LIST	= asList(CONTAINER_ACTIONS);

	private static final Integer[] GROUP_ACTIONS				= {ADDED_USER_TO_GROUP, REMOVED_USER_FROM_GROUP, CREATE_GROUP, UPDATE_GROUP, DELETE_GROUP, SHARE_CONTAINER_WITH_GROUP, REVOKE_CONTAINER_FROM_GROUP};
	private static final List<Integer> GROUP_ACTION_LIST 		= asList(GROUP_ACTIONS);

	private static final Integer[] CREATE_ACTIONS				= {CREATE_CONTAINER, CREATE_GROUP, CREATE_LDAP, CREATE_USER, CREATE_SMTP_CONFIG, CREATE_PASSWORD_RESET, CREATE_ITEM, CREATE_USER_DEFINED_TYPE, CREATE_USER_DEFINED_FIELD, CREATE_USER_DEFINED_FIELD_VALUE};
	private static final List<Integer> CREATE_ACTION_LIST		= asList(CREATE_ACTIONS);
	private static final List<Integer> CREATE_CONTAINER_LIST	= asList(CREATE_CONTAINER);
	private static final List<Integer> CREATE_ITEM_LIST			= asList(CREATE_ITEM);

	private static final Integer[] UPDATE_ACTIONS				= {UPDATE_CONTAINER, UPDATE_GROUP, UPDATE_LDAP, UPDATE_USER, UPDATE_SMTP_CONFIG, UPDATE_PASSWORD_RESET, UPDATE_CAPABILITY, UPDATE_ITEM, UPDATE_USER_DEFINED_TYPE, UPDATE_USER_DEFINED_FIELD, UPDATE_USER_DEFINED_FIELD_VALUE};
	private static final List<Integer> UPDATE_ACTION_LIST		= asList(UPDATE_ACTIONS);
	private static final List<Integer> UPDATE_CONTAINER_LIST	= asList(UPDATE_CONTAINER);
	private static final List<Integer> UPDATE_ITEM_LIST			= asList(UPDATE_ITEM);

	private static final Integer[] DELETE_ACTIONS				= {DELETE_CONTAINER, DELETE_GROUP, DELETE_LDAP, DELETE_USER, DELETE_SMTP_CONFIG, DELETE_CAPABILITY, DELETE_PASSWORD_RESET, DELETE_ITEM, DELETE_USER_DEFINED_TYPE, DELETE_USER_DEFINED_FIELD, DELETE_USER_DEFINED_FIELD_VALUE, DELETE_USER_DEFINED_TYPE_VALUE, DELETE_NOTIFICATION};
	private static final List<Integer> DELETE_ACTION_LIST		= asList(DELETE_ACTIONS);
	private static final List<Integer> DELETE_CONTAINER_LIST	= asList(DELETE_CONTAINER);
	private static final List<Integer> DELETE_ITEM_LIST			= asList(DELETE_ITEM);
	private static final List<Integer> DELETE_GROUP_LIST		= asList(DELETE_GROUP);

	private static final Integer[] REVOKE_ACTIONS				= {REVOKE_ITEM, REVOKE_CONTAINER_FROM_USER, REVOKE_CONTAINER_FROM_GROUP};
	private static final List<Integer> REVOKE_ACTION_LIST		= asList(REVOKE_ACTIONS);
	private static final List<Integer> REVOKE_CONTAINER_LIST	= asList(REVOKE_CONTAINER_FROM_USER, REVOKE_CONTAINER_FROM_GROUP);
	private static final List<Integer> REVOKE_ITEM_LIST			= asList(REVOKE_ITEM);

	private static final Integer[] SHARE_ACTIONS				= {SHARE_ITEM, SHARE_CONTAINER_WITH_GROUP, SHARE_CONTAINER_WITH_USER};
	private static final List<Integer> SHARE_ACTION_LIST		= asList(SHARE_ACTIONS);
	private static final List<Integer> SHARE_CONTAINER_LIST		= asList(SHARE_CONTAINER_WITH_GROUP, SHARE_CONTAINER_WITH_USER);
	private static final List<Integer> SHARE_ITEM_LIST			= asList(SHARE_ITEM);

	private static final Integer[] RELINQUISH_ACTIONS			= {ITEM_RELINQUISHED, CONTAINER_RELINQUISHED};
	private static final List<Integer> RELINQUISH_ACTION_LIST	= asList(RELINQUISH_ACTIONS);
	private static final List<Integer> RELINQUISH_CONTAINER_LIST= asList(CONTAINER_RELINQUISHED);
	private static final List<Integer> RELINQUISH_ITEM_LIST		= asList(ITEM_RELINQUISHED);

	private static final Integer[] SYSTEM_ACTIONS				= {LICENSE_INSTALLED, UPDATE_SYSTEM_CONFIG};
	private static final List<Integer> SYSTEM_ACTION_LIST		= asList(SYSTEM_ACTIONS);

	private static final Integer[] LOGIN_ACTIONS				= {LOGIN, LOGOUT, FAILED_LOGIN_ATTEMPT};
	private static final List<Integer> LOGIN_ACTION_LIST		= asList(LOGIN_ACTIONS);

	private static final List<Integer> USER_LOCKED_LIST			= asList(USER_LOCKED);
	private static final List<Integer> USER_UNLOCKED_LIST		= asList(USER_UNLOCKED);
	private static final List<Integer> USER_TOMBSTONED_LIST		= asList(USER_TOMBSTONED);
	private static final List<Integer> USER_RESTORED_LIST		= asList(USER_RESTORED);

	private static final Integer[] PASSWORD_ACTIONS				= {PASSWORD_EXPIRED, PASSPHRASE_RESET_REQUEST, SENT_PASSPHRASE_RESET_EMAIL, PASSWORD_RESET_REQUEST, PASSWORD_RESET_REQUEST_IGNORED, PASSWORD_RESET_IGNORED, SENT_PASSWORD_RESET_EMAIL};
	private static final List<Integer> PASSWORD_ACTION_LIST		= asList(PASSWORD_ACTIONS);

	private static final Map<String, List<Integer>> actionMapper = new HashMap<>(32);

	static {
		actionMapper.put("item",				ITEM_ACTION_LIST);
		actionMapper.put("container",			CONTAINER_ACTION_LIST);
		actionMapper.put("group",				GROUP_ACTION_LIST);
		actionMapper.put("user",				USER_ADMIN_ACTION_LIST);
		actionMapper.put("delete",				DELETE_ACTION_LIST);
		actionMapper.put("delete item",			DELETE_ITEM_LIST);
		actionMapper.put("delete container",	DELETE_CONTAINER_LIST);
		actionMapper.put("create",				CREATE_ACTION_LIST);
		actionMapper.put("create item",			CREATE_ITEM_LIST);
		actionMapper.put("create container",	CREATE_CONTAINER_LIST);
		actionMapper.put("update",				UPDATE_ACTION_LIST);
		actionMapper.put("update item",			UPDATE_ITEM_LIST);
		actionMapper.put("update container",	UPDATE_CONTAINER_LIST);
		actionMapper.put("delete",				DELETE_ACTION_LIST);
		actionMapper.put("delete item",			DELETE_ITEM_LIST);
		actionMapper.put("delete container",	DELETE_CONTAINER_LIST);
		actionMapper.put("delete group",		DELETE_GROUP_LIST);
		actionMapper.put("revoke",				REVOKE_ACTION_LIST);
		actionMapper.put("revoke item",			REVOKE_ITEM_LIST);
		actionMapper.put("revoke container",	REVOKE_CONTAINER_LIST);
		actionMapper.put("share",				SHARE_ACTION_LIST);
		actionMapper.put("share item",			SHARE_ITEM_LIST);
		actionMapper.put("share container",		SHARE_CONTAINER_LIST);
		actionMapper.put("relinquish",			RELINQUISH_ACTION_LIST);
		actionMapper.put("relinquish item",		RELINQUISH_ITEM_LIST);
		actionMapper.put("relinquish container",RELINQUISH_CONTAINER_LIST);
		actionMapper.put("system",				SYSTEM_ACTION_LIST);
		actionMapper.put("login",				LOGIN_ACTION_LIST);
		actionMapper.put("lock user",			USER_LOCKED_LIST);
		actionMapper.put("unlock user",			USER_UNLOCKED_LIST);
		actionMapper.put("tombstone user",		USER_TOMBSTONED_LIST);
		actionMapper.put("restore user",		USER_RESTORED_LIST);
		actionMapper.put("password management",	PASSWORD_ACTION_LIST);
	}

	public static final Collection<Integer> getIntendedActions(final String action) {
		return actionMapper.get(action);
	}

	private Long id;
	private Date createdAt;
	private Integer action;
	private Long userId;
	private Long objectId;
	private Long recipientId;
	private Long groupId;
	private Long newItemId;
	private String username;
	private String objectName;
	private String fullname;
	private String recipientUsername;
	private String recipientFullname;
	private String groupName;
	private transient Supplier<String> objectNameSupplier;
	private String details;

	public static final String action(final int actionCode) {
		switch(actionCode) {
		case EXCEPTION:					return "generated exception";
		case META_EXCEPTION:			return "generated exception while processing audit";
		case ITEM_GROUP_EXCEPTION:		return "generated exception while sharing item with grop";
		case PASS_RESET_EXCEPTION:		return "generated exception while resetting a password";
		case SUBVERSIVE_ITEM_VIEW:		return "attempted to view an item they do not own";
		case SUBVERSIVE_CONTAINER_VIEW:	return "attempted to view a container they do not own";

		case LOGIN:						return "logged in";
		case LOGOUT:					return "logged out";
		case FAILED_LOGIN_ATTEMPT:		return "attempted to log in";

		case CREATE_USER:				return "created user";
		case CREATE_ITEM:				return "created item";
		case CREATE_GROUP:				return "created group";
		case CREATE_LDAP:				return "created LDAP";

		case UPDATE_USER:				return "updated user";
		case UPDATE_ITEM:				return "updated item";
		case UPDATE_GROUP:				return "updated group";
		case UPDATE_LDAP:				return "updated LDAP";
		case ADDED_USER_TO_GROUP:		return "added user to group";
		case REMOVED_USER_FROM_GROUP:	return "removed user from group";
		case USER_TOMBSTONED:			return "user marked as deleted";

		case DELETE_USER:				return "deleted user";
		case DELETE_ITEM:				return "deleted item";
		case DELETE_GROUP:				return "deleted group";
		case DELETE_LDAP:				return "deleted LDAP";

		case SHARE_ITEM:				return "shared item";
		case REVOKE_ITEM:				return "revoked item";
		default:						return "unknown";
		}
	}

	@Override
	public void clear() {
		id = null;
		createdAt = null;
		action = null;
		userId =
		objectId =
		newItemId = null;
		username = objectName = null;
		objectNameSupplier = null;
		details = null;
	}

	public String getActionStr() {
		return action(action);
	}

	public Audit() {}

	private static String truncatedString(final String s) {
		return (s == null || s.length() < 256) ? s : s.substring(0, 255);
	}

	private void setUserDetails(final User user) {
		this.username = user == null ? null : user.getName();
		this.fullname = user == null ? null : user.getFullname();
		this.userId = user == null ? null : user.getId();
	}

	private void setRecipientDetails(final User user) {
		this.setRecipientUsername(user == null ? null : user.getName());
		this.recipientFullname = user == null ? null : user.getFullname();
		this.recipientId = user == null ? null : user.getId();
	}

	public Audit(final int action, final User user, final Object object, final Long objectId, final Long newItemId, final String details) {
		setUserDetails(user);
		this.action = action;
		this.username = user == null ? null : user.getName();
		this.fullname = user == null ? null : user.getFullname();
		this.userId = user == null ? null : user.getId();
		this.objectName = object == null ? null : object.toString();
		this.objectId = objectId;
		this.newItemId = newItemId;
		this.details = truncatedString(details);
	}

	public Audit(final int action, final User user, final Long objectId, final User recipient, final Object object, final String details) {
		setUserDetails(user);
		setRecipientDetails(recipient);
		this.action = action;
		this.objectName = object == null ? null : object.toString();
		this.objectId = objectId;
		this.details = truncatedString(details);
	}

	public Audit(final int action, final User user, final Long objectId, final User recipient, final Object object) {
		setUserDetails(user);
		setRecipientDetails(recipient);
		this.action = action;
		this.objectName = object == null ? null : object.toString();
		this.objectId = objectId;
		this.details = null;
	}

	public Audit(final int action, final User user, final Long objectId, final Group group, final Object object) {
		setUserDetails(user);
		this.setGroupId(group.getId());
		this.setGroupName(group.getName());
		this.action = action;
		this.objectName = object == null ? null : object.toString();
		this.objectId = objectId;
		this.details = null;
	}

	public Audit(final int action, final User user, final Supplier<String> objectNameSupplier, final Long objectId, final Long newItemId, final String details) {
		setUserDetails(user);
		this.action = action;
		this.objectNameSupplier = objectNameSupplier;
		this.objectName = null;
		this.objectId = objectId;
		this.newItemId = newItemId;
		this.details = truncatedString(details);
	}

	public boolean isCreateAction() {
		return action == CREATE_USER || action == CREATE_ITEM;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	@Override
	public void setCreatedAt(final Date createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getAction() {
		return action;
	}

	public void setAction(Integer action) {
		this.action = action;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Long getNewItemId() {
		return newItemId;
	}

	public void setNewItemId(Long newItemId) {
		this.newItemId = newItemId;
	}

	public Long getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(Long recipientId) {
		this.recipientId = recipientId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username == null ? null : username.trim();
	}

	/**
	 * Get the object name, but if objectName is null and a NameSupplier has been specified, set the objectName first.
	 *
	 */
	public String getObjectName() {
		if (objectName == null && objectNameSupplier != null) {
			objectName = objectNameSupplier.get();
		}

		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName == null ? null : objectName.trim();
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details == null ? null : details.trim();
	}

	@Override
	public void setUpdatedAt(Date updatedAt) { /* we do not update audit records */ }

	@Override
	public void setVersion(Integer version) { /* we do not update audit records */ }

	@Override
	public String getName() { return null; }

	@Override
	public Date getUpdatedAt() { // we never update
		return null;
	}

	@Override
	public Integer getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRecipientFullname() {
		return recipientFullname;
	}

	public void setRecipientFullname(String recipientFullname) {
		this.recipientFullname = recipientFullname;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getRecipientUsername() {
		return recipientUsername;
	}

	public void setRecipientUsername(String recipientUsername) {
		this.recipientUsername = recipientUsername;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
