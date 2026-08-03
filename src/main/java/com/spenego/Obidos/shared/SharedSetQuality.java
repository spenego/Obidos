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

package com.spenego.Obidos.shared;

import java.io.Serializable;

/**
 * Allows you to specify if the users returned are those that the item/container has not been share with (NOT_SHARED_WITH)
 * or only user that HAVE shared the item/container (ONLY_SHARED_WITH). You can have the list include both with
 * SHARED_ARE_MARKED. The users sharing the item/container will be marked as such.
 *
 * @author mmorgan
 *
 */
public enum SharedSetQuality implements Serializable  {
	/**
	 * Entries in resultant list are only users/groups not yet sharing the item/container.
	 */
	NOT_SHARED_WITH,

	/**
	 * Entries in resultant list are only users/groups that ARE sharing the item/container.
	 */
	ONLY_SHARED_WITH,

	/**
	 * Entries in resultant list container both user/groups that are share and not sharing but those that are sharing are marked.
	 */
	SHARED_ARE_MARKED
}
