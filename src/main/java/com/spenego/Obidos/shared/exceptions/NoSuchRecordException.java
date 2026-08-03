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

public final class NoSuchRecordException extends ServerSideException {
	private static final long serialVersionUID = 1L;

	public NoSuchRecordException() {}

	public NoSuchRecordException(final String type, final long id) {
		super("No " + type + " of id " + id + " exists in the system.");
	}

	public NoSuchRecordException(final String type, final String id) {
		super("No " + type + " of id " + id + " exists in the system.");
	}

	public NoSuchRecordException(final String msg) {
		super(msg);
	}

}
