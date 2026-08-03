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

package com.spenego.Obidos.server.services.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spenego.Obidos.client.rpc.AuditService;
import com.spenego.Obidos.server.actions.AuditActions;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuditReport;
import com.spenego.Obidos.shared.dto.AuditResult;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.DateRange;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@Service("auditService")
public final class AuditServiceImpl extends ObidosService implements AuditService {
	private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);

	@Autowired private final AuditActions actions = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

 	@Transactional(readOnly=true) @Override
	public AuditResult getAuditRecords(final AuthCredsDTO creds, final  ArrayList<String> actionlist, final ArrayList<String> usernames, final ArrayList<String> objects, final DateRange dateRange, final Integer offset, final Integer count, final ArrayList<OrderBy> orderby) throws ServerSideException {
		return userOrAdminFunction(creds, "get audit records", "getAuditRecords", true, caller -> actions.getAuditRecords(caller, actionlist, usernames, objects, dateRange, offset, count, orderby));
	}

 	@Transactional(readOnly=true) @Override
	public AuditReport getAuditReport(final AuthCredsDTO creds) throws ServerSideException {
		return adminFunction(creds, "get an audit report", "getAuditReport", false, actions::getAuditReport);
	}
}
