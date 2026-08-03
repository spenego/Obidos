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
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.spenego.Obidos.server.model.Notification;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.NotificationsResult;

/**
 * Notifications are sent when items/containers are shared or revoked with a user.  They are also sent
 * by the system when significant actions effecting the user are performed.
 *
 * @since   Obidos1.0
 * @author  Mike Morgan
 * @see     com.spenego.Obidos.server.actions.impl.ObidosActions
 *
 */
public interface NotificationActions {
	Notification				getNotification		(Integer action, Long ownerId, Long userId, Long targetId);
	NotificationsResult			getNotifications	(User user, Boolean unread, String messageSearch, Integer first, Integer count, List<OrderBy> orderBy);
	Integer						getNotificationCount(User user, Boolean unread);
	Void						markRead			(User user, Collection<Long> notificationIds, PassphraseHash passphraseHash);
	Void						markUnRead			(User user, Collection<Long> notificationIds, PassphraseHash passphraseHash);
	Void						update				(Notification notification);
	Void						delete				(User user, Collection<Long> notificationIds, PassphraseHash passphraseHash);
	Void						deleteAll			(User user);
	void						runInNotificationThread(Runnable runnable);
	void						postCommitAction	(Runnable r);
	<T> void					processNotifications(Supplier<String> nameSupplier, Supplier<Stream<T>> supplier, Consumer<T> processor);
	void						notifyRecipient		(int action, User owner, Long recipientId, Long targetId, String sharedObjectName, String name, String shareComment, Consumer<User> notifier);
}
