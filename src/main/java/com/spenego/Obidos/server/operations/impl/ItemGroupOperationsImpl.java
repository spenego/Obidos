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
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.ItemGroupMapper;
import com.spenego.Obidos.server.model.ItemGroup;
import com.spenego.Obidos.server.model.ItemGroupExample;
import com.spenego.Obidos.server.operations.ItemGroupOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public final class ItemGroupOperationsImpl extends ObidosOperations<ItemGroup> implements ItemGroupOperations {
	private static final Logger	logger = LoggerFactory.getLogger(ItemOperationsImpl.class);

	@Autowired
	private final ItemGroupMapper			mapper = null;

	@Override
	protected Logger getLogger()			{ return logger; }
	@Override
	protected ItemGroupMapper getMapper()	{ return mapper; }
	@Override
	protected String getModelName()			{ return "itemGroup"; }

	@Override
	public Long create(final ItemGroup itemGroup) {
		return createWithRandomID(itemGroup);
	}

	@Override
	public Stream<ItemGroup> getItemGroupStream(final Long groupId) {
		return mapper.selectByExample(new ItemGroupExample(c -> c.andGroupIdEqualTo(groupId))).stream();
	}

	@Override
	public Integer getItemGroupCount(final Long groupId) {
		return mapper.countByExample(new ItemGroupExample(c -> c.andGroupIdEqualTo(groupId)));
	}

	@Override
	public Integer deleteAllGroups(final Long itemId) {
		return mapper.deleteByExample(new ItemGroupExample(c -> c.andItemIdEqualTo(itemId)));
	}

	@Override
	public Integer delete(final Long itemId, final Long groupId) {
		return mapper.deleteByExample(new ItemGroupExample(c -> c.andItemIdEqualTo(itemId).andGroupIdEqualTo(groupId)));
	}

	@Override
	public Stream<ItemGroup> getGroupsForItem(final Long itemId) {
		return mapper.selectByExample(new ItemGroupExample(c -> c.andItemIdEqualTo(itemId))).stream();
	}

	@Override
	public Void grantPermissions(final Long itemId, final PermissionDTO permission, final Collection<Long> groups) throws NoSuchRecordException {
		mapper.updateByExampleSelective(new ItemGroup(permission), new ItemGroupExample(c -> c.andItemIdEqualTo(itemId).andGroupIdIn(groups)));
		return null;
	}
}
