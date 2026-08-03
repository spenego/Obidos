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

import com.spenego.Obidos.server.model.Item;
import com.spenego.Obidos.server.model.SharedItem;
import com.spenego.Obidos.shared.OrderBy;

public interface ItemOperations extends Operations<Item> {
	Integer				getUsersItemsCount(Long userId, Long containerAssignmentId, Long notContainerAssignmentId, Boolean shared, String nameSearch, Collection<Long> preSelectedUsers);
	Stream<SharedItem>	getUsersItems(Long userId, Long containerAssignmentId, Long notContainerAssignmentId, Boolean shared, String nameSearch, Collection<Long> preSelectedUsers, Integer first, Integer count, List<OrderBy> orderBy);
	Stream<SharedItem>	getItemsInContainer(Long containerAssignmentId);
	Stream<SharedItem>	getShareableItemsInContainer(Long containerAssignmentId);
	Stream<SharedItem>	getItemsSharedWithUser(Long userId, Long containerId, Long notContainerId, String searchString, Collection<Long> preSelectedUsers, Integer first, Integer count, List<OrderBy> orderby);
	Integer				getItemsSharedWithUserCount(Long userId, Long containerId, Long notContainerId, String searchString, Collection<Long> preSelectedUsers);
	Stream<SharedItem>	getItemsSharedWithGroup(Long userId, Long groupId);
	Stream<SharedItem>	getItemsSharedWithGroup(Long userId, Long groupId, String searchString, Collection<Long> preSelectedUsers, Integer first, Integer count, List<OrderBy> orderby);
	Integer				getItemsSharedWithGroupCount(Long userId, Long groupId, String searchString, Collection<Long> preSelectedUsers);
	SharedItem			getSharedItem(Long userId, Long itemId);
	Integer				deleteAllUserItems(Long userId);
	Integer				deleteAllUnsharedUserItems(Long userId);
	Integer				countItemsInContainer(Long containerAssignmentId);

	/**
	 * 
	 * @param userId
	 * @param itemId
	 * @return true if the user is a member of a group that was granted permission to update the item.
	 */
	boolean				mayUpdateItem(Long userId, Long itemId);

	/**
	 * 
	 * @param userId
	 * @param itemId
	 * @return true if the user is a member of a group that was granted permission to take ownership the item.
	 */
	boolean				mayTakeOwnership(Long userId, Long itemId);
}
