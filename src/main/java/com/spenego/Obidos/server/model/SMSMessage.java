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

import java.util.Date;

import com.spenego.Obidos.shared.dto.Clearable;

public class SMSMessage implements Model, Clearable {
	private static final long serialVersionUID = 1L;
	private String smsMessage;

	public String getSMSMessage() {
		return smsMessage;
	}

	public void setSMSMessage(String smsMessage) {
		this.smsMessage = smsMessage;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getUserId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getUpdatedAt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getCreatedAt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCreatedAt(Date createdAt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUpdatedAt(Date updatedAt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVersion(Integer version) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		smsMessage = null;
	}
}
