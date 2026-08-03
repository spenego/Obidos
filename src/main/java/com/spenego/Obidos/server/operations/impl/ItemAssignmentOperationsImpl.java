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
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.ItemAssignmentMapper;
import com.spenego.Obidos.server.model.ItemAssignment;
import com.spenego.Obidos.server.model.ItemAssignmentExample;
import com.spenego.Obidos.server.model.UserGroupCombo;
import com.spenego.Obidos.server.operations.ItemAssignmentOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public final class ItemAssignmentOperationsImpl extends ObidosOperations<ItemAssignment> implements ItemAssignmentOperations {
	private static final Logger	logger = LoggerFactory.getLogger(ItemAssignmentOperationsImpl.class);

	@Autowired
	private final ItemAssignmentMapper mapper = null;

	@Override
	protected Logger getLogger()				{ return logger; }
	@Override
	protected ItemAssignmentMapper getMapper()	{ return mapper; }
	@Override
	protected String getModelName()				{ return "itemAssignment"; }

	@Override
	public Long create(final ItemAssignment ia) {
		return createWithRandomID(ia);
	}

	private ItemAssignmentExample createBaseExample(final String user_id_col, final Collection<Long> preSelectedElements, final List<OrderBy> orderByList, final Consumer<ItemAssignmentExample.Criteria> consumer, final Consumer<ItemAssignmentExample.Criteria> preSelectedUserconsumer) {
		final ItemAssignmentExample example = new ItemAssignmentExample(generatePreSelectedOrderClause(preSelectedElements, user_id_col, orderByList), 1024);
		consumer.accept(example.createCriteria());

		if (preSelectedElements != null && !preSelectedElements.isEmpty()) {
			final ItemAssignmentExample.Criteria criteria = example.createCriteria();

			criteria.andIdIn(preSelectedElements);
			if (preSelectedUserconsumer != null) {
				preSelectedUserconsumer.accept(criteria);
			}
			example.or(criteria);
		}
		return example;
	}

	@Override
	public Stream<ItemAssignment> getList(final Long userId) {
		return mapper.selectByExample(new ItemAssignmentExample(c -> c.andUserIdEqualTo(userId))).stream();
	}

	@Override
	public Integer getItemShareCount(final Long itemId) {
		return countByExample(new ItemAssignmentExample(c -> c.andItemIdEqualTo(itemId)));
	}

	@Override
	public ItemAssignment get(final Long itemId, final Long userId) throws NoSuchRecordException {
		final List<ItemAssignment> cas = mapper.selectByExample(new ItemAssignmentExample(c -> c.andItemIdEqualTo(itemId).andUserIdEqualTo(userId)));
		if (cas != null && !cas.isEmpty()) {
			return cas.get(0);
		}
		throw new NoSuchRecordException("Unable to find a item assignment for user " + userId);
	}

	@Override
	public Integer count(final Long itemId, final Long userId) throws NoSuchRecordException {
		return countByExample(new ItemAssignmentExample(c -> c.andItemIdEqualTo(itemId).andUserIdEqualTo(userId)));
	}

	/**
	 * Delete all the item assignments EXCEPT for the specified user (usually the owner).
	 */
	@Override
	public Integer deleteOthers(final Long itemId, final Long userId) throws NoSuchRecordException {
		return mapper.deleteByExample(new ItemAssignmentExample(c -> c.andItemIdEqualTo(itemId).andUserIdNotEqualTo(userId)));
	}

	@Override
	public Integer deleteSharedItem(final Long itemId, final Long userId) throws NoSuchRecordException {
		return attemptOp(mapper::deleteByExample, () -> new ItemAssignmentExample(c -> c.andItemIdEqualTo(itemId).andUserIdEqualTo(userId)));
	}

	@Override
	public Integer deleteSharedItemGroup(final Long itemId, final Long groupId) throws NoSuchRecordException {
		return attemptOp(mapper::deleteGroupByExample, () -> new ItemAssignmentExample(c -> c.andItemIdEqualTo(itemId).andGroupIdEqualTo(groupId)));
	}

	@Override
	public Integer decrementShareCountForGroup(final Long itemId, final Long groupId) throws NoSuchRecordException {
		return attemptOp(mapper::decrementShareCount, () -> new ItemAssignmentExample(c -> c.andItemIdEqualTo(itemId).andGroupIdEqualTo(groupId).andCountGreaterThan(0)));
	}

	@Override
	public Integer decrementShareCountForUser(final Long userId, final Long groupId) throws NoSuchRecordException {
		return attemptOp(mapper::decrementShareCountForUser, () -> new ItemAssignmentExample(c -> c.andUserIdEqualTo(userId).andGroupIdEqualTo(groupId).andCountGreaterThan(0)));
	}

	@Override
	public Integer incrementShareCount(final Long itemAssignmentId) throws NoSuchRecordException {
		return attemptOp(e -> mapper.incrementByPrimaryKey(itemAssignmentId), null);
	}

	@Override
	public Integer decrementShareCount(final Long itemAssignmentId) throws NoSuchRecordException {
		return attemptOp(e -> mapper.decrementByPrimaryKey(itemAssignmentId), null);
	}

	@Override
	public Integer resetShareCount(final Long userId, final Long itemId) throws NoSuchRecordException {
		return attemptOp(e -> mapper.resetCountByExample(new ItemAssignmentExample(c -> c.andItemIdEqualTo(itemId))), null);
	}

	@Override
	public Integer deleteArtifacts(final Long itemId) throws NoSuchRecordException {
		return attemptOp(mapper::deleteByExample, () -> new ItemAssignmentExample(c -> c.andItemIdEqualTo(itemId).andCountEqualTo(0)));
	}

	@Override
	public Integer deleteArtifactsOfUser(final Long userId) throws NoSuchRecordException {
		return attemptOp(mapper::deleteByExample, () -> new ItemAssignmentExample(c -> c.andUserIdEqualTo(userId).andCountEqualTo(0)));
	}

	@Override
	public Integer deleteAllItemsInContainer(final Long containerId, final Long userId) throws NoSuchRecordException {
		return attemptOp(mapper::deleteByExample, () -> new ItemAssignmentExample(c -> c.andUserIdEqualTo(userId).andItemsInContainer(containerId)));
	}

	@Override
	public Stream<ItemAssignment> getArtifacts(final Long userId) {
		return mapper.selectByExample(new ItemAssignmentExample(c -> c.andUserIdEqualTo(userId).andCountEqualTo(0))).stream();
	}

	@Override
	public Collection<ItemAssignment> getAllArtifacts() {
		return mapper.selectByExample(new ItemAssignmentExample(c -> c.andCountEqualTo(0)));
	}

	@Override
	public Collection<ItemAssignment> getItemAssignmentsOfUsers(final Long itemId, final Collection<Long> users) throws NoSuchRecordException {
		return mapper.selectByExample(new ItemAssignmentExample(c -> c.andItemIdEqualTo(itemId).andUserIdIn(users)));
	}

	/**
	 * This method has half of the SQL code generated by the andIAComboItemIdEqualTo method.
	 */
	@Override
	public Stream<UserGroupCombo> getItemShares(final Long callerId, final Long itemId, final String searchString, final Collection<Long> preSelectedElements, final Integer offset, final Integer count, final List<OrderBy> orderBy) {
		return mapper.selectItemSharesByExample(createBaseExample("id", preSelectedElements, getComboTypeOrderByList(orderBy), c -> c.andIAComboItemIdEqualTo(callerId, itemId, searchString), null), createRowBounds(offset, count)).stream();
	}

	@Override
	public Integer getItemShareCount(final Long callerId, final Long itemId, final String searchString, final Collection<Long> preSelectedElements) {
		return mapper.countItemSharesByExample(createBaseExample("id", preSelectedElements, null, c -> c.andIAComboItemIdEqualTo(callerId, itemId, searchString), null));
	}

	@Override
	public List<ItemAssignment> getItemAssignmentsOfItem(final Long itemId) throws NoSuchRecordException {
		return mapper.selectByExample(new ItemAssignmentExample(c -> c.andItemIdEqualTo(itemId)));
	}

	@Override
	public Void grantPermissions(final Long itemId, final PermissionDTO permission, final Collection<Long> users) throws NoSuchRecordException {
		mapper.updateByExampleSelective(new ItemAssignment(permission), new ItemAssignmentExample(c -> c.andItemIdEqualTo(itemId).andUserIdIn(users)));
		return null;
	}

	@Override
	public Integer deleteAllForUser(final Long userId) {
		return mapper.deleteByExample(new ItemAssignmentExample(c -> c.andUserIdEqualTo(userId)));
	}

	@Override
	public Collection<ItemAssignment> getAssignmentsViaGroupId(final Long groupId) throws NoSuchRecordException {
		return mapper.selectByExample(new ItemAssignmentExample(c -> c.andItemGroupIdEqualTo(groupId)));
	}

	@Override
	public ItemAssignment getByDocumentId(final Long userId, final Long documentId) throws NoSuchRecordException {
		return mapper.selectByExampleWithDocument(new ItemAssignmentExample(c -> c.andDocumentIdEqualTo(documentId).andUserIdEqualTo(userId)));
	}
}
