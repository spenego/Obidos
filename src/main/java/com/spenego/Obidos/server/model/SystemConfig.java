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

import static java.lang.Boolean.FALSE;

import java.io.Serializable;
import java.util.Date;

import com.spenego.Obidos.server.utils.ServerUtils;

public final class SystemConfig extends BaseModel implements Model, Serializable {
	private static final long serialVersionUID = 1L;

	private Boolean	useSecurityClearances;
	private Boolean	sendIndividualItemNotifications;	// when a container is shared or revoked or deleted, should we send individual item notifications?
	private String	fqdn;
	private String	dateFormat;
	private String	adminEmail;
	private String	twoFactorAuthIssuer;
	private String	passwordComplexityName;				// the name of the password complexity requirements to use
	private String	contextPath;
	private String	scheme;
	private String	documentStorageDirectory;
	private Integer	sessionTimeoutSeconds;
	private Integer	memoryWipeDelay;					// value in milliseconds between an object being marked for wipe and wiping
	private Integer serverPort;
	private String	license;							// base64 encoded YAML
	private byte[]	licensePublicKey;
	private byte[]	smsKey;
	private String	smsAccountSID;
	private String	smsAuthToken;
	private String	smsProviderPhoneNumber;

	public SystemConfig() {
		this.sendIndividualItemNotifications = FALSE;
	}

	public SystemConfig(final Long id) {
		super(id);
		this.sendIndividualItemNotifications = FALSE;
	}

	public SystemConfig(final Long id, final String license) {
		super(id);
		this.license = license;
		this.sendIndividualItemNotifications = FALSE;
	}

	public SystemConfig(final Long id, final String fqdn, final String twoFactorAuthIssuer, final String passwordComplexityName, final Integer memoryWipeDelay) {
		super(id, new Date());
		this.serverPort = 443;
		this.fqdn = fqdn;
		this.sendIndividualItemNotifications = FALSE;
		this.twoFactorAuthIssuer = twoFactorAuthIssuer;
		this.passwordComplexityName = passwordComplexityName;
		this.memoryWipeDelay = memoryWipeDelay;
		this.useSecurityClearances = false;
		this.license = null;
		// @Deprecated
		this.license = "LS0tCnByb2R1Y3ROYW1lOiAiU3BlbmVnbyBPYmlkb3MiCmNvbXBhbnlOYW1lOiAiU3BlbmVnbyBEZW1vIExpY2Vuc2UiCmNvbXBhbnlFbWFpbDogInN1cHBvcnRAc3BlbmVnby5jb20iCm1heFVzZXJzOiAxMApzaWduaW5nRXBvY2g6IDE1NjEzMzcwNjcKbGljZW5zZVRlcm1zOiAiRGVtbyBsaWNlbnNlIGZvciBTcGVuZWdvIE9iaWRvcy5UaGlzIGxpY2Vuc2UgcmVzdHJpY3RzIG51bWJlciBvZiB1c2Vyc1wKICBcIHRvIDEwIGFuZCBudW1iZXIgb2YgYWRtaW5zIHRvIDEwIgpub25jZTogImRHb0VPTWJualVWV1lkYVVyTVgxQk80UWRCVlJoVXlHdGRxdGNmMHYxQ3M9IgpzaWduYXR1cmU6ICJ2YnQ5bkhjZGkzNTNvM29CTEl2TkhFeXVOQkRDWWVPeTA5aHFjcXpobWVibjhHdFM1eFBxNlQzRE5QYmdBQ0Zoa3R6VHZWandmNWNBSDA2OGh5dUZEUT09Igo=";
		this.licensePublicKey = ServerUtils.decodeFromBase64("QzfayX41K2/Scrd5g6wFvvqpK18pJrImc5/mQ8U5VG8=");
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

	@Override
	public String getName() {
		return null;
	}

	public String getTwoFactorAuthIssuer() {
		return twoFactorAuthIssuer;
	}

	public void setTwoFactorAuthIssuer(String twoFactorAuthIssuer) {
		this.twoFactorAuthIssuer = twoFactorAuthIssuer;
	}

	public Integer getSessionTimeoutSeconds() {
		return sessionTimeoutSeconds;
	}

	public void setSessionTimeoutSeconds(Integer sessionTimeoutSeconds) {
		this.sessionTimeoutSeconds = sessionTimeoutSeconds;
	}

	public Integer getMemoryWipeDelay() {
		return memoryWipeDelay;
	}

	public void setMemoryWipeDelay(Integer memoryWipeDelay) {
		this.memoryWipeDelay = memoryWipeDelay;
	}

	public Boolean getUseSecurityClearances() {
		return useSecurityClearances;
	}

	public void setUseSecurityClearances(Boolean useSecurityClearances) {
		this.useSecurityClearances = useSecurityClearances;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public byte[] getLicensePublicKey() {
		return licensePublicKey;
	}

	public void setLicensePublicKey(byte[] smsKey) {
		this.licensePublicKey = smsKey;
	}

	public byte[] getSmsKey() {
		return smsKey;
	}

	public void setSmsKey(byte[] smsKey) {
		this.smsKey = smsKey;
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

	public Boolean getSendIndividualItemNotifications() {
		return sendIndividualItemNotifications;
	}

	public void setSendIndividualItemNotifications(Boolean sendIndividualItemNotifications) {
		this.sendIndividualItemNotifications = sendIndividualItemNotifications;
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

	public String	getSmsAccountSID()						{ return smsAccountSID; }
	public void		setSmsAccountSID(String smsAccountSID)	{ this.smsAccountSID = smsAccountSID; }
	public String	getSmsAuthToken()						{ return smsAuthToken; }
	public void		setSmsAuthToken(String smsAuthToken)	{ this.smsAuthToken = smsAuthToken; }
	public String	getSmsProviderPhoneNumber()				{ return smsProviderPhoneNumber; }
	public void		setSmsProviderPhoneNumber(String smsProviderPhoneNumber) { this.smsProviderPhoneNumber = smsProviderPhoneNumber; }
}