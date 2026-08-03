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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.server.actions.CapabilityActions;
import com.spenego.Obidos.server.actions.EmailActions;
import com.spenego.Obidos.server.actions.LdapConfigActions;
import com.spenego.Obidos.server.actions.PasswordResetActions;
import com.spenego.Obidos.server.actions.SystemConfigActions;
import com.spenego.Obidos.server.actions.UserActions;
import com.spenego.Obidos.server.actions.UserManagementActions;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.server.validators.EmailValidator;
import com.spenego.Obidos.server.validators.ValidateBean;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.SharedSetQuality;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.CapabilityDTO;
import com.spenego.Obidos.shared.dto.CountryCodeDTO;
import com.spenego.Obidos.shared.dto.GroupDTO;
import com.spenego.Obidos.shared.dto.GroupResult;
import com.spenego.Obidos.shared.dto.LimitedUserForAdminResult;
import com.spenego.Obidos.shared.dto.LimitedUserResult;
import com.spenego.Obidos.shared.dto.PassComplexityDTO;
import com.spenego.Obidos.shared.dto.UserComboResult;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.dto.UserGroupComboResult;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;
import com.spenego.Obidos.shared.exceptions.UsernameExistsException;

@Service("userService")
public final class UserServiceImpl extends ObidosService implements UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired private final UserActions			userActions = null;
	@Autowired private final EmailActions			emailActions = null;
	@Autowired private final CapabilityActions		capabilityActions = null;
	@Autowired private final LdapConfigActions		ldapConfigActions = null;
	@Autowired private final SystemConfigActions	systemConfigActions = null;
	@Autowired private final PasswordResetActions	passwordResetActions = null;
	@Autowired private final UserManagementActions	userManagementActions = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

 	@Transactional(readOnly=true) @Override
	public UserDTO getUser(final AuthCredsDTO creds, final Long userId) throws ServerSideException {
		return userOrAdminFunction(creds, "get user", "getUser", false, caller -> userManagementActions.getUser(caller, userId));

		// testing-- dump spgdev@spenego.com - Aug 30, 2019
		// please do not delete
		/*
		String content = ServerUtils.dumpObjectToString(dto);
		logger.info(() -> "MMMMMMMMMMMMMMMMMMMMM dump UserdTO----------");
		logger.info(() -> content);
		logger.info(() -> "MMMMMMMMMMMMMMMMMMMMM dump UserdTO----------");
		*/
	}

 	private UserComboResult getUserCombo(final User caller, final Long userId) {
		final UserDTO user = userManagementActions.getUser(caller, userId);
		return new UserComboResult(user, ldapConfigActions.getAllConfigs(caller, null, null, null), capabilityActions.get(user));
 	}

 	@Transactional(readOnly=true) @Override
	public UserComboResult getUserCombo(final AuthCredsDTO creds, final Long userId) throws ServerSideException {
 		return userOrAdminFunction(creds, "get user combo", "getUserCombo", false, caller -> getUserCombo(caller, userId));
	}

	private Long createUser(final User admin, final UserDTO user, final CapabilityDTO capabilities, final Boolean notifyUser,final String emailComment) throws ServerSideException {
		if (user.authSourceIsLocal()) {
			logger.info(() -> "Auth source is local");
			user.setPasswordChangeRequired(true);
		} else {
			// set a random password otherwise validator catches it
			logger.info(() -> "Auth source is not local, setting random password");
			String password = ServerUtils.randomString();
			user.setPassword(password);
		}

		// Validate userDTO at first opportunity
		ValidateBean.validate(user);

		return userManagementActions.createUser(admin, user, capabilities, notifyUser, emailComment);
	}

	/**
	 *
	 * @param creds
	 * @param userInfo
	 * @param capabilities
	 * @return
	 * @throws UsernameExistsException
	 * @throws ServerSideException
	 * <p>
	 * Add 	parameters for email notification - spgdev@spenego.com - Aug 26, 2018
	 */
 	@Transactional @Override
	public Long createUser(final AuthCredsDTO creds, final UserDTO userInfo, final CapabilityDTO capabilities, final Boolean notifyUser, final String emailComment) throws UsernameExistsException, ServerSideException {
		return adminFunction(creds, "create a user", "createUser", true, admin -> createUser(admin, userInfo, capabilities, notifyUser, emailComment));
	}

 	@Transactional @Override
	public Void deleteUsers(final AuthCredsDTO creds, final ArrayList<Long> userIds, final Boolean deletePermanently) throws ServerSideException {
		return adminFunction(creds, "delete a user", "deleteUser", true, admin -> userManagementActions.deleteUsers(admin, userIds, deletePermanently));
	}

 	@Transactional @Override
	public Void deleteUsers(final AuthCredsDTO creds, final ArrayList<Long> userIds) throws ServerSideException {
		return deleteUsers(creds, userIds, Boolean.FALSE);
	}

 	@Transactional @Override
	public Void restoreUsers(final AuthCredsDTO creds, final ArrayList<Long> userIds, final ArrayList<String> requestedUsernames) throws ServerSideException {
 		return adminFunction(creds, "restore a user", "restoreUser", true, admin -> userManagementActions.restoreUsers(admin, userIds, requestedUsernames));
	}

 	@Transactional @Override
	public Void lockUsers(final AuthCredsDTO creds, final ArrayList<Long> userIds) throws ServerSideException {
 		return adminFunction(creds, "lock a user", "lockUsers", true, admin -> userManagementActions.lockUsers(admin, userIds));
	}

 	@Transactional @Override
	public Void unlockUsers(final AuthCredsDTO creds, final ArrayList<Long> userIds) throws ServerSideException {
 		return adminFunction(creds, "unlock a user", "unlockUsers", true, admin -> userManagementActions.unLockUsers(admin, userIds));
	}

 	@Transactional @Override
	public Void unTombstoneUsers(final AuthCredsDTO creds, final ArrayList<Long> userIds) throws ServerSideException {
 		return adminFunction(creds, "unlock a user", "unlockUsers", true, admin -> userManagementActions.unTombstoneUsers(admin, userIds));
	}

 	@Transactional @Override
	public Void createKeypair(final AuthCredsDTO creds, final byte[] passphrase) throws ServerSideException {
		return userFunction(creds, "create a key-pair", "createKeypairForUser", user -> userManagementActions.createKeypair(user, passphrase));
	}

 	@Transactional @Override
 	public Void resetPassphrase(final AuthCredsDTO creds, final String password, final byte[] newPassphrase, final String passwordResetToken, final byte[] twoFACode) throws ServerSideException {
		return userFunction(creds, "reset passphrase", "resetPassphrase", user -> userManagementActions.resetPassphrase(user, password, newPassphrase, passwordResetToken, twoFACode, authenticator.getPassphraseHash()));
	}

 	@Transactional @Override
	public Void updatePassphrase(final AuthCredsDTO creds, final byte[] newPassphrase, final byte[] twoFactorAuthCode) throws ServerSideException {
		return userFunction(creds, "update passphrase", "updatePassphrase", user -> userManagementActions.updatePassphrase(user, summonPWHash(), newPassphrase, twoFactorAuthCode, u -> cacheNewPassphraseHash(u, newPassphrase, twoFactorAuthCode, true)));
	}

 	@Transactional @Override
	public Void modifyUser(final AuthCredsDTO creds, final UserDTO userInfo) throws ServerSideException {
		return userOrAdminFunction(creds, "modify user", "modifyUser", true, caller -> userManagementActions.modifyUser(caller, userInfo));
	}

 	@Transactional @Override
	public Void changePassword(final AuthCredsDTO creds, final String oldPassword, final String newPassword) throws ServerSideException {
		if (creds == null) {
			throw new PermissionDeniedException("Denied! You must supply credentials to change your password.");
		}

		return anonymousFunction("change password", "changePassword", true, () -> userManagementActions.changePassword(authenticator.checkLoggedIn(creds), oldPassword, newPassword));
	}

 	@Transactional(readOnly=true) @Override
	public LimitedUserForAdminResult getUsers(final AuthCredsDTO creds, final UserDTO userInfo, final ArrayList<Long> preSelectedUsers, final Integer first, final Integer count, final ArrayList<OrderBy> orderby) throws ServerSideException {
		return adminFunction(creds, "get user list matching pattern", "getUsers", false, user -> userManagementActions.getUsers(user, userInfo, preSelectedUsers, first, count, orderby));
	}

 	@Transactional @Override
	public Long createGroup(final AuthCredsDTO creds, final String name, final String comment) throws ServerSideException {
		return userFunction(creds, "create group " + name, "createGroup", user -> userActions.createGroup(user, name, comment, summonPWHash()));
	}

 	@Transactional(readOnly=true) @Override
	public GroupDTO getGroup(final AuthCredsDTO creds, final Long id) throws ServerSideException {
		return userOrAdminFunction(creds, "get group", "getGroup", true, admin -> userActions.getGroupDTO(id));
	}

 	@Transactional(readOnly=true) @Override
	public GroupResult getGroups(final AuthCredsDTO creds, final String search, final ArrayList<Long> preSelectedGroups, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
		return userFunction(creds, "get groups", "getGroups", user -> userActions.getGroups(user, search, preSelectedGroups, first, count, orderBy));
	}

 	@Transactional @Override
	public Void updateGroup(final AuthCredsDTO creds, final GroupDTO group) throws ServerSideException {
		return userFunction(creds, "Update group", "updateGroup", user -> userActions.updateGroup(user, group));
	}

 	@Transactional @Override
	public Void addUsersToGroup(final AuthCredsDTO creds, final ArrayList<Long> userIds, final Long groupId, final String shareComment, final PostOpActions postOpActions) throws ServerSideException {
		return userFunction(creds, "Add user to group", "addUserToGroup", user -> userActions.addUsersToGroup(user, userIds, groupId, summonPWHash(), postOpActions,  shareComment));
	}

 	@Transactional(readOnly=true) @Override
	public LimitedUserResult getUsersForGroup(final AuthCredsDTO creds, final Long groupId, final UserDTO userPatterns, final SharedSetQuality sharedSetQuality, final ArrayList<Long> preSelectedUsers, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
		return userFunction(creds, "get users for a group", "getUsersForGroup", user -> log(userActions.getUsersForGroup(user, groupId, userPatterns, sharedSetQuality, preSelectedUsers, first, count, orderBy)));
	}

 	@Transactional @Override
	public Void removeUsersFromGroup(final AuthCredsDTO creds, final ArrayList<Long> userIds, final Long groupId) throws ServerSideException {
		return userFunction(creds, "remove a user from group", "removeUserFromGroup", user -> userActions.removeUsersFromGroup(user, userIds, groupId, summonPWHash()));
	}

 	@Transactional @Override
	public Void deleteGroups(final AuthCredsDTO creds, final ArrayList<Long> groupIds) throws ServerSideException {
		return userFunction(creds, "delete a group", "deleteGroups", user -> userActions.deleteGroups(user, groupIds, summonPWHash()));
	}

 	@Transactional @Override
	public Void sendUserCreationEmail(final AuthCredsDTO creds, final Long userId) throws ServerSideException {
		return adminFunction(creds, "send a user creation email", "sendUserCreationEmail", true, admin -> emailActions.sendUserCreationEmail(admin, userId));
	}

 	@Transactional @Override
	public Void sendUserCreationEmail(final AuthCredsDTO creds, final Long userId, final String comment) throws ServerSideException {
		return adminFunction(creds, "send a user creation email", "sendUserCreationEmail", true, admin -> emailActions.sendUserCreationEmail(admin, userId,comment));
	}

 	@Transactional(readOnly=true) @Override
	public Boolean keypairExists(final AuthCredsDTO creds, final Long userId) throws ServerSideException {
		return userFunction(creds, "check for keypair existance", "keypairExists", user -> userManagementActions.keypairExists(user, userId));
	}

 	@Transactional(readOnly=true) @Override
	public Void cachePassphrase(final AuthCredsDTO creds, final byte[] passphrase, final byte[] twoFactorAuthCode) throws ServerSideException {
		return userFunction(creds, "cache the passphrase", "cachePassphrase", user -> cachePassphraseHash(user, passphrase, twoFactorAuthCode, false));
	}

 	@Transactional(readOnly=true) @Override
	public Boolean isPassphraseCached(final AuthCredsDTO creds) throws ServerSideException {
		return authenticator.getPassphraseHash() != null;
	}

 	@Transactional @Override
	public Void sendPasswordResetEmail(final String username) throws ServerSideException {
		return anonymousFunction("request a password reset email", "sendPasswordResetEmail", true, () -> passwordResetActions.queuePasswordResetEmail(username));
	}

 	@Transactional @Override
	public Void sendPassphraseResetEmail(final AuthCredsDTO creds) throws ServerSideException {
		return userFunction(creds, "request a passphrase reset email", "sendPassphraseResetEmail", passwordResetActions::queuePassphraseResetEmail);
	}

 	@Transactional @Override
	public Void sendPasswordResetWithAccountLookupEmail(final String emailAddress, final String subject) throws ServerSideException {
	    EmailValidator.validateEmail(emailAddress);
		return anonymousFunction("request a password reset email with email lookup", "sendPasswordResetWithAccountLookupEmail", true, () -> passwordResetActions.queuePasswordResetWithAccountLookupEmail(emailAddress,subject));
	}

	@Transactional @Override
	public Void resetPassword(final String token, final String newPassword, final byte[] twoFactorCode) throws ServerSideException {
		return anonymousFunction("reset password", "resetPassword", true, () -> passwordResetActions.resetPassword(token, newPassword, twoFactorCode));
	}

 	@Transactional(readOnly=true) @Override
	public GroupResult getGroupsForContainer(final AuthCredsDTO creds, final Long containerId, final String nameSearch, final SharedSetQuality sharedSetQuality, final ArrayList<Long> preSelectedGroups, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
		return userFunction(creds, "get groups sharing container", "getGroupsForContainer", user -> log(userActions.getGroupsForContainer(user, containerId, nameSearch, sharedSetQuality, preSelectedGroups, first, count, orderBy)));
	}

 	@Transactional(readOnly=true) @Override
	public LimitedUserResult getUsersForContainer(final AuthCredsDTO creds, final Long containerId, final UserDTO userSearchTemplate, final SharedSetQuality sharedSetQuality, final ArrayList<Long> preSelectedUsers, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
		return userFunction(creds, "get users (not)sharing container", "getUsersForContainer", user -> log(userActions.getUsersForContainer(user, containerId, userSearchTemplate, sharedSetQuality, preSelectedUsers, first, count, orderBy)));
 	}

	@Transactional(readOnly=true) @Override
	public UserGroupComboResult getContainerShares(final AuthCredsDTO creds, final Long containerId, final String searchString, final ArrayList<Long> preSelectedElements, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
 		return userFunction(creds, "get users and groups sharing container", "getComboForContainer", user -> log(userActions.getSharesForContainer(user, containerId, searchString, preSelectedElements, first, count, orderBy)));
 	}

 	@Transactional(readOnly=true) @Override
	public UserGroupComboResult getItemShares(final AuthCredsDTO creds, final Long itemId, final String searchString, final ArrayList<Long> preSelectedElements, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
 		return userFunction(creds, "get users and groups sharing item", "getComboForItem", user -> log(userActions.getSharesForItem(user, itemId, searchString, preSelectedElements, first, count, orderBy)));
 	}

 	@Transactional(readOnly=true) @Override
	public LimitedUserResult getUsersForItem(final AuthCredsDTO creds, final Long itemAssignmentId, final UserDTO userPatterns, final SharedSetQuality sharedSetQuality, final ArrayList<Long> preSelectedUsers, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
 		return userFunction(creds, "get recipients of shared item", "getUsersForItem", user -> log(userActions.getUsersForItem(user, itemAssignmentId, userPatterns, sharedSetQuality, preSelectedUsers, first, count, orderBy)));
	}

 	@Transactional(readOnly=true) @Override
	public GroupResult getGroupsForItem(final AuthCredsDTO creds, final Long itemAssignmentId, final String searchString, final SharedSetQuality sharedSetQuality, final ArrayList<Long> preSelectedGroups, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
 		return userFunction(creds, "get groups of item", "getGroupsForItem", user -> log(userActions.getGroupsForItem(user, itemAssignmentId, searchString, sharedSetQuality, preSelectedGroups, first, count, orderBy)));
	}

 	@Transactional(readOnly=true) @Override
	public PassComplexityDTO getPassComplexity(final String name) throws ServerSideException {
		return anonymousFunction("get password complexity rules", "getPassComplexity", true, () -> userManagementActions.getPasswordComplexity(name));
 	}

 	@Transactional @Override
	public Void createPassComplexity(final AuthCredsDTO creds, final PassComplexityDTO rules) throws ServerSideException {
		return adminFunction(creds, "create password complexity rules", "createPassComplexity", true, admin -> userManagementActions.createPasswordComplexity(admin, rules));
 	}

 	@Transactional(readOnly=true) @Override
	public LimitedUserResult getLoggedInUsers(final AuthCredsDTO creds) throws ServerSideException {
		return adminFunction(creds, "get users currently logged in", "getLoggedInUsers", true, admin -> log(userManagementActions.getLoggedInUsers(admin, systemConfigActions.get().getSessionTimeoutSeconds())));
 	}

	@Override
	public List<String> getCountryCodes(AuthCredsDTO creds) throws ServerSideException {
		return userOrAdminFunction(creds, "get CountryCodes", "getCountryCodes", true, admin -> userActions.getCountryCodes());
	}

	@Override
	public Map<String, CountryCodeDTO> getCountryCodesMap(AuthCredsDTO creds) throws ServerSideException {
		return userOrAdminFunction(creds, "get CountryCodesMap", "getCountryCodesMap", true, admin -> userActions.getCountryCodesMap());
	}
}
