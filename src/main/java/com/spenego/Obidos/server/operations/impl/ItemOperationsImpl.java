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
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.ItemMapper;
import com.spenego.Obidos.server.dao.SharedItemMapper;
import com.spenego.Obidos.server.model.Item;
import com.spenego.Obidos.server.model.ItemExample;
import com.spenego.Obidos.server.model.SharedItem;
import com.spenego.Obidos.server.model.SharedItemExample;
import com.spenego.Obidos.server.operations.ItemOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.exceptions.DuplicateRecordException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class ItemOperationsImpl extends ObidosOperations<Item> implements ItemOperations {
	private static final Logger	logger = LoggerFactory.getLogger(ItemOperationsImpl.class);

	@Autowired
	private final ItemMapper			itemMapper = null;
	@Autowired
	private final SharedItemMapper		sharedItemMapper = null;

	@Override
	protected Logger getLogger()		{ return logger; }
	@Override
	protected ItemMapper getMapper()	{ return itemMapper; }
	@Override
	protected String getModelName()		{ return "item"; }

	@Override
	public Long create(final Item item) {
		try {
			return createWithRandomID(item);
		} catch(final DuplicateRecordException ex) {
			throw new ServerSideException("An item with the name " + item.getName() + " already exists.");
		}
	}

	@Override
	protected String containerOrderByName() {
		return "container_name";
	}

	private SharedItemExample getExample(final Long userId, final Consumer<SharedItemExample.Criteria> collector, final Collection<Long> preSelectedItems, final List<OrderBy> orderBy) {
		final SharedItemExample example = new SharedItemExample(collector);
		if (orderBy != null) {
			example.setOrderByClause(generatePreSelectedOrderClause(preSelectedItems, "ia.id", orderBy));
		}
		if (preSelectedItems != null && !preSelectedItems.isEmpty()) {
			example.or(example.createCriteria().andItemAssignmentIdIn(preSelectedItems).andItemAssignmentOwnerIs(userId));
		}

		return example;
	}

	private SharedItemExample getExample(final Long userId, final Long containerAssignmentId, final Long notContainerAssignmentId, final Boolean shared, final String nameSearch, final Collection<Long> preSelectedItems, final List<OrderBy> orderBy) {
		final boolean hasNegate = nameSearch != null && nameSearch.length() > 1 && nameSearch.substring(0,1).equals("!");
		final String nameNotLike = hasNegate ? nameSearch.substring(1) : null;
		final String nameLike = hasNegate ? null : nameSearch;

		return getExample(userId, c -> c.andUserIdEqualTo(userId).andOwnerIdEqualTo(userId).andContainerAssignmentIdEqualTo(containerAssignmentId).andContainerAssignmentIdNotEqualTo(notContainerAssignmentId).andSharedEqualTo(shared).andNameLike(nameLike).andNameNotLike(nameNotLike), preSelectedItems, orderBy);
	}

	private SharedItemExample getSharedWithExample(final Long userId, final Long containerId, final Long notConatinerId, final String nameSearch, final Collection<Long> preSelectedItems, final List<OrderBy> orderBy) {
		return getExample(userId, c -> c.andUserIdEqualTo(userId).andOwnerIdNotEqualTo(userId).andContainerIdEqualTo(containerId).andContainerIdNotEqualTo(notConatinerId).andShareExpiresAtGreaterThan(new Date()).andNameLike(nameSearch), preSelectedItems, orderBy);
	}

	@Override
	public Integer getUsersItemsCount(final Long userId, final Long containerAssignmentId, final Long notContainerAssignmentId, final Boolean shared, final String nameSearch, final Collection<Long> preSelectedItems) {
		return sharedItemMapper.countByExample(getExample(userId, containerAssignmentId, notContainerAssignmentId, shared, nameSearch, preSelectedItems, null));
	}

	@Override
	public Stream<SharedItem> getUsersItems(final Long userId, final Long containerAssignmentId, final Long notContainerAssignmentId, final Boolean shared, final String nameSearch, final Collection<Long> preSelectedItems, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return sharedItemMapper.selectByExample(getExample(userId, containerAssignmentId, notContainerAssignmentId, shared, nameSearch, preSelectedItems, orderBy), createRowBounds(first, count)).stream();
	}

	@Override
	public Stream<SharedItem> getItemsSharedWithUser(final Long userId, final Long containerId, final Long notContainerId, final String searchString, final Collection<Long> preSelectedItems, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return sharedItemMapper.selectByExample(getSharedWithExample(userId, containerId, notContainerId, searchString, preSelectedItems, orderBy), createRowBounds(first, count)).stream();
	}

	@Override
	public Integer getItemsSharedWithUserCount(final Long userId, final Long containerId, final Long notContainerId, final String searchString, final Collection<Long> preSelectedItems) {
		return sharedItemMapper.countByExample(getSharedWithExample(userId, containerId, notContainerId, searchString, preSelectedItems, null));
	}

	@Override
	public Stream<SharedItem> getItemsSharedWithGroup(final Long userId, final Long groupId, final String searchString, final Collection<Long> preSelectedItems, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return sharedItemMapper.selectByGroupExample(getExample(userId, c -> c.andGroupIdEqualTo(groupId).andNameLike(searchString), preSelectedItems, orderBy), createRowBounds(first, count)).stream();
	}

	@Override
	public Stream<SharedItem> getItemsSharedWithGroup(final Long userId, final Long groupId) {
		return sharedItemMapper.selectByGroupExample(getExample(userId, c -> c.andGroupIdEqualTo(groupId), null, null), createRowBounds(null, null)).stream();
	}

	@Override
	public Integer getItemsSharedWithGroupCount(final Long userId, final Long groupId, final String searchString, final Collection<Long> preSelectedItems) {
		return sharedItemMapper.countByGroupExample(getExample(userId, c -> c.andGroupIdEqualTo(groupId).andNameLike(searchString), preSelectedItems, null));
	}

	@Override
	public SharedItem getSharedItem(final Long userId, final Long itemId) {
		return sharedItemMapper.selectByExample(new SharedItemExample(c -> c.andItemIdEqualTo(itemId).andUserIdEqualTo(userId)), createRowBounds(null, null)).get(0);
	}

	@Override
	public Integer deleteAllUserItems(final Long userId) {
		return itemMapper.deleteByExample(new ItemExample(c -> c.andUserIdEqualTo(userId)));
	}

	@Override
	public Integer deleteAllUnsharedUserItems(final Long userId) {
		return itemMapper.deleteByExample(new ItemExample(c -> c.andUserIdEqualTo(userId).andSharedEqualTo(false)));
	}

	@Override
	public Stream<SharedItem> getItemsInContainer(final Long containerAssignmentId) {
		return sharedItemMapper.selectByExample(new SharedItemExample(c -> c.andContainerAssignmentIdEqualTo(containerAssignmentId)), createRowBounds(null, null)).stream();
	}

	@Override
	public Stream<SharedItem> getShareableItemsInContainer(final Long containerAssignmentId) {
		return sharedItemMapper.selectTerseByExample(new SharedItemExample(c -> c.andContainerAssignmentIdEqualTo(containerAssignmentId).andShareableEqualTo(true)), createRowBounds(null, null)).stream();
	}

	@Override
	public Integer countItemsInContainer(final Long containerAssignmentId) {
		return countByExample(new ItemExample(c -> c.andContainerAssignmentIdEqualTo(containerAssignmentId)));
	}

	@Override
	public boolean mayUpdateItem(final Long userId, final Long itemId) {
		return itemMapper.hasGroupUpdate(userId, itemId) > 0;
	}

	@Override
	public boolean mayTakeOwnership(final Long userId, final Long itemId) {		
		return itemMapper.hasGroupOwnershipControl(userId, itemId) > 0;
	}
}
