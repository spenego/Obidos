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

public final class LimitedUserForAdminDTO extends LimitedUserDTO implements Clearable, Selectable, Serializable {
	private static final long serialVersionUID = 1L;

	private String username; // should only be set when called by Admin for Admin functions
	private Long capabilities;

	public LimitedUserForAdminDTO() {}

	public LimitedUserForAdminDTO(final Long id, final String username) {
		super(id);
		this.username = username;
	}

	@Override
	public void clear() {
		capabilities = null;
		username = null;
		super.clear();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean getRootAdmin() {
		return (capabilities & CapabilityDTO.ROOT_ADMIN) != 0;
	}

	public void setCapabilities(final Long capabilities) {
		this.capabilities = capabilities;
	}

	public Long getCapabilities() {
		return capabilities;
	}
}
