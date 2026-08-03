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
import java.util.Arrays;

import com.spenego.Obidos.shared.HasName;

public final class UserDefinedFieldValueDTO extends BaseDTO implements Clearable, HasName, Serializable {
	private static final long serialVersionUID = 1L;

	private Long userDefinedTypeValueId;
	private Long userDefinedFieldId;
	private Long rowId;			// this is used for recursive field definitions (UDF composed of other UDFs)
	private Long type;
	private Integer position;
	private byte[] blobValue;
	private DocumentDTO document;
	private String name; // this is the name given to the User Defined Field (not stored with the UDF value)
	private Integer version;

	public UserDefinedFieldValueDTO() {}

	public UserDefinedFieldValueDTO(final Long userDefinedFieldId) {
		this.userDefinedFieldId = userDefinedFieldId;
	}

	public UserDefinedFieldValueDTO(final Long userDefinedFieldId, final byte[] blobValue) {
		this.userDefinedFieldId = userDefinedFieldId;
		this.blobValue = blobValue;
	}

	public UserDefinedFieldValueDTO(final Integer position, final byte[] blobValue) {
		this.position = position;
		this.blobValue = blobValue;
	}

	@Override
	public void clear() {
		version = null;
		name = null;
		if (blobValue != null) {
			Arrays.fill(blobValue, (byte) 0);
			blobValue = null;
		}
		position = null;
		rowId = userDefinedFieldId = userDefinedTypeValueId = null;
		super.clear();
	}

	@Override
	protected void finalize() { // NOSONAR -- we know that this method may not be called timely
		clear();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getUserDefinedFieldId() {
		return userDefinedFieldId;
	}
	public void setUserDefinedFieldId(final Long userDefinedFieldId) {
		this.userDefinedFieldId = userDefinedFieldId;
	}
	public Long getUserDefinedTypeValueId() {
		return userDefinedTypeValueId;
	}
	public void setUserDefinedTypeValueId(final Long userDefinedTypeValueId) {
		this.userDefinedTypeValueId = userDefinedTypeValueId;
	}
	public Long getRowId() {
		return rowId;
	}
	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}
	public byte[] getBlobValue() {
		return blobValue;
	}
	public void setBlobValue(final byte[] val) {
		this.blobValue = val;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
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
