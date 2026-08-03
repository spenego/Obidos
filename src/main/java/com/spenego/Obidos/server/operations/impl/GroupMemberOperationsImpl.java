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

import com.spenego.Obidos.server.dao.GroupMemberMapper;
import com.spenego.Obidos.server.dao.GroupUserMapper;
import com.spenego.Obidos.server.model.GroupMember;
import com.spenego.Obidos.server.model.GroupMemberExample;
import com.spenego.Obidos.server.model.GroupUserExample;
import com.spenego.Obidos.server.model.LimitedUser;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.GroupMemberOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public final class GroupMemberOperationsImpl extends ObidosOperations<GroupMember> implements GroupMemberOperations {
	private static final Logger	logger = LoggerFactory.getLogger(GroupMemberOperationsImpl.class);

	@Autowired
	private final GroupMemberMapper	groupMemberMapper = null;
	@Autowired
	private final GroupUserMapper	groupUserMapper = null;

	@Override
	protected Logger getLogger()			{ return logger; }
	@Override
	protected GroupMemberMapper getMapper()	{ return groupMemberMapper; }
	@Override
	protected String getModelName()			{ return "group member"; }

	@Override
	public Long addUserToGroup(final GroupMember groupMember) {
		return createWithRandomID(groupMember);
	}

	private GroupUserExample createExample(final Long groupId, final Long notGroupId, final User userPatterns, final Collection<Long> preSelectedUsers, final List<OrderBy> orderBy) {
		final GroupUserExample example = new GroupUserExample(generatePreSelectedOrderClause(preSelectedUsers, "id", orderBy));
		final GroupUserExample.Criteria criteria = example.createCriteria();

		if (groupId != null) {
			criteria.andGroupIdEqualTo(groupId);
		}

		if (orderBy != null) {
			example.setOrderByClause(generateOrderByString(orderBy));
		}

		gen(null, criteria, userPatterns);

		if (preSelectedUsers != null && !preSelectedUsers.isEmpty()) {
			example.or(example.createCriteria().andIdIn(preSelectedUsers));
		}

		return example;
	}

	@Override
	public Stream<LimitedUser> getUsersInGroup(final Long groupId, final User userPatterns, final Collection<Long> preSelectedUsers, final Integer first, final Integer count, final List<OrderBy> orderby) {
		return groupUserMapper.selectByExample(createExample(groupId, null, userPatterns, preSelectedUsers, orderby), createRowBounds(first, count)).stream();
	}

	@Override
	public Stream<LimitedUser> getUsersInGroup(final Long groupId) {
		return getUsersInGroup(groupId, null, null, null, null, null);
	}

	@Override
	public Collection<Long> getUserIdsInGroup(final Long groupId) {
		return groupUserMapper.selectIdsByExample(createExample(groupId, null, null, null, null), createRowBounds(null, null));
	}

	@Override
	public Integer getUsersInGroupCount(final Long groupId, final User userPatterns, final Collection<Long> preSelectedUsers) {
		return groupUserMapper.countByExample(createExample(groupId, null, userPatterns, preSelectedUsers, null));
	}

	@Override
	public Void delete(final Long userId, final Long groupId) {
		if (groupMemberMapper.deleteByExample(new GroupMemberExample(c -> c.andUserIdEqualTo(userId).andGroupIdEqualTo(groupId))) != 1) {
			throw new NoSuchRecordException("group " + groupId + " has no user " + userId);
		}
		return null;
	}

	@Override
	public Void deleteAllUserMemberships(final Long userId) {
		groupMemberMapper.deleteByExample(new GroupMemberExample(c -> c.andUserIdEqualTo(userId)));
		return null;
	}
}
