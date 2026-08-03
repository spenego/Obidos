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
import java.util.List;
import java.util.function.Consumer;

public class ComplexityRequirementsExample extends ObidosExample<ComplexityRequirementsExample.Criteria> {
	public ComplexityRequirementsExample() {
		oredCriteria = new ArrayList<>();
	}

	public ComplexityRequirementsExample(final Consumer<Criteria> c) {
		super(c);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria(sb);
	}

	public static class Criteria extends NamedCriteria<Criteria> {
		Criteria(final StringBuilder sb) {
			super(sb);
		}

		public Criteria andTypeIsNull() {
			return addCriterion("type is null");
		}

		public Criteria andTypeIsNotNull() {
			return addCriterion("type is not null");
		}

		public Criteria andTypeEqualTo(Byte value) {
			return addCriterion("type =", value);
		}

		public Criteria andTypeNotEqualTo(Byte value) {
			return addCriterion("type <>", value);
		}

		public Criteria andTypeIn(List<Byte> values) {
			return addCriterion("type in", values);
		}

		public Criteria andTypeNotIn(List<Byte> values) {
			return addCriterion("type not in", values);
		}

		public Criteria andMinimumLengthIsNull() {
			return addCriterion("minimum_length is null");
		}

		public Criteria andMinimumLengthIsNotNull() {
			return addCriterion("minimum_length is not null");
		}

		public Criteria andMinimumLengthEqualTo(Integer value) {
			return addCriterion("minimum_length =", value);
		}

		public Criteria andMinimumLengthNotEqualTo(Integer value) {
			return addCriterion("minimum_length <>", value);
		}

		public Criteria andMinimumLengthGreaterThan(Integer value) {
			return addCriterion("minimum_length >", value);
		}

		public Criteria andMinimumLengthGreaterThanOrEqualTo(Integer value) {
			return addCriterion("minimum_length >=", value);
		}

		public Criteria andMinimumLengthLessThan(Integer value) {
			return addCriterion("minimum_length <", value);
		}

		public Criteria andMinimumLengthLessThanOrEqualTo(Integer value) {
			return addCriterion("minimum_length <=", value);
		}

		public Criteria andMinimumLengthIn(List<Integer> values) {
			return addCriterion("minimum_length in", values);
		}

		public Criteria andMinimumLengthNotIn(List<Integer> values) {
			return addCriterion("minimum_length not in", values);
		}

		public Criteria andMinimumLengthBetween(Integer value1, Integer value2) {
			return addCriterion("minimum_length between", value1, value2, "minimumLength");
		}

		public Criteria andMinimumLengthNotBetween(Integer value1, Integer value2) {
			return addCriterion("minimum_length not between", value1, value2, "minimumLength");
		}

		public Criteria andMinimumUppercaseIsNull() {
			return addCriterion("minimum_uppercase is null");
		}

		public Criteria andMinimumUppercaseIsNotNull() {
			return addCriterion("minimum_uppercase is not null");
		}

		public Criteria andMinimumUppercaseEqualTo(Integer value) {
			return addCriterion("minimum_uppercase =", value);
		}

		public Criteria andMinimumUppercaseNotEqualTo(Integer value) {
			return addCriterion("minimum_uppercase <>", value);
		}

		public Criteria andMinimumUppercaseGreaterThan(Integer value) {
			return addCriterion("minimum_uppercase >", value);
		}

		public Criteria andMinimumUppercaseGreaterThanOrEqualTo(Integer value) {
			return addCriterion("minimum_uppercase >=", value);
		}

		public Criteria andMinimumUppercaseLessThan(Integer value) {
			return addCriterion("minimum_uppercase <", value);
		}

		public Criteria andMinimumUppercaseLessThanOrEqualTo(Integer value) {
			return addCriterion("minimum_uppercase <=", value);
		}

		public Criteria andMinimumUppercaseIn(List<Integer> values) {
			return addCriterion("minimum_uppercase in", values);
		}

