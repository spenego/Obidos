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

package com.spenego.Obidos.server.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.spenego.Obidos.server.model.Document;
import com.spenego.Obidos.server.utils.DecryptedInputStream;
import com.spenego.Obidos.server.utils.DocumentDecryptor;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.Payload;

/**
 *
 * @author spgdev@spenego.com - Sep 22, 2020
 */
public final class FileDownloadServlet extends HttpServlet {
	protected static final Logger logger = LoggerFactory.getLogger(FileDownloadServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileDownloadServlet() {
		super();
	}

	protected ApplicationContext actx;

	public void init(final ServletConfig servletConfig) throws ServletException {
		logger.info(() -> "in FileDownloadServlet init");
		actx = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
	}

	private class Downloader extends ServletRunnable {
		public Downloader(final HttpServletRequest request, final HttpServletResponse response) {
			super(actx, request, response);
		}

		@Override
		protected String servletType() { return "file download"; }

		@Override
		protected Logger getLogger() { return logger; }

		private void copyToOutputStream(final InputStream inputStream) throws IOException {
			try(final ServletOutputStream outputStream = getOutputStream()) {
				ServerUtils.copy(inputStream, outputStream);
			}
		}

		private void downloadGuide(final Payload payload, final int actionType) throws IOException {
			String mimeType = "application/pdf";
			String filePath = null;
			String docType = "Admin Guide";

			if (actionType == ObidosConstants.ACTION_TYPE_DOWNLOAD_ADMINGUIDE) {
				filePath = ServerUtils.getAdminGuidePath();
			} else if (actionType == ObidosConstants.ACTION_TYPE_DOWNLOAD_USERGUIDE) {
				docType = "User Guide";
				filePath = ServerUtils.getUserGuidePath();
			}

			final File file = new File(filePath);
			final long fileSize	= file.length();
			final String fck = filePath;
			logger.info(() -> "Download file: " + fck);
			if (! ServerUtils.fileExist(filePath)) {
				throw new IOException("Could not download document: " + docType + " File does not exist");
			}
//			mimeType = getServletContext().getMimeType(filePath);
			populateResponse(fileSize, file.getName(), mimeType);
			logger.info(() -> "Download file: " + fck + " Size: " + fileSize + " bytes");
			try(final FileInputStream inputStream = new FileInputStream(file)) {
				copyToOutputStream(inputStream);
			} finally {
				payload.clear();
			}
			return;

		}

		@Override
		public void action(final Payload payload) throws IOException {
			final int actionType = payload.getActionType();
			
			// download obidos pdf guides, no encryption/decryption
			if (actionType == ObidosConstants.ACTION_TYPE_DOWNLOAD_ADMINGUIDE || actionType == ObidosConstants.ACTION_TYPE_DOWNLOAD_USERGUIDE) {
				downloadGuide(payload, actionType);
				return;
			}

			// User supplied file upload/download
			ensureDocumentUploadIsEnabled();
			final Document document				= getDocument(payload.getDocumentId());
			final DocumentDecryptor decryptor	= new DocumentDecryptor(getPublicKeyDecryptor(getUser(payload.getUserId()), payload.getPassphraseHash()), document.getCompressedFileLength(), document.getPublicKey(), document.getDecryptionKey());
			final long fileSize					= Long.parseLong(new String(decryptor.decrypt(document.getFileLength())));

			try (final InputStream inputStream = new GZIPInputStream(new DecryptedInputStream(new FileInputStream(getSavePath(document.getGuid()).toFile()), decryptor, DocumentDecryptor.BUFFER_SIZE))) {
				populateResponse(fileSize, new String(decryptor.decrypt(document.getFilename())));
				copyToOutputStream(inputStream);
			} finally {
				decryptor.clear();
				document.clear();
				payload.clear();
			}
		}
	}

	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		logger.info(() -> "Download with POST............");
		new Downloader(request, response).run();
	}
}
