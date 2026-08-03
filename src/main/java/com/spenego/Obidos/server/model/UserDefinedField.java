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

public class UserDefinedField extends BaseModel implements Model, Serializable {
	private static final long serialVersionUID = 1L;

	private Long typeId;
	private Long type;
	private Integer position;
	private String name;

	@Override
	public void clear() {
		name = null;
		position = null;
		type = typeId = null;
		super.clear();
	}

	@Override
	public String toString() {
		return "User Defined Field " + getName();
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public boolean typeIsUDF() {
		return typeId > 100000000L;
	}

	public UserDefinedField() {}

	public UserDefinedField(final Long id, final Long typeId, final Long type, final String name, final Integer position) {
		super(id);
		this.typeId = typeId;
		this.type = type;
		this.name = name;
		this.position = position;
	}

	@Override
	public Long getUserId() {
		return null;
	}
}