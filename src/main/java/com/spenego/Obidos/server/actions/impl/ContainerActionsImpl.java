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

import static com.spenego.Obidos.server.model.Audit.CREATE_CONTAINER;
import static com.spenego.Obidos.server.model.Audit.DELETE_CONTAINER;
import static com.spenego.Obidos.server.model.Audit.REVOKE_CONTAINER_FROM_GROUP;
import static com.spenego.Obidos.server.model.Audit.REVOKE_CONTAINER_FROM_USER;
import static com.spenego.Obidos.server.model.Audit.SHARE_CONTAINER_WITH_GROUP;
import static com.spenego.Obidos.server.model.Audit.SHARE_CONTAINER_WITH_USER;
import static com.spenego.Obidos.server.model.Audit.SUBVERSIVE_CONTAINER_VIEW;
import static com.spenego.Obidos.server.model.Audit.UPDATE_CONTAINER;
import static com.spenego.Obidos.server.utils.NotificationEngine.NotificationType.REVOKE_CONTAINER;
import static com.spenego.Obidos.server.utils.NotificationEngine.NotificationType.SHARE_CONTAINER;
import static com.spenego.Obidos.shared.dto.NotificationDTO.CONTAINER_DELETED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.CONTAINER_OWNED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.CONTAINER_RELINQUISHED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.CONTAINER_SHARED;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import com.spenego.Obidos.server.actions.ContainerActions;
import com.spenego.Obidos.server.actions.EmailActions;
import com.spenego.Obidos.server.actions.ItemActions;
import com.spenego.Obidos.server.actions.LoginActions;
import com.spenego.Obidos.server.actions.SMSActions;
import com.spenego.Obidos.server.actions.UserDefinedTypeValueActions;
import com.spenego.Obidos.server.model.Audit;
import com.spenego.Obidos.server.model.Container;
import com.spenego.Obidos.server.model.ContainerAssignment;
import com.spenego.Obidos.server.model.ContainerGroupAssignment;
import com.spenego.Obidos.server.model.Group;
import com.spenego.Obidos.server.model.Item;
import com.spenego.Obidos.server.model.LimitedItem;
import com.spenego.Obidos.server.model.LimitedUser;
import com.spenego.Obidos.server.model.SharedItem;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.ContainerGroupAssignmentOperations;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.operations.UserDefinedTypeValueOperations;
import com.spenego.Obidos.server.security.Encryption.PublicKeyDecryptor;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.NotificationEngine;
import com.spenego.Obidos.server.utils.NotificationEngine.NotificationType;
import com.spenego.Obidos.server.utils.ObidosExecutor;
import com.spenego.Obidos.server.utils.StreamSupplier;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.ContainerResult;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.dto.SharedContainerResult;
import com.spenego.Obidos.shared.dto.UserDefinedTypeValueDTO;
import com.spenego.Obidos.shared.exceptions.DuplicateRecordException;
import com.spenego.Obidos.shared.exceptions.ItemOwnershipException;
import com.spenego.Obidos.shared.exceptions.LicenseKeyException;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;
import com.spenego.Obidos.shared.exceptions.SharingProhibitedException;

public final class ContainerActionsImpl extends CryptoActions<Container> implements ContainerActions {
	private static final Logger logger = LoggerFactory.getLogger(ContainerActionsImpl.class);
	private static final String DEFAULT_PRIVATE_CONTAINER_NAME = "Private";
	private static final String DEFAULT_PUBLIC_CONTAINER_NAME  = "Public";
	private static final String CONTAINER  = "container";

	@Autowired private final SMSActions								smsActions = null;
	@Autowired private final ContainerGroupAssignmentOperations		containerGroupAssignmentOperations = null;
	@Autowired private final EmailActions							emailActions = null;
	@Autowired private final ItemActions							itemActions = null;
	@Autowired private final UserDefinedTypeValueActions			userDefinedTypeValueActions = null;
	@Autowired private final UserDefinedTypeValueOperations			userDefinedTypeValueOperations = null;
	@Autowired private final ObjectFactory<NotificationEngine<ContainerAssignment>> containerNotificationEngineFactory = null;
	@Autowired private final LoginActions							loginActions = null;

	public ContainerActionsImpl() {
		super(null, null);
	}

	@Override
	protected final String elementName() {
		return CONTAINER;
	}

