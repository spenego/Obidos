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

public final class LdapExample extends ObidosExample<LdapExample.Criteria> {
	public LdapExample() { }

	public LdapExample(final Consumer<Criteria> c) {
		super(c);
	}

	public LdapExample(final String orderByClause) {
		super(orderByClause);
	}

	protected Criteria createCriteriaInternal() {
		return new Criteria(sb);
	}

	public static class Criteria extends NamedCriteria<Criteria> {
		Criteria(final StringBuilder sb) {
			super(sb);
		}

		public Criteria andLdapuriIsNull() {
			return addCriterion("ldapuri is null");
		}

		public Criteria andLdapuriIsNotNull() {
			return addCriterion("ldapuri is not null");
		}

		public Criteria andLdapuriEqualTo(String value) {
			return addCriterion("ldapuri =", value);
		}

		public Criteria andLdapuriNotEqualTo(String value) {
			return addCriterion("ldapuri <>", value);
		}

		public Criteria andLdapuriGreaterThan(String value) {
			return addCriterion("ldapuri >", value);
		}

		public Criteria andLdapuriGreaterThanOrEqualTo(String value) {
			return addCriterion("ldapuri >=", value);
		}

		public Criteria andLdapuriLessThan(String value) {
			return addCriterion("ldapuri <", value);
		}

		public Criteria andLdapuriLessThanOrEqualTo(String value) {
			return addCriterion("ldapuri <=", value);
		}

		public Criteria andLdapuriLike(String value) {
			return addLikeCriterion("ldapuri like", value);
		}

		public Criteria andLdapuriNotLike(String value) {
			return addLikeCriterion("ldapuri not like", value);
		}

		public Criteria andLdapuriIn(Collection<String> values) {
			return addCriterion("ldapuri in", values);
		}

		public Criteria andLdapuriNotIn(Collection<String> values) {
			return addCriterion("ldapuri not in", values);
		}

		public Criteria andBaseDnIsNull() {
			return addCriterion("base_dn is null");
		}

		public Criteria andBaseDnIsNotNull() {
			return addCriterion("base_dn is not null");
		}

		public Criteria andBaseDnEqualTo(String value) {
			return addCriterion("base_dn =", value);
		}

		public Criteria andBaseDnNotEqualTo(String value) {
			return addCriterion("base_dn <>", value);
		}

		public Criteria andBaseDnLike(String value) {
			return addLikeCriterion("base_dn like", value);
		}

		public Criteria andBaseDnNotLike(String value) {
			return addLikeCriterion("base_dn not like", value);
		}

		public Criteria andBaseDnIn(Collection<String> values) {
			return addCriterion("base_dn in", values);
		}

		public Criteria andBindDnIsNull() {
			return addCriterion("bind_dn is null");
		}

		public Criteria andBindDnIsNotNull() {
			return addCriterion("bind_dn is not null");
		}

		public Criteria andBindDnEqualTo(String value) {
			return addCriterion("bind_dn =", value);
		}

		public Criteria andBindDnNotEqualTo(String value) {
			return addCriterion("bind_dn <>", value);
		}

		public Criteria andBindDnLike(String value) {
			return addLikeCriterion("bind_dn like", value);
		}

		public Criteria andBindDnNotLike(String value) {
			return addLikeCriterion("bind_dn not like", value);
		}

		public Criteria andBindPassIsNull() {
			return addCriterion("bind_pass is null");
		}

		public Criteria andBindPassIsNotNull() {
			return addCriterion("bind_pass is not null");
		}

		public Criteria andAuthAttrIsNull() {
			return addCriterion("auth_attr is null");
		}

		public Criteria andAuthAttrIsNotNull() {
			return addCriterion("auth_attr is not null");
		}

		public Criteria andAuthAttrEqualTo(String value) {
			return addCriterion("auth_attr =", value);
		}

		public Criteria andAuthAttrNotEqualTo(String value) {
			return addCriterion("auth_attr <>", value);
		}

		public Criteria andAuthAttrLike(String value) {
			return addLikeCriterion("auth_attr like", value);
		}

		public Criteria andAuthAttrNotLike(String value) {
			return addLikeCriterion("auth_attr not like", value);
		}

		public Criteria andStartTlsIsNull() {
			return addCriterion("start_tls is null");
		}

		public Criteria andStartTlsIsNotNull() {
			return addCriterion("start_tls is not null");
		}

		public Criteria andStartTlsEqualTo(Boolean value) {
			return addCriterion("start_tls =", value);
		}

		public Criteria andStartTlsNotEqualTo(Boolean value) {
			return addCriterion("start_tls <>", value);
		}

		public Criteria andAuthorizeIsNull() {
			return addCriterion("authorize is null");
		}

		public Criteria andAuthorizeIsNotNull() {
			return addCriterion("authorize is not null");
		}

		public Criteria andAuthorizeEqualTo(Boolean value) {
			return addCriterion("authorize =", value);
		}

		public Criteria andAuthorizeNotEqualTo(Boolean value) {
			return addCriterion("authorize <>", value);
		}

		public Criteria andAuthorizationModeIsNull() {
			return addCriterion("authorization_mode is null");
		}

		public Criteria andAuthorizationModeIsNotNull() {
			return addCriterion("authorization_mode is not null");
		}

