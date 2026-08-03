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

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.UserDefinedTypeValueMapper;
import com.spenego.Obidos.server.model.UserDefinedTypeValue;
import com.spenego.Obidos.server.model.UserDefinedTypeValueExample;
import com.spenego.Obidos.server.operations.UserDefinedTypeValueOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;

public final class UserDefinedTypeValueOperationsImpl extends ObidosOperations<UserDefinedTypeValue> implements UserDefinedTypeValueOperations {
	private static final Logger	logger = LoggerFactory.getLogger(UserDefinedTypeValueOperationsImpl.class);

	@Autowired
	private final UserDefinedTypeValueMapper			userDefinedTypeValueMapper = null;

	@Override
	protected Logger getLogger()						{ return logger; }
	@Override
	protected UserDefinedTypeValueMapper getMapper()	{ return userDefinedTypeValueMapper; }
	@Override
	protected String getModelName()						{ return "userDefinedTypeValue"; }

	@Override
	public Long create(final UserDefinedTypeValue value) {
		return createWithRandomID(value);
	}

	@Override
	public Stream<UserDefinedTypeValue> getUserDefinedTypeValues(final Long userId, final Long itemId) {
		return userDefinedTypeValueMapper.selectByExample(new UserDefinedTypeValueExample(c -> c.andUserIdEqualTo(userId).andItemIdEqualTo(itemId))).stream();
	}

	@Override
	public Stream<UserDefinedTypeValue> getUserDefinedTypeValues(final Long typeId) {
		return userDefinedTypeValueMapper.selectByExample(new UserDefinedTypeValueExample(c -> c.andUserDefinedTypeIdEqualTo(typeId))).stream();
	}

	@Override
	public Void updateType(final Long userId, final Long typeId, final Long newTypeId) {
		userDefinedTypeValueMapper.updateByExampleSelectiveJoin(new UserDefinedTypeValue(newTypeId), new UserDefinedTypeValueExample(c -> c.andUserDefinedTypeIdEqualTo(typeId).andItemOwnerIdEqualTo(userId)));
		return null;
	}

	@Override
	public int numberOfInstancesOfType(final Long typeid, final Long notUserId) {
		return countByExample(new UserDefinedTypeValueExample(c -> c.andUserDefinedTypeIdEqualTo(typeid).andUserIdNotEqualTo(notUserId)));
	}

	@Override
	public int numberOfItemReferencesUsingType(final Long typeid, final Long userId) {
		return userDefinedTypeValueMapper.countItemReferencesByExample(new UserDefinedTypeValueExample(c -> c.andUserDefinedTypeIdEqualTo(typeid).andItemOwnerIdNotEqualTo(userId)));
	}

	@Override
	public int deleteItemReferencesUsingType(final Long typeid, final Long userId) {
		return userDefinedTypeValueMapper.deleteItemReferencesByExample(new UserDefinedTypeValueExample(c -> c.andUserDefinedTypeIdEqualTo(typeid).andItemOwnerIdEqualTo(userId)));
	}

	@Override
	public int deleteUserItemReferences(final Long itemId, final Long userId) {
		return userDefinedTypeValueMapper.deleteByExample(new UserDefinedTypeValueExample(c -> c.andUserIdEqualTo(userId).andItemIdEqualTo(itemId)));
	}

	@Override
	public int deleteOtherUserItemReferences(final Long itemId, final Long userId) {
		return userDefinedTypeValueMapper.deleteByExample(new UserDefinedTypeValueExample(c -> c.andUserIdNotEqualTo(userId).andItemIdEqualTo(itemId)));
	}

	@Override
	public int deleteUserItemReferencesViaContainerId(final Long containerId, final Long userId) {
		return userDefinedTypeValueMapper.deleteByExample(new UserDefinedTypeValueExample(c -> c.andUserIdEqualTo(userId).andItemsInContainer(containerId)));
	}
}

