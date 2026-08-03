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

package com.spenego.Obidos.server.actions;

import java.util.List;

import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.shared.dto.CertificateInfoDTO;
import com.spenego.Obidos.shared.dto.LicenseKeyDTO;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.SystemConfigDTO;

public interface SystemConfigActions {
	SystemConfigDTO get();
	Void update(User admin, SystemConfigDTO configuration);
	LicenseStats installLicense(User admin, String license);
	LicenseKeyDTO getCurrentLicense();
	LicenseKeyDTO decodeLicense(String license);
	Void installNginxCertificates(User admin, String pemCerts, String pemPrivateKey);
	Void installAdLdapCertificates(User admin, String pemCerts);
	Boolean validPEMCert(User admin, String pemCert);
	Boolean adLdapCertInstalled(User admin);
	List<CertificateInfoDTO> getCertificateInfo(User admin, String pemChain);
	List<CertificateInfoDTO> getNginxCertificateInfo(User admin);
	List<CertificateInfoDTO> getAdLdapCertificateInfo(User admin);
	Void updateSMSKey(byte[] smsKey);
	byte[] getSMSKey();
}
