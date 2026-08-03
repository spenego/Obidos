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

import javax.validation.constraints.Email;

public final class ResetPasswordDTO implements Serializable {
	/**
	 * @author spgdev@spenego.com - Jun 11, 2017
	 */
	private static final long serialVersionUID = 1L;

	@Email(message = "Invalid email Address")
	private String emailAddress;

	public ResetPasswordDTO() { /* default constructor is sufficient */ }

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
}
