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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
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
import com.spenego.Obidos.shared.exceptions.ServerSideException;
import com.spenego.Obidos.shared.exceptions.UsernameExistsException;

/**
 * This service provides the methods to operate on users and groups in both an administrative fashion,
 * adding/deleting/modifying users and groups, and in a regular functional fashion where users can
 * retrieve details of those users or groups.
 *
 * @author  Mike Morgan
 * @see com.spenego.Obidos.client.rpc.UserServiceAsync;
 * @since   Obidos1.0
 *
 */
@RemoteServiceRelativePath("rpc/userService")
public interface UserService extends RemoteService {
	public static class Utility {
		private Utility() { /* no instances */ }
		private static final UserServiceAsync instance = (UserServiceAsync) GWT.create(UserService.class);
		public static final UserServiceAsync getInstance() { return instance; }
	}

	/**
	 * Create a new user with the attributes in userDTO.
	 *
	 * @param creds
	 * @param userDTO
	 * @param capabilities
	 * @return the new user id
	 * @throws UsernameExistsException
	 * @throws ServerSideException
	 * email was send from UI. Add params so that mail can be sent after
	 * user is created successfully. spgdev@spenego.com - Aug 26, 2018
	 */
	Long createUser(AuthCredsDTO creds, UserDTO userDTO, CapabilityDTO capabilities, Boolean notifyUser, String emailComment) throws UsernameExistsException, ServerSideException;

	/**
	 * Retrieves a user. If the caller is getting their own ID or the call is being made by an admin, the full object is returned. Otherwise, just public
	 * info about that user is returned.
	 *
	 * @param creds
	 * @param userId The ID of the user to retrieve.
	 * @return A user with full details.
	 * @throws ServerSideException
	 */
	UserDTO getUser(AuthCredsDTO creds, Long userId) throws ServerSideException;

	/**
	 * A courtesy method to remove the need for multiple calls by the client.
	 * Retrieves a user combo. This combo is a combination of all results a user
	 * may need when editing their information. Currently it only contains a user
	 * and all LDAP configurations. This is expected to increase to include any
	 * lists the user may need to select from.
	 *
	 * The caller must either be either be
	 * getting their own ID or the call is being made by an admin.
	 *
	 * @param creds
	 * @param userId The ID of the user to retrieve.
	 * @return A UserComboResult object.
	 * @throws ServerSideException
	 */
	UserComboResult getUserCombo(AuthCredsDTO creds, Long userId) throws ServerSideException;

	/**
	 * Get a list of users that match the specified search criteria. Users can currently search via
	 * username, full name or email address.
	 *
	 * @param creds
	 * @param userInfo Search for users that have either similar email addresses, username or full name.
	 * You can pass null if you do not wish to search and just get all users.
	 * @param preSelectedUsers Include these users in the result set. The users are marked as 'selected' on the user object.
	 * @param count Retrieve at most this many users.
	 * @param orderby Order the results by this field.
	 * @return
	 * @throws ServerSideException
	 */
	LimitedUserForAdminResult getUsers(AuthCredsDTO creds, UserDTO userInfo, ArrayList<Long> preSelectedUsers, Integer first, Integer count, ArrayList<OrderBy> orderby) throws ServerSideException;

	/**
	 * Updates the user record with the supplied information.
	 *
	 * Admins may not change their auth source from AUTH_LOCAL. This is silently enforced. Attempting to change this
	 * will have no effect or warning.
	 *
	 * @param creds
	 * @param userInfo
	 * @return
	 * @throws ServerSideException
	 */
	Void modifyUser(AuthCredsDTO creds, UserDTO userInfo) throws ServerSideException;

	/**
	 * Creates the current password of the user
	 *
	 * @param creds
	 * @param oldPassword
	 * @param newPassword
	 *
	 * @return
	 * @throws ServerSideException
	 */
	Void changePassword(AuthCredsDTO creds, String oldPassword, String newPassword) throws ServerSideException;

