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

public class NamedModel extends BaseModel implements Clearable, Serializable, Model {
	private static final long serialVersionUID = 1L;
	private String name;

	public NamedModel() { }

	public NamedModel(final Long id) {
		super(id);
	}

	public NamedModel(final Long id, final Date createdAt) {
		super(id, createdAt);
	}

	public NamedModel(final Long id, final Long userId) {
		super(id, userId);
	}

	public NamedModel(final String name) {
		this.name = name;
	}

	public NamedModel(final Long id, final Long userId, final String name) {
		super(id, userId);
		this.name = name;
	}

	public NamedModel(final Long id, final Integer version, final String name) {
		super(id, version);
		this.name = name;
	}

	@Override
	public void clear() {
		name = null;
		super.clear();
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}
}
