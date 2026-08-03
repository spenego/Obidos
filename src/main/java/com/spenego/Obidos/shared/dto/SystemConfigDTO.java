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
import java.util.Date;

public final class SystemConfigDTO implements Clearable, Serializable {
	private String	fqdn;
	private String	adminEmail;
	private String	twoFactorAuthIssuer;
	private String	contextPath;
	private String	scheme;
	private String	dateFormat;
	private Date	createdAt;
	private Date	updatedAt;
	private Integer	version;
	private Integer	sessionTimeoutSeconds;
	private Integer	memoryWipeDelay;			// value in seconds between an object being marked for wipe and wiping
	private Integer serverPort;
	private String	passwordComplexityName;
	private String	documentStorageDirectory;
	private PassComplexityDTO passComplexityDTO; // will be mapped from JSON blob in database

	private static final long serialVersionUID = 1L;

	public SystemConfigDTO() { /* default config is sufficient */ }

    @Override
    public void clear() {
    	passComplexityDTO = null;
    	passwordComplexityName = null;
    	serverPort = memoryWipeDelay = sessionTimeoutSeconds = version = null;
    	createdAt = updatedAt = null;
    	fqdn = adminEmail = twoFactorAuthIssuer = contextPath = scheme = null;
    }

	public String getFqdn() {
		return fqdn;
	}

	public void setFqdn(String fqdn) {
		this.fqdn = fqdn == null ? null : fqdn.trim();
	}

	public String getAdminEmail() {
		return adminEmail;
	}

	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail == null ? null : adminEmail.trim();
	}

	public String getTwoFactorAuthIssuer() {
		return twoFactorAuthIssuer;
	}

	public void setTwoFactorAuthIssuer(String twoFactorAuthIssuer) {
		this.twoFactorAuthIssuer = twoFactorAuthIssuer;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getSessionTimeoutSeconds() {
		return sessionTimeoutSeconds;
	}

	public void setSessionTimeoutSeconds(final Integer sessionTimeoutSeconds) {
		this.sessionTimeoutSeconds = sessionTimeoutSeconds;
	}

	public Integer getMemoryWipeDelay() {
		return memoryWipeDelay;
	}

	public void setMemoryWipeDelay(Integer memoryWipeDelay) {
		this.memoryWipeDelay = memoryWipeDelay;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public Integer getServerPort() {
		return serverPort;
	}

	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}

	public PassComplexityDTO getPassComplexityDTO()
	{
		return passComplexityDTO;
	}

	public void setPassComplexityDTO(PassComplexityDTO passComplexityDTO)
	{
		this.passComplexityDTO = passComplexityDTO;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getPasswordComplexityName() {
		return passwordComplexityName;
	}

	public void setPasswordComplexityName(String passwordComplexityName) {
		this.passwordComplexityName = passwordComplexityName;
	}

	public String getDocumentStorageDirectory() {
		return documentStorageDirectory;
	}

	public void setDocumentStorageDirectory(String documentStorageDirectory) {
		this.documentStorageDirectory = documentStorageDirectory;
	}
}
