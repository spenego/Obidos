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

import com.spenego.Obidos.shared.dto.PermissionDTO;

public final class ItemAssignment extends BaseModel implements Assignable, HasOwner, Model, Serializable {
	private static final long serialVersionUID = 1L;
	private Long itemId;
	private Boolean updatePermitted;
	private Boolean ownershipControl;
	private Boolean sharedExplicitly;	// item was shared explicitly with user as opposed to being shared via a group
	private Integer count;

	public ItemAssignment() {
		count = 1;
	}

	public ItemAssignment(final PermissionDTO dto) {
		this.updatePermitted  = dto.getMayUpdate();
		this.ownershipControl = dto.getHasOwnershipControl();
	}

	public ItemAssignment(final Long itemId, final Long userId, final Boolean isOwner, final Boolean sharedExplicitly) {
		super(null, userId);
		this.itemId = itemId;
		count = 1;
		updatePermitted = ownershipControl = isOwner;
		this.sharedExplicitly = sharedExplicitly;
	}

	public ItemAssignment(final Long id) {
		super(id);
		count = 1;
	}

	public ItemAssignment(final Long id, final Boolean isOwner) {
		this(id);
		updatePermitted = ownershipControl = isOwner;
	}

	public ItemAssignment(final Boolean sharedExplicitly, final Long id ) {
		super(id);
		this.sharedExplicitly = sharedExplicitly;
	}

	@Override
	public boolean equals(final Object o) {
		return (o == this) ? true : (o == null || !(o instanceof ItemAssignment)) ? false : ((ItemAssignment) o).getId().equals(getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public Long getReferredId() {
		return itemId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Integer getVersion() {
		return null;
	}

	@Override
	public void setVersion(Integer version) { /* Assignment records do not have version columns */ }

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
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

	public Boolean getSharedExplicitly() {
		return sharedExplicitly;
	}

	public void setSharedExplicitly(Boolean sharedExplicitly) {
		this.sharedExplicitly = sharedExplicitly;
	}
}
