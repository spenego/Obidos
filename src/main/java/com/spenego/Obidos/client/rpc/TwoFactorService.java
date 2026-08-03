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
import com.spenego.Obidos.shared.dto.TwoFactorDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 *
 * @author spgdev@spenego.com
 * Jun 22, 2018 4:04:24 PM - first cut
 */
@RemoteServiceRelativePath("rpc/twofactorService")
public interface TwoFactorService extends RemoteService {
	public static class Utility {
		private Utility() { /* no instances */ }
		private static final TwoFactorServiceAsync instance = (TwoFactorServiceAsync) GWT.create(TwoFactorService.class);
		public static final TwoFactorServiceAsync getInstance() { return instance; }
	}
	TwoFactorDTO create2FASecret(AuthCredsDTO creds) throws ServerSideException;
	Void enable2FA(AuthCredsDTO creds, byte[] code) throws ServerSideException;
	TwoFactorDTO decodeQRCodeImageDataUri(AuthCredsDTO creds, String dataUri) throws ServerSideException;
	TwoFactorDTO generate2FACode(AuthCredsDTO creds, String otpAuthUri) throws ServerSideException;
}
