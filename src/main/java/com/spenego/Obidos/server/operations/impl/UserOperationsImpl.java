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

package com.spenego.Obidos.server.operations.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.UserMapper;
import com.spenego.Obidos.server.model.LimitedUser;
import com.spenego.Obidos.server.model.SharedItemUser;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.model.UserExample;
import com.spenego.Obidos.server.operations.UserOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.exceptions.DataIntegrityException;
import com.spenego.Obidos.shared.exceptions.DuplicateRecordException;
import com.spenego.Obidos.shared.exceptions.InvalidPasswordException;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;
import com.spenego.Obidos.shared.exceptions.RecordModifiedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;
import com.spenego.Obidos.shared.exceptions.UnableToCreateRecordException;
import com.spenego.Obidos.shared.exceptions.UsernameExistsException;
import com.spenego.Obidos.shared.exceptions.ValueTooLongException;

public final class UserOperationsImpl extends ObidosOperations<User> implements UserOperations {
	private static final Logger	logger = LoggerFactory.getLogger(UserOperationsImpl.class);

	@Autowired
	private final UserMapper	userMapper = null;

	@Override
	protected Logger getLogger()		{ return logger; }
	@Override
	protected UserMapper getMapper()	{ return userMapper; }
	@Override
	protected String getModelName()		{ return "user"; }

	/**
	 * @param userMapper
	 * @param user
	 * @return true if the loginName or Email exists, false otherwise
	 */
	@Override
	public boolean usernameExists(final String username) {
		// we know loginName and Email will be in the model
		final Collection<User> users = userMapper.selectByExample(new UserExample(c -> c.andUsernameEqualTo(username)));
		if (users != null && users.size() > 0) {
			logger.info(() -> "Size of Users list: " + users.size());
			return true;
		}
		logger.info(() -> "User " + username + " does not exist");
		return false;
	}

	public boolean emailExists(final String email, final Boolean admin) {
		// we know loginName and Email will be in the model
		final Collection<User> users = userMapper.selectByExample(new UserExample(c -> c.andEmail1EqualTo(email).andAdministratorEqualTo(admin)));
		if (users != null && users.size() > 0) {
			logger.info(() -> "Size of Users list: " + users.size());
			return true;
		}
		logger.info(() -> "Email " + email + " does not exist with admin " + admin);
		return false;
	}

	private static UserExample createExample(final String username, final String emailAddress) {
		final UserExample example = new UserExample(c -> c.andUsernameEqualTo(username));

		if (emailAddress != null) {
			example.or().andEmail1EqualTo(emailAddress);
			example.or().andEmail2EqualTo(emailAddress);
			example.or().andEmail3EqualTo(emailAddress);
		}

		return example;
	}

	/**
	 *
	 * @param id
	 * @return
	 * @throws ServersideException
	 */
	@Override
	public User getUserById(final Long id) {
		logger.info(() -> "looking up user with id " + Long.toString(id));

		try {
			final User user = userMapper.selectByPrimaryKey(id);
			// must match exactly one user
			if (user == null) {
				logger.warn(() -> "user with id " + Long.toString(id) + " does not exist");
				throw new NoSuchRecordException("user with id " + Long.toString(id) + " not found");
			}
			return user;
		} catch (final NoSuchRecordException e) {
			throw e;
		} catch (final Exception e) {
			throw new ServerSideException(e.getMessage());
		}
	}

	/**
	 *
	 * @param username
	 * @return
	 * @throws ServersideException
	 */
	@Override
	public User getUserByUsername(final String username) {
		logger.info(() -> "looking up user " + username);

		try {
			final List<User> users = userMapper.selectByExample(createExample(username, null));
			// must match exactly one user
			if (users == null || users.size() != 1) {
				logger.warn(() -> "user " + username + " does not exist");
				throw new NoSuchRecordException("user " + username + " not found");
			}
			return users.get(0);
		} catch (final NoSuchRecordException e) {
			throw e;
		} catch (final Exception e) {
			throw new ServerSideException(e.getMessage());
		}
	}

