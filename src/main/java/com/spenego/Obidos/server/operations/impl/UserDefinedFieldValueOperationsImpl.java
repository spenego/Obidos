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
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.Mapper;
import com.spenego.Obidos.server.dao.UserDefinedFieldValueMapper;
import com.spenego.Obidos.server.model.UserDefinedFieldValue;
import com.spenego.Obidos.server.model.UserDefinedFieldValueExample;
import com.spenego.Obidos.server.operations.UserDefinedFieldValueOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public final class UserDefinedFieldValueOperationsImpl extends ObidosOperations<UserDefinedFieldValue> implements UserDefinedFieldValueOperations {
	private static final Logger	logger = LoggerFactory.getLogger(UserDefinedFieldValueOperationsImpl.class);

	@Autowired
	private final UserDefinedFieldValueMapper mapper = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected Mapper<UserDefinedFieldValue> getMapper() {
		return mapper;
	}

	@Override
	protected String getModelName() {
		return "userDefinedFieldValue";
	}

	@Override
	public Long create(final UserDefinedFieldValue val) {
		return createWithRandomID(val);
	}

	private static UserDefinedFieldValueExample getExample(final Long id, final Long userDefinedTypeId) {
		return new UserDefinedFieldValueExample(c -> c.andIdEqualTo(id).andUserDefinedTypeValueIdEqualTo(userDefinedTypeId));
	}

	@Override
	public Void updateSelective(final UserDefinedFieldValue val) {
		final int result = mapper.updateByExampleSelective(val, getExample(val.getId(), val.getUserDefinedTypeValueId()));

		if (result != 1) {
			throw new NoSuchRecordException(getModelName(), val.getId());
		}
		return null;
	}

	private static UserDefinedFieldValueExample.Criteria setCriteria(final UserDefinedFieldValueExample.Criteria criteria, final Long userDefinedTypeValueId) {
		if (userDefinedTypeValueId != null) {
			criteria.andUserDefinedTypeValueIdEqualTo(userDefinedTypeValueId);
		} else {
			criteria.andIdIsNotNull();
		}

		return criteria;
	}

	@Override
	public List<UserDefinedFieldValue> getUserDefinedFieldValues(final Long userDefinedTypeValueId, final List<OrderBy> orderBy) {
		return mapper.selectByExample(createExample(() -> new UserDefinedFieldValueExample(c -> setCriteria(c, userDefinedTypeValueId)), orderBy));
	}

	@Override
	public Stream<UserDefinedFieldValue> getFieldsOfItem(final Long userId, final Long itemId, final Collection<Long> fields) {
		return mapper.selectByItemExample(new UserDefinedFieldValueExample(c -> c.andUserIdEqualTo(userId).andItemIdEqualTo(itemId).andUserDefinedFieldIdIn(fields))).stream();
	}
}
