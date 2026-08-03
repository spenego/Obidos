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

public class ItemContainerComboDTO implements HasId, Clearable, Selectable, Serializable {
	private static final long serialVersionUID = 1L;

	private Long		id;
	private String		name;
	private Character	type;
	private Boolean		selected;	// set by Client to indicate that the user has selected this element

	@Override
	public void clear() {
		selected = null;
		type = null;
		name = null;
		id = null;
	}

	@Override
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Character getType() {
		return type;
	}
	public void setType(Character type) {
		this.type = type;
	}
	@Override
	public Boolean getSelected() {
		return selected;
	}
	@Override
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
}
