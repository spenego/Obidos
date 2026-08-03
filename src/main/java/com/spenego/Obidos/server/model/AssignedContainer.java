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

import java.util.Date;

import com.spenego.Obidos.shared.dto.Clearable;

public final class AssignedContainer extends BaseModel implements Clearable {
	private static final long serialVersionUID = 1L;
	private Long containerId;
	private Long ownerId;
	private String name;
	private String ownerFullname;
	private String email;
	private String phone;
	private Boolean isPrivate;
	private Boolean shared;
	private Boolean viewFlag;
	private Boolean shareFlag;
	private Boolean modifyFlag;
	private Boolean addPermitted;
	private Boolean updatePermitted;
	private Boolean ownershipControl;
	private Boolean profilePictureEnabled;
	private Date sharedAt;

	@Override
	public void clear() {
		modifyFlag = shareFlag = viewFlag = shared = isPrivate = updatePermitted = addPermitted = ownershipControl = null;
		ownerFullname = name = null;
		ownerId = containerId = null;
		super.clear();
	}

	@Override
	public String toString()					{ return "Assigned Container " + getName(); }
	public String getName()						{ return name; }
	public Long getContainerId()				{ return containerId; }
	public Long getOwnerId()					{ return ownerId; }
	public Date getSharedAt()					{ return sharedAt; }
	public String getEmail()					{ return email; }
	public String getPhone()					{ return phone; }
	public String getOwnerFullname()			{ return ownerFullname; }
	public Boolean getViewFlag()				{ return viewFlag; }
	public Boolean getShareFlag()				{ return shareFlag; }
	public Boolean getModifyFlag()				{ return modifyFlag; }
	public Boolean getIsPrivate()				{ return isPrivate; }
	public Boolean getShared()					{ return shared; }
	public Boolean getProfilePictureEnabled()	{ return profilePictureEnabled; }
	public Boolean getAddPermitted()			{ return addPermitted; }
	public Boolean getUpdatePermitted()			{ return updatePermitted; }
	public Boolean getOwnershipControl()		{ return ownershipControl; }
	public void setName(String name)					{ this.name = name == null ? null : name.trim(); }
	public void setContainerId(Long containerId)		{ this.containerId = containerId; }
	public void setOwnerFullname(String ownerFullname)	{ this.ownerFullname = ownerFullname == null ? null : ownerFullname.trim(); }
	public void setViewFlag(Boolean viewFlag)			{ this.viewFlag = viewFlag; }
	public void setShareFlag(Boolean shareFlag)			{ this.shareFlag = shareFlag; }
	public void setModifyFlag(Boolean modifyFlag)		{ this.modifyFlag = modifyFlag; }
	public void setOwnerId(Long ownerId)				{ this.ownerId = ownerId; }
	public void setIsPrivate(Boolean isPrivate)			{ this.isPrivate = isPrivate; }
	public void setShared(Boolean shared)				{ this.shared = shared; }
	public void setSharedAt(Date sharedAt)				{ this.sharedAt = sharedAt; }
	public void setEmail(String email)					{ this.email = email; }
	public void setPhone(String phone)					{ this.phone = phone; }
	public void setAddPermitted(Boolean addPermitted)					{ this.addPermitted = addPermitted; }
	public void setUpdatePermitted(Boolean updatePermitted)				{ this.updatePermitted = updatePermitted; }
	public void setOwnershipControl(Boolean ownershipControl)			{ this.ownershipControl = ownershipControl; }
	public void setProfilePictureEnabled(Boolean profilePictureEnabled)	{ this.profilePictureEnabled = profilePictureEnabled; } 
}
