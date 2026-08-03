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

public final class SharedContainerDTO extends ContainerDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long	ownerId;
	private String	username;
	private String	ownerFullname;
	private String	email;
	private String	phone;
	private Date	sharedAt;
	private Boolean addPermitted;
	private Boolean updatePermitted;
	private Boolean ownershipControl;
	private Boolean profilePictureEnabled;

	@Override
	public void clear() {
		profilePictureEnabled = addPermitted = updatePermitted = ownershipControl = null;
		sharedAt = null;
		phone = email = ownerFullname = username = null;
		ownerId = null;
		super.clear();
	}

	/**
	 * A method to determine if a user owns this container.
	 *
	 * @param userId
	 * @return true if the userId passed in is the same userId of the owner of the item, false otherwise.
	 */
	public boolean isOwner(final Long userId)	{ return ownerId.equals(userId); }
	public Long getOwnerId()					{ return ownerId; }
	public String getUsername()					{ return username; }
	public String getOwnerFullname()			{ return ownerFullname; }
	public String getEmail()					{ return email; }
	public String getPhone()					{ return phone; }
	public Date getSharedAt()					{ return sharedAt; }
	public Boolean getAddPermitted()			{ return addPermitted; }
	public Boolean getUpdatePermitted()			{ return updatePermitted; }
	public Boolean getOwnershipControl()		{ return ownershipControl; }
	public Boolean getProfilePictureEnabled()	{ return profilePictureEnabled; }
	public void setOwnerId(Long ownerId)						{ this.ownerId = ownerId; }
	public void setUsername(String username)					{ this.username = username; }
	public void setOwnerFullname(String ownerFullname)			{ this.ownerFullname = ownerFullname; }
	public void setEmail(String email)							{ this.email = email; }
	public void setPhone(String phone)							{ this.phone = phone; }
	public void setSharedAt(Date sharedAt)						{ this.sharedAt = sharedAt; }
	public void setAddPermitted(Boolean addPermitted)			{ this.addPermitted = addPermitted; }
	public void setUpdatePermitted(Boolean updatePermitted)		{ this.updatePermitted = updatePermitted; }
	public void setOwnershipControl(Boolean ownershipControl)	{ this.ownershipControl = ownershipControl; }
	public void setProfilePictureEnabled(Boolean profilePictureEnabled) { this.profilePictureEnabled = profilePictureEnabled; }
}
