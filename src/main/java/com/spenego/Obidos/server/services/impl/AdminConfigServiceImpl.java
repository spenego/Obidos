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

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spenego.Obidos.client.rpc.AdminConfigService;
import com.spenego.Obidos.server.actions.EmailActions;
import com.spenego.Obidos.server.actions.LdapConfigActions;
import com.spenego.Obidos.server.actions.SmtpConfigActions;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LdapConfigurationResult;
import com.spenego.Obidos.shared.dto.LdapDTO;
import com.spenego.Obidos.shared.dto.SmtpConfigDTO;
import com.spenego.Obidos.shared.dto.SmtpEnvelope;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * Spring service that implements AdminConfigService rpc
 *
 * @author spgdev@spenego.com - Jan 29, 2017
 */
@Service("adminSettingsService")
public final class AdminConfigServiceImpl extends ObidosService implements AdminConfigService {
	private static final Logger logger = LoggerFactory.getLogger(AdminConfigServiceImpl.class);

	@Autowired private final LdapConfigActions ldapConfigActions = null;
	@Autowired private final SmtpConfigActions smtpConfigActions = null;
	@Autowired private final EmailActions emailActions = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Transactional @Override
	public Long createLdapSettings(final AuthCredsDTO creds, final LdapDTO ldapDTO) throws ServerSideException {
		return adminSettingsFunction(creds, "create an LDAP configuration", "createLdapSettings", true, admin -> ldapConfigActions.create(admin, ldapDTO));
	}

	@Transactional(readOnly = true) @Override
	public LdapDTO getLdapSettings(final AuthCredsDTO credsDTO, final Long id) throws ServerSideException {
		return adminSettingsFunction(credsDTO, "get the LDAP settings", "getLdapSettings", false, admin -> ldapConfigActions.get(id));
	}

	@Transactional(readOnly = true) @Override
	public Boolean testLDAPConnection(final AuthCredsDTO creds, final Long ldapId) throws ServerSideException {
		return adminSettingsFunction(creds, "test an LDAP connection", "testLDAPConnectoin", true, admin -> ldapConfigActions.testConnection(ldapId));
	}

	@Transactional(readOnly = true) @Override
	public Void testLDAPConnection(final AuthCredsDTO creds, final LdapDTO ldapDTO) throws ServerSideException {
		return adminSettingsFunction(creds, "test an LDAP connection", "testLDAPConnectoin", true, admin -> ldapConfigActions.testConnection(ldapDTO));
	}

	@Transactional(readOnly = true) @Override
	public Void testLDAPAuthentication(AuthCredsDTO credsDTO, LdapDTO ldapDTO, String username, String password) throws ServerSideException {
		return adminSettingsFunction(credsDTO, "test an LDAP authentication", "testLDAPAuthentication", true, admin -> ldapConfigActions.testAuthentication(ldapDTO, username, password));
	}

	@Transactional(readOnly = true) @Override
	public LdapConfigurationResult getAllLdapSettings(final AuthCredsDTO creds, final String searchString, final Integer first, final Integer count) throws ServerSideException {
		return adminSettingsFunction(creds, "get a list of ldap configurations", "getAllLdapSettings", false, admin -> ldapConfigActions.getAllConfigs(admin, searchString, first, count));
	}

	@Transactional @Override
	public Void updateLdapConfig(final AuthCredsDTO creds, final LdapDTO ldapDTO) throws ServerSideException {
		return adminSettingsFunction(creds, "update an LDAP configuration", "updateLdapConfig", true, admin -> ldapConfigActions.update(admin, ldapDTO));
	}

	@Transactional @Override
	public Void deleteLdapConfig(final AuthCredsDTO creds, final ArrayList<Long> ldapConfigIDs) throws ServerSideException {
		return adminSettingsFunction(creds, "delete LDAP configuration", "getAllLdapSettings", true, admin -> ldapConfigActions.delete(admin, ldapConfigIDs));
	}

