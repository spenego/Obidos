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
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.ComplexityRequirementsMapper;
import com.spenego.Obidos.server.dao.Mapper;
import com.spenego.Obidos.server.model.ComplexityRequirements;
import com.spenego.Obidos.server.model.ComplexityRequirementsExample;
import com.spenego.Obidos.server.model.ComplexityRequirementsExample.Criteria;
import com.spenego.Obidos.server.operations.ComplexityRequirementsOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public final class ComplexityRequirementsOperationsImpl extends ObidosOperations<ComplexityRequirements> implements ComplexityRequirementsOperations {
	private static final Logger	logger = LoggerFactory.getLogger(ComplexityRequirementsOperationsImpl.class);

	@Autowired
	private final ComplexityRequirementsMapper	mapper = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected Mapper<ComplexityRequirements> getMapper() {
		return mapper;
	}

	@Override
	protected String getModelName() {
		return "ComplexityRequirements";
	}

	private List<ComplexityRequirements> getList(final Consumer<Criteria> c, final String name) {
		final List<ComplexityRequirements> list = mapper.selectByExample(new ComplexityRequirementsExample(c));

		if (list == null || list.isEmpty()) {
			throw new NoSuchRecordException("No complexity requirement named " + name + " exists.");
		}

		return list;
	}

	@Override
	public ComplexityRequirements get(final Byte type, final String name) {
		return getList(c -> c.andTypeEqualTo(type).andNameEqualTo(name), name).get(0);
	}

	@Override
	public List<ComplexityRequirements> get(final String name) {
		return getList(c -> c.andNameEqualTo(name), name);
	}
}
