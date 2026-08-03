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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.spenego.Obidos.shared.Selectable;

/**
 * {@code SpenegoResult} is the base class for all result sets returned from
 * Obidos.
 * This will typically be used to display a list of T that match the search criteria.
 * The number of elements contained within each SpenegoResult is constrained by pageSize.
 *
 * @author  Mike Morgan
 * @since   Obidos1.0
*/

public abstract class ObidosResult<T extends Clearable & HasId & Selectable> extends BaseShared implements Clearable {
	private static final long serialVersionUID = 1L;
	private ArrayList<T>	elements;
	private Integer			total;
	private Integer			pageSize;
	private Integer			startRow;

	protected ObidosResult() {}

	protected ObidosResult(final Integer pageSize, final Integer startRow) {
		this.pageSize = pageSize;
		this.startRow = startRow;
	}

	abstract Class<T> getElementClass();

	protected <X extends Clearable> ObidosResult(final Integer pageSize, final Integer startRow, final Collection<Long> psc, final BiFunction<Supplier<Stream<X>>,Class<T>,List<T>> converter, final IntSupplier totalSupplier, final Supplier<Stream<X>> elementSupplier) {
		this.pageSize = pageSize;
		this.startRow = startRow;
		setTotal(totalSupplier.getAsInt());
		if (!noResults()) {
			setElements(converter.apply(elementSupplier, getElementClass()));
			if (psc != null && !psc.isEmpty()) {
				final Collection<Long> c = new HashSet<>(psc);
				getElements().forEach(e -> e.setSelected(c.contains(e.getId())));
			}
		}
	}

	@Override
	public void clear() {
		if (elements != null) {
			for(int i=0 ; i < elements.size(); i++) {
				elements.get(i).clear();
				elements.set(i, null);
			}
			elements.clear();
			elements = null;
		}
		total = pageSize = startRow = null;
	}

	/**
	 *
	 * @return true if the result set is empty, false if there are elements.
	 */
	public final boolean noResults()	{ return total.intValue() == 0; }
	public final List<T> getElements()	{ return elements; }
	public final Integer getTotal()		{ return total; }
	public final Integer getPageSize()	{ return pageSize; }
	public final Integer getStartRow()	{ return startRow; }

	public final void setElements(final Collection<T> elements)	{ this.elements = toArrayList(elements); }
	public final void setTotal(final Integer total)				{ this.total = total; }
	public final void setPageSize(final Integer pageSize)		{ this.pageSize = pageSize; }
	public final void setStartRow(final Integer startRow)		{ this.startRow = startRow; }
}
