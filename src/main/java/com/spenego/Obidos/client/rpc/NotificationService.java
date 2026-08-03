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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.NotificationsResult;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@RemoteServiceRelativePath("rpc/notificationService")
public interface NotificationService extends RemoteService {
	public static class Utility {
		private final static NotificationServiceAsync instance = (NotificationServiceAsync) GWT.create(NotificationService.class);
		public static NotificationServiceAsync getInstance() { return instance; }
	}
	
	/**
	 * Returns a list of notifications to the user.
	 * 
	 * @param creds
	 * @param unread If true, only notifications that are still marked unread are returned. Conversely, if false,
	 *               only notifications that have been read are returned. If null, all notifications are returned.
	 * @param first
	 * @param count
	 * @param orderBy Orders the result set according to unread state, create time
	 * @return
	 * @throws ServerSideException
	 */
	NotificationsResult getNotifications(AuthCredsDTO creds, Boolean unread, Integer first, Integer count, ArrayList<OrderBy> orderBy) throws ServerSideException;

	Integer getNotificationCount(AuthCredsDTO creds, Boolean unread) throws ServerSideException;

	Void markRead(	AuthCredsDTO creds, ArrayList<Long> notificationIds) throws ServerSideException;
	Void markUnRead(AuthCredsDTO creds, ArrayList<Long> notificationIds) throws ServerSideException;
	Void delete(	AuthCredsDTO creds, ArrayList<Long> notificationIds) throws ServerSideException;
}
