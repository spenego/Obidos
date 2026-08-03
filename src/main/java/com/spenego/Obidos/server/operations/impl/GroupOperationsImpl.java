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
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.GroupMapper;
import com.spenego.Obidos.server.model.Group;
import com.spenego.Obidos.server.model.GroupExample;
import com.spenego.Obidos.server.model.GroupExample.Criteria;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.GroupOperations;
import com.spenego.Obidos.server.operations.UserOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;

public final class GroupOperationsImpl extends ObidosOperations<Group> implements GroupOperations {
	private static final Logger	logger = LoggerFactory.getLogger(GroupOperationsImpl.class);

	@Autowired
	private final GroupMapper		groupMapper = null;
	@Autowired
	private final UserOperations	userOperations = null;

	@Override
	protected Logger getLogger()		{ return logger; }
	@Override
	protected GroupMapper getMapper()	{ return groupMapper; }
	@Override
	protected String getModelName()		{ return "group"; }

	private GroupExample createExample(final User user, final String name, final boolean partialName, final Collection<Long> preSelectedGroups, final Consumer<Criteria> consumer, final List<OrderBy> orderBy) {
		final GroupExample example = new GroupExample(generatePreSelectedOrderClause(preSelectedGroups, "g.id", orderBy), c -> {
			if (name != null) {
				if (partialName) {
					c.andNameLike(name);
				} else {
					c.andNameEqualTo(name);
				}
			}

			if (user != null && !user.isAdmin()) {
				c.andAdminOrMyGroups(user.getId()); // user can see global groups created by Admin
			}

			if (consumer != null) {
				consumer.accept(c);
			}});

		if (preSelectedGroups != null && !preSelectedGroups.isEmpty()) {
			example.or(example.createCriteria().andIdIn(preSelectedGroups));
		}

		return example;
	}

	private static <T> Collection<T> convertCollection(final Collection<T> c) {
		return c.size() > 2 ? new HashSet<>(c) : c;
	}

	@Override
	public Collection<Long> getGroupIdsSharingItem(final Long itemId) {
		return convertCollection(groupMapper.selectItemGroupIdsByExample(new GroupExample(c -> c.andItemIdEqualTo(itemId))));
	}

	@Override
	public Long create(final Group group) {
		return createWithRandomID(group);
	}

	@Override
	public Integer getCount(final Long userId, final String search, final Collection<Long> preSelectedGroups) {
		return groupMapper.countByExample(createExample(userOperations.get(userId), search, true, preSelectedGroups, null, null));
	}

	@Override
	public Stream<Group> getList(final Long userId, final String search, final Collection<Long> preSelectedGroups, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return groupMapper.selectByExample(createExample(userOperations.get(userId), search, true, preSelectedGroups, null, orderBy), createRowBounds(first, count)).stream();
	}

	@Override
	public Integer getGroupsNotInContainerCount(final Long userId, final Long containerId, final String search, final Collection<Long> preSelectedGroups) {
		return countByExample(createExample(userOperations.get(userId), search, true, preSelectedGroups, c -> c.andGroupsNotInContainer(containerId), null));
	}

	@Override
	public Stream<Group> getGroupsNotInContainer(final Long userId, final Long containerId, final String searchString, final Collection<Long> preSelectedGroups, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return groupMapper.selectByExample(createExample(userOperations.get(userId), searchString, true, preSelectedGroups, c -> c.andGroupsNotInContainer(containerId), orderBy), createRowBounds(first, count)).stream();
	}

	@Override
	public Stream<Group> getGroupsSharingItem(final Long itemId, final String searchString, final Collection<Long> preSelectedGroups, final Boolean sharedExplicitly, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return groupMapper.selectItemGroupsByExample(createExample(null, searchString, true, preSelectedGroups, c -> c.andItemIdEqualTo(itemId).andNameLike(searchString).andSharedExplicitlyEqualTo(sharedExplicitly), orderBy), createRowBounds(first, count)).stream();
	}

	@Override
	public Integer getGroupsSharingItemCount(final Long itemId, final String searchString, final Collection<Long> preSelectedGroups, final Boolean sharedExplicitly) {
		return groupMapper.countItemGroupsByExample(createExample(null, searchString, true, preSelectedGroups, c -> c.andItemIdEqualTo(itemId).andNameLike(searchString).andSharedExplicitlyEqualTo(sharedExplicitly), null));
	}

	@Override
	public Stream<Group> getGroupsSharingContainer(final Long containerID, final String searchString, final Collection<Long> preSelectedGroups, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return groupMapper.selectGroupsSharingContainerByExample(createExample(null, searchString, true, preSelectedGroups, c -> c.andContainerIdEqualTo(containerID), orderBy), createRowBounds(first, count)).stream();
	}

	@Override
	public Collection<Long> getGroupIdsSharingContainer(final Long containerID) {
		return convertCollection(groupMapper.selectGroupIdsSharingContainerByExample(createExample(null, null, true, null, c -> c.andContainerIdEqualTo(containerID), null), createRowBounds(null, null)));
	}

	@Override
	public Integer getGroupsSharingContainerCount(final Long containerID, final String searchString, final Collection<Long> preSelectedGroups) {
		return groupMapper.countGroupsSharingContainerByExample(createExample(null, searchString, true, preSelectedGroups, c -> c.andContainerIdEqualTo(containerID), null));
	}

	@Override
	public Stream<Group> getGroupsNotSharingItem(final User user, final Long itemId, final String searchString, final Collection<Long> preSelectedGroups, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return groupMapper.selectByExample(createExample(user, searchString, true, preSelectedGroups, c -> c.andGroupNotSharingItem(itemId), orderBy), createRowBounds(first, count)).stream();
	}

	@Override
	public Integer getGroupsNotSharingItemCount(final User user, final Long itemId, final String searchString, final Collection<Long> preSelectedGroups) {
		return countByExample(createExample(user, searchString, true, preSelectedGroups, c -> c.andGroupNotSharingItem(itemId), null));
	}
}