	/**
	 * Creates the asymmetric key-pair that will be used to encrypt all secrets created by this user.
	 * If the keypair already exists, an exception is thrown.
	 *
	 * @param creds
	 * @param passPhrase
	 * @return
	 * @throws ServerSideException
	 */
	Void createKeypair(AuthCredsDTO creds, byte[] passPhrase) throws ServerSideException;

	/**
	 *
	 * Creates a new asymmetric key-pair. All items created with the previous key-pair are destroyed.
	 *
	 * @param creds
	 * @param currentPassword
	 * @param newPassphrase
	 * @param ourToken
	 * @param twoFACode
	 * @return
	 */
	Void resetPassphrase(AuthCredsDTO creds, String currentPassword, byte[] newPassphrase, String passwordResetToken, byte[] twoFACode) throws ServerSideException;

	/**
	 * Returns true if the keypair has been generated for this user. The client must call createKeypair in order
	 * for this to return true.
	 *
	 * @param creds
	 * @param user
	 * @return true if there is a keyPair exists for the user
	 * @throws ServerSideException
	 */
	Boolean keypairExists(AuthCredsDTO creds, Long userId) throws ServerSideException;

	/**
	 * Deletes the user specified by userId. All items associated with this user are deleted too.
	 *
	 * @param creds
	 * @param userId
	 * @throws ServerSideException
	 */
	Void deleteUsers(AuthCredsDTO creds, ArrayList<Long> userIds) throws ServerSideException;

	/**
	 * Marks the user specified by userId as tombstoned without actually deleting them from the database. This is done
	 * so that audit logs are viable for a period of time and that the user may be restored in the future.
	 *
	 * All Items, Notes and Containers associated with this user are deleted.
	 * All shared Items, Notes and Containers are revoked.
	 *
	 * @param creds
	 * @param userId
	 * @param deletePermanently If true, instead of just being marked as deleted, the user is actually deleted from the DB. Use this
	 * feature judiciously since audit logs will not make sense once the user is deleted.
	 * @throws ServerSideException
	 */
	Void deleteUsers(AuthCredsDTO creds, ArrayList<Long> userIds, Boolean deletePermanently) throws ServerSideException;

	/**
	 * Restores a tomb-stoned user.
	 *
	 * @param creds
	 * @param userIds
	 * @param requestedUsernames When a user is tombstoned, their username is renamed. The new name
	 *                           contains a string the user would not be able to specify themselves.
	 *                           This allows the username to be reused while not deleting the
	 *                           original user.
	 *                           The values for requestedUsernames are the usernames the caller
	 *                           wishes to use for the restored users. If a value is not specified or
	 *                           if requestedUsernames is null, the original username is attempted.
	 *                           There is no guarantee that the original username will be available
	 *                           for the restored user. If the original username is unavailable,
	 *                           the new username will be the original username appended with a digit.
	 *
	 * @throws ServerSideException
	 */
	Void restoreUsers(AuthCredsDTO creds, ArrayList<Long> userIds, ArrayList<String> requestedUsernames) throws ServerSideException;

	/**
	 * Locks a user. This prevents the user from logging in.
	 *
	 * @param creds
	 * @param userId
	 * @throws ServerSideException
	 */
	Void lockUsers(AuthCredsDTO creds, ArrayList<Long> userIds) throws ServerSideException;

	/**
	 * unlocks a locked user.
	 *
	 * @param creds
	 * @param userId
	 * @throws ServerSideException
	 */
	Void unlockUsers(AuthCredsDTO creds, ArrayList<Long> userIds) throws ServerSideException;

	/**
	 * Restores a deleted user.
	 *
	 * @param creds
	 * @param userId
	 * @throws ServerSideException
	 */
	Void unTombstoneUsers(AuthCredsDTO creds, ArrayList<Long> userIds) throws ServerSideException;

	/**
	 * Creates a group, owned by the user. Groups can be used to share secrets en masse.
	 * Group names must be unique for that user and global groups. Different users may
	 * have groups of the same name. This is permitted since user owned groups are always
	 * private.
	 *
	 * @param creds
	 * @param name
	 * @param comment
	 * @return
	 * @throws ServerSideException
	 */
	Long createGroup(AuthCredsDTO creds, String name, String comment) throws ServerSideException;

