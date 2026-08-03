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

package com.spenego.Obidos.server.utils;

import java.util.function.Supplier;

/**
 * Defines a supplier that ensures only one T is ever supplied by it (the value is cached for subsequent requests).
 *
 * @author mmorgan
 *
 * @param <T>
 */
public final class CachedSupplier<T> implements Supplier<T> {
	private T o;
	private final Supplier<T> supplier;

	public CachedSupplier(final Supplier<T> supplier) {
		this.supplier = supplier;
	}

	@Override
	public T get() {
		if (o == null) {
			o = supplier.get();
		}

		return o;
	}
}
