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

import java.util.function.Consumer;

public final class UserDefinedTypeExample extends ObidosExample<UserDefinedTypeExample.Criteria> {
	public UserDefinedTypeExample() { }

	public UserDefinedTypeExample(final String orderByClause) {
		super(orderByClause);
	}

	public UserDefinedTypeExample(final Consumer<Criteria> c) {
		super(c);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria(sb);
	}

	public static class Criteria extends NamedCriteria<Criteria> {
		Criteria(final StringBuilder sb) { super(sb); }

		public Criteria andPersonalIsNull()					{ return addCriterion("personal is null"); }
		public Criteria andPersonalIsNotNull()				{ return addCriterion("personal is not null"); }
		public Criteria andPersonalEqualTo(Boolean value)	{ return addCriterion("personal =", value); }
		public Criteria andPersonalNotEqualTo(Boolean value){ return addCriterion("personal <>", value); }
		public Criteria andGlobalIsNull()					{ return addCriterion("global is null"); }
		public Criteria andGlobalIsNotNull()				{ return addCriterion("global is not null"); }
		public Criteria andGlobalEqualTo(Boolean value)		{ return addCriterion("global =", value); }
		public Criteria andGlobalNotEqualTo(Boolean value)	{ return addCriterion("global <>", value); }
		public Criteria andAdHocEqualTo(final Boolean value){ return addCriterion("ad_hoc =", value); }
	}
}
