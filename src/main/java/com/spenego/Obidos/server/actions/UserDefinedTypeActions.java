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

import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeResult;

public interface UserDefinedTypeActions {
	Long create(User caller, UserDefinedTypeDTO dto, PassphraseHash passphraseHash);
	/**
	 * Creates a type with a dynamic name. The caller does not specify a type name for ad-hoc types.
	 * @param caller
	 * @param dto
	 * @return
	 */
	Long createAdHoc(User caller, UserDefinedTypeDTO dto, PassphraseHash passphraseHash);

	/**
	 * Update a UserDefinedType.
	 *
	 * @param caller
	 * @param fto You may change the name of the UserDefinedType or update fields.
	 * You must set the ID field when you intend on updating a field.
	 * If you wish to delete the field, you must set the ID and the delete flag.
	 * If you pass fields without an ID, a new field will be created.
	 * @param passphraseHash hash of caller's passphrase to authenticate caller and decrypt data
	 * @param modifyGlobalTemplateInstances set to true if you wish to modify affect instances of this type. Instances are items, or
	 *        more precisely, values of the items.
	 */
	UserDefinedTypeDTO update(User caller, UserDefinedTypeDTO dto, PassphraseHash passphraseHash, Boolean modifyGlobalTemplateInstances);
	UserDefinedTypeDTO get(User caller, Long id);
	UserDefinedTypeResult getUserDefinedTypes(User caller, Boolean personal, Long userId, String search, Collection<Long> preSelectedTemplates, Integer first, Integer count, List<OrderBy> orderBy);
	Void delete(User caller, Collection<Long> ids, Boolean deleteReferringItems, PassphraseHash passphraseHash);
	Long duplicate(User caller, Long typeId, String newName);
	void setRunningInUnitTest();
}
