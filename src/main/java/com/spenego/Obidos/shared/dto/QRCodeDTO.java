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

package com.spenego.Obidos.shared.dto;

import java.io.Serializable;

public class QRCodeDTO implements Clearable, Serializable 
{
	/**
	 * @author spgdev@spenego.com - Nov 14, 2024
	 */
	private static final long serialVersionUID = 3203993923551943383L;
	
	private String base64QRCodeImage;
	private String title;
	
	public QRCodeDTO() {}
	
	public QRCodeDTO(String base64QRCodeImage)
	{
		this.base64QRCodeImage = base64QRCodeImage;
	}
	
	@Override
	public void clear()
	{
		// Nothing can be cleared.
	}

	public String getBase64QRCodeImage()
	{
		return base64QRCodeImage;
	}

	public void setBase64QRCodeImage(String base64qrCodeImage)
	{
		base64QRCodeImage = base64qrCodeImage;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
}