	@Override
	protected final Logger getLogger() { return logger; }

	@Override
	protected final Operations<Container> getOperations() {
		return containerOperations;
	}

	@Override
	protected final Integer getAuditDeleteAction() {
		return DELETE_CONTAINER;
	}

	private ContainerGroupAssignment getContainerGroupAssignment(final Long containerId, final Long groupId) {
		return containerGroupAssignmentOperations.get(containerId, groupId);
	}

	private Long getContainerId(final Long containerAssignmentId) {
		if (containerAssignmentId == null) { throw new ServerSideException("You must supply a container which you wish to share."); }

		return getContainerAssignment(containerAssignmentId).getContainerId();
	}

	@Override
	public Long createContainerAssignment(final Long recipientId, final Long containerId, final Collection<Long> recipients, final Boolean sharedExplicitly, final Boolean ownershipControl, final Boolean addPermitted, final Boolean updatePermitted) {
		logger.info(() -> "in createContainerAssignment, recipientId = " + recipientId + ", containerId = " + containerId + ", shared explicitly = " + sharedExplicitly);

		try {
			final Long caId = getContainerAssignmentId(containerId, recipientId);
			containerAssignmentOperations.incrementShareCount(caId);

			// This is invoked when a container is shared implicitly (via a group), and now the user is sharing
			// the container explicitly with the recipient.  We need to set the sharedExplicitly flag.  Bug #750
			if (sharedExplicitly != null && sharedExplicitly) {
				containerAssignmentOperations.updateSelective(new ContainerAssignment(caId, Boolean.TRUE, ownershipControl));
			}
			return caId;
		} catch(final NoSuchRecordException ex) {
			try {
				logger.info(() -> "in createContainerAssignment, creating new Container Assignment");
				if (recipients != null) {
					recipients.add(recipientId);	// add to the recipients list only if the recipient has not already had this container shared with them
				}
				return containerAssignmentOperations.create(new ContainerAssignment(recipientId, containerId, sharedExplicitly, ownershipControl, addPermitted, updatePermitted));
			} catch(final DuplicateKeyException ex2) {
				return createContainerAssignment(recipientId, containerId, recipients, sharedExplicitly, ownershipControl, addPermitted, updatePermitted); // some other process created the mapping, retry
			}
		}
	}

	@Override
	public Long getContainerAssignmentId(final User caller, final Long containerId) {
		return getContainerAssignmentId(containerId, caller.getId());
	}

	private Long getContainerGroupAssignmentId(final Long containerId, final Long groupId) {
		return getContainerGroupAssignment(containerId, groupId).getId();
	}

