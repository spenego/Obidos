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

public final class ItemGroupExample extends ObidosExample<ItemGroupExample.Criteria> {
	public ItemGroupExample() { }

	public ItemGroupExample(final Consumer<Criteria> c) {
		super(c);
	}

	public ItemGroupExample(final String orderByClause) {
		super(orderByClause);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria();
	}

	public static class Criteria extends ObidosCriteria<Criteria> {
		public Criteria andItemIdIsNull()							{ return addCriterion("item_id is null"); }
		public Criteria andItemIdIsNotNull()						{ return addCriterion("item_id is not null"); }
		public Criteria andItemIdEqualTo(Long value)				{ return addCriterion("item_id =", value); }
		public Criteria andItemIdNotEqualTo(Long value)				{ return addCriterion("item_id <>", value); }
		public Criteria andSharedExplicitlyEqualTo(Boolean value)	{ return addCriterion("shared_explicitly =", value); }
	}
}
