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
import java.util.Date;
import java.util.function.Consumer;

public final class UserExample extends ObidosExample<UserExample.Criteria> {
	public UserExample() { }

	public UserExample(final String orderByClause, final Consumer<Criteria> consumer) {
		super(orderByClause);
		consumer.accept(createCriteria());
	}

	public UserExample(final Consumer<Criteria> c) {
		super(c);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria(sb);
	}

	public static class Criteria extends ObidosCriteria<Criteria> {
		Criteria(final StringBuilder sb) {
			super(sb);
		}

		public Criteria andItemIdEqualTo(final Long value) {
			return addCriterion("i.id =", value);
		}

		@Override
		protected String colPrefix() {
			return "u.";
		}

		private StringBuilder getGroupCondition(final Collection<Long> groupIds) {
			final StringBuilder sb = initStringBuilder(64 + groupIds.size() * 12).append("u.id in (select user_id from group_members where group_id in (");

			boolean first = true;
			for(final Long g : groupIds) {
				if (!first) { sb.append(','); }
				first = false;
				sb.append(g);
			}

			return sb.append("))");
		}

		@Override
		public Criteria andGroupIdIn(final Collection<Long> groupIds) {
			return (groupIds != null && !groupIds.isEmpty()) ? addCriterion(getGroupCondition(groupIds)) : (Criteria) this;
		}

		@Override
		public Criteria andGroupIdNotEqualTo(final Long value) {
			return addCriterion("gm.group_id <>", value);
		}

		@Override
		public Criteria andGroupIdIsNull() {
			return addCriterion("gm.group_id is null");
		}

		public Criteria andSecurityClearanceLessThan(Integer value) {
			return addCriterion("security_clearance <", value);
		}

		@Override
		public Criteria andContainerIdEqualTo(final Long value) {
			return addCriterion("c.id =", value);
		}

		public Criteria andContainerSharedExplicitly(final Boolean value) {
			return addCriterion("ca.shared_explicitly =", value);
		}

		public Criteria andItemSharedExplicitly(final Boolean value) {
			return addCriterion("ia.shared_explicitly =", value);
		}

		public Criteria andDeletedIsNull() {
			return addCriterion("deleted is null");
		}

		public Criteria andDeletedIsNotNull() {
			return addCriterion("deleted is not null");
		}

		@Override
		public Criteria andDeletedEqualTo(Boolean value) {
			return addCriterion("deleted =", value);
		}

		public Criteria andUserDeletedEqualTo(Boolean value) {
			return addCriterion("u.deleted =", value);
		}

		public Criteria andDeletedNotEqualTo(Boolean value) {
			return addCriterion("deleted <>", value);
		}

		public Criteria andLockedIsNull() {
			return addCriterion("locked is null");
		}

		public Criteria andLockedIsNotNull() {
			return addCriterion("locked is not null");
		}

		@Override
		public Criteria andLockedEqualTo(Boolean value) {
			return addCriterion("locked =", value);
		}

		public Criteria andAdministratorIsNull() {
			return addCriterion("administrator is null");
		}

		public Criteria andAdministratorIsNotNull() {
			return addCriterion("administrator is not null");
		}

		@Override
		public Criteria andAdministratorEqualTo(final Boolean value) {
			return addCriterion("administrator =", value);
		}

		public Criteria andAdministratorNotEqualTo(Boolean value) {
			return addCriterion("administrator <>", value);
		}

		public Criteria andPasswordChangeRequiredIsNull() {
			return addCriterion("password_change_required is null");
		}

		public Criteria andPasswordChangeRequiredIsNotNull() {
			return addCriterion("password_change_required is not null");
		}

		@Override
		public Criteria andPasswordChangeRequiredEqualTo(Boolean value) {
			return addCriterion("password_change_required =", value);
		}

		public Criteria andLastLoginIsNull() {
			return addCriterion("last_login is null");
		}

		public Criteria andLastLoginIsNotNull() {
			return addCriterion("last_login is not null");
		}

		public Criteria andLastLoginEqualTo(Date value) {
			return addCriterion("last_login =", value);
		}

