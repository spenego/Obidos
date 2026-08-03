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

package com.spenego.Obidos.server.model;

import java.util.Collection;
import java.util.function.Consumer;

public final class ItemAssignmentExample extends ObidosExample<ItemAssignmentExample.Criteria> {
	public ItemAssignmentExample() { }

	public ItemAssignmentExample(final String orderByClause, int initialStringBuilderSize) {
		super(orderByClause, initialStringBuilderSize);
	}

	public ItemAssignmentExample(final Consumer<Criteria> c) {
		super(c);
	}

	public ItemAssignmentExample(final String orderByClause) {
		super(orderByClause);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria();
	}

	public static class Criteria extends ObidosCriteria<Criteria> {
		@Override
		protected String colPrefix() {
			return "ia.";
		}

		public Criteria andDocumentIdEqualTo(final Long documentId) {
			return addCriterion("d.id = " + documentId);
		}

		public Criteria andItemGroupIdEqualTo(final Long id) {
			return addCriterion("ia.item_id in (select igx.item_id from item_groups igx where igx.group_id = " + id + ")");
		}

		@Override
		public Criteria andGroupIdEqualTo(final Long id) { return addCriterion("gm.group_id =", id); }

		@Override
		public Criteria andGroupIdNotEqualTo(final Long value)		{ return addCriterion("gm.group_id <>", value); }
		public Criteria andCountGreaterThan(final Integer value)	{ return addCriterion("count >", value); }
		public Criteria andCountEqualTo(final Integer value)		{ return addCriterion("count =", value); }
		public Criteria andItemIdIsNull()							{ return addCriterion("ia.item_id is null"); }
		public Criteria andItemIdIsNotNull()						{ return addCriterion("ia.item_id is not null"); }
		public Criteria andItemIdEqualTo(final Long value)			{ return addCriterion("ia.item_id =", value); }
		public Criteria andItemIdNotEqualTo(final Long value)		{ return addCriterion("ia.item_id <>", value); }
		public Criteria andItemIdIn(final Collection<Long> values)	{ return addCriterion("ia.item_id in", values); }
		public Criteria andItemIdNotIn(final Collection<Long> values) { return addCriterion("ia.item_id not in", values); }

		public Criteria andItemsInContainer(final Long containerId) {
			return addCriterion("ia.item_id in (select i.id from items i join container_assignments ca on ca.id = i.container_assignment_id join containers c on c.id = ca.container_id where c.id = " + containerId + ")");
		}

		public Criteria andIAComboItemIdEqualTo(final Long callerId, final Long itemId, final String searchString) {
			final String preppedSearchString = searchString == null || searchString.isEmpty() ? null : prepSearchString(searchString);
			final StringBuilder sb = initStringBuilder(1024);

			sb.append(" ig.item_id = ").append(itemId);

			if (preppedSearchString != null) {
				sb.append(" and g.name like \"").append(preppedSearchString).append("\"");
			}

			sb.append(") union");
			sb.append("  select u.id as id, u.fullname as name, 'u' as type, u.email1 as email1, u.office as office, u.preference_flags & 0x4 as profile_pic_enabled");
			sb.append(" from item_assignments ia");
			sb.append("   join users u on u.id = ia.user_id");
			sb.append("   where (ia.item_id = ").append(itemId);
			sb.append("    and u.id <> ").append(callerId);
			if (preppedSearchString != null) {
				sb.append(" and (u.fullname like \"").append(preppedSearchString).append("\" or u.email1 like \"").append(preppedSearchString).append("\")" );
			}
			sb.append(" and ia.count > (select count(*) from item_groups ig join group_members gm on gm.group_id = ig.group_id join users u on u.id = gm.user_id where gm.user_id = ia.user_id and ig.item_id = ia.item_id)");

			return addCriterion(sb.toString());
		}
	}
}
