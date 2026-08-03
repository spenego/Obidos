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

public final class AssignedContainerExample extends ObidosExample<AssignedContainerExample.Criteria> {
	public AssignedContainerExample() { }

	public AssignedContainerExample(final String orderByClause) { super(orderByClause); }
	public AssignedContainerExample(final Consumer<Criteria> c) { super(c); }

	protected Criteria createCriteriaInternal() {
		return new Criteria();
	}

	public static class Criteria extends ObidosCriteria<Criteria> {
		@Override
		protected String colPrefix() { return "ca."; }

		public Criteria andOwnerIdEqualTo(Long value)		{ return addCriterion("c.user_id =", value); }
		public Criteria andOwnerIdNotEqualTo(Long value)	{ return addCriterion("c.user_id <>", value); }
		public Criteria andNameIsNull()						{ return addCriterion("name is null"); }
		public Criteria andNameIsNotNull()					{ return addCriterion("name is not null"); }
		public Criteria andNameEqualTo(String value)		{ return addCriterion("name =", value); }
		public Criteria andNameNotEqualTo(String value)		{ return addCriterion("name <>", value); }
		public Criteria andNameLike(String value)			{ return addLikeCriterion("name like", value); }
		public Criteria andNameNotLike(String value)		{ return addLikeCriterion("name not like", value); }
		public Criteria andNameIn(Collection<String> values){ return addCriterion("name in", values); }
		public Criteria andNameNotIn(Collection<String> values) { return addCriterion("name not in", values); }
		public Criteria andOwnernameIsNull()				{ return addCriterion("ownername is null"); }
		public Criteria andOwnernameIsNotNull()				{ return addCriterion("ownername is not null"); }
		public Criteria andOwnernameEqualTo(String value)	{ return addCriterion("ownername =", value); }
		public Criteria andOwnernameNotEqualTo(String value){ return addCriterion("ownername <>", value); }
		public Criteria andOwnernameLike(String value)		{ return addLikeCriterion("ownername like", value); }
		public Criteria andOwnernameNotLike(String value)	{ return addLikeCriterion("ownername not like", value); }
		public Criteria andOwnernameIn(Collection<String> values) { return addCriterion("ownername in", values); }
		public Criteria andOwnernameNotIn(Collection<String> values) { return addCriterion("ownername not in", values); }
		public Criteria andIsPrivateEqualTo(Boolean value)	{ return addCriterion("is_private =", value); }
		public Criteria andSharedEqualTo(Boolean value)		{ return addCriterion("shared =", value); }
		public Criteria andViewFlagIsNull()					{ return addCriterion("view_flag is null"); }
		public Criteria andViewFlagIsNotNull()				{ return addCriterion("view_flag is not null"); }
		public Criteria andViewFlagEqualTo(Boolean value)	{ return addCriterion("view_flag =", value); }
		public Criteria andViewFlagNotEqualTo(Boolean value){ return addCriterion("view_flag <>", value); }
		public Criteria andShareFlagEqualTo(Boolean value)	{ return addCriterion("share_flag =", value); }
		public Criteria andShareFlagNotEqualTo(Boolean value){return addCriterion("share_flag <>", value); }
		public Criteria andModifyFlagIsNull()				{ return addCriterion("modify_flag is null"); }
		public Criteria andModifyFlagIsNotNull()			{ return addCriterion("modify_flag is not null"); }
		public Criteria andModifyFlagEqualTo(Boolean value) { return addCriterion("modify_flag =", value); }
		public Criteria andModifyFlagNotEqualTo(Boolean value){return addCriterion("modify_flag <>", value); }
	}
}
