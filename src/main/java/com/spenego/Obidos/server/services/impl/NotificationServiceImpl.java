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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spenego.Obidos.client.rpc.NotificationService;
import com.spenego.Obidos.server.actions.NotificationActions;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.NotificationsResult;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@Service("notificationService")
public final class NotificationServiceImpl extends ObidosService implements NotificationService {
	private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

	@Autowired private final NotificationActions actions = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

 	@Transactional(readOnly=true) @Override
	public NotificationsResult getNotifications(final AuthCredsDTO creds, final Boolean unread, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
		return userOrAdminFunction(creds, "get user notifications.", "getNotifications", true, user -> actions.getNotifications(user, unread, null, first, count, orderBy));
	}

 	@Transactional(readOnly=true) @Override
	public Integer getNotificationCount(final AuthCredsDTO creds, final Boolean unread) throws ServerSideException {
		return userOrAdminFunction(creds, "get notification count.", "getNotificationCount", false, user -> actions.getNotificationCount(user, unread));
	}

 	@Transactional @Override
	public Void markRead(final AuthCredsDTO creds, final ArrayList<Long> notificationIds) throws ServerSideException {
		return userOrAdminFunction(creds, "mark notifications read.", "markRead", true, user -> actions.markRead(user, notificationIds, summonPWHash()));
	}

 	@Transactional @Override
	public Void markUnRead(final AuthCredsDTO creds, final ArrayList<Long> notificationIds) throws ServerSideException {
		return userOrAdminFunction(creds, "mark notifications un-read.", "markUnRead", true, user -> actions.markUnRead(user, notificationIds, summonPWHash()));
	}

 	@Transactional @Override
	public Void delete(final AuthCredsDTO creds, final ArrayList<Long> notificationIds) throws ServerSideException {
		return userOrAdminFunction(creds, "delete notifications.", "delete", true, user -> actions.delete(user, notificationIds, summonPWHash()));
	}
}
