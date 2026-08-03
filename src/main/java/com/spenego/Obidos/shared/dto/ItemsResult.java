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

/**
 * The {@code ItemsResult} class represents a result set of <@code LimitedItemDTO> items.
 * This will typically be used to display a list of items that match the search criteria.
 *
 * @author  Mike Morgan
 * @see     com.spenego.Obidos.shared.dto.ItemExpiration
 * @see     com.spenego.Obidos.shared.dto.ItemDTO
 * @see     com.spenego.Obidos.shared.dto.LimitedItemDTO
 * @see     com.spenego.Obidos.shared.dto.ObidosResult
 * @since   Obidos1.0
*/

public final class ItemsResult  extends ObidosResult<LimitedItemDTO> implements Serializable {
	private static final long serialVersionUID = 1L;

	protected Class<LimitedItemDTO> getElementClass() { return LimitedItemDTO.class; }

	public ItemsResult() {}

	public <T extends Clearable> ItemsResult(final Integer pageSize, final Integer startRow, final Collection<Long> preSelectedContainers, final BiFunction<Supplier<Stream<T>>,Class<LimitedItemDTO>,List<LimitedItemDTO>> converter, final IntSupplier totalSupplier, final Supplier<Stream<T>> elementSupplier) {
		super(pageSize, startRow, preSelectedContainers, converter, totalSupplier, elementSupplier);
	}

	public Integer getTotalItems() {
		return getTotal();
	}
}
