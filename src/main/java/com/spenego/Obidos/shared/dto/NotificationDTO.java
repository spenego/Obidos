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
import java.util.HashMap;
import java.util.Map;

import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.Selectable;

public final class NotificationDTO extends BaseDTO implements HasCreatedAt, HasId, Clearable, Serializable, Selectable {
	private static final long serialVersionUID = 1L;

	public static final int MESSAGE_FROM_SYSTEM			= 0;

	public static final int ITEM_START					= 100;
	public static final int ITEM_SHARED					= 100;
	public static final int ITEM_REVOKED				= 101;
	public static final int ITEM_RELINQUISHED			= 102;
	public static final int ITEM_DELETED				= 103;
	public static final int ITEM_RENAMED				= 104;
	public static final int ITEM_OWNED					= 105;
	public static final int ITEM_UPDATED				= 106;
	public static final int NOTE_SHARED					= 107;
	public static final int NOTE_REVOKED				= 108;
	public static final int NOTE_RELINQUISHED			= 109;
	public static final int NOTE_DELETED				= 110;
	public static final int NOTE_RENAMED				= 111;
	public static final int NOTE_OWNED					= 112;
	public static final int NOTE_UPDATED				= 113;
	public static final int ITEM_DEPOSITED				= 114; // user deposited an Item into another user's Container
	public static final int NOTE_PERMISSION_GRANTED		= 115;
	public static final int ITEM_PERMISSION_GRANTED		= 116;
	public static final int ITEM_END					= 116;

	public static final int CONTAINER_START				= 200;
	public static final int CONTAINER_SHARED			= 200;
	public static final int CONTAINER_REVOKED			= 201;
	public static final int CONTAINER_RELINQUISHED		= 202;
	public static final int CONTAINER_DELETED			= 203;
	public static final int CONTAINER_RENAMED			= 204;
	public static final int CONTAINER_OWNED				= 205;
	public static final int CONTAINER_END				= 205;

	public static final int MULTIPLE_ITEMS_SHARED		= 300; // typically when a user adds a recipient to a group that is already sharing items
	public static final int PASSPHRASE_RESET			= 301;
	public static final int PASSWORD_RESET				= 302;
	public static final int CAPABILITY_GRANTED			= 303;
	public static final int CAPABILITY_RESCINDED		= 304;
	public static final int MULTIPLE_CONTAINERS_SHARED	= 305;
	public static final int PASSWORD_EXPIRING_SOON		= 306;

	private Integer		action;
	private Boolean		unread;
	private String		message;
	private String		ownerFullname;
	private String		name;
	private Long		ownerId;
	private Long		targetId;
	private Boolean		selected;	// set by Client to indicate that the user has selected this user

	public static String getNotificationTypeString(int action) {
		switch(action) {
		case MESSAGE_FROM_SYSTEM:		return "message from system";
		case ITEM_SHARED:				return "shared item";
		case ITEM_REVOKED:				return "revoked item";
		case CONTAINER_SHARED:			return "shared container";
		case CONTAINER_REVOKED:			return "revoked container";
		case ITEM_RELINQUISHED:			return "relinquished item";
		case CONTAINER_RELINQUISHED:	return "relinquished container";
		case MULTIPLE_ITEMS_SHARED:		return "shared multiple items";
		case MULTIPLE_CONTAINERS_SHARED:return "shared multiple containers";
		case PASSPHRASE_RESET:			return "reset passphrase";
		case PASSWORD_RESET:			return "reset password";
		case ITEM_DELETED:				return "deleted item";
		case CONTAINER_DELETED:			return "deleted container";
		case ITEM_RENAMED:				return "renamed item";
		case ITEM_UPDATED:				return "updated item";
		case CONTAINER_RENAMED:			return "container renamed";
		case ITEM_OWNED:				return "taken ownership of item";
		case CONTAINER_OWNED:			return "taken ownership of container";
		case CAPABILITY_GRANTED:		return "capability granted";
		case CAPABILITY_RESCINDED:		return "capability rescinded";
		case NOTE_SHARED:				return "shared note";
		case NOTE_REVOKED:				return "revoked note";
		case NOTE_RELINQUISHED:			return "relinquished note";
		case NOTE_DELETED:				return "deleted note";
		case NOTE_RENAMED:				return "renamed note";
		case NOTE_OWNED:				return "taken ownership of note";
		case NOTE_UPDATED:				return "updated note";
		case PASSWORD_EXPIRING_SOON:	return "password expiring soon";
		case ITEM_DEPOSITED:			return "deposited item ";
		case NOTE_PERMISSION_GRANTED:
		case ITEM_PERMISSION_GRANTED:	return "granted you permission to ";
		default:						return "unknown action";
		}
	}

