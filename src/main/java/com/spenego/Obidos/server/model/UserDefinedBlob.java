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
import java.util.Date;

public final class UserDefinedBlob implements Model,Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private Integer encryptionMode;
	private byte[] value;

	@Override
	protected void finalize() {	// NOSONAR -- we do not care when this may be executed
		if (value != null) {
			Arrays.fill(value, (byte) 0); // NOSONAR -- we do this to wipe data from memory
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public UserDefinedBlob() { /* Default constructor is sufficient */ }
	public UserDefinedBlob(final byte[] data, final int encryptionMode) {
		this.encryptionMode = encryptionMode;
		value = data;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Date getUpdatedAt() {
		return null;
	}

	@Override
	public Date getCreatedAt() {
		return null;
	}

	@Override
	public Integer getVersion() {
		return null;
	}

	@Override
	public void setCreatedAt(Date createdAt) { /* we don't keep this record type on user data blobs */ }

	@Override
	public void setUpdatedAt(Date updatedAt) { /* we don't keep this record type on user data blobs */ }

	@Override
	public void setVersion(Integer version)  { /* we don't keep this record type on user data blobs */ }

	@Override
	public Long getUserId() {
		return null;
	}

	public Integer getEncryptionMode() {
		return encryptionMode;
	}

	public void setEncryptionMode(final Integer encryptionMode) {
		this.encryptionMode = encryptionMode;
	}
}
