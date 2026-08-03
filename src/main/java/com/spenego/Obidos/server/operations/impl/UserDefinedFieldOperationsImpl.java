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

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.Mapper;
import com.spenego.Obidos.server.dao.UserDefinedFieldMapper;
import com.spenego.Obidos.server.model.UserDefinedField;
import com.spenego.Obidos.server.model.UserDefinedFieldExample;
import com.spenego.Obidos.server.operations.UserDefinedFieldOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class UserDefinedFieldOperationsImpl extends ObidosOperations<UserDefinedField>  implements UserDefinedFieldOperations {
	private static final Logger	logger = LoggerFactory.getLogger(UserDefinedFieldOperationsImpl.class);

	@Autowired
	private final UserDefinedFieldMapper mapper = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected Mapper<UserDefinedField> getMapper() {
		return mapper;
	}

	@Override
	protected String getModelName() {
		return "userDefinedField";
	}

	private UserDefinedFieldExample createExample(final Long typeId, final String search, final List<OrderBy> orderBy) {
		return createExample(() -> new UserDefinedFieldExample(c -> c.andTypeIdEqualTo(typeId).andNameLike(search)), orderBy);
	}

	@Override
	public Long create(final UserDefinedField udf) {
		if (udf.getTypeId() == null) {
			throw new ServerSideException("Type ID may not be null");
		}
		return udf.getId() == null ? createWithRandomID(udf) : super.create(udf);
	}

	@Override
	public Collection<UserDefinedField> getTypeFields(final Long typeId) {
		return mapper.selectByExample(new UserDefinedFieldExample(c -> c.andTypeIdEqualTo(typeId)));
	}

	@Override
	public Collection<UserDefinedField> getTypeFields(final Long typeId, final String search, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return mapper.selectByExample(createExample(typeId, search, orderBy), createRowBounds(first, count));
	}

	@Override
	public Integer getUserDefinedFieldCount(Long typeId, String search) {
		return countByExample(createExample(typeId, search, null));
	}
}
