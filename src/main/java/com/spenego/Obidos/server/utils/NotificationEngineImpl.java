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

package com.spenego.Obidos.server.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.EmailActions;
import com.spenego.Obidos.server.actions.ItemActions;
import com.spenego.Obidos.server.actions.NotificationActions;
import com.spenego.Obidos.server.model.Assignable;
import com.spenego.Obidos.server.model.Container;
import com.spenego.Obidos.server.model.Item;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.ContainerOperations;
import com.spenego.Obidos.server.operations.ItemOperations;
import com.spenego.Obidos.server.operations.UserOperations;
import com.spenego.Obidos.shared.dto.NotificationDTO;

public final class NotificationEngineImpl<T extends Assignable> implements NotificationEngine<T> {
	private static final Logger logger = LoggerFactory.getLogger(NotificationEngineImpl.class);

	@Autowired private final ContainerOperations		containerOperations	= null;
	@Autowired private final EmailActions				emailActions		= null;
	@Autowired private final ItemOperations				itemOperations		= null;
	@Autowired private final ItemActions				itemActions			= null;
	@Autowired private final NotificationActions		notificationActions	= null;
	@Autowired private final UserOperations				userOperations		= null;

	private final Set<T>								originalAssignmentSet;
	private NotificationType							type;
	private final Collection<Supplier<Collection<T>>>	suppliers;

	public NotificationEngineImpl() {
		originalAssignmentSet = new HashSet<>();
		suppliers = new ArrayList<>();
	}

	@Override
	public NotificationEngine<T> addSupplier(final Supplier<Collection<T>> supplier) {
		suppliers.add(supplier);
		supplier.get().forEach(originalAssignmentSet::add);
		return this;
	}

	@Override
	public NotificationEngine<T> setType(final NotificationType type) {
		this.type = type;
		return this;
	}

	private int convertAction(int setSize) {
		switch(type) {
		case SHARE_CONTAINER:	return setSize > 1 ? NotificationDTO.MULTIPLE_CONTAINERS_SHARED : NotificationDTO.CONTAINER_SHARED;
		case SHARE_ITEM: 		return setSize > 1 ? NotificationDTO.MULTIPLE_ITEMS_SHARED : NotificationDTO.ITEM_SHARED;
		case DELETE_ITEM:		return NotificationDTO.ITEM_DELETED;
		case DELETE_CONTAINER:	return NotificationDTO.CONTAINER_DELETED;
		case REVOKE_CONTAINER:	return NotificationDTO.CONTAINER_REVOKED;
		case REVOKE_ITEM:		return NotificationDTO.ITEM_REVOKED;
		case UPDATE_CONTAINER:	return NotificationDTO.CONTAINER_RENAMED;
		case UPDATE_ITEM:		return NotificationDTO.ITEM_UPDATED;
		}
		return NotificationDTO.ITEM_RELINQUISHED;
	}

	/**
	 * Destructive actions will no longer have assess to the assignable object. We must pass a null as the id.
	 *
	 * @return
	 */
	private boolean isDestructiveAction() {
		switch(type) {
		case UPDATE_CONTAINER:
		case UPDATE_ITEM:
		case SHARE_CONTAINER:
		case SHARE_ITEM: 		return false;
		case DELETE_ITEM:
		case DELETE_CONTAINER:
		case REVOKE_CONTAINER:
		case REVOKE_ITEM:		return true;
		}
		return true;
	}


	private void notifyRecipient(final int action, final User caller, final Long userId, final String shareComment, final Long id, final String objectType, final String name, final Consumer<User> emailSender) {
		notificationActions.notifyRecipient(action, caller, userId, id, objectType, name, shareComment, emailSender);
	}

	private void sendItemNotificationToUser(final User caller, final Long userId,  final Assignable assignable, final int action, final Long id, final String shareComment) {
		// We do not want to generate a notification for every single item shared, so in cases with multiple items, we just say 'multiple'
		final Item item = (assignable != null) ? itemOperations.get(assignable.getReferredId()) : null;
		final String name = (item == null) ? null : item.getName(); // NOSONAR -- bug in SonarQube
		final boolean isNote = (item == null) ? false : itemActions.isNote(item); // NOSONAR -- bug in SonarQube
		final Consumer<User> emailSender = type == NotificationType.SHARE_ITEM ? recipient -> emailActions.sendItemSharedEmail(id, caller, recipient, shareComment, isNote):
																				recipient -> emailActions.sendItemRevokedEmail(caller, recipient, shareComment);
		logger.info(() -> "sending item notification to user " + userId);
		notifyRecipient(action, caller, userId, shareComment, id, "item", name, emailSender);
	}

	private void sendContainerNotificationToUser(final User caller, final Long userId,  final Assignable assignable, final int action, final Long id, final String shareComment) {
		final Container container = (assignable == null) ? null : containerOperations.get(assignable.getReferredId()); // NOSONAR -- bug in SonarQube
		final String name = (container == null) ? null : container.getName(); // NOSONAR -- bug in SonarQube, does not always evaluate to true
		final Consumer<User> emailSender = type == NotificationType.SHARE_CONTAINER ? recipient -> emailActions.sendContainerSharedEmail(id, caller, recipient, shareComment) :
																						recipient -> emailActions.sendContainerRevokedEmail(caller, recipient, shareComment);
		logger.info(() -> "sending container notification to user " + userId);
		notifyRecipient(action, caller, userId, shareComment, id, "container", name, emailSender);
	}

