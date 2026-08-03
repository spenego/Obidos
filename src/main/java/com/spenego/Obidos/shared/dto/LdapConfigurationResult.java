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

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class LdapConfigurationResult extends ObidosResult<LimitedLdapDTO> {
	private static final long serialVersionUID = 1L;

	protected Class<LimitedLdapDTO> getElementClass() { return LimitedLdapDTO.class; }

	public LdapConfigurationResult() {}

	public LdapConfigurationResult(final Integer start, final Integer count) {
		super(start, count);
	}

	public <T extends Clearable> LdapConfigurationResult(final Integer pageSize, final Integer startRow, final Collection<Long> preSelectedElements, final BiFunction<Supplier<Stream<T>>,Class<LimitedLdapDTO>,List<LimitedLdapDTO>> converter, final IntSupplier totalSupplier, final Supplier<Stream<T>> elementSupplier) {
		super(pageSize, startRow, preSelectedElements, converter, totalSupplier, elementSupplier);
	}

	public List<LimitedLdapDTO> getLdapConfigList() {
		return getElements();
	}

	public Integer getTotalConfigurations() {
		return getTotal();
	}
}
