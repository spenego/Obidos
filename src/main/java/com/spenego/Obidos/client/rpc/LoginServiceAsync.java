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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LoginActionDTO;
import com.spenego.Obidos.shared.dto.LoginResult;
import com.spenego.Obidos.shared.dto.PasswordAnalysisResults;
import com.spenego.Obidos.shared.dto.SessionInfoDTO;

/**
 *
 * @author spgdev@spenego.com - Dec 18, 2016
 */
public interface LoginServiceAsync
{
	void checkPassStrength(AuthCredsDTO creds, String passphrase, int type, AsyncCallback<PasswordAnalysisResults> callback);
	void checkPassStrength(AuthCredsDTO creds, String passphrase, int type, Long guessesPerSecond, AsyncCallback<PasswordAnalysisResults> callback);
	void checkPassStrengthForGeneratedPassword(AuthCredsDTO creds, String pass, AsyncCallback<PasswordAnalysisResults> callback);
	void login(LoginActionDTO loginAction, AsyncCallback<LoginResult> callback);
	void isCurrentUserLoggedIn(AuthCredsDTO creds, AsyncCallback<LoginResult> callback);
	void sessionInfo(AsyncCallback<SessionInfoDTO> callback);
	void refreshSession(AuthCredsDTO creds, AsyncCallback<Void> callback);
	void logout(AuthCredsDTO creds, AsyncCallback<Void> callback);

}
