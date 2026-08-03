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

package com.spenego.Obidos.server.actions;

import com.spenego.Obidos.server.model.SystemConfig;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.LoginActionDTO;
import com.spenego.Obidos.shared.dto.LoginResult;
import com.spenego.Obidos.shared.dto.PasswordAnalysisResults;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.ObidosAuthenticationException;

public interface LoginActions {
	/**
	 * Authenticates a user either from sent cookie or the credentials.
	 *
	 * @param loginAction   Contains login type (via cookie or via credentials). if
	 * login type is a via cookie, there will be the login cookie otherwise there will be
	 * username and password
	 * If authentication type is via credentials:
	 *    - get user out of database by username
	 *      - verify hashed password matches
	 *        - save session
	 * If authentication type is via cookie:
	 *    - get user out of session
	 *    	- verify cookie in session matches
	 *  @return UserDTO on success
	 *  @throws ObidosAuthenticationException on failure
	 */
	LoginResult login(LoginActionDTO loginAction, int sessionTimeout) throws ObidosAuthenticationException;
	LoginResult createLoginResult(User user, SystemConfig sc, int sessionTimeout) throws ObidosAuthenticationException;
	Void logout(User user);

	PasswordAnalysisResults checkPassStrength(User user, String password,int type);
	PasswordAnalysisResults checkPassStrength(User user, String password,int type, Long guessesPerSecond);

	PasswordAnalysisResults checkPassStrengthForGeneratedPassword(User user, String password);


	boolean licenseIsExpired();
	int passwordAgeInDays(User user);
	UserDTO setDaysUntilPasswordExpiration(UserDTO user, int daysUntilPasswordExpiration);
	void validateLicense();
	User validateCredentialsAndLicense(AuthCredsDTO creds);
	boolean licenseHasEnteredSecondGracePeriod();
	LicenseStats updateCachedLicense();
	LicenseStats currentLicenseStats();
}
