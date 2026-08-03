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

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LdapConfigurationResult;
import com.spenego.Obidos.shared.dto.LdapDTO;
import com.spenego.Obidos.shared.dto.SmtpConfigDTO;
import com.spenego.Obidos.shared.dto.SmtpEnvelope;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * Service for admins to set/get various settings
 *
 * @author spgdev@spenego.com - Jan 29, 2017
 */
@RemoteServiceRelativePath("rpc/adminSettingsService")
public interface AdminConfigService extends RemoteService {
	public static class Utility {
		private Utility() { /* no instances */ }
		private static final AdminConfigServiceAsync instance = (AdminConfigServiceAsync) GWT.create(AdminConfigService.class);

		public static AdminConfigServiceAsync getInstance() {
			return instance;
		}
	}

	/**
	 * Creates an LDAP configuration record.
	 *
	 * @param credsDTO
	 * @param ldapDTO
	 * @return
	 * @throws ServerSideException
	 */
	Long createLdapSettings(AuthCredsDTO credsDTO, LdapDTO ldapDTO) throws ServerSideException;

	LdapDTO getLdapSettings(AuthCredsDTO credsDTO, Long id) throws ServerSideException;

	/**
	 * Get a list of all the LDAP configurations that have a name that contains
	 * the searchString. Pass null for search string to get all configurations.
	 *
	 * @param credsDTO
	 * @param searchString
	 * @param first
	 * @param count
	 * @return
	 * @throws ServerSideException
	 */
	LdapConfigurationResult getAllLdapSettings(AuthCredsDTO credsDTO, String searchString, Integer first, Integer count) throws ServerSideException;

	Void updateLdapConfig(AuthCredsDTO credsDTO, LdapDTO ldapDTO) throws ServerSideException;

	/**
	 *
	 * @param credsDTO
	 * @param ldapCondigID
	 * @return
	 * @throws ServerSideException
	 */
	Void deleteLdapConfig(AuthCredsDTO credsDTO, ArrayList<Long> ldapCondigIDs) throws ServerSideException;

	/**
	 * use this method when LdapDTO is not available and have to be obtained
	 * from database
	 *
	 * @param credsDTO
	 * @param ldapId
	 * @return
	 * @throws ServerSideException
	 *             <p>
	 * @author spgdev@spenego.com - Apr 16, 2017
	 */
	Boolean testLDAPConnection(AuthCredsDTO credsDTO, Long ldapId) throws ServerSideException;


	/**
	 * Use this method when LdapDTO is available as for example before saving
	 * ldap configuration from the gui, in that case LdapDTO can be constructed
	 * from the form
	 *
	 * @param credsDTO
	 * @param ldapDTO
	 * @return
	 * @throws ServerSideException
	 *             <p>
	 * @author spgdev@spenego.com - Apr 16, 2017
	 */
	Void testLDAPConnection(AuthCredsDTO credsDTO, LdapDTO ldapDTO) throws ServerSideException;
	Void testLDAPAuthentication(AuthCredsDTO credsDTO, LdapDTO ldapDTO, String username, String password) throws ServerSideException;

	Long createSmtpConfig(AuthCredsDTO credsDTO, SmtpConfigDTO config) throws ServerSideException;

	/**
	 * A convenience method to detect if the named SMTP configuration exists.
	 *
	 * @param credsDTO
	 * @param name
	 * @return
	 * @throws ServerSideException
	 */
	Boolean smtpConfigExists(AuthCredsDTO credsDTO, String name) throws ServerSideException;

	/**
	 * Get a single DTO.
	 *
	 * @param credsDTO
	 * @param name
	 *            Pass null to retrieve the default SMTP config.
	 * @return The smtp config with the specified name.
	 * @throws ServerSideException
	 */
	SmtpConfigDTO getSmtpConfig(AuthCredsDTO credsDTO, String name) throws ServerSideException;

	Void updateSmtpConfig(AuthCredsDTO credsDTO, SmtpConfigDTO config) throws ServerSideException;

	Void deleteSmtpConfig(AuthCredsDTO credsDTO, ArrayList<Long> ids) throws ServerSideException;

	Void sendEmail(AuthCredsDTO credsDTO, SmtpConfigDTO smtpConfigDTO, SmtpEnvelope envelope) throws ServerSideException;
	
	Void sendNotificationTemplateTestEmail(AuthCredsDTO creds, SmtpEnvelope envelope,  Long templateType);
}
