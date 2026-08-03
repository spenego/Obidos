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

public final class DocumentExample extends ObidosExample<DocumentExample.Criteria> {
	public DocumentExample() { }

	public DocumentExample(final String orderByClause) {
		super(orderByClause);
	}

	public DocumentExample(final Consumer<Criteria> c) {
		super(c);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria();
	}

	public static class Criteria extends ObidosCriteria<Criteria> {
		public Criteria andItemIdEqualTo(final Long value) {
			return addCriterion("document.item_id =", value);
		}

		@Override
		protected String colPrefix() {
			return "document.";
		}

		public Criteria andGuidEqualTo(final String value) {
			return addCriterion("guid =", value);
		}
	}
}
