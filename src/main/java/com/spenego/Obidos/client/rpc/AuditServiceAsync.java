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

package com.spenego.Obidos.client.rpc;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuditReport;
import com.spenego.Obidos.shared.dto.AuditResult;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.DateRange;

public interface AuditServiceAsync {
	void getAuditRecords(AuthCredsDTO creds, ArrayList<String> actions, ArrayList<String> usernames, ArrayList<String> objects, DateRange dateRange, Integer offset, Integer count, ArrayList<OrderBy> orderby, AsyncCallback<AuditResult> callback);
	void getAuditReport(AuthCredsDTO creds, AsyncCallback<AuditReport> callback);
}
