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

package com.spenego.Obidos.server.actions.impl;

import java.util.HashMap;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.LdapConfigActions;
import com.spenego.Obidos.server.model.Audit;
import com.spenego.Obidos.server.model.Ldap;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.LdapConfigOperations;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.security.LDAPSecurity;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.validators.ValidateBean;
import com.spenego.Obidos.server.validators.ValidateUrl;
import com.spenego.Obidos.shared.dto.LdapConfigurationResult;
import com.spenego.Obidos.shared.dto.LdapDTO;
import com.spenego.Obidos.shared.exceptions.LdapNotConfiguredException;
import com.spenego.Obidos.shared.exceptions.RecordModifiedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class LdapConfigActionsImpl extends CryptoActions<Ldap> implements LdapConfigActions {
	private static final Logger logger = LoggerFactory.getLogger(LdapConfigActionsImpl.class);

	@Autowired private final LdapConfigOperations ldapConfigOperations = null;

	public LdapConfigActionsImpl() {
		super("ldap_crypto.properties", "LDAP Crypto Config");
	}

	@Override
	protected final Logger getLogger() { return logger; }

	@Override
	protected final String elementName() {
		return "ldap config";
	}

	@Override
	protected final Operations<Ldap> getOperations() {
		return ldapConfigOperations;
	}

	@Override
	protected final Integer getAuditDeleteAction() {
		return Audit.DELETE_LDAP;
	}

	private LdapDTO decryptBindPass(final LdapDTO ldapDTO) {
		final String pass = ldapDTO.getBindPass();
		if (pass != null) {
			ldapDTO.setBindPass(decrypt(pass));
		}
		return ldapDTO;
	}

	private Ldap encryptBindPass(final Ldap ldap) {
		String pass = ldap.getBindPass();
		if (pass != null) {
			ldap.setBindPass(encrypt(pass));
		}
		return ldap;
	}

	@Override
	public LdapDTO get(final String name) throws LdapNotConfiguredException {
		return wrapAction(() -> decryptBindPass(convert(ldapConfigOperations.get(name), LdapDTO.class)));
	}

	@Override
	public LdapDTO get(final Long id) throws LdapNotConfiguredException {
		return wrapAction(() -> decryptBindPass(convert(getModel(id), LdapDTO.class)));
	}

	@Override
	public Long create(final User admin, final LdapDTO config) throws RecordModifiedException, ServerSideException {
		logger.syslogInfo(() -> "Admin " + admin.getUsername() + "Creating LDAP configuration " + config.getName());

		logger.info(() -> "Validate LdapDTO based on annotations.");
		ValidateBean.validate(config);
		logger.info(() -> "Validate LDAP URL ----------------------------------");
		ValidateUrl.validateLdapUrl(config.getLdapuri());
		logger.info(() -> "Done Validating LdapDTO");

		return wrapAction(() -> {
			final Ldap ldap = encryptBindPass(convert(config, Ldap.class));
			ldapConfigOperations.create(ldap);
			audit(Audit.CREATE_LDAP, admin.getUsername(), admin.getId(), ldap, ldap.getId());
			return ldap.getId();});
	}

	/**
	 * Use JNDI to test LDAP connection Ref:
	 * http://www.codejava.net/coding/connecting-to-ldap-server-using-jndi-in-java
	 *
	 * @author spgdev@spenego.com
	 */
	@Override
	public Boolean testConnection(final Long id)
	{
		final Ldap ldap = getModel(id);
		if (ldap == null) {
			throw new ServerSideException("LDAP configuration not found.");
		}
		final HashMap<String, String> env = new HashMap<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldap.getLdapuri());
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		String bindDN = ldap.getBindDn();
		if (bindDN != null)
		{
			env.put(Context.SECURITY_PRINCIPAL, bindDN);
		}
		String bindPass = ldap.getBindPass();
		if (bindPass != null)
		{
			env.put(Context.SECURITY_CREDENTIALS, bindPass);
		}
		try
		{
			final DirContext ctx = new InitialDirContext(new Hashtable<>(env));
			logger.info(() -> "Connected to LDAP server");
			ctx.close();
			return true;
		} catch (final Throwable e)
		{
			logger.exception(e);
			return false;
		}
	}

	/**
	 * This method is used for testing connection before saving the settings
	 * from GUI
	 */
	@Override
	public Void testConnection(final LdapDTO ldapDTO) throws ServerSideException
	{
		LDAPSecurity.testConnection(ldapDTO);
		return null;
	}

	//
	// Test authentication of a user with data from screen, not from database
	@Override
	public Void testAuthentication(final LdapDTO ldapDTO, final String username, final String password) throws ServerSideException
	{
		LDAPSecurity.testAuthenticate(ldapDTO, username, password);
		return null;
	}


	@Override
	public LdapConfigurationResult getAllConfigs(final User admin, final String searchString, final Integer first, final Integer count) {
		return new LdapConfigurationResult(first, count, null, (a,b) -> convert(a,b),
				() -> ldapConfigOperations.getCount(searchString),
				() -> ldapConfigOperations.getStream(searchString, first, count));
	}

	@Override
	public Void update(final User admin, final LdapDTO config) {
		wrapAction(() -> ldapConfigOperations.update(encryptBindPass(convert(config, Ldap.class))));
		audit(Audit.UPDATE_LDAP, admin.getUsername(), admin.getId(), config, 0L);

		return null;
	}

}