	private Long getAssignableId(final Assignable a) {
		return (isDestructiveAction() ? null : a.getId());
	}

	private Void sendNotificationsToUser(final User caller, final Long userId, final Set<T> set, final String shareComment) {
		final int action = convertAction(set.size());
		final Assignable[] assignableArray = new Assignable[1];
		final Assignable assignable = (set.size() == 1) ? (set.toArray(assignableArray)[0]) : null; // NOSONAR -- bug in SonarQube
		final Long id = (assignable == null) ? null : getAssignableId(assignable);

		if (NotificationDTO.isItemAction(action)) {
			sendItemNotificationToUser(caller, userId, assignable, action, id, shareComment);
		} else {
			sendContainerNotificationToUser(caller, userId, assignable, action, id, shareComment);
		}

		return null;
	}

	private String getAction() {
		switch(type) {
		case SHARE_CONTAINER:
		case SHARE_ITEM: 		return "notifying share recipients";
		case DELETE_ITEM:
		case DELETE_CONTAINER:	return "notifying delete recipients";
		case REVOKE_CONTAINER:
		case REVOKE_ITEM:		return "notifying revoke recipients";
		case UPDATE_CONTAINER:
		case UPDATE_ITEM:		return "notifying update recipients";
		}
		return null;
	}

	private boolean containerOperation() {
		switch(type) {
		case SHARE_CONTAINER:
		case DELETE_CONTAINER:
		case REVOKE_CONTAINER:
		case UPDATE_CONTAINER:	return true;
		case SHARE_ITEM:
		case DELETE_ITEM:
		case REVOKE_ITEM:
		case UPDATE_ITEM:		return false;
		}
		return false;
	}

	private void logAssignmentSet(final String name, final Set<T> set) {
		logger.info(() -> name + " assignment set has " + set.size() + " assignments");
		set.forEach(a -> logger.info(() -> "    User: " + userOperations.get(a.getUserId()) + (containerOperation() ? ", container = " : ", item = ") + a.getId()));
	}

	private void addUserAssignmentMapping(final Map<Long, Set<T>> map, final Long userId, final T assignable) {
		map.computeIfAbsent(userId, u -> new HashSet<>()).add(assignable);
	}

	private void postCommitNotification(final User caller, final String comment, final Collection<T> assignments) {
		final Map<Long, Set<T>>	userAssignmentMap = new HashMap<>(assignments.size());
		assignments.forEach(a -> addUserAssignmentMapping(userAssignmentMap, a.getUserId(), a));
		notificationActions.processNotifications(this::getAction, () -> userAssignmentMap.keySet().stream(), u -> sendNotificationsToUser(caller, u, userAssignmentMap.get(u), comment));
	}

	@Override
	public void notifyUsers(final User caller, final String comment, final Collection<T> assignments) {
		logger.info(() -> "Will notify " + assignments.size() + " users");
		if (assignments.isEmpty()) {
			return;
		}
		notificationActions.postCommitAction(() -> postCommitNotification(caller, comment, assignments));
	}

	/**
	 * Notify users of items or containers that are now shared with them or have been revoked from them.  This method determines which
	 * items of containers to send notifications about by invoking the suppliers supplied from the earlier calls to addSupplier.  It is
	 * assumed some operation has taken place between calling addSupplier and notifyUsers.
	 *
	 * @param caller - the user sharing or revoking the items and containers.
	 * @param comment - the message to send to the user regarding the share or revoke
	 */
	@Override
	public void notifyUsers(final User caller, final String comment) {
		final Set<T> residualSet = new HashSet<>(originalAssignmentSet.size() * 2 + 16);

		logAssignmentSet("Original", originalAssignmentSet);
		suppliers.forEach(supplier -> supplier.get().forEach(residualSet::add));
		logAssignmentSet("Residual", residualSet);

		if (residualSet.size() != originalAssignmentSet.size()) {
		 	// Determines the difference in sets, between the originalAssignmentSet and the objects that are currently available.
		 	// This is effectively the difference in users who are granted access to items or containers between calls of addSupplier
		 	// and this method. This is used to determine which users to send share or revoke notifications to.
			final boolean assignmentsCreated = residualSet.size() > originalAssignmentSet.size();
			final Set<T> s1 = assignmentsCreated ? originalAssignmentSet : residualSet;
			final Set<T> difference = assignmentsCreated ? residualSet : originalAssignmentSet;

			s1.forEach(difference::remove);

			// Getting the set difference needs to be done within the context of the transaction.
			// We can't do it within the notification thread since we could end up seeing differences due to other operations. It must be
			// done in the context of one transaction.
			notifyUsers(caller, comment, difference);
		}
	}
}
