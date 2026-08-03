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
import org.springframework.transaction.annotation.Transactional;

import com.spenego.Obidos.client.rpc.LoginService;
import com.spenego.Obidos.server.actions.LoginActions;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LoginActionDTO;
import com.spenego.Obidos.shared.dto.LoginResult;
import com.spenego.Obidos.shared.dto.PasswordAnalysisResults;
import com.spenego.Obidos.shared.dto.SessionInfoDTO;
import com.spenego.Obidos.shared.exceptions.ObidosAuthenticationException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * A spring service
 *
 * @author spgdev@spenego.com - Dec 18, 2016
 */
@Service("loginService")
public final class LoginServiceImpl extends ObidosService implements LoginService {
	private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

	@Autowired protected final LoginActions loginActions = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	private int getMaxInactiveInterval() {
		return sessionInfo().getMaxInactiveInterval();
	}

	/**
	 * Authenticates a user either from sent cookie or the credentials.
	 *
	 * @param loginAction
	 *            Contains login type (via cookie or via credentials). if login type is a via cookie, there will be the login cookie otherwise
	 *            there will be username and password If authentication type is via credentials:
	 *              - get user out of database by username
	 *              - verify hashed password matches
	 *              - save session
	 *            If authentication type is via cookie:
	 *              - get user out of session
	 *              - verify cookie in session matches
	 * @return LoginResult on success
	 * @throws ObidosAuthenticationException on failure
	 */
	@Transactional @Override
	public LoginResult login(final LoginActionDTO loginAction) throws ObidosAuthenticationException, PermissionDeniedException, ServerSideException {
		return anonymousFunction("Login", "login", false, () -> loginActions.login(loginAction, getMaxInactiveInterval()));
	}

	@Transactional @Override
	public Void logout(final AuthCredsDTO creds) throws ServerSideException {
		return userOrAdminFunction(creds, "logout", "logout", false, loginActions::logout);
	}

	@Transactional(readOnly = true) @Override
	public PasswordAnalysisResults checkPassStrength(AuthCredsDTO creds, String password, int type) throws ServerSideException {
		final User user = authenticator.checkLoggedIn(creds);
		return anonymousFunction("check password strength", "checkPasswordStrength", true, () -> loginActions.checkPassStrength(user, password, type));
	}

	@Override
	public PasswordAnalysisResults checkPassStrength(AuthCredsDTO creds, String pass, int type, Long guessesPerSecond)
			throws ServerSideException
	{
		final User user = authenticator.checkLoggedIn(creds);
		return anonymousFunction("check password strength", "checkPasswordStrength", true, () -> loginActions.checkPassStrength(user, pass, type, guessesPerSecond));
	}

	@Override
	public PasswordAnalysisResults checkPassStrengthForGeneratedPassword(AuthCredsDTO creds, String pass)
			throws ServerSideException
	{
		final User user = authenticator.checkLoggedIn(creds);
		if (user == null)
		{
			throw new ServerSideException("User is not logged in");
		}

		return loginActions.checkPassStrengthForGeneratedPassword(user, pass);
				
	}

	private LoginResult loginViaCookie(final AuthCredsDTO creds) {
		final User user = authenticator.checkLoggedIn(creds);

		if (user == null) {
			throw new ServerSideException("User is not logged in");
		}

		logger.info(() -> "User " + user.getUsername() + " is logged in");

		return loginActions.createLoginResult(user, null, getMaxInactiveInterval());
	}


	@Transactional(readOnly = true) @Override
	public LoginResult isCurrentUserLoggedIn(final AuthCredsDTO creds) throws ServerSideException {
		return anonymousFunction("Login via cookie", "isCurrentUserLoggedIn", false, () -> loginViaCookie(creds));
	}

	@Transactional(readOnly = true) @Override
	public Void refreshSession(final AuthCredsDTO creds) throws ServerSideException {
		authenticator.pingSession(creds);
		return null;
	}

	@Transactional(readOnly = true) @Override
	public SessionInfoDTO sessionInfo() throws ServerSideException {
		return authenticator.sessionInfo();
	}


}
