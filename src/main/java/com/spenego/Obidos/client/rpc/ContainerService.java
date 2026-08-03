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

package com.spenego.Obidos.client.rpc;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.ContainerResult;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.dto.SharedContainerResult;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * All items exist within containers.  Users may share containers, and thus, all items within that container.
 * 
 * 
 * @since   Obidos1.0
 * @author  Mike Morgan
 * @see com.spenego.Obidos.client.rpc.ContainerServiceAsync;
 * 
 */
@RemoteServiceRelativePath("rpc/containerService")
public interface ContainerService extends RemoteService {
	public static class Utility {
		private Utility() { /* no instances */ }
		private static final ContainerServiceAsync instance = (ContainerServiceAsync) GWT.create(ContainerService.class);
		public static final ContainerServiceAsync getInstance() { return instance; }
	}

	Long create(AuthCredsDTO creds, String name, Boolean isPrivate) throws ServerSideException;

	/**
	 * If the user owns the container, the container and all items in the container are deleted.
	 *
	 * If the container was simply shared with the caller (not owned by the caller), the container is relinquished by the caller.
	 *
	 * @param creds
	 * @param containerId
	 * @return
	 * @throws ServerSideException
	 */
	Void delete(AuthCredsDTO creds, ArrayList<Long> containerId) throws ServerSideException;

	/**
	 * Returns the containers owned by the caller.
	 *
	 * @param creds
	 * @param search Only return containers that have this string in their name.
	 * @param shared When true, only containers that have been shared are returned.
	 * @param shareable pass false if you want only private containers, true for shareable, of null for both
	 * @param first
	 * @param count
	 * @param orderBy
	 * @return
	 * @throws ServerSideException
	 */
	ContainerResult getMyContainers(AuthCredsDTO creds, String search, List<Long> preSelectedContainers, Boolean shared, Boolean shareable, Integer first, Integer count, ArrayList<OrderBy> orderBy) throws ServerSideException;

	/**
	 * Returns the containers that have been shared with this user.
	 *
	 * @param creds
	 * @param search
	 * @param first
	 * @param count
	 * @param orderBy
	 * @return
	 * @throws ServerSideException
	 */
	SharedContainerResult getContainersSharedWithMe(AuthCredsDTO creds, String search, List<Long> preSelectedContainers, Integer first, Integer count, ArrayList<OrderBy> orderBy) throws ServerSideException;

	ContainerDTO get(AuthCredsDTO creds, Long containerId) throws ServerSideException;

	/**
	 * Updates the name of a container (the only field which may be changed).  isPrivate is immutable.
	 *
	 * @param creds
	 * @param container
	 * @return
	 */
	Void update(AuthCredsDTO creds, ContainerDTO container) throws ServerSideException;

	/**
	 *
	 * @param creds
	 * @param containerId		The container that will be shared.
	 * @param shareComment		The message to send to the recipient.
	 * @param userId			The recipient of the shared container.
	 * @return
	 * @throws ServerSideException
	 */
	Void shareContainerWithUsers(  AuthCredsDTO creds, Long containerId, ArrayList<Long> userIds,  String shareComment, PostOpActions postShareActions) throws ServerSideException;
	Void shareContainerWithGroups( AuthCredsDTO creds, Long containerId, ArrayList<Long> groupIds, String shareComment, PostOpActions postShareActions) throws ServerSideException;
	Void revokeContainerFromUsers( AuthCredsDTO creds, Long containerId, ArrayList<Long> userIds,  String revokeComment, PostOpActions postRevokeActions) throws ServerSideException;
	Void revokeContainerFromGroups(AuthCredsDTO creds, Long containerId, ArrayList<Long> groupIds, String revokeComment, PostOpActions postRevokeActions) throws ServerSideException;

	/**
	 * Gives the specified users permission to perform various actions on the Container.
	 * 
	 * @param creds
	 * @param containerAssignmentId The container to grant permission to
	 * @param users                 A list of users that should be granted this permission
	 * @param permission            The type of permission to grant
	 * @return
	 * @throws ServerSideException
	 */
	Void grantPermission			(AuthCredsDTO creds, Long containerAssignmentId, ArrayList<Long> users, PermissionDTO permission) throws ServerSideException;

	/**
	 * Gives the specified groups permission to perform various actions on the Container.
	 * 
	 * @param creds
	 * @param containerAssignmentId The container to grant permission to
	 * @param groups                A list of groups that should be granted this permission
	 * @param permission            The type of permission to grant
	 * @return
	 * @throws ServerSideException
	 */
	Void grantGroupPermission		(AuthCredsDTO creds, Long containerAssignmentId, ArrayList<Long> groups, PermissionDTO permission) throws ServerSideException;
	Void takeOwnership				(AuthCredsDTO creds, Long containerAssignmentId, PostOpActions postOpActions) throws ServerSideException;
}