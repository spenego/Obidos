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
import java.util.List;
import java.util.function.Consumer;

public abstract class ObidosExample<C> {
	protected String orderByClause;
	protected boolean distinct;
	protected List<C> oredCriteria;
	protected final StringBuilder sb;
	protected abstract C createCriteriaInternal();

	protected ObidosExample(int initialStringBuilderSize) {
		oredCriteria = new ArrayList<>();
		sb = new StringBuilder(initialStringBuilderSize);
	}

	protected ObidosExample(final Consumer<C> c) {
		this(128);
		c.accept(createCriteria());
		setDistinct(true);
	}

	protected ObidosExample() {
		this(128);
	}

	protected ObidosExample(final String orderByClause, int initialStringBuilderSize) {
		this(initialStringBuilderSize);
		this.orderByClause = orderByClause;
		this.distinct = true;
	}

	protected ObidosExample(final String orderByClause) {
		this();
		this.orderByClause = orderByClause;
		this.distinct = true;
	}

	public final void setOrderByClause(final String orderByClause) {
		this.orderByClause = orderByClause;
	}

	public final String getOrderByClause() {
		return orderByClause;
	}

	public final void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public final boolean isDistinct() {
		return distinct;
	}

	public final List<C> getOredCriteria() {
		return oredCriteria;
	}

	public final void or(C criteria) {
		oredCriteria.add(criteria);
	}

	public final C or() {
		C criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}

	public final C createCriteria() {
		C criteria = createCriteriaInternal();
		if (oredCriteria.isEmpty()) {
			oredCriteria.add(criteria);
		}
		return criteria;
	}

	public final void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
		sb.setLength(0);
	}
}
