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

public final class AuditExample extends ObidosExample<AuditExample.Criteria> {
	public AuditExample() { }

	public AuditExample(final Consumer<Criteria> c) {
		super(c);
	}

	public AuditExample(final String orderByClause) {
		super(orderByClause);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria();
	}

	public static class Criteria extends ObidosCriteria<Criteria> {
		public Criteria andActionIsNull()							{ return addCriterion("action is null"); }
		public Criteria andActionIsNotNull()						{ return addCriterion("action is not null"); }
		public Criteria andActionEqualTo(Byte value)				{ return addCriterion("action =", value); }
		public Criteria andActionNotEqualTo(Byte value)				{ return addCriterion("action <>", value); }
		public Criteria andActionIn(Collection<Integer> values) 	{ return addCriterion("action in", values); }
		public Criteria andActionNotIn(Collection<Integer> values)	{ return addCriterion("action not in", values); }
		public Criteria andUserOrRecipientIdEqualTo(Long value)		{ return addCriterion("(user_id = " + value + " or recipient_id = " + value + ")"); }
		public Criteria andObjectIdIsNull()							{ return addCriterion("object_id is null"); }
		public Criteria andObjectIdIsNotNull()						{ return addCriterion("object_id is not null"); }
		public Criteria andObjectIdEqualTo(Long value)				{ return addCriterion("object_id =", value); }
		public Criteria andObjectIdNotEqualTo(Long value)			{ return addCriterion("object_id <>", value); }
		public Criteria andObjectIdIn(Collection<Long> values)		{ return addCriterion("object_id in", values); }
		public Criteria andObjectIdNotIn(Collection<Long> values)	{ return addCriterion("object_id not in", values); }
		public Criteria andNewItemIdIsNull()						{ return addCriterion("new_item_id is null"); }
		public Criteria andNewItemIdIsNotNull()						{ return addCriterion("new_item_id is not null"); }
		public Criteria andNewItemIdEqualTo(Long value)				{ return addCriterion("new_item_id =", value); }
		public Criteria andNewItemIdNotEqualTo(Long value)			{ return addCriterion("new_item_id <>", value); }
		public Criteria andNewItemIdIn(Collection<Long> values)		{ return addCriterion("new_item_id in", values); }
		public Criteria andNewItemIdNotIn(Collection<Long> values)	{ return addCriterion("new_item_id not in", values); }
		@Override
		public Criteria andUsernameLike(String value)				{ return addLikeCriterion("username like", value); }
		public Criteria andUsernameNotLike(String value)			{ return addLikeCriterion("username not like", value); }
		public Criteria andUsernameIn(Collection<String> values)	{ return addCriterion("username in", values); }
		public Criteria andUsernameNotIn(Collection<String> values)	{ return addCriterion("username not in", values); }
		public Criteria andUsernameBetween(String value1, String value2) { return addCriterion("username between", value1, value2, "username"); }
		public Criteria andUsernameNotBetween(String value1, String value2) { return addCriterion("username not between", value1, value2, "username"); }
		public Criteria andObjectNameIsNull()						{ return addCriterion("object_name is null"); }
		public Criteria andObjectNameIsNotNull()					{ return addCriterion("object_name is not null"); }
		public Criteria andObjectNameEqualTo(String value)			{ return addCriterion("object_name =", value); }
		public Criteria andObjectNameNotEqualTo(String value)		{ return addCriterion("object_name <>", value); }
		public Criteria andObjectNameGreaterThan(String value)		{ return addCriterion("object_name >", value); }
		public Criteria andObjectNameGreaterThanOrEqualTo(String value) { return addCriterion("object_name >=", value); }
		public Criteria andObjectNameLessThan(String value)			{ return addCriterion("object_name <", value); }
		public Criteria andObjectNameLessThanOrEqualTo(String value){ return addCriterion("object_name <=", value); }
		public Criteria andObjectNameLike(String value)				{ return addCriterion("object_name like", value); }
		public Criteria andObjectNameNotLike(String value)			{ return addCriterion("object_name not like", value); }
		public Criteria andObjectNameIn(Collection<String> values)	{ return addCriterion("object_name in", values); }
		public Criteria andObjectNameNotIn(Collection<String> values)			{ return addCriterion("object_name not in", values); }
		public Criteria andObjectNameBetween(String value1, String value2)		{ return addCriterion("object_name between", value1, value2, "objectName"); }
		public Criteria andObjectNameNotBetween(String value1, String value2)	{ return addCriterion("object_name not between", value1, value2, "objectName"); }
		public Criteria andDetailsIsNull()							{ return addCriterion("details is null"); }
		public Criteria andDetailsIsNotNull()						{ return addCriterion("details is not null"); }
		public Criteria andDetailsEqualTo(String value)				{ return addCriterion("details =", value); }
		public Criteria andDetailsNotEqualTo(String value)			{ return addCriterion("details <>", value); }
		public Criteria andDetailsLike(String value)				{ return addLikeCriterion("details like", value); }
		public Criteria andDetailsNotLike(String value)				{ return addLikeCriterion("details not like", value); }
		public Criteria andObjectNameOrDetailsIn(final Collection<String> values) { return (values == null || values.isEmpty()) ? this : andLikeAny(values, "object_name", "details"); }
		@Override
		public Criteria andCreatedAtBetween(final Date value1, final Date value2) { return addCriterion("a.created_at between", value1, value2, "createdAt"); }
	}
}
