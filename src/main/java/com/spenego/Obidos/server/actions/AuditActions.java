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

import com.spenego.Obidos.server.model.Audit;
import com.spenego.Obidos.server.model.Model;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuditReport;
import com.spenego.Obidos.shared.dto.AuditResult;
import com.spenego.Obidos.shared.dto.DateRange;

public interface AuditActions {
	void setRunningInUnitTest();
	void queueAuditAction(Supplier<Audit> supplier);

	Audit createAuditEntry(Integer action, Long userId, Object object, Long objectId);
	Audit createAuditEntry(Integer action, Long userId, Object object, Long objectId, Long newObjectId);
	Audit createAuditEntry(Integer action, Long userId, Object object, Long objectId, Long newObjectId, String details);
	Audit createAuditEntry(Integer action, Long userId, Long objectId, Long recipientId, Object objectName);

	Audit createContainerShareAuditEntry(int shareType, Long userId, Long recipientId, Long itemId);

	/**
	 * This creates a SHARE audit record specifically for sharing credentials with another user.
	 * @param username
	 * @param userId
	 * @param recipientName
	 * @param recipientId
	 * @param credentialsId
	 * @return
	 */
	Audit createItemShareAuditEntry(int shareType, Long userId, Long recipientId, Long itemId);
	Audit createRevokeAuditEntry(Long userId, Long recipientId, Model item);
	Audit createGroupRevokeAuditEntry(Long userId, Long groupId, Model group);
	Audit createItemOwnedAuditEntry(Long userId, Long previousOwnerId, Long itemId);
	Audit createContainerOwnedAuditEntry(Long userId, Long previousOwnerId, Long containerId);
	Audit createItemRelinquishAuditEntry(Long userId, Long recipientId, String itemName, Long itemId);
	Audit exception	(String details);
	//void  queue		(Supplier<Audit> supplier);

	AuditResult getAuditRecords	(User caller, Collection<String> actions, Collection<String> usernames, Collection<String> objects, DateRange dateRange, Integer offset, Integer count, List<OrderBy> orderby);
	AuditReport getAuditReport	(User admin);

}
