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

import com.spenego.Obidos.shared.dto.Clearable;

public final class NotificationTemplate implements Clearable, Model, Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String subject;
	private Date createdAt;
	private Date updatedAt;
	private Integer version;
	private byte[] message;
	private byte[] html_message;

	public NotificationTemplate() {
	}

	public NotificationTemplate(final Long id) {
		this.id = id;
	}

	@Override
	public void clear() {
		id = null;
		subject = null;
		createdAt = updatedAt = null;
		version = null;
		Arrays.fill(message, (byte) 0);
		Arrays.fill(html_message, (byte) 0);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject == null ? null : subject.trim();
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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}

	@Override
	public Long getUserId() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	public byte[] getHtml_message() {
		return html_message;
	}

	public void setHtml_message(byte[] html_message) {
		this.html_message = html_message;
	}
}