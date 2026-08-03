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

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

import com.muquit.libsodiumjna.SodiumLibrary;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;

/**
 *
 * @author spgdev@spenego.com - Dec 20, 2016
 */
public final class LibSodiumPasswordSecurity implements PasswordSecurity {
	private static final Logger logger = LoggerFactory.getLogger(LibSodiumPasswordSecurity.class);

	public LibSodiumPasswordSecurity() {
		LibSodium.initialize();
	}

	@Override
	public String libraryVersion() {
		return SodiumLibrary.libsodiumVersionString();
	}

	private static byte[] passwordBytes(final String password) {
		return password.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public String hashPassword(final String password) {
		try {
			return SodiumLibrary.cryptoPwhashStr(passwordBytes(password));
		} catch (final Exception e) {
			logger.exception(e);
			return null;
		}
	}

	@Override
	public boolean verifyPassword(final String password, final String hashedPassword) {
		try {
			// If the hashedPassword comes from database it will not be right
			// padded with null
			// characters. So if it is less than 128 bytes, right pad with null
			// characters
			// Watch out!! StringUtils also exist in
			// org.apache.commons.lang.StringUtils which
			// seems to die silently without any exception!!
			return SodiumLibrary.cryptoPwhashStrVerify(StringUtils.rightPad(hashedPassword, 128, '\0'), passwordBytes(password));
		} catch (final Exception e) {
			logger.exception(e);
			return false;
		}
	}
}
