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

import com.spenego.Obidos.server.model.Audit;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.DateRange;

public interface AuditOperations extends Operations<Audit> {
	/**
	 * Get a list of audit records.
	 *
	 * @param actions Filter for audit records of this action.
	 * @param userIds Filter for audit records created by the specified user.
	 * @param objects Filter for audit records containing string in details or objectName.
	 * @param dateRange only include audit records between these dates
	 * @param offset
	 * @param count
	 * @param orderby
	 * @return
	 */
	Stream<Audit> getStream(User caller, Collection<Integer> actions, Collection<Long> userIds, Collection<String> objects, DateRange dateRange, Integer offset, Integer count, List<OrderBy> orderby);

	/**
	 * Get a list of audit records.
	 *
	 * @param actions Filter for audit records of this action.
	 * @param userIds Filter for audit records created by the specified user.
	 * @param dateRange only include audit records between these dates
	 * @param offset
	 * @param count
	 * @param orderby
	 * @return the total number of records matching the criteria
	 */
	Integer getCount(User caller, Collection<Integer> actions, Collection<Long> userIds, Collection<String> objects, DateRange dateRange);
}
