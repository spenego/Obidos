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

public final class GroupUserExample extends ObidosExample<GroupUserExample.Criteria> {
	public GroupUserExample() { }

	public GroupUserExample(final String orderByClause) {
		super(orderByClause);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria();
	}

	public static class Criteria extends ObidosCriteria<Criteria> {
		@Override
		protected String colPrefix() {
			return "u.";
		}

		@Override
		public Criteria andAnyEmailLike(final String s) {
			if (s == null) {
				return this;
			}

			final String ps = prepSearchString(s);
			return addCriterion("(u.email1 like '" + ps + "' OR u.email2 like '" + ps + "' OR u.email3 like '" + ps + "')");
		}

		@Override
		public Criteria andUsernameLike(String value) {
			return addLikeCriterion("u.username like", value);
		}

		@Override
		public Criteria andPhoneLike(final String value) {
			return addLikeCriterion("u.phone like", value);
		}

		@Override
		public Criteria andAdministratorEqualTo(final Boolean value) {
			return addCriterion("u.administrator =", value);
		}

		public Criteria andDeletedIsNull() {
			return addCriterion("u.deleted is null");
		}

		public Criteria andDeletedIsNotNull() {
			return addCriterion("u.deleted is not null");
		}

		@Override
		public Criteria andDeletedEqualTo(Boolean value) {
			return addCriterion("u.deleted =", value);
		}

		public Criteria andDeletedNotEqualTo(Boolean value) {
			return addCriterion("u.deleted <>", value);
		}

		public Criteria andLockedIsNull() {
			return addCriterion("u.locked is null");
		}

		public Criteria andLockedIsNotNull() {
			return addCriterion("u.locked is not null");
		}

		@Override
		public Criteria andLockedEqualTo(Boolean value) {
			return addCriterion("u.locked =", value);
		}

		public Criteria andLockedNotEqualTo(Boolean value) {
			return addCriterion("u.locked <>", value);
		}

		public Criteria andPasswordChangeRequiredIsNull() {
			return addCriterion("u.password_change_required is null");
		}

		public Criteria andPasswordChangeRequiredIsNotNull() {
			return addCriterion("u.password_change_required is not null");
		}

		@Override
		public Criteria andPasswordChangeRequiredEqualTo(Boolean value) {
			return addCriterion("u.password_change_required =", value);
		}

		public Criteria andPasswordChangeRequiredNotEqualTo(Boolean value) {
			return addCriterion("u.password_change_required <>", value);
		}

		public Criteria andFullnameIsNull() {
			return addCriterion("u.fullname is null");
		}

		public Criteria andFullnameIsNotNull() {
			return addCriterion("u.fullname is not null");
		}

		public Criteria andFullnameEqualTo(String value) {
			return addCriterion("u.fullname =", value);
		}

		public Criteria andFullnameNotEqualTo(String value) {
			return addCriterion("u.fullname <>", value);
		}

		@Override
		public Criteria andFullnameLike(String value) {
			return addLikeCriterion("u.fullname like", value);
		}

		@Override
		public Criteria andFullnameNotLike(String value) {
			return addLikeCriterion("u.fullname not like", value);
		}

		public Criteria andFullnameIn(Collection<String> values) {
			return addCriterion("u.fullname in", values);
		}

		public Criteria andFullnameNotIn(Collection<String> values) {
			return addCriterion("u.fullname not in", values);
		}

		@Override
		public Criteria andGroupIdEqualTo(final Long value) {
			return addCriterion("gm.group_id =", value);
		}
	}
}
