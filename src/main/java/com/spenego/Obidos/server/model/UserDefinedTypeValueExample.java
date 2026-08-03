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

public final class UserDefinedTypeValueExample extends ObidosExample<UserDefinedTypeValueExample.Criteria> {
	public UserDefinedTypeValueExample() { }

	public UserDefinedTypeValueExample(final Consumer<Criteria> c) {
		super(c);
	}

	public UserDefinedTypeValueExample(final String orderByClause) {
		super(orderByClause);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria();
	}

	public static class Criteria extends ObidosCriteria<Criteria> {
		@Override
		protected String colPrefix() {
			return "udtv.";
		}

		public Criteria andUserDefinedTypeIdIsNull()				{ return addCriterion("user_defined_type_id is null"); }
		public Criteria andUserDefinedTypeIdIsNotNull()				{ return addCriterion("user_defined_type_id is not null"); }
		public Criteria andUserDefinedTypeIdEqualTo(final Long value) { return addCriterion("user_defined_type_id =", value); }
		public Criteria andUserDefinedTypeIdNotEqualTo(Long value)	{ return addCriterion("user_defined_type_id <>", value); }
		public Criteria andUserDefinedTypeIdIn(Collection<Long> values) { return addCriterion("user_defined_type_id in", values); }
		public Criteria andUserDefinedTypeIdNotIn(Collection<Long> values) { return addCriterion("user_defined_type_id not in", values); }
		public Criteria andItemOwnerIdEqualTo(final Long value)		{ return addCriterion("ia.user_id =", value); }
		public Criteria andItemOwnerIdNotEqualTo(final Long value)	{ return addCriterion("ia.user_id <>", value); }
		public Criteria andItemIdEqualTo(Long value)				{ return addCriterion("ia.item_id =", value); }
		public Criteria andItemsInContainer(Long value) {
			return addCriterion("item_id in (select i.id from items i join container_assignments ca on ca.id = i.container_assignment_id join containers c on c.id = ca.container_id where c.id = " + value + ")");
		}
	}
}
