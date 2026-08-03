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

package com.spenego.Obidos.server.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.server.actions.ContainerActions;
import com.spenego.Obidos.server.operations.SystemConfigOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.StreamSupplier;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.ContainerResult;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.dto.SharedContainerResult;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@Service("containerService")
public final class ContainerServiceImpl extends ObidosService implements ContainerService {
	private static final Logger logger = LoggerFactory.getLogger(ContainerServiceImpl.class);

	@Autowired private final ContainerActions			actions = null;
	@Autowired private final SystemConfigOperations	systemConfigOperations = null;

	private int getMemoryDelay() {
		try {
			return systemConfigOperations.get(1L).getMemoryWipeDelay();
		} catch(final Exception ex) {
			logger.warn(() -> "Caught " + ex + " while attempting to get system config and set memory wipe delay.");
			return 2000;
		}
	}

	@PostConstruct
	private void configureMemoryDelay() {
		logger.info(() -> "Configuring memory wipe delay.");
		final int ms = getMemoryDelay();
		memoryWiper.setMemoryWipeDelay(ms);
		logger.info(() -> "Memory wipe delay set to " + ms + "ms");
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

 	@Transactional @Override
	public Long create(final AuthCredsDTO creds, final String name, final Boolean isPrivate) throws ServerSideException {
		return userFunction(creds, "create a container", "createContainer", user -> actions.createContainer(user, name, isPrivate, summonPWHash()));
	}

 	@Transactional @Override
	public Void update(final AuthCredsDTO creds, final ContainerDTO container) throws ServerSideException {
		return userFunction(creds, "update a container", "update", user -> actions.update(user, container, summonPWHash()));
	}

 	@Transactional @Override
	public Void delete(final AuthCredsDTO creds, final ArrayList<Long> ids) throws ServerSideException {
		return userFunction(creds, "delete a container", "delete", user -> actions.delete(user, ids, summonPWHash()));
	}

 	@Transactional(readOnly=true) @Override
	public ContainerResult getMyContainers(final AuthCredsDTO creds, final String search, List<Long> preSelectedContainers, final Boolean shared, final Boolean shareable, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
 		return userFunction(creds, "get containers", "getMyContainers", user -> actions.getMyContainers(user, search, preSelectedContainers, shared, shareable, first, count, orderBy));
	}

 	@Transactional(readOnly=true) @Override
	public SharedContainerResult getContainersSharedWithMe(final AuthCredsDTO creds, final String search, final List<Long> preSelectedContainers, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
		return userFunction(creds, "get containers", "getContainersSharedWithMe", user -> actions.getContainersSharedWithMe(user, search, preSelectedContainers, first, count, orderBy));
	}

 	@Transactional @Override
	public Void shareContainerWithUsers(final AuthCredsDTO creds, final Long containerId, final ArrayList<Long> userIds, final String shareComment, final PostOpActions postShareActions) throws ServerSideException {
		return userFunction(creds, "share a container with users", "shareContainerWithUsers", user -> actions.shareContainerWithUsers(user, containerId, StreamSupplier.create(userIds), summonPWHash(), shareComment, postShareActions));
	}

 	@Transactional @Override
	public Void revokeContainerFromUsers(final AuthCredsDTO creds, final Long containerId, final ArrayList<Long> userIds, final String revokeComment, final PostOpActions postRevokeActions) throws ServerSideException {
		return userFunction(creds, "revoke a container from users", "revokeContainerFromUsers", user -> actions.revokeContainerFromUsers(user, containerId, StreamSupplier.create(userIds), summonPWHash(), revokeComment, postRevokeActions));
	}

 	@Transactional @Override
	public Void shareContainerWithGroups(final AuthCredsDTO creds, final Long containerId, final ArrayList<Long> groupIds, final String shareComment, final PostOpActions postShareActions) throws ServerSideException {
		return userFunction(creds, "share a container with group", "shareContainerWithGroups", user -> actions.shareContainerWithGroups(user, containerId, StreamSupplier.create(groupIds), summonPWHash(), shareComment, postShareActions));
	}

 	@Transactional @Override
	public Void revokeContainerFromGroups(final AuthCredsDTO creds, final Long containerId, final ArrayList<Long> groupIds, final String revokeComment, final PostOpActions postRevokeActions) throws ServerSideException {
		return userFunction(creds, "revoke a container from groups", "revokeContainerFromGroups", user -> actions.revokeContainerFromGroups(user, containerId, groupIds, summonPWHash(), revokeComment));
	}

 	@Transactional @Override
	public Void grantPermission(final AuthCredsDTO creds, final Long containerAssignmentId, final ArrayList<Long> users, final PermissionDTO permission) throws ServerSideException {
 		return userFunction(creds, "grant permission to users.", "grantPermission", user -> actions.grantUsersPermission(user, containerAssignmentId, permission, users, summonPWHash()));
	}

 	@Transactional @Override
	public Void grantGroupPermission(final AuthCredsDTO creds, final Long containerAssignmentId, final ArrayList<Long> groups, final PermissionDTO permission) throws ServerSideException {
 		return userFunction(creds, "grant permission to users.", "grantPermission", caller -> actions.grantGroupsPermission(caller, containerAssignmentId, permission, groups, summonPWHash()));
	}

 	@Transactional @Override
	public Void takeOwnership(final AuthCredsDTO creds, final Long containerAssignmentId, final PostOpActions postOpActions) throws ServerSideException {
		return userFunction(creds, "take ownership of object.", "takeOwnership", user -> actions.takeOwnership(user, containerAssignmentId, summonPWHash(), postOpActions));
	}

 	@Transactional(readOnly=true) @Override
	public ContainerDTO get(final AuthCredsDTO creds, final Long containerId) throws ServerSideException {
		return userFunction(creds, "get a container", "get", user -> actions.get(user, containerId));
	}
}
