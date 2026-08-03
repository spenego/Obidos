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

package com.spenego.Obidos.server.actions.impl;

import static com.spenego.Obidos.server.model.Audit.ADDED_USER_TO_GROUP;
import static com.spenego.Obidos.server.model.Audit.CREATE_GROUP;
import static com.spenego.Obidos.server.model.Audit.DELETE_GROUP;
import static com.spenego.Obidos.server.model.Audit.DELETE_USER;
import static com.spenego.Obidos.server.model.Audit.REMOVED_USER_FROM_GROUP;
import static com.spenego.Obidos.server.utils.NotificationEngine.NotificationType.REVOKE_CONTAINER;
import static com.spenego.Obidos.server.utils.NotificationEngine.NotificationType.REVOKE_ITEM;
import static com.spenego.Obidos.server.utils.NotificationEngine.NotificationType.SHARE_CONTAINER;
import static com.spenego.Obidos.server.utils.NotificationEngine.NotificationType.SHARE_ITEM;
import static com.spenego.Obidos.shared.SharedSetQuality.NOT_SHARED_WITH;
import static com.spenego.Obidos.shared.SharedSetQuality.ONLY_SHARED_WITH;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.ContainerActions;
import com.spenego.Obidos.server.actions.ItemActions;
import com.spenego.Obidos.server.actions.UserActions;
import com.spenego.Obidos.server.actions.UserDefinedTypeValueActions;
import com.spenego.Obidos.server.model.BaseModel;
import com.spenego.Obidos.server.model.ContainerAssignment;
import com.spenego.Obidos.server.model.ContainerGroupAssignment;
import com.spenego.Obidos.server.model.ContainerTarget;
import com.spenego.Obidos.server.model.Group;
import com.spenego.Obidos.server.model.GroupMember;
import com.spenego.Obidos.server.model.ItemAssignment;
import com.spenego.Obidos.server.model.LimitedItem;
import com.spenego.Obidos.server.model.LimitedUser;
import com.spenego.Obidos.server.model.SharedItem;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.ContainerGroupAssignmentOperations;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.security.Encryption.PublicKeyDecryptor;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.utils.CountryCodeUtil;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.NotificationEngine;
import com.spenego.Obidos.server.utils.NotificationEngine.NotificationType;
import com.spenego.Obidos.server.utils.StreamSupplier;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.SharedSetQuality;
import com.spenego.Obidos.shared.dto.CountryCodeDTO;
import com.spenego.Obidos.shared.dto.GroupDTO;
import com.spenego.Obidos.shared.dto.GroupResult;
import com.spenego.Obidos.shared.dto.LimitedUserResult;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeValueDTO;
import com.spenego.Obidos.shared.dto.UserGroupComboResult;
import com.spenego.Obidos.shared.exceptions.DuplicateRecordException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class UserActionsImpl extends CryptoActions<User> implements UserActions {
	private static final Logger logger = LoggerFactory.getLogger(UserActionsImpl.class);

	@Autowired private final ContainerActions						containerActions = null;
	@Autowired private final ContainerGroupAssignmentOperations		containerGroupAssignmentOperations = null;
	@Autowired private final ItemActions							itemActions = null;
	@Autowired private final UserDefinedTypeValueActions			userDefinedTypeValueActions = null;
	@Autowired private final ObjectFactory<NotificationEngine<ContainerAssignment>> containerNotificationEngineFactory = null;

	public UserActionsImpl() {
		super(null, null);
	}

	@Override
	protected final Logger getLogger() {
		return logger;
	}

	@Override
	protected final Operations<User> getOperations() {
		return userOperations;
	}

	@Override
	protected final Integer getAuditDeleteAction() {
		return DELETE_USER;
	}

	@Override
	protected final String elementName() {
		return "user";
	}

	private User convertUser(final UserDTO user) {
		return convert(user, User.class);
	}

	@Override
	public Long createGroup(final User caller, final String name, final String comment, final PassphraseHash passphraseHash) {
		validatePassphraseHash(caller, passphraseHash);

		try {
			final Long gid = groupOperations.create(new Group(caller.getId(), name, comment));
			audit(CREATE_GROUP, caller.getUsername(), caller.getId(), name, gid);

			return gid;
		} catch(final DuplicateRecordException ex) {
			throw new DuplicateRecordException("A group with the name " + name + " already exists.", ex);
		}
	}

	@Override
	public GroupResult getGroups(final User caller, final String search, final Collection<Long> preSelectedGroups, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return new GroupResult(first, count, null, (a,b) -> convert(a,b),
				() -> groupOperations.getCount(caller.getId(), search, preSelectedGroups),
				() -> groupOperations.getList(caller.getId(), search, preSelectedGroups, first, count, orderBy));
	}

	private Long getContainerId(final User caller, final Long containerAssignmentId) {
		if (containerAssignmentId == null) {
			throw new ServerSideException("You must specify a container.");
		}

		final ContainerAssignment ca = getContainerAssignment(containerAssignmentId);

		if (!caller.self(ca.getUserId())) {
			throw new PermissionDeniedException("You do not own that container.");
		}

		if (!caller.self(getContainer(ca.getContainerId()).getUserId())) {
			throw new PermissionDeniedException("You do not own that container.");
		}

		return ca.getContainerId();
	}

	private static <T extends ContainerTarget> T mark(final T g, final Set<Long> set) {
		g.setInContainer(set.contains(g.getId()));
		return g;
	}

	private static <T extends ContainerTarget> Stream<T> markInContainer(final Supplier<Stream<T>> streamSupplier, final Supplier<Stream<Long>> idSupplier) {
		final Set<Long> groupSet = idSupplier.get().collect(toSet());
		return streamSupplier.get().map(g -> mark(g, groupSet));
	}

	private static <T extends ContainerTarget,X> Stream<T> getElementStream(final Long containerId, final Boolean onlyNewEntries, final Supplier<Stream<T>> supplier, final Supplier<Stream<X>> assignments, final ToLongFunction<X> f) {
		return (onlyNewEntries || containerId == null) ? supplier.get() : markInContainer(supplier, () -> assignments.get().map(f::applyAsLong));
	}

	private Stream<Group> getGroupsForContainer(final Long userId, final Long containerId, final Boolean onlyNewGroups, final String search, final Collection<Long> preSelectedGroups, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return getElementStream(containerId, onlyNewGroups, () -> groupOperations.getGroupsNotInContainer(userId, isTrue(onlyNewGroups) ? containerId : null, search, preSelectedGroups, first, count, orderBy),
															() -> containerGroupAssignmentOperations.getAll(containerId), ContainerGroupAssignment::getGroupId);
	}

	private Stream<LimitedUser> getUsersForContainer(final Long userId, final Long containerId, final Boolean onlyNewUsers, final User userPatterns, final Collection<Long> preSelectedUsers, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return getElementStream(containerId, onlyNewUsers, () -> userOperations.getUsersNotSharingContainer(userId, isTrue(onlyNewUsers) ? containerId : null, userPatterns, preSelectedUsers, first, count, orderBy),
															() -> containerAssignmentOperations.getAll(containerId), BaseModel::getUserId);
	}

	private static Long useId(final SharedSetQuality sharedSetQuality, final Long id) {
		return sharedSetQuality == NOT_SHARED_WITH ? id : null;
	}

	private static void logDetails(final String type, final String msg, final UserDTO userTemplate, final Long id, final SharedSetQuality sharedSetQuality, final Collection<Long> preSelected) {
		if (logger.isInfoEnabled()) {
			logger.info(() -> "Getting " + msg + " " + id);
			logger.info(() -> "Shared Set Quality = " + sharedSetQuality);
			logger.info(() -> "Pre-Selected " + type + " count = " + (preSelected == null ? "null" : Integer.toString(preSelected.size())));
			if (userTemplate != null) {
				logger.info(() -> "User template fullname = " + userTemplate.getFullname());
				logger.info(() -> "User template username = " + userTemplate.getUsername());
			}
		}
	}

	private static void checkOrderBy(final List<OrderBy> orderBy) {
		if (orderBy != null) {
			for(final OrderBy o : orderBy) {
				if (OrderBy.USERNAME_ASC.equals(o) || OrderBy.USERNAME_DESC.equals(o)) {
					throw new ServerSideException("You may not order by username.");
				}
			}
		}
	}

	@Override
	public GroupResult getGroupsForContainer(final User requestor, final Long containerAssignmentId, final String searchString, final SharedSetQuality sharedSetQuality, final Collection<Long> preSelectedGroups, final Integer offset, final Integer count, final List<OrderBy> orderBy) {
		checkOrderBy(orderBy);
		final Long containerId = containerAssignmentId == null ? null : getContainerId(requestor, containerAssignmentId);
		final Long userId = requestor.getId();

		logDetails("Group", "groups for container", null, containerId, sharedSetQuality, preSelectedGroups);

		return new GroupResult(offset, count, preSelectedGroups, (a,b) -> convert(a,b),
				sharedSetQuality == ONLY_SHARED_WITH ?
					() -> groupOperations.getGroupsSharingContainerCount(containerId, searchString, preSelectedGroups) :
					() -> groupOperations.getGroupsNotInContainerCount(userId, useId(sharedSetQuality, containerId), searchString, preSelectedGroups),
				sharedSetQuality == ONLY_SHARED_WITH ?
					() -> groupOperations.getGroupsSharingContainer(containerId, searchString, preSelectedGroups, offset, count, orderBy) :
					() -> getGroupsForContainer(userId, containerId, sharedSetQuality == NOT_SHARED_WITH, searchString, preSelectedGroups, offset, count, orderBy));
	}

	@Override
	public LimitedUserResult getUsersForContainer(final User requestor, final Long containerAssignmentId, final UserDTO userPatterns, final SharedSetQuality sharedSetQuality, final Collection<Long> preSelectedUsers, final Integer offset, final Integer count, final List<OrderBy> orderBy) {
		checkOrderBy(orderBy);
		final Long containerId = containerAssignmentId == null ? null : getContainerId(requestor, containerAssignmentId);
		final User u = (userPatterns == null) ? null : convertUser(userPatterns);
		final Long userId = requestor.getId();

		logDetails("User", "users for container", userPatterns, containerId, sharedSetQuality, preSelectedUsers);

		return new LimitedUserResult(offset, count, preSelectedUsers, (a,b) -> convert(a,b),
				sharedSetQuality == ONLY_SHARED_WITH ?
					() -> userOperations.getUsersSharingContainerCount(userId, containerId, u, preSelectedUsers) :
					() -> userOperations.getUsersNotSharingContainerCount(userId, useId(sharedSetQuality, containerId), u, preSelectedUsers),
				sharedSetQuality == ONLY_SHARED_WITH ?
					() -> userOperations.getUsersSharingContainer(userId, containerId, u, preSelectedUsers, offset, count, orderBy) :
					() -> getUsersForContainer(userId, containerId, sharedSetQuality == NOT_SHARED_WITH, u, preSelectedUsers, offset, count, orderBy));
	}

	@Override
	public LimitedUserResult getUsersForItem(final User requestor, final Long itemAssignmentId, final UserDTO userPatterns, final SharedSetQuality sharedSetQuality, final Collection<Long> preSelectedUsers, final Integer offset, final Integer count, final List<OrderBy> orderBy) {
		checkOrderBy(orderBy);
		if (isFalse(getItemAssignment(itemAssignmentId).getUpdatePermitted())) {
			throw new PermissionDeniedException("In order to view the users sharing this item, you must have update permission.");
		}

		final Long itemId = getItemId(requestor, itemAssignmentId);
		final Long userId = requestor.getId();
		final User u = (userPatterns == null) ? null : convertUser(userPatterns);

		logDetails("User", "users for item", userPatterns, itemId, sharedSetQuality, preSelectedUsers);

		return new LimitedUserResult(offset, count, preSelectedUsers, (a,b) -> convert(a,b),
				sharedSetQuality == ONLY_SHARED_WITH ?
					() -> userOperations.getRecipientsOfSharedItemCount(userId, itemId, u, preSelectedUsers) :
					() -> userOperations.getUsersNotSharingItemCount(userId, useId(sharedSetQuality, itemId), u, preSelectedUsers),
				sharedSetQuality == ONLY_SHARED_WITH ?
					() -> userOperations.getUsersSharingItem(userId, itemId, u, preSelectedUsers, offset, count, orderBy) :
					() -> userOperations.getUsersNotSharingItem(userId, useId(sharedSetQuality, itemId), u, preSelectedUsers, offset, count, orderBy));
	}

	@Override
	public GroupResult getGroupsForItem(final User requestor, final Long itemAssignmentId, final String searchString, final SharedSetQuality sharedSetQuality, final Collection<Long> preSelectedGroups, final Integer offset, final Integer count, final List<OrderBy> orderBy) {
		checkOrderBy(orderBy);
		final Long itemId = getItemId(requestor, itemAssignmentId);

		logDetails("Group", "groups for item", null, itemId, sharedSetQuality, preSelectedGroups);

		return new GroupResult(offset, count, preSelectedGroups, (a,b) -> convert(a,b),
				sharedSetQuality == ONLY_SHARED_WITH ?
					() -> groupOperations.getGroupsSharingItemCount(itemId, searchString, preSelectedGroups, Boolean.TRUE) :
					() -> groupOperations.getGroupsNotSharingItemCount(requestor, useId(sharedSetQuality, itemId), searchString, preSelectedGroups),
				sharedSetQuality == ONLY_SHARED_WITH ?
					() -> groupOperations.getGroupsSharingItem(itemId, searchString, preSelectedGroups, Boolean.TRUE, offset, count, orderBy) :
					() -> groupOperations.getGroupsNotSharingItem(requestor, useId(sharedSetQuality, itemId), searchString, preSelectedGroups, offset, count, orderBy));
	}

	@Override
	public UserGroupComboResult getSharesForContainer(final User requestor, final Long containerAssignmentId, final String searchString, final Collection<Long> preSelectedElements, final Integer offset, final Integer count, final List<OrderBy> orderBy) {
		checkOrderBy(orderBy);
		final Long containerId = getContainerId(requestor, containerAssignmentId);

		logDetails("Users and Groups", "users and groups for container", null, containerId, null, preSelectedElements);

		return new UserGroupComboResult(offset, count, preSelectedElements, (a,b) -> convert(a,b),
				() -> containerAssignmentOperations.getContainerAssignmentShareCount(requestor.getId(), containerId, searchString, preSelectedElements),
				() -> containerAssignmentOperations.getContainerAssignmentShares(requestor.getId(), containerId, searchString, preSelectedElements, offset, count, orderBy));
	}

	@Override
	public UserGroupComboResult getSharesForItem(final User requestor, final Long itemAssignmentId, final String searchString, final Collection<Long> preSelectedElements, final Integer offset, final Integer count, final List<OrderBy> orderBy) {
		checkOrderBy(orderBy);
		final Long itemId = getItemId(requestor, itemAssignmentId);

		logDetails("Users and Groups", "users and groups for item", null, itemId, null, preSelectedElements);

		return new UserGroupComboResult(offset, count, preSelectedElements, (a,b) -> convert(a,b),
				() -> itemAssignmentOperations.getItemShareCount(requestor.getId(), itemId, searchString, preSelectedElements),
				() -> itemAssignmentOperations.getItemShares(requestor.getId(), itemId, searchString, preSelectedElements, offset, count, orderBy));
	}

	/**
	 * A PermissionDeniedException is thrown if the user does not have authority
	 * to perform actions on the specified group.
	 *
	 * @param user
	 * @param groupId
	 */
	private Group checkUserGroupPermission(final User requestor, final Long groupId) {
		final Group g = getGroup(groupId);

		if (!requestor.self(g.getUserId())) {
			throw new PermissionDeniedException("You do not own this group.");
		}

		return g;
	}

	@Override
	public LimitedUserResult getUsersForGroup(final User requestor, final Long groupId, final UserDTO userPatterns, final SharedSetQuality sharedSetQuality, final Collection<Long> preSelectedUsers, final Integer offset, final Integer count, final List<OrderBy> orderBy) {
		checkOrderBy(orderBy);
		final User u = (userPatterns == null) ? null : convertUser(userPatterns);

		logDetails("User", "users for group", userPatterns, groupId, sharedSetQuality, preSelectedUsers);

		checkUserGroupPermission(requestor, groupId);

		return new LimitedUserResult(offset, count, preSelectedUsers, (a,b) -> convert(a,b),
					sharedSetQuality == ONLY_SHARED_WITH ?
					() -> groupMemberOperations.getUsersInGroupCount(groupId, u, preSelectedUsers) :
					() -> userOperations.getUsersNotInGroupCount(requestor, useId(sharedSetQuality, groupId), u, preSelectedUsers),
					sharedSetQuality == ONLY_SHARED_WITH ?
					() -> groupMemberOperations.getUsersInGroup(groupId, u, preSelectedUsers, offset, count, orderBy) :
					() -> userOperations.getUsersNotInGroup(requestor, useId(sharedSetQuality, groupId), u, preSelectedUsers, offset, count, orderBy));
	}

	@Override
	public GroupDTO getGroupDTO(final Long id) {
		return convert(getGroup(id), GroupDTO.class);
	}

	private Void shareItemWithUser(final LimitedItem item, final Long requestorId, final Long recipientId, final PostOpActions postOpAction, final Supplier<Stream<UserDefinedTypeValueDTO>> supplier, final Consumer<LimitedUser> consumer, final Boolean sharedExplicitly) {
		return itemActions.shareItemWithUser(item, requestorId, recipientId, postOpAction, supplier, consumer, sharedExplicitly);
	}

	private Void shareItemWithUsers(final User requestor, final SharedItem item, final PostOpActions postOpAction, final Supplier<Stream<Long>> userIdSupplier, final Supplier<PublicKeyDecryptor> pkdSupplier, final Consumer<LimitedUser> consumer, final Boolean sharedExplicitly) {
		final Supplier<Stream<UserDefinedTypeValueDTO>> valueSupplier = StreamSupplier.create(() -> userDefinedTypeValueActions.getList(requestor, item.getItemId(), pkdSupplier, null));
		return processStream(() -> requestor + " is sharing item with users", userIdSupplier, userId -> shareItemWithUser(item, requestor.getId(), userId, postOpAction, valueSupplier, consumer, sharedExplicitly));
	}

	private Collection<Long> containersSharedWithGroup(final Long groupId) {
		return containerGroupAssignmentOperations.getContainers(groupId).map(ContainerGroupAssignment::getContainerId).collect(toSet());
	}

	private void addUserToGroup(final User requestor, final Long groupId, final Long recipientId) {
		groupMemberOperations.addUserToGroup(new GroupMember(groupId, recipientId));
		auditGroupAction(ADDED_USER_TO_GROUP, requestor.getUsername(), requestor.getId(), groupId, recipientId, getGroup(groupId));
	}

	private Group prepareForGroupUpdate(final User requestor, final Long groupId) {
		return checkUserGroupPermission(requestor, groupId);
	}

	private void identifyUsersWithInsufficientClearance(final Collection<Long> userIds, final int minimumSecurityClearance) {
		for(final Long userId : userIds) {
			final User u = getUser(userId);
			if (minimumSecurityClearance > u.getSecurityClearance()) {
				throw new ServerSideException("User " + u.getFullname() + " does not meet the security clearance for this group.");
			}
		}
	}

	protected NotificationEngine<ItemAssignment> getItemNotificationEngine(final NotificationType type, final Long groupId) {
		return getNotificationEngine(type, itemNotificationEngineFactory, getItemAssignmentSupplierViaGroupId(groupId));
	}

	protected NotificationEngine<ContainerAssignment> getContainerNotificationEngine(final NotificationType type, final Long groupId) {
		return getNotificationEngine(type, containerNotificationEngineFactory, () -> groupId != null ? containerAssignmentOperations.getAssignmentsViaGroupId(groupId) : null);
	}

	private enum NotificationAction {SHARE, REVOKE}

	/**
	 * Recipients may be notified of shared items when shared directly or via group. We want to ensure they only get one notification per share action.
	 *
	 */
	private class NotificationAggregator {
		final NotificationEngine<ItemAssignment>		itemNotificationEngine;
		final NotificationEngine<ContainerAssignment>	containerNotificationEngine;

		public NotificationAggregator(final NotificationAction type, final Long groupId) {
			itemNotificationEngine		= getItemNotificationEngine(type == NotificationAction.SHARE ? SHARE_ITEM : REVOKE_ITEM, groupId);
			containerNotificationEngine	= getContainerNotificationEngine(type == NotificationAction.SHARE ? SHARE_CONTAINER : REVOKE_CONTAINER, groupId);
		}

		public Void notifyUsers(final User caller, final String comment) {
			itemNotificationEngine.notifyUsers(caller, comment);
			containerNotificationEngine.notifyUsers(caller, comment);
			return null;
		}
	}

	@Override
	public Void addUsersToGroup(final User requestor, final Collection<Long> userIds, final Long groupId, final PassphraseHash passphraseHash, final PostOpActions postOpAction, final String shareComment) {
		final Group group = prepareForGroupUpdate(requestor, groupId);

		if (userOperations.countUsersWithInsufficientClearance(userIds, group.getMinimumSecurityClearance()) > 0) {
			identifyUsersWithInsufficientClearance(userIds, group.getMinimumSecurityClearance());
		}

		final Supplier<PublicKeyDecryptor> pkdSupplier	= getPKDSupplier(requestor, passphraseHash);
		final Collection<Long> containers				= containersSharedWithGroup(groupId);
		final Supplier<Stream<Long>> userIdsSupplier	= StreamSupplier.create(userIds);
		final NotificationAggregator notifier			= new NotificationAggregator(NotificationAction.SHARE, groupId);
		/**
		 * Notify users when they have access to NEW items. If the user was added to a group and they already had access to the item(s) in that group, do not send them any notifications.
		 * Likewise, if a user now has access to multiple items, they only get one notification that 'multiple items' have been shared.
		 */
		processStream(() -> requestor + " is adding users to group " + groupId, userIds::stream, userId -> addUserToGroup(requestor, groupId, userId), () -> "adding user to group");
		processStream(() -> requestor + " is sharing items with group " + groupId, () -> itemOperations.getItemsSharedWithGroup(requestor.getId(), groupId), item -> shareItemWithUsers(requestor, item, postOpAction, userIdsSupplier, pkdSupplier, null, Boolean.FALSE));
		processStream(() -> requestor + " is sharing containers with group " + groupId, containers::stream, containerId -> containerActions.shareContainerWithUsersWithoutNotify(requestor, containerId, getContainerAssignmentId(containerId, requestor.getId()), StreamSupplier.create(userIds), pkdSupplier, null, Boolean.FALSE), () -> "adding users to group");
		return notifier.notifyUsers(requestor, shareComment);
	}

	@Override
	public Void updateGroup(final User requestor, final GroupDTO group) {
		prepareForGroupUpdate(requestor, group.getId());
		return groupOperations.updateSelective(convert(group, Group.class));
	}

	/**
	 * We revoke access to the item (reduce reference count and delete when zero).  We also revoke container assignments via group.
	 */
	private void removeUserFromGroup(final User requestor, final Long userId, final Long groupId) {
		itemActions.revokeItemsSharedWithUserViaGroup(userId, groupId);
	 	// Since the user may have been granted access to the container via shareContainerWithUser, we can't simply delete
		// the container assignment. We decrement the reference count and then clean up any that have gone to zero.
		containerAssignmentOperations.decrementShareCountsForUserInGroup(userId, groupId);
		groupMemberOperations.delete(userId, groupId);
		auditGroupAction(REMOVED_USER_FROM_GROUP, requestor.getUsername(), requestor.getId(), groupId, userId, getGroup(groupId));
	}

	private Void removeUsersFromGroup(final User requestor, final Collection<Long> userIds, final Long groupId) {
		processStream(() -> requestor + " is removing users from group " + groupId, userIds::stream, userId -> removeUserFromGroup(requestor, userId, groupId), () -> "removing users from group");
		containerActions.deleteArtifacts(requestor, null, null);
		itemActions.deleteArtifacts(requestor, null);
		return null;
	}

	@Override
	public Void removeUsersFromGroup(final User requestor, final Collection<Long> userIds, final Long groupId, final PassphraseHash passphraseHash) {
		prepareForGroupUpdate(requestor, groupId);
		validatePassphraseHash(requestor, passphraseHash);
		return removeUsersFromGroup(requestor, userIds, groupId);
	}

	private Void deleteGroup(final User caller, final Long groupId) {
		logger.info(() -> "Deleting group " + groupId);
		checkUserGroupPermission(caller, groupId);
		audit(DELETE_GROUP, caller.getUsername(), caller.getId(), getGroup(groupId), null, null);
		// we can't simply delete the group because we need to detect what users need to be notified (removing does that notification)
		removeUsersFromGroup(caller, groupMemberOperations.getUserIdsInGroup(groupId), groupId);
		return groupOperations.delete(groupId);	// will cause a cascade of deletes. However, we still need to remove container assignments manually.
	}

	@Override
	public Void deleteGroups(final User caller, final Collection<Long> groupIds, final PassphraseHash passphraseHash) {
		validatePassphraseHash(caller, passphraseHash); // Why? It ensures the authorized user is deleting group.
		return processStream(() -> "deleting groups", groupIds::stream, groupId -> deleteGroup(caller, groupId), () -> "deleting group");
	}

	@Override
	public List<String> getCountryCodes()
	{
		return CountryCodeUtil.getCountryCode();
	}

	@Override
	public Map<String, CountryCodeDTO> getCountryCodesMap()
	{
		return CountryCodeUtil.getCountryCodesMap();
	}
}