	public Map<String, String> getWith() {
		final Map<String,String> with = new HashMap<>();

		switch(action) {
			case NOTE_OWNED:
			case NOTE_RENAMED:
			case NOTE_UPDATED:
			case NOTE_SHARED:
			case NOTE_PERMISSION_GRANTED:
			with.put(ObidosConstants.ITEM_TYPE, "notebook");
			with.put(ObidosConstants.ITEM_ID, targetId == null ? "Unknown target" : targetId.toString());
			break;

			case ITEM_OWNED:
			case ITEM_RENAMED:
			case ITEM_UPDATED:
			case ITEM_SHARED:
			case ITEM_PERMISSION_GRANTED:
			with.put(ObidosConstants.ITEM_ID, targetId == null ? "Unknown target" : targetId.toString());
			break;

			case CONTAINER_SHARED:
			case CONTAINER_RENAMED:
			with.put(ObidosConstants.CONTAINER_ID, targetId.toString());
			break;

			default:	return null;  // NOSONAR -- the GUI has code conditional on null
		}

		with.put(ObidosConstants.OWNERID,	ownerId.toString());
		with.put(ObidosConstants.TYPE,		ObidosConstants.SHARED_WITH_ME);
		with.put(ObidosConstants.PLACE,		NameTokens.NOTIFICATION_MESSAGE);

		return with;
	}

	@Override
	public void clear() {
		selected = null;
		targetId = ownerId = null;
		name = ownerFullname = message = null;
		unread = null;
		action = null;
		super.clear();
	}

	public String getNameToken() {
		switch(action) {
			case NotificationDTO.NOTE_SHARED:
			case NotificationDTO.NOTE_RENAMED:
			case NotificationDTO.NOTE_UPDATED:
			case NotificationDTO.NOTE_OWNED:
			case NotificationDTO.ITEM_SHARED:
			case NotificationDTO.ITEM_RENAMED:
			case NotificationDTO.ITEM_UPDATED:
			case NotificationDTO.ITEM_OWNED:					return NameTokens.VIEW_ITEM;
			case NotificationDTO.CONTAINER_SHARED:				return NameTokens.LIST_ITEMS_IN_SHARED_CONTAINER;
			case NotificationDTO.MULTIPLE_ITEMS_SHARED:			return NameTokens.LIST_ITEMS_SHARED_WITH_ME;
			case NotificationDTO.MULTIPLE_CONTAINERS_SHARED:	return NameTokens.LIST_CONTAINERS_SHARED_WITH_ME;
			default:											return null;
		}
	}

	public static boolean isItemAction(int action) {
		return action >= ITEM_START && action <= ITEM_END;
	}

	public static boolean isContainerAction(int action) {
		return action >= CONTAINER_START && action <= CONTAINER_END;
	}

	public String getMessage() {
		if (action == PASSWORD_EXPIRING_SOON) {
			return message;
		}

		final StringBuilder sb = new StringBuilder(128);

		if (ownerFullname != null) {
			sb.append(ownerFullname).append(" has ");
		}

		sb.append(getNotificationTypeString(action));

		if (name != null && !name.isEmpty()) {
			switch(action) {
			case NOTE_PERMISSION_GRANTED:
			case ITEM_PERMISSION_GRANTED:
				final String s = (action == ITEM_PERMISSION_GRANTED) ? " the item: " : " the note: ";
				sb.append(message).append(s).append(name);
				break;
			
			case ITEM_RENAMED:
			case MULTIPLE_ITEMS_SHARED:
			case MULTIPLE_CONTAINERS_SHARED:
				break;
			
			default:
				sb.append(": ").append(name);
			}
		}

		if (action != ITEM_PERMISSION_GRANTED && action != NOTE_PERMISSION_GRANTED) {
			if (message != null && !message.isEmpty()) {
				sb.append(": ").append(message);
			}
		}

		return sb.toString();
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getAction() {
		return action;
	}

	public void setAction(Integer action) {
		this.action = action;
	}

	public Boolean getUnread() {
		return unread;
	}

	public void setUnread(Boolean unread) {
		this.unread = unread;
	}

	public String getOwnerFullname() {
		return ownerFullname;
	}

	public void setOwnerFullname(String ownerFullname) {
		this.ownerFullname = ownerFullname;
	}

	public String getName() {
		return name;
	}

	public void setName(String itemName) {
		this.name = itemName;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	@Override
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	@Override
	public Boolean getSelected() {
		return selected;
	}
}
