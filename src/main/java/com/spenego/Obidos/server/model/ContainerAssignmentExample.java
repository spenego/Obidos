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

public final class ContainerAssignmentExample  extends ObidosExample<ContainerAssignmentExample.Criteria> {
	public ContainerAssignmentExample() { }

	public ContainerAssignmentExample(final Consumer<Criteria> c) {
		super(c);
	}

	public ContainerAssignmentExample(final String orderByClause, int initialStringBuilderSize) {
		super(orderByClause, initialStringBuilderSize);
	}

	public ContainerAssignmentExample(final String orderByClause) {
		super(orderByClause);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria();
	}

	public static class Criteria extends ObidosCriteria<Criteria> {
		public Criteria andCountGreaterThan(int value) {
			return addCriterion("count >", value);
		}

		public Criteria andCountEqualTo(int value) {
			return addCriterion("count =", value);
		}

		@Override
		public Criteria andGroupIdEqualTo(final Long id) {
			return addCriterion("cg.group_id =", id);
		}

		public Criteria andContainersSharedWithGroup(final Long groupId) {
			return (groupId == null) ? this : addCriterion("ca.container_id in (select cgx.container_id from container_group_assignments cgx where cgx.group_id = " + groupId + ")");
		}

		public Criteria andCAComboContainerIdEqualTo(final Long callerId, final Long containerId, final String searchString) {
			final String preppedSearchString = searchString == null || searchString.isEmpty() ? null : prepSearchString(searchString);
			final StringBuilder sb = initStringBuilder(1024);

			sb.append(" cga.container_id = ").append(containerId);

			if (preppedSearchString != null) {
				sb.append(" and g.name like \"").append(preppedSearchString).append("\"");
			}

			sb.append(") union");
			sb.append("  select u.id as id, u.fullname as name, 'u' as type, u.email1 as email1, u.office as office, u.preference_flags & 0x4 as profile_pic_enabled, ca.add_permitted as add_permitted, ca.update_permitted as update_permitted, ca.ownership_control as ownership_control");
			sb.append(" from container_assignments ca");
			sb.append("   join users u on u.id = ca.user_id");
			sb.append("   where (ca.container_id = ").append(containerId);
			sb.append("    and u.id <> ").append(callerId);
			if (preppedSearchString != null) {
				sb.append(" and (u.fullname like \"").append(preppedSearchString).append("\" or u.email1 like \"").append(preppedSearchString).append("\")" );
			}
			sb.append(" and ca.count > (select count(*) from container_group_assignments cga join group_members gm on gm.group_id = cga.group_id join users u on u.id = gm.user_id where gm.user_id = ca.user_id and cga.container_id = ca.container_id)");

			return addCriterion(sb.toString());
		}

		public Criteria andCAContainerIdEqualTo(final Long value) {
			return addCriterion("ca.container_id =", value);
		}

		@Override
		public Criteria andUserIdIsNull() {
			return addCriterion("user_id is null");
		}

		@Override
		public Criteria andUserIdIsNotNull() {
			return addCriterion("user_id is not null");
		}

		@Override
		public Criteria andUserIdEqualTo(final Long value) {
			return addCriterion("user_id =", value);
		}

		@Override
		public Criteria andUserIdNotEqualTo(Long value) {
			return addCriterion("ca.user_id <>", value);
		}

		@Override
		public Criteria andUserIdIn(Collection<Long> values) {
			return addCriterion("ca.user_id in", values);
		}

		public Criteria andUserIdInGroup(final Long groupId) {
			return addCriterion("ca.user_id in (select user_id from group_members where group_id = " + groupId + ")");
		}

		@Override
		public Criteria andUserIdNotIn(Collection<Long> values) {
			return addCriterion("ca.user_id not in", values);
		}

		public Criteria andViewFlagIsNull() {
			return addCriterion("ca.view_flag is null");
		}

		public Criteria andViewFlagIsNotNull() {
			return addCriterion("ca.view_flag is not null");
		}

		public Criteria andViewFlagEqualTo(Boolean value) {
			return addCriterion("ca.view_flag =", value);
		}

		public Criteria andModifyFlagIsNull() {
			return addCriterion("ca.modify_flag is null");
		}

		public Criteria andModifyFlagIsNotNull() {
			return addCriterion("ca.modify_flag is not null");
		}

		public Criteria andModifyFlagEqualTo(Boolean value) {
			return addCriterion("ca.modify_flag =", value);
		}

		public Criteria andModifyFlagNotEqualTo(Boolean value) {
			return addCriterion("ca.modify_flag <>", value);
		}

		public Criteria andShareFlagIsNull() {
			return addCriterion("ca.share_flag is null");
		}

		public Criteria andShareFlagIsNotNull() {
			return addCriterion("ca.share_flag is not null");
		}

		public Criteria andShareFlagEqualTo(Boolean value) {
			return addCriterion("ca.share_flag =", value);
		}

		public Criteria andShareFlagNotEqualTo(Boolean value) {
			return addCriterion("ca.share_flag <>", value);
		}
	}
}
