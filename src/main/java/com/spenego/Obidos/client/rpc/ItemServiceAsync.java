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
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.dto.ItemExpiration;
import com.spenego.Obidos.shared.dto.ItemGroupsResult;
import com.spenego.Obidos.shared.dto.ItemsResult;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.dto.SharedItemDTO;
import com.spenego.Obidos.shared.dto.SharedItemsResult;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeValueDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * Items are the fundamental unit of the Obidos system.  Users share items, organize them in containers,
 * grant users permission to perform operations on them, and revoke them from users or groups.
 * 
 * This service provides the asynchronous methods to operate on items.
 * 
 * @since   Obidos1.0
 * @author  Mike Morgan
 * @see com.spenego.Obidos.client.rpc.ItemService;
 * 
 */
public interface ItemServiceAsync {
	/**
	 * Create an item.
	 *
	 * @param creds
	 * @param name The name of the new item. This is used when searching.
	 * @param itemExpiration TODO
	 * @param values The values that compose the item. An item can consist of multiple User Defined Types.
	 * @param shareable Will the item be sharable?
	 * @return The ID of the new Item.
	 */
	void create(AuthCredsDTO creds, String name, ItemExpiration itemExpiration, Long containerId, PostOpActions postOpActions, Boolean sharable, ArrayList<UserDefinedTypeValueDTO> values, AsyncCallback<ItemDTO> value);

	/**
	 * Create a new item using a ad-hoc, free-form template. You are able to specify the name and the fields of the type.
	 *
	 * @param creds
	 * @param name The name of the new item. This is used when searching.
	 * @param itemExpiration TODO
	 * @param template The template definition to use for this free-form item.
	 * @param values The values that compose the item. The values should match fields defined in the template.
	 * @param container The ID of the container that holds the item.
	 * @param shareable Will the item be sharable?
	 * @return The ID of the new Item.
	 */
	void create(AuthCredsDTO creds, String name, ItemExpiration itemExpiration, Long containerId, PostOpActions postOpActions, Boolean sharable, UserDefinedTypeDTO template, ArrayList<UserDefinedTypeValueDTO> values, AsyncCallback<ItemDTO> value);

	/**
	 * Returns a list of items owned by the user. The items in the list DO NOT have the contents, just names.
	 * Full items can be obtained by calling get().
	 *
	 * @param creds
	 * @param containerAssignmentId
	 * @param shared If true, only items that have been shared to other users are returned. Conversely, if false,
	 *               only items that have not been shared are returned. If null, all items are returned.
	 * @param nameSearch - search for item by name, pass null to not filter
	 * @param first
	 * @param count
	 * @param orderBy Orders the result set according to item name, update time, create time
	 * @throws ServerSideException
	 */
	void getMyItems(AuthCredsDTO creds, Long containerAssignmentId, Boolean shared, String nameSearch, List<Long> preSelectedItems, Integer first, Integer count, ArrayList<OrderBy> orderBy, AsyncCallback<ItemsResult> result);

	/**
	 * Get a specific item.
	 *
	 * @param creds
	 * @param itemId
	 * @param fieldsOrderby order the fields according to this
	 * @return An item that contains all the data and fields associated with that item.
	 */
	void get(AuthCredsDTO creds, Long itemId, ArrayList<OrderBy> fieldsOrderby, AsyncCallback<ItemDTO> result);

	/**
	 * Get a specific item. Load additional ownership and contact details about the item. Primarily used to see who owns
	 * the item when a user has an item shared with them.
	 *
	 * @param creds
	 * @param itemId
	 * @param fieldsOrderby order the fields according to this
	 * @return An item that contains all the data and fields associated with that item plus ownership and contact info.
	 * @throws ServerSideException
	 */
	void getSharedItem(AuthCredsDTO creds, Long itemId, ArrayList<OrderBy> fieldsOrderby, AsyncCallback<SharedItemDTO> result);

	/**
	 * Update an item.
	 * @param item
	 * @param user
	 *
	 * @return
	 */
	void update(AuthCredsDTO creds, ItemDTO item, AsyncCallback<Void> result);

	/**
	 * Deletes an item.  If the caller does not own the item (the item was shared with the caller), the item is
	 * relinquished.
	 *
	 * @param id
	 * @param user
	 *
	 * @return
	 * @throws ServerSideException
	 */
	void delete(AuthCredsDTO creds, List<Long> ids, AsyncCallback<Void> result);

	/**
	 * Share an item with another user.
	 *
	 * @param creds
	 * @param itemId The item you wish to share.
	 * @param recipientIds The IDs of the users you would like to share note with.
	 * @param shareComment pass this comment to the post share actions
	 * @param postShareActions actions to perform after item has been shared, usually sending email or notifications
	 * @return The ID of the new Item record.
	 */
	void shareItemWithUsers(AuthCredsDTO creds, Long itemId, ArrayList<Long> recipientIds, String shareComment, PostOpActions postShareActions, AsyncCallback<Void> result);

	/**
	 * Share an item with each user in group.
	 *
	 * @param creds
	 * @param itemId The item you wish to share.
	 * @param groupIds The IDs of the groups you would like to share item with.
	 * @param shareComment pass this comment to the post share actions
	 * @param postShareActions actions to perform after item has been shared, usually sending email or notifications
	 * @throws ServerSideException A SharingProhibitedException is thrown if the user attempts to share an item
	 *                             that is not owned by them.
	 */
	void shareItemWithGroups(AuthCredsDTO creds, Long itemId, ArrayList<Long> groupIds, String shareComment, PostOpActions postShareActions, AsyncCallback<Void> result);

	/**
	 * Returns a list of items that have been shared with this user.
	 *
	 * @param creds
	 * @param containerAssignmentId Only show items in this container. Pass null to show all containers.
	 * @param searchString Only items that have a name that contain the searchString are contained in the result.
	 * @param first
	 * @param count
	 * @param orderBy Orders the result set according to item name, update time, create time
	 */
	void getItemsSharedWithMe(AuthCredsDTO creds, Long containerAssignmentId, String searchString, List<Long> preSelectedItems, Integer first, Integer count, ArrayList<OrderBy> orderBy, AsyncCallback<SharedItemsResult> result);

	/**
	 * Revokes (un-shares) an item with a particular list of users.
	 *
	 * @param creds
	 * @param itemId The item you wish to revoke from the specified user.
	 * @param recipientIds The IDs of all the users you would like to revoke item sharing from.
	 * @throws ServerSideException
	 */
	void revokeFromUsers(AuthCredsDTO creds, Long itemId, ArrayList<Long> recipientIds, String revokeComment, PostOpActions postRevokeActions, AsyncCallback<Void> result);

	/**
	 * Revokes (un-shares) an item with all users.
	 *
	 * @param creds
	 * @param itemId The item you wish to revoke from all users.
	 * @throws ServerSideException
	 */
	void revokeSharedItem(AuthCredsDTO creds, Long itemId, String revokeComment, PostOpActions postRevokeActions, AsyncCallback<Void> result);

	/**
	 * Revokes (un-shares) an item with all users in specified groups.
	 *
	 * @param creds
	 * @param itemId The item you wish to revoke from all users.
	 * @param groupIds The IDs of the groups you would like to revoke item sharing from.
	 * @throws ServerSideException
	 */
	void revokeFromGroups(AuthCredsDTO creds, Long itemId, ArrayList<Long> groupIds, String revokeComment, PostOpActions postRevokeActions, AsyncCallback<Void> result);

	/**
	 * Returns a list of groups that have access to the item.
	 *
	 * @param creds
	 * @param itemId The item you wish to revoke from all users.
	 * @throws ServerSideException
	 */
	void getGroupsSharingItem(AuthCredsDTO creds, Long itemId, String nameSearch, ArrayList<Long> preSelectedGroups, Integer first, Integer count, ArrayList<OrderBy> orderBy, AsyncCallback<ItemGroupsResult> result);

	/**
	 * Grants the specified permission to the users for the specified item.
	 *
	 * @param creds
	 * @param itemId The item you wish to grant the specified permission to for the specified users.
	 * @param users The users who will receive the specified permission.
	 * @param permission The permission you wish to grant/change for the specified users. Pass null for permission values you do not wish to change.
	 * @throws ServerSideException
	 */
	void grantPermission(AuthCredsDTO creds, Long itemId, ArrayList<Long> users, PermissionDTO permission, AsyncCallback<Void> result);

	/**
	 * Grants the specified permission to the groups for the specified item.
	 *
	 * @param creds
	 * @param itemId The item you wish to grant the specified permission to for the specified groups.
	 * @param groups The groups who will receive the specified permission.
	 * @param permission The permission you wish to grant/change for the specified users. Pass null for permission values you do not wish to change.
	 * @throws ServerSideException
	 */
	void grantGroupPermission(AuthCredsDTO creds, Long itemId, ArrayList<Long> groups, PermissionDTO permission, AsyncCallback<Void> result);

	/**
	 * Take ownership of the specified item. The must fist grant Ownership Control permission for this call to succeed.
	 *
	 * @param creds
	 * @param itemId The item you wish to take ownership of. This is the actual item ID and not the item assignment ID passed back in lists.
	 * @throws ServerSideException
	 */
	void takeOwnership(AuthCredsDTO creds, Long itemId, Long containerAssignmentId, PostOpActions postRevokeActions, AsyncCallback<Void> result);

	/**
	 * Move an item to a different container.
	 *
	 * @param creds
	 * @param itemId The item you wish to move.
	 * @param containerAssignmentId The destination container.
	 * @throws ServerSideException
	 */
	void moveToContainer(AuthCredsDTO creds, Long itemId, Long containerAssignmentId, AsyncCallback<Void> result);
}
