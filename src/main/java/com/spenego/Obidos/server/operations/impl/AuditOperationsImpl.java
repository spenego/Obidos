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

package com.spenego.Obidos.server.operations.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.AuditMapper;
import com.spenego.Obidos.server.model.Audit;
import com.spenego.Obidos.server.model.AuditExample;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.AuditOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.DateRange;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public final class AuditOperationsImpl extends ObidosOperations<Audit> implements AuditOperations {
	private static final Logger	logger = LoggerFactory.getLogger(AuditOperationsImpl.class);

	@Autowired
	private final AuditMapper	auditMapper = null;

	@Override
	protected Logger getLogger()		{ return logger; }
	@Override
	protected AuditMapper getMapper()	{ return auditMapper; }
	@Override
	protected String getModelName()		{ return "audit"; }

	@Override
	protected String createdAtName() {
		return "a_created_at";
	}

	private static void setCriteria(final AuditExample.Criteria criteria, final Collection<Long> userIds, final User caller, final DateRange dateRange) {
		if (caller.isAdmin()) {
			criteria.andUserIdIn(userIds);
		} else {
			criteria.andUserOrRecipientIdEqualTo(caller.getId());
		}

		if (dateRange != null) {
			final Date start = dateRange.getStart();
			final Date end = dateRange.getEnd();

			if (start != null && end != null) {
				final boolean correctOrder = start.before(end);
				criteria.andCreatedAtBetween(correctOrder ? start : end, correctOrder ? end : start);
			}
		}
	}

	private AuditExample generateExample(final User caller, final Collection<Integer> actions, final Collection<Long> userIds, final Collection<String> objects, final DateRange dateRange, final List<OrderBy> orderBy) {
		return createExample(() -> new AuditExample(c -> { c.andActionIn(actions).andObjectNameOrDetailsIn(objects); setCriteria(c, userIds, caller, dateRange); }), orderBy);
	}

	@Override
	public Stream<Audit> getStream(final User caller, final Collection<Integer> actions, final Collection<Long> userIds, final Collection<String> objects, final DateRange dateRange, final Integer offset, final Integer count, final List<OrderBy> orderBy) {
		return auditMapper.selectByExample(generateExample(caller, actions, userIds, objects, dateRange, orderBy), createRowBounds(offset, count)).stream();
	}

	@Override
	public Integer getCount(final User caller, final Collection<Integer> actions, final Collection<Long> userIds, final Collection<String> objects, final DateRange dateRange) {
		return countByExample(generateExample(caller, actions, userIds, objects, dateRange, null));
	}

	// We do not update or delete Audit records
	@Override
	public Void update(final Audit o) throws NoSuchRecordException { return null; }

	@Override
	public Void delete(final Long id) throws NoSuchRecordException { return null; }
}
