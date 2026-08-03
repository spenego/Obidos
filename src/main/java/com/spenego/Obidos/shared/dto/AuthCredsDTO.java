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

/**
 * {@code SpenegoResult} is used as credentials to most service layer calls.
 *
 * @since   Obidos1.0
*/

public final class AuthCredsDTO implements Serializable, Clearable {
	private static final long serialVersionUID = 1L;

	private String xsrfToken;
	private String locale;

	public AuthCredsDTO() {
		this.xsrfToken = null;
		this.locale = null;
	}

	public AuthCredsDTO(final String xsrfToken) {
		this.xsrfToken = xsrfToken;
	}

	@Override
	public void clear() {
		xsrfToken = null;
		locale = null;
	}

	public String getLocale() {
		return locale;
	}

	public String getXsrfToken() {
		return xsrfToken;
	}

	public void setXsrfToken(final String xsrfToken) {
		this.xsrfToken = xsrfToken;
	}

	public void setLocale(final String locale) {
		this.locale = locale;
	}
}
