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

public class LicenseStats implements Serializable {
	private static final long serialVersionUID = 1L;

    public static final int LICENSE_STATE_BEYOND_GRACE_PERIOD = 5;
    public static final int LICENSE_STATE_GRACE_PERIOD_SECOND_MONTH = 4;
    public static final int LICENSE_STATE_GRACE_PERIOD_FIRST_MONTH = 3;
    public static final int LICENSE_STATE_ONE_MONTH_UNTIL_EXPIREY = 2;
    public static final int LICENSE_STATE_NOMINAL = 1;

	private Boolean	licenseIsValid;
	private Boolean	hasExpired;
	private int		licenseExpirationState;
	private Integer	currentNumberOfUsers;
	private Integer	maxNumberOfUsers;
	private Long	expirationEpoch;
	private Boolean supportsAudit;
	private Boolean supportsNotificationEmails;
	private Boolean supportsSNMP;
	private Boolean supportsSMS;
	private Boolean supportsDocumentUpload;
	private Boolean supportQRCodeUpload;
	private Boolean supportsContainerSharing;

	private static final long DAY = 1000L * 60 * 60 * 24;
	private static final long THIRTY_DAYS = DAY * 30;
	private static final long SIXTY_DAYS = DAY * 60;

	public LicenseStats() {
		licenseIsValid = false;
		licenseExpirationState = LICENSE_STATE_NOMINAL;
		currentNumberOfUsers = maxNumberOfUsers = null;
		supportsAudit = true;
		supportsNotificationEmails = true;
		supportsSNMP = true;
		supportsSMS = true;
		supportsDocumentUpload = true;
		supportsContainerSharing = true;
		setSupportQRCodeUpload(true);
	}

	private static int calculateLicenseExpirationState(long now, long expirationEpoch) {
		final long delta = expirationEpoch * 1000 - now;

		return  delta < -SIXTY_DAYS	? LicenseStats.LICENSE_STATE_BEYOND_GRACE_PERIOD :		// NOSONAR -- as presented, ternary is the clearest.
				delta < -THIRTY_DAYS? LicenseStats.LICENSE_STATE_GRACE_PERIOD_SECOND_MONTH :// NOSONAR
				delta < 0			? LicenseStats.LICENSE_STATE_GRACE_PERIOD_FIRST_MONTH :	// NOSONAR
				delta < THIRTY_DAYS	? LicenseStats.LICENSE_STATE_ONE_MONTH_UNTIL_EXPIREY :	// NOSONAR
									  LicenseStats.LICENSE_STATE_NOMINAL;					// NOSONAR
	}

	public LicenseStats(final long now, final Long expirationEpoch, final Integer maxNumberOfUsers, final Integer currentNumberOfUsers, final Boolean hasExpired,
			final Boolean supportsAudit, final Boolean supportsNotificationEmails, final Boolean supportsSNMP, final Boolean supportsDocumentUpload,
			final Boolean supportsContainerSharing, final Boolean supportQRCodeUpload, final Boolean supportsSMS) {
		licenseIsValid = true;
		this.licenseExpirationState 	= (expirationEpoch == null) ? LicenseStats.LICENSE_STATE_NOMINAL : calculateLicenseExpirationState(now, expirationEpoch);
		this.maxNumberOfUsers			= maxNumberOfUsers;
		this.currentNumberOfUsers		= currentNumberOfUsers;
		this.expirationEpoch			= expirationEpoch;
		this.hasExpired					= hasExpired;
		this.supportsAudit				= supportsAudit;
		this.supportsNotificationEmails = supportsNotificationEmails;
		this.supportsSNMP				= supportsSNMP;
		this.supportsDocumentUpload		= supportsDocumentUpload;
		this.supportsContainerSharing	= supportsContainerSharing;
		this.supportsSMS				= supportsSMS;
		this.setSupportQRCodeUpload(supportQRCodeUpload);
	}

	public Boolean getLicenseIsValid() {
		return licenseIsValid;
	}

	public void setLicenseIsValid(Boolean licenseIsValid) {
		this.licenseIsValid = licenseIsValid;
	}

	public int getLicenseExpirationState() {
		return licenseExpirationState;
	}

	public void setLicenseExpirationState(Integer licenseExpirationState) {
		this.licenseExpirationState = licenseExpirationState;
	}

	public Integer getCurrentNumberOfUsers() {
		return currentNumberOfUsers;
	}

	public void setCurrentNumberOfUsers(Integer currentNumberOfUsers) {
		this.currentNumberOfUsers = currentNumberOfUsers;
	}

	public Integer getMaxNumberOfUsers() {
		return maxNumberOfUsers;
	}

	public void setMaxNumberOfUsers(Integer maxNumberOfUsers) {
		this.maxNumberOfUsers = maxNumberOfUsers;
	}

	public Long getExpirationEpoch() {
		return expirationEpoch;
	}

	public void setExpirationEpoch(Long expirationEpoch) {
		this.expirationEpoch = expirationEpoch;
	}

	public Boolean getHasExpired() {
		return hasExpired;
	}

	public void setHasExpired(Boolean hasExpired) {
		this.hasExpired = hasExpired;
	}

	public Boolean getSupportsAudit() {
		return supportsAudit;
	}

	public void setSupportsAudit(Boolean supportsAudit) {
		this.supportsAudit = supportsAudit;
	}

	public Boolean getSupportsSNMP() {
		return supportsSNMP;
	}

	public void setSupportsSNMP(Boolean supportsSNMP) {
		this.supportsSNMP = supportsSNMP;
	}

	public Boolean getSupportsSMS() {
		return supportsSMS;
	}

	public void setSupportsSMS(Boolean supportsSMS) {
		this.supportsSMS = supportsSMS;
	}

	public Boolean getSupportsContainerSharing() {
		return supportsContainerSharing;
	}

	public void setSupportsContainerSharing(Boolean supportsContainerSharing) {
		this.supportsContainerSharing = supportsContainerSharing;
	}

	public Boolean getSupportsDocumentUpload() {
		return supportsDocumentUpload;
	}

	public void setSupportsDocumentUpload(Boolean supportsDocumentUpload) {
		this.supportsDocumentUpload = supportsDocumentUpload;
	}

	public Boolean getSupportsNotificationEmails() {
		return supportsNotificationEmails;
	}

	public void setSupportsNotificationEmails(Boolean supportsNotificationEmails) {
		this.supportsNotificationEmails = supportsNotificationEmails;
	}

	public Boolean getSupportQRCodeUpload() {
		return supportQRCodeUpload;
	}

	public void setSupportQRCodeUpload(Boolean supportQRCodeUpload) {
		this.supportQRCodeUpload = supportQRCodeUpload;
	}
}
