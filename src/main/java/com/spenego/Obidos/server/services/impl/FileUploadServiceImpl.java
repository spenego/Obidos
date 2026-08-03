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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.spenego.Obidos.client.rpc.FileUploadService;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;

@Service("fileUploadService")
public class FileUploadServiceImpl extends RemoteServiceServlet implements FileUploadService
{
	/**
	 * @author spgdev@spenego.com - Sep 22, 2019
	 */
	private static final long serialVersionUID = 3871719505488594360L;

	@Override
	public Void uploadFile(AuthCredsDTO creds, String filename)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    protected void service(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* stub */
	}
}
