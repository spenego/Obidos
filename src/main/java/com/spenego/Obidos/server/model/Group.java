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

public final class Group extends NamedModel implements Clearable, ContainerTarget, Model, Serializable {
	private static final long serialVersionUID = 1L;
	private String comments;
	private Boolean inContainer;
	private Boolean global;
	private Integer minimumSecurityClearance;

	public Group() { }

	public Group(final Long userId, final String name, final String comment) {
		super(null, userId, name);
		this.comments = comment;
		minimumSecurityClearance = 0;
		global = false;
	}

	@Override
	public void clear() {
		comments = null;
		global = inContainer = null;
		super.clear();
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments == null ? null : comments.trim();
	}

	public Boolean getInContainer() {
		return inContainer;
	}

	public void setInContainer(Boolean inContainer) {
		this.inContainer = inContainer;
	}

	public Integer getMinimumSecurityClearance() {
		return minimumSecurityClearance;
	}

	public void setMinimumSecurityClearance(Integer minimumSecurityClearance) {
		this.minimumSecurityClearance = minimumSecurityClearance;
	}

	public Boolean getGlobal() {
		return global;
	}

	public void setGlobal(Boolean global) {
		this.global = global;
	}
}