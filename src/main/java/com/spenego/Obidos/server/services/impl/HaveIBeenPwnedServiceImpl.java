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

package com.spenego.Obidos.server.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spenego.Obidos.client.rpc.HaveIBeenPwnedService;
import com.spenego.Obidos.server.actions.HaveIBeenPwnedActions;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.security.Authenticator;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@Service("haveIBeenPwnedService")
public class HaveIBeenPwnedServiceImpl implements HaveIBeenPwnedService
{
	private static final Logger logger = LoggerFactory.getLogger(HaveIBeenPwnedServiceImpl.class);

	@Autowired private Authenticator authenticator;
	@Autowired private HaveIBeenPwnedActions haveIBeenPwnedActions;

	@Override
	public String passwordFound(AuthCredsDTO creds, String password) throws ServerSideException
	{
		// make sure user is logged in. The user can be admin or a regular user
		User user = authenticator.checkLoggedIn(creds);
		logger.info(() -> "user " + user.getUsername() + " is logged in");
		if (Boolean.TRUE.equals(user.getAdministrator()))
		{
			logger.info(() -> "user " + user.getUsername() + " is an admin");
		}

		return checkWithHaveIBeenPwned(password);
	}

	/**
	 * Check if password is in haveibeenpwned.com database
	 *
	 * @param password
	 * @return Number of times password found as string
	 * null if password is not found in the database
	 * @throws ServerSideException
	 * <p>
	 * @author spgdev@spenego.com - Nov 11, 2018
	 */
	private String checkWithHaveIBeenPwned(String password) throws ServerSideException
	{
		Integer found = haveIBeenPwnedActions.checkPassword(password);
		if (found != null)
		{
			return Integer.toString(found);
		}
		return null;
	}
}
