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

public class ContainerDTO extends BaseDTO implements HasId, Selectable, Clearable, Serializable {
	private static final long serialVersionUID = 1L;
	public static final long NOTEBOOK_ID = 2000L;

	private Long userId;
	private Long parentId;
	private String name;
	private Boolean view;
	private Boolean modify;
	private Boolean isPrivate;
	private Boolean selected;
	private Boolean shared;
	private Boolean profilePictureEnabled;
	private int numberOfSystems;


	@Override
	public void clear() {
		numberOfSystems = 0;
		view = modify = isPrivate = shared = selected = profilePictureEnabled = null;
		name = null;
		userId = parentId = null;
		super.clear();
	}

	public Long		getUserId()					{ return userId; }
	public String	getName()					{ return name; }
	public Boolean	getView()					{ return view; }
	public Boolean	getModify()					{ return modify; }
	public Long		getParentId()				{ return parentId; }
	public Boolean	getIsPrivate()				{ return isPrivate; }
    public int		getNumberOfSystems()		{ return numberOfSystems; }
	public Boolean	getGroupShared()			{ return getShared(); }
	public Boolean	getShared()					{ return shared; }
	public Boolean getProfilePictureEnabled()	{ return profilePictureEnabled; }

	public void setUserId(final Long userId)			{ this.userId = userId; }
	public void setName(final String name)				{ this.name = name; }
	public void setView(final Boolean view)				{ this.view = view; }
	public void setModify(final Boolean modify)			{ this.modify = modify; }
	public void setParentId(final Long parentId)		{ this.parentId = parentId; }
	public void setIsPrivate(final Boolean isPrivate)	{ this.isPrivate = isPrivate; }
    public void setNumberOfSystems(int numberOfSystems) { this.numberOfSystems = numberOfSystems; }
	public void setShared(final Boolean shared)			{ this.shared = shared; }
	public void setProfilePictureEnabled(Boolean profilePictureEnabled) { this.profilePictureEnabled = profilePictureEnabled; }

	@Override
	public Boolean getSelected() {
		return selected;
	}

	@Override
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
}
