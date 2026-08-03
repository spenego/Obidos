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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Supplies a stream on demand via a backing list. The motivation for this class was to provide a way
 * to provide a stream from a list without requiring the list to exist prior to the get being called
 * for the stream. Each call to get returns a new stream from the same list. This is to allow
 * multiple threads to work with their own stream.
 *
 * Currently having multiple threads process a stream is not generally implemented since Spring stores
 * transaction information in ThreadLocalStorage. Using multiple threads just causes a big mess of
 * deadlocks.
 *
 * @author mmorgan
 *
 * @param <T>
 */
public final class StreamSupplier<T> implements Supplier<Stream<T>> {
	private final Supplier<Collection<T>> collectionSupplier;
	private Collection<T> collection;

	private StreamSupplier(final Supplier<Collection<T>> supplier) {
		this.collectionSupplier = supplier;
	}

	private StreamSupplier(final Collection<T> collection) {
		this.collectionSupplier = null;
		this.collection = collection;
	}

	public static <Z> StreamSupplier<Z> create(final Supplier<Collection<Z>> supplier) {
		return new StreamSupplier<>(supplier);
	}

	public static <Z> StreamSupplier<Z> create(final Collection<Z> collection) {
		return new StreamSupplier<>(collection);
	}

	public synchronized Collection<T> collection() {
		if (collection == null) {
			collection = collectionSupplier.get();
		}
		return collection;
	}

	public synchronized List<T> list() {
		if (collection == null) {
			collection = collectionSupplier.get();
		}
		return (collection instanceof List) ? (List<T>) collection : new ArrayList<>(collection);
	}

	@Override
	public synchronized Stream<T> get() {
		if (collection == null) {
			collection = collectionSupplier.get();
		}
		return collection.stream();
	}
}