	GroupDTO getGroup(AuthCredsDTO creds, Long id) throws ServerSideException;

	Void updateGroup(AuthCredsDTO creds, GroupDTO group) throws ServerSideException;

	/**
	 * Returns a list of groups that this user has access to. Groups owned by the admin are
	 * global. Users can share their credentials with everyone in them
	 * Groups owned by non-admin users are private and only visible to that user.
	 *
	 * @param creds
	 * @param search
	 * @param first
	 * @param count
	 * @return
	 * @throws ServerSideException
	 */
	GroupResult getGroups(AuthCredsDTO creds, String search, ArrayList<Long> preSelectedGroups, Integer first, Integer count, ArrayList<OrderBy> orderBy) throws ServerSideException;

	/**
	 * Determine all the groups that have access to the specified item.
	 *
	 * @param creds
	 * @param itemAssignmentId
	 * @param searchString      Search for groups containing this string.
	 * @param sharedSetQuality  Used to specify if the users returned should be only users that are sharing the item, not sharing the item, or both but with those that are sharing marked.
	 * @param preSelectedGroups Include these groups in the result set. The users are marked as 'selected' on the user object.
	 * @param first
	 * @param count
	 * @param orderBy
	 * @return A GroupResult that contains a list of groups that the item may be shared with or already sharing the item.
	 */
	GroupResult getGroupsForItem(AuthCredsDTO creds, Long itemAssignmentId, String searchString, SharedSetQuality sharedSetQuality, ArrayList<Long> preSelectedGroups, Integer first, Integer count, ArrayList<OrderBy> orderBy) throws ServerSideException;

	/**
	 * Returns a list of users that are not in the specified container already (if onlyNewUsers is true). If onlyNewUsers is false, the users that are already sharing the container are simply
	 * marked as 'InContainer'.
	 *
	 * @param creds
	 * @param containerId get users for this container. If onlyNewUsers is true, users already sharing the container are not returned in the result set.
	 * @param userSearchTemplate You may specify various attributes to search for with this user-template parameter.
	 * @param sharedSetQuality Used to specify if the users returned should be only users that are sharing the container, not sharing the container, or both but with those that are sharing marked.
	 * @param preSelectedUsers The result set will include these users and mark them as pre-selected.
	 * @param search
	 * @param first
	 * @param count
	 * @return
	 * @throws ServerSideException
	 */
	LimitedUserResult getUsersForContainer(AuthCredsDTO creds, Long containerId, UserDTO userSearchTemplate, SharedSetQuality sharedSetQuality,  ArrayList<Long> preSelectedUsers, Integer first, Integer count, ArrayList<OrderBy> orderBy) throws ServerSideException;

	/**
	 * Returns a list of groups that this user has access to that are not in the specified container.
	 * Groups owned by the admin are global. Users can share their credentials with everyone in them
	 * Groups owned by non-admin users are private and only visible to that user.
	 *
	 * @param creds
	 * @param containerId get groups for this container. If onlyNewGroups is true, groups already in the container are not
	 * returned in the result set.
	 * @param sharedSetQuality Used to specify if the groups returned should be only groups that are sharing the container, not sharing the container, or both but with those that are sharing marked.
	 * @param search
	 * @param first
	 * @param count
	 * @return
	 * @throws ServerSideException
	 */
	GroupResult getGroupsForContainer(AuthCredsDTO creds, Long containerId, String search, SharedSetQuality sharedSetQuality, ArrayList<Long> preSelectedGroups, Integer first, Integer count, ArrayList<OrderBy> orderBy) throws ServerSideException;

