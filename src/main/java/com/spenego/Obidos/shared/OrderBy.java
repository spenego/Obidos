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

public enum OrderBy implements Serializable {
	CONTAINER_NAME_ASC,
	CREATE_TIME_ASC,
	EMAIL1_ASC,
	EMAIL2_ASC,
	EMAIL3_ASC,
	FULLNAME_ASC,
	GROUP_NAME_ASC,
	ITEM_NAME_ASC,
	USER_GROUP_COMBO_TYPE_ASC,
	USER_GROUP_COMBO_NAME_ASC,
	SMTP_SERVER_NAME_ASC,
	UDF_POSITION_ASC,
	UNREAD_ASC,
	UPDATE_TIME_ASC,
	USERNAME_ASC,

	CONTAINER_NAME_DESC,
	CREATE_TIME_DESC,
	EMAIL1_DESC,
	EMAIL2_DESC,
	EMAIL3_DESC,
	FULLNAME_DESC,
	GROUP_NAME_DESC,
	USER_GROUP_COMBO_TYPE_DESC,
	USER_GROUP_COMBO_NAME_DESC,
	ITEM_NAME_DESC,
	SMTP_SERVER_NAME_DESC,
	UDF_POSITION_DESC,
	UNREAD_DESC,
	UPDATE_TIME_DESC,
	USERNAME_DESC
}
