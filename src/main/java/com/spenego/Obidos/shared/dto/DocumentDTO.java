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
import java.nio.charset.StandardCharsets;

public final class DocumentDTO extends BaseDTO implements HasId, Clearable, Serializable {
	private static final long serialVersionUID = 1L;
	public static final String UPLOAD_PENDING	= "PENDING-";
	public static final String UPLOAD_CANCELLED	= "CANCELLED-";
	public static final String UPLOAD_FAILED	= "FAILED-";
	public static final int STATE_COMPLETE  = 0;
	public static final int STATE_PENDING   = 1;
	public static final int STATE_CANCELLED = 2;
	public static final int STATE_FAILED    = 3;
	public static final int STATE_UNKNOWN   = 4;

	private Integer uploadState;
	private String guid;
	private byte[] decryptionKey;
	private byte[] publicKey;
	private byte[] filename;
	private byte[] fileLength;				// an Integer but kept encrypted since that info is sensitive
	private byte[] compressedFileLength;	// an Integer but kept encrypted since that info is sensitive

	public DocumentDTO() {
		uploadState = STATE_UNKNOWN;
	}

	public DocumentDTO(String filename) {
		uploadState = STATE_UNKNOWN;
		this.filename = filename.getBytes();
	}

	@Override
	public void clear() {
		setFilename(null);
		super.clear();
	}

	public void setUploadState(final Integer state) {
		uploadState = state;
	}

	public static Integer inferUploadState(final String guid) {
		if (guid == null) {
			return STATE_UNKNOWN;
		}

		if (guid.startsWith(UPLOAD_PENDING)) {
			return STATE_PENDING;
		}
		
		if (guid.startsWith(UPLOAD_CANCELLED)) {
			return STATE_CANCELLED;
		}

		if (guid.startsWith(UPLOAD_FAILED)) {
			return STATE_FAILED;
		}

		return STATE_COMPLETE;
	}

	/**
	 * Clears all data that should not leave the server.  DTOs are sometimes used for intra-server communication (sharing items).
	 * In these cases, all information is required.  However, we do not want to send most of this information to a client where
	 * it is of little or no use.
	 */
	public void redact() {
		if (guid != null) {
			uploadState = inferUploadState(guid);
		}
		guid = null;
		fileLength = compressedFileLength = decryptionKey = publicKey = null;
	}

	public Boolean isUploadPending() {
		return uploadState == STATE_PENDING;
	}

	public Boolean isUploadComplete() {
		return uploadState == STATE_COMPLETE;
	}

	public Boolean isUploadCancelled() {
		return uploadState == STATE_CANCELLED;
	}

	public Boolean isUploadFailed() {
		return uploadState == STATE_FAILED;
	}

	public String getFilenameString() {
		return (filename != null) ? new String(filename, StandardCharsets.UTF_8) : null;
	}

	public byte[] getFilename() {
		return filename;
	}

	public void setFilename(final byte[] filename) {
		this.filename = filename;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public byte[] getDecryptionKey() {
		return decryptionKey;
	}

	public void setDecryptionKey(byte[] decryptionKey) {
		this.decryptionKey = decryptionKey;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}

	public byte[] getFileLength() {
		return fileLength;
	}

	public void setFileLength(byte[] fileLength) {
		this.fileLength = fileLength;
	}

	public byte[] getCompressedFileLength() {
		return compressedFileLength;
	}

	public void setCompressedFileLength(byte[] compressedFileLength) {
		this.compressedFileLength = compressedFileLength;
	}
}
