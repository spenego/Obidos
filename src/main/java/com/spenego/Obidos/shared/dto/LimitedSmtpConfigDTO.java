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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.spenego.Obidos.shared.Selectable;

public class LimitedSmtpConfigDTO extends BaseDTO implements HasId, Clearable, Selectable, Serializable {
	private static final long serialVersionUID = 1L;

	private String name;

    @NotNull
    @NotBlank(message = "Please specify an SMTP servername or IP address")
	private String smtpServer;

    public LimitedSmtpConfigDTO() {}

    public LimitedSmtpConfigDTO(String name, String smtpServer) {
    	this.name = name;
    	this.smtpServer = smtpServer;
    }

    @Override
    public void clear() {
    	name = null;
    	super.clear();
    }

	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	public final String getSmtpServer() {
		return smtpServer;
	}
	public final void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	@Override
	public void setSelected(Boolean selected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Boolean getSelected() {
		// TODO Auto-generated method stub
		return null;
	}
}
