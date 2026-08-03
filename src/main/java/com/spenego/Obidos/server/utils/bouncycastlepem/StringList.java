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
 * An interface defining a list of strings.
 */
public interface StringList
    extends Iterable<String>
{
    /**
     * Add a String to the list.
     *
     * @param s the String to add.
     * @return true
     */
    boolean add(String s);

    /**
     * Get the string at index index.
     *
     * @param index the index position of the String of interest.
     * @return the String at position index.
     */
    String get(int index);

    int size();

    /**
     * Return the contents of the list as an array.
     *
     * @return an array of String.
     */
    String[] toStringArray();

    /**
     * Return a section of the contents of the list. If the list is too short the array is filled with nulls.
     *
     * @param from the initial index of the range to be copied, inclusive
     * @param to the final index of the range to be copied, exclusive.
     * @return an array of length to - from
     */
    String[] toStringArray(int from, int to);
}
