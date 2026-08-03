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

import com.google.gwt.user.client.rpc.AsyncCallback;
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

/**
 * This service provides the methods to operate on users and groups in both an administrative fashion,
 * adding/deleting/modifying users and groups, and in a regular functional fashion where users can
 * retrieve details of those users or groups.
 *
 * @since   Obidos1.0
 * @author  Mike Morgan
 * @see com.spenego.Obidos.client.rpc.UserService;
 *
 */
public interface UserServiceAsync
{
	/**
	 * Create a new user with the attributes in userDTO.
	 *
	 * @param creds
	 * @param userDTO
	 * @param capabilities
	 * add params for notification. spgdev@spenego.com - Aug 26, 2018
	 */
	void createUser(AuthCredsDTO creds, UserDTO userDTO, CapabilityDTO capabilities, Boolean notifyUser, String emailComment, AsyncCallback<Long> callback);

	/**
	 * Retrieves a user. If the caller is getting their own ID or the call is being made by an admin, the full object is returned. Otherwise, just public
	 * info about that user is returned.
	 *
	 * @param creds
	 * @param userId The ID of the user to retrieve.
	 */
	void getUser(AuthCredsDTO creds, Long userId, AsyncCallback<UserDTO> callback);

	/**
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
	 */
	void getUserCombo(AuthCredsDTO creds, Long userId, AsyncCallback<UserComboResult> callback);

	/**
	 * Change user password
	 *
	 * @param creds
	 * @param oldPassword
	 * @param newPassword
	 *
	 * @return
	 * @throws ServerSideException
	 */
	void changePassword(AuthCredsDTO creds, String oldPassword, String newPassword, AsyncCallback<Void> callback);

	/**
	 * Creates the asymmetric key-pair that will be used to encrypt all secrets created by this user.
	 *
	 * @param creds
	 * @param passPhrase
	 */
	void createKeypair(AuthCredsDTO creds, byte[] passPhrase, AsyncCallback<Void> callback);

	/**
	 * Creates the asymmetric key-pair that will be used to encrypt all secrets created by this user.
	 *
	 * @param creds
	 * @param currentPassword
	 * @param newPassphrase
	 * @param ourToken
	 * @param twoFACode
	 */
	void resetPassphrase(AuthCredsDTO creds, String currentPassword, byte[] newPassphrase, String passwordResetToken, byte[] twoFACode, AsyncCallback<Void> callback);

	/**
	 * Updates the pass phrase used to encrypt the users private key.
	 *
	 * @param creds
	 * @param newPassPhrase
	 * @return
	 * @throws ServerSideException
	 */
	void updatePassphrase(AuthCredsDTO creds, byte[] newPassPhrase, byte[] twoFACode, AsyncCallback<Void> callback);

	/**
	 * Returns true if the keypair has been generated for this user.
	 *
	 * @param creds
	 * @param user
	 * @return
	 * @throws ServerSideException
	 */
	void keypairExists(AuthCredsDTO creds, Long userId, AsyncCallback<Boolean> callback);

	/**
	 * Updates the user record with the supplied information.
	 *
	 * @param creds
	 * @param userInfo
	 */
	void modifyUser(AuthCredsDTO creds, UserDTO userInfo, AsyncCallback<Void> callback);

	/**
	 * Get a list of users that match the specified search criteria. Users can currently search via
	 * username or email address.
	 *
	 * @param creds
	 * @param userInfo Search for users that have either similar email addresses, username or full name.
	 * You can pass null if you do not wish to search and just get all users.
	 * @param preSelectedUsers Include these users in the result set. The users are marked as 'selected' on the user object.
	 * @param first Start the results with this offset.
	 * @param count Retrieve at most this many users.
	 * @param orderby Order the results by this field.
	 */
	void getUsers(AuthCredsDTO creds, UserDTO userInfo, ArrayList<Long> preSelectedUsers, Integer first, Integer count, ArrayList<OrderBy> orderby, AsyncCallback<LimitedUserForAdminResult> callback);

	/**
	 * Marks the user specified by userId as tombstoned without actually deleting them from the database. This is done
	 * so that audit logs are viable for a period of time and that the user may be restored in the future.
	 *
	 * All Items, Notes and Containers associated with this user are deleted.
	 * All shared Items, Notes and Containers are revoked.
	 *
	 * @param creds
	 * @param userIds
	 */
	void deleteUsers(AuthCredsDTO creds, ArrayList<Long> userIds, AsyncCallback<Void> callback);

