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

package com.spenego.Obidos.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LimitedFernetDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@RemoteServiceRelativePath("rpc/fernetService")
public interface FernetService extends RemoteService
{
	// Helper class to create a service
	// spgdev@spenego.com - Apr 21, 2019
	public static class Utility {
		private Utility() { /* no instances */ }
		private static final FernetServiceAsync instance = (FernetServiceAsync) GWT.create(FernetService.class);
		public static final FernetServiceAsync getInstance() { return instance; }
	}
	LimitedFernetDTO getFernetToken(AuthCredsDTO creds) throws ServerSideException;
	LimitedFernetDTO getFileUploadFernetToken(AuthCredsDTO creds, Long documentId, int actionType) throws ServerSideException;
	LimitedFernetDTO getFileDownloadFernetToken(AuthCredsDTO creds, Long documentId, int actionType) throws ServerSideException;

}
