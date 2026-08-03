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

import com.gwtplatform.mvp.client.annotations.DefaultGatekeeper;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.dto.UserDTO;

/**
 * @author spgdev@spenego.com - May 4, 2017
 */
@DefaultGatekeeper
public class LoggedInUserGatekeeper implements Gatekeeper
{
    private final CurrentUser currentUser;

    @Inject
    LoggedInUserGatekeeper(CurrentUser currentUser)
    {
        this.currentUser = currentUser;
    }

    @Override
    public boolean canReveal()
    {
    	if (currentUser == null)
    	{
    		return false;
    	}
    	boolean b = false;
    	UserDTO userDTO = currentUser.getUserDTO();
    	if (userDTO != null)
    	{
    		// Issue #806
    		b = ClientUtils.fromBoolean(userDTO.authSourceIsLocal() &&  userDTO.getPasswordChangeRequired());
    	}
    	gwtLog("password change requried: " + b);
        return currentUser.isLoggedIn() && !ClientUtils.isAdmin(currentUser) && !b;
    }
    
    private void gwtLog(final String message)
    {
    	ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

}