	/**
	 * Returns a list of users and groups that the container has been shared with.
	 *
	 * @param creds
	 * @param containerId The container of which you want to see the shares of.
	 * @param searchString Only users with this string in either their full name or email will be returned and only groups that have this string in their name will be returned.
	 * @param preSelectedElements You may specify a list of users or groups that you have already selected to be contained in the result set.
	 * @param first
	 * @param count
	 * @param orderBy
	 * @return
	 * @throws ServerSideException
	 */
	UserGroupComboResult getContainerShares(AuthCredsDTO creds, Long containerId, String searchString, ArrayList<Long> preSelectedElements, Integer first, Integer count, ArrayList<OrderBy> orderBy) throws ServerSideException;

	/**
	 * Returns a list of users and groups that the item has been shared with.
	 *
	 * @param creds
	 * @param itemId The item of which you want to see the shares of.
	 * @param searchString Only users with this string in either their full name or email will be returned and only groups that have this string in their name will be returned.
	 * @param preSelectedElements You may specify a list of users or groups that you have already selected to be contained in the result set.
	 * @param first
	 * @param count
	 * @param orderBy
	 * @return
	 * @throws ServerSideException
	 */
	UserGroupComboResult getItemShares(AuthCredsDTO creds, Long itemId, String searchString, ArrayList<Long> preSelectedElements, Integer first, Integer count, ArrayList<OrderBy> orderBy) throws ServerSideException;

	/**
	 * Determine all the users whom have access to the specified item.
	 *
	 * @param creds
	 * @param noteId
	 * @param searchString Only users that have a name that contain the searchString are contained in the result.
	 * @param sharedSetQuality Used to specify if the users returned should be only users that are sharing the item, not sharing the item, or both but with those that are sharing marked.
	 * @param preSelectedUsers Include these users in the result set. The users are marked as 'selected' on the user object.
	 * @param first
	 * @param count
	 * @param orderBy
	 * @return A UserResult that contains a list of users of whom have access to this item.
	 */
	LimitedUserResult getUsersForItem(AuthCredsDTO creds, Long itemAssignmentId, UserDTO userPatterns, SharedSetQuality sharedSetQuality, ArrayList<Long> preSelectedUsers, Integer first, Integer count, ArrayList<OrderBy> orderBy) throws ServerSideException;

	/**
	 * Adds a user to an existing group. All credentials shared with that group will be shared with
	 * that user. This creates a problem since the owner of the credentials must supply their
	 * passphrase for the sharing to occur.  We will create a sharing notification record in these
	 * cases. The user will be notified that they need to supply their passphrase in order for the
	 * credentials to be shared with the new users in the group.
	 *
	 * @param creds
	 * @param groupId
	 * @param shareComment TODO
	 * @param username
	 * @return
	 * @throws ServerSideException
	 */
	Void addUsersToGroup(AuthCredsDTO creds, ArrayList<Long> userIds, Long groupId, String shareComment, PostOpActions postOpActions) throws ServerSideException;

	/**
	 * Removes a user from the group. The user will lose access to all credentials shared via this group.
	 *
	 * @param creds
	 * @param userId
	 * @param groupId
	 * @return
	 */
	Void removeUsersFromGroup(AuthCredsDTO creds, ArrayList<Long> userId, Long groupId) throws ServerSideException;

	/**
	 * Returns a list of users in the specified group. Non-admin users will only get a list that
	 * contains their groups and global groups.
	 *
	 * @param creds
	 * @param groupId Get a list of users from this group. If the user is not an admin, they must
	 *                own the group or it must be global.
	 * @param first
	 * @param count
	 * @param orderBy
	 * @return
	 * @throws ServerSideException
	 */
	LimitedUserResult getUsersForGroup(AuthCredsDTO creds, Long groupId, UserDTO userPatterns, SharedSetQuality sharedSetQuality, ArrayList<Long> preSelectedUsers, Integer first, Integer count, ArrayList<OrderBy> order) throws ServerSideException;

	/**
	 * Deletes the specified group. All credentials shared via this group will no longer be shared.
	 *
	 * @param creds
	 * @param groupId
	 * @return
	 */
	Void deleteGroups(AuthCredsDTO creds, ArrayList<Long> groupId) throws ServerSideException;

