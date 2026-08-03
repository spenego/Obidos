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

import static com.spenego.Obidos.server.model.Audit.DELETE_DOCUMENT;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.muquit.libsodiumjna.SodiumKeyPair;
import com.spenego.Obidos.server.actions.DocumentActions;
import com.spenego.Obidos.server.actions.ItemActions;
import com.spenego.Obidos.server.actions.LoginActions;
import com.spenego.Obidos.server.model.Document;
import com.spenego.Obidos.server.model.Item;
import com.spenego.Obidos.server.model.ItemAssignment;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.DocumentOperations;
import com.spenego.Obidos.server.operations.ItemAssignmentOperations;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.security.Encryption.PublicKeyEncryptor;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.dto.DocumentDTO;
import com.spenego.Obidos.shared.exceptions.LicenseKeyException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class DocumentActionsImpl extends CryptoActions<Document> implements DocumentActions {
	private static final Logger logger = LoggerFactory.getLogger(DocumentActionsImpl.class);

	@Autowired private final DocumentOperations			documentOperations = null;
	@Autowired private final LoginActions				loginActions = null;
	@Autowired private final ItemActions				itemActions = null;
	@Autowired private final ItemAssignmentOperations	itemAssignmentOperations = null;

	public DocumentActionsImpl() {
		super(null, null);
	}

	@Override
	protected String elementName() {
		return "document";
	}

	@Override
	protected Operations<Document> getOperations() {
		return documentOperations;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected Integer getAuditDeleteAction() {
		return DELETE_DOCUMENT;
	}

	private void ensureCallerHasAccessToDocument(final User caller, final Long documentId) {
		final Long itemId = documentOperations.getDocumentItemId(documentId);
		final Long callerId = caller.getId();
		boolean callerHasAccessToDocument = false;

		for(final ItemAssignment ia: itemAssignmentOperations.getItemAssignmentsOfItem(itemId)) {
			if (callerId.equals(ia.getUserId())) {
				callerHasAccessToDocument = true;
			}
		}

		if (!callerHasAccessToDocument) {
			throw new PermissionDeniedException("You do not have access to this document status.");
		}
	}

	@Override
	public Integer getUploadState(final User caller, final Long documentId) {
		if (documentId == null) {
			throw new ServerSideException("Document ID may not be null.");
		}

		// Do we need to ensure that the user has access to the document?
		ensureCallerHasAccessToDocument(caller, documentId);

		return DocumentDTO.inferUploadState(documentOperations.get(documentId).getGuid());
	}

	@Override
	public Void delete(User caller, Collection<Long> documentIds, PassphraseHash passphraseHash) {
		return null;
	}

	private User getOwnerOfDocument(final Long documentId) {
		return userOperations.getOwnerOfDocument(documentId);
	}

	private void update(final User caller, final Long documentId, final String filename, final String guidFilename, final byte[] fileSize, final byte[] compressedFileSize, final SodiumKeyPair kp) {
		final PublicKeyEncryptor pke = encryption.createPublicKeyEncryptor(caller);
		final Document document = new Document(documentId, pke.encrypt(filename.getBytes()), guidFilename, pke.encrypt(kp.getPrivateKey()), kp.getPublicKey(), pke.encrypt(fileSize), pke.encrypt(compressedFileSize));

		documentOperations.updateSelective(document);
		document.clear();
	}

	private static User addUser(final User u, final Collection<Long> c) {
		c.add(u.getId());
		return u;
	}

	@Override
	public Void update(final User caller, final Document document, final String filename, final String guidFilename, final byte[] fileSize, final byte[] compressedFileSize, final SodiumKeyPair kp, final PassphraseHash passphraseHash) {
		if (isFalse(loginActions.currentLicenseStats().getSupportsNotificationEmails())) {
			throw new LicenseKeyException("Document management is unavailable with current License.");
		}
		final Collection<Long> users = new ArrayList<>();

		if (document.getGuid() != null) { // old document exists, file was previously uploaded and potentially shared
			processStream(() -> "Updating documents ", () -> documentOperations.getDocumentsOfGuid(document.getGuid()), d -> update(addUser(getOwnerOfDocument(d.getId()), users), d.getId(), filename, guidFilename, fileSize, compressedFileSize, kp));
			final Long userId = caller.getId();
			final Long itemId = documentOperations.getDocumentItemId(document.getId());
			final Item item = new Item();

			item.setId(itemId);
			itemOperations.updateSelective(item);
			itemActions.notifyUpdateRecipients(caller, () -> users.stream().filter(id -> !userId.equals(id)), itemId, "modified the attached document");
		} else {
			update(caller, document.getId(), filename, guidFilename, fileSize, compressedFileSize, kp);
		}

		return null;
	}

	@Override
	public Stream<Document> getDocumentsOfItem(final Long itemId) {
		return documentOperations.getDocumentsOfItem(itemId);
	}

	@Override
	public Path getPath(final String filename) throws IOException {
		final String subdir = filename.substring(filename.length() - 4, filename.length() - 2);
		final File directory = ServerUtils.createSecuredDirectory(getSystemConfig().getDocumentStorageDirectory(), subdir);

		return new File(directory, filename).toPath();
	}

	@Override
	public Void delete(final Document d) {
		try {
			documentOperations.delete(d.getId());
			Path filePath = getPath(d.getGuid());
			Files.delete(filePath);
			// Bug #135 delete empty directory
			// Dec-28-2025
			ServerUtils.deleteEmptyParentDirectory(filePath);
		} catch(final Exception ex) {
			logger.exception(ex);
			logger.error(() -> "Failed to delete document file " + d.getGuid());
		}
		return null;
	}
}
