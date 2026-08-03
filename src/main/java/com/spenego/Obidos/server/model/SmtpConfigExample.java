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

public final class SmtpConfigExample extends ObidosExample<SmtpConfigExample.Criteria> {
	public SmtpConfigExample() { }

	public SmtpConfigExample(final Consumer<Criteria> c) {
		super(c);
	}

	public SmtpConfigExample(final String orderByClause) {
		super(orderByClause);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria(sb);
	}

	public static class Criteria extends NamedCriteria<Criteria> {
		Criteria(final StringBuilder sb) {
			super(sb);
		}

		public Criteria andSmtpServerIsNull() {
			return addCriterion("smtp_server is null");
		}

		public Criteria andSmtpServerIsNotNull() {
			return addCriterion("smtp_server is not null");
		}

		public Criteria andSmtpServerEqualTo(String value) {
			return addCriterion("smtp_server =", value);
		}

		public Criteria andSmtpServerNotEqualTo(final String value) {
			return addCriterion("smtp_server <>", value);
		}

		public Criteria andSmtpServerLike(final String value) {
			return addLikeCriterion("smtp_server like", value);
		}

		public Criteria andSmtpServerNotLike(final String value) {
			return addLikeCriterion("smtp_server not like", value);
		}

		public Criteria andSmtpServerIn(final Collection<String> values) {
			return addCriterion("smtp_server in", values);
		}

		public Criteria andSmtpServerNotIn(final Collection<String> values) {
			return addCriterion("smtp_server not in", values);
		}

		public Criteria andSmtpPortIsNull() {
			return addCriterion("smtp_port is null");
		}

		public Criteria andSmtpPortIsNotNull() {
			return addCriterion("smtp_port is not null");
		}

		public Criteria andSmtpPortEqualTo(Integer value) {
			return addCriterion("smtp_port =", value);
		}

		public Criteria andSmtpPortNotEqualTo(Integer value) {
			return addCriterion("smtp_port <>", value);
		}

		public Criteria andSmtpPortGreaterThan(Integer value) {
			return addCriterion("smtp_port >", value);
		}

		public Criteria andSmtpPortGreaterThanOrEqualTo(Integer value) {
			return addCriterion("smtp_port >=", value);
		}

		public Criteria andSmtpPortLessThan(Integer value) {
			return addCriterion("smtp_port <", value);
		}

		public Criteria andSmtpPortLessThanOrEqualTo(Integer value) {
			return addCriterion("smtp_port <=", value);
		}

		public Criteria andSmtpPortIn(Collection<Integer> values) {
			return addCriterion("smtp_port in", values);
		}

		public Criteria andSmtpPortNotIn(Collection<Integer> values) {
			return addCriterion("smtp_port not in", values);
		}

		public Criteria andSmtpPortBetween(Integer value1, Integer value2) {
			return addCriterion("smtp_port between", value1, value2, "smtpPort");
		}

		public Criteria andSmtpPortNotBetween(Integer value1, Integer value2) {
			return addCriterion("smtp_port not between", value1, value2, "smtpPort");
		}

		public Criteria andUseAuthenticationIsNull() {
			return addCriterion("use_authentication is null");
		}

		public Criteria andUseAuthenticationIsNotNull() {
			return addCriterion("use_authentication is not null");
		}

		public Criteria andUseAuthenticationEqualTo(Boolean value) {
			return addCriterion("use_authentication =", value);
		}

		public Criteria andUseAuthenticationNotEqualTo(Boolean value) {
			return addCriterion("use_authentication <>", value);
		}

		public Criteria andUseSslIsNull() {
			return addCriterion("use_ssl is null");
		}

		public Criteria andUseSslIsNotNull() {
			return addCriterion("use_ssl is not null");
		}

		public Criteria andUseSslEqualTo(Boolean value) {
			return addCriterion("use_ssl =", value);
		}

		public Criteria andUseSslNotEqualTo(Boolean value) {
			return addCriterion("use_ssl <>", value);
		}

		public Criteria andUseStartTlsIsNull() {
			return addCriterion("use_start_tls is null");
		}

		public Criteria andUseStartTlsIsNotNull() {
			return addCriterion("use_start_tls is not null");
		}

		public Criteria andUseStartTlsEqualTo(Boolean value) {
			return addCriterion("use_start_tls =", value);
		}

