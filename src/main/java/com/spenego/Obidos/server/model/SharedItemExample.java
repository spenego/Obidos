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
import java.util.Date;
import java.util.function.Consumer;

public final class SharedItemExample extends ObidosExample<SharedItemExample.Criteria> {
	public SharedItemExample() { }

	public SharedItemExample(final String orderByClause) {
		super(orderByClause);
	}

	public SharedItemExample(final Consumer<Criteria> c) {
		super(c);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria(sb);
	}

	public static class Criteria extends ObidosCriteria<Criteria> {
		Criteria(final StringBuilder sb) {
			super(sb);
		}

		@Override
		protected String colPrefix() {
			return "i.";
		}

		public Criteria andShareableEqualTo(final Boolean value)	{ return addCriterion("i.shareable =", value); }
		public Criteria andOwnerIdIsNull()							{ return addCriterion("i.user_id is null"); }
		public Criteria andOwnerIdIsNotNull()						{ return addCriterion("i.user_id is not null"); }
		public Criteria andOwnerIdEqualTo(final Long value)			{ return addCriterion("i.user_id =", value); }
		public Criteria andOwnerIdNotEqualTo(final Long value)		{ return addCriterion("i.user_id <>", value); }
		public Criteria andOwnerIdIn(Collection<Long> values)		{ return addCriterion("i.user_id in", values); }
		public Criteria andOwnerIdNotIn(Collection<Long> values)	{ return addCriterion("i.user_id not in", values); }
		public Criteria andNameLike(final String value)				{ return addLikeCriterion("i.name like", value); }
		public Criteria andNameNotLike(String value)				{ return addLikeCriterion("i.name not like", value); }
		public Criteria andDeletedIsNull()							{ return addCriterion("deleted is null"); }
		public Criteria andDeletedIsNotNull()						{ return addCriterion("deleted is not null"); }
		@Override
		public Criteria andDeletedEqualTo(Boolean value)			{ return addCriterion("deleted =", value); }
		public Criteria andDeletedNotEqualTo(Boolean value)			{ return addCriterion("deleted <>", value); }
		@Override
		public Criteria andUserIdIsNull()							{ return addCriterion("ia.user_id is null"); }
		@Override
		public Criteria andUserIdIsNotNull()						{ return addCriterion("ia.user_id is not null"); }
		@Override
		public Criteria andUserIdEqualTo(Long value)				{ return addCriterion("ia.user_id =", value); }
		@Override
		public Criteria andUserIdNotEqualTo(final Long value)		{ return addCriterion("ia.user_id <>", value); }
		@Override
		public Criteria andUserIdIn(final Collection<Long> values)	{ return addCriterion("ia.user_id in", values); }
		@Override
		public Criteria andUserIdNotIn(final Collection<Long> values) { return addCriterion("ia.user_id not in", values); }
		@Override
		public Criteria andGroupIdEqualTo(final Long value)			{ return addCriterion("ig.group_id =", value); }
		public Criteria andItemIdIsNull()							{ return addCriterion("i.id is null"); }
		public Criteria andItemIdIsNotNull()						{ return addCriterion("i.id is not null"); }
		public Criteria andItemIdEqualTo(final Long value)			{ return addCriterion("ia.item_id =", value); }
		public Criteria andItemIdNotEqualTo(Long value)				{ return addCriterion("i.id <>", value); }
		public Criteria andItemIdIn(final Collection<Long> values)	{ return addCriterion("i.id in", values); }
		public Criteria andSharedIsNull()							{ return addCriterion("i.shared is null"); }
		public Criteria andSharedIsNotNull()						{ return addCriterion("i.shared is not null"); }
		public Criteria andSharedEqualTo(final Boolean value)		{ return addCriterion("i.shared =", value); }
		public Criteria andSharedNotEqualTo(final Boolean value)	{ return addCriterion("i.shared <>", value); }
		@Override
		public Criteria andContainerIdEqualTo(final Long id)		{ return addCriterion("ca.container_id =", id); }
		@Override
		public Criteria andContainerIdNotEqualTo(final Long id)		{ return addCriterion("ca.container_id !=", id); }

		public Criteria andItemIdNotIn(final Collection<Long> values)		{ return addCriterion("i.id not in", values); }
		public Criteria andContainerAssignmentIdEqualTo(final Long id)		{ return addCriterion("i.container_assignment_id =", id); }
		public Criteria andContainerAssignmentIdNotEqualTo(final Long id)	{ return addCriterion("i.container_assignment_id !=", id); }
		public Criteria andShareExpiresAtGreaterThan(final Date value)		{ return addCriterion(new ObidosCriterion("shares_expire_at >", value, "shares_expire_at is null")); }
	}
}
