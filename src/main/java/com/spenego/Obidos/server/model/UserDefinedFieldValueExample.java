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

public final class UserDefinedFieldValueExample extends ObidosExample<UserDefinedFieldValueExample.Criteria> {
	public UserDefinedFieldValueExample() { }

	public UserDefinedFieldValueExample(final Consumer<Criteria> c) {
		super(c);
	}

	public UserDefinedFieldValueExample(final String orderByClause) {
		super(orderByClause);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria();
	}

	public static class Criteria extends ObidosCriteria<Criteria> {
		@Override
		public Criteria andUserIdEqualTo(final Long value) {
			return addCriterion("udtv.user_id =", value);
		}

		public Criteria andItemIdEqualTo(final Long value) {
			return addCriterion("i.id =", value);
		}

		@Override
		protected String colPrefix() {
			return "udfv.";
		}

		public Criteria andUserDefinedTypeValueIdIsNull() {
			return addCriterion("user_defined_type_value_id is null");
		}

		public Criteria andUserDefinedTypeValueIdIsNotNull() {
			return addCriterion("user_defined_type_value_id is not null");
		}

		public Criteria andUserDefinedTypeValueIdEqualTo(Long value) {
			return addCriterion("user_defined_type_value_id =", value);
		}

		public Criteria andUserDefinedTypeValueIdNotEqualTo(Long value) {
			return addCriterion("user_defined_type_value_id <>", value);
		}

		public Criteria andUserDefinedTypeValueIdIn(Collection<Long> values) {
			return addCriterion("user_defined_type_value_id in", values);
		}

		public Criteria andUserDefinedTypeValueIdNotIn(Collection<Long> values) {
			return addCriterion("user_defined_type_value_id not in", values);
		}

		public Criteria andUserDefinedFieldIdIsNull() {
			return addCriterion("user_defined_field_id is null");
		}

		public Criteria andUserDefinedFieldIdIsNotNull() {
			return addCriterion("user_defined_field_id is not null");
		}

		public Criteria andUserDefinedFieldIdEqualTo(Long value) {
			return addCriterion("user_defined_field_id =", value);
		}

		public Criteria andUserDefinedFieldIdNotEqualTo(Long value) {
			return addCriterion("user_defined_field_id <>", value);
		}

		public Criteria andUserDefinedFieldIdIn(Collection<Long> values) {
			return addCriterion("user_defined_field_id in", values);
		}

		public Criteria andUserDefinedFieldIdNotIn(Collection<Long> values) {
			return addCriterion("user_defined_field_id not in", values);
		}

		public Criteria andStringValueIdIsNull() {
			return addCriterion("string_value_id is null");
		}

		public Criteria andStringValueIdIsNotNull() {
			return addCriterion("string_value_id is not null");
		}

		public Criteria andStringValueIdEqualTo(Long value) {
			return addCriterion("string_value_id =", value);
		}

		public Criteria andStringValueIdNotEqualTo(Long value) {
			return addCriterion("string_value_id <>", value);
		}

		public Criteria andStringValueIdIn(Collection<Long> values) {
			return addCriterion("string_value_id in", values);
		}

		public Criteria andStringValueIdNotIn(Collection<Long> values) {
			return addCriterion("string_value_id not in", values);
		}

		public Criteria andBlobValueIdIsNull() {
			return addCriterion("blob_value_id is null");
		}

		public Criteria andBlobValueIdIsNotNull() {
			return addCriterion("blob_value_id is not null");
		}

		public Criteria andBlobValueIdEqualTo(Long value) {
			return addCriterion("blob_value_id =", value);
		}

		public Criteria andBlobValueIdNotEqualTo(Long value) {
			return addCriterion("blob_value_id <>", value);
		}

		public Criteria andBlobValueIdIn(Collection<Long> values) {
			return addCriterion("blob_value_id in", values);
		}

		public Criteria andBlobValueIdNotIn(Collection<Long> values) {
			return addCriterion("blob_value_id not in", values);
		}
	}
}
