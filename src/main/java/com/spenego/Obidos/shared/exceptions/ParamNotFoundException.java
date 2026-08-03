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

package com.spenego.Obidos.shared.exceptions;

import java.io.Serializable;

/**
 * Spenego excetion is uncheked
 * @author spgdev@spenego.com - Nov 23, 2018
 *
 */
public class ParamNotFoundException extends Exception implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String message; // NOSONAR - do not make final. It will not be serialized if

	public ParamNotFoundException() {
		message = null;
	}

	public ParamNotFoundException(final String message) {
		super(message);
		this.message = message;
	}

	public ParamNotFoundException(final Throwable cause) {
		super(cause);
		message = (cause != null) ? cause.getMessage() : null;
	}

	public ParamNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}