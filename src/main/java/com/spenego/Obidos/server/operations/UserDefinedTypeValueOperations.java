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

import java.util.stream.Stream;

import com.spenego.Obidos.server.model.UserDefinedTypeValue;

public interface UserDefinedTypeValueOperations extends Operations<UserDefinedTypeValue> {
	Stream<UserDefinedTypeValue> getUserDefinedTypeValues(Long userId, Long itemid);
	Stream<UserDefinedTypeValue> getUserDefinedTypeValues(Long typeId);
	Void updateType(Long userId, Long typeId, Long newTypeId);
	int numberOfInstancesOfType(Long typeid, Long userId);

	/**
	 * Finds the number of references items make to the typeId and are not owned by userId.
	 * This is used to find out if there are any users that have created items that use the
	 * type in case we need to update or delete it. If only the owner has created items that
	 * use this type, then the type can be updated, or deleted, without cloning.
	 *
	 * @param typeid
	 * @param userId
	 * @return
	 */
	int numberOfItemReferencesUsingType(Long typeId, Long userId);
	int deleteItemReferencesUsingType(Long typeid, Long userId);
	int deleteUserItemReferences(Long itemId, Long userId);
	int deleteOtherUserItemReferences(Long itemId, Long userId);
	int deleteUserItemReferencesViaContainerId(Long containerId, Long userId);
}
