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

import java.io.Serializable;
import java.util.Date;

import com.spenego.Obidos.shared.dto.Clearable;

/**
 * Some sneaky tricks are employed to get objects of this class to function as desired.
 *
 * 1 - ownerFullname is read only. It gets generated within the select
 *     call in the database (via several joins). You can't populate the database with those values.
 *
 * 2 - When loaded from the database, itemId is the user's Item Assignment ID (not the item ID proper).
 *     Likewise, containerId is the Container Assignment ID, not the container ID proper.
 *     This is regardless of the fact that the database actually contains the item ID or Container ID proper.
 *     We do this so that the Notifications that are delivered to the client contain IDs that can be used
 *     to calls the the appropriate get methods.
 *
 * @author mmorgan
 *
 */
public final class Notification extends NamedModel implements Clearable, Serializable, Model {
	private static final long serialVersionUID = 1L;

	private Long ownerId;
	private Long targetId;
	private Boolean unread;
	private Integer action;
	private String message;
	private String ownerFullname;

	public Notification() { }

	public Notification(final Long id, final Date createdAt) {
		super(id, createdAt);
	}

	public Notification(final Boolean unread) {
		this.unread = unread;
	}

	public Notification(final Long ownerId, final Long userId, final Long targetId, final String name, final Integer action, final String message) {
		super(null, userId, name);
		this.ownerId = ownerId;
		this.targetId = targetId;
		this.action = action;
		this.unread = true;
		this.message = message == null || message.isEmpty() ? null : message;
	}

	public Notification(final Long userId, final Integer action) {
		super(null, userId);
		this.ownerId = null;
		this.action = action;
		this.unread = true;
		this.message = null;
	}

	@Override
	public void clear() {
		ownerFullname = message = null;
		action = null;
		unread = null;
		ownerId = targetId = null;
		super.clear();
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Integer getAction() {
		return action;
	}

	public void setAction(Integer action) {
		this.action = action;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message == null ? null : message.trim();
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

	public void setOwnerFullname(final String ownerFullname) {
		this.ownerFullname = ownerFullname;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
}
