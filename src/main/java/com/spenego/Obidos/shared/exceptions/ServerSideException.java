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

public class ServerSideException extends ObidosException {
    private static final long serialVersionUID = 1L;

    public ServerSideException() {}

    public ServerSideException(final String message) {
        super(message);
    }

    public ServerSideException(final Throwable cause) {
        super(cause);
    }

    public ServerSideException(final String message, final Throwable cause) {
        super(message,cause);
    }
}
