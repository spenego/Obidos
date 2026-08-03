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
import com.spenego.Obidos.shared.dto.LicenseStats;

/**
 * check if user is logged in and license is in 2nd month or grace period
 * or beyond
 * @author spgdev@spenego.com - Mar 28, 2020
 */
public class LoggedInUserLicenseCanCreateShareGateKeepr implements Gatekeeper
{
    private final CurrentUser currentUser;

    @Inject
    LoggedInUserLicenseCanCreateShareGateKeepr(CurrentUser currentUser)
    {
        this.currentUser = currentUser;
    }

	@Override
	public boolean canReveal()
	{
		if (ClientUtils.isAdmin(currentUser))
		{
			return false;
		}
		LicenseStats license = currentUser.getLoginResult().getLicenseStats();
		int state = license.getLicenseExpirationState();
				
		return currentUser.isLoggedIn() && state < LicenseStats.LICENSE_STATE_GRACE_PERIOD_SECOND_MONTH;
	}
    private void gwtLog(String message)
    {
    	ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

}
