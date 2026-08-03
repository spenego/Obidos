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

import com.spenego.Obidos.shared.dto.Clearable;

public class UserGroupCombo extends BaseModel implements Assignable, HasOwner, Model, Clearable, Serializable {
	private static final long serialVersionUID = 1L;

	private Character	type;		// u - user, g - group
	private String		name;
	private String		office;
	private String		email1;
	private Boolean		profilePictureEnabled;// only set for type u -- Users
	private Boolean		addPermitted;
	private Boolean		updatePermitted;
	private Boolean		ownershipControl;

	@Override
	public void clear() {
		addPermitted = updatePermitted = ownershipControl = profilePictureEnabled = null;
		email1 = office = name = null;
		type = null;
	}

	@Override
	public Long getReferredId()						{ return null; }
	@Override
	public String		getName()					{ return name; }
	public Character	getType()					{ return type; }
	public String		getOffice()					{ return office; }
	public String		getEmail1()					{ return email1; }
	public Boolean		getProfilePictureEnabled()	{ return profilePictureEnabled; }
	public Boolean		getAddPermitted()			{ return addPermitted; }
	public Boolean		getUpdatePermitted()		{ return updatePermitted; }
	public Boolean		getOwnershipControl()		{ return ownershipControl; }

	public void setName(String name)							{ this.name = name; }
	public void setType(Character type)							{ this.type = type; }
	public void setOffice(String office)						{ this.office = office; }
	public void setEmail1(String email1)						{ this.email1 = email1; }
	public void setAddPermitted(Boolean addPermitted)			{ this.addPermitted = addPermitted; }
	public void setUpdatePermitted(Boolean updatePermitted)		{ this.updatePermitted = updatePermitted; }
	public void setOwnershipControl(Boolean ownershipControl)	{ this.ownershipControl = ownershipControl; }
	public void setProfilePictureEnabled(Boolean profilePictureEnabled) { this.profilePictureEnabled = profilePictureEnabled; }
}
