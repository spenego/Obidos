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

public final class ContainerExample extends ObidosExample<ContainerExample.Criteria> {
	public ContainerExample() { }

	public ContainerExample(final String orderByClause) {
		super(orderByClause);
	}

	public ContainerExample(final Consumer<Criteria> c) {
		super(c);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria(sb);
	}

	public static class Criteria extends NamedCriteria<Criteria> {
		Criteria(final StringBuilder sb) {
			super(sb);
		}

		public Criteria andParentIdIsNull() {
			return addCriterion("parent_id is null");
		}

		public Criteria andParentIdIsNotNull() {
			return addCriterion("parent_id is not null");
		}

		public Criteria andParentIdEqualTo(Long value) {
			return addCriterion("parent_id =", value);
		}

		public Criteria andParentIdNotEqualTo(Long value) {
			return addCriterion("parent_id <>", value);
		}

		public Criteria andParentIdGreaterThan(Long value) {
			return addCriterion("parent_id >", value);
		}

		public Criteria andParentIdGreaterThanOrEqualTo(Long value) {
			return addCriterion("parent_id >=", value);
		}

		public Criteria andParentIdLessThan(Long value) {
			return addCriterion("parent_id <", value);
		}

		public Criteria andParentIdLessThanOrEqualTo(Long value) {
			return addCriterion("parent_id <=", value);
		}

		public Criteria andParentIdIn(Collection<Long> values) {
			return addCriterion("parent_id in", values);
		}

		public Criteria andParentIdNotIn(Collection<Long> values) {
			return addCriterion("parent_id not in", values);
		}

		public Criteria andParentIdBetween(Long value1, Long value2) {
			return addCriterion("parent_id between", value1, value2, "parentId");
		}

		public Criteria andParentIdNotBetween(Long value1, Long value2) {
			return addCriterion("parent_id not between", value1, value2, "parentId");
		}

		public Criteria andIsPrivateIsNull() {
			return addCriterion("is_private is null");
		}

		public Criteria andIsPrivateIsNotNull() {
			return addCriterion("is_private is not null");
		}

		public Criteria andIsPrivateEqualTo(Boolean value) {
			return addCriterion("is_private =", value);
		}

		public Criteria andIsPrivateNotEqualTo(Boolean value) {
			return addCriterion("is_private <>", value);
		}

		public Criteria andSharedIsNull() {
			return addCriterion("shared is null");
		}

		public Criteria andSharedIsNotNull() {
			return addCriterion("shared is not null");
		}

		public Criteria andSharedEqualTo(Boolean value) {
			return addCriterion("shared =", value);
		}

		public Criteria andSharedNotEqualTo(Boolean value) {
			return addCriterion("shared <>", value);
		}
	}
}