		public Criteria andLastLoginNotEqualTo(Date value) {
			return addCriterion("last_login <>", value);
		}

		public Criteria andLastLoginGreaterThan(Date value) {
			return addCriterion("last_login >", value);
		}

		public Criteria andLastLoginGreaterThanOrEqualTo(Date value) {
			return addCriterion("last_login >=", value);
		}

		public Criteria andLastLoginLessThan(Date value) {
			return addCriterion("last_login <", value);
		}

		public Criteria andLastLoginLessThanOrEqualTo(Date value) {
			return addCriterion("last_login <=", value);
		}

		public Criteria andLastLoginIn(Collection<Date> values) {
			return addCriterion("last_login in", values);
		}

		public Criteria andLastLoginNotIn(Collection<Date> values) {
			return addCriterion("last_login not in", values);
		}

		public Criteria andLastLoginBetween(Date value1, Date value2) {
			return addCriterion("last_login between", value1, value2, "lastLogin");
		}

		public Criteria andLastLoginNotBetween(Date value1, Date value2) {
			return addCriterion("last_login not between", value1, value2, "lastLogin");
		}

		@Override
		public Criteria andUsernameLike(String value) {
			return addLikeCriterion("username like", value);
		}

		public Criteria andUsernameNotLike(String value) {
			return addLikeCriterion("username not like", value);
		}

		public Criteria andUsernameIn(Collection<String> values) {
			return addCriterion("username in", values);
		}

		public Criteria andUsernameNotIn(Collection<String> values) {
			return addCriterion("username not in", values);
		}

		public Criteria andAuthSourceIsNull() {
			return addCriterion("auth_source is null");
		}

		public Criteria andAuthSourceIsNotNull() {
			return addCriterion("auth_source is not null");
		}

		public Criteria andAuthSourceEqualTo(String value) {
			return addCriterion("auth_source =", value);
		}

		public Criteria andAuthSourceNotEqualTo(String value) {
			return addCriterion("auth_source <>", value);
		}

		public Criteria andAuthSourceLike(String value) {
			return addLikeCriterion("auth_source like", value);
		}

		public Criteria andAuthSourceNotLike(String value) {
			return addLikeCriterion("auth_source not like", value);
		}

		public Criteria andFullnameIsNull() {
			return addCriterion("fullname is null");
		}

		public Criteria andFullnameIsNotNull() {
			return addCriterion("fullname is not null");
		}

		public Criteria andFullnameEqualTo(String value) {
			return addCriterion("fullname =", value);
		}

		public Criteria andFullnameNotEqualTo(String value) {
			return addCriterion("fullname <>", value);
		}

		@Override
		public Criteria andFullnameLike(final String value) {
			return addLikeCriterion("fullname like", value);
		}

		@Override
		public Criteria andFullnameNotLike(String value) {
			return addLikeCriterion("fullname not like", value);
		}

		public Criteria andFullnameIn(Collection<String> values) {
			return addCriterion("fullname in", values);
		}

		public Criteria andFullnameNotIn(Collection<String> values) {
			return addCriterion("fullname not in", values);
		}

		public Criteria andEmail1IsNull() {
			return addCriterion("email1 is null");
		}

		public Criteria andEmail1IsNotNull() {
			return addCriterion("email1 is not null");
		}

		public Criteria andEmail1EqualTo(String value) {
			return addCriterion("email1 =", value);
		}

		public Criteria andEmail1NotEqualTo(String value) {
			return addCriterion("email1 <>", value);
		}

		public Criteria andEmail1Like(String value) {
			return addLikeCriterion("email1 like", value);
		}

		public Criteria andEmail1NotLike(String value) {
			return addLikeCriterion("email1 not like", value);
		}

		public Criteria andEmail1In(Collection<String> values) {
			return addCriterion("email1 in", values);
		}

		public Criteria andEmail1NotIn(Collection<String> values) {
			return addCriterion("email1 not in", values);
		}

		public Criteria andEmail2IsNull() {
			return addCriterion("email2 is null");
		}

		public Criteria andEmail2IsNotNull() {
			return addCriterion("email2 is not null");
		}

