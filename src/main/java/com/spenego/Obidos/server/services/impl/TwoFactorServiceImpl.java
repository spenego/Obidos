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
import org.springframework.transaction.annotation.Transactional;

import com.spenego.Obidos.client.rpc.TwoFactorService;
import com.spenego.Obidos.server.actions.TwoFactorAuthenticationActions;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.TwoFactorDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@Service("twofactorService")
public final class TwoFactorServiceImpl extends ObidosService implements TwoFactorService
{
	private static final Logger logger = LoggerFactory.getLogger(TwoFactorServiceImpl.class);

	@Autowired private TwoFactorAuthenticationActions actions;

	@Override
	protected Logger getLogger() {
		return logger;
	}

 	@Transactional @Override
	public TwoFactorDTO create2FASecret(final AuthCredsDTO creds) throws ServerSideException {
		return userFunction(creds, "create 2FA Secret", "create2FASecret", user -> actions.create2FASecret(user, summonPWHash()));
	}

 	@Transactional @Override
	public Void enable2FA(final AuthCredsDTO creds, final byte[] code) throws ServerSideException {
		return userFunction(creds, "enable 2FA", "enable2FA", user -> actions.enable2FA(user, code));
	}

	@Override
	public TwoFactorDTO decodeQRCodeImageDataUri(AuthCredsDTO creds, String imageDataUri) throws ServerSideException
	{
		return userFunction(creds, "decode QR Code Image Data Uri", "decodeQRCodeImageDataUri", user -> actions.decodeQRCodeImageDataUri(user, imageDataUri));
	}

	@Override
	public TwoFactorDTO generate2FACode(AuthCredsDTO creds, String otpAuthUri) throws ServerSideException
	{
		return userFunction(creds, "Generate 2FA Code", "generate2FACode", user -> actions.generate2FACode(user, otpAuthUri));
	}
}
