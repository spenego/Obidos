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

public abstract class BaseModel implements Clearable, Model, Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long userId;
	private Date createdAt;
	private Date updatedAt;
	private Integer version;

	protected BaseModel() {}

	protected BaseModel(final Long id) {
		this.id = id;
	}

	protected BaseModel(final Long id, final Date createdAt) {
		this.id = id;
		this.createdAt = createdAt;
	}

	protected BaseModel(final Long id, final Integer version) {
		this.id = id;
		this.version = version;
	}

	protected BaseModel(final Long id, final Long userId) {
		this.id = id;
		this.userId = userId;
	}

	@Override
	public void clear() {
		version = null;
		createdAt = updatedAt = null;
		id = userId = null;
	}

	public boolean ownerIs(final User user) {
		return userId.equals(user.getId());
	}

	public boolean ownerIs(final Long otherId) {
		return userId.equals(otherId);
	}

	public boolean self(final Long userId) {
		return this.userId.equals(userId);
	}

	@Override
	public final Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public Long getUserId() {
		return userId;
	}
	public void setUserId(final Long userId) {
		this.userId = userId;
	}
	@Override
	public Date getCreatedAt() {
		return createdAt;
	}
	@Override
	public void setCreatedAt(final Date createdAt) {
		this.createdAt = createdAt;
	}
	@Override
	public Date getUpdatedAt() {
		return updatedAt;
	}
	@Override
	public void setUpdatedAt(final Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	@Override
	public Integer getVersion() {
		return version;
	}
	@Override
	public void setVersion(final Integer version) {
		this.version = version;
	}
}
