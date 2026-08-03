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

public final class ItemExample extends ObidosExample<ItemExample.Criteria> {
	public ItemExample() { }

	public ItemExample(final String orderByClause) {
		super(orderByClause);
	}

	public ItemExample(final Consumer<Criteria> c) {
		super(c);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria(sb);
	}

	public static class Criteria extends NamedCriteria<Criteria> {
		Criteria(final StringBuilder sb) {
			super(sb);
		}

		@Override
		protected String colPrefix() {
			return "items.";
		}

		public Criteria andContainerAssignmentIdIsNull() {
			return addCriterion("container_assignment_id is null");
		}

		public Criteria andContainerAssignmentIdIsNotNull() {
			return addCriterion("container_assignment_id is not null");
		}

		public Criteria andContainerAssignmentIdEqualTo(Long value) {
			return addCriterion("container_assignment_id =", value);
		}

		public Criteria andContainerAssignmentIdNotEqualTo(Long value) {
			return addCriterion("container_assignment_id <>", value);
		}

		@Override
		public Criteria andContainerAssignmentIdIn(Collection<Long> values) {
			return addCriterion("container_assignment_id in", values);
		}

		public Criteria andContainerAssignmentIdNotIn(Collection<Long> values) {
			return addCriterion("container_assignment_id not in", values);
		}

		public Criteria andShareableIsNull() {
			return addCriterion("shareable is null");
		}

		public Criteria andShareableIsNotNull() {
			return addCriterion("shareable is not null");
		}

		public Criteria andShareableEqualTo(Boolean value) {
			return addCriterion("shareable =", value);
		}

		public Criteria andShareableNotEqualTo(Boolean value) {
			return addCriterion("shareable <>", value);
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