		public Criteria andMinimumUppercaseNotIn(List<Integer> values) {
			return addCriterion("minimum_uppercase not in", values);
		}

		public Criteria andMinimumUppercaseBetween(Integer value1, Integer value2) {
			return addCriterion("minimum_uppercase between", value1, value2, "minimumUppercase");
		}

		public Criteria andMinimumUppercaseNotBetween(Integer value1, Integer value2) {
			return addCriterion("minimum_uppercase not between", value1, value2, "minimumUppercase");
		}

		public Criteria andMinimumLowercaseIsNull() {
			return addCriterion("minimum_lowercase is null");
		}

		public Criteria andMinimumLowercaseIsNotNull() {
			return addCriterion("minimum_lowercase is not null");
		}

		public Criteria andMinimumLowercaseEqualTo(Integer value) {
			return addCriterion("minimum_lowercase =", value);
		}

		public Criteria andMinimumLowercaseNotEqualTo(Integer value) {
			return addCriterion("minimum_lowercase <>", value);
		}

		public Criteria andMinimumLowercaseGreaterThan(Integer value) {
			return addCriterion("minimum_lowercase >", value);
		}

		public Criteria andMinimumLowercaseGreaterThanOrEqualTo(Integer value) {
			return addCriterion("minimum_lowercase >=", value);
		}

		public Criteria andMinimumLowercaseLessThan(Integer value) {
			return addCriterion("minimum_lowercase <", value);
		}

		public Criteria andMinimumLowercaseLessThanOrEqualTo(Integer value) {
			return addCriterion("minimum_lowercase <=", value);
		}

		public Criteria andMinimumLowercaseIn(List<Integer> values) {
			return addCriterion("minimum_lowercase in", values);
		}

		public Criteria andMinimumLowercaseNotIn(List<Integer> values) {
			return addCriterion("minimum_lowercase not in", values);
		}

		public Criteria andMinimumLowercaseBetween(Integer value1, Integer value2) {
			return addCriterion("minimum_lowercase between", value1, value2, "minimumLowercase");
		}

		public Criteria andMinimumLowercaseNotBetween(Integer value1, Integer value2) {
			return addCriterion("minimum_lowercase not between", value1, value2, "minimumLowercase");
		}

		public Criteria andMinimumSpecialIsNull() {
			return addCriterion("minimum_special is null");
		}

		public Criteria andMinimumSpecialIsNotNull() {
			return addCriterion("minimum_special is not null");
		}

		public Criteria andMinimumSpecialEqualTo(Integer value) {
			return addCriterion("minimum_special =", value);
		}

		public Criteria andMinimumSpecialNotEqualTo(Integer value) {
			return addCriterion("minimum_special <>", value);
		}

		public Criteria andMinimumSpecialGreaterThan(Integer value) {
			return addCriterion("minimum_special >", value);
		}

		public Criteria andMinimumSpecialGreaterThanOrEqualTo(Integer value) {
			return addCriterion("minimum_special >=", value);
		}

		public Criteria andMinimumSpecialLessThan(Integer value) {
			return addCriterion("minimum_special <", value);
		}

		public Criteria andMinimumSpecialLessThanOrEqualTo(Integer value) {
			return addCriterion("minimum_special <=", value);
		}

		public Criteria andMinimumSpecialIn(List<Integer> values) {
			return addCriterion("minimum_special in", values);
		}

		public Criteria andMinimumSpecialNotIn(List<Integer> values) {
			return addCriterion("minimum_special not in", values);
		}

		public Criteria andMinimumSpecialBetween(Integer value1, Integer value2) {
			return addCriterion("minimum_special between", value1, value2, "minimumSpecial");
		}

		public Criteria andMinimumSpecialNotBetween(Integer value1, Integer value2) {
			return addCriterion("minimum_special not between", value1, value2, "minimumSpecial");
		}

