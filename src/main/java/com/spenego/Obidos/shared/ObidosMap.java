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

package com.spenego.Obidos.shared;

import java.util.HashMap;
import java.util.Map;

/**
 * A map to return value from key and key from value
 * Ref: https://stackoverflow.com/questions/1383797/java-hashmap-how-to-get-key-from-value
 * @author spgdev@spenego.com - Feb 5, 2019
 *
 * @param <K>
 * @param <V>
 */
public class ObidosMap<K, V> extends HashMap<K, V>
{
	/**
	 * @author spgdev@spenego.com - Feb 5, 2019
	 */
	private static final long serialVersionUID = 1L;
	Map<V, K> reverseMap = new HashMap<V, K>();

	@Override
	public V put(K key, V value)
	{
		reverseMap.put(value, key);
		return super.put(key, value);
	}

	public K getKey(V value)
	{
		return reverseMap.get(value);
	}
}
