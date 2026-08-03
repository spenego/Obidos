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

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

public final class SmtpConfigDTO extends LimitedSmtpConfigDTO implements Clearable, Serializable {
	/**
	 * @author spgdev@spenego.com - May 5, 2019
	 */
	private static final long serialVersionUID = -6281755008920176643L;
	@NotNull(message="SMTP Port is Required")
	@Range(min=1, max=65535, message="Invalid SMTP Port")
	private Integer smtpPort;
	private Boolean useAuthentication;
	private Boolean useSsl;
	private Boolean useStartTls;
	private String toAddress;
	private String fromAddress;
	private String smtpUsername;
	private String smtpPassword;

	public SmtpConfigDTO() {}

	public SmtpConfigDTO(String name, String servername, Integer port, Boolean useAuthentication, Boolean useSsl, Boolean useStartTls, String toAddress, String fromAddress, String smtpUsername, String smtpPassword) {
		super(name, servername);
		this.smtpPort = port;
		this.useAuthentication = useAuthentication;
		this.useSsl = useSsl;
		this.useStartTls = useStartTls;
		this.toAddress = toAddress;
		this.fromAddress = fromAddress;
		this.smtpUsername = smtpUsername;
		this.smtpPassword = smtpPassword;
	}

    @Override
    public void clear() {
    	smtpPort = null;
    	useAuthentication = useSsl = useStartTls = null;
    	toAddress = fromAddress = smtpUsername = smtpPassword = null;
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
		this.toAddress = toAddress;
	}
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String getSmtpUsername()
    {
        return smtpUsername;
    }
    public void setSmtpUsername(String smtpUsername)
    {
        this.smtpUsername = smtpUsername;
    }
    public String getSmtpPassword() {
		return smtpPassword;
	}
	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}
}