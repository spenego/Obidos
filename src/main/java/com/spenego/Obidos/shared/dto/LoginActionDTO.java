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

public final class LoginActionDTO implements Serializable
{
	/**
	 * @author spgdev@spenego.com - Dec 19, 2016
	 */
	private static final long serialVersionUID = 1L;
	private LoginType loginType;
	private String username;
	private String password;
	private String xsrfToken;

	protected LoginActionDTO() { }

	public LoginActionDTO(String username, String password) {
		this.loginType = LoginType.VIA_CREDENTIALS;
		this.username = username;
		this.password = password;
	}

	public LoginActionDTO(final String xsrfToken) {
		this.loginType = LoginType.VIA_COOKIE;
		this.xsrfToken = xsrfToken;
	}

	public static boolean isSecured() {
		return false;
	}

	public LoginType getLoginType() {
		return loginType;
	}

	public void setLoginType(final LoginType loginType) {
		this.loginType = loginType;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

    public String getXsrfToken() {
        return xsrfToken;
    }

    public void setXsrfToken(final String xsrfToken) {
        this.xsrfToken = xsrfToken;
    }
}
