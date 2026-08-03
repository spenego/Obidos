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

public final class ContainerAssignment extends BaseModel implements Assignable, HasOwner, Model, Serializable {
	private static final long serialVersionUID = 1L;
	private Long containerId;
	private Boolean viewFlag;
	private Boolean modifyFlag;
	private Boolean shareFlag;
	private Boolean sharedExplicitly;	// container was shared explicitly with user as opposed to being shared via a group
	private Boolean addPermitted;
	private Boolean updatePermitted;
	private Boolean ownershipControl;
	private Integer count;

	public ContainerAssignment() {}

	public ContainerAssignment(final PermissionDTO dto) {
		this.addPermitted  = dto.getMayAdd();
		this.updatePermitted  = dto.getMayUpdate();
		this.ownershipControl = dto.getHasOwnershipControl();
	}

	public ContainerAssignment(final Long id, final Boolean sharedExplicitly, final Boolean ownershipControl) {
		super(id);
		this.sharedExplicitly = sharedExplicitly;
		this.ownershipControl = ownershipControl;
	}

	public ContainerAssignment(final Long id, final Boolean sharedExplicitly, final Boolean updatePermitted, final Boolean ownershipControl) {
		super(id);
		this.sharedExplicitly = sharedExplicitly;
		this.updatePermitted = updatePermitted;
		this.ownershipControl = ownershipControl;
	}

	public ContainerAssignment(final Long userId, final Long containerId, final Boolean sharedExplicitly, final Boolean ownershipControl, final Boolean addPermitted, final Boolean updatePermitted) {
		super(null, userId);
		this.containerId		= containerId;
		this.viewFlag			= Boolean.TRUE;
		this.addPermitted		= addPermitted;
		this.updatePermitted	= updatePermitted;
		this.shareFlag			= Boolean.FALSE;
		this.sharedExplicitly	= sharedExplicitly;
		this.ownershipControl	= ownershipControl;
		this.modifyFlag			= Boolean.FALSE;
		this.setCount(1);
	}

	public Long getContainerId() {
		return containerId;
	}

	@Override
	public Long getReferredId() {
		return getContainerId();
	}

	private boolean instanceofEquals(final Object o) {
		return (o instanceof ContainerAssignment) && ((ContainerAssignment) o).getId().equals(getId());
	}

	@Override
	public boolean equals(final Object o) {
		return (o == this) || instanceofEquals(o);
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	public void setContainerId(Long containerId) {
		this.containerId = containerId;
	}

	public Boolean getViewFlag() {
		return viewFlag;
	}

	public void setViewFlag(Boolean viewFlag) {
		this.viewFlag = viewFlag;
	}

	public Boolean getModifyFlag() {
		return modifyFlag;
	}

	public void setModifyFlag(Boolean modifyFlag) {
		this.modifyFlag = modifyFlag;
	}

	public Boolean getShareFlag() {
		return shareFlag;
	}

	public void setShareFlag(Boolean shareFlag) {
		this.shareFlag = shareFlag;
	}

	@Override
	public String getName() {
		return null;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Boolean getSharedExplicitly() {
		return sharedExplicitly;
	}

	public void setSharedExplicitly(Boolean sharedExplicitly) {
		this.sharedExplicitly = sharedExplicitly;
	}

	public Boolean getOwnershipControl() {
		return ownershipControl;
	}

	public void setOwnershipControl(Boolean ownershipControl) {
		this.ownershipControl = ownershipControl;
	}

	public Boolean getUpdatePermitted() {
		return updatePermitted;
	}

	public void setUpdatePermitted(Boolean updatePermitted) {
		this.updatePermitted = updatePermitted;
	}

	public Boolean getAddPermitted() {
		return addPermitted;
	}

	public void setAddPermitted(Boolean addPermitted) {
		this.addPermitted = addPermitted;
	}
}
