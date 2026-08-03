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

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * @author spgdev@spenego.com - Jun 10, 2017
 */
public class ValidatePhoneNumber
{

    public ValidatePhoneNumber()
    {
    }

    public static void validate(String phoneNumber)
    {
        if (phoneNumber == null) {
            return;
        }
        if (phoneNumber.length() == 0) {
            return;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        String defaultRegion = "";
        try {
            phoneUtil.parse(phoneNumber, defaultRegion);
        } catch (NumberParseException e) {
            throw new ServerSideException("Invalid phone number: " + e.toString());
        }
    }

}
