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

public abstract class BaseShared {
	protected BaseShared() { /* abstract */ }
	protected static <T> ArrayList<T> toArrayList(final Collection<T> values) { // convert a Collection to an ArrayList
		return values instanceof ArrayList ? (ArrayList<T>) values : new ArrayList<>(values);
	}
}
