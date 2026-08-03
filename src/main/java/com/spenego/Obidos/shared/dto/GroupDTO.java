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

public final class GroupDTO implements HasId, Clearable, Selectable, Serializable {
	private static final long serialVersionUID = 1L;

	private Long		id;
	private Long		userId;
	private String		name;
	private String		comments;
	private Boolean		inContainer;
	private Boolean		selected;	// set by Client to indicate that the user has selected this user

	public GroupDTO() {
	}

	public GroupDTO(final Long id) {
		this.id = id;
	}

	@Override
	public void clear() {
		id = userId = null;
		name = comments = null;
		inContainer = selected = null;
	}

	@Override
	public boolean equals(final Object o) {
		if((o == null) || (getClass() != o.getClass())){
	        return false;
	    }
		final GroupDTO g = (GroupDTO) o;
		return id.equals(g.id) && userId.equals(g.userId) && name.equals(g.name);
	}

	@Override
	public int hashCode() {
		return id.hashCode() ^ name.hashCode() ^ userId.hashCode();
	}

	@Override
	public Long getId() {
		return id;
	}
	public void setId(final Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	public Boolean getInContainer() {
		return inContainer == null ? Boolean.FALSE : inContainer;
	}

	public void setInContainer(Boolean inContainer) {
		this.inContainer = inContainer;
	}

	@Override
	public Boolean getSelected() {
		return selected == null ? Boolean.FALSE : selected;
	}

	@Override
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
}
