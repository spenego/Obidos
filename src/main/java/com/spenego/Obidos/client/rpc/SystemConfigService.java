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

package com.spenego.Obidos.client.rpc;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.CertificateInfoDTO;
import com.spenego.Obidos.shared.dto.LicenseKeyDTO;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.SmsKeyDTO;
import com.spenego.Obidos.shared.dto.SystemConfigDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@RemoteServiceRelativePath("rpc/systemConfigService")
public interface SystemConfigService extends RemoteService {
	public static class Utility {
		private final static SystemConfigServiceAsync instance = (SystemConfigServiceAsync) GWT.create(SystemConfigService.class);
		public static SystemConfigServiceAsync getInstance() { return instance; }
	}

	/**
	 * Get the system configuration.
	 *
	 * @param creds
	 * @throws ServerSideException
	 */
	SystemConfigDTO get(AuthCredsDTO creds) throws ServerSideException;

	/**
	 * Update the system configuration.
	 *
	 * @param creds
	 * @throws ServerSideException
	 */
	Void update(AuthCredsDTO creds, SystemConfigDTO config) throws ServerSideException;

	/**
	 * Installs a base-64 license into the database.
	 * 
	 * @param license
	 * @return
	 * @throws ServerSideException
	 */
	LicenseStats installLicense(AuthCredsDTO creds, String license) throws ServerSideException;

	/**
	 * @return the license currently installed in the database
	 */
	LicenseKeyDTO getCurrentLicense(AuthCredsDTO creds) throws ServerSideException;
	
	String getFeaturesByLicense(AuthCredsDTO creds) throws ServerSideException;

	/**
	 * decodes the base64 license string and returns 
	 * 
	 * @param license
	 * @return 
	 */
	LicenseKeyDTO decodeLicense(AuthCredsDTO creds, String license) throws ServerSideException;
	
	/**
	 * Install nginx certificate. It does not really install the certificates,
	 * It just creates certificate and private key file. A cron job picks them
	 * up, install and restarts nginx;
	 * 
	 * @param creds
	 * @param pemCerts
	 * @param pemPrivateKey
	 * @return
	 * @throws ServerSideException
	 * <p>
	 * @author spgdev@spenego.com - Jun 30, 2020
	 */
	Void installNginxCertificates(AuthCredsDTO creds, String pemCerts, String pemPrivateKey) throws ServerSideException;
	
	/**
	 * Install AD/LDAP CA/Server certificates.
	 * It does not really install the certificates,
	 * It just creates certificate file. A cron job picks them
	 * up and inserts to Java cacerts keystore
	 * 
	 * @param creds
	 * @param pemCerts
	 * @return
	 * @throws ServerSideException
	 * <p>
	 * @author spgdev@spenego.com - Jun 30, 2020
	 */
	Void installAdLdapCertificates(AuthCredsDTO creds, String pemCerts) throws ServerSideException;
	
	/**
	 * Check if the content is actually a X509 certificate
	 * 
	 * @param creds
	 * @param pemCert
	 * @return
	 * @throws ServerSideException
	 * <p>
	 * @author spgdev@spenego.com - Jul 1, 2020
	 */
	Boolean validPEMCertifiate(AuthCredsDTO creds, String pemCert) throws ServerSideException;
	
	Boolean adLdapCertInstalled(AuthCredsDTO creds) throws ServerSideException;
	
	List<CertificateInfoDTO> getCertificateInfo(AuthCredsDTO creds, String pemChain) throws ServerSideException;
	List<CertificateInfoDTO> getNginxCertificateInfo(AuthCredsDTO creds) throws ServerSideException;
	List<CertificateInfoDTO> getAdLdapCertificateInfo(AuthCredsDTO creds) throws ServerSideException;

	SmsKeyDTO getSmsConfig(AuthCredsDTO adminCreds) throws ServerSideException;
	Void updateSMSConfig(AuthCredsDTO adminCreds, SmsKeyDTO smsKey) throws ServerSideException;
	Void deleteSmsConfig(AuthCredsDTO adminCreds) throws ServerSideException;
	Void sendTestSMS(AuthCredsDTO adminCreds, UserDTO user, String message) throws ServerSideException;
	Void sendTestSMS(AuthCredsDTO adminCreds, String recipientPhoneNumber, String message) throws ServerSideException;
}