		public Criteria andUseStartTlsNotEqualTo(Boolean value) {
			return addCriterion("use_start_tls <>", value);
		}

		public Criteria andToAddressIsNull() {
			return addCriterion("to_address is null");
		}

		public Criteria andToAddressIsNotNull() {
			return addCriterion("to_address is not null");
		}

		public Criteria andToAddressEqualTo(String value) {
			return addCriterion("to_address =", value);
		}

		public Criteria andToAddressNotEqualTo(String value) {
			return addCriterion("to_address <>", value);
		}

		public Criteria andToAddressGreaterThan(String value) {
			return addCriterion("to_address >", value);
		}

		public Criteria andToAddressGreaterThanOrEqualTo(String value) {
			return addCriterion("to_address >=", value);
		}

		public Criteria andToAddressLessThan(String value) {
			return addCriterion("to_address <", value);
		}

		public Criteria andToAddressLessThanOrEqualTo(String value) {
			return addCriterion("to_address <=", value);
		}

		public Criteria andToAddressLike(String value) {
			return addLikeCriterion("to_address like", value);
		}

		public Criteria andToAddressNotLike(String value) {
			return addLikeCriterion("to_address not like", value);
		}

		public Criteria andToAddressIn(Collection<String> values) {
			return addCriterion("to_address in", values);
		}

		public Criteria andToAddressNotIn(Collection<String> values) {
			return addCriterion("to_address not in", values);
		}

		public Criteria andToAddressBetween(String value1, String value2) {
			return addCriterion("to_address between", value1, value2, "toAddress");
		}

		public Criteria andToAddressNotBetween(String value1, String value2) {
			return addCriterion("to_address not between", value1, value2, "toAddress");
		}

		public Criteria andFromAddressIsNull() {
			return addCriterion("from_address is null");
		}

		public Criteria andFromAddressIsNotNull() {
			return addCriterion("from_address is not null");
		}

		public Criteria andFromAddressEqualTo(String value) {
			return addCriterion("from_address =", value);
		}

		public Criteria andFromAddressNotEqualTo(String value) {
			return addCriterion("from_address <>", value);
		}

		public Criteria andFromAddressGreaterThan(String value) {
			return addCriterion("from_address >", value);
		}

		public Criteria andFromAddressGreaterThanOrEqualTo(String value) {
			return addCriterion("from_address >=", value);
		}

		public Criteria andFromAddressLessThan(String value) {
			return addCriterion("from_address <", value);
		}

		public Criteria andFromAddressLessThanOrEqualTo(String value) {
			return addCriterion("from_address <=", value);
		}

		public Criteria andFromAddressLike(String value) {
			return addLikeCriterion("from_address like", value);
		}

		public Criteria andFromAddressNotLike(String value) {
			return addLikeCriterion("from_address not like", value);
		}

		public Criteria andSmtpUsernameIsNull() {
			return addCriterion("smtp_username is null");
		}

		public Criteria andSmtpUsernameIsNotNull() {
			return addCriterion("smtp_username is not null");
		}

		public Criteria andSmtpUsernameEqualTo(String value) {
			return addCriterion("smtp_username =", value);
		}

		public Criteria andSmtpUsernameNotEqualTo(String value) {
			return addCriterion("smtp_username <>", value);
		}

		public Criteria andSmtpUsernameLike(String value) {
			return addLikeCriterion("smtp_username like", value);
		}

		public Criteria andSmtpUsernameNotLike(String value) {
			return addLikeCriterion("smtp_username not like", value);
		}

		public Criteria andSmtpUsernameIn(Collection<String> values) {
			return addCriterion("smtp_username in", values);
		}

		public Criteria andSmtpUsernameNotIn(Collection<String> values) {
			return addCriterion("smtp_username not in", values);
		}

		public Criteria andSmtpPasswordIsNull() {
			return addCriterion("smtp_password is null");
		}

		public Criteria andSmtpPasswordIsNotNull() {
			return addCriterion("smtp_password is not null");
		}

		public Criteria andSmtpPasswordEqualTo(String value) {
			return addCriterion("smtp_password =", value);
		}

		public Criteria andSmtpPasswordNotEqualTo(String value) {
			return addCriterion("smtp_password <>", value);
		}
	}
}
