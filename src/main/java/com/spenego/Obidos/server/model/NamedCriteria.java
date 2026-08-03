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

public abstract class NamedCriteria<C> extends ObidosCriteria<C>  {
	NamedCriteria(final StringBuilder sb) {
		super(sb);
	}

	public C andNameIsNull() {
		return addCriterion("name is null");
	}

	public C andNameIsNotNull() {
		return addCriterion("name is not null");
	}

	public C andNameEqualTo(String value) {
		return addCriterion("name =", value);
	}

	public C andNameNotEqualTo(String value) {
		return addCriterion("name <>", value);
	}

	public C andNameGreaterThan(String value) {
		return addCriterion("name >", value);
	}

	public C andNameGreaterThanOrEqualTo(String value) {
		return addCriterion("name >=", value);
	}

	public C andNameLessThan(String value) {
		return addCriterion("name <", value);
	}

	public C andNameLessThanOrEqualTo(String value) {
		return addCriterion("name <=", value);
	}

	public final C andNameLike(final String value) {
		return addLikeCriterion("name like", value);
	}

	public final C andNameNotLike(final String value) {
		return addLikeCriterion("name not like", value);
	}

	public C andNameIn(Collection<String> values) {
		return addCriterion("name in", values);
	}

	public C andNameNotIn(Collection<String> values) {
		return addCriterion("name not in", values);
	}

	public C andNameBetween(String value1, String value2) {
		return addCriterion("name between", value1, value2, "name");
	}

	public C andNameNotBetween(String value1, String value2) {
		return addCriterion("name not between", value1, value2, "name");
	}
}
