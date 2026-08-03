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

package com.spenego.Obidos.server.operations.impl;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.SmtpConfigMapper;
import com.spenego.Obidos.server.model.SmtpConfig;
import com.spenego.Obidos.server.model.SmtpConfigExample;
import com.spenego.Obidos.server.operations.SmtpConfigOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.exceptions.RecordModifiedException;

public final class SmtpConfigOperationsImpl extends ObidosOperations<SmtpConfig> implements SmtpConfigOperations {
	private static final Logger	logger = LoggerFactory.getLogger(SmtpConfigOperationsImpl.class);

	@Autowired private final SmtpConfigMapper	smtpConfigMapper = null;

	@Override
	protected Logger getLogger()			{ return logger; }
	@Override
	protected SmtpConfigMapper getMapper()	{ return smtpConfigMapper; }
	@Override
	protected String getModelName()			{ return "smtp config"; }

	@Override
	public Long create(final SmtpConfig o) throws RecordModifiedException {
		return createWithRandomID(o);
	}

	private SmtpConfigExample createExample(final String serverSearchString, final String name, final List<OrderBy> orderBy) {
		return createExample(() -> new SmtpConfigExample(c -> c.andSmtpServerLike(serverSearchString).andNameLike(name)), orderBy);
	}

	@Override
	public Stream<SmtpConfig> getStream(final String searchString, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return smtpConfigMapper.selectByExample(createExample(null, searchString, orderBy), createRowBounds(first, count)).stream();
	}

	@Override
	public Stream<SmtpConfig> getByName(final String name) {
		return smtpConfigMapper.selectByExample(createExample(null, name, null), createRowBounds(null, null)).stream();
	}

	@Override
	public Integer getCount(final String searchString) {
		return (int) smtpConfigMapper.countByExample(createExample(null, searchString, null));
	}
}