	/**
	 * Send email to the user telling that the account is created
	 *
	 * @param credsDTO
	 * @param userId
	 * @return
	 * @throws ServerSideException
	 * <p>
	 * @author spgdev@spenego.com - Jun 11, 2017
	 */
    Void sendUserCreationEmail(AuthCredsDTO credsDTO, Long userId) throws ServerSideException;
    Void sendUserCreationEmail(AuthCredsDTO credsDTO, Long userId, String comment) throws ServerSideException;

    /**
     * Certain actions, like sharing credentials (explicitly or implicitly via containers) requires
     * the user's passphrase to decrypt the user's private key. Rather than requiring the user to
     * supply this for each sharing operation, we cache it.
     *
     * If the passphrase is incorrect, the cached value is cleared and a ServerSideException is
     * thrown.
     *
     * @param credsDTO
     * @param passphrase
     * @return
     * @throws ServerSideException
     */
    Void cachePassphrase(AuthCredsDTO credsDTO, byte[] passphrase, byte[] twoFACode) throws ServerSideException;

    /**
     * Returns true if the user's passphrase is currently cached, false otherwise.
     *
     * @param credsDTO
     * @return
     * @throws ServerSideException
     */
    Boolean isPassphraseCached(AuthCredsDTO credsDTO) throws ServerSideException;

    /**
	 * The user's private key is re-encrypted with the new pass phrase.
	 *
	 * @param creds
	 * @param newPassPhrase
	 * @return
	 * @throws ServerSideException
	 */
	Void updatePassphrase(AuthCredsDTO creds, byte[] newPassPhrase, byte[] twoFACode) throws ServerSideException;

	/**
	 * Sends a password reset email to the user identified by the email address.
	 *
	 * @param emailAddress
	 * @param subject  We need to pass subject because it can be in some other language. At this time
	 * server side can not obtain i18n properties from properties file
	 * @return
	 * @throws ServerSideException
	 */
	Void sendPasswordResetWithAccountLookupEmail(String emailAddress, String subject) throws ServerSideException;

	/**
	 * Sends a password reset email to the user specified in either the username. Reset messages are sent
	 * to all email addresses associated with the account.
	 *
	 * @param username
	 * @return
	 * @throws ServerSideException
	 */
	Void sendPasswordResetEmail(String username) throws ServerSideException;

	/**
	 * Sends a password reset email to the user specified in either the username. Reset messages are sent
	 * to all email addresses associated with the account.
	 *
	 * @param creds
	 * @return
	 * @throws ServerSideException
	 */
	Void sendPassphraseResetEmail(AuthCredsDTO creds) throws ServerSideException;

	/**
	 * Resets the user's login password.
	 *
	 * @param token the token sent to the user
	 * @param twoFactorCode the generated two-Factor Auth code (only required if user has enabled Two-Factor Auth)
	 * @param newPassword the user's password will be changed to this
	 * @return
	 * @throws ServerSideException
	 */
	Void resetPassword(String token, String newPassword, byte[] twoFactorCode) throws ServerSideException;

	/**
	 * Creates a new set of Password Complexity Rules.
	 *
	 * @return
	 * @throws ServerSideException
	 */
	Void createPassComplexity(AuthCredsDTO creds, PassComplexityDTO passComplexity) throws ServerSideException;

	/**
	 * Resets the system password complexity rules.
	 *
	 * @return
	 * @throws ServerSideException
	 */
	PassComplexityDTO getPassComplexity(String name) throws ServerSideException;

	/**
	 * Returns list of users currently logged in.
	 *
	 * @return
	 * @throws ServerSideException
	 */
	LimitedUserResult getLoggedInUsers(final AuthCredsDTO creds) throws ServerSideException;
	
	/**
	 * Get the list of "Country Name (+dial_code)" for SMS
	 * 
	 * @param creds
	 * @return
	 * @throws ServerSideException
	 * <p>
	 * @author spgdev@spenego.com - Jul 6, 2024
	 */
	List<String> getCountryCodes(final AuthCredsDTO creds) throws ServerSideException;
	Map<String, CountryCodeDTO> getCountryCodesMap(final AuthCredsDTO creds) throws ServerSideException;
}
