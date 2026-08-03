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
import java.util.Date;

public class BaseDTO extends BaseShared implements Clearable, HasId, Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final long MIN_DYNAMIC_ID = 100000000L;

	private Long id;
	private Date createdAt;
	private Date updatedAt;

	protected BaseDTO() {}

	protected BaseDTO(final Long id) {
		this.id = id;
	}

	public boolean isSpenegoDeliveredElement() {
		return getId() < MIN_DYNAMIC_ID;
	}

	@Override
	public void clear() {
		updatedAt = createdAt = null;
		id = null;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
}