		public Criteria andMinimumNumbersIsNull() {
			return addCriterion("minimum_numbers is null");
		}

		public Criteria andMinimumNumbersIsNotNull() {
			return addCriterion("minimum_numbers is not null");
		}

		public Criteria andMinimumNumbersEqualTo(Integer value) {
			return addCriterion("minimum_numbers =", value);
		}

		public Criteria andMinimumNumbersNotEqualTo(Integer value) {
			return addCriterion("minimum_numbers <>", value);
		}

		public Criteria andMinimumNumbersGreaterThan(Integer value) {
			return addCriterion("minimum_numbers >", value);
		}

		public Criteria andMinimumNumbersGreaterThanOrEqualTo(Integer value) {
			return addCriterion("minimum_numbers >=", value);
		}

		public Criteria andMinimumNumbersLessThan(Integer value) {
			return addCriterion("minimum_numbers <", value);
		}

		public Criteria andMinimumNumbersLessThanOrEqualTo(Integer value) {
			return addCriterion("minimum_numbers <=", value);
		}

		public Criteria andMinimumNumbersIn(List<Integer> values) {
			return addCriterion("minimum_numbers in", values);
		}

		public Criteria andMinimumNumbersNotIn(List<Integer> values) {
			return addCriterion("minimum_numbers not in", values);
		}

		public Criteria andMinimumNumbersBetween(Integer value1, Integer value2) {
			return addCriterion("minimum_numbers between", value1, value2, "minimumNumbers");
		}

		public Criteria andMinimumNumbersNotBetween(Integer value1, Integer value2) {
			return addCriterion("minimum_numbers not between", value1, value2, "minimumNumbers");
		}

		public Criteria andMinimumEntropyIsNull() {
			return addCriterion("minimum_entropy is null");
		}

		public Criteria andMinimumEntropyIsNotNull() {
			return addCriterion("minimum_entropy is not null");
		}

		public Criteria andMinimumEntropyEqualTo(Integer value) {
			return addCriterion("minimum_entropy =", value);
		}

		public Criteria andMinimumEntropyNotEqualTo(Integer value) {
			return addCriterion("minimum_entropy <>", value);
		}

		public Criteria andMinimumEntropyGreaterThan(Integer value) {
			return addCriterion("minimum_entropy >", value);
		}

		public Criteria andMinimumEntropyGreaterThanOrEqualTo(Integer value) {
			return addCriterion("minimum_entropy >=", value);
		}

		public Criteria andMinimumEntropyLessThan(Integer value) {
			return addCriterion("minimum_entropy <", value);
		}

		public Criteria andMinimumEntropyLessThanOrEqualTo(Integer value) {
			return addCriterion("minimum_entropy <=", value);
		}

		public Criteria andMinimumEntropyIn(List<Integer> values) {
			return addCriterion("minimum_entropy in", values);
		}

		public Criteria andMinimumEntropyNotIn(List<Integer> values) {
			return addCriterion("minimum_entropy not in", values);
		}

		public Criteria andMinimumEntropyBetween(Integer value1, Integer value2) {
			return addCriterion("minimum_entropy between", value1, value2, "minimumEntropy");
		}

		public Criteria andMinimumEntropyNotBetween(Integer value1, Integer value2) {
			return addCriterion("minimum_entropy not between", value1, value2, "minimumEntropy");
		}

		public Criteria andMaxAgeInDaysIsNull() {
			return addCriterion("max_age_in_days is null");
		}

		public Criteria andMaxAgeInDaysIsNotNull() {
			return addCriterion("max_age_in_days is not null");
		}

		public Criteria andMaxAgeInDaysEqualTo(Integer value) {
			return addCriterion("max_age_in_days =", value);
		}

		public Criteria andMaxAgeInDaysNotEqualTo(Integer value) {
			return addCriterion("max_age_in_days <>", value);
		}

		public Criteria andMaxAgeInDaysGreaterThan(Integer value) {
			return addCriterion("max_age_in_days >", value);
		}