	private Long createContainerInternal(final User caller, final String containerName, final Boolean isPrivate) {
		try {
			final Container c = new Container(caller.getId(), containerName, isPrivate);
			final Long containerId = containerOperations.create(c);
			auditContainer(CREATE_CONTAINER, caller, c, containerId, containerName);
			return createContainerAssignment(caller.getId(), containerId, null, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
		} catch (final DuplicateRecordException ex2) {
			logger.error(() -> "Caught " + ex2);
			throw new DuplicateRecordException("A container named " + containerName + " already exists." + ex2);
		}
	}

	@Override
	public Long createContainer(final User caller, final String containerName, final Boolean isPrivate, final PassphraseHash passphraseHash) {
		validatePassphraseHash(caller, passphraseHash); // ensures user has active public key as an additional level of authentication
		return createContainerInternal(caller, containerName, isPrivate);
	}

	@Override
	public Long createDefaultPrivateContainer(final User caller) {
		return createContainerInternal(caller, DEFAULT_PRIVATE_CONTAINER_NAME, true);
	}

	@Override
	public Long createDefaultPublicContainer(final User caller) {
		return createContainerInternal(caller, DEFAULT_PUBLIC_CONTAINER_NAME, false);
	}

	@Override
	public ContainerDTO get(final User caller, final Long containerAssignmentId) {
		final ContainerAssignment ca = getContainerAssignment(containerAssignmentId);

		ensureCallerOwnsAssignment(ca, caller, containerAssignmentId, SUBVERSIVE_CONTAINER_VIEW, "view container");

		final ContainerDTO c = convert(getModel(ca.getContainerId()), ContainerDTO.class);
		c.setId(containerAssignmentId);

		return c;
	}

	@Override
	public Boolean containerIsPrivate(final Long containerAssignmentId) {
		return getModel(getContainerAssignment(containerAssignmentId).getContainerId()).getIsPrivate();
	}

	private Container checkContainerPermissions(final User caller, final Long containerId) {
		final Container container = getModel(containerId);

		if (caller.isAdmin()) {
			throw new PermissionDeniedException("Admins may not modify user data.");
		} else if (!container.ownerIs(caller)) {
			throw new PermissionDeniedException("You do not own this container.");
		}

		return container;
	}

	private void updateSelective(final Container c) {
		containerOperations.updateSelective(c);
	}

	private ContainerAssignment secureGetContainerAssignment(final User caller, final Long containerAssignmentId, final PassphraseHash passphraseHash) {
		containerNotNull(containerAssignmentId);
		validatePassphraseHash(caller, passphraseHash);
		final ContainerAssignment ca = getContainerAssignment(containerAssignmentId);
		if (!ca.ownerIs(caller.getId())) {
			throw new PermissionDeniedException("This is not your container assignment.");
		}

		return ca;
	}

	@Override
	public Void update(final User caller, final ContainerDTO container, final PassphraseHash passphraseHash) {
		// The container ID passed in via container is actually a container-assignment ID, not a container ID.
		final ContainerAssignment ca = secureGetContainerAssignment(caller, container.getId(), passphraseHash); // container id is container Assignment ID
		if (isFalse(ca.getUpdatePermitted())) { throw new PermissionDeniedException("You were not granted update permission on this container."); }

		final Container c = checkContainerPermissions(caller, ca.getContainerId());
		if ((container.getName() != null && !c.getName().equals(container.getName()))) {
			updateSelective(new Container(c.getId(), container.getName()));
			auditContainer(UPDATE_CONTAINER, caller, c, null, container.getName());
		}
		return null;
	}

	@Override
	public ContainerResult getMyContainers(final User caller, final String search, final Collection<Long> preSelectedContainers, final Boolean shared, final Boolean shareable, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		final String searchStr = (search == null) ? "!Notebook" : search;

		return new ContainerResult(first, count, preSelectedContainers, (a,b) -> convert(a,b),
				() -> containerOperations.getUserContainerCount(caller.getId(), searchStr, preSelectedContainers, shared, shareable),
				() -> containerOperations.getUserContainers(caller.getId(), searchStr, preSelectedContainers, shared, shareable, first, count, orderBy));
	}

	@Override
	public SharedContainerResult getContainersSharedWithMe(final User caller, final String search, final Collection<Long> preSelectedContainers, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		final String searchStr = (search == null) ? "!Notebook" : search;

		return new SharedContainerResult(first, count, preSelectedContainers, (a,b) -> convert(a,b),
				() -> containerOperations.getContainersSharedWithUserCount(caller.getId(), searchStr, preSelectedContainers),
				() -> containerOperations.getContainersSharedWithUser(caller.getId(), searchStr, preSelectedContainers, first, count, orderBy));
	}

	/**
	 * We remove all references to the container and any items referenced in the container.
	 */
	private void expungeReferencesToContainer(final Long assignedContainerId, final Long containerId, final Long userId) {
		containerAssignmentOperations.delete(assignedContainerId);
		itemAssignmentOperations.deleteAllItemsInContainer(containerId, userId);
		// If the schema used item_assignment_ids in the User Defined Type Value table, the delete would happen via cascade.
		userDefinedTypeValueOperations.deleteUserItemReferencesViaContainerId(containerId, userId);
	}

	private Void relinquishContainer(final User caller, final Long assignedContainerId, final Long ownerId, final Long containerId) {
		final Container c = getModel(containerId);
		relinquish(caller, ownerId, containerId, getContainerName(containerId), CONTAINER_RELINQUISHED, () -> expungeReferencesToContainer(assignedContainerId, containerId, caller.getId()));
		checkShareCount(containerId);
		auditContainer(Audit.CONTAINER_RELINQUISHED, caller, c, null, null);
		return null;
	}

	// We need to notify users, that we shared the container with, that we are now deleting it.
	private Void deleteWithNotify(final User user, final Long containerId) {
		notifyDeleteRecipients(user, getUserIdsSharingContainer(user.getId(), containerId), containerId);

		return super.delete(user, containerId);
	}

	@Override
	protected final Void delete(final User caller, final Long containerAssignmentId) {
		final Long containerId = getContainerAssignment(containerAssignmentId).getContainerId();
		final Long ownerId = getModel(containerId).getUserId();
		final boolean userOwnsContainer = caller.self(ownerId);

		return userOwnsContainer ? deleteWithNotify(caller, containerId) : relinquishContainer(caller, containerAssignmentId, ownerId, containerId);
	}

	/**
	 * Ensure that sharing of the container is permitted. We currently allow
	 * sharing with recipients that have a account in locked state. It is
	 * assumed that the state is temporary, and we will be unable to
	 * retroactively share container contents when the user is unlocked.
	 *
	 * @param caller
	 * @param container
	 * @param recipient
	 * @throws PermissionDeniedException
	 */
	private static void ensureCallerMayShareContainer(final User caller, final Container container) throws PermissionDeniedException {
		if (!container.ownerIs(caller))			{ throw new PermissionDeniedException("You do not own this container."); }
		if (isTrue(container.getIsPrivate()))	{ throw new PermissionDeniedException("You may not share private containers."); }
	}

	private static void ensureCallerMayShareContainer(final User caller, final Container container, final User recipient) throws PermissionDeniedException {
		ensureCallerMayShareContainer(caller, container);

		if (caller.self(recipient.getId())) { throw new SharingProhibitedException("Sharing a container with yourself is not permitted."); }
		if (isTrue(recipient.getDeleted())) { throw new PermissionDeniedException("The recipient, " + recipient.getUsername() + ", no longer exists."); }
	}

	private void processItemsInContainer(final User caller, final Long containerAssignmentId, final Consumer<SharedItem> consumer) {
		processStream(() -> "processing items in container for " + caller, () -> itemOperations.getShareableItemsInContainer(containerAssignmentId), consumer::accept);
	}

	private void markContainerShared(final Container container) {
		if (container.getShared() == null || !container.getShared()) {
			updateSelective(new Container(container.getId(), true));
			container.setShared(true);
		}
	}

	private static <T,R> void ensureContainerWasNotYetShared(final Function<T,R> f, final Supplier<String> nameSupplier) {
		ensureElementWasNotYetShared(f, CONTAINER, nameSupplier);
	}

	private void notifyRecipients(final User owner, final Supplier<Stream<Long>> recipientSupplier, final Long containerId, final String comment, final String message, final int action, final LongUnaryOperator containerAssignmentIdSupplier, final Consumer<User> notifier) {
		final String name = getContainerName(containerId);
		postCommitQueue(() -> processStream(() -> message, recipientSupplier, id -> notifyRecipient(action, owner, id, containerAssignmentIdSupplier == null ? null : containerAssignmentIdSupplier.applyAsLong(id), CONTAINER, name, comment, notifier)));
	}

	private void notifyShareRecipients(final User owner, final Stream<Long> recipients, final Long containerId, final String shareComment, final PostOpActions postOpActions) {
		final Map<Long,Long> map = createUserModelMap(containerAssignmentOperations.getAll(containerId).collect(toList()));

		notifyRecipients(owner, () -> recipients, containerId, shareComment, "notifying share recipients", CONTAINER_SHARED, map::get,
				recipient -> {
					if (postOpActions != null && postOpActions.sendEmail()) { emailActions.sendContainerSharedEmail(map.get(recipient.getId()), owner, recipient, shareComment); }
					if (postOpActions != null && postOpActions.sendSMS())   { smsActions.sendContainerSharedSms(map.get(recipient.getId()), owner, recipient, shareComment); }
				});
	}

	private void notifyDeleteRecipients(final User owner, final Stream<Long> recipients, final Long containerId) {
		notifyRecipients(owner, () -> recipients, containerId, null, "notifying delete recipients", CONTAINER_DELETED, null,recipient -> emailActions.sendContainerRevokedEmail(owner, recipient, null));
	}

	private void resetContainerShareFlag(final Long containerId) {
		updateSelective(new Container(containerId, false));
	}

	/**
	 * If the share count for the container indicates that the container is no longer shared, we reset the shared flag on the container.
	 *
	 * @param containerId
	 * @return
	 */
	private void checkShareCount(final Long containerId) {
		if (containerAssignmentOperations.getShareCount(containerId).equals(1) && containerGroupAssignmentOperations.getShareCount(containerId).equals(0)) {
			try {
				resetContainerShareFlag(containerId);
			} catch(final ServerSideException ex) {
				logger.exception(ex);
			}
		}
	}

	private Void shareItemWithUser(final User caller, final SharedItem item, final Supplier<Stream<UserDefinedTypeValueDTO>> valueSupplier, final Long recipientId, final PostOpActions postOpActions, final Boolean sharedExplicitly) {
		return itemActions.shareItemWithUser(item, caller.getId(), recipientId, postOpActions, valueSupplier, null, sharedExplicitly);
	}

	private Void shareItemWithUsers(final User caller, final SharedItem item, final PostOpActions postOpActions, final Supplier<Stream<Long>> recipientStreamSupplier, final Container container, final Supplier<PublicKeyDecryptor> pkdSupplier, final Boolean sharedExplicitly) {
		item.setItemId(item.getId());
		final Supplier<Stream<UserDefinedTypeValueDTO>> valueSupplier = StreamSupplier.create(() -> userDefinedTypeValueActions.getList(caller, item.getId(), pkdSupplier, null)); // outside of processStream parameter list so that it is only created once
		return processStream(() -> caller + " is sharing item '" + item + "' in container '" + container + "' with users", recipientStreamSupplier, recipientId -> shareItemWithUser(caller, item, valueSupplier, recipientId, postOpActions, sharedExplicitly));
	}

	private Long shareContainerWithUser(final User caller, final Long recipientId, final Container container, final Collection<Long> recipients, final Boolean sharedExplicitly) {
		if (caller.self(recipientId)) { throw new SharingProhibitedException("You may not share a container with yourself."); }
		auditContainerShare(SHARE_CONTAINER_WITH_USER, caller.getUsername(), caller.getId(), recipientId, container.getId());

		return createContainerAssignment(recipientId, container.getId(), recipients, sharedExplicitly, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
	}

	@Override
	public Void shareContainerWithUsersWithoutNotify(final User caller, final Long containerId, final Long containerAssignmentId, final Supplier<Stream<Long>> recipientStreamSupplier, final Supplier<PublicKeyDecryptor> pkdSupplier, final Collection<Long> recipients, final Boolean sharedExplicitly) {
		final Container container = getModel(containerId);

		ensureCallerMayShareContainer(caller, container);
		processStream(() -> caller + " is sharing container " + container + " with users", recipientStreamSupplier, uid -> shareContainerWithUser(caller, uid, container, recipients, sharedExplicitly), () -> "creating container assignment");
		processItemsInContainer(caller, containerAssignmentId, item -> shareItemWithUsers(caller, item, null, recipientStreamSupplier, container, pkdSupplier, Boolean.FALSE));
		markContainerShared(container);
		return null;
	}

	private void ensureContainerBasedSharingIsEnabled() {
		if (isFalse(loginActions.currentLicenseStats().getSupportsContainerSharing())) {
			throw new LicenseKeyException("Container sharing operations are unavailable with current License.");
		}
	}

	@Override
	public Void shareContainerWithUsers(final User caller, final Long containerId, final Long containerAssignmentId, final Supplier<Stream<Long>> recipientStreamSupplier, final Supplier<PublicKeyDecryptor> pkdSupplier, final String shareComment, final PostOpActions postOpActions) {
		ensureContainerBasedSharingIsEnabled();
		final Collection<Long> recipients = new HashSet<>();
		shareContainerWithUsersWithoutNotify(caller, containerId, containerAssignmentId, recipientStreamSupplier, pkdSupplier, recipients, Boolean.TRUE);
		notifyShareRecipients(caller, recipients.stream(), containerId, shareComment, postOpActions);
		return null;
	}

	@Override
	public Void shareContainerWithUsers(final User caller, final Long containerAssignmentId, final Supplier<Stream<Long>> recipientStreamSupplier, final PassphraseHash passphraseHash, final String shareComment, final PostOpActions postOpActions) {
		shareContainerWithUsers(caller, getContainerId(containerAssignmentId), containerAssignmentId, recipientStreamSupplier, getPKDSupplier(caller, passphraseHash), shareComment, postOpActions);
		return null;
	}

	@Override
	public void deleteArtifacts(final User caller, final String revokeComment, final Long containerId) {
		getNotificationEngine(REVOKE_CONTAINER, containerNotificationEngineFactory).notifyUsers(caller, revokeComment, containerAssignmentOperations.getAllArtifacts());
		containerAssignmentOperations.deleteArtifacts(containerId); // count is checked in where clause
	}

	private Void auditContainerRevoke(final User caller, final Long recipientId, final Container container) {
		final User recipient = getUser(recipientId);
		ensureCallerMayShareContainer(caller, container, recipient);
		auditContainerShare(REVOKE_CONTAINER_FROM_USER, caller.getUsername(), caller.getId(), recipientId, container.getId());

		return null;
	}

	@Override
	public Void revokeContainerFromUsers(final User caller, final Long containerAssignmentId, final Supplier<Stream<Long>> recipientIdSupplier, final PassphraseHash passphraseHash, final String revokeComment, final PostOpActions postRevokeActions) {
		ensureContainerBasedSharingIsEnabled();
		final Long containerId = getContainerId(containerAssignmentId);
		final Container container = getModel(containerId);

		ensureCallerMayShareContainer(caller, container);
		processItemsInContainer(caller, containerAssignmentId, i -> itemActions.revokeItemFromUsers(caller, i.getId(), recipientIdSupplier, passphraseHash, Boolean.FALSE, revokeComment, postRevokeActions));
		processStream(() -> caller + " is revoking container '" + container + "' from users", recipientIdSupplier, r -> auditContainerRevoke(caller, r, container));
		final Collection<Long> users = recipientIdSupplier.get().collect(toList());
		containerAssignmentOperations.decrementShareCount(container.getId(), users);
		containerAssignmentOperations.setSharedExplicitly(container.getId(), users, Boolean.FALSE);
		deleteArtifacts(caller, revokeComment, containerId);
		checkShareCount(containerId);
		return null;
	}

	private Void containerGroupUpdateAction(final User caller, final Long containerAssignmentId, final Consumer<Container> consumer) {
		final Container container = getModel(getContainerId(containerAssignmentId));

		ensureCallerMayShareContainer(caller, container);
		consumer.accept(container);

		return null;
	}

	private Void shareContainerWithUser(final Container c, final LimitedUser u, final Collection<Long> recipients, final Boolean sharedExplicitly) {
		createContainerAssignment(u.getId(), c.getId(), recipients, sharedExplicitly, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
		return null;
	}

	private Void shareContainerWithUsersInGroup(final User caller, final Container c, final Group group, final Collection<Long> recipients) {
		logger.info(() -> "Sharing container '" + c + "' with users in group '" + group + "'");
		final Long groupId = group.getId();
		processStream(() -> caller + " is adding users in group '" + group + "' to container '" + c + "'", () -> getOthersInGroup(caller, groupId), user -> shareContainerWithUser(c, user, recipients, Boolean.FALSE));
		containerGroupAssignmentOperations.create(new ContainerGroupAssignment(c.getId(), groupId));
		auditContainerShare(SHARE_CONTAINER_WITH_GROUP, caller.getUsername(), caller.getId(), groupId, c.getId());

		return null;
	}

	private NotificationEngine<ContainerAssignment> getNotificationEngine(final NotificationType type, final Long containerAssignmentId) {
		return getNotificationEngine(type, containerNotificationEngineFactory).addSupplier(() -> containerAssignmentOperations.getAll(getContainerId(containerAssignmentId)).collect(toList()));
	}

	private Void shareItemsWithGroups(final User caller, final PostOpActions postOpActions, final Supplier<PublicKeyDecryptor> pkdSupplier, final Supplier<Stream<Long>> groupIds, final Collection<Long> recipients, final LimitedItem item) {
		return itemActions.shareItemWithGroups(caller, item.getId(), postOpActions, itemActions.createUDTValueListSupplier(caller, item.getId(), pkdSupplier), groupIds, recipients);
	}

	/**
	 *
	 * @param c
	 * @param groupId
	 * @return a Group that is guaranteed to not be already shared with Container c.
	 */
	private Group getUnsharedGroup(final Container c, final Long groupId) {
		final Group group = getGroup(groupId);		// ensure, early, that group exists
		ensureContainerWasNotYetShared(x -> getContainerGroupAssignmentId(c.getId(), groupId), () -> "the group " + group);
		return group;
	}

	@Override
	public Void shareContainerWithGroups(final User caller, final Long containerAssignmentId, final Supplier<Stream<Long>> groupIdStreamSupplier, final PassphraseHash passphraseHash, final String shareComment, final PostOpActions postOpActions) {
		ensureContainerBasedSharingIsEnabled();
		final Supplier<PublicKeyDecryptor> pkdSupplier = getPKDSupplier(caller, passphraseHash);

		return containerGroupUpdateAction(caller, containerAssignmentId, c -> {
			final NotificationEngine<ContainerAssignment> notificationEngine = getNotificationEngine(SHARE_CONTAINER, containerAssignmentId);
			final Supplier<Stream<Group>> groups = () -> groupIdStreamSupplier.get().map(groupId -> getUnsharedGroup(c, groupId));

			// TODO: Remove recipients variable.  It looks like recipients was used prior to the NotificationEngine.  We now appear to add users to recipients, but do not actually do anything with the list.
			final Collection<Long> recipients = new HashSet<>();
			processStream(() -> caller + " is sharing container " + c + " with groups", groups, group -> shareContainerWithUsersInGroup(caller, c, group, recipients), () -> "sharing container with group");
			processItemsInContainer(caller, containerAssignmentId, item -> shareItemsWithGroups(caller, postOpActions, pkdSupplier, groupIdStreamSupplier, recipients, item));
			markContainerShared(c);
			notificationEngine.notifyUsers(caller, shareComment);});
	}

	private void revokeContainerFromGroup(final User caller, final Container c, final Long groupId) {
		containerAssignmentOperations.decrementShareCount(c.getId(), groupId);
		containerGroupAssignmentOperations.delete(c.getId(), groupId);
		auditContainerShare(REVOKE_CONTAINER_FROM_GROUP, caller.getUsername(), caller.getId(), groupId, c.getId());
	}

	@Override
	public Void revokeContainerFromGroups(final User caller, final Long containerAssignmentId, final Collection<Long> groupIdList, final PassphraseHash passphraseHash, final String revokeComment) {
		ensureContainerBasedSharingIsEnabled();
		validatePassphraseHash(caller, passphraseHash); // ensures user has active public key as an additional level of authentication

		return containerGroupUpdateAction(caller, containerAssignmentId, c -> {
			final Long containerId = c.getId();
			final StreamSupplier<Long> groupIdSupplier = StreamSupplier.create(groupIdList);
			final ObidosExecutor onCompletion = new ObidosExecutor();	// this is used to call the checkShared method which needs to be called after deleteArtifacts

			processItemsInContainer(caller, containerAssignmentId, i -> itemActions.revokeItemFromGroups(caller, i.getId(), groupIdSupplier, onCompletion)); // we notify users of entire container revoke, not individual items
			itemActions.deleteArtifacts(caller, revokeComment);
			onCompletion.runAll();
			processStream(() -> caller + " is revoking container '" + c + "' from groups", groupIdSupplier, g -> revokeContainerFromGroup(caller, c, g), () -> "revoking container from group");
			deleteArtifacts(caller, revokeComment, containerId);
			checkShareCount(containerId);});
	}

	@Override
	public void deleteIfEmpty(final Long containerAssignmentId) {
		if (itemOperations.countItemsInContainer(containerAssignmentId).equals(0)) {
			containerOperations.delete(getContainerId(containerAssignmentId));
		}
	}

	private static void containerNotNull(final Long id) {
		if (id == null) { throw new ServerSideException("You must specify a container."); }
	}

	private String userList(final Collection<Long> recipients) {
		final StringBuilder sb = new StringBuilder();
		recipients.forEach(id -> sb.append(userOperations.get(id).getFullname() + ", "));
		return sb.toString();
	}

	private String groupList(final Collection<Long> groups) {
		final StringBuilder sb = new StringBuilder();
		groups.forEach(id -> sb.append(groupOperations.get(id).getName() + ", "));
		return sb.toString();
	}

	@Override
	public Void grantUsersPermission(final User caller, final Long containerAssignmentId, final PermissionDTO permission, final Collection<Long> recipients, final PassphraseHash passphraseHash) {
		if (recipients == null || recipients.isEmpty())			{ throw new ServerSideException("Please specify recipients."); }
		if (permission == null || !permission.somethingIsSet())	{ throw new ServerSideException("Please specify permissions to grant."); }

		final ContainerAssignment containerAssignment = secureGetContainerAssignment(caller, containerAssignmentId, passphraseHash);
		if (!containerAssignment.ownerIs(caller)) { throw new ItemOwnershipException(); }

		final Long containerId = containerAssignment.getContainerId();

		if (!getModel(containerId).ownerIs(caller)) { throw new ItemOwnershipException(); }

		logger.debug(() -> "Granting  permission " + permission + " to " + userList(recipients) + " for container " + containerId);

		return containerAssignmentOperations.grantPermissions(containerId, permission, recipients);
	}

	@Override
	public Void grantGroupsPermission(final User caller, final Long containerAssignmentId, final PermissionDTO permission, final Collection<Long> groups, final PassphraseHash passphraseHash) {
		if (groups == null || groups.isEmpty())			{ throw new ServerSideException("Please specify groups."); }
		if (permission == null || !permission.somethingIsSet())	{ throw new ServerSideException("Please specify permissions to grant."); }

		final ContainerAssignment containerAssignment = secureGetContainerAssignment(caller, containerAssignmentId, passphraseHash);
		if (!containerAssignment.ownerIs(caller)) { throw new ItemOwnershipException(); }

		final Long containerId = containerAssignment.getContainerId();

		if (!getModel(containerId).ownerIs(caller)) { throw new ItemOwnershipException(); }

		logger.debug(() -> "Granting  permission " + permission + " to " + groupList(groups) + " for container " + containerId);

		return containerGroupAssignmentOperations.grantPermissions(containerId, permission, groups);
	}

	private Supplier<Stream<Long>> getUsersSharingContainerSupplier(final User caller, final Long containerId) {
		return () -> getUserIdsSharingContainer(caller.getId(), containerId);
	}

	private void takeOwnershipOfItem(final User caller, final Long containerAssignmentId, final PassphraseHash passphraseHash, final PostOpActions postOpActions, final Item item) {
		itemActions.takeOwnership(caller, item, itemActions.getItemAssignment(item.getId(), caller.getId()), containerAssignmentId, passphraseHash, postOpActions);
	}

	private Long getPrevOwnerId(final User caller, final Long containerId) {
		final Container container = getModel(containerId);
		logger.info(() -> "User " + caller.getUsername() + " (" + caller.getId() + ") is taking ownership of container " + container.getName() + " (" + containerId + ") ");
		if (container.ownerIs(caller)) { throw new PermissionDeniedException("You already own this container."); }
		return container.getUserId();
	}

	private void mayTakeContainerOwnership(final Long userId, final ContainerAssignment ca) {
		if (isFalse(ca.getOwnershipControl()) && !containerOperations.mayTakeOwnership(userId, ca.getContainerId())) {
			throw new PermissionDeniedException("You were not granted ownership control of this container.");
		}
	}

	@Override
	public Void takeOwnership(final User caller, final Long containerAssignmentId, final PassphraseHash passphraseHash, final PostOpActions postOpActions) {
		final Long userId = caller.getId();
		final ContainerAssignment ca = secureGetContainerAssignment(caller, containerAssignmentId, passphraseHash);
		final Long containerId = ca.getContainerId();

		mayTakeContainerOwnership(userId, ca);

		final Long prevOwnerId = getPrevOwnerId(caller, containerId);

		containerAssignmentOperations.updateSelective(new ContainerAssignment(containerAssignmentId, (Boolean) null, true, null));	// gives new user (full) control in case they did not have it before
		containerAssignmentOperations.resetShareCount(userId, containerId);
		updateSelective(new Container(containerId, userId)); // container is owned by caller after call completes
		processItemsInContainer(caller, getContainerAssignmentId(containerId, prevOwnerId), item -> takeOwnershipOfItem(caller, containerAssignmentId, passphraseHash, postOpActions, item));
		// change ownership of all items within container
		auditContainerOwned(caller.getUsername(), userId, prevOwnerId, containerId);
		// We notify all the users who have access to the container after we return.
		notifyRecipients(caller, getUsersSharingContainerSupplier(caller, containerId), containerId, null, "notifying ownership change recipients", CONTAINER_OWNED, u-> getContainerAssignmentId(containerId, u), null);

		return null;
	}
}
