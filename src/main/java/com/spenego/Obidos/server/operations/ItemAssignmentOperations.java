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

import com.spenego.Obidos.server.model.ItemAssignment;
import com.spenego.Obidos.server.model.UserGroupCombo;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public interface ItemAssignmentOperations extends Operations<ItemAssignment> {
	Stream<ItemAssignment>	getList(Long userId);

	/**
	 * Provides a stream of item assignments that have a count of 0. These item assignments are about to be
	 * deleted but we need to get a list of items to know which items may not be shared anymore.
	 * @param userId
	 * @return
	 */
	Stream<ItemAssignment>		getArtifacts(Long userId);
	Collection<ItemAssignment>	getAllArtifacts();

	/**
	 * When adding a user to a group, we need to see if the user already has access to an item. If not, we need to send
	 * a notification about access to a new item.
	 *
	 * @param groupId
	 * @return
	 */
	Collection<ItemAssignment>	getAssignmentsViaGroupId	(Long groupId) throws NoSuchRecordException;

	Integer					getItemShareCount				(Long itemId);
	ItemAssignment			get								(Long itemId, Long userId) throws NoSuchRecordException;
	ItemAssignment			getByDocumentId					(Long userId, Long documentId) throws NoSuchRecordException;
	Integer					count							(Long itemId, Long userId);
	Integer					deleteAllForUser				(Long userId);
	Integer					deleteOthers					(Long itemId, Long userId) throws NoSuchRecordException;
	Integer					deleteSharedItem				(Long itemId, Long userId) throws NoSuchRecordException;
	Integer					deleteSharedItemGroup			(Long itemId, Long groupId) throws NoSuchRecordException;
	Integer					decrementShareCountForGroup		(Long itemId, Long groupId) throws NoSuchRecordException;
	Integer					decrementShareCountForUser		(Long userId, Long groupId) throws NoSuchRecordException;
	Integer					incrementShareCount				(Long itemAssignmentId) throws NoSuchRecordException;
	Integer					decrementShareCount				(Long itemAssignmentId) throws NoSuchRecordException;
	// When a User Takes Ownership of an Item, we need to reset the count to adjust for groups that the user is no longer
	// sharing the item through.
	Integer					resetShareCount					(Long userId, Long itemId) throws NoSuchRecordException;
	Integer					deleteArtifacts					(Long itemId) throws NoSuchRecordException;
	Integer					deleteArtifactsOfUser			(Long itemId) throws NoSuchRecordException;
	Void					grantPermissions				(Long itemId, PermissionDTO permission, Collection<Long> users) throws NoSuchRecordException;
	Collection<ItemAssignment>	getItemAssignmentsOfUsers	(Long itemId, Collection<Long> users) throws NoSuchRecordException;
	Collection<ItemAssignment>	getItemAssignmentsOfItem	(Long itemId) throws NoSuchRecordException;

	/**
	 * Retrieve a list of groups and users that are sharing an item.
	 *
	 * @param containerId
	 * @return
	 * @throws NoSuchRecordException
	 */
	Stream<UserGroupCombo>	getItemShares			(Long callerId, Long itemId, String searchString, Collection<Long> preSelectedElements, Integer offset, Integer count, List<OrderBy> orderBy);
	Integer					getItemShareCount		(Long callerId, Long itemId, String searchString, Collection<Long> preSelectedElements);

	// Primarily used to delete item assignments for items in a shared container; when a user relinquishes a container.
	Integer					deleteAllItemsInContainer		(Long containerId, Long userId) throws NoSuchRecordException;
}
