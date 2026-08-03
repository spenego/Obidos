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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LimitedFernetDTO;

public interface FernetServiceAsync
{
	void getFernetToken(AuthCredsDTO creds, AsyncCallback<LimitedFernetDTO> callback);
	void getFileUploadFernetToken(AuthCredsDTO creds, Long documentId, int actionType, AsyncCallback<LimitedFernetDTO> callback);
	void getFileDownloadFernetToken(AuthCredsDTO creds, Long documentId, int actionType, AsyncCallback<LimitedFernetDTO> callback);
}
