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

public class ObidosException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = 1L;
	private String message;	// NOSONAR - do not make final. It will not be serialized if final.

	public ObidosException() {
		message = null;
	}

	public ObidosException(final String message) {
		super(message);
		this.message = message;
	}

	public ObidosException(final Throwable cause) {
		super(cause);
		message = (cause != null) ? cause.getMessage() : null;
	}

	public ObidosException(final String message, final Throwable cause) {
		super(message, cause);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}