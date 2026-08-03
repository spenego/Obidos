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

package com.spenego.Obidos.server.operations;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.spenego.Obidos.server.model.GroupMember;
import com.spenego.Obidos.server.model.LimitedUser;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.shared.OrderBy;

public interface GroupMemberOperations extends Operations<GroupMember> {
	Long addUserToGroup(GroupMember gm);

	/**
	 * Although part of groupMemberOperations, this method returns a LimitedUser object. It uses the GroupUser mapper (not the GroupMember mapper),
	 * which joins with the User table to allow the caller to obtain fullname/email addresses for users in the group.
	 *
	 * @param groupId
	 * @return a Stream of LimitedUser objects
	 */
	Stream<LimitedUser> getUsersInGroup(Long groupId, User userPatterns, Collection<Long> preSelectedUsers, Integer first, Integer count, List<OrderBy> orderby);

	/**
	 * Although part of groupMemberOperations, this method returns a LimitedUser object. It uses the GroupUser mapper (not the GroupMember mapper),
	 * which joins with the User table to allow the caller to obtain fullname/email addresses for users in the group.
	 *
	 * @param groupId
	 * @return a Stream of LimitedUser objects
	 */
	Stream<LimitedUser> getUsersInGroup(Long groupId);
	Collection<Long> getUserIdsInGroup(Long groupId);
	Integer getUsersInGroupCount(Long groupId, User userPatterns, Collection<Long> preSelectedUsers);
	Void delete(Long userId, Long groupId);
	Void deleteAllUserMemberships(Long userId);
}
