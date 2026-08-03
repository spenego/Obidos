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
import com.spenego.Obidos.shared.dto.DocumentDTO;

public final class UserDefinedFieldValue extends BaseModel implements Clearable, Model, Serializable {
	private static final long serialVersionUID = 1L;

	private Long userDefinedTypeValueId;
	private Long userDefinedFieldId;
	private Long blobValueId;
	private Long documentId;
	private Long type;
	private String name;
	private Integer position;
	private byte[] blobValue;
	private boolean decrypted; // during testing I discovered that the myBatis layer was caching results, hence we were attempting to decrypt blobs already decrypted
	private Integer encryptionMode;
	private DocumentDTO document;

	public UserDefinedFieldValue() {}

	public UserDefinedFieldValue(final Long userDefinedTypeValueId, final Long userDefinedFieldId) {
		this.userDefinedTypeValueId = userDefinedTypeValueId;
		this.userDefinedFieldId = userDefinedFieldId;
	}

	@Override
	public void clear() {
		position = encryptionMode = null;
		name = null;
		blobValueId = userDefinedFieldId = userDefinedTypeValueId = null;

		if (blobValue != null) {
			Arrays.fill(blobValue, (byte) 0);
			blobValue = null;
		}

		super.clear();
	}

	@Override
	public String toString() {
		return "User Defined Field Value " + getName();
	}

	public Long getUserDefinedTypeValueId() {
		return userDefinedTypeValueId;
	}

	public void setUserDefinedTypeValueId(Long userDefinedTypeValueId) {
		this.userDefinedTypeValueId = userDefinedTypeValueId;
	}

	public Long getUserDefinedFieldId() {
		return userDefinedFieldId;
	}

	public void setUserDefinedFieldId(Long userDefinedFieldId) {
		this.userDefinedFieldId = userDefinedFieldId;
	}

	public Long getBlobValueId() {
		return blobValueId;
	}

	public void setBlobValueId(Long blobValueId) {
		this.blobValueId = blobValueId;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public byte[] getBlobValue() {
		return blobValue;
	}

	public void setBlobValue(byte[] blobValue) {
		this.blobValue = blobValue;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}

	@Override
	public Long getUserId() {
		return null;
	}

	public Integer getEncryptionMode() {
		return encryptionMode;
	}

	public void setEncryptionMode(Integer encryptionMode) {
		this.encryptionMode = encryptionMode;
	}

	public boolean isDecrypted() {
		return decrypted;
	}

	public void setDecrypted(boolean decrypted) {
		this.decrypted = decrypted;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public DocumentDTO getDocument() {
		return document;
	}

	public void setDocument(DocumentDTO document) {
		this.document = document;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}
}