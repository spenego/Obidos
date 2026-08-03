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

import com.spenego.Obidos.shared.Selectable;
/**
 * A Class the represents a user or group that a Container or Item is shared with.
 *
 * @author mmorgan
 *
 */
public final class UserGroupComboDTO  implements HasId, Clearable, Selectable, Serializable {
	private static final long serialVersionUID = 1L;
	private Long		id;
	private String		name;
	private String		office;		// only set for type u -- Users
	private String		email1;		// only set for type u -- Users
	private Character	type;
	private Boolean		selected;	// set by Client to indicate that the user has selected this user
	private Boolean		profilePictureEnabled;// only set for type u -- Users
	private Boolean		addPermitted;
	private Boolean		updatePermitted;
	private Boolean		ownershipControl;

	@Override
	public void clear() {
		updatePermitted = ownershipControl = profilePictureEnabled = selected = null;
		type = null;
		name = office = email1 = null;
		id = null;
	}

	@Override
	public Long			getId()						{ return id; }
	public String		getName()					{ return name; }
	public String		getFullname()				{ return getName(); }
	public Character	getType()					{ return type; }
	public String		getOffice()					{ return office; }
	public String		getEmail1()					{ return email1; }
	public Boolean		getProfilePictureEnabled()	{ return profilePictureEnabled; }
	public Boolean		getAddPermitted()			{ return addPermitted; }
	public Boolean		getUpdatePermitted()		{ return updatePermitted; }
	public Boolean		getOwnershipControl()		{ return ownershipControl; }
	public boolean		isUser()					{ return type == 'u'; }
	public boolean		isGroup()					{ return type == 'g'; }
	@Override
	public Boolean		getSelected()				{ return selected; }

	public void			setId(final Long id)			{ this.id = id; }
	public void			setName(final String name)		{ this.name = name; }
	public void			setType(final Character type)	{ this.type = type; }
	public void			setOffice(final String office)	{ this.office = office; }
	public void			setEmail1(final String email1)	{ this.email1 = email1; }
	public void			setAddPermitted(final Boolean addPermitted)			{ this.addPermitted = addPermitted; }
	public void			setUpdatePermitted(final Boolean updatePermitted)	{ this.updatePermitted = updatePermitted; }
	public void			setOwnershipControl(final Boolean ownershipControl) { this.ownershipControl = ownershipControl; }
	public void			setProfilePictureEnabled(final Boolean profilePictureEnabled) { this.profilePictureEnabled = profilePictureEnabled; }
	@Override
	public void			setSelected(Boolean selected) { this.selected = selected; }

}