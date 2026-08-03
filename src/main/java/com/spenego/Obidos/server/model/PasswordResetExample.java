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

public final class PasswordResetExample extends ObidosExample<PasswordResetExample.Criteria> {
	public PasswordResetExample() { }

	public PasswordResetExample(final Consumer<Criteria> c) {
		super(c);
	}

	public PasswordResetExample(final String orderByClause) {
		super(orderByClause);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria();
	}

	public static class Criteria extends ObidosCriteria<Criteria> {
		public Criteria andStateIsNull() {
			return addCriterion("state is null");
		}

		public Criteria andStateIsNotNull() {
			return addCriterion("state is not null");
		}

		public Criteria andStateEqualTo(Byte value) {
			return addCriterion("state =", value);
		}

		public Criteria andStateNotEqualTo(Byte value) {
			return addCriterion("state <>", value);
		}

		public Criteria andStateGreaterThan(Byte value) {
			return addCriterion("state >", value);
		}

		public Criteria andStateGreaterThanOrEqualTo(Byte value) {
			return addCriterion("state >=", value);
		}

		public Criteria andStateLessThan(Byte value) {
			return addCriterion("state <", value);
		}

		public Criteria andStateLessThanOrEqualTo(Byte value) {
			return addCriterion("state <=", value);
		}

		public Criteria andStateIn(Collection<Byte> values) {
			return addCriterion("state in", values);
		}

		public Criteria andStateNotIn(Collection<Byte> values) {
			return addCriterion("state not in", values);
		}

		public Criteria andStateBetween(Byte value1, Byte value2) {
			return addCriterion("state between", value1, value2, "state");
		}

		public Criteria andStateNotBetween(Byte value1, Byte value2) {
			return addCriterion("state not between", value1, value2, "state");
		}

		public Criteria andTokenIsNull() {
			return addCriterion("token is null");
		}

		public Criteria andTokenIsNotNull() {
			return addCriterion("token is not null");
		}

		public Criteria andTokenEqualTo(String value) {
			return addCriterion("token =", value);
		}

		public Criteria andTokenNotEqualTo(String value) {
			return addCriterion("token <>", value);
		}

		public Criteria andTokenLike(String value) {
			return addLikeCriterion("token like", value);
		}

		public Criteria andTokenNotLike(String value) {
			return addLikeCriterion("token not like", value);
		}

		public Criteria andEmailAddressIsNull() {
			return addCriterion("email_address is null");
		}

		public Criteria andEmailAddressIsNotNull() {
			return addCriterion("email_address is not null");
		}

		public Criteria andEmailAddressEqualTo(String value) {
			return addCriterion("email_address =", value);
		}

		public Criteria andEmailAddressNotEqualTo(String value) {
			return addCriterion("email_address <>", value);
		}

		public Criteria andEmailAddressLike(String value) {
			return addLikeCriterion("email_address like", value);
		}

		public Criteria andEmailAddressNotLike(String value) {
			return addLikeCriterion("email_address not like", value);
		}

		public Criteria andEmailAddressIn(Collection<String> values) {
			return addCriterion("email_address in", values);
		}

		public Criteria andEmailAddressNotIn(Collection<String> values) {
			return addCriterion("email_address not in", values);
		}
	}
}
