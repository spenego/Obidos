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

public final class ContainerGroupAssignment implements Model, Serializable {
	private Long id;
	private Long containerId;
	private Long groupId;
	private Boolean updatePermitted;
	private Boolean ownershipControl;
	private Boolean addPermitted;
	private static final long serialVersionUID = 1L;

	public ContainerGroupAssignment() {
		updatePermitted = ownershipControl = addPermitted = Boolean.FALSE;
	}

	public ContainerGroupAssignment(final PermissionDTO permissions) {
		updatePermitted = permissions.getMayUpdate();
		addPermitted = permissions.getMayAdd();
		ownershipControl = permissions.getHasOwnershipControl();
	}

	public ContainerGroupAssignment(final Long containerId, final Long groupId) {
		this.containerId = containerId;
		this.groupId = groupId;
		updatePermitted = addPermitted = ownershipControl = Boolean.FALSE;
	}

	public Long getId()								{ return id; }
	public void setId(Long id)						{ this.id = id; }
	public Long getContainerId()					{ return containerId; }
	public void setContainerId(Long containerId)	{ this.containerId = containerId; }
	public Long getGroupId()						{ return groupId; }
	public void setGroupId(Long groupId)			{ this.groupId = groupId; }

	@Override
	public Long getUserId() { return null; }

	@Override
	public String getName() { return null; }

	@Override
	public Date getUpdatedAt() { return null; }

	@Override
	public Date getCreatedAt() { return null; }

	@Override
	public Integer getVersion() { return null; }

	@Override
	public void setCreatedAt(Date createdAt) { /* record has no created_at column */ }

	@Override
	public void setUpdatedAt(Date updatedAt) { /* record has no updated_at column */ }

	@Override
	public void setVersion(Integer version) { /* record has no version column */ }
	public Boolean getAddPermitted() { return addPermitted; }
	public void setAddPermitted(Boolean addPermitted)		{ this.addPermitted = addPermitted; }
	public Boolean getUpdatePermitted()						{ return updatePermitted; }
	public void setUpdatePermitted(Boolean updatePermitted) { this.updatePermitted = updatePermitted; }
	public Boolean getOwnershipControl()					{ return ownershipControl; }
	public void setOwnershipControl(Boolean ownershipControl) { this.ownershipControl = ownershipControl; }
}