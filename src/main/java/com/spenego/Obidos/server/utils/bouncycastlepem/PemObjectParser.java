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

import java.io.IOException;

/**
 * Base interface for parsers to convert PEM objects into specific objects.
 */
public interface PemObjectParser
{
    /**
     * Parse an object out of the PEM object passed in.
     *
     * @param obj the PEM object containing the details for the specific object.
     * @return a specific object represented by the  PEM object.
     * @throws IOException on a parsing error.
     */
    Object parseObject(PemObject obj) throws IOException;
}
