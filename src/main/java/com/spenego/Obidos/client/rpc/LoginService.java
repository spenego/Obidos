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

package com.spenego.Obidos.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LoginActionDTO;
import com.spenego.Obidos.shared.dto.LoginResult;
import com.spenego.Obidos.shared.dto.PasswordAnalysisResults;
import com.spenego.Obidos.shared.dto.SessionInfoDTO;
import com.spenego.Obidos.shared.exceptions.ObidosAuthenticationException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * The relative path must start with rpc
 * @author spgdev@spenego.com - Dec 18, 2016
 */
@RemoteServiceRelativePath("rpc/loginService")
public interface LoginService extends RemoteService {
	public static class Utility {
        private final static LoginServiceAsync instance = (LoginServiceAsync) GWT.create(LoginService.class);
        public static LoginServiceAsync getInstance() { return instance; }
    }

	// check strength, validate password or passphrase
	PasswordAnalysisResults checkPassStrength(AuthCredsDTO creds, String pass, int type) throws ServerSideException;
	PasswordAnalysisResults checkPassStrength(AuthCredsDTO creds, String pass, int type, Long guessesPerSecond) throws ServerSideException;
	// just return the strength result
	PasswordAnalysisResults checkPassStrengthForGeneratedPassword(AuthCredsDTO creds, String pass) throws ServerSideException;

	LoginResult login(LoginActionDTO loginAction) throws ObidosAuthenticationException, ServerSideException;

	LoginResult isCurrentUserLoggedIn(AuthCredsDTO creds) throws ServerSideException;

	Void refreshSession(AuthCredsDTO creds) throws ServerSideException;

	Void logout(AuthCredsDTO creds) throws ServerSideException;

	SessionInfoDTO sessionInfo() throws ServerSideException;
}
