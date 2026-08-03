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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.CertificateInfoDTO;
import com.spenego.Obidos.shared.dto.LicenseKeyDTO;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.SmsKeyDTO;
import com.spenego.Obidos.shared.dto.SystemConfigDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public interface SystemConfigServiceAsync {
	/**
	 * Get the system configuration.
	 *
	 * @param creds
	 * @throws ServerSideException
	 */
	void get(AuthCredsDTO creds, AsyncCallback<SystemConfigDTO> callback);

	/**
	 * Update the system configuration.
	 *
	 * @param creds
	 * @throws ServerSideException
	 */
	void update(AuthCredsDTO creds, SystemConfigDTO config, AsyncCallback<Void> callback);
	
	/**
	 * Installs a base-64 license into the database.
	 * 
	 * @param license
	 * @return
	 * @throws ServerSideException
	 */
	void installLicense(AuthCredsDTO creds, String license, AsyncCallback<LicenseStats> callback);

	/**
	 * @return the license currently installed in the database
	 */
	void getCurrentLicense(AuthCredsDTO creds, AsyncCallback<LicenseKeyDTO> callback);

	void getFeaturesByLicense(AuthCredsDTO creds, AsyncCallback<String> callback);

	/**
	 * decodes the base64 license string and returns 
	 * 
	 * @param license
	 * @return 
	 */
	void decodeLicense(AuthCredsDTO creds, String license, AsyncCallback<LicenseKeyDTO> callback);
	
	
	/**
	 * Install nginx certificate. It does not really install the certificates,
	 * It just creates certificate and private key file. A cron job picks them
	 * up, installs and restarts nginx
	 * 
	 * @param creds
	 * @param pemCerts
	 * @param pemPrivateKey
	 * @param callback
	 * <p>
	 * @author spgdev@spenego.com - Jun 30, 2020
	 */
	void installNginxCertificates(AuthCredsDTO creds, String pemCerts, String pemPrivateKey, AsyncCallback<Void> callback);

	/**
	  * Install AD/LDAP CA/Server certificates.
	 * It does not really install the certificates,
	 * It just creates certificate file. A cron job picks them
	 * up and inserts to Java cacerts keystore
	 * 
	 * @param creds
	 * @param pemCerts
	 * @param callback
	 * <p>
	 * @author spgdev@spenego.com - Jun 30, 2020
	 */
	void installAdLdapCertificates(AuthCredsDTO creds, String pemCerts, AsyncCallback<Void> callback);

	/**
	 * Check if the content is actually a X509 certificate
	 * 
	 * @param creds
	 * @param pemCert
	 * @param callback
	 * <p>
	 * @author spgdev@spenego.com - Jul 1, 2020
	 */
	void validPEMCertifiate(AuthCredsDTO creds, String pemCert, AsyncCallback<Boolean> callback);

	/**
	 * check if AD/LDAP certificate is installed
	 * 
	 * @param creds
	 * <p>
	 * @author spgdev@spenego.com - Jul 2, 2020
	 */
	void adLdapCertInstalled(AuthCredsDTO creds, AsyncCallback<Boolean> callback);

	void getCertificateInfo(AuthCredsDTO creds, String pemChain, AsyncCallback<List<CertificateInfoDTO>> callback); 
	void getNginxCertificateInfo(AuthCredsDTO creds, AsyncCallback<List<CertificateInfoDTO>> callback);
	void getAdLdapCertificateInfo(AuthCredsDTO creds, AsyncCallback<List<CertificateInfoDTO>> callback);

	void getSmsConfig(AuthCredsDTO adminCreds, AsyncCallback<SmsKeyDTO> callback);
	void updateSMSConfig(AuthCredsDTO adminCreds, SmsKeyDTO smsKey, AsyncCallback<Void> callback);
	void deleteSmsConfig(AuthCredsDTO adminCreds, AsyncCallback<Void> callback);
	void sendTestSMS(AuthCredsDTO adminCreds, UserDTO recipient, String message, AsyncCallback<Void> callback);
	void sendTestSMS(AuthCredsDTO adminCreds, String recipientPhoneNumber, String message, AsyncCallback<Void> callback);
}