	@Transactional @Override
	public Long createSmtpConfig(final AuthCredsDTO creds, final SmtpConfigDTO config) throws ServerSideException {
		return adminSettingsFunction(creds, "create an SMTP configuration", "createSmtpConfig", true, admin -> smtpConfigActions.create(admin, config));
	}

	@Transactional(readOnly = true) @Override
	public SmtpConfigDTO getSmtpConfig(final AuthCredsDTO creds, final String name) throws ServerSideException {
		return adminSettingsFunction(creds, "get an SMTP configuration", "getSmtpConfig", false, admin -> smtpConfigActions.get(admin, name));
	}

	@Transactional(readOnly = true) @Override
	public Boolean smtpConfigExists(final AuthCredsDTO creds, final String name) throws ServerSideException {
		try {
			getSmtpConfig(creds, name);
			return Boolean.TRUE;
		} catch (final NoSuchRecordException ex) {
			return Boolean.FALSE;
		}
	}

	@Transactional @Override
	public Void updateSmtpConfig(AuthCredsDTO creds, SmtpConfigDTO config) throws ServerSideException {
		return adminSettingsFunction(creds, "update the SMTP configuration", "updateSmtpConfig", true, admin -> smtpConfigActions.update(admin, config));
	}

	@Transactional @Override
	public Void deleteSmtpConfig(final AuthCredsDTO creds, final ArrayList<Long> ids) throws ServerSideException {
		return adminSettingsFunction(creds, "delete the SMTP configuration", "deleteSmtpConfig", true, admin -> smtpConfigActions.delete(admin, ids));
	}

	@Transactional @Override
	public Void sendEmail(final AuthCredsDTO creds, final SmtpConfigDTO config, final SmtpEnvelope envelope) throws ServerSideException {
		return adminFunction(creds, "send an email via SMTP", "sendEmail", true, admin -> emailActions.sendEmail(admin, config, envelope));
	}

	@Override
	public Void sendNotificationTemplateTestEmail(final AuthCredsDTO creds, final SmtpEnvelope envelope, final Long templateType) {
		return adminFunction(creds, "send a test email with Notification Template type", "sendEmail", true, admin -> emailActions.sendNotificationTemplateTestEmail(admin, envelope, templateType));
	}


		/*******  T h e   C o d e   G r a v e y a r d   **********/
	/*
	@Transactional(readOnly = true)
	@Override
	public LdapDTO getLdapSettings(final AuthCredsDTO creds, final String name) throws ServerSideException {
		return adminFunction(creds, "get the LDAP settings", "getLdapSettings", false, admin -> ldapConfigActions.get(name));
	}

	@Transactional(readOnly = true)
	@Override
	public SmtpConfigResult getSmtpConfigs(final AuthCredsDTO creds, final String searchString, final Integer first, final Integer count, final ArrayList<OrderBy> orderby) throws ServerSideException {
		return adminFunction(creds, "get the SMTP configuration", "getSmtpConfigs", false, admin -> smtpConfigActions.getList(admin, searchString, first, count, orderby));
	}

	@Transactional
	@Override
	public Void sendEmail(AuthCredsDTO creds, final SmtpEnvelope envelope) throws ServerSideException {
		return adminFunction(creds, "send an email via SMTP", "sendEmail", true, admin -> emailActions.sendEmail(admin, envelope));
	}

	@Override
	public Void sendNotificationTemplateTestEmail(final AuthCredsDTO creds, final SmtpEnvelope envelope, final NotificationTemplateJSONDTO dto) {
		return adminFunction(creds, "send a test email wth Notification Template", "sendEmail", true, admin -> emailActions.sendEmail(admin, envelope, dto));
	}

	@Transactional(readOnly = true)
	@Override
	public SmtpConfigDTO getSmtpConfig(final AuthCredsDTO creds, final Long id) throws ServerSideException {
		return adminFunction(creds, "get an SMTP configuration", "getSmtpConfig", false, admin -> smtpConfigActions.get(admin, id));
	} */
}
