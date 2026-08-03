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

public final class UserDefinedFieldExample extends ObidosExample<UserDefinedFieldExample.Criteria> {
	public UserDefinedFieldExample() { }

	public UserDefinedFieldExample(final String orderByClause) {
		super(orderByClause);
	}

	public UserDefinedFieldExample(final Consumer<Criteria> c) {
		super(c);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria(sb);
	}

	public static class Criteria extends NamedCriteria<Criteria> {
		Criteria(final StringBuilder sb) {
			super(sb);
		}

		public Criteria andTypeIdIsNull() {
			return addCriterion("type_id is null");
		}

		public Criteria andTypeIdIsNotNull() {
			return addCriterion("type_id is not null");
		}

		public Criteria andTypeIdEqualTo(Long value) {
			return addCriterion("type_id =", value);
		}

		public Criteria andTypeIdNotEqualTo(Long value) {
			return addCriterion("type_id <>", value);
		}

		public Criteria andTypeIdIn(Collection<Long> values) {
			return addCriterion("type_id in", values);
		}

		public Criteria andTypeIdNotIn(Collection<Long> values) {
			return addCriterion("type_id not in", values);
		}

		public Criteria andTypeIsNull() {
			return addCriterion("type is null");
		}

		public Criteria andTypeIsNotNull() {
			return addCriterion("type is not null");
		}

		public Criteria andTypeEqualTo(Long value) {
			return addCriterion("type =", value);
		}

		public Criteria andTypeNotEqualTo(Long value) {
			return addCriterion("type <>", value);
		}

		public Criteria andTypeGreaterThan(Long value) {
			return addCriterion("type >", value);
		}

		public Criteria andTypeGreaterThanOrEqualTo(Long value) {
			return addCriterion("type >=", value);
		}

		public Criteria andTypeLessThan(Long value) {
			return addCriterion("type <", value);
		}

		public Criteria andTypeLessThanOrEqualTo(Long value) {
			return addCriterion("type <=", value);
		}

		public Criteria andTypeIn(final Collection<Long> values) {
			return addCriterion("type in", values);
		}

		public Criteria andTypeNotIn(final Collection<Long> values) {
			return addCriterion("type not in", values);
		}

		public Criteria andTypeBetween(Long value1, Long value2) {
			return addCriterion("type between", value1, value2, "type");
		}

		public Criteria andTypeNotBetween(Long value1, Long value2) {
			return addCriterion("type not between", value1, value2, "type");
		}
	}
}
