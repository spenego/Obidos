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
import java.util.function.Supplier;

import com.spenego.Obidos.server.model.LimitedUser;
import com.spenego.Obidos.server.model.SharedItemUser;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.model.UserDefinedFieldValue;
import com.spenego.Obidos.server.security.Encryption.PublicKeyDecryptor;
import com.spenego.Obidos.server.security.Encryption.PublicKeyEncryptor;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.UserDefinedFieldValueDTO;

public interface UserDefinedFieldValueActions {
	Void create(SharedItemUser creator, List<UserDefinedFieldValueDTO> row, Long rowId);
	Long addUDFValue(User creator, UserDefinedFieldValueDTO value, Long userDefinedTypeValueId);
	Void update(LimitedUser creator, UserDefinedFieldValueDTO value, Supplier<PublicKeyEncryptor> pkeSupplier);
	Collection<UserDefinedFieldValueDTO> getUDFValues(User creator, Long userDefinedType, Supplier<PublicKeyDecryptor> supplier, List<OrderBy> orderby);
	void setRunningInUnitTest();
	Long createUDFValue(UserDefinedFieldValue udfVal, byte[] value, Supplier<PublicKeyEncryptor> pkeSupplier);
}
