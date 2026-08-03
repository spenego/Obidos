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

public final class SmtpConfigResult extends ObidosResult<LimitedSmtpConfigDTO> {
	private static final long serialVersionUID = 1L;

	protected Class<LimitedSmtpConfigDTO> getElementClass() { return LimitedSmtpConfigDTO.class; }

	public SmtpConfigResult() {}

	public <T extends Clearable> SmtpConfigResult(final Integer pageSize, final Integer startRow, final Collection<Long> preSelectedElements, final BiFunction<Supplier<Stream<T>>,Class<LimitedSmtpConfigDTO>,List<LimitedSmtpConfigDTO>> converter, final IntSupplier totalSupplier, final Supplier<Stream<T>> elementSupplier) {
		super(pageSize, startRow, preSelectedElements, converter, totalSupplier, elementSupplier);
	}

	public List<LimitedSmtpConfigDTO> getLdapConfigList() {
		return getElements();
	}

	public void setSmtpConfigList(final Collection<LimitedSmtpConfigDTO> smtpConfigList) {
		setElements(smtpConfigList);
	}

	public Integer getTotalConfigurations() {
		return getTotal();
	}
	public void setTotalConfigurations(Integer totalConfigurations) {
		setTotal(totalConfigurations);
	}
}
