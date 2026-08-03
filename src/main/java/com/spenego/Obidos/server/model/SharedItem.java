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

public final class SharedItem extends Item implements Clearable, Serializable, LimitedItem {
	private static final long serialVersionUID = 1L;

	private Long	itemId;
	private Long	ownerId;
	private Long	containerAssignmentId;
	private String	ownerFullname;
	private String	username;
	private String	email;
	private String	phone;
	private Date	sharedAt;
	private Boolean updatePermitted;
	private Boolean sharePermitted;
	private Boolean sharedExplicitly;
	private Boolean ownershipControl;
	private Boolean profilePictureEnabled;

	@Override
	public void clear() {
		updatePermitted = sharePermitted = ownershipControl = null;
		sharedAt = null;
		phone = email = username = ownerFullname = null;
		ownerId = itemId = null;
		super.clear();
	}

	@Override
	public String toString()					{ return "Shared Item " + getName(); }

	@Override
	public boolean	ownerIs(final User user)	{ return ownerId.equals(user.getId()); }
	public String	getOwnerFullname()			{ return ownerFullname; }
	public Long		getOwnerId()				{ return ownerId; }
	@Override
	public Long		getItemId()					{ return itemId; }
	public String	getUsername()				{ return username; }
	public String	getEmail()					{ return email; }
	public String	getPhone()					{ return phone; }
	public Date		getSharedAt()				{ return sharedAt; }
	public Boolean	getUpdatePermitted()		{ return updatePermitted; }
	public Boolean	getSharePermitted()			{ return sharePermitted; }
	public Boolean	getOwnershipControl()		{ return ownershipControl; }
	public Boolean	getProfilePictureEnabled()	{ return profilePictureEnabled; }
	public Boolean	getSharedExplicitly()		{ return sharedExplicitly; }
	@Override
	public Long		getContainerAssignmentId()	{ return containerAssignmentId; }

	public void setOwnerId(Long ownerId)						{ this.ownerId = ownerId; }
	public void setItemId(Long itemId)							{ this.itemId = itemId; }
	public void setUsername(String username)					{ this.username = username; }
	public void setEmail(String email)							{ this.email = email; }
	public void setPhone(String phone)							{ this.phone = phone; }
	public void setSharedAt(Date sharedAt)						{ this.sharedAt = sharedAt; }
	public void setOwnerFullname(String ownerFullname)			{ this.ownerFullname = ownerFullname; }
	public void setUpdatePermitted(Boolean updatePermitted)		{ this.updatePermitted = updatePermitted; }
	public void setSharePermitted(Boolean sharePermitted)		{ this.sharePermitted = sharePermitted; }
	public void setOwnershipControl(Boolean ownershipControl)	{ this.ownershipControl = ownershipControl; }
	@Override
	public void setContainerAssignmentId(Long containerAssignmentId) { this.containerAssignmentId = containerAssignmentId; }
	public void setProfilePictureEnabled(Boolean profilePictureEnabled) { this.profilePictureEnabled = profilePictureEnabled; }
}
