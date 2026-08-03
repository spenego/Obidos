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
import java.util.stream.Stream;

import com.spenego.Obidos.server.model.ContainerGroupAssignment;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public interface ContainerGroupAssignmentOperations extends Operations<ContainerGroupAssignment> {
	ContainerGroupAssignment get(Long containerId, Long groupId) throws NoSuchRecordException;
	Integer getShareCount(Long containerId);
	Stream<ContainerGroupAssignment> getAll(Long containerId);

	/**
	 * Get the containers that have been shared with the specified group.
	 *
	 * @param groupId
	 * @return
	 */
	Stream<ContainerGroupAssignment> getContainers(Long groupId);
	Integer delete(Long containerId, Long groupId);
	Void grantPermissions(Long containerId, PermissionDTO permission, Collection<Long> groups);
	

	/**
	 * @param userId
	 * @param containerId
	 * @return true if the user is a member of a group that was granted permission to add items to the container.
	 */
	Boolean mayAddItems(Long userId, Long containerId);
	
	/**
	 * @param userId
	 * @param containerId
	 * @return true if the user is a member of a group that was granted permission to update items in the container.
	 */
	Boolean mayUpdateItems(Long userId, Long containerId);
}
