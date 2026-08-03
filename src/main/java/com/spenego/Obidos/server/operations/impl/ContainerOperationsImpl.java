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

import com.spenego.Obidos.server.dao.AssignedContainerMapper;
import com.spenego.Obidos.server.dao.ContainerMapper;
import com.spenego.Obidos.server.model.AssignedContainer;
import com.spenego.Obidos.server.model.AssignedContainerExample;
import com.spenego.Obidos.server.model.Container;
import com.spenego.Obidos.server.model.ContainerExample;
import com.spenego.Obidos.server.operations.ContainerOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public final class ContainerOperationsImpl extends ObidosOperations<Container> implements ContainerOperations
{
	private static final Logger	logger = LoggerFactory.getLogger(ContainerOperationsImpl.class);

	@Autowired
	private final ContainerMapper			containerMapper = null;
	@Autowired
	private final AssignedContainerMapper	assignedContainerMapper = null;

	@Override
	protected Logger getLogger()			{ return logger; }
	@Override
	protected ContainerMapper getMapper()	{ return containerMapper; }
	@Override
	protected String getModelName()			{ return "container"; }

	@Override
	public Long create(final Container container) {
		container.setShared(false);
		return container.getId() != null ? super.create(container) : createWithRandomID(container);
	}

	private AssignedContainerExample createExample(final Long userId, final Long ownerId, final Long notOwnerId, final String search, final Collection<Long> preSelectedContainers, final Boolean shared, final Boolean shareable, final List<OrderBy> orderBy) {
		final AssignedContainerExample example = new AssignedContainerExample();
		final AssignedContainerExample.Criteria criteria = example.createCriteria();

		if (userId != null) {
			criteria.andUserIdEqualTo(userId);
		}

		if (ownerId != null) {
			criteria.andOwnerIdEqualTo(ownerId);
		}

		if (notOwnerId != null) {
			criteria.andOwnerIdNotEqualTo(notOwnerId);
		}

		if (search != null) {
			if (search.startsWith("!")) {
				criteria.andNameNotLike(search.substring(1));
			} else {
				criteria.andNameLike(search);
			}
		}

		if (shared != null) {
			criteria.andSharedEqualTo(shared);
		}

		if (shareable != null) {
			criteria.andIsPrivateEqualTo(!shareable);
		}

		if (orderBy != null) {
			example.setOrderByClause(generatePreSelectedOrderClause(preSelectedContainers, "ca.id", orderBy));
		}

		if (preSelectedContainers != null && !preSelectedContainers.isEmpty()) {
			example.or(example.createCriteria().andContainerAssignmentIdIn(preSelectedContainers).andContainerAssignmentOwnerIs(userId));
		}

		return example;
	}

	@Override
	public Integer getUserContainerCount(final Long userId, final String search, final Collection<Long> preSelectedContainers, final Boolean shared, final Boolean shareable) {
		return assignedContainerMapper.countByExample(createExample(userId, userId, null, search, preSelectedContainers, shared, shareable, null));
	}

	@Override
	public Stream<AssignedContainer> getUserContainers(final Long userId, final String search, final Collection<Long> preSelectedContainers, final Boolean shared, final Boolean shareable, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return assignedContainerMapper.selectByExample(createExample(userId, userId, null, search, preSelectedContainers, shared, shareable, orderBy), createRowBounds(first, count)).stream();
	}

	@Override
	public Integer getContainersSharedWithUserCount(final Long userId, final String search, final Collection<Long> preSelectedContainers) {
		return assignedContainerMapper.countByExample(createExample(userId, null, userId, search, preSelectedContainers, null, null, null));
	}

	@Override
	public Stream<AssignedContainer> getContainersSharedWithUser(final Long userId, final String search, final Collection<Long> preSelectedContainers, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return assignedContainerMapper.selectByExample(createExample(userId, null, userId, search, preSelectedContainers, null, null, orderBy), createRowBounds(first, count)).stream();
	}

	/**
	 * AssignedContainers are created via a view, so there is no creation of, deletion of, or updates
	 * of AssignedContainers.
	 */
	@Override
	public Stream<AssignedContainer> getAssignedContainers(final Long userId) {
		return assignedContainerMapper.selectByExample(new AssignedContainerExample(c -> c.andUserIdEqualTo(userId)), createRowBounds(null, null)).stream();
	}

	private AssignedContainer container(final String name, final Consumer<AssignedContainerExample.Criteria> c) throws NoSuchRecordException {
		final List<AssignedContainer> containers = assignedContainerMapper.selectByExample(new AssignedContainerExample(c), createRowBounds(null, null));

		if (containers.size() != 1) {
			throw new NoSuchRecordException("Could not find container called " + name);
		}

		return containers.get(0);
	}

	@Override
	public AssignedContainer getContainer(final Long userId, final String name) throws NoSuchRecordException {
		return container(name, c -> c.andOwnerIdEqualTo(userId).andNameEqualTo(name));
	}

	@Override
	public AssignedContainer getAssignedContainer(final Long userId, final String name) throws NoSuchRecordException {
		return container(name, c -> c.andUserIdEqualTo(userId).andNameEqualTo(name));
	}

	@Override
	public Stream<Container> getContainers(final Long containerId) {
		return containerMapper.selectByExample(new ContainerExample(c -> c.andParentIdEqualTo(containerId))).stream();
	}

	@Override
	public Integer deleteAllForUser(final Long userId) {
		return containerMapper.deleteByExample(new ContainerExample(c -> c.andUserIdEqualTo(userId)));
	}

	@Override
	public boolean mayTakeOwnership(Long userId, Long containerId) {		
		return containerMapper.hasGroupOwnershipControl(userId, containerId) > 0;
	}
}