	@Override
	public final Stream<User> getUsersByEmail(final String emailAddress) {
		final Collection<User> users = userMapper.selectByExample(createExample(null, emailAddress));
		if (users == null || users.isEmpty()) {
			throw new NoSuchRecordException("User with email " + emailAddress + " does not exist.");
		}
		return users.stream();
	}

	private static final void checkUser(final User user) throws DataIntegrityException, InvalidPasswordException {
		if (user == null) {
			throw new DataIntegrityException("User object is null");
		}
	}

	/*******************************************************************
	 *   C r e a t e
	 * @throws DuplicateRecordException
	 * @throws ValueTooLongException
	 *******************************************************************/
	@Override
	public final Long create(final User user) throws UnableToCreateRecordException, UsernameExistsException, DataIntegrityException, InvalidPasswordException, DuplicateRecordException, ValueTooLongException {
		checkUser(user);

		if (usernameExists(user.getUsername())) {
			throw new UsernameExistsException("username " + user.getUsername() + " already exists");
		}

		if (user.getAdministrator() == null) {
			user.setAdministrator(Boolean.FALSE);
		}

		if (user.getSecurityClearance() == null) {
			user.setSecurityClearance(0);
		}

		if (user.getTwoFARequired() == null) {
			user.setTwoFARequired(Boolean.FALSE);
		}

		if (user.getTwoFAPasswordResetEnabled() == null) {
			user.setTwoFAPasswordResetEnabled(Boolean.FALSE);
		}

		user.setDeleted(Boolean.FALSE);
		user.setLocked(Boolean.FALSE);

		if (user.getPasswordChangeRequired() == null) {
			user.setPasswordChangeRequired(Boolean.FALSE);
		}

		if (user.getAuthSource() == null) {
			user.setAuthSource("local");
		}

		logger.info(() -> "Creating user with username = " + user.getUsername());

		return (user.getId() == null) ? createWithRandomID(user) : super.create(user);
	}

	@Override
	public final Void updateSelective(final User user) {
		logger.info(() -> "updating user " + user.getId() + ", username = " + user.getUsername() + ", email = " + user.getEmail1() + ", accept email notification = " + user.getAcceptEmailNotification());

		user.setUpdatedAt(new Date());
		final Date createdAt = user.getCreatedAt();
		user.setCreatedAt(null);

		try {
			int rows = userMapper.updateByPrimaryKeySelective(user);

			if (rows == 0) {
				throw new RecordModifiedException("User", user.getId());
			}

			return null;
		} catch (final Exception e) {
			throw new ServerSideException("Could not update user.");
		} finally {
			user.setCreatedAt(createdAt);
		}
	}

	@Override
	public Void deleteUserByUsername(final String username) {
		final User user = getUserByUsername(username);
		if (user != null) {
			logger.info(() -> "Deleting user: " + username);
			userMapper.deleteByPrimaryKey(user.getId());
		}

		return null;
	}

	private UserExample createBaseExample(final String user_id_col, final Collection<Long> preSelectedUsers, final List<OrderBy> orderBy, final Consumer<UserExample.Criteria> consumer, final Consumer<UserExample.Criteria> preSelectedUserconsumer) {
		final UserExample example = new UserExample(generatePreSelectedOrderClause(preSelectedUsers, user_id_col, orderBy), consumer);
		if (preSelectedUsers != null && !preSelectedUsers.isEmpty()) {
			final UserExample.Criteria criteria = example.createCriteria();

			criteria.andIdIn(preSelectedUsers);
			if (preSelectedUserconsumer != null) {
				preSelectedUserconsumer.accept(criteria);
			}
			example.or(criteria);
		}
		return example;
	}

	private UserExample getExample(final Long notUserId, final User template, final Collection<Long> preSelectedUsers, final List<OrderBy> orderBy,
			final Function<UserExample.Criteria, UserExample.Criteria> f, final Consumer<UserExample.Criteria> consumer) {
		return createBaseExample("u.id", preSelectedUsers, orderBy, c -> gen(null, f.apply(c.andIdNotEqualTo(notUserId)), template), consumer);
	}

