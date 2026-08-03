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

package com.spenego.Obidos.server.utils.bouncycastlepem;

import java.io.IOException;

/**
 * Exception thrown on failure to generate a PEM object.
 */
public class PemGenerationException extends IOException {
	private static final long serialVersionUID = 1L;
	private Throwable cause;

	public PemGenerationException(String message, Throwable cause) {
		super(message);
		this.cause = cause;
	}

	public PemGenerationException(String message) {
		super(message);
	}

	public synchronized Throwable getCause() {
		return cause;
	}
}
