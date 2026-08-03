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

package com.spenego.Obidos.server.security;

import com.spenego.Obidos.shared.exceptions.PassphraseRequiredException;

public final class PassphraseHash {
	private boolean validated;
	private final byte[] passphraseHash;

	public PassphraseHash(final byte[] passphraseHash) {
		this.passphraseHash = passphraseHash;
		validated = false;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public byte[] get() {
		if (passphraseHash == null) {
			throw new PassphraseRequiredException("You must set your passphrase.");
		}

		return passphraseHash;
	}
}
