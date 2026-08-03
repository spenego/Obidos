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

import java.util.Collection;
import java.util.function.Supplier;

import com.spenego.Obidos.server.model.Assignable;
import com.spenego.Obidos.server.model.User;


/**
 * Used to notify users when Items and Containers have been Shared or Revoked.  It will detect which Items and Containers
 * have been shared or revoked and determine which users those Items and Containers have been shared with or revoked from.
 *
 * @author mmorgan
 *
 */
public interface NotificationEngine<T extends Assignable> {
	public enum NotificationType {
		SHARE_ITEM,
		REVOKE_ITEM,
		UPDATE_ITEM,
		DELETE_ITEM,
		SHARE_CONTAINER,
		REVOKE_CONTAINER,
		UPDATE_CONTAINER,
		DELETE_CONTAINER
	}

	/**
	 * Notify users of items or containers that are now shared with them or have been revoked from them.
	 *
	 * @param caller
	 * @param comment
	 */
	void notifyUsers(User caller, String comment);
	void notifyUsers(User caller, String comment, Collection<T> collection);
	NotificationEngine<T> addSupplier(Supplier<Collection<T>> supplier);
	NotificationEngine<T> setType(NotificationType type);
}
