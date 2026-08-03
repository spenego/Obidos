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

package com.spenego.Obidos.shared.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class UserDefinedFieldResult extends ObidosResult<UserDefinedFieldDTO> implements Serializable {
	private static final long serialVersionUID = 1L;

	protected Class<UserDefinedFieldDTO> getElementClass() { return UserDefinedFieldDTO.class; }

	public UserDefinedFieldResult() {}

	public UserDefinedFieldResult(final Integer pageSize, final Integer startRow) {
		super(pageSize, startRow);
	}

	public <T extends Clearable> UserDefinedFieldResult(final Integer pageSize, final Integer startRow, final Collection<Long> preSelectedElements, final BiFunction<Supplier<Stream<T>>,Class<UserDefinedFieldDTO>,List<UserDefinedFieldDTO>> converter, final IntSupplier totalSupplier, final Supplier<Stream<T>> elementSupplier) {
		super(pageSize, startRow, preSelectedElements, converter, totalSupplier, elementSupplier);
	}

	public List<UserDefinedFieldDTO> getFields() {
		return getElements();
	}

	public Integer getTotalFields() {
		return getTotal();
	}
}