		public Criteria andEmail2EqualTo(String value) {
			return addCriterion("email2 =", value);
		}

		public Criteria andEmail2NotEqualTo(String value) {
			return addCriterion("email2 <>", value);
		}

		public Criteria andEmail2Like(String value) {
			return addLikeCriterion("email2 like", value);
		}

		public Criteria andEmail2NotLike(String value) {
			return addLikeCriterion("email2 not like", value);
		}

		public Criteria andEmail3IsNull() {
			return addCriterion("email3 is null");
		}

		public Criteria andEmail3IsNotNull() {
			return addCriterion("email3 is not null");
		}

		public Criteria andEmail3EqualTo(String value) {
			return addCriterion("email3 =", value);
		}

		public Criteria andEmail3NotEqualTo(String value) {
			return addCriterion("email3 <>", value);
		}

		public Criteria andEmail3Like(String value) {
			return addLikeCriterion("email3 like", value);
		}

		public Criteria andEmail3NotLike(String value) {
			return addLikeCriterion("email3 not like", value);
		}

		public Criteria andPhoneIsNull() {
			return addCriterion("phone is null");
		}

		public Criteria andPhoneIsNotNull() {
			return addCriterion("phone is not null");
		}

		public Criteria andPhoneEqualTo(final String value) {
			return addCriterion("phone =", value);
		}

		public Criteria andPhoneNotEqualTo(final String value) {
			return addCriterion("phone <>", value);
		}

		@Override
		public Criteria andPhoneLike(final String value) {
			return addLikeCriterion("phone like", value);
		}

		public Criteria andPhoneNotLike(final String value) {
			return addLikeCriterion("phone not like", value);
		}

		public Criteria andPhoneIn(final Collection<String> values) {
			return addCriterion("phone in", values);
		}

		public Criteria andPhoneNotIn(final Collection<String> values) {
			return addCriterion("phone not in", values);
		}

		public Criteria andMobile1IsNull() {
			return addCriterion("mobile1 is null");
		}

		public Criteria andMobile1IsNotNull() {
			return addCriterion("mobile1 is not null");
		}

		public Criteria andMobile1EqualTo(String value) {
			return addCriterion("mobile1 =", value);
		}

		public Criteria andMobile1NotEqualTo(String value) {
			return addCriterion("mobile1 <>", value);
		}

		public Criteria andMobile1Like(String value) {
			return addLikeCriterion("mobile1 like", value);
		}

		public Criteria andMobile1NotLike(String value) {
			return addLikeCriterion("mobile1 not like", value);
		}

		public Criteria andMobile2IsNull() {
			return addCriterion("mobile2 is null");
		}

		public Criteria andMobile2IsNotNull() {
			return addCriterion("mobile2 is not null");
		}

		public Criteria andMobile2EqualTo(String value) {
			return addCriterion("mobile2 =", value);
		}

		public Criteria andMobile2NotEqualTo(String value) {
			return addCriterion("mobile2 <>", value);
		}

		public Criteria andMobile2Like(String value) {
			return addLikeCriterion("mobile2 like", value);
		}

		public Criteria andMobile2NotLike(String value) {
			return addLikeCriterion("mobile2 not like", value);
		}

		public Criteria andMobile3IsNull() {
			return addCriterion("mobile3 is null");
		}

		public Criteria andMobile3IsNotNull() {
			return addCriterion("mobile3 is not null");
		}

		public Criteria andMobile3EqualTo(String value) {
			return addCriterion("mobile3 =", value);
		}

		public Criteria andMobile3NotEqualTo(String value) {
			return addCriterion("mobile3 <>", value);
		}

		public Criteria andMobile3Like(String value) {
			return addLikeCriterion("mobile3 like", value);
		}

		public Criteria andMobile3NotLike(String value) {
			return addLikeCriterion("mobile3 not like", value);
		}

		public Criteria andTwitterIsNull() {
			return addCriterion("twitter is null");
		}

		public Criteria andTwitterIsNotNull() {
			return addCriterion("twitter is not null");
		}

		public Criteria andTwitterEqualTo(String value) {
			return addCriterion("twitter =", value);
		}

