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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LdapConfigurationResult;
import com.spenego.Obidos.shared.dto.LdapDTO;
import com.spenego.Obidos.shared.dto.SmtpConfigDTO;
import com.spenego.Obidos.shared.dto.SmtpEnvelope;

/**
 *
 * @author spgdev@spenego.com - Jan 29, 2017
 */
public interface AdminConfigServiceAsync
{
	void createLdapSettings(AuthCredsDTO credsDTO, LdapDTO ldapDTO, AsyncCallback<Long> callback) ;
	void getLdapSettings(AuthCredsDTO credsDTO, Long ldapId, AsyncCallback<LdapDTO> callback);
	void testLDAPConnection(AuthCredsDTO credsDTO, Long ldapId, AsyncCallback<Boolean> callback);
	void testLDAPConnection(AuthCredsDTO credsDTO, LdapDTO ldapDTO, AsyncCallback<Void> callback);
	void testLDAPAuthentication(AuthCredsDTO credsDTO, LdapDTO ldapDTO, String username, String password, AsyncCallback<Void> callback);

	void sendEmail(AuthCredsDTO credsDTO, SmtpConfigDTO smtpConfigDTO, SmtpEnvelope envelope, AsyncCallback<Void> callback);
	void sendNotificationTemplateTestEmail(AuthCredsDTO creds, SmtpEnvelope envelope, Long templateType, AsyncCallback<Void> callabck);

	/**
	 * Get a list of all the LDAP configurations that have a name that contains the searchString. Pass
	 * null for search string to get all configurations.
	 *
	 * @param credsDTO
	 * @param searchString
	 * @param first
	 * @param count
	 */
	void getAllLdapSettings(AuthCredsDTO credsDTO, String searchString, Integer first, Integer count, AsyncCallback<LdapConfigurationResult> callback);

	void updateLdapConfig(AuthCredsDTO credsDTO, LdapDTO ldapDTO, AsyncCallback<Void> callback);

	/**
     * Deletes an LDAP configuration.
     *
     * @param credsDTO
     * @param ldapCondigID
     */
	void deleteLdapConfig(AuthCredsDTO credsDTO, ArrayList<Long> ldapCondigIDs, AsyncCallback<Void> callback);

	void createSmtpConfig(AuthCredsDTO credsDTO, SmtpConfigDTO config, AsyncCallback<Long> callback);
	void getSmtpConfig(AuthCredsDTO credsDTO, String name, AsyncCallback<SmtpConfigDTO> callback);
	void smtpConfigExists(AuthCredsDTO credsDTO, String name, AsyncCallback<Boolean> callback);
	void updateSmtpConfig(AuthCredsDTO credsDTO, SmtpConfigDTO config, AsyncCallback<Void> callback);
	void deleteSmtpConfig(AuthCredsDTO credsDTO, ArrayList<Long> ids, AsyncCallback<Void> callback);
}
