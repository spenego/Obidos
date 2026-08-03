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

package com.spenego.Obidos.server.operations;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.spenego.Obidos.server.model.Notification;
import com.spenego.Obidos.shared.OrderBy;

public interface NotificationOperations extends Operations<Notification>  {
	Notification			getNotification(Integer action, Long ownerId, Long userId, Long targetId);
	Integer					getNotificationCount(Long userId, Boolean unread, String msgSearch);
	Stream<Notification>	getNotifications(Long userId, Boolean unread, String msgSearch, Integer first, Integer count, List<OrderBy> orderBy);
	Void					markUnreadAs(Long userId, Collection<Long> notificationId, Boolean unRead);
	Integer					delete(Long userId, Collection<Long> notificationIds);
}
