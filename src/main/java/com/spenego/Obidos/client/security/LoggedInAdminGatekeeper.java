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

package com.spenego.Obidos.client.security;

import javax.inject.Inject;

import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;

/**
 * @author spgdev@spenego.com - Jun 15, 2019
 */
public class LoggedInAdminGatekeeper implements Gatekeeper
{
    private final CurrentUser currentUser;

    @Inject
    LoggedInAdminGatekeeper(CurrentUser currentUser)
    {
        this.currentUser = currentUser;
    }

	@Override
	public boolean canReveal()
	{
		return currentUser.isLoggedIn() && ClientUtils.isAdmin(currentUser);
	}
	
	private void gwtLog(String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}
}
