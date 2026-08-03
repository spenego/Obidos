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

public final class Container extends NamedModel implements Clearable, Model, Serializable {
	private static final long serialVersionUID = 1L;
	private Long parentId;
	private Boolean isPrivate;
	private Boolean shared;
	private Integer maximumSecurityClassification;

	@Override
	public void clear() {
		shared = isPrivate = null;
		parentId = null;
		super.clear();
	}

	@Override
	public String toString() {
		return getName();
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Boolean getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public Boolean getShared() {
		return shared;
	}

	public void setShared(Boolean shared) {
		this.shared = shared;
	}

	public Container() { /* Default constructor */ }

	public Container(final Long id, final Long userId) {
		super(id, userId);
	}

	public Container(final Long id, final Boolean shared) {
		super(id);
		this.shared = shared;
		this.maximumSecurityClassification = 42000;
	}

	public Container(final Long id, final String name) {
		super(id, (Long) null, name);
		this.maximumSecurityClassification = 42000;
	}

	public Container(final Long userId, final String name, final Boolean isPrivate) {
		super(null, userId, name);
		this.isPrivate = isPrivate;
		this.maximumSecurityClassification = 42000;
	}

	public Container(final Long id, final Long userId, final String name, final Boolean isPrivate) {
		super(id, userId, name);
		this.isPrivate = isPrivate;
		this.maximumSecurityClassification = 42000;
	}

	public Integer getMaximumSecurityClassification() {
		return maximumSecurityClassification;
	}

	public void setMaximumSecurityClassification(Integer maximumSecurityClassification) {
		this.maximumSecurityClassification = maximumSecurityClassification;
	}
}