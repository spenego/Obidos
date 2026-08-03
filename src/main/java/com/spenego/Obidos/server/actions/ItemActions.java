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

package com.spenego.Obidos.server.actions;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.spenego.Obidos.server.model.Item;
import com.spenego.Obidos.server.model.ItemAssignment;
import com.spenego.Obidos.server.model.LimitedItem;
import com.spenego.Obidos.server.model.LimitedUser;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.security.Encryption.PublicKeyDecryptor;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.utils.ObidosExecutor;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.dto.ItemExpiration;
import com.spenego.Obidos.shared.dto.ItemGroupsResult;
import com.spenego.Obidos.shared.dto.ItemsResult;
import com.spenego.Obidos.shared.dto.NotesResult;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.dto.SecurityClassificationDTO;
import com.spenego.Obidos.shared.dto.SharedItemDTO;
import com.spenego.Obidos.shared.dto.SharedItemsResult;
import com.spenego.Obidos.shared.dto.SharedNotesResult;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeValueDTO;

/**
 * Items are the fundamental unit of the Obidos system.  Users share items, organize them in containers,
 * grant users permission to perform operations on them, and revoke them from users or groups.
 *
 * @since   Obidos1.0
 * @author  Mike Morgan
 * @see     com.spenego.Obidos.server.actions.impl.ObidosActions
 *
 */
public interface ItemActions {
	@FunctionalInterface
	public interface GroupItemConsumer {
		void apply(Item item, Supplier<Stream<UserDefinedTypeValueDTO>> values, Long groupId);
	}

	ItemAssignment				getItemAssignment(Long itemId, Long userId);

	ItemDTO						create				(User caller, String name, ItemExpiration itemExpiration, Long containerId, PassphraseHash passphraseHash, PostOpActions postOpActions, Boolean sharable, SecurityClassificationDTO securityClassificationDTO, List<UserDefinedTypeValueDTO> values);
	ItemDTO						create				(User caller, String name, ItemExpiration itemExpiration, Long containerId, PassphraseHash passphraseHash, PostOpActions postOpActions, Boolean sharable, SecurityClassificationDTO securityClassificationDTO, UserDefinedTypeDTO template, List<UserDefinedTypeValueDTO> values);
	ItemsResult					getMyItems			(User caller, Long containerId, Boolean shared, String nameSearch, Collection<Long> preSelectedItems, Integer first, Integer count, List<OrderBy> orderBy);
	NotesResult					getMyNotes			(User caller, Boolean shared, String nameSearch, Collection<Long> preSelectedItems, Integer first, Integer count, List<OrderBy> orderBy);
	Void						update				(User caller, ItemDTO item, PassphraseHash passphraseHash);
	Void						delete				(User caller, Collection<Long> itemIds, PassphraseHash passphraseHash);
	Void						moveToContainer		(User caller, Long itemAssignmentId, Long containerAssignmentId, PassphraseHash passphraseHash);
	boolean						isNote				(Item item);
	Map<Long,Long>				createItemAssignmentsLookupTable(Long itemId);
	void						notifyUpdateRecipients(User owner, Supplier<Stream<Long>> recipientSupplier, Long itemId, String comment);
	Supplier<Stream<UserDefinedTypeValueDTO>>		createUDTValueListSupplier(User caller, Long itemId, Supplier<PublicKeyDecryptor> pkdSupplier);


	/**
	 * Share an item with a stream of users. A user supplied comment is sent to each recipient.
	 *
	 * @param user
	 * @param itemId
	 * @param passphraseHash
	 * @param postShareActions
	 * @param recipientSuppliers
	 * @param shareComment
	 * @return
	 */
	Void						shareItemWithUsers(User caller, Long itemId, PassphraseHash passphraseHash, PostOpActions postOpActions, Supplier<Stream<Long>> recipientSuppliers, String shareComment, Boolean sharedExplicitly);

	/**
	 * This method is intended to only be called from other Actions. It does not do any validation.
	 *
	 * @param item
	 * @param ownerId
	 * @param recipientId
	 * @param itemValueSupplier
	 * @return
	 */
	Void						shareItemWithUser(LimitedItem item, Long ownerId, Long recipientId, PostOpActions postOpActions, Supplier<Stream<UserDefinedTypeValueDTO>> itemValueSupplier, Consumer<LimitedUser> consumer, Boolean sharedExplicitly);

	/**
	 * This method is intended to only be called from other Actions. It does not do any validation.
	 *
	 * @param user
	 * @param item
	 * @param groupId
	 * @param valueSupplier
	 * @param consumer
	 * @param shareComment
	 * @return
	 */
	Void						shareItemWithGroup(User caller, LimitedItem item, Long groupId, PostOpActions postOpActions, Supplier<Stream<UserDefinedTypeValueDTO>> valueSupplier, Consumer<LimitedUser> consumer, Boolean sharedExplicitly, String shareComment);

	/**
	 * Shares an item (supplied by the user's item assignment ID) with a stream of groups. The consumer is invoked for each user in each group from the stream. This is done to collect
	 * a set of users so that a notification is not sent to one user multiple times (if the user was in multiple groups).
	 *
	 * @param user
	 * @param itemAssignmentId
	 * @param passphraseHash
	 * @param groupIdSupplier
	 * @param consumer
	 * @param shareComment
	 * @return
	 */
	Void						shareItemWithGroups(User caller, Long itemAssignmentId, PassphraseHash passphraseHash, PostOpActions postOpActions, Supplier<Stream<Long>> groupIdSupplier, Consumer<LimitedUser> consumer, Boolean sharedExplicitly, String shareComment);

	/**
	 * This method is intended to only be called from other Actions. It does not do any validation.
	 *
	 * @param user
	 * @param itemId
	 * @param pkdSupplier
	 * @param groupIdSupplier
	 * @param consumer
	 * @return
	 */
	Void						shareItemWithGroups	(User caller, Long itemId, PostOpActions postOpActions, Supplier<Stream<UserDefinedTypeValueDTO>> valueSupplier, Supplier<Stream<Long>> groupIdSupplier, final Collection<Long> recipients);

	/**
	 * Revoke an item from users.  A runnable is returned which will notify the users from which the item was revoked.
	 *
	 * @param caller
	 * @param itemId
	 * @param recipientSupplier
	 * @param passphraseHash
	 * @param explicit         True if the item is being revoked explicitly as an Item and not from a container revoke
	 * @param revokeComment
	 * @return
	 */
	Void						revokeItemFromUsers(User caller, Long itemId, Supplier<Stream<Long>> recipientSupplier, PassphraseHash passphraseHash, Boolean explicit, String revokeComment, PostOpActions postRevokeActions);

	/**
	 * Revokes (un-shares) an item with all users.
	 */
	Void						revokeItem			(User caller, Long itemAssignmentId, PassphraseHash passphraseHash, String revokeComment, PostOpActions postRevokeActions);

	/**
	 *
	 * @param caller
	 * @param itemAssignmentId
	 * @param recipients
	 * @param passphrase
	 * @param explicit         True if the item is being revoked explicitly as an Item and not from a container revoke
	 * @param revokeComment
	 * @return
	 */
	Void						revokeItemFromUsers	(User caller, Long itemAssignmentId, Collection<Long> recipients, PassphraseHash passphrase, Boolean explicit, String revokeComment, PostOpActions postRevokeActions);

	/**
	 * Revoke an item from a group; decrementing the reference count for each group members item assignment id reference count.
	 * This method is internal only; it is not called from the service layer since it does not do certain sanity/security checks.
	 * @param caller
	 * @param itemId
	 * @param groupIdSupplier
	 * @param revokeComment
	 * @param sendNotifications
	 * @return
	 */
	Void						revokeItemFromGroups(User caller, Long itemId, Supplier<Stream<Long>> groupIdSupplier, ObidosExecutor onCompletion);
	Void						revokeItemFromGroups(User caller, Long itemAssignmentId, Supplier<Stream<Long>> groupIdSupplier, PassphraseHash passphraseHash, String revokeCommentfinal, PostOpActions postRevokeActions);

	/**
	 * After calling this method, you MUST invoke deleteArtifacts to actually perform the delete of the revoked items.  Making deleteArtifacts a separate call
	 * allows us to get the list of revoked items once we are done.
	 */
	Void						revokeItemsSharedWithUserViaGroup(Long userId, Long groupId);
	Integer						deleteAllUserItems	(User caller);
	
	/**
	 * Grant specified permission to the collection of users.
	 * @param caller
	 * @param itemAssignmentId
	 * @param permission the type of permission to grant to each user
	 * @param recipients a collection of users to grant the specified permission to
	 * @param passphraseHash due to the nature of the call, we require a passphrase to ensure the identity of the caller 
	 * @return
	 */
	Void						grantUsersPermission(User caller, Long itemAssignmentId, PermissionDTO permission, Collection<Long> recipients, PassphraseHash passphraseHash);

	/**
	 * Grant specified permission to the collection of groups.
	 * @param caller
	 * @param itemAssignmentId
	 * @param permission the type of permission to grant to each group
	 * @param grups a collection of groups to grant the specified permission to
	 * @param passphraseHash due to the nature of the call, we require a passphrase to ensure the identity of the caller 
	 * @return
	 */
	Void						grantGroupsPermission(User caller, Long itemAssignmentId, PermissionDTO permission, Collection<Long> groups, PassphraseHash passphraseHash);

	Long						getItemId			(User caller, Long itemAssignmentId);
	Void						takeOwnership		(User caller, Long itemAssignmentId, Long containerAssignmentId, PassphraseHash passphraseHash, PostOpActions postOpActions);
	void						takeOwnership		(User caller, Item item, ItemAssignment itemAssignment, Long containerAssignmentId, PassphraseHash passphraseHash, PostOpActions postOpActions);

	/**
	 * After invoking revoke actions, this action must be invoked to actually perform the delete of the objects and notification to users or revoke.
	 * Notifications are sent to users who no longer have access to items.
	 */
	void						deleteArtifacts		(User caller, String revokeComment);

	<T extends ItemDTO> T		get(User caller, Long itemId, PassphraseHash passphraseHash, Class<T> clazz, List<OrderBy> orderby);
	ItemDTO						get(User caller, Long itemId, PassphraseHash passphraseHash, List<OrderBy> orderby);

	/**
	 * Shared items have additional information associated with them.
	 *
	 * @param user
	 * @param itemId
	 * @param passphrase
	 * @param orderby
	 * @return
	 */
	SharedItemDTO				getSharedItem(User caller, Long itemId, PassphraseHash passphraseHash, List<OrderBy> orderby);
	SharedItemsResult			getItemsSharedWithUser(User caller, Long containerAssignmentId, String searchString, Collection<Long> preSelectedItems, Integer first, Integer count, List<OrderBy> orderBy);
	SharedNotesResult			getNotesSharedWithUser(User caller, String searchString, Collection<Long> preSelectedItems, Integer first, Integer count, List<OrderBy> orderBy);
	ItemGroupsResult			getGroupsSharingItem(User caller, Long itemAssignmentId, String nameSearch, Collection<Long> preSelectedGroups, Integer first, Integer count, List<OrderBy> orderBy);

	/**
	 * Notebook values are a simple array of bytes. We need to convert these to the UserDefinedTypeValueDTO type before we can work on them as an Item.
	 * @param userId
	 * @param itemId
	 * @param bytes
	 * @return
	 */
	List<UserDefinedTypeValueDTO> convertToItemValues(Long userId, Long itemId, byte[] bytes);

	void setRunningInUnitTest();

	// A relinquish call that makes no validation checks, generates no notifications, creates no audit records.
	// Designed for cleanup code.
	void relinquishItem(User caller, Long itemAssignmentId, Long itemId);
}
