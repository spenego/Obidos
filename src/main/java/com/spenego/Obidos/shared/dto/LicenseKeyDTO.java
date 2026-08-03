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

// Issue #483
// Follow Java beans standard for a DTO.
// Setters were violating java beans convention causing weird serialization issue.
// spgdev@spenego.com - Jun 29, 2019

// WARNING: Do not modify this file.
// The source of this file is in the repo GenSpenegoLicense

public final class LicenseKeyDTO implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String licenseType;
	private String productName;
	private String companyName;
	private String companyEmail;
	private String customerId;
	private String phoneNumber;
	private String contactName;
	private String uuid;
	private String licenseTerms;
	private String nonce;
	private String signature;
	private Integer maxUsers;
	private Long expirationEpoch;
	private Long signingEpoch;
	private Boolean hasExpired;
	private Boolean allowDocumentUploading;
	private Boolean allowQRCodeUploading;
	private Boolean allowAuditing;
	private Boolean allowSMTP;
	private Boolean allowContainerSharing;
	private Boolean allowContainerOwnershipTransfer;
	private Boolean snmpSupport;
	private Boolean smsSupport;

	public Boolean getSmsSupport()
	{
		return smsSupport;
	}

	public void setSmsSupport(Boolean smsSupport)
	{
		this.smsSupport = smsSupport;
	}

	public Boolean getSnmpSupport()
	{
		return snmpSupport;
	}

	public void setSnmpSupport(Boolean snmpSupport)
	{
		this.snmpSupport = snmpSupport;
	}

	public LicenseKeyDTO()
	{
	}

	public LicenseKeyDTO(String s)
	{
		// why is this empty?
	}

	public String getCompanyName()
	{
		return companyName;
	}

	public void setCompanyName(String companyName)
	{
		this.companyName = companyName;
	}

	public Integer getMaxUsers()
	{
		return maxUsers;
	}

	public void setMaxUsers(Integer maxUsers)
	{
		this.maxUsers = maxUsers;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	public String getContactName()
	{
		return contactName;
	}

	public void setContactName(String contactName)
	{
		this.contactName = contactName;
	}

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}

	public String getProductName()
	{
		return productName;
	}

	public void setProductName(String productName)
	{
		this.productName = productName;
	}

	public String getCompanyEmail()
	{
		return companyEmail;
	}

	public void setCompanyEmail(String companyEmail)
	{
		this.companyEmail = companyEmail;
	}

	public Long getExpirationEpoch()
	{
		return expirationEpoch;
	}

	public void setExpirationEpoch(Long expirationEpoch)
	{
		this.expirationEpoch = expirationEpoch;
	}

	public Long getSigningEpoch()
	{
		return signingEpoch;
	}

	public void setSigningEpoch(Long signingEpoch)
	{
		this.signingEpoch = signingEpoch;
	}

	public String getLicenseTerms()
	{
		return licenseTerms;
	}

	public void setLicenseTerms(String licenseTerms)
	{
		this.licenseTerms = licenseTerms;
	}

	public String getSignature()
	{
		return signature;
	}

	public void setSignature(String signature)
	{
		this.signature = signature;
	}

	public String getNonce()
	{
		return nonce;
	}

	public void setNonce(String nonce)
	{
		this.nonce = nonce;
	}

	public Boolean getHasExpired()
	{
		return hasExpired;
	}

	public void setHasExpired(Boolean hasExpired)
	{
		this.hasExpired = hasExpired;
	}

	public String getCustomerId()
	{
		return customerId;
	}

	public void setCustomerId(String customerId)
	{
		this.customerId = customerId;
	}

	public Boolean getAllowAuditing()
	{
		return allowAuditing;
	}

	public void setAllowAuditing(Boolean allowAuditing)
	{
		this.allowAuditing = allowAuditing;
	}

	public Boolean getAllowSMTP()
	{
		return allowSMTP;
	}

	public void setAllowSMTP(Boolean allowSMTP)
	{
		this.allowSMTP = allowSMTP;
	}

	public Boolean getAllowContainerSharing()
	{
		return allowContainerSharing;
	}

	public void setAllowContainerSharing(Boolean allowContainerSharing)
	{
		this.allowContainerSharing = allowContainerSharing;
	}

	public Boolean getAllowContainerOwnershipTransfer()
	{
		return allowContainerOwnershipTransfer;
	}

	public void setAllowContainerOwnershipTransfer(Boolean allowContainerOwnershipTransfer)
	{
		this.allowContainerOwnershipTransfer = allowContainerOwnershipTransfer;
	}

	public Boolean getAllowDocumentUploading()
	{
		return allowDocumentUploading;
	}

	public void setAllowDocumentUploading(Boolean allowDocumentUploading)
	{
		this.allowDocumentUploading = allowDocumentUploading;
	}

	public Boolean getAllowQRCodeUploading()
	{
		return allowQRCodeUploading;
	}

	public void setAllowQRCodeUploading(Boolean allowQRCodeUploading)
	{
		this.allowQRCodeUploading = allowQRCodeUploading;
	}

	public String getLicenseType()
	{
		return licenseType;
	}

	public void setLicenseType(String licenseType)
	{
		this.licenseType = licenseType;
	}
}