	// Bug #356: We need to add more criteria to the query since the 'or' section was selecting additional records which we did not want since the 'ored' criteria
	//           only specified user-ids. We need to ask for those user ids and only where the user ids are associated with the item.
	// Bug #750: We need to only look for recipients where the item was shared explicitly.
	private UserExample getRecipientExample(final Long notUserId, final Long itemId, final User template, final Collection<Long> preSelectedUsers, final List<OrderBy> orderBy) {
		return getExample(notUserId, template, preSelectedUsers, orderBy, c -> c.andItemIdEqualTo(itemId).andItemSharedExplicitly(Boolean.TRUE), c -> c.andItemIdEqualTo(itemId));
	}

	private UserExample getContainerRecipientExample(final Long notUserId, final Long containerId, final User user, final Collection<Long> preSelectedUsers, final List<OrderBy> orderBy) {
		return getExample(notUserId, user, preSelectedUsers, orderBy, c -> c.andContainerIdEqualTo(containerId).andContainerSharedExplicitly(Boolean.TRUE), null);
	}

	private UserExample createSimpleExample(final User user, final String search, final Boolean getDeleted, final Boolean getAdmins, final Collection<Long> preSelectedUsers, final List<OrderBy> orderBy) {
		return createBaseExample("id", preSelectedUsers, orderBy, c -> c.andAdministratorEqualTo(getAdmins).andDeletedEqualTo((user != null && user.isAdmin() && getDeleted != null) ? getDeleted : Boolean.FALSE).andFullnameOrUsernameLike(search), null);
	}

	private static void callConsumer(final Consumer<UserExample.Criteria> criteriaConsumer, final UserExample.Criteria c) {
		if (criteriaConsumer != null) { criteriaConsumer.accept(c); }
	}

	private UserExample createExample(final User user, final User template, final Collection<Long> preSelectedUsers, final List<OrderBy> orderBy, final Consumer<UserExample.Criteria> criteriaConsumer) {
		return createBaseExample("u.id", preSelectedUsers, orderBy,
					c -> { gen(user, c, template).andUserNotEqualTo(user); callConsumer(criteriaConsumer, c); }, null);
	}

/* -------- andRegionIdEqualTo() criteria is added as a hack to put users into different Organizations.
            This has to be properly addressed. This hack is a temporary work-around -------------- */ 

	private UserExample createExample(final User user, final User template, final Collection<Long> preSelectedUsers, final Consumer<UserExample.Criteria> criteriaConsumer, final List<OrderBy> orderBy) {
		return createExample(user, template, preSelectedUsers, orderBy, c -> { c.andPublickeyIsNotNull().andRegionIdEqualTo(user.getRegionId()); criteriaConsumer.accept(c); });
	}

	@Override
	public User getUserPublicProfile(final Long userId) {
		return userMapper.getUserPublicProfileByPrimaryKey(userId);
	}

	@Override
	public boolean newerUserExists(final User user) {
		return countByExample(new UserExample(null, c -> c.andIdEqualTo(user.getId()).andVersionGreaterThan(user.getVersion()))) > 0;
	}

	@Override
	public Integer countTombstonedUsers(final Collection<Long> userIds) {
		return countByExample(new UserExample(null, c -> c.andIdIn(userIds).andDeletedEqualTo(true)));
	}

	@Override
	public Stream<User> getUsers(final Long userId, final User template, final Collection<Long> preSelectedUsers, final Integer offset, final Integer count, final List<OrderBy> orderBy) {
		return userMapper.selectUsersForAdminByExample(createExample(get(userId), template, preSelectedUsers, orderBy, null), createRowBounds(offset, count)).stream();
	}

	@Override
	public Collection<Long> getUserIds(Collection<String> usernames) {
		return userMapper.selectIdsByExample(new UserExample(c -> c.andUsernameIn(usernames)));
	}

