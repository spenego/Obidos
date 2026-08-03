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
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.util.ClientUtils;

/**
 * Gatekeeper to check if user has logged in
 * @author spgdev@spenego.com - Jun 15, 2019
 */
public class LoggedInGatekeeper implements Gatekeeper
{
    private final CurrentUser currentUser;
    private final PlaceManager placeManager;

    @Inject
    LoggedInGatekeeper(CurrentUser currentUser, PlaceManager placeManager)
    {
        this.currentUser = currentUser;
        this.placeManager = placeManager;
    }

    @Override
    public boolean canReveal()
    {
    	if (currentUser != null) // it will never be null but just in case!
    	{
    		// Bug #820
            // When an unknown place was typed when the user not logged, an 
            // Umbrella exception is thrown. If the user is not logged, send to 
            // Login Place
            if (!currentUser.isLoggedIn())
            {
            	ClientUtils.showPage(placeManager, NameTokens.LOGIN);
            	return true; // must return true
            }
            return true;
    	}
    	else
    	{
    		return false;
    	}
    }


}
