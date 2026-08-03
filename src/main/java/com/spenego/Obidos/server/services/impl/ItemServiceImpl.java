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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.server.actions.ItemActions;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.StreamSupplier;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.dto.ItemExpiration;
import com.spenego.Obidos.shared.dto.ItemGroupsResult;
import com.spenego.Obidos.shared.dto.ItemsResult;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.dto.SecurityClassificationDTO;
import com.spenego.Obidos.shared.dto.SharedItemDTO;
import com.spenego.Obidos.shared.dto.SharedItemsResult;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeValueDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@Service("itemService")
public final class ItemServiceImpl extends ObidosService implements ItemService {
	private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

	@Autowired private final ItemActions actions = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

 	@Transactional @Override
	public ItemDTO create(final AuthCredsDTO creds, final String name, final ItemExpiration itemExpiration, final Long containerId, final PostOpActions postOpActions, final Boolean sharable, final ArrayList<UserDefinedTypeValueDTO> values) throws ServerSideException {
		return callWithWipe(() -> userFunction(creds, "create item", "create", user -> actions.create(user, name, itemExpiration, containerId, summonPWHash(), postOpActions, sharable, new SecurityClassificationDTO(), values)), values);
	}

 	@Transactional @Override
	public ItemDTO create(final AuthCredsDTO creds, final String name, final ItemExpiration itemExpiration, final Long containerId, final PostOpActions postOpActions, final Boolean sharable, final UserDefinedTypeDTO template, final ArrayList<UserDefinedTypeValueDTO> values) throws ServerSideException {
		return callWithWipe(() -> userFunction(creds, "create item", "create", user -> actions.create(user, name, itemExpiration, containerId, summonPWHash(), postOpActions, sharable, new SecurityClassificationDTO(), template, values)), values);
	}

 	@Transactional(readOnly=true) @Override
	public ItemsResult getMyItems(final AuthCredsDTO creds, final Long containerAssignmentId, final Boolean shared, final String nameSearch, final List<Long> preSelectedItems, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
		return userFunction(creds, "get items", "getMyItems", user -> actions.getMyItems(user, containerAssignmentId, shared, nameSearch, preSelectedItems, first, count, orderBy));
	}

 	@Transactional(readOnly=true) @Override
	public ItemDTO get(final AuthCredsDTO creds, final Long itemAssignmentId, final ArrayList<OrderBy> fieldsOrderby) throws ServerSideException {
		return userFunction(creds, "get item", "get", user -> actions.get(user, itemAssignmentId, summonPWHash(), fieldsOrderby));
 	}

 	@Transactional(readOnly=true) @Override
	public SharedItemDTO getSharedItem(final AuthCredsDTO creds, final Long itemAssignmentId, final ArrayList<OrderBy> fieldsOrderby) throws ServerSideException {
 		logger.info(() -> "getSharedItem: itemId = " + itemAssignmentId + ", xsrf Token - " + creds.getXsrfToken());
		return userFunction(creds, "get shared item", "getSharedItem", user -> actions.getSharedItem(user, itemAssignmentId, summonPWHash(), fieldsOrderby));
	}

 	@Transactional @Override
	public Void update(final AuthCredsDTO creds, final ItemDTO item) throws ServerSideException {
		return callWithWipe(() -> userFunction(creds, "update item.", "update", user -> actions.update(user, item, summonPWHash())), item.getValues());
	}

 	@Transactional @Override
	public Void delete(final AuthCredsDTO creds, final List<Long> ids) throws ServerSideException {
		return userFunction(creds, "delete item.", "delete", user -> actions.delete(user, ids, summonPWHash()));
	}

 	@Transactional @Override
	public Void shareItemWithUsers(final AuthCredsDTO creds, final Long itemAssignmentId, final ArrayList<Long> recipientIds, final String shareComment, final PostOpActions postShareActions) throws ServerSideException {
		return userFunction(creds, "share item with users", "shareItemWithUsers", user -> actions.shareItemWithUsers(user, actions.getItemId(user, itemAssignmentId), summonPWHash(), postShareActions, StreamSupplier.create(recipientIds), shareComment, Boolean.TRUE));
	}

