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

package com.spenego.Obidos.server.utils.bouncycastlepem;

/**
 * Class representing a PEM header (name, value) pair.
 */
public class PemHeader {
	private String name;
	private String value;

	/**
	 * Base constructor.
	 *
	 * @param name
	 *            name of the header property.
	 * @param value
	 *            value of the header property.
	 */
	public PemHeader(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public int hashCode() {
		return getHashCode(this.name) + 31 * getHashCode(this.value);
	}

	public boolean equals(Object o) {
		if (!(o instanceof PemHeader)) {
			return false;
		}

		PemHeader other = (PemHeader) o;

		return other == this || (isEqual(this.name, other.name) && isEqual(this.value, other.value));
	}

	private static int getHashCode(String s) {
		if (s == null) {
			return 1;
		}

		return s.hashCode();
	}

	private static boolean isEqual(String s1, String s2) {
		if (s1 == s2) {
			return true;
		}

		if (s1 == null || s2 == null) {
			return false;
		}

		return s1.equals(s2);
	}

}
