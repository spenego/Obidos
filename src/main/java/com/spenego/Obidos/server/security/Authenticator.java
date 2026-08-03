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

package com.spenego.Obidos.server.security;

import java.util.Collection;
import java.util.function.Supplier;

import javax.servlet.http.HttpSession;

import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.SessionInfoDTO;
import com.spenego.Obidos.shared.exceptions.SessionErrorException;

public interface Authenticator {
	void setSessionSupplier(Supplier<HttpSession> provider);
	void insertMockState() throws SessionErrorException;

	/**
	 * To make the UI easier to use, instead of requiring the user to pass
	 * the passphrase for every operation, we save a hash of the passphrase
	 * in the session.
	 *
	 * @return
	 */
	PassphraseHash getPassphraseHash();
	Void setPassphraseHash(PassphraseHash passphraseHash, boolean bypassRateLimiter);

	/**
	 * Calls to the service layer are authorized by checking the passed session
	 * hash with the stored session hash; retrieved via this call.
	 *
	 * @return
	 * @throws SessionErrorException
	 */
	String getSessionHash();
	String getSavedSessionHash();
	User authenticateCredentials(String username, final String password, final boolean createNewSessionState);
	User authenticateCookie(String sessionId);
	SessionInfoDTO getSessionInfo();

	/**
	 * check if the admin is logged in and valid in the session
	 *
	 * @param authCred
	 * @return UserDTO
	 * <p>
	 * @author spgdev@spenego.com - Jan 29, 2017
	 */
	User checkLoggedInAdmin(AuthCredsDTO authCred);

	/**
	 * A collection of users that have been active over the last hour is maintained. This methods returns that collection.
	 */
	Collection<User> getActiveUsers();

	/**
	 * check if user is logged in and valid in the session
	 *
	 * @param authCred
	 *            - came from the client side
	 * @return UserDTO
	 *             <p>
	 * @author spgdev@spenego.com - Jan 29, 2017
	 */
	User checkLoggedInUser(AuthCredsDTO authCred);
	User checkLoggedIn(AuthCredsDTO authCred);
	Integer remainingTimeInSession(AuthCredsDTO creds);
	SessionInfoDTO sessionInfo();
	void pingSession(AuthCredsDTO creds);
	void logout() throws SessionErrorException;
}