		public Criteria andAuthorizationModeEqualTo(String value) {
			return addCriterion("authorization_mode =", value);
		}

		public Criteria andAuthorizationModeNotEqualTo(String value) {
			return addCriterion("authorization_mode <>", value);
		}

		public Criteria andAuthorizationModeLike(String value) {
			return addLikeCriterion("authorization_mode like", value);
		}

		public Criteria andAuthorizationModeNotLike(String value) {
			return addLikeCriterion("authorization_mode not like", value);
		}

		public Criteria andAttrValIsNull() {
			return addCriterion("attr_val is null");
		}

		public Criteria andAttrValIsNotNull() {
			return addCriterion("attr_val is not null");
		}

		public Criteria andAttrValEqualTo(String value) {
			return addCriterion("attr_val =", value);
		}

		public Criteria andAttrValNotEqualTo(String value) {
			return addCriterion("attr_val <>", value);
		}

		public Criteria andAttrValLike(String value) {
			return addLikeCriterion("attr_val like", value);
		}

		public Criteria andAttrValNotLike(String value) {
			return addLikeCriterion("attr_val not like", value);
		}

		public Criteria andAttrValIn(Collection<String> values) {
			return addCriterion("attr_val in", values);
		}

		public Criteria andAttrValNotIn(Collection<String> values) {
			return addCriterion("attr_val not in", values);
		}

		public Criteria andFilterIsNull() {
			return addCriterion("filter is null");
		}

		public Criteria andFilterIsNotNull() {
			return addCriterion("filter is not null");
		}

		public Criteria andFilterEqualTo(String value) {
			return addCriterion("filter =", value);
		}

		public Criteria andFilterNotEqualTo(String value) {
			return addCriterion("filter <>", value);
		}

		public Criteria andFilterLike(String value) {
			return addLikeCriterion("filter like", value);
		}

		public Criteria andFilterNotLike(String value) {
			return addLikeCriterion("filter not like", value);
		}

		public Criteria andFilterIn(Collection<String> values) {
			return addCriterion("filter in", values);
		}

		public Criteria andFilterNotIn(Collection<String> values) {
			return addCriterion("filter not in", values);
		}

		public Criteria andLGroupIsNull() {
			return addCriterion("l_group is null");
		}

		public Criteria andLGroupIsNotNull() {
			return addCriterion("l_group is not null");
		}

		public Criteria andLGroupEqualTo(String value) {
			return addCriterion("l_group =", value);
		}

		public Criteria andLGroupNotEqualTo(String value) {
			return addCriterion("l_group <>", value);
		}

		public Criteria andLGroupLike(String value) {
			return addLikeCriterion("l_group like", value);
		}

		public Criteria andLGroupNotLike(String value) {
			return addLikeCriterion("l_group not like", value);
		}

		public Criteria andLGroupIn(Collection<String> values) {
			return addCriterion("l_group in", values);
		}

		public Criteria andLGroupNotIn(Collection<String> values) {
			return addCriterion("l_group not in", values);
		}

		public Criteria andGroupAttrIsNull() {
			return addCriterion("group_attr is null");
		}

		public Criteria andGroupAttrIsNotNull() {
			return addCriterion("group_attr is not null");
		}

		public Criteria andGroupAttrEqualTo(String value) {
			return addCriterion("group_attr =", value);
		}

		public Criteria andGroupAttrNotEqualTo(String value) {
			return addCriterion("group_attr <>", value);
		}

		public Criteria andGroupAttrLike(String value) {
			return addLikeCriterion("group_attr like", value);
		}

		public Criteria andGroupAttrNotLike(String value) {
			return addLikeCriterion("group_attr not like", value);
		}

		public Criteria andKeystorePathIsNull() {
			return addCriterion("keystore_path is null");
		}

		public Criteria andKeystorePathIsNotNull() {
			return addCriterion("keystore_path is not null");
		}

		public Criteria andKeystorePathEqualTo(String value) {
			return addCriterion("keystore_path =", value);
		}

		public Criteria andKeystorePathNotEqualTo(String value) {
			return addCriterion("keystore_path <>", value);
		}

		public Criteria andKeystorePathLike(String value) {
			return addLikeCriterion("keystore_path like", value);
		}

		public Criteria andKeystorePathNotLike(String value) {
			return addLikeCriterion("keystore_path not like", value);
		}

		public Criteria andKeystorePathIn(Collection<String> values) {
			return addCriterion("keystore_path in", values);
		}

		public Criteria andKeystorePathNotIn(Collection<String> values) {
			return addCriterion("keystore_path not in", values);
		}

		public Criteria andKeystorePasswordIsNull() {
			return addCriterion("keystore_password is null");
		}

		public Criteria andKeystorePasswordIsNotNull() {
			return addCriterion("keystore_password is not null");
		}

		public Criteria andKeystorePasswordEqualTo(String value) {
			return addCriterion("keystore_password =", value);
		}

		public Criteria andKeystorePasswordNotEqualTo(String value) {
			return addCriterion("keystore_password <>", value);
		}

		public Criteria andKeystorePasswordLike(String value) {
			return addLikeCriterion("keystore_password like", value);
		}

		public Criteria andKeystorePasswordNotLike(String value) {
			return addLikeCriterion("keystore_password not like", value);
		}
	}
}
