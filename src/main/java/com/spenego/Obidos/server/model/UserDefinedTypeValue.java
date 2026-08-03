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

public final class UserDefinedTypeValue extends NamedModel implements Model, Serializable {
	private static final long serialVersionUID = 1L;

	private Long userDefinedTypeId;
	private Long itemAssignmentId;

	@Override
	public void clear() {
		itemAssignmentId = userDefinedTypeId = null;
		super.clear();
	}

	@Override
	public String toString() {
		return "User Defined Type Value " + getName();
	}

	public Long getUserDefinedTypeId() {
		return userDefinedTypeId;
	}

	public void setUserDefinedTypeId(Long userDefinedTypeId) {
		this.userDefinedTypeId = userDefinedTypeId;
	}

	public Long getItemAssignmentId() {
		return itemAssignmentId;
	}

	public void setItemAssignmentId(final Long itemAssignmentId) {
		this.itemAssignmentId = itemAssignmentId;
	}

	public UserDefinedTypeValue() {}

	public UserDefinedTypeValue(final Long userDefinedTypeId) {
		this.userDefinedTypeId = userDefinedTypeId;
	}

	public UserDefinedTypeValue(final Long userDefinedTypeId, final Long userId, final Long itemAssignmentId) {
		super((Long) null, userId);
		this.userDefinedTypeId = userDefinedTypeId;
		this.itemAssignmentId = itemAssignmentId;
	}

	public boolean ownerIs(final LimitedUser user) {
		return user.self(getUserId());
	}
}