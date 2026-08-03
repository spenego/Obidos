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

import com.spenego.Obidos.shared.ObidosConstants;

/**
 * We use JSON of this object to include as payload in encrypted Fernet token.
 * This class only used in server side.
 *
 * @author spgdev@spenego.com - Sep 21, 2020
 */
public final class FernetPayload implements Payload, Serializable {
	private static final long serialVersionUID = 1026614081132515646L;
	private Long userId;
	private Long documentId;
	private int actionType;
	private byte[] passphraseHash;

	public FernetPayload() {
	}

	public FernetPayload(final Long userId, final Long documentId, final byte[] passphraseHash, int actionType) {
		this.userId = userId;
		this.documentId = documentId;
		this.passphraseHash = passphraseHash;
		this.actionType = actionType;
	}

	@Override
	public void clear() {
		Arrays.fill(passphraseHash, (byte) 0);
		userId = documentId = null;
		actionType = ObidosConstants.ACTION_TYPE_NA;
	}

	@Override
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	@Override
	public byte[] getPassphraseHash() {
		return passphraseHash;
	}

	public void setPassphraseHash(final byte[] passphraseHash) {
		this.passphraseHash = passphraseHash;
	}

	public int getActionType()
	{
		return actionType;
	}

	// at this time actionType can be one of:
	// ObidosConstants.ACTION_TYPE_NA
	// ObidosConstants.ACTION_TYPE_DOWNLOAD_ADMINGUIDE
	// ObidosConstants.ACTION_TYPE_DOWNLOAD_USERGUIDE
	public void setActionType(int actionType)
	{
		this.actionType = actionType;
	}
}
