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

package com.spenego.Obidos.server.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spenego.Obidos.client.rpc.FernetService;
import com.spenego.Obidos.server.actions.FernetActions;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LimitedFernetDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@Service("fernetService")
public final class FernetServiceImpl extends ObidosService implements FernetService {
	private static final Logger logger = LoggerFactory.getLogger(FernetServiceImpl.class);

	@Autowired private final FernetActions fernetActions = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	public LimitedFernetDTO getFernetToken(final AuthCredsDTO creds) throws ServerSideException {
		return adminFunction(creds, "get a Fernet token", "getFernetToken", true, admin -> fernetActions.getFernetToken(admin, summonPWHash()));
	}

	@Override
	public LimitedFernetDTO getFileUploadFernetToken(final AuthCredsDTO creds, final Long documentId, final int actionType) throws ServerSideException {
		return userOrAdminFunction(creds, "get a Security token for file upload", "getFileUploadFernetToken", true, user -> fernetActions.getFileUploadFernetToken(user, documentId, summonPWHash(), actionType));
	}

	@Override
	public LimitedFernetDTO getFileDownloadFernetToken(final AuthCredsDTO creds, final Long documentId, final int actionType) throws ServerSideException {
		return userOrAdminFunction(creds, "get a Security token for file download", "getFileDownloadFernetToken", true, user -> fernetActions.getFileDownloadFernetToken(user, documentId, summonPWHash(), actionType));
	}
}
