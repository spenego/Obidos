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

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Arrays;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.gson.Gson;
import com.muquit.libsodiumjna.SodiumKeyPair;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.spenego.Obidos.server.actions.DocumentActions;
import com.spenego.Obidos.server.actions.ItemActions;
import com.spenego.Obidos.server.actions.LoginActions;
import com.spenego.Obidos.server.model.Document;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.DocumentOperations;
import com.spenego.Obidos.server.operations.ItemAssignmentOperations;
import com.spenego.Obidos.server.operations.UserOperations;
import com.spenego.Obidos.server.security.Encryption;
import com.spenego.Obidos.server.security.Encryption.PublicKeyDecryptor;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.server.utils.TimeLogger;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.FernetDTO;
import com.spenego.Obidos.shared.dto.FernetPayload;
import com.spenego.Obidos.shared.dto.Payload;
import com.spenego.Obidos.shared.exceptions.LicenseKeyException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public abstract class ServletRunnable implements Runnable {
	private final static int TOKEN_LIFE_TIME = 5; // seconds
	private final HttpServletRequest			request;
	private final HttpServletResponse			response;
	private final DocumentOperations			documentOperations;
	private final PlatformTransactionManager	transactionManager;
	private final UserOperations				userOperations;
	private final Encryption					encryption;
	private final LoginActions					loginActions;
	protected final ItemActions					itemActions;
	protected final DocumentActions				documentActions;
	protected final ItemAssignmentOperations	itemAssignmentOperations;


	protected abstract void action(Payload payload) throws IOException, FileUploadException, SodiumLibraryException;
	protected abstract Logger getLogger();
	protected static final byte[] EMPTYBUF = new byte[0];

	public ServletRunnable(final ApplicationContext actx, final HttpServletRequest request, final HttpServletResponse response) {
		this.request			= request;
		this.response			= response;
		documentOperations		= (DocumentOperations)			actx.getBean("documentOperations");
		encryption				= (Encryption)					actx.getBean("encryption");
		itemActions				= (ItemActions)					actx.getBean("itemActions");
		itemAssignmentOperations= (ItemAssignmentOperations)	actx.getBean("itemAssignmentOperations");
		transactionManager		= (PlatformTransactionManager)	actx.getBean("transactionManager");
		userOperations			= (UserOperations)				actx.getBean("userOperations");
		loginActions			= (LoginActions)				actx.getBean("loginActions");
		documentActions			= (DocumentActions)				actx.getBean("documentActions");
	}

	protected final Path getSavePath(final String filename) {
		try {
			return documentActions.getPath(filename);
		} catch(Exception e) {
			throw new ServerSideException("Could not get document path: " + e.getMessage());
		}
	}

	protected static final void clear(final byte[] b) {
		if (b != null) {
			Arrays.fill(b, (byte) 0);
		}
	}

	protected static final void clear(final SodiumKeyPair kp) {
		if (kp != null) {
			clear(kp.getPrivateKey());
			clear(kp.getPublicKey());
		}
	}

	protected final FileItemIterator getFileItemIterator() throws FileUploadException, IOException {
		return new ServletFileUpload().getItemIterator(request);
	}

	private void sendResponseError(final HttpServletResponse res, final String s) throws IOException {
		getLogger().info(() -> s);
		res.sendError(HttpServletResponse.SC_FORBIDDEN, s == null ? "Not Authorized" : s);
	}

	private void sendNotAuthorized(final HttpServletResponse res) throws IOException {
		sendResponseError(res, null);
	}

	protected final void populateResponse(long contentLength, final String filename) {
		getLogger().info(() -> "Response contains file " + filename + " which is " + contentLength + " bytes");

		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
		response.addHeader("Content-Length", Long.toString(contentLength));
	}

	protected final void populateResponse(long contentLength, final String filename, final String contentType)
	{
		getLogger().info(() -> "Response contains file " + filename + " which is " + contentLength + " bytes");

		response.setContentType(contentType);
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
		response.addHeader("Content-Length", Long.toString(contentLength));
	}

	protected final ServletOutputStream getOutputStream() throws IOException {
		return response.getOutputStream();
	}

	private <T> T runInTransaction(final TransactionCallback<T> r, final boolean readonly) {
		final TransactionTemplate tt = new TransactionTemplate(transactionManager);
		tt.setReadOnly(readonly);
		return tt.execute(r);
	}

	protected final <T> T readInTransaction(final TransactionCallback<T> r) {
		return runInTransaction(r, true);
	}

	protected final <T> T runInTransaction(final TransactionCallback<T> r) {
		return runInTransaction(r, false);
	}

	protected final User getUser(final Long userId) {
		return readInTransaction(s -> userOperations.get(userId));
	}

	protected final Document getDocument(final Long documentId) {
		try {
			return readInTransaction(s -> documentOperations.get(documentId));
		} catch(final Exception ex) {
			throw new ServerSideException("Unable to find Document Slot");
		}
	}

	private static String getExceptionErrorMessage(final Throwable t) {
		final String err = t.getMessage();
		return err == null ? "Unable to load document. Look at server log for details." : err;
	}

	private void sendErrorResponse(final Throwable t) {
		getLogger().exception(t);
		final Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		response.setContentType("text/html"); // Set response content type
		try (final PrintWriter out = response.getWriter()) {
			final String msg = (statusCode != null && statusCode != 500) ? "Error Status code: " + statusCode : getExceptionErrorMessage(t);
			getLogger().error(() -> "Sending ErrorResponse: " + msg);
			out.write("<html><head><title>The " + servletType() + " failed. Exception/Error Details</title></head><body>");
			out.write(msg);
		    out.write("</body></html>");
			out.flush();
		} catch (final IOException e) {
			getLogger().exception(e);
		}
	}

	private FernetPayload validatePayload(final FernetPayload payload) throws IOException {
		final Long userId		= payload.getUserId();
		final Long documentId	= payload.getDocumentId();
		final int actionType    = payload.getActionType();

		getLogger().info(() -> "validating Fernet token");
		getLogger().info(() -> "Userid: " + userId);
		getLogger().info(() -> "Document id: " + documentId);
		getLogger().info(() -> "action type: " + actionType);

		if (documentId == null) {
			throw new ServerSideException("Caller failed to supply a Document ID.");
		}

		if (userId == null) {
			throw new ServerSideException("Caller failed to supply a User ID.");
		}

		if (payload.getPassphraseHash() == null) {
			throw new ServerSideException("Caller failed to supply a Passphrase Hash.");
		}

		return payload;
	}

	private String getFernetPayloadString() throws IOException {
		final String propFilePath	= ServerUtils.getFernetRotatingPropertiesFilePath();
		final FernetDTO dto			= ServerUtils.getFernetDTO(propFilePath, ObidosConstants.FILE_UPLOAD_FERNET_KEY_FILE_AGE);
		final String jsonStr		= ServerUtils.validateFernetToken(dto, request, response, TOKEN_LIFE_TIME);

		if (jsonStr == null) {
			sendNotAuthorized(response);
			throw new IOException("Non-Authorized request.");
		}

		return jsonStr;
	}

	public FernetPayload getFernetPayload() throws IOException {
		return validatePayload(new Gson().fromJson(getFernetPayloadString(), FernetPayload.class));
	}

	protected void ensureDocumentUploadIsEnabled() {
		if (!Boolean.TRUE.equals(loginActions.currentLicenseStats().getSupportsDocumentUpload())) {
			throw new LicenseKeyException("Document upload is unavailable with current License.");
		}
	}

	protected final PublicKeyDecryptor getPublicKeyDecryptor(final User caller, final byte[] passphraseHash) {
		return encryption.createPublicKeyDecryptor(caller, new PassphraseHash(passphraseHash));
	}

	protected abstract String servletType();

	@Override
	public final void run() {
		final TimeLogger timeLogger = TimeLogger.createTimeLogger(getLogger(), "Servlet action");

		try {
			getLogger().info(() -> "run method calling action()");
			action(getFernetPayload());
		} catch (final Throwable t) {
			getLogger().info(() -> "Caught exception: " + t.getMessage() + " while processing " + servletType());
			sendErrorResponse(t);
			getLogger().exception(t);
		} finally {
			timeLogger.complete(getLogger());
		}
	}
}
