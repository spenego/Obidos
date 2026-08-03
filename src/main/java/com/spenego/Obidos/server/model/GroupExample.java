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

public final class GroupExample extends ObidosExample<GroupExample.Criteria> {
	public GroupExample() { }

	public GroupExample(final Consumer<Criteria> c) {
		super(c);
	}

	public GroupExample(final String orderByClause, final Consumer<Criteria> consumer) {
		super(orderByClause);
		consumer.accept(createCriteria());
	}

	@Override
	protected Criteria createCriteriaInternal() {
		return new Criteria(sb);
	}

	public static class Criteria extends NamedCriteria<Criteria> {
		Criteria(final StringBuilder sb) {
			super(sb);
		}

		@Override
		protected String colPrefix() {
			return "g.";
		}

		@Override
		public Criteria andContainerIdEqualTo(final Long value) {
			return addCriterion("c.id =", value);
		}

		public Criteria andAdminOrMyGroups(final Long userId) {
			return addCriterion("g.user_id in (select id from users where administrator = 1 or id = " + userId + ")");
		}

		public Criteria andGroupsNotInContainer(final Long containerId) {
			return addCriterion("id not in (select group_id from container_group_assignments where container_id = " + containerId + ")");
		}

		public Criteria andItemIdEqualTo(final Long value) {
			return addCriterion("ig.item_id =", value);
		}

		public Criteria andSharedExplicitlyEqualTo(Boolean value) {
			return addCriterion("ig.shared_explicitly =", value);
		}

		public Criteria andGroupNotSharingItem(final Long itemId) {
			return (itemId != null) ? addCriterion("g.id not in (select group_id from item_groups ig where shared_explicitly = true and item_id = " + itemId + ")") : (Criteria) this;
		}
	}
}
