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

package com.spenego.Obidos.server.actions.impl;

import java.io.IOException;

import com.spenego.Obidos.server.actions.HaveIBeenPwnedActions;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * return number of times the password is found, null if not
 *
 * @author spgdev@spenego.com - Nov 13, 2018
 */
public class HaveIBeenPwnedActionsImpl extends ServerUtils implements HaveIBeenPwnedActions {
	private static final Logger logger = LoggerFactory.getLogger(HaveIBeenPwnedActionsImpl.class);

	@Override
	public Integer checkPassword(final String password) {
		final String hex = getHexSha1Digest(password).toUpperCase();
		final Request request = new Request.Builder().url("https://api.pwnedpasswords.com/range/" + hex.substring(0, 5)).build();

		try(final Response response = new OkHttpClient().newCall(request).execute()) {
			try(final ResponseBody body = response.body()) {
				if (body != null) {
					final String suffixHash = hex.substring(5);
					for (final String line : body.string().split("\\r?\\n")) {
						if (line.startsWith(suffixHash)) {
							final String found = line.substring(line.indexOf(":") + 1);
							logger.info(() -> "Password Found in haveibeenpwned.com: " + found + " times");
							return stringToInteger(found);
						}
					}
				}
			}
		} catch (final IOException e) {
			throw new ServerSideException("Error getting response form HaveIBeenPwned.com: " + e.getMessage());
		}
		return null;
	}
}
