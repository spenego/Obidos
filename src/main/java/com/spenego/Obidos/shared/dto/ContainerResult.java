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

public final class ContainerResult extends ObidosResult<ContainerDTO> implements Serializable {
	private static final long serialVersionUID = 1L;

	protected Class<ContainerDTO> getElementClass() { return ContainerDTO.class; }

	public ContainerResult() {}

	public <T extends Clearable> ContainerResult(final Integer pageSize, final Integer startRow, final Collection<Long> preSelectedContainers, final BiFunction<Supplier<Stream<T>>,Class<ContainerDTO>,List<ContainerDTO>> converter, final IntSupplier totalSupplier, final Supplier<Stream<T>> elementSupplier) {
		super(pageSize, startRow, preSelectedContainers, converter, totalSupplier, elementSupplier);
	}

	public List<ContainerDTO> getContainers() {
		return getElements();
	}

	public void setContainers(Collection<ContainerDTO> containers) {
		setElements(containers);
	}

	public Integer getTotalContainers() {
		return getTotal();
	}

	public void setTotalContainers(Integer totalContainers) {
		setTotal(totalContainers);
	}
}