	// get list of admins
//	@Override
//	public Stream<LimitedUser> getAdmins(final Long userId, final String search, final Boolean getDeleted, final Collection<Long> preSelectedUsers, final Integer offset, final Integer count, final List<OrderBy> orderBy) {
//		return userMapper.selectByExample(createSimpleExample(get(userId), search, getDeleted, Boolean.TRUE, preSelectedUsers, orderBy), createRowBounds(offset, count)).stream();
//	}

	// get count of users
	@Override
	public Integer getCount(final Long userId, final User template, final Collection<Long> preSelectedUsers) {
		return userMapper.countByGroupExcludedExample(createExample(get(userId), template, preSelectedUsers, (List<OrderBy>) null, null));
	}

	// get count of users
	@Override
	public Integer getCount(final Long userId, final String search, final Boolean getDeleted, final Collection<Long> preSelectedUsers) {
		return countByExample(createSimpleExample(get(userId), search, getDeleted, Boolean.FALSE, preSelectedUsers, null));
	}

	// get count of admins
	@Override
	public Integer getAdminCount(final Long userId, final String search, final Boolean getDeleted, final Collection<Long> preSelectedUsers) {
		return countByExample(createSimpleExample(get(userId), search, getDeleted, Boolean.TRUE, preSelectedUsers, null));
	}

	private static UserExample getExampleOfUsersSharingItem(final Long userId, final Long itemId) {
		return new UserExample(c -> c.andIdNotEqualTo(userId).andItemIdEqualTo(itemId).andItemSharedExplicitly(Boolean.TRUE));
	}

	@Override
	public Stream<SharedItemUser> getUsersSharingItem(final Long userId, final Long itemId) {
		return userMapper.selectUsersSharingItemByExample(getExampleOfUsersSharingItem(userId, itemId)).stream();
	}

	@Override
	public Stream<Long> getUserIdsSharingItem(final Long userId, final Long itemId) {
		return userMapper.selectUserIdsSharingItemByExample(getExampleOfUsersSharingItem(userId, itemId)).stream();
	}

	private static UserExample getExampleForUsersSharingContainer(final Long userId, final Long containerId) {
		return new UserExample(c -> c.andIdNotEqualTo(userId).andContainerIdEqualTo(containerId).andContainerSharedExplicitly(Boolean.TRUE));
	}

	@Override
	public Collection<LimitedUser> getUsersSharingContainer(final Long userId, final Long containerId) {
		return userMapper.selectUsersSharingContainerByExample(getExampleForUsersSharingContainer(userId, containerId), createRowBounds(null, null));
	}

	@Override
	public Stream<Long> getUserIdsSharingContainer(final Long userId, final Long containerId) {
		return userMapper.selectUserIdsSharingContainerByExample(getExampleForUsersSharingContainer(userId, containerId), createRowBounds(null, null)).stream();
	}

	@Override
	public Stream<LimitedUser> getUsersSharingContainerViaGroups(final Long containerId, final Collection<Long> groupIds) {
		return userMapper.selectUsersSharingContainerByExample(new UserExample(c -> c.andContainerIdEqualTo(containerId).andGroupIdIn(groupIds)), createRowBounds(null, null)).stream();
	}

	private Stream<LimitedUser> selectByExample(final UserExample example, final Integer offset, final Integer count) {
		return userMapper.selectByExample(example, createRowBounds(offset, count)).stream();
	}

	@Override
	public Stream<LimitedUser> getUsersNotSharingContainer(final Long userId, final Long containerId, final User userPattern, final Collection<Long> preSelectedUsers, final Integer offset, final Integer count, final List<OrderBy> orderBy) {
		return selectByExample(createExample(get(userId), userPattern, preSelectedUsers, c -> c.andNotInContainer(containerId), orderBy), offset, count);
	}

	@Override
	public Integer getUsersNotSharingContainerCount(final Long userId, final Long containerId, final User userPattern, final Collection<Long> preSelectedUsers) {
		return countByExample(createExample(get(userId), userPattern, preSelectedUsers, c -> c.andNotInContainer(containerId), null));
	}