		public Criteria andTwitterNotEqualTo(String value) {
			return addCriterion("twitter <>", value);
		}

		public Criteria andTwitterLike(String value) {
			return addLikeCriterion("twitter like", value);
		}

		public Criteria andTwitterNotLike(String value) {
			return addLikeCriterion("twitter not like", value);
		}

		public Criteria andTwitterIn(Collection<String> values) {
			return addCriterion("twitter in", values);
		}

		public Criteria andTwitterNotIn(Collection<String> values) {
			return addCriterion("twitter not in", values);
		}

		public Criteria andFacebookIsNull() {
			return addCriterion("facebook is null");
		}

		public Criteria andFacebookIsNotNull() {
			return addCriterion("facebook is not null");
		}

		public Criteria andFacebookEqualTo(String value) {
			return addCriterion("facebook =", value);
		}

		public Criteria andFacebookNotEqualTo(String value) {
			return addCriterion("facebook <>", value);
		}

		public Criteria andFacebookLike(String value) {
			return addLikeCriterion("facebook like", value);
		}

		public Criteria andFacebookNotLike(String value) {
			return addLikeCriterion("facebook not like", value);
		}

		public Criteria andFacebookIn(Collection<String> values) {
			return addCriterion("facebook in", values);
		}

		public Criteria andFacebookNotIn(Collection<String> values) {
			return addCriterion("facebook not in", values);
		}

		public Criteria andRegionIdEqualTo(String value) {
			return (value != null && !value.isEmpty()) ? addCriterion("region_id = ", value) : addCriterion("region_id is null");
		}

		public Criteria andPasswordDigestIsNull() {
			return addCriterion("password_digest is null");
		}

		public Criteria andPasswordDigestIsNotNull() {
			return addCriterion("password_digest is not null");
		}

		public Criteria andAuthurnIsNull() {
			return addCriterion("authURN is null");
		}

		public Criteria andAuthurnIsNotNull() {
			return addCriterion("authURN is not null");
		}

		public Criteria andAuthurnEqualTo(String value) {
			return addCriterion("authURN =", value);
		}

		public Criteria andAuthurnNotEqualTo(String value) {
			return addCriterion("authURN <>", value);
		}

		public Criteria andAuthurnLike(String value) {
			return addLikeCriterion("authURN like", value);
		}

		public Criteria andAuthurnNotLike(String value) {
			return addLikeCriterion("authURN not like", value);
		}

		public Criteria andAuthurnIn(Collection<String> values) {
			return addCriterion("authURN in", values);
		}

		public Criteria andAuthurnNotIn(Collection<String> values) {
			return addCriterion("authURN not in", values);
		}

		public Criteria andPublickeyIsNull() {
			return addCriterion("publickey is null");
		}

		public Criteria andPublickeyIsNotNull() {
			return addCriterion("publickey is not null");
		}

		@Override
		public Criteria andAnyEmailLike(final String s) {
			if (s == null) {
				return this;
			}

			final String ps = prepSearchString(s);
			return addCriterion(initStringBuilder(ps.length() * 3 + 60).append("(email1 like '").append(ps)
					.append("' OR email2 like '").append(ps).append("' OR email3 like '").append(ps).append("')").toString());
		}

		private Criteria createCriteria(int len, final String str, final Long id) {
			return id == null ? this : addCriterion(initStringBuilder(len).append(str).append(id).append(")").toString());
		}

		public Criteria andNotInGroup(final Long groupId) {
			return createCriteria(86, "u.id not in (select user_id from group_members where group_id = ", groupId);
		}

		// Bug #750: We only exclude a user if the shared_explicitly flag is set to true. The will cause the
		//           user to be returned if the container was only shared with the user via a group share.
		public Criteria andNotInContainer(final Long containerId) {
			return createCriteria(126, "u.id not in (select user_id from container_assignments where shared_explicitly = true and container_id = ", containerId);
		}

		// Bug #750 affected this sql statement too.
		public Criteria andUsersNotSharingItem(final Long itemId) {
			return createCriteria(116, "u.id not in (select user_id from item_assignments where shared_explicitly = true and item_id = ", itemId);
		}
	}
}
