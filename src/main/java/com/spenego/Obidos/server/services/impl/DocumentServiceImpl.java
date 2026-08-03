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

import com.spenego.Obidos.client.rpc.DocumentService;
import com.spenego.Obidos.server.actions.DocumentActions;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;

@Service("documentService")
public class DocumentServiceImpl extends ObidosService implements DocumentService {
	private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

	@Autowired private final DocumentActions actions = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	public Integer getUploadState(final AuthCredsDTO creds, final Long documentId) {
		return userFunction(creds, "get document upload state", "getUploadState", user -> actions.getUploadState(user, documentId));
	}
}
