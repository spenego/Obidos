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

/**
 * @author spgdev@spenego.com - Mar 22, 2017
 */
package com.spenego.Obidos.shared.exceptions;

import java.io.Serializable;

/**
 * @author spgdev@spenego.com - Mar 22, 2017
 *
 */
public final class SessionMismatchException extends ServerSideException implements Serializable
{

    /**
     * @author spgdev@spenego.com - Mar 22, 2017
     */
    private static final long serialVersionUID = 1L;

    /**
     * @author spgdev@spenego.com - Mar 22, 2017
     */
    public SessionMismatchException()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * @author spgdev@spenego.com - Mar 22, 2017
     * @param message
     */
    public SessionMismatchException(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @author spgdev@spenego.com - Mar 22, 2017
     * @param cause
     */
    public SessionMismatchException(Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @author spgdev@spenego.com - Mar 22, 2017
     * @param message
     * @param cause
     */
    public SessionMismatchException(String message, Throwable cause)
    {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
