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

import com.google.zxing.WriterException;
import com.spenego.Obidos.server.actions.QRCodeActions;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.dto.QRCodeDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 *
 * @author spgdev@spenego.com
 * Nov-14-2024
 */
public final class QRCodeActionsImpl implements QRCodeActions {
    // private static final Logger logger = LoggerFactory.getLogger(QRCodeActionsImpl.class);

	public QRCodeActionsImpl() {
        super();
	}


	@Override
	public QRCodeDTO createQRCodeImage(User user, String str)
	{
		try
		{
			byte[] imageBytes = ServerUtils.createQRCodeImageBytes(str);
			String base64Image = ServerUtils.encodeToBase64(imageBytes);
			QRCodeDTO dto = new QRCodeDTO(base64Image);
			dto.setTitle(str);
			return dto;
		} catch (ServerSideException | WriterException e)
		{
			throw new ServerSideException("Failed to Create QR Code image" + e.getMessage());
		}
	}
}
