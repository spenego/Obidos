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

import com.spenego.Obidos.client.rpc.QRCodeService;
import com.spenego.Obidos.server.actions.QRCodeActions;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.QRCodeDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@Service("qrCodeService")
public final class QRCodeServiceImpl extends ObidosService implements QRCodeService
{
    private static final Logger logger = LoggerFactory.getLogger(QRCodeServiceImpl.class);

    @Autowired
    private QRCodeActions actions;

    @Override
    protected Logger getLogger()
    {
        return logger;
    }

    @Transactional @Override
	public QRCodeDTO createQRCodeImage(AuthCredsDTO creds, String str) throws ServerSideException
	{
		return userFunction(creds, "create QR Code Image", "createQRCodeImage", user -> actions.createQRCodeImage(user, str));
	}

}
