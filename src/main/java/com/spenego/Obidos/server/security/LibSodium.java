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

import com.muquit.libsodiumjna.SodiumLibrary;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;

public final class LibSodium
{
	private static final Logger logger = LoggerFactory.getLogger(LibSodium.class);

	public static void initialize() {
		final String libSodiumPath = ServerUtils.getSodiumLibPath();
		SodiumLibrary.setLibraryPath(libSodiumPath);
		logger.info(() -> "Loaded libsodium version: " + SodiumLibrary.libsodiumVersionString() + " from " + libSodiumPath);
	}
}
