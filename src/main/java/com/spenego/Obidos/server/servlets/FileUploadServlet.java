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

import static com.spenego.Obidos.shared.ObidosConstants.ENCRYPTION_BLOCK_SIZE;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.muquit.libsodiumjna.SodiumKeyPair;
import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.spenego.Obidos.server.model.Document;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.utils.EncryptedOutputStream;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.dto.Payload;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;

/**
 * Servlet implementation class FileUploadServlet
 */
/*
@WebServlet(
		name = "FileUploadServlet",
		description = "Servlet to upload file securely",
		urlPatterns = "/upload",
		asyncSupported = true)
		*/
// Annotation does not work if deployed to jetty . spgdev@spenego.com - Sep 21, 2020
// before saving to disk, upload db following the documents:
// docs/Design/file_sharing/file_sharing.txt
// docs/Design/file_sharing/file_sharing_api.pdf
public final class FileUploadServlet extends HttpServlet {
	protected static final Logger logger = LoggerFactory.getLogger(FileUploadServlet.class);
	protected static final FileTime TIMESTAMP = FileTime.fromMillis(933438042000L);
	private static final long serialVersionUID = 1L;

	protected ApplicationContext actx;

	protected static Set<PosixFilePermission> FILE_PERMISSIONS = new HashSet<>();

	static {
		FILE_PERMISSIONS.add(PosixFilePermission.OWNER_READ);
	}

	public void init(final ServletConfig servletConfig) throws ServletException {
		logger.info(() -> "in FileUploadServlet init");
		actx = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileUploadServlet() {
		super();
	}

	// Simple method to convert a long to an array of bytes representing a decimal string of the value
	protected static byte[] tob(final long v) {
		return ((Long) v).toString().getBytes();
	}

	private class Uploader extends ServletRunnable {
		private SodiumKeyPair kp; // because creating this can throw an exception, we don't create it in the constructor

		Uploader(final HttpServletRequest request, final HttpServletResponse response) {
			super(actx, request, response);
		}

		@Override
		protected String servletType() { return "file upload"; }

		@Override
		protected Logger getLogger() { return logger; }

		private void setTimestampsOnPath(final Path path) throws IOException {
		    Files.setAttribute(path, "lastModifiedTime", TIMESTAMP);
		    Files.setAttribute(path, "creationTime",     TIMESTAMP);
		}

		/**
		 * Sets the timestamp on all files to the same value to make it more difficult to distinguish
		 * files from when they were uploaded.
		 * @param path Sets the date on the file at pathname (and its parent directory).
		 */
		private void makeFileInconspicuious(final Path path) {
			try {
				if (Files.exists(path)) {
					Files.setPosixFilePermissions(path, FILE_PERMISSIONS);
					setTimestampsOnPath(path);
					setTimestampsOnPath(path.getParent());
				}
			} catch(final Throwable t) {
				logger.error(() -> "Caught exception while attempting to adjust file time of " + path);
				logger.exception(t);
			}
		}

		/**
		 * This is the Encryptor passed to the EncryptedOutputStream constructor.  It pads the supplied buffer if it is not full.
		 * We are also required to throw an IOException instead of whatever type of exception we may generate from our
		 * encryption mechanism.
		 */
		private byte[] encrypt(final byte[] data, final int len) throws IOException {
			try {
				int diff = data.length - len;
				if (diff != 0) {
					System.arraycopy(ServerUtils.randomBytes(diff), 0, data, len, diff);
				}

				return ServerUtils.encryptWithPublicKey(data, kp.getPublicKey());
			} catch (final SodiumLibraryException e) {
				throw new IOException(e);
			}
		}

		private Void updateDocument(final User caller, final String filename, final Document document, final byte[] passphraseHash, final String guidFilename, final EncryptedOutputStream eos, long bytesCopied) {
			return documentActions.update(caller, document, filename, guidFilename, tob(bytesCopied), tob(eos.getTotalBytesRead()), kp, new PassphraseHash(passphraseHash));
		}

		private void upload(final User caller, final String filename, final Document document, final byte[] passphraseHash, final String guidFilename, final InputStream is) throws IOException, SodiumLibraryException {
		    final String originalFilename = document.getGuid();
		    final Path savePath = getSavePath(guidFilename);

		    try(final EncryptedOutputStream eos = new EncryptedOutputStream(new FileOutputStream(savePath.toFile()), this::encrypt, ENCRYPTION_BLOCK_SIZE); final GZIPOutputStream os = new GZIPOutputStream(eos)) {
		        long bytesCopied = ServerUtils.copy(is, os);
		        os.close();
		        runInTransaction(ts -> updateDocument(caller, filename, document, passphraseHash, guidFilename, eos, bytesCopied));
		    } catch(final Throwable t) {
		        logger.error(() -> "Unable to save to " + savePath);
		        logger.error(() -> "Caught exception: " + t);
		        ServerUtils.deleteFile(savePath);
		        // Bug #135
		        ServerUtils.deleteEmptyParentDirectory(savePath);
		        throw t;
		    } finally {
		        makeFileInconspicuious(savePath);
		        document.clear();
		    }

		    if (originalFilename != null) {
		        Path oldPath = getSavePath(originalFilename);
		        ServerUtils.deleteFile(oldPath);
		        // Bug #135
		        ServerUtils.deleteEmptyParentDirectory(oldPath);
		    }
		    logger.info(()-> "MMM Save to: " + savePath);
		}
		
		private boolean callerMayUpdateDocument(final Long userid, final Long documentId) {
			try {
				return readInTransaction(s -> itemAssignmentOperations.getByDocumentId(userid, documentId)).getUpdatePermitted();
			} catch(final Exception ex) {
				return false;
			}
		}

		@Override
		public void action(final Payload payload) throws IOException, FileUploadException, SodiumLibraryException {
			ensureDocumentUploadIsEnabled();
			final User user = getUser(payload.getUserId());

			if (!callerMayUpdateDocument(user.getId(), payload.getDocumentId())) {
				throw new PermissionDeniedException("You may not modify this Document");
			}

			kp = SodiumLibrary.cryptoBoxKeyPair();

			try { // at this time we only upload only 1 file, in future we may upload more than one.
				final FileItemIterator iter = getFileItemIterator();
				while (iter.hasNext()) {
					final FileItemStream fileItem = iter.next();
					final String fileName = fileItem.getName();

					if (fileItem.getFieldName() == null || fileName == null) {
						continue;
					}

					try (final InputStream is = fileItem.openStream()) {
						upload(user, fileName, getDocument(payload.getDocumentId()), payload.getPassphraseHash(), ServerUtils.randomString(), is);
					}
				}
			} finally {
				clear(kp);
				kp = null;
				payload.clear();
			}
		}
	}

	/**
	 * validate Fernet token first, if validated successfully, decrypt and get
	 * the payload inside. Obtain user and note/item info from payload.
	 * We had to do that way because our session is not accessible from standard
	 * java servlet. Therefore, a logged in user had to get a very short lived
	 * (5 sec right now) fernet token before making upload POST request. Note
	 * Fernet key rotates when it is older that 30 minutes.
	 */
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		logger.info(() -> "in doPost");
		final HttpSession session = request.getSession(false);
		if (session != null) {
			logger.info(() -> "Session id: " + session.getId());
		}
		new Uploader(request, response).run();
	}
}
