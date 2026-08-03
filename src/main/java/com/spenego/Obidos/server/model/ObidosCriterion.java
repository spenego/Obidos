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

public final class ObidosCriterion {
	private String condition;
	private String secondCondition;
	private Object value;
	private Object secondValue;
	private boolean noValue;
	private boolean singleValue;
	private boolean betweenValue;
	private boolean interiorOr;
	private boolean listValue;
	private String typeHandler;

	public String getCondition() {
		return condition;
	}

	public Object getValue() {
		return value;
	}

	public Object getSecondValue() {
		return secondValue;
	}

	public Object getSecondCondition() {
		return secondCondition;
	}

	public boolean isNoValue() {
		return noValue;
	}

	public boolean isSingleValue() {
		return singleValue;
	}

	public boolean isBetweenValue() {
		return betweenValue;
	}

	public boolean isInteriorOr() {
		return interiorOr;
	}

	public boolean isListValue() {
		return listValue;
	}

	public String getTypeHandler() {
		return typeHandler;
	}

	public ObidosCriterion(final String condition) {
		this.condition = condition;
		this.typeHandler = null;
		this.noValue = true;
	}

	public ObidosCriterion(final String condition, final Object value, final String typeHandler) {
		this.condition = condition;
		this.value = value;
		this.typeHandler = typeHandler;
		if (value instanceof Collection<?>) {
			this.listValue = true;
		} else {
			this.singleValue = true;
		}
	}

	public ObidosCriterion(final String condition, final Object value) {
		this(condition, value, null);
	}

	public ObidosCriterion(final String condition, final Object value, final Object secondValue, final String typeHandler) {
		this.condition = condition;
		this.value = value;
		this.secondValue = secondValue;
		this.typeHandler = typeHandler;
		this.betweenValue = true;
	}

	public ObidosCriterion(final String condition, final Object value, final Object secondValue) {
		this(condition, value, secondValue, null);
	}

	/**
	 * I needed a method where I could construct a Date test like 'where expires_at > 'date' or expires_at is null).
	 * I do this with the boolean interiorOr which you'll see references in the Mapper.xml files.
	 * Perhaps there was an existing alternative.
	 *
	 * @param condition
	 * @param secondCondition
	 */
	public ObidosCriterion(final String condition, final Date value, final String secondCondition) {
		this.condition = condition;
		this.value = value;
		this.secondCondition = secondCondition;
		this.interiorOr = true;
	}
}