 	@Transactional @Override
	public Void shareItemWithGroups(final AuthCredsDTO creds, final Long itemAssignmentId, final ArrayList<Long> groupIds, final String shareComment, final PostOpActions postShareActions) throws ServerSideException {
		return userFunction(creds, "share item with groups", "shareItemWithGroups", user -> actions.shareItemWithGroups(user, itemAssignmentId, summonPWHash(), postShareActions, StreamSupplier.create(groupIds), null, Boolean.TRUE, shareComment));
	}

 	@Transactional(readOnly=true) @Override
	public SharedItemsResult getItemsSharedWithMe(final AuthCredsDTO creds, final Long containerAssignmentId, final String searchString, final List<Long> preSelectedItems, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
		return userFunction(creds, "get items shared with me.", "getItemsSharedWithMe", user -> actions.getItemsSharedWithUser(user, containerAssignmentId, searchString, preSelectedItems, first, count, orderBy));
	}

 	@Transactional(readOnly=true) @Override
	public ItemGroupsResult getGroupsSharingItem(final AuthCredsDTO creds, final Long itemAssignmentId, final String nameSearch, final ArrayList<Long> preSelectedGroups, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
		return userFunction(creds, "get groups sharing item.", "getGroupsSharingItem", user -> actions.getGroupsSharingItem(user, itemAssignmentId, nameSearch, preSelectedGroups, first, count, orderBy));
	}

 	@Transactional @Override
	public Void revokeFromGroups(final AuthCredsDTO creds, final Long itemAssignmentId, final ArrayList<Long> groupIds, final String revokeComment, final PostOpActions postRevokeActions) throws ServerSideException {
		return userFunction(creds, "revoke a shared item from group.", "revokeSharedItemFromGroup", user -> actions.revokeItemFromGroups(user, itemAssignmentId, StreamSupplier.create(groupIds), summonPWHash(), revokeComment, postRevokeActions));
	}

 	@Transactional @Override
	public Void revokeFromUsers(final AuthCredsDTO creds, final Long itemAssignmentId, final ArrayList<Long> recipientIds, final String revokeComment, final PostOpActions postRevokeActions) throws ServerSideException {
		return userFunction(creds, "revoke a shared item from user.", "revokeFromUsers", user -> actions.revokeItemFromUsers(user, itemAssignmentId, recipientIds, summonPWHash(), Boolean.TRUE, revokeComment, postRevokeActions));
	}

 	@Transactional @Override
	public Void revokeSharedItem(final AuthCredsDTO creds, final Long itemAssignmentId, final String revokeComment, final PostOpActions postRevokeActions) throws ServerSideException {
		return userFunction(creds, "revoke a shared item from all users.", "revokeSharedItem", user -> actions.revokeItem(user, itemAssignmentId, summonPWHash(), revokeComment, postRevokeActions));
	}

 	@Transactional @Override
	public Void grantPermission(final AuthCredsDTO creds, final Long itemAssignmentId, final ArrayList<Long> users, final PermissionDTO permission) throws ServerSideException {
		return userFunction(creds, "grant permission to users.", "grantPermission", user -> actions.grantUsersPermission(user, itemAssignmentId, permission, users, summonPWHash()));
	}

 	@Transactional @Override
	public Void grantGroupPermission(final AuthCredsDTO creds, final Long itemAssignmentId, final ArrayList<Long> groups, final PermissionDTO permission) throws ServerSideException {
		return userFunction(creds, "grant permission to groups.", "grantGroupPermission", caller -> actions.grantGroupsPermission(caller, itemAssignmentId, permission, groups, summonPWHash()));
 	}

	@Transactional @Override
	public Void takeOwnership(final AuthCredsDTO creds, final Long itemAssignmentId, final Long containerAssignmentId, final PostOpActions postOpActions) throws ServerSideException {
		return userFunction(creds, "take ownership of item.", "takeOwnership", user -> actions.takeOwnership(user, itemAssignmentId, containerAssignmentId, summonPWHash(), postOpActions));
	}

 	@Transactional @Override
	public Void moveToContainer(final AuthCredsDTO creds, final Long itemAssignmentId, final Long containerAssignmentId) throws ServerSideException {
		return userFunction(creds, "move item to container.", "moveToContainer", user -> actions.moveToContainer(user, itemAssignmentId, containerAssignmentId, summonPWHash()));
	}
}
