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
 * General interface for a translator.
 */
public interface Translator
{
    /**
     * size of the output block on encoding produced by getDecodedBlockSize()
     * bytes.
     */
    public int getEncodedBlockSize();

    public int encode(byte[] in, int inOff, int length, byte[] out, int outOff);

    /**
     * size of the output block on decoding produced by getEncodedBlockSize()
     * bytes.
     */
    public int getDecodedBlockSize();

    public int decode(byte[] in, int inOff, int length, byte[] out, int outOff);
}