	/**
	 * Marks the user specified by userId as tombstoned without actually deleting them from the database. This is done
	 * so that audit logs are viable for a period of time and that the user may be restored in the future.
	 *
	 * All Items, Notes and Containers associated with this user are deleted.
	 * All shared Items, Notes and Containers are revoked.
	 *
	 * @param creds
	 * @param userIds
	 * @param deletePermanently If true, instead of just being marked as deleted, the user is actually deleted from the DB. Use this
	 *                          feature judiciously since audit logs will not make sense once the user is deleted.
	 */
	void deleteUsers(AuthCredsDTO creds, ArrayList<Long> userIds, Boolean deletePermanently, AsyncCallback<Void> callback);

	/**
	 * Restores deleted users.
	 *
	 * @param creds
	 * @param userIds a list of users to restore
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
	void restoreUsers(AuthCredsDTO creds, ArrayList<Long> userIds, ArrayList<String> requestedUsernames, AsyncCallback<Void> callback);

	/**
	 * Locks users.
	 *
	 * @param creds
	 * @param userIds
	 * @throws ServerSideException
	 */
	void lockUsers(AuthCredsDTO creds, ArrayList<Long> userIds, AsyncCallback<Void> callback);
	
	/**
	 * Unlocks users.
	 *
	 * @param creds
	 * @param userIds
	 * @throws ServerSideException
	 */
	void unlockUsers(AuthCredsDTO creds, ArrayList<Long> userIds, AsyncCallback<Void> callback);
	
	/**
	 * Unlocks users.
	 *
	 * @param creds
	 * @param userIds
	 * @throws ServerSideException
	 */
	void unTombstoneUsers(AuthCredsDTO creds, ArrayList<Long> userIds, AsyncCallback<Void> callback);
	
	/******************************************************************************************************/
	//                                    Group operations
	/******************************************************************************************************/
	/**
	 * Creates a group, owned by the user. Groups can be used to share secrets en masse.
	 * Group names must be unique for that user and global groups. Different users may
	 * have groups of the same name. This is permitted since user owned groups are always
	 * private.
	 *
	 * @param creds
	 * @param name
	 * @param comment
	 */
	void createGroup(AuthCredsDTO creds, String name, String comment, AsyncCallback<Long> callback);
	void getGroups(AuthCredsDTO creds, String search, ArrayList<Long> preSelectedGroups,  Integer first, Integer count, ArrayList<OrderBy> orderBy, AsyncCallback<GroupResult> callback);
	void getGroup(AuthCredsDTO creds, Long id, AsyncCallback<GroupDTO> callback);
	void updateGroup(AuthCredsDTO creds, GroupDTO group, AsyncCallback<Void> callback);

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
	void getUsersForContainer(AuthCredsDTO creds, Long containerId, UserDTO userSearchTemplate, SharedSetQuality sharedSetQuality,  ArrayList<Long> preSelectedUsers, Integer first, Integer count, ArrayList<OrderBy> orderBy, AsyncCallback<LimitedUserResult> callback);

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
	void getGroupsForContainer(AuthCredsDTO creds, Long containerId, String search, SharedSetQuality sharedSetQuality,  ArrayList<Long> preSelectedUsers,  Integer first, Integer count, ArrayList<OrderBy> orderBy, AsyncCallback<GroupResult> callback);

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
	void getContainerShares(AuthCredsDTO creds, Long containerId, String searchString, ArrayList<Long> preSelectedElements, Integer first, Integer count, ArrayList<OrderBy> orderBy, AsyncCallback<UserGroupComboResult> callback);

	/**
	 * Returns a list of users and groups that the container has been shared with.
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
	void getItemShares(AuthCredsDTO creds, Long itemId, String searchString, ArrayList<Long> preSelectedElements, Integer first, Integer count, ArrayList<OrderBy> orderBy, AsyncCallback<UserGroupComboResult> callback);

	/**
	 * Determine all the users whom have access to the specified item.
	 *
	 * @param creds
	 * @param itemAssignmentId
	 * @param userSearchTemplate You may specify various attributes to search for with this user-template parameter.
	 * @param sharedSetQuality Used to specify if the users returned should be only users that are sharing the item, not sharing the item, or both but with those that are sharing marked.
	 * @param preSelectedUsers Include these users in the result set. The users are marked as 'selected' on the user object.
	 * @param first
	 * @param count
	 * @param orderBy
	 * @return A UserResult that contains a list of users of whom have access to this item.
	 */
	void getUsersForItem(AuthCredsDTO creds, Long itemAssignmentId, UserDTO userPatterns, SharedSetQuality sharedSetQuality, ArrayList<Long> preSelectedUsers, Integer first, Integer count, ArrayList<OrderBy> orderBy, AsyncCallback<LimitedUserResult> callback);

