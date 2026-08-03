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

import com.spenego.Obidos.shared.dto.PermissionDTO;

public class ItemGroup implements Model, Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long itemId;
	private Long groupId;
	private Boolean sharedExplicitly;
	private Boolean updatePermitted;
	private Boolean ownershipControl;

	public ItemGroup(final PermissionDTO dto) {
		this.updatePermitted  = dto.getMayUpdate();
		this.ownershipControl = dto.getHasOwnershipControl();
	}

	public ItemGroup(final Long itemId, final Long groupId) {
		this.itemId = itemId;
		this.groupId = groupId;
		updatePermitted = ownershipControl = Boolean.FALSE;
	}

	public ItemGroup(final Long itemId, final Long groupId, final Boolean sharedExplicitly) {
		this.itemId = itemId;
		this.groupId = groupId;
		this.sharedExplicitly = sharedExplicitly;
		updatePermitted = ownershipControl = Boolean.FALSE;
	}

	public ItemGroup() {
		updatePermitted = ownershipControl = Boolean.FALSE;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	@Override
	public Integer getVersion() {
		return null;
	}

	@Override
	public void setVersion(Integer version) { /* joined record types do not have this record */ }

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Date getUpdatedAt() {
		return null;
	}

	@Override
	public Date getCreatedAt() {
		return null;
	}

	@Override
	public void setCreatedAt(final Date createdAt)  { /* joined record types do not have this record */ }

	@Override
	public void setUpdatedAt(final Date updatedAt)  { /* joined record types do not have this record */ }

	@Override
	public Long getUserId() {
		return null;
	}

	public Boolean getSharedExplicitly() {
		return sharedExplicitly;
	}

	public void setSharedExplicitly(Boolean sharedExplicitly) {
		this.sharedExplicitly = sharedExplicitly;
	}

	public Boolean getUpdatePermitted() {
		return updatePermitted;
	}

	public void setUpdatePermitted(Boolean updatePermitted) {
		this.updatePermitted = updatePermitted;
	}

	public Boolean getOwnershipControl() {
		return ownershipControl;
	}

	public void setOwnershipControl(Boolean ownershipControl) {
		this.ownershipControl = ownershipControl;
	}
}