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

package com.spenego.Obidos.server.operations;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.spenego.Obidos.server.model.LimitedUser;
import com.spenego.Obidos.server.model.SharedItemUser;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.exceptions.DataIntegrityException;
import com.spenego.Obidos.shared.exceptions.DuplicateRecordException;
import com.spenego.Obidos.shared.exceptions.InvalidPasswordException;
import com.spenego.Obidos.shared.exceptions.RecordModifiedException;
import com.spenego.Obidos.shared.exceptions.UnableToCreateRecordException;
import com.spenego.Obidos.shared.exceptions.UsernameExistsException;
import com.spenego.Obidos.shared.exceptions.ValueTooLongException;

public interface UserOperations extends Operations<User> {
	User getUserByUsername(String username);
	User getUserById(Long id);
	User getUserPublicProfile(Long userId);

	/**
	 * Returns true only if the version in the database is newer than version for the specified user.
	 *
	 * @param userId
	 * @param version
	 * @return
	 */
	boolean newerUserExists(User user);

	// returns all users with this email address specified as contact
	Stream<User> getUsersByEmail(String emailAddress);
	Long create(User user) throws UnableToCreateRecordException, UsernameExistsException, DataIntegrityException, InvalidPasswordException, DuplicateRecordException, ValueTooLongException;
	boolean usernameExists(String username);

	Void deleteUserByUsername(String username);

	// updateSelective allows updating of username
	Void updateSelective(User user) throws RecordModifiedException;
	Collection<Long>       getUserIds(Collection<String> usernames);
	/*
	 * @param userId    User ID of caller. This prevents the caller from being displayed in the result.
	 * @param template  Search for users that have either similar email addresses, username or full name.
	 * You can pass null if you do not wish to search and just get all users.
	 * @param preSelectedUsers Include these users in the result set. The users are marked as 'selected' on the user object.
	 * @param offset    Skip this many users in the result set.
	 * @param count     Retrieve at most this many users.
	 * @param orderby   Order the results by this field.
	 */
	Stream<User>           getUsers(Long userId, User template, Collection<Long> preSelectedUsers, Integer offset, Integer count, List<OrderBy> orderby);
	Stream<LimitedUser>    getUsersNotSharingItem(Long userId, Long itemId, User template, Collection<Long> preSelectedUsers, Integer offset, Integer count, List<OrderBy> orderby);
	// Stream<LimitedUser>    getAdmins(Long userId, String search, Boolean getDeleted, Collection<Long> preSelectedUsers, Integer offset, Integer count, List<OrderBy> orderby);
	// Stream<LimitedUser>	   getRecipientsOfSharedItem(Long userId, Long itemId, User template, Collection<Long> preSelectedUsers, Integer first, Integer count, List<OrderBy> orderby);
	Stream<LimitedUser>	   getUsersSharingContainer(Long ownerId, Long containerID, User userTemplate, Collection<Long> preSelectedGroups, Integer first, Integer count, List<OrderBy> orderBy);
	Stream<SharedItemUser> getUsersSharingItem(Long userId, Long itemId);
	Stream<Long>           getUserIdsSharingItem(Long userId, Long itemId);
	Stream<LimitedUser>	   getUsersSharingItem(Long userId, Long itemId, User template, Collection<Long> preSelectedUsers, Integer first, Integer count, List<OrderBy> orderby);
	Integer getCount(Long userId, User template, Collection<Long> preSelectedUsers);
	Integer getCount(Long userId, String search, Boolean getDeleted, Collection<Long> preSelectedUsers);
	Integer getUsersNotSharingItemCount(Long userId, Long itemId, User template, Collection<Long> preSelectedUsers);
	Integer getAdminCount(Long userId, String search, Boolean getDeleted, Collection<Long> preSelectedUsers);
	Integer	getRecipientsOfSharedItemCount(Long userId, Long itemId, User template, Collection<Long> preSelectedUsers);
	Integer	getUsersSharingContainerCount(Long ownerId, Long containerID, User userTemplate, Collection<Long> preSelectedGroups);
	Integer countTombstonedUsers(Collection<Long> userIds);

	User getOwnerOfDocument(Long documentId);

	/**
	 * List all users sharing a container.
	 *
	 * @param containerId
	 * @return a list of all the users who are sharing this container either directly or via a group.
	 */
	Collection<LimitedUser> getUsersSharingContainer(Long userId, Long containerId);
	Stream<Long> getUserIdsSharingContainer(Long userId, Long containerId);

	/**
	 *
	 * @param containerId
	 * @param groupIds
	 * @return a stream of users, in the specified groups, who are sharing the container.
	 */
	Stream<LimitedUser> getUsersSharingContainerViaGroups(Long containerId, Collection<Long> groupIds);
	Stream<LimitedUser> getUsersNotInGroup(User user, Long groupId, User userPattern, Collection<Long> preSelectedUsers, Integer offset, Integer count, List<OrderBy> orderby);
	Integer getUsersNotInGroupCount(User user, Long groupId, User userPattern, Collection<Long> preSelectedUsers);

	Stream<LimitedUser> getUsersNotSharingContainer(Long userId, Long containerId, User userPattern, Collection<Long> preSelectedUsers, Integer offset, Integer count, List<OrderBy> orderby);
	Integer getUsersNotSharingContainerCount(Long userId, Long containerId, User userPattern, Collection<Long> preSelectedUsers);

	Integer countUsersWithInsufficientClearance(Collection<Long> userIds, Integer minimumSecurityClearance);
}
