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
import java.util.Date;

public class PasswordReset implements Model, Serializable {
	private Long id;
	private Byte state;
	private Long userId;
	private String token;
	private String emailAddress;
	private Date createdAt;
	private Date updatedAt;
	private static final long serialVersionUID = 1L;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Byte getState() {
		return state;
	}

	public void setState(Byte state) {
		this.state = state;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token == null ? null : token.trim();
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress == null ? null : emailAddress.trim();
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public static final byte PASSWORD_RESET_REQUESTED = 0;
	public static final byte NON_EXISTANT_EMAIL_REQUEST = 1; // If the email used for password reset does not exist in our system we will let the recipient know.
	public static final byte EMAIL_SENT = 2;
	public static final byte PASSPHRASE_RESET_REQUESTED = 3;

	@Override
	public Integer getVersion() {
		return null;
	}

	@Override
	public void setVersion(Integer version)  { /* this does not have this record since it is so ephemeral */ }

	public PasswordReset() {}

	public PasswordReset(final Long userId, final String emailAddress, final byte state) {
		this.userId = userId;
		this.emailAddress = emailAddress;
		this.state = state;
	}

	public PasswordReset(final Long id, final byte state, final String token) {
		this.id = id;
		this.state = state;
		this.token = token;
	}

	@Override
	public String getName() {
		return getToken();
	}

	public final boolean isRequestForPassphraseReset() {
		return state == PASSPHRASE_RESET_REQUESTED;
	}

	public final boolean isRequestForNonExistantEmail() {
		return state == NON_EXISTANT_EMAIL_REQUEST;
	}
}