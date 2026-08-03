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
import java.util.List;
import java.util.Map;

/**
 *
 * @author spgdev@spenego.com - Jul 3, 2020
 */
public class CertificateInfoDTO implements Serializable
{

	/**
	 * @author spgdev@spenego.com - Jul 3, 2020
	 */
	private static final long serialVersionUID = -3109840311835194750L;

	private String subjectNameHeading;
	private String subjectCommonName;

	private String issuerNameHeading;
	private String issuerCountry;
	private String issuerOrganization;
	private String issuerCommonName;

	private String validityHeading;
	private Date notBeforeDate;
	private Date notAfterDate;

	private String subjectAltNamesHeading;
	private List<String> ipaAltNames;
	private List<String> dnsAltNames;
	private Boolean isCA;

	private String publicKeyInfoHeading;
	private String publicKeyAlgorithm;
	private int keySize;

	private String miscHeading;
	private String serialNumber;
	private String signatureAlgorithmString;
	private int version;
	private Date installDate;

	private String fingerPrintHeading;
	private Map<String, String> fingerprints;
	public String getSubjectNameHeading()
	{
		return subjectNameHeading;
	}
	public void setSubjectNameHeading(String subjectNameHeading)
	{
		this.subjectNameHeading = subjectNameHeading;
	}
	public String getSubjectCommonName()
	{
		return subjectCommonName;
	}
	public void setSubjectCommonName(String subjectCommonName)
	{
		this.subjectCommonName = subjectCommonName;
	}
	public String getIssuerNameHeading()
	{
		return issuerNameHeading;
	}
	public void setIssuerNameHeading(String issuerNameHeading)
	{
		this.issuerNameHeading = issuerNameHeading;
	}
	public String getIssuerCountry()
	{
		return issuerCountry;
	}
	public void setIssuerCountry(String issuerCountry)
	{
		this.issuerCountry = issuerCountry;
	}
	public String getIssuerOrganization()
	{
		return issuerOrganization;
	}
	public void setIssuerOrganization(String issuerOrganization)
	{
		this.issuerOrganization = issuerOrganization;
	}
	public String getIssuerCommonName()
	{
		return issuerCommonName;
	}
	public void setIssuerCommonName(String issuerCommonName)
	{
		this.issuerCommonName = issuerCommonName;
	}
	public String getValidityHeading()
	{
		return validityHeading;
	}
	public void setValidityHeading(String validityHeading)
	{
		this.validityHeading = validityHeading;
	}
	public Date getNotBeforeDate()
	{
		return notBeforeDate;
	}
	public void setNotBeforeDate(Date notBeforeDate)
	{
		this.notBeforeDate = notBeforeDate;
	}
	public Date getNotAfterDate()
	{
		return notAfterDate;
	}
	public void setNotAfterDate(Date notAfterDate)
	{
		this.notAfterDate = notAfterDate;
	}
	public String getSubjectAltNamesHeading()
	{
		return subjectAltNamesHeading;
	}
	public void setSubjectAltNamesHeading(String subjectAltNamesHeading)
	{
		this.subjectAltNamesHeading = subjectAltNamesHeading;
	}
	public String getPublicKeyInfoHeading()
	{
		return publicKeyInfoHeading;
	}
	public void setPublicKeyInfoHeading(String publicKeyInfoHeading)
	{
		this.publicKeyInfoHeading = publicKeyInfoHeading;
	}

	public int getKeySize()
	{
		return keySize;
	}
	public void setKeySize(int keySize)
	{
		this.keySize = keySize;
	}
	public String getMiscHeading()
	{
		return miscHeading;
	}
	public void setMiscHeading(String miscHeading)
	{
		this.miscHeading = miscHeading;
	}
	public String getSerialNumber()
	{
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber)
	{
		this.serialNumber = serialNumber;
	}

	public int getVersion()
	{
		return version;
	}
	public void setVersion(int version)
	{
		this.version = version;
	}
	public String getFingerPrintHeading()
	{
		return fingerPrintHeading;
	}
	public void setFingerPrintHeading(String fingerPrintHeading)
	{
		this.fingerPrintHeading = fingerPrintHeading;
	}
	public Map<String, String> getFingerprints()
	{
		return fingerprints;
	}
	public void setFingerprints(Map<String, String> fingerprints)
	{
		this.fingerprints = fingerprints;
	}
	public List<String> getIpaAltNames()
	{
		return ipaAltNames;
	}
	public void setIpaAltNames(List<String> ipaAltNames)
	{
		this.ipaAltNames = ipaAltNames;
	}
	public List<String> getDnsAltNames()
	{
		return dnsAltNames;
	}
	public void setDnsAltNames(List<String> dnsAltNames)
	{
		this.dnsAltNames = dnsAltNames;
	}
	public String getPublicKeyAlgorithm()
	{
		return publicKeyAlgorithm;
	}
	public void setPublicKeyAlgorithm(String publicKeyAlgorithm)
	{
		this.publicKeyAlgorithm = publicKeyAlgorithm;
	}
	public String getSignatureAlgorithmString()
	{
		return signatureAlgorithmString;
	}
	public void setSignatureAlgorithmString(String signatureAlgorithmString)
	{
		this.signatureAlgorithmString = signatureAlgorithmString;
	}
	public Boolean getIsCA()
	{
		return isCA;
	}
	public void setIsCA(Boolean isCA)
	{
		this.isCA = isCA;
	}
	public Date getInstallDate()
	{
		return installDate;
	}
	public void setInstallDate(Date installDate)
	{
		this.installDate = installDate;
	}
}
