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

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.CapabilityDTO;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;
import com.spenego.Obidos.shared.dto.LimitedUserForAdminResult;
import com.spenego.Obidos.shared.dto.LimitedUserResult;
import com.spenego.Obidos.shared.dto.PassComplexityDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public interface UserManagementActions {
	Long createUser(User admin, UserDTO user, CapabilityDTO capabilities, Boolean notifyUser, String emailComment);

	UserDTO getUser(User requestor, Long id) throws NoSuchRecordException, ServerSideException;
	LimitedUserDTO getUserByUsername(String username);
	LimitedUserForAdminResult getUsers(User requestor, UserDTO userPatterns, Collection<Long> preSelectedUsers, Integer offset, Integer count, List<OrderBy> orderby);
	void setBlockAudit(boolean blockAudit);

	Void modifyUser			(User requestor, UserDTO user);
	Void changePassword		(User user, String oldPassword, String newPassword);
	Void createKeypair		(User user, byte[] passPhrase);
 	Void resetPassphrase	(User user, String password, byte[] newPassphrase, String passwordResetToken, byte[] twoFACode, PassphraseHash passphraseHash);
	Void updatePassphrase	(User user, PassphraseHash oldPassphraseHash, byte[] newPassphrase, byte[] twoFACode, Consumer<User> consumer);
	Void deleteUsers		(User admin, Collection<Long> userIds, Boolean deletePermanently);

	// A method used to delete users in bulk from a script.
	Void deleteUsersByUsername(User admin, Collection<String> userNames, Boolean deletePermanently);

	Integer getTotalUserCount(User caller);

	/**
	 * Restores (un-deletes) previously deleted (marked as deleted, not permanently deleted) user.
	 *
	 * @param caller
	 * @param userIds
	 * @param requestedUsernames
	 */
	Void restoreUsers(User admin, List<Long> userIds, List<String> requestedUsernames);

	/**
	 * Locks users.
	 *
	 * @param caller
	 * @param userId
	 * @return
	 */
	Void lockUsers(User admin, Collection<Long> userIds);

	/**
	 * Removes the lock flag from users.
	 *
	 * @param caller
	 * @param userId
	 * @return
	 */
	Void unLockUsers(User admin, Collection<Long> userIds);

	Void unTombstoneUsers(User admin, Collection<Long> userIds);

	/**
	 * The service layer must ensure that only admin are calling this method. It does not check.
	 *
	 * @param admin
	 * @param userId
	 * @return
	 */
	Void			delete(User admin, Collection<Long> userId);

	Boolean			keypairExists(User caller, Long userId);

	void authenticateTwoFactorCode(User user, byte[] twoFactorAuthCode);

	/**
	 *
	 * @param create this password complexity rule set.
	 * @return
	 */
	Void createPasswordComplexity(User admin, PassComplexityDTO rules);

	/**
	 *
	 * @param name the name of the password complexity record to return. If null is passed, the current system wide value is returned.
	 * @return
	 */
	PassComplexityDTO getPasswordComplexity(String name);

	Void validatePassComplexity(User user, String pass, byte passType);

	LimitedUserResult getLoggedInUsers(User admin, Integer secondsSinceLastActivity);
	Void refreshUser(User user); // used to update the user's activity timestamp (used in conjunction with getLoggedInUsers)
	Void logout(User user);
}
