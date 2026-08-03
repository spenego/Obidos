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

package com.spenego.Obidos.server.services.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.spenego.Obidos.client.rpc.SystemConfigService;
import com.spenego.Obidos.server.actions.SMSActions;
import com.spenego.Obidos.server.actions.SystemConfigActions;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.CertificateInfoDTO;
import com.spenego.Obidos.shared.dto.LicenseKeyDTO;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.SmsKeyDTO;
import com.spenego.Obidos.shared.dto.SystemConfigDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public class SystemConfigServiceImpl extends ObidosService implements SystemConfigService {
	private static final Logger logger = LoggerFactory.getLogger(SystemConfigServiceImpl.class);

	@Autowired private final SystemConfigActions systemConfigActions = null;
	@Autowired private final SMSActions smsActions = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Transactional(readOnly = true)
	@Override
	public SystemConfigDTO get(final AuthCredsDTO creds) throws ServerSideException {
		return adminSettingsFunction(creds, "get the system configuration", "get", false, admin -> systemConfigActions.get());
	}

	@Transactional
	@Override
	public Void update(final AuthCredsDTO creds, final SystemConfigDTO configuration) throws ServerSideException {
		return adminSettingsFunction(creds, "update the system configuration", "update", true, admin -> systemConfigActions.update(admin, configuration));
	}

	@Transactional
	@Override
	public LicenseStats installLicense(final AuthCredsDTO creds, final String license) throws ServerSideException {
		return adminSettingsFunction(creds, "install a license", "installLicense", false, admin -> systemConfigActions.installLicense(admin, license));
	}

	@Transactional(readOnly = true)
	@Override
	public LicenseKeyDTO getCurrentLicense(final AuthCredsDTO creds) throws ServerSideException {
		return userOrAdminFunction(creds, "get the current license", "getCurrentLicense", false, admin -> systemConfigActions.getCurrentLicense());
	}

	@Transactional(readOnly = true)
	@Override
	public LicenseKeyDTO decodeLicense(final AuthCredsDTO creds, final String license) throws ServerSideException {
		return adminFunction(creds, "decode the license", "decodeLicense", false, admin -> systemConfigActions.decodeLicense(license));
	}

	@Override
	public Void installNginxCertificates(final AuthCredsDTO creds, final String pemCerts, String pemPrivateKey) throws ServerSideException {
		return adminSettingsFunction(creds, "install nginx certificate", "installNginxCertificates", false, admin -> systemConfigActions.installNginxCertificates(admin, pemCerts, pemPrivateKey));
	}

	@Override
	public Void installAdLdapCertificates(final AuthCredsDTO creds, final String pemCerts) throws ServerSideException {
		return adminSettingsFunction(creds, "install ad/ldap certificate", "installAdLdapCertificates", false, admin -> systemConfigActions.installAdLdapCertificates(admin, pemCerts));
	}

	@Override
	public Boolean validPEMCertifiate(final AuthCredsDTO creds, String pemCert) throws ServerSideException {
		return adminFunction(creds, "install ad/ldap certificate", "installAdLdapCertificates", false, admin -> systemConfigActions.validPEMCert(admin, pemCert));
	}

	@Override
	public Boolean adLdapCertInstalled(final AuthCredsDTO creds) throws ServerSideException {
		return Boolean.FALSE;
	}

	@Override
	public List<CertificateInfoDTO> getNginxCertificateInfo(final AuthCredsDTO creds) throws ServerSideException {
		return adminFunction(creds, "get nginx certificates info", "getNginxCertificateInfo", false, systemConfigActions::getNginxCertificateInfo);
	}

	@Override
	public List<CertificateInfoDTO> getAdLdapCertificateInfo(final AuthCredsDTO creds) throws ServerSideException {
		return adminFunction(creds, "get ad/ldap certificates info", "getAdLdapCertificateInfo", false, systemConfigActions::getAdLdapCertificateInfo);
	}

	@Override
	public List<CertificateInfoDTO> getCertificateInfo(final AuthCredsDTO creds, final String pemChain) throws ServerSideException {
		return adminFunction(creds, "get certificates info", "getCertificateInfo", false, admin -> systemConfigActions.getCertificateInfo(admin, pemChain));
	}

	@Override
	public String getFeaturesByLicense(final AuthCredsDTO creds) throws ServerSideException {
		try {
			return ServerUtils.readTextFile(getClass(), "features", "features.html");
		} catch (final IOException e) {
			throw new ServerSideException(e.getMessage());
		}
	}

	@Override
	public SmsKeyDTO getSmsConfig(final AuthCredsDTO adminCreds) throws ServerSideException {
		return adminFunction(adminCreds, "get SMS Config", "getSmsConfig", false, admin -> smsActions.getSMSConfig(admin));
	}

	@Override
	public Void updateSMSConfig(final AuthCredsDTO adminCreds, final SmsKeyDTO smsKeyDTO) throws ServerSideException {
		return adminSettingsFunction(adminCreds, "update SMS Config", "updateSMSConfig", false, admin -> smsActions.updateSMSConfig(admin, smsKeyDTO));
	}
	
	@Override
	public Void deleteSmsConfig(final AuthCredsDTO adminCreds) throws ServerSideException {
		return adminSettingsFunction(adminCreds, "delete SMS config", "deleteSmsConfig", false, admin -> smsActions.deleteSmsConfig(admin));
	}

	@Override
	public Void sendTestSMS(final AuthCredsDTO adminCreds, final UserDTO recipient, final String message) throws ServerSideException {
		return adminSettingsFunction(adminCreds, "send test SMS", "sendTestSMS", false, admin -> smsActions.sendTestSms(recipient, message));
	}

	// admin has access to SMS provider credentials, therefore an admin can send 
	// SMS to any phone number as long as license allows it
	@Override
	public Void sendTestSMS(AuthCredsDTO adminCreds, final String recipientPhoneNumber, final String message) throws ServerSideException {
		return adminSettingsFunction(adminCreds, "send a test SMS", "sendTestSms", false, admin -> smsActions.sendTestSms(recipientPhoneNumber, message));
	}
}
