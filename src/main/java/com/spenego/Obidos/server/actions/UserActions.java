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
import java.util.Map;

import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.SharedSetQuality;
import com.spenego.Obidos.shared.dto.CountryCodeDTO;
import com.spenego.Obidos.shared.dto.GroupDTO;
import com.spenego.Obidos.shared.dto.GroupResult;
import com.spenego.Obidos.shared.dto.LimitedUserResult;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.dto.UserGroupComboResult;

public interface UserActions {
	Long			createGroup(User caller, String name, String comment, PassphraseHash passphraseHash);
	GroupResult		getGroups(User caller, String search, Collection<Long> preSelectedGroups, Integer first, Integer count, List<OrderBy> orderBy);
	GroupDTO		getGroupDTO(Long groupId);
	Void			addUsersToGroup(User caller, Collection<Long> userIds, Long groupId, PassphraseHash passphraseHash, PostOpActions postOpActions, String shareComment);
	Void			updateGroup(User caller, GroupDTO group);
	Void			removeUsersFromGroup(User caller, Collection<Long> userIds, Long groupId, PassphraseHash passphraseHash);
	Void			deleteGroups(User caller, Collection<Long> groupIds, PassphraseHash passphraseHash);
	void			validatePassphraseHash(User caller, PassphraseHash passphraseHash);
	void			setRunningInUnitTest();

	List<String>    getCountryCodes();
	Map<String, CountryCodeDTO> getCountryCodesMap();

	/**
	 * This method is typically used to get users and know if those users have already had the specified container shared with them.
	 * This method will return only users not already in the container if onlyNewUsers is true.
	 * The standard usersPatterns filter can be applied to filter users via name or email.
	 * The users returned will include the preSelectedUsers if they have had the container shared with them.
	 *
	 * @param caller
	 * @param containerAssignmentId if this is not null, if a the container is shared with the user, the attribute inContainer will be set on the user object.
	 * @param userPatterns
	 * @param sharedSetQuality specifies if the result set should contain those users that are either sharing, not yet sharing, or everything but with those that are sharing being marked as such
	 * @param preSelectedUsers
	 * @param offset
	 * @param count
	 * @param orderBy
	 * @return the users
	 */
	LimitedUserResult		getUsersForContainer (User caller, Long containerAssignmentId, UserDTO userPatterns, SharedSetQuality sharedSetQuality, Collection<Long> preSelectedUsers,    Integer offset, Integer count, List<OrderBy> orderBy);
	LimitedUserResult		getUsersForItem      (User caller, Long itemAssignmentId,      UserDTO userPatterns, SharedSetQuality sharedSetQuality, Collection<Long> preSelectedUsers,    Integer offset, Integer count, List<OrderBy> orderBy);
	LimitedUserResult		getUsersForGroup     (User caller, Long groupId,               UserDTO userPatterns, SharedSetQuality sharedSetQuality, Collection<Long> preSelectedUsers,    Integer offset, Integer count, List<OrderBy> orderBy);
	UserGroupComboResult	getSharesForItem     (User caller, Long itemAssignmentId,      String searchString,                                     Collection<Long> preSelectedElements, Integer offset, Integer count, List<OrderBy> orderBy);
	UserGroupComboResult	getSharesForContainer(User caller, Long containerAssignmentId, String searchString,                                     Collection<Long> preSelectedElements, Integer offset, Integer count, List<OrderBy> orderBy);
	GroupResult				getGroupsForContainer(User caller, Long containerAssignmentId, String searchString,  SharedSetQuality sharedSetQuality, Collection<Long> preSelectedGroups,   Integer offset, Integer count, List<OrderBy> orderBy);
	GroupResult				getGroupsForItem     (User caller, Long itemAssignmentId,      String searchString,  SharedSetQuality sharedSetQuality, Collection<Long> preSelectedGroups,   Integer offset, Integer count, List<OrderBy> orderBy);
	void authenticateTwoFactorCode(User user, byte[] twoFactorAuthCode);
}
