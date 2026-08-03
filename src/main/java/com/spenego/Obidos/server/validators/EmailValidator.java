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

package com.spenego.Obidos.server.validators;

import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * Validate an email address
 * @author spgdev@spenego.com - Sep 13, 2017
 */
public class EmailValidator {

    public EmailValidator() { }

    public static void validateEmail(String email) {
        if (!org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(email)) {
            throw new ServerSideException("Invalid email address");
        }
    }
}
