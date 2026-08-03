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

import static com.spenego.Obidos.server.model.Audit.DELETE_NOTIFICATION;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.NotificationActions;
import com.spenego.Obidos.server.model.Notification;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.NotificationOperations;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.NotificationsResult;

/**
 * This class is lighter-weight than other Action classes. It does not audit operations, nor does it
 * retrieve objects before deleting them (to inform users of permission issues).
 *
 * @author mmorgan
 *
 */
public final class NotificationActionsImpl extends CryptoActions<Notification> implements NotificationActions {
	private static final Logger logger = LoggerFactory.getLogger(NotificationActionsImpl.class);

	@Autowired private final NotificationOperations operations = null;
	private final Thread notificationThread = getThreadFactory("Notifier").newThread(this);

	@Override
	protected final String messageType() { return "Notification"; }

	public NotificationActionsImpl() {
		super(null, null);
	}

	@Override
	protected final Operations<Notification> getOperations() {
		return operations;
	}

	@Override
	protected final Logger getLogger() {
		return logger;
	}

	@Override
	protected final Integer getAuditDeleteAction() {
		return DELETE_NOTIFICATION;
	}

	@Override
	protected final String elementName() {
		return "notification";
	}

	@PostConstruct
	@Override
	protected final void init() {
		logger.info(() -> "Starting Notification thread");
		notificationThread.start();
		initialized = true;
	}

	@Override
	public final void runInNotificationThread(final Runnable runnable) {
		execute(runnable);
	}

	@Override
	public <T> void processNotifications(final Supplier<String> nameSupplier, final Supplier<Stream<T>> supplier, final Consumer<T> processor) {
		runInNotificationThread(() -> processStream(nameSupplier, supplier, processor));
	}

	private NotificationsResult completeMessageCounts(final User caller, final NotificationsResult result) {
		result.setUnreadMessageCount(operations.getNotificationCount(caller.getId(), true, null));
		result.setReadMessageCount(operations.getNotificationCount(caller.getId(), false, null));
		return result;
	}

	@Override
	public NotificationsResult getNotifications(final User caller, final Boolean unread, final String messageSearch, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return completeMessageCounts(caller, new NotificationsResult(first, count, null, (a,b) -> convert(a,b),
				() -> operations.getNotificationCount(caller.getId(), unread, messageSearch),
				() -> operations.getNotifications(caller.getId(), unread, messageSearch, first, count, orderBy)));
	}

	@Override
	public Integer getNotificationCount(final User caller, final Boolean unread) {
		return operations.getNotificationCount(caller.getId(), unread, null);
	}

	@Override
	public Notification getNotification(final Integer action, final Long ownerId, final Long userId, final Long targetId) {
		return operations.getNotification(action, ownerId, userId, targetId);
	}

	@Override
	public Void update(final Notification notification) {
		return operations.update(notification);
	}

	private Void markUnreadAs(final User caller, final Collection<Long> notificationIds, final PassphraseHash passphraseHash, final boolean unread) {
		validatePassphraseHash(caller, passphraseHash); // ensures user has active public key as an additional level of authentication
		return operations.markUnreadAs(caller.getId(), notificationIds, unread);
	}

	@Override
	public Void markRead(final User caller, final Collection<Long> notificationIds, final PassphraseHash passphraseHash) {
		return markUnreadAs(caller, notificationIds, passphraseHash, false);
	}

	@Override
	public Void markUnRead(final User caller, final Collection<Long> notificationIds, final PassphraseHash passphraseHash) {
		return markUnreadAs(caller, notificationIds, passphraseHash, true);
	}

	@Override
	public Void delete(final User caller, final Collection<Long> notificationIds, final PassphraseHash passphraseHash) {
		validatePassphraseHash(caller, passphraseHash); // ensures user has active public key as an additional level of authentication
		operations.delete(caller.getId(), notificationIds);
		return null;
	}

	@Override
	public Void deleteAll(final User caller) {
		operations.delete(caller.getId(), null);
		return null;
	}
}