	@Override
	public Integer getUsersNotInGroupCount(final User user, final Long groupId, final User userPattern, final Collection<Long> preSelectedUsers) {
		return countByExample(createExample(user, userPattern, preSelectedUsers, c -> c.andNotInGroup(groupId).andDeletedNotEqualTo(true), null));
	}

	@Override
	public Stream<LimitedUser> getUsersNotInGroup(final User user, final Long groupId, final User userPattern, final Collection<Long> preSelectedUsers, final Integer offset, final Integer count, final List<OrderBy> orderBy) {
		return selectByExample(createExample(user, userPattern, preSelectedUsers, c -> c.andNotInGroup(groupId).andDeletedNotEqualTo(true), orderBy), offset, count);
	}

	@Override
	public Stream<LimitedUser> getUsersNotSharingItem(final Long userId, final Long itemId, final User template, final Collection<Long> preSelectedUsers, final Integer offset, final Integer count, final List<OrderBy> orderBy) {
		return selectByExample(createExample(get(userId), template, preSelectedUsers, c -> c.andUsersNotSharingItem(itemId), orderBy), offset, count);
	}

	@Override
	public Integer getUsersNotSharingItemCount(final Long userId, final Long itemId, final User template, final Collection<Long> preSelectedUsers) {
		return countByExample(createExample(get(userId), template, preSelectedUsers, c -> c.andUsersNotSharingItem(itemId), null));
	}

//	@Override
//	public Stream<LimitedUser> getRecipientsOfSharedItem(final Long userId, final Long itemId, final User template, final Collection<Long> preSelectedUsers, final Integer first, final Integer count, final List<OrderBy> orderBy) {
//		return userMapper.selectUsersSharingItemByExample(getRecipientExample(userId, itemId, template, preSelectedUsers, orderBy), createRowBounds(first, count)).stream();
//	}

	@Override
	public Integer getRecipientsOfSharedItemCount(final Long userId, final Long itemId, final User template, final Collection<Long> preSelectedUsers) {
		return userMapper.countUsersSharingItemByExample(getRecipientExample(userId, itemId, template, preSelectedUsers, null));
	}

	/**
	 * TODO: I should do some work on those result sets. This call is used for the client (to show those users)
	 * but is also used by the server to update shared items. Only the second scenario do we need crypto details.
	 * Details are not sent to the client but unnecessary object creation is the result.
	 */
	@Override
	public Stream<LimitedUser> getUsersSharingItem(final Long userId, final Long itemId, final User template, final Collection<Long> preSelectedUsers, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return userMapper.selectSharedItemUsersSharingItemByExample(getRecipientExample(userId, itemId, template, preSelectedUsers, orderBy), createRowBounds(first, count)).stream();
	}

	@Override
	public Stream<LimitedUser> getUsersSharingContainer(final Long ownerId, final Long containerID, final User user, final Collection<Long> preSelectedUsers, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return userMapper.selectUsersSharingContainerByExample(getContainerRecipientExample(ownerId, containerID, user, preSelectedUsers, orderBy), createRowBounds(first, count)).stream();
	}

	@Override
	public Integer getUsersSharingContainerCount(final Long ownerId, final Long containerID, final User user, final Collection<Long> preSelectedUsers) {
		return userMapper.countUsersSharingContainerByExample(getContainerRecipientExample(ownerId, containerID, user, preSelectedUsers, null));
	}

	@Override
	public Integer countUsersWithInsufficientClearance(final Collection<Long> userIds, final Integer minimumSecurityClearance) {
		return countByExample(new UserExample(c -> c.andIdIn(userIds).andSecurityClearanceLessThan(minimumSecurityClearance)));
	}

	@Override
	public User getOwnerOfDocument(final Long documentId) {
		final List<User> list = userMapper.selectByDocumentId(documentId);

		if (list.size() != 1) {
			throw new NoSuchRecordException("Could not find owner of document.");
		}

		return list.get(0);
	}
}
