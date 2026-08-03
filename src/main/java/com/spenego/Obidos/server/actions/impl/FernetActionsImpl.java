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
import java.security.SecureRandom;

import com.google.gson.Gson;
import com.macasaet.fernet.Key;
import com.macasaet.fernet.Token;
import com.spenego.Obidos.server.actions.FernetActions;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.FernetPayload;
import com.spenego.Obidos.shared.dto.LimitedFernetDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class FernetActionsImpl extends ServerUtils implements FernetActions {
	private static final Logger logger = LoggerFactory.getLogger(FernetActionsImpl.class);

	private static LimitedFernetDTO createFernetDTO(final String key, final String plaintext) {
		return new LimitedFernetDTO(Token.generate(new SecureRandom(), new Key(key), plaintext).serialise());
	}

	@Override
	public LimitedFernetDTO getFernetToken(final User admin, final PassphraseHash passphraseHash) {
		logger.info(() -> "Generating Fernet Token");
		try {
			final String adminId = admin.getId().toString();
			final StringBuilder sb = new StringBuilder(adminId.length() + admin.getUsername().length() + admin.getFullname().length() + 3);
			sb.append(adminId);
			sb.append(",");
			sb.append(admin.getUsername());
			sb.append(",");
			sb.append(admin.getFullname());
			return createFernetDTO(getFernetDTO("fernet_crypto.properties").getKey(), sb.toString());
		} catch (final IOException e) {
			throw new ServerSideException("Could not read secret key for token: " + e);
		}
	}

	private static String fernetKey() {
		try {
			return getFernetDTO(getFernetRotatingPropertiesFilePath(), ObidosConstants.FILE_UPLOAD_FERNET_KEY_FILE_AGE).getKey();
		} catch (final IOException e) {
			throw new ServerSideException("Could not read secret key for token: " + e);
		}
	}

	private static LimitedFernetDTO genFernetToken(final String plaintext, final int actionType) {
		logger.info(() -> "Generating Fernet Token for File " + actionType);

		return createFernetDTO(fernetKey(), plaintext);
	}

	private static String getFernetText(final User user, final Long documentId, final PassphraseHash passphraseHash, final int actionType) {
		String jsonStr = null;
		byte[] phash = null;
		if (actionType == ObidosConstants.ACTION_TYPE_NA)
		{
			phash = passphraseHash.get();
		}
		else
		{
			// admin does not have passphrase hash
			phash = "na".getBytes();
		}
		jsonStr = new Gson().toJson(new FernetPayload(user.getId(), documentId, phash, actionType));
		return jsonStr;
	}

	private static LimitedFernetDTO getFileBackedFernetToken(final User user, final Long documentId, final PassphraseHash passphraseHash, final int actionType) {
		final LimitedFernetDTO fernetToken = genFernetToken(getFernetText(user, documentId, passphraseHash, actionType), actionType);
		try {
			createFerenetTokenFile(fernetToken.getToken(), "0");
		} catch (final IOException e) {
			logger.error(() -> "Could not create fernet semaphore file for " + actionType + ":" + e.getMessage());
		}
		return fernetToken;
	}

	@Override
	public LimitedFernetDTO getFileUploadFernetToken(final User user, final Long documentId, final PassphraseHash passphraseHash, final int actionType) {
		return getFileBackedFernetToken(user, documentId, passphraseHash, actionType);
	}

	@Override
	public LimitedFernetDTO getFileDownloadFernetToken(final User user, final Long documentId, final PassphraseHash passphraseHash, final int actionType) {
		return getFileBackedFernetToken(user, documentId, passphraseHash, actionType);
	}
}
