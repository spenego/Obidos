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

import com.spenego.Obidos.server.model.Group;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.shared.OrderBy;

public interface GroupOperations extends Operations<Group> {
	Integer			getCount(Long userId, String search, Collection<Long> preSelectedGroups);
	Integer			getGroupsNotInContainerCount(Long userId, Long containerId, String search, Collection<Long> preSelectedGroups);
	Stream<Group>	getList(Long userId, String search, Collection<Long> preSelectedGroups,  Integer first, Integer count, List<OrderBy> orderBy);
	Stream<Group>	getGroupsNotInContainer(Long userId, Long containerId, String search, Collection<Long> preSelectedGroups, Integer first, Integer count, List<OrderBy> orderBy);

	Collection<Long>getGroupIdsSharingItem(Long itemId);	// if the collection has more than 4 elements, the returned object is a Set instead of List

	Integer			getGroupsNotSharingItemCount(User user, Long itemId, String searchString, Collection<Long> preSelectedGroups);
	Integer			getGroupsSharingItemCount(Long itemId, String searchString, Collection<Long> preSelectedGroups, Boolean sharedExplicitly);
	Integer			getGroupsSharingContainerCount(Long containerID, String searchString, Collection<Long> preSelectedGroups);

	Stream<Group>	getGroupsNotSharingItem(User user, Long itemId, String searchString, Collection<Long> preSelectedGroups, Integer first, Integer count, List<OrderBy> orderBy);
	Stream<Group>	getGroupsSharingItem(Long itemId, String searchString, Collection<Long> preSelectedGroups, Boolean sharedExplicitly, Integer first, Integer count, List<OrderBy> orderBy);
	Stream<Group>	getGroupsSharingContainer(Long containerID, String searchString, Collection<Long> preSelectedGroups, Integer first, Integer count, List<OrderBy> orderBy);
	Collection<Long>getGroupIdsSharingContainer(Long containerID);
}
