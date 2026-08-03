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
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import com.spenego.Obidos.shared.exceptions.ObidosException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public abstract class ObidosCriteria<C> {
	private static final String CREATED_AT	= "created_at";
	private static final String UPDATED_AT	= "updated_at";
	private static final String USER_ID		= "user_id";
	private static final String GROUP_ID	= "group_id";
	private static final String VERSION		= "version";
	private static final String CONTAINER_ID= "container_id";
	private static final String USERNAME	= "username";
	private static final String COMMENTS	= "comments";
	private final Collection<ObidosCriterion> criteria;
	private final StringBuilder sb;

	protected ObidosCriteria() {
		criteria = new ArrayList<>();
		sb = new StringBuilder(128);
	}

	protected ObidosCriteria(final StringBuilder sb) {
		criteria = new ArrayList<>();
		this.sb = sb;
	}

	public boolean isValid() {
		return !criteria.isEmpty();
	}

	@SuppressWarnings("unchecked")	// the derived class should only ever parameterize with its derived 'Criteria' class
	protected final C returner() {
		return (C) this;
	}

	protected final StringBuilder initStringBuilder(final int len) {
		sb.setLength(0);	// clear
		sb.ensureCapacity(len);
		return sb;
	}

	private static String cleanString(final String s) {
		return (s.length() > 64 ? s.substring(0, 64) : s).replace("%", "\\\\%").replace("_", "\\\\_");
	}

	/**
	 * @param str must be neither null nor empty
	 * @return
	 */
	protected final String prepSearchString(final String str) {
		final String s = cleanString(str);
		return initStringBuilder(s.length() + 8).append("%").append(s).append("%").toString();
	}

	protected final C addCriterion(final ObidosCriterion condition) {
		criteria.add(condition);
		return returner();
	}

	protected final C addCriterion(final String condition) {
		if (condition == null) {
			throw new ObidosException("Value for condition cannot be null");
		}
		return addCriterion(new ObidosCriterion(condition));
	}

	protected final C addCriterion(final StringBuilder condition) {
		return addCriterion(condition.toString());
	}

	protected final C addCriterion(final String condition, final Object value) {
		return (value == null) ? returner() : addCriterion(new ObidosCriterion(condition, value));
	}

	protected final C addCriterion(final StringBuilder condition, final Object value) {
		return addCriterion(condition.toString(), value);
	}

	protected final C addLikeCriterion(final String condition, final String value) {
		return (value == null || value.isEmpty()) ? returner() : addCriterion(condition, prepSearchString(value));
	}

	protected final C addCriterion(final String condition, final Object value1, final Object value2, final String property) {
		if (value1 == null || value2 == null) {
			throw new ObidosException("Between values for " + property + " cannot be null");
		}
		return addCriterion(new ObidosCriterion(condition, value1, value2));
	}

	protected final C addCriterion(final StringBuilder condition, final Object value1, final Object value2, final String property) {
		return addCriterion(condition.toString(), value1, value2, property);
	}

	private static StringBuilder buildLikeEither(final StringBuilder sb, final String ps, final String col1, final String col2) {
		sb.append("(").append(col1).append(" like '").append(ps).append("' OR ").append(col2).append(" like '").append(ps).append("')");
		return sb;
	}

	/**
	 * @param value must not be null
	 * @param col1
	 * @param col2
	 * @return
	 */
	protected final C andLikeEither(final String value, final String col1, final String col2) {
		final String ps = prepSearchString(value);

		initStringBuilder(ps.length() * 2 + 48);

		return addCriterion(buildLikeEither(sb, ps, col1, col2));
	}

	protected final C andLikeAny(final Collection<String> values, final String col1, final String col2) {
		final AtomicBoolean firstPass = new AtomicBoolean(true);
		final StringBuilder lsb = new StringBuilder(128);

		lsb.append('(');
		values.forEach(value -> {
			if (!firstPass.get()) {
				lsb.append(" or ");
			} else {
				firstPass.set(false);
			}
			buildLikeEither(lsb, prepSearchString(value), col1, col2);});
		lsb.append(')');

		return addCriterion(lsb);
	}

	public C andFullnameOrUsernameLike(final String s) {
		return (s == null || s.isEmpty()) ? returner() : andLikeEither(s, "fullname", USERNAME);
	}

	public C andFullnameOrEmailLike(final String s) {
		return (s == null || s.isEmpty()) ? returner() : andLikeEither(s, "fullname", "email1");
	}

	public C andAdministratorEqualTo(final Boolean adminId) { throw new ServerSideException("caller must override method andAdministratorEqualTo"); }
	public C andDeletedEqualTo(final Boolean val)			{ throw new ServerSideException("caller must override method andDeletedEqualTo"); }
	public C andLockedEqualTo(final Boolean val)			{ throw new ServerSideException("caller must override method andLockedEqualTo"); }
	public C andPasswordChangeRequiredEqualTo(final Boolean val) { throw new ServerSideException("caller must override method andPasswordChangeRequiredEqualTo"); }
	public C andAnyEmailLike(final String s)				{ throw new ServerSideException("caller must override method andAnyEmailLike"); }
	public C andUsernameLike(String value)					{ throw new ServerSideException("caller must override method andUsernameLike"); }
	public C andFullnameLike(final String s)				{ throw new ServerSideException("caller must override method andFullnameLike"); }
	public C andFullnameNotLike(final String s)				{ throw new ServerSideException("caller must override method andFullnameNotLike"); }
	public C andPhoneLike(final String s)					{ throw new ServerSideException("caller must override method andPhoneLike"); }

	protected String colPrefix() {  return ""; } // NOSONAR -- bug in SonarQube, this method can not be removed

	private final StringBuilder condition(final String field, final String suffix) {
		return initStringBuilder(field.length() + suffix.length() + 2).append(colPrefix()).append(field).append(suffix);
	}

	private C andIsNull(final String field)						{ return addCriterion(condition(field, " is null")); }
	private C andIsNotNull(final String field)					{ return addCriterion(condition(field, " is not null")); }
	private <T> C andEqualTo(final String field, final T value)	{ return addCriterion(condition(field, " ="), value); }
	private <T> C andNotEqualTo(final String field, final T value){ return addCriterion(condition(field, " <>"), value); }
	private C andNotEqualTo(final String field, final Date value) { return addCriterion(condition(field, " <>"), value); }
	private C andGreaterThan(final String field, final Date value){ return addCriterion(condition(field, " >"), value); }
	public C andIdIsNull()										{ return andIsNull("id"); }
	public C andIdIsNotNull()									{ return andIsNotNull("id"); }
	public C andIdEqualTo(final Long value)						{ return andEqualTo("id", value); }
	public C andUserNotEqualTo(final User user)					{ return user == null ? returner() : addCriterion(condition("id", " <>"), user.getId()); }
	public C andIdNotEqualTo(final Long value)					{ return addCriterion(condition("id", " <>"), value); }
	public C andIdIn(final Collection<Long> values)				{ return addCriterion(colPrefix() + "id in", values); }
	public C andIdNotIn(final Collection<Long> values)			{ return addCriterion(colPrefix() + "id not in", values); }
	public C andItemAssignmentIdIn(final Collection<Long> values){ return addCriterion("ia.id in", values); }
	public C andItemAssignmentOwnerIs(final Long userId)		{ return addCriterion("ia.user_id =", userId); }
	public C andContainerAssignmentIdIn(final Collection<Long> values) { return addCriterion("ca.id in", values); }
	public C andContainerAssignmentOwnerIs(final Long userId)	{ return addCriterion("ca.user_id =", userId); }

	private C valuesCondition(final Object values, final String field, String condition) { return addCriterion(condition(field, condition), values); }
	private C valuesIn(final Object values, final String field) { return valuesCondition(values, field, " in"); }
	private C valuesNotIn(final Object values, final String field) { return valuesCondition(values, field, " not in"); }

	public C andCreatedAtIsNull()								{ return andIsNull(CREATED_AT); }
	public C andCreatedAtIsNotNull()							{ return andIsNotNull(CREATED_AT); }
	public C andCreatedAtEqualTo(final Date date)				{ return andEqualTo(CREATED_AT, date); }
	public C andCreatedAtNotEqualTo(final Date date)			{ return andNotEqualTo(CREATED_AT, date); }
	public C andCreatedAtGreaterThan(final Date date)			{ return andGreaterThan(CREATED_AT, date); }
	public C andCreatedAtGreaterThanOrEqualTo(final Date value)	{ return addCriterion(condition(CREATED_AT, " >="), value); }
	public C andCreatedAtLessThan(final Date value)				{ return addCriterion(condition(CREATED_AT, " <"), value); }
	public C andCreatedAtLessThanOrEqualTo(final Date value)	{ return addCriterion(condition(CREATED_AT, " <="), value); }
	public C andCreatedAtIn(final Collection<Date> values)		{ return valuesIn(values, CREATED_AT); }
	public C andCreatedAtNotIn(final Collection<Date> values)	{ return valuesNotIn(values, CREATED_AT); }
	public C andCreatedAtBetween(final Date value1, final Date value2) { return addCriterion(condition(CREATED_AT, " between"), value1, value2, "createdAt"); }
	public C andCreatedAtNotBetween(final Date value1, final Date value2) { return addCriterion(condition(CREATED_AT, " not between"), value1, value2, "createdAt"); }

	public C andUpdatedAtIsNull()								{ return andIsNull(UPDATED_AT); }
	public C andUpdatedAtIsNotNull()							{ return andIsNotNull(UPDATED_AT); }
	public C andUpdatedAtEqualTo(final Date date)				{ return andEqualTo(UPDATED_AT, date); }
	public C andUpdatedAtNotEqualTo(final Date date)			{ return andNotEqualTo(UPDATED_AT, date); }
	public C andUpdatedAtGreaterThan(final Date date)			{ return andGreaterThan(UPDATED_AT, date); }
	public C andUpdatedAtGreaterThanOrEqualTo(final Date value) { return addCriterion(condition(UPDATED_AT, " >="), value); }
	public C andUpdatedAtLessThan(final Date value)				{ return addCriterion(condition(UPDATED_AT, " <"), value); }
	public C andUpdatedAtLessThanOrEqualTo(final Date value)	{ return addCriterion(condition(UPDATED_AT, " <="), value); }
	public C andUpdatedAtBetween(final Date value1, final Date value2) { return addCriterion(condition(UPDATED_AT, " between"), value1, value2, "updatedAt"); }
	public C andUpdatedAtNotBetween(final Date value1, final Date value2) { return addCriterion(condition(UPDATED_AT, " not between"), value1, value2, "updatedAt"); }

	public C andVersionEqualTo(final Integer value)				{ return andEqualTo(VERSION, value); }
	public C andVersionNotEqualTo(final Integer value)			{ return andNotEqualTo(VERSION, value); }
	public C andVersionGreaterThan(final Integer value) 		{ return addCriterion("version >", value); }
	public C andVersionGreaterThanOrEqualTo(final Integer value){ return addCriterion("version >=", value); }
	public C andVersionLessThan(final Integer value)			{ return addCriterion("version <", value); }
	public C andVersionLessThanOrEqualTo(final Integer value)	{ return addCriterion("version <=", value); }
	public C andVersionIn(final Collection<Integer> values)		{ return valuesIn(values, VERSION); }
	public C andVersionNotIn(final Collection<Integer> values)	{ return valuesNotIn(values, VERSION); }
	public C andVersionBetween(final Integer value1, final Integer value2) { return addCriterion("version between", value1, value2, VERSION); }
	public C andVersionNotBetween(final Integer value1, final Integer value2) { return addCriterion("version not between", value1, value2, VERSION); }

	public C andUserIdIsNull()									{ return andIsNull(USER_ID); }
	public C andUserIdIsNotNull()								{ return andIsNotNull(USER_ID); }
	public C andUserIdEqualTo(final Long value)					{ return andEqualTo(USER_ID, value); }
	public C andUserIdNotEqualTo(final Long value)				{ return andNotEqualTo(USER_ID, value); }
	public C andUserIdIn(final Collection<Long> values) 		{ return valuesIn(values, USER_ID); }
	public C andUserIdNotIn(Collection<Long> values)			{ return valuesNotIn(values, USER_ID); }

	public C andGroupIdIsNull()									{ return andIsNull(GROUP_ID);}
	public C andGroupIdIsNotNull()								{ return andIsNotNull(GROUP_ID);}
	public C andGroupIdEqualTo(Long value)						{ return andEqualTo(GROUP_ID, value);}
	public C andGroupIdNotEqualTo(Long value)					{ return andNotEqualTo(GROUP_ID, value);}
	public C andGroupIdIn(Collection<Long> values)				{ return valuesIn(values, GROUP_ID);}
	public C andGroupIdNotIn(Collection<Long> values)			{ return valuesNotIn(values, GROUP_ID); }

	public C andContainerIdIsNull()								{ return andIsNull(CONTAINER_ID); }
	public C andContainerIdIsNotNull()							{ return andIsNotNull(CONTAINER_ID); }
	public C andContainerIdEqualTo(Long value)					{ return andEqualTo(CONTAINER_ID, value); }
	public C andContainerIdNotEqualTo(Long value)				{ return andNotEqualTo(CONTAINER_ID, value); }
	public C andContainerIdIn(Collection<Long> values)			{ return valuesIn(values, CONTAINER_ID); }
	public C andContainerIdNotIn(Collection<Long> values)		{ return valuesNotIn(values, CONTAINER_ID); }

	public C andUsernameIsNull()								{ return andIsNull(USERNAME); }
	public C andUsernameIsNotNull()								{ return andIsNotNull(USERNAME); }
	public C andUsernameEqualTo(String value)					{ return andEqualTo(USERNAME, value); }
	public C andUsernameNotEqualTo(String value)				{ return andNotEqualTo(USERNAME, value); }
	public C andUsernameGreaterThan(String value)				{ return addCriterion("username >", value); }
	public C andUsernameGreaterThanOrEqualTo(String value)		{ return addCriterion("username >=", value); }
	public C andUsernameLessThan(String value)					{ return addCriterion("username <", value); }
	public C andUsernameLessThanOrEqualTo(String value)			{ return addCriterion("username <=", value); }

	public C andCommentsIsNull()								{ return andIsNull(COMMENTS); }
	public C andCommentsIsNotNull()								{ return andIsNotNull(COMMENTS); }
	public C andCommentsEqualTo(String value)					{ return andEqualTo(COMMENTS, value); }
	public C andCommentsNotEqualTo(String value)				{ return andNotEqualTo(COMMENTS, value); }
	public C andCommentsLike(String value)						{ return addLikeCriterion("comments like", value); }
	public C andCommentsNotLike(String value)					{ return addLikeCriterion("comments not like", value); }
	public C andCommentsIn(Collection<String> values)			{ return valuesIn(values, COMMENTS); }
	public C andCommentsNotIn(Collection<String> values)		{ return valuesNotIn(values, COMMENTS); }
}
