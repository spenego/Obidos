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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.NotificationsResult;

public interface NotificationServiceAsync {
	void getNotifications(AuthCredsDTO creds, Boolean unread, Integer first, Integer count, ArrayList<OrderBy> orderBy, AsyncCallback<NotificationsResult> result);
	void getNotificationCount(AuthCredsDTO creds, Boolean unread, AsyncCallback<Integer> result);
	
	/**
	 * 
	 * @param creds
	 * @param notificationIds A list of ids to mark as read. Pass null to mark all notifications, of this user, as read.
	 * @param result
	 */
	void markRead(AuthCredsDTO creds, ArrayList<Long> notificationIds, AsyncCallback<Void> result);
	
	/**
	 * 
	 * @param creds
	 * @param notificationIds A list of ids to mark as un-read. Pass null to mark all notifications, of this user, as un-read.
	 * @param result
	 */
	void markUnRead(AuthCredsDTO creds, ArrayList<Long> notificationIds, AsyncCallback<Void> result);
	
	/**
	 * Deletes a list of notifications.
	 * 
	 * @param creds
	 * @param notificationIds A list of ids to delete. Pass null to delete all notifications for this user.
	 * @param result
	 */
	void delete(AuthCredsDTO creds, ArrayList<Long> notificationIds, AsyncCallback<Void> result);
}