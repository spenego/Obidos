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

public final class SharedItemDTO extends ItemDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long	ownerId;
	private String	username;
	private String	ownerFullname;
	private String	email;
	private String	phone;
	private Date	sharedAt;
	private Boolean updatePermitted;
	private Boolean sharedExplicitly;
	private Boolean ownershipControl;
	private Boolean profilePictureEnabled;

	@Override
	public void clear() {
		profilePictureEnabled = updatePermitted = ownershipControl = null;
		sharedAt = null;
		phone = email = ownerFullname = username = null;
		ownerId = null;
		super.clear();
	}

	/**
	 * A method to determine if a user owns this item.
	 *
	 * @param userId
	 * @return true if the userId passed in is the same userId of the owner of the item, false otherwise.
	 */
	public boolean isOwner(final Long userId)	{ return ownerId.equals(userId); }
	public boolean ownerId(final Long userId)	{ return ownerId.equals(userId); }
	public Long getOwnerId()					{ return ownerId; }
	public String getOwnerUsername()			{ return username; }
	public String getOwnerFullname()			{ return ownerFullname; }
	public String getEmail()					{ return email; }
	public String getPhone()					{ return phone; }
	public Date getSharedAt()					{ return sharedAt; }
	public Boolean getUpdatePermitted()			{ return updatePermitted; }
	public Boolean getOwnershipControl()		{ return ownershipControl; }
	public Boolean getProfilePictureEnabled()	{ return profilePictureEnabled; }
	public Boolean getSharedExplicitly()		{ return sharedExplicitly; }

	public void setOwnerId(final Long ownerId)					{ this.ownerId = ownerId; }
	public void setOwnerUsername(final String ownerUsername)	{ this.username = ownerUsername; }
	public void setOwnerFullname(final String ownerFullname)	{ this.ownerFullname = ownerFullname; }
	public void setEmail(final String email)					{ this.email = email; }
	public void setPhone(final String phone)					{ this.phone = phone; }
	public void setSharedAt(final Date sharedAt)				{ this.sharedAt = sharedAt; }
	public void setUpdatePermitted(Boolean updatePermitted)		{ this.updatePermitted = updatePermitted; }
	public void setOwnershipControl(Boolean ownershipControl)	{ this.ownershipControl = ownershipControl; }
	public void setSharedExplicitly(Boolean sharedExplicitly)	{ this.sharedExplicitly = sharedExplicitly; }
	public void setProfilePictureEnabled(Boolean profilePictureEnabled) { this.profilePictureEnabled = profilePictureEnabled; }

	@Override
	public PermissionDTO getPermissions() {
		if (super.getPermissions() == null) {
			setPermissions(new PermissionDTO(updatePermitted, ownershipControl));
		}
		return super.getPermissions();
	}
}
