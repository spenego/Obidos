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

import com.spenego.Obidos.server.model.AssignedContainer;
import com.spenego.Obidos.server.model.Container;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public interface ContainerOperations extends Operations<Container> {
	Long						create(Container container);
	Integer						getUserContainerCount(Long userId, String search, Collection<Long> preSelectedContainers, Boolean shared, Boolean shareable);
	AssignedContainer			getContainer(Long userId, String name) throws NoSuchRecordException;
	Stream<AssignedContainer>	getUserContainers(Long userId, String search, Collection<Long> preSelectedContainers, Boolean shared, Boolean shareable, Integer first, Integer count, List<OrderBy> orderBy);
	Stream<AssignedContainer>	getAssignedContainers(Long userId);
	AssignedContainer			getAssignedContainer(Long userId, String name) throws NoSuchRecordException;
	Integer						getContainersSharedWithUserCount(Long userId, String search, Collection<Long> preSelectedContainers);
	Stream<AssignedContainer>	getContainersSharedWithUser(Long userId, String search, Collection<Long> preSelectedContainers, Integer first, Integer count, List<OrderBy> orderBy);
	
	/**
	 * 
	 * @param userId
	 * @param containerId
	 * @return true if the user is a member of a group that was granted permission to take ownership of the container.
	 */
	boolean						mayTakeOwnership(Long userId, Long containerId);

	/**
	 * Get containers contained within the specified container.
	 *
	 * @param containerId
	 * @return
	 */
	Stream<Container>			getContainers(Long containerId);
	Integer						deleteAllForUser(Long userId);
}
