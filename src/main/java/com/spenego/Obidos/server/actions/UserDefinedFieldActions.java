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
import com.spenego.Obidos.shared.dto.UserDefinedFieldDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldResult;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public interface UserDefinedFieldActions {
	void setBlockAudit(boolean blockAudit);
	Long create(User caller, UserDefinedFieldDTO dto, PassphraseHash passphraseHash, boolean isAdHoc);
	Long create(User caller, Long typeId, UserDefinedFieldDTO dto, PassphraseHash passphraseHash, boolean isAdHoc);
	Long create(Long typeId, UserDefinedFieldDTO dto, Long userId, String username, boolean isAdHoc);

	/**
	 * Make a copy of the field.
	 * @param fieldId
	 * @return the new field ID
	 */
	Long clone(User caller, Long fieldId, Long typeId);
	UserDefinedFieldDTO get(Long id) throws NoSuchRecordException;
	UserDefinedFieldResult getUserDefinedFields(Long userId, String search, Integer first, Integer count, List<OrderBy> orderBy);
	Void update(User caller, UserDefinedFieldDTO dto, PassphraseHash passphraseHash);
	Void update(UserDefinedFieldDTO dto, Long userId, String username);
	Void delete(User caller, Collection<Long> id, PassphraseHash passphraseHash);
	Void delete(Long id, String objName, Long userId, String username);

	void setRunningInUnitTest();
}
