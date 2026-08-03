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

import com.spenego.Obidos.server.model.ContainerAssignment;
import com.spenego.Obidos.server.model.UserGroupCombo;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public interface ContainerAssignmentOperations extends Operations<ContainerAssignment> {
	ContainerAssignment get(Long containerId, Long userId) throws NoSuchRecordException;
	Stream<ContainerAssignment> getAll(Long containerId);
	Collection<ContainerAssignment> getAssignmentsViaGroupId(Long groupId);
	Integer getShareCount(Long id);
	Integer incrementShareCount(Long id) throws NoSuchRecordException;
	Integer decrementShareCount(Long containerId, Long groupId) throws NoSuchRecordException;
	Collection<ContainerAssignment> getAllArtifacts();
	Integer decrementShareCount(Long containerId, Collection<Long> userIds) throws NoSuchRecordException;
	Integer setSharedExplicitly(Long containerId, Collection<Long> userIds, Boolean sharedExplicitly) throws NoSuchRecordException;

	/**
	 * For all containers that share via groupId, decrement the reference count for the specified user.
	 *
	 * @param userId
	 * @param groupId
	 * @return
	 */
	Integer decrementShareCountsForUserInGroup(Long userId, Long groupId) throws NoSuchRecordException;

	/**
	 * Delete any container assignments that have a reference count of zero. This implies the container is no longer shared with this user and this record can now be deleted.
	 *
	 * @param containerId
	 * @return
	 * @throws NoSuchRecordException
	 */
	Integer deleteArtifacts(Long containerId) throws NoSuchRecordException;
	Integer deleteArtifactsForUser(Long userId) throws NoSuchRecordException;
	Integer deleteAllForUser(Long userId) throws NoSuchRecordException;

	/**
	 * Retrieve a list of groups and users the are sharing a container.
	 *
	 * @param containerId
	 * @return
	 * @throws NoSuchRecordException
	 */
	Stream<UserGroupCombo> getContainerAssignmentShares(Long callerId, Long containerId, String searchString, Collection<Long> preSelectedElements, Integer offset, Integer count, List<OrderBy> orderBy);
	Integer getContainerAssignmentShareCount(Long callerId, Long containerId, String searchString, Collection<Long> preSelectedElements);
	Void grantPermissions(final Long containerId, final PermissionDTO permission, final Collection<Long> users) throws NoSuchRecordException;
	Integer resetShareCount(final Long userId, final Long containerId) throws NoSuchRecordException;
}