		public Criteria andMaxAgeInDaysGreaterThanOrEqualTo(Integer value) {
			return addCriterion("max_age_in_days >=", value);
		}

		public Criteria andMaxAgeInDaysLessThan(Integer value) {
			return addCriterion("max_age_in_days <", value);
		}

		public Criteria andMaxAgeInDaysLessThanOrEqualTo(Integer value) {
			return addCriterion("max_age_in_days <=", value);
		}

		public Criteria andMaxAgeInDaysIn(List<Integer> values) {
			return addCriterion("max_age_in_days in", values);
		}

		public Criteria andMaxAgeInDaysNotIn(List<Integer> values) {
			return addCriterion("max_age_in_days not in", values);
		}

		public Criteria andMaxAgeInDaysBetween(Integer value1, Integer value2) {
			return addCriterion("max_age_in_days between", value1, value2, "maxAgeInDays");
		}

		public Criteria andMaxAgeInDaysNotBetween(Integer value1, Integer value2) {
			return addCriterion("max_age_in_days not between", value1, value2, "maxAgeInDays");
		}

		public Criteria andMinAgeInSecondsBeforeResetIsNull() {
			return addCriterion("min_age_in_seconds_before_reset is null");
		}

		public Criteria andMinAgeInSecondsBeforeResetIsNotNull() {
			return addCriterion("min_age_in_seconds_before_reset is not null");
		}

		public Criteria andMinAgeInSecondsBeforeResetEqualTo(Integer value) {
			return addCriterion("min_age_in_seconds_before_reset =", value);
		}

		public Criteria andMinAgeInSecondsBeforeResetNotEqualTo(Integer value) {
			return addCriterion("min_age_in_seconds_before_reset <>", value);
		}

		public Criteria andMinAgeInSecondsBeforeResetGreaterThan(Integer value) {
			return addCriterion("min_age_in_seconds_before_reset >", value);
		}

		public Criteria andMinAgeInSecondsBeforeResetGreaterThanOrEqualTo(Integer value) {
			return addCriterion("min_age_in_seconds_before_reset >=", value);
		}

		public Criteria andMinAgeInSecondsBeforeResetLessThan(Integer value) {
			return addCriterion("min_age_in_seconds_before_reset <", value);
		}

		public Criteria andMinAgeInSecondsBeforeResetLessThanOrEqualTo(Integer value) {
			return addCriterion("min_age_in_seconds_before_reset <=", value);
		}

		public Criteria andMinAgeInSecondsBeforeResetIn(List<Integer> values) {
			return addCriterion("min_age_in_seconds_before_reset in", values);
		}

		public Criteria andMinAgeInSecondsBeforeResetNotIn(List<Integer> values) {
			return addCriterion("min_age_in_seconds_before_reset not in", values);
		}

		public Criteria andMinAgeInSecondsBeforeResetBetween(Integer value1, Integer value2) {
			return addCriterion("min_age_in_seconds_before_reset between", value1, value2, "minAgeInSecondsBeforeReset");
		}

		public Criteria andMinAgeInSecondsBeforeResetNotBetween(Integer value1, Integer value2) {
			return addCriterion("min_age_in_seconds_before_reset not between", value1, value2, "minAgeInSecondsBeforeReset");
		}

		public Criteria andExtraChecksEnabledIsNull() {
			return addCriterion("extra_checks_enabled is null");
		}

		public Criteria andExtraChecksEnabledIsNotNull() {
			return addCriterion("extra_checks_enabled is not null");
		}

		public Criteria andExtraChecksEnabledBetween(Long value1, Long value2) {
			return addCriterion("extra_checks_enabled between", value1, value2, "extraChecksEnabled");
		}

		public Criteria andExtraChecksEnabledNotBetween(Long value1, Long value2) {
			return addCriterion("extra_checks_enabled not between", value1, value2, "extraChecksEnabled");
		}
	}
}
