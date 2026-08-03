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

public class SmtpConfig extends NamedModel implements Model, Serializable {
	private String smtpServer;
	private Integer smtpPort;
	private Boolean useAuthentication;
	private Boolean useSsl;
	private Boolean useStartTls;
	private String toAddress;
	private String fromAddress;
	private String smtpUsername;
	private String smtpPassword;
	private static final long serialVersionUID = 1L;

	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer == null ? null : smtpServer.trim();
	}

	public Integer getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(Integer smtpPort) {
		this.smtpPort = smtpPort;
	}

	public Boolean getUseAuthentication() {
		return useAuthentication;
	}

	public void setUseAuthentication(Boolean useAuthentication) {
		this.useAuthentication = useAuthentication;
	}

	public Boolean getUseSsl() {
		return useSsl;
	}

	public void setUseSsl(Boolean useSsl) {
		this.useSsl = useSsl;
	}

	public Boolean getUseStartTls() {
		return useStartTls;
	}

	public void setUseStartTls(Boolean useStartTls) {
		this.useStartTls = useStartTls;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress == null ? null : toAddress.trim();
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress == null ? null : fromAddress.trim();
	}

	public String getSmtpUsername() {
		return smtpUsername;
	}

	public void setSmtpUsername(String smtpUsername) {
		this.smtpUsername = smtpUsername == null ? null : smtpUsername.trim();
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword == null ? null : smtpPassword.trim();
	}
}