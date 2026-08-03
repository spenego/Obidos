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

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.SmtpConfigActions;
import com.spenego.Obidos.server.model.Audit;
import com.spenego.Obidos.server.model.SmtpConfig;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.operations.SmtpConfigOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.validators.ValidateBean;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.SmtpConfigDTO;
import com.spenego.Obidos.shared.dto.SmtpConfigResult;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public final class SmtpConfigActionsImpl extends CryptoActions<SmtpConfig> implements SmtpConfigActions {
	private static final Logger logger = LoggerFactory.getLogger(SmtpConfigActionsImpl.class);

	private static final String DEFAULT = "default";

	@Autowired private final SmtpConfigOperations smtpConfigOperations = null;

	@Override
	protected final Logger getLogger() {
		return logger;
	}

	@Override
	protected final Operations<SmtpConfig> getOperations() {
		return smtpConfigOperations;
	}

	@Override
	protected final Integer getAuditDeleteAction() {
		return Audit.DELETE_SMTP_CONFIG;
	}

	@Override
	protected final String elementName() {
		return "smtp config";
	}

	public SmtpConfigActionsImpl() {
		super("smtp_crypto.properties", "SMTP Crypto Config");
	}

	private SmtpConfig convert(final SmtpConfigDTO dto) {
		return convert(dto, SmtpConfig.class);
	}

	private SmtpConfigDTO convert(final SmtpConfig smtpConfig) {
		return convert(smtpConfig, SmtpConfigDTO.class);
	}

	private SmtpConfigDTO decryptSMTPPass(final SmtpConfigDTO config) {
		final String pass = config.getSmtpPassword();
		if (pass != null) {
			config.setSmtpPassword(decrypt(pass));
		}
		return config;
	}

	private SmtpConfig encryptSMTPPass(final SmtpConfig config) {
		String pass = config.getSmtpPassword();
		if (pass != null) {
			config.setSmtpPassword(encrypt(pass));
		}
		return config;
	}

	@Override
	public Long create(final User admin, final SmtpConfigDTO config) {
		logger.syslogInfo(() -> "Admin " + admin.getUsername() + "Creating SMTP configuration for server " + config.getSmtpServer());
		ValidateBean.validate(config);

		if (config.getName() == null) {
			config.setName(DEFAULT);
		}

		return wrapAction(() -> {
			final SmtpConfig c = encryptSMTPPass(convert(config));
			final Long id = smtpConfigOperations.create(c);
			audit(Audit.CREATE_SMTP_CONFIG, admin.getUsername(), admin.getId(), c.getSmtpServer(), id);
			return id;
		});
	}

	@Override
	public SmtpConfigDTO get(final Long id) {
		return wrapAction(() -> decryptSMTPPass(convert(getModel(id))));
	}

	@Override
	public SmtpConfigDTO get(final User admin, final Long id) {
		return get(id);
	}

	@Override
	public SmtpConfigDTO get(final String name) {
		final List<SmtpConfig> list = smtpConfigOperations.getByName(name == null ? DEFAULT : name).collect(Collectors.toList());
		if (list == null || list.isEmpty())
			throw new NoSuchRecordException();
		return wrapAction(() -> decryptSMTPPass(convert(list.get(0))));
	}

	@Override
	public SmtpConfigDTO get(final User admin, final String name) {
		return get(name);
	}

	@Override
	public SmtpConfigResult getList(final User admin, final String search, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return new SmtpConfigResult(first, count, null, (a,b) -> convert(a,b), () -> smtpConfigOperations.getCount(search), () -> smtpConfigOperations.getStream(search, first, count, orderBy));
	}

	@Override
	public Void update(final User admin, final SmtpConfigDTO config) {
		if (config.getName() == null) {
			config.setName(DEFAULT);
		}
		logger.info(() -> "SMTP server: " + config.getSmtpServer());
		logger.info(() -> "SMTP port: " + config.getSmtpPort());
		logger.info(() -> "Authentication: " + config.getUseAuthentication());
		logger.info(() -> "StartTLS: " + config.getUseStartTls());
		logger.info(() -> "SSL: " + config.getUseSsl());
		logger.info(() -> "Auth user: " + config.getSmtpUsername());

		wrapAction(() -> smtpConfigOperations.update(encryptSMTPPass(convert(config))));
		audit(Audit.UPDATE_SMTP_CONFIG, admin.getUsername(), admin.getId(), config.getSmtpServer(), config.getId());

		return null;
	}
}
