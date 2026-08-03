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

import com.spenego.Obidos.server.dao.ContainerGroupAssignmentMapper;
import com.spenego.Obidos.server.dao.Mapper;
import com.spenego.Obidos.server.model.ContainerGroupAssignment;
import com.spenego.Obidos.server.model.ContainerGroupAssignmentExample;
import com.spenego.Obidos.server.operations.ContainerGroupAssignmentOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public class ContainerGroupAssignmentOperationsImpl extends ObidosOperations<ContainerGroupAssignment> implements ContainerGroupAssignmentOperations {
	private static final Logger	logger = LoggerFactory.getLogger(ContainerGroupAssignmentOperationsImpl.class);

	@Autowired
	private final ContainerGroupAssignmentMapper mapper = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected Mapper<ContainerGroupAssignment> getMapper() {
		return mapper;
	}

	@Override
	protected String getModelName() {
		return "ContainerGroupAssignment";
	}

	@Override
	public Long create(final ContainerGroupAssignment cga) {
		return cga.getId() != null ? super.create(cga) : createWithRandomID(cga);
	}

	@Override
	public ContainerGroupAssignment get(final Long containerId, final Long groupId) throws NoSuchRecordException {
		final List<ContainerGroupAssignment> list = mapper.selectByExample(new ContainerGroupAssignmentExample(c -> c.andContainerIdEqualTo(containerId).andGroupIdEqualTo(groupId)));

		if (list == null || list.isEmpty()) {
			throw new NoSuchRecordException("");
		}
		return list.get(0);
	}

	private Stream<ContainerGroupAssignment> getStream(final Consumer<ContainerGroupAssignmentExample.Criteria> c) {
		return mapper.selectByExample(new ContainerGroupAssignmentExample(c)).stream();
	}

	@Override
	public Stream<ContainerGroupAssignment> getAll(final Long containerId) {
		return getStream(c -> c.andContainerIdEqualTo(containerId));
	}

	@Override
	public Stream<ContainerGroupAssignment> getContainers(final Long groupId) {
		return getStream(c -> c.andGroupIdEqualTo(groupId));
	}

	@Override
	public Integer getShareCount(final Long containerId) {
		return countByExample(new ContainerGroupAssignmentExample(c -> c.andContainerIdEqualTo(containerId)));
	}

	@Override
	public Integer delete(final Long containerId, final Long groupId) {
		return mapper.deleteByExample(new ContainerGroupAssignmentExample(c -> c.andContainerIdEqualTo(containerId).andGroupIdEqualTo(groupId)));
	}

	@Override
	public Void grantPermissions(final Long containerId, final PermissionDTO permission, final Collection<Long> groups) {
		mapper.updateByExampleSelective(new ContainerGroupAssignment(permission), new ContainerGroupAssignmentExample(c -> c.andContainerIdEqualTo(containerId).andGroupIdIn(groups)));
		return null;
	}

	@Override
	public Boolean mayAddItems(final Long userId, final Long containerId) {
		return mapper.hasGroupAdd(userId, containerId) > 0;
	}

	@Override
	public Boolean mayUpdateItems(final Long userId, final Long containerId) {
		return mapper.hasGroupUpdate(userId, containerId) > 0;
	}
}
