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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.ContainerResult;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.dto.SharedContainerResult;

public interface ContainerServiceAsync {
	/**
	 * Creates a container. Containers that are private may not share items.
	 *
	 * @param creds
	 * @param name
	 * @param isPrivate
	 * @param callback
	 */
	void create			(AuthCredsDTO creds, String name, Boolean isPrivate, AsyncCallback<Long> callback);

	/**
	 * Deletes the specified container if called by the owner of the container. All items within the container,
	 * and all shares of that item, are deleted too.
	 *
	 * If the container was simply shared with the caller, the container is relinquished by the caller.
	 *
	 * @param creds
	 * @param containerId
	 * @param callback
	 */
	void delete			(AuthCredsDTO creds, ArrayList<Long> containerIds, AsyncCallback<Void> callback);

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
	 */
	void getMyContainers(AuthCredsDTO creds, String search, List<Long> preSelectedContainers, Boolean shared, Boolean shareable, Integer first, Integer count, ArrayList<OrderBy> orderBy, AsyncCallback<ContainerResult> callback);

	/**
	 * Returns the containers that have been shared with this user.
	 *
	 * @param creds
	 * @param search
	 * @param first
	 * @param count
	 * @param orderBy
	 * @return
	 */
	void getContainersSharedWithMe(AuthCredsDTO creds, String search, List<Long> preSelectedContainers,  Integer first, Integer count, ArrayList<OrderBy> orderBy, AsyncCallback<SharedContainerResult> callback);


	/**
	 * Updates the name of a container (the only field which may be changed).  isPrivate is immutable.
	 *
	 * @param creds
	 * @param container
	 * @param callback
	 */
	void update						(AuthCredsDTO creds, ContainerDTO container, AsyncCallback<Void> callback);
	void get						(AuthCredsDTO creds, Long containerId, AsyncCallback<ContainerDTO> callback);
	void shareContainerWithUsers	(AuthCredsDTO creds, Long containerId, ArrayList<Long> userIds,  String shareComment, PostOpActions postShareActions, AsyncCallback<Void> callback);
	void shareContainerWithGroups	(AuthCredsDTO creds, Long containerId, ArrayList<Long> groupIds, String shareComment, PostOpActions postShareActions, AsyncCallback<Void> callback);
	void revokeContainerFromUsers	(AuthCredsDTO creds, Long containerId, ArrayList<Long> userIds,  String shareComment, PostOpActions postRevokeActions, AsyncCallback<Void> callback);
	void revokeContainerFromGroups	(AuthCredsDTO creds, Long containerId, ArrayList<Long> groupIds, String shareComment, PostOpActions postRevokeActions, AsyncCallback<Void> callback);
	void grantPermission			(AuthCredsDTO creds, Long containerId, ArrayList<Long> users,	 PermissionDTO permission, AsyncCallback<Void> callback);
	void grantGroupPermission		(AuthCredsDTO creds, Long containerId, ArrayList<Long> groups,	 PermissionDTO permission, AsyncCallback<Void> callback);
	void takeOwnership				(AuthCredsDTO creds, Long containerId, PostOpActions postOpActions, AsyncCallback<Void> callback);
}