	/**
	 * Determine all the groups whom have access to the specified item.
	 *
	 * @param creds
	 * @param itemAssignmentId
	 * @param searchString      Search for groups containing this string.
	 * @param sharedSetQuality  Used to specify if the users returned should be only users that are sharing the item, not sharing the item, or both but with those that are sharing marked.
	 * @param preSelectedGroups Include these groups in the result set. The users are marked as 'selected' on the user object.
	 * @param first
	 * @param count
	 * @param orderBy
	 * @return A UserResult that contains a list of users of whom have access to this item.
	 */
	void getGroupsForItem(AuthCredsDTO creds, Long itemAssignmentId, String searchString, SharedSetQuality sharedSetQuality, ArrayList<Long> preSelectedUsers, Integer first, Integer count, ArrayList<OrderBy> orderBy, AsyncCallback<GroupResult> callback);

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
	 */
	void addUsersToGroup(AuthCredsDTO creds, ArrayList<Long> usersId, Long groupId, String shareComment, PostOpActions postOpActions, AsyncCallback<Void> callback);

	/**
	 * Returns a list of users in the specified group. Non-admin users will only get a list that
	 * contains their groups and global groups.
	 *
	 * @param creds
	 * @param groupId Get a list of users from this group. If the user is not an admin, they must
	 *                own the group or it must be global.
	 * @param userPatterns TODO
	 * @param sharedSetQuality TODO
	 * @param preSelectedUsers TODO
	 * @param first
	 * @param count
	 */
	void getUsersForGroup(AuthCredsDTO creds, Long groupId, UserDTO userPatterns, SharedSetQuality sharedSetQuality, ArrayList<Long> preSelectedUsers, Integer first, Integer count, ArrayList<OrderBy> order, AsyncCallback<LimitedUserResult> callback);

	/**
	 * Removes a user from the group. The user will lose access to all credentials shared via this group.
	 *
	 * @param creds
	 * @param userId
	 * @param groupId
	 */
	void removeUsersFromGroup(AuthCredsDTO creds, ArrayList<Long> userId, Long groupId, AsyncCallback<Void> callback);

	void deleteGroups(AuthCredsDTO creds, ArrayList<Long> groupId, AsyncCallback<Void> callback);

    void sendUserCreationEmail(AuthCredsDTO credsDTO, Long userId, AsyncCallback<Void> callback);
    void sendUserCreationEmail(AuthCredsDTO credsDTO, Long userId, String message, AsyncCallback<Void> callback);

    void cachePassphrase(AuthCredsDTO credsDTO, byte[] passphrase, byte[]twoFACode, AsyncCallback<Void> callback);
    void isPassphraseCached(AuthCredsDTO credsDTO, AsyncCallback<Boolean> callback);

	void sendPassphraseResetEmail(AuthCredsDTO creds, AsyncCallback<Void> callback);

    /**
	 * Sends a password reset email to the user identified by the email address.
	 *
	 * @param emailAddress
	 */
	void sendPasswordResetEmail(String username, AsyncCallback<Void> callback);

	/**
	 * Sends a password reset email to the user specified in either the username. Reset messages are sent
	 * to all email addresses associated with the account.
	 *
	 * @param username
	 */
	void sendPasswordResetWithAccountLookupEmail(String emailAddress, String subject, AsyncCallback<Void> callback);

	/**
	 * Resets the user's login password.
	 *
	 * @param token the token sent to the user
	 * @param twoFactorCode the generated two-Factor Auth code (only required if user has enabled Two-Factor Auth)
	 * @param newPassword the user's password will be changed to this
	 */
	void resetPassword(String token, String newPassword, byte[] twoFactorAuthCode, AsyncCallback<Void> callback);

	/**
	 * Creates a new set of Password Complexity Rules.
	 *
	 * @return
	 * @throws ServerSideException
	 */
	void createPassComplexity(AuthCredsDTO creds, PassComplexityDTO passComplexity, AsyncCallback<Void> callback);

	/**
	 * Resets the password complexity rules. If null is passed for name, the current System Pass Complexity rules are returned.
	 *
	 * @return
	 * @throws ServerSideException
	 */
	void getPassComplexity(String name, AsyncCallback<PassComplexityDTO> callback);
	
	/**
	 * Returns a list of users logged in.
	 *
	 * @return
	 * @throws ServerSideException
	 */
	void getLoggedInUsers(final AuthCredsDTO creds, AsyncCallback<LimitedUserResult> callback);
	
	
	/**
	 * Get the list of "Country Name (+dial_code)" for SMS
	 * 
	 * @param creds
	 * @param callback
	 * <p>
	 * @author spgdev@spenego.com - Jul 6, 2024
	 */
	void getCountryCodes(final AuthCredsDTO creds, AsyncCallback<List<String>> callback);
	void getCountryCodesMap(final AuthCredsDTO creds, AsyncCallback<Map<String, CountryCodeDTO>> callback);
}
