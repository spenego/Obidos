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

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.spenego.Obidos.server.model.UserDefinedType;
import com.spenego.Obidos.shared.OrderBy;

public interface UserDefinedTypeOperations extends Operations<UserDefinedType> {
	Long create(UserDefinedType udt);
	Stream<UserDefinedType> getTypes(Long userId, String search, Collection<Long> preSelectedTemplates, Boolean personal, Integer first, Integer count, List<OrderBy> orderBy);
	Integer getUserDefinedTypeCount(Long userId, String search, Collection<Long> preSelectedTemplates, Boolean personal);
	Integer updateSelectiveForVersion(Long id, Integer version, UserDefinedType t);
}
