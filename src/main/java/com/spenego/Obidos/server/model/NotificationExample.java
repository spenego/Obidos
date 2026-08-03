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

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public final class NotificationExample extends ObidosExample<NotificationExample.Criteria> {
	public NotificationExample() {
		oredCriteria = new ArrayList<Criteria>();
	}

	public NotificationExample(final Consumer<Criteria> c) {
		super(c);
	}

	@Override
	protected Criteria createCriteriaInternal() {
		return new Criteria(sb);
	}

	public static class Criteria extends ObidosCriteria<Criteria> {
		Criteria(final StringBuilder sb) {
			super(sb);
		}

		@Override
		protected String colPrefix() {
			return "n.";
		}

		public Criteria andOwnerIdIsNull() {
			return addCriterion("owner_id is null");
		}

		public Criteria andOwnerIdIsNotNull() {
			return addCriterion("owner_id is not null");
		}

		public Criteria andOwnerIdEqualTo(Long value) {
			return addCriterion("owner_id =", value);
		}

		public Criteria andOwnerIdNotEqualTo(Long value) {
			return addCriterion("owner_id <>", value);
		}

		public Criteria andOwnerIdIn(Collection<Long> values) {
			return addCriterion("owner_id in", values);
		}

		public Criteria andOwnerIdNotIn(Collection<Long> values) {
			return addCriterion("owner_id not in", values);
		}

		public Criteria andTargetIdEqualTo(Long value) {
			return addCriterion("target_id =", value);
		}

		public Criteria andActionEqualTo(Integer value) {
			return addCriterion("action =", value);
		}

		public Criteria andActionNotEqualTo(Integer value) {
			return addCriterion("action <>", value);
		}

		public Criteria andActionIn(Collection<Integer> values) {
			return addCriterion("action in", values);
		}

		public Criteria andActionNotIn(Collection<Integer> values) {
			return addCriterion("action not in", values);
		}

		public Criteria andMessageIsNull() {
			return addCriterion("message is null");
		}

		public Criteria andMessageIsNotNull() {
			return addCriterion("message is not null");
		}

		public Criteria andMessageEqualTo(String value) {
			return addCriterion("message =", value);
		}

		public Criteria andMessageNotEqualTo(String value) {
			return addCriterion("message <>", value);
		}

		public Criteria andMessageLike(String value) {
			return addLikeCriterion("message like", value);
		}

		public Criteria andMessageNotLike(String value) {
			return addLikeCriterion("message not like", value);
		}

		public Criteria andUnreadEqualTo(final Boolean value) {
			return addCriterion("unread =", value);
		}

		public Criteria andUnreadNotEqualTo(Boolean value) {
			return addCriterion("unread <>", value);
		}
	}
}
