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

import com.spenego.Obidos.shared.Selectable;

public abstract class UsersResult<T extends Clearable & HasId & Selectable> extends ObidosResult<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	public UsersResult() {}

	public <X extends Clearable> UsersResult(final Integer pageSize, final Integer startRow, final Collection<Long> preSelectedContainers, final BiFunction<Supplier<Stream<X>>,Class<T>,List<T>> converter, final IntSupplier totalSupplier, final Supplier<Stream<X>> elementSupplier) {
		super(pageSize, startRow, preSelectedContainers, converter, totalSupplier, elementSupplier);
	}

	public List<T> getUsers() {
		return getElements();
	}

	public Integer getTotalUsers() {
		return getTotal();
	}
}
