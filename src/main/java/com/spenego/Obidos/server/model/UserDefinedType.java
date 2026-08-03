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

public final class UserDefinedType extends NamedModel implements Clearable, Serializable, Model {
	private static final long serialVersionUID = 1L;

	private Boolean personal;
	private Boolean adHoc;
	private Boolean global;
	private Long freeFormItemId;

	public enum State { PRIVATE, GLOBAL }

	@Override
	public void clear() {
		personal = adHoc = global = null;
		freeFormItemId = null;
		super.clear();
	}

	@Override
	public String toString() {
		return getName();
	}

	public boolean isPrivate() {
		return getState() == State.PRIVATE;
	}

	public State getState() {
		return Boolean.TRUE.equals(personal) ? State.PRIVATE : State.GLOBAL;
	}

	public Boolean getPersonal() {
		return personal;
	}
	public void setPersonal(final Boolean personal) {
		this.personal = personal;
	}

	public UserDefinedType() {
		this.personal = true;
		this.adHoc = false;
	}

	// Suitable for creating a Personal type
	public UserDefinedType(final Long userId, final String name) {
		super(null, userId, name);
		this.personal = true;
		this.global = false;
		this.adHoc = false;
	}

	public UserDefinedType(final Long id, final Long freeFormItemId) {
		super(id);
		this.freeFormItemId = freeFormItemId;
	}

	public UserDefinedType(final Long userId, final String name, final Boolean personal, final Boolean adHoc) {
		super(null, userId, name);
		boolean p = Boolean.TRUE.equals(personal);
		this.personal = p;
		this.adHoc = Boolean.TRUE.equals(adHoc);
		this.global = p ? null : true;
	}

	public UserDefinedType(final Long id, final String name, final Long userId, final Boolean personal) {
		super(id, userId, name);
		boolean p = Boolean.TRUE.equals(personal);
		this.personal = p;
		this.adHoc = false;
		this.global = p ? null : true;
	}

	public UserDefinedType(final Long id, final String name, final Boolean personal, final Boolean adHoc, final Integer version) {
		super(id, version, name);
		this.personal = personal;
		this.adHoc = adHoc;
	}

	public Boolean getGlobal() {
		return global;
	}

	public void setGlobal(Boolean global) {
		this.global = global;
	}

	public Long getFreeFormItemId() {
		return freeFormItemId;
	}

	public void setFreeFormItemId(Long freeFormItemId) {
		this.freeFormItemId = freeFormItemId;
	}

	public Boolean getAdHoc() {
		return adHoc;
	}

	public void setAdHoc(Boolean adHoc) {
		this.adHoc = adHoc;
	}
}
