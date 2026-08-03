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

public final class UserComboResult implements Serializable {
	private static final long serialVersionUID = 1L;

	private UserDTO user;
	private LdapConfigurationResult ldapConfigResult;

	public UserDTO getUser() {
		return user;
	}

	public void setUser(final UserDTO user) {
		this.user = user;
	}

	public LdapConfigurationResult getLdapConfigs() {
		return ldapConfigResult;
	}

	public void setLdapConfigs(final LdapConfigurationResult ldapConfigResult) {
		this.ldapConfigResult = ldapConfigResult;
	}

	public UserComboResult() {}

	public UserComboResult(final UserDTO user, final LdapConfigurationResult ldapConfigResult, final CapabilityDTO capabilityDTO) {
		this.user = user;
		this.ldapConfigResult = ldapConfigResult;
		this.user.setCapabilities(capabilityDTO);
	}
}
