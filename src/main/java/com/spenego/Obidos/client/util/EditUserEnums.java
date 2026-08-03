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

package com.spenego.Obidos.client.util;

import java.util.EnumMap;

/**
 * @author spgdev@spenego.com - Jun 23, 2019
 */
public class EditUserEnums
{
	public EditUserEnums()
	{
		
	}
    private static EnumMap<FieldNumber, Boolean> eMap = new EnumMap<FieldNumber,Boolean>(FieldNumber.class);
	private static enum FieldNumber
    {
		username,
		fullname,
		primaryEmail,
		primaryPhone,
		authSource,
		lockedUser,
		resetUserPassword,
		requires2FAPasswordReset,
		is2FAEnabled,
		reset2FA,
		canCreateGlobalTemplate
    };

    public static void resetEmap()
    {
        eMap.put(FieldNumber.username, false);
        eMap.put(FieldNumber.fullname, false);
        eMap.put(FieldNumber.primaryEmail, false);
        eMap.put(FieldNumber.primaryPhone, false);
        eMap.put(FieldNumber.authSource, false);
        eMap.put(FieldNumber.lockedUser, false);
        eMap.put(FieldNumber.resetUserPassword, false);
        eMap.put(FieldNumber.requires2FAPasswordReset, false);
        eMap.put(FieldNumber.is2FAEnabled,false);
        eMap.put(FieldNumber.reset2FA, false);
        eMap.put(FieldNumber.canCreateGlobalTemplate, false);
    }


}
