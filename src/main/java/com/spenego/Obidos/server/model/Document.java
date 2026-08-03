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

package com.spenego.Obidos.server.model;

import java.io.Serializable;
import java.util.Arrays;

import com.spenego.Obidos.shared.dto.Clearable;

public final class Document extends BaseModel implements Clearable, Model, Serializable {
	private static final long serialVersionUID = 1L;
	private String guid;
	private byte[] decryptionKey;
	private byte[] publicKey;
	private byte[] filename;
	private byte[] fileLength;					// an Integer but kept encrypted since that info is sensitive
	private byte[] compressedFileLength;		// an Integer but kept encrypted since that info is sensitive

	public Document() {}

	public Document(final Long id, final byte[] filename, final String guid, final byte[] decryptionKey, final byte[] publicKey, final byte[] fileLength, final byte[] compressedFileLength) {
		super(id);
		this.guid = guid;
		this.filename = filename;
		this.decryptionKey = decryptionKey;
		this.publicKey = publicKey;
		this.fileLength = fileLength;
		this.compressedFileLength = compressedFileLength;
	}

	@Override
	public void clear() {
		if (fileLength != null) {
			Arrays.fill(fileLength, (byte) 0);
			fileLength = null;
		}
		if (compressedFileLength != null) {
			Arrays.fill(compressedFileLength, (byte) 0);
			compressedFileLength = null;
		}
		if (filename != null) {
			Arrays.fill(filename, (byte) 0);
			filename = null;
		}
		if (decryptionKey != null) {
			Arrays.fill(decryptionKey, (byte) 0);
			decryptionKey = null;
		}

		guid = null;
		super.clear();
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public byte[] getDecryptionKey() {
		return decryptionKey;
	}

	public void setDecrytptionKey(final byte[] decryptionkey) {
		this.decryptionKey = decryptionkey;
	}

	@Override
	public String getName() {
		return null;
	}

	public byte[] getFilename() {
		return filename;
	}

	public void setFilename(byte[] filename) {
		this.filename = filename;
	}

	public byte[] getFileLength() {
		return fileLength;
	}

	public void setFileLength(byte[] fileLength) {
		this.fileLength = fileLength;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}

	public byte[] getCompressedFileLength() {
		return compressedFileLength;
	}

	public void setCompressedFileLength(byte[] compressedFileLength) {
		this.compressedFileLength = compressedFileLength;
	}
}
