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

import static com.spenego.Obidos.shared.dto.BaseDTO.MIN_DYNAMIC_ID;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.DuplicateKeyException;

//import com.mysql.jdbc.MysqlDataTruncation;
// Bug #35
// mysql-connector-java has changed class
// spgdev May-27-2024
//import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import com.spenego.Obidos.server.dao.Mapper;
import com.spenego.Obidos.server.dao.ObidosRowBounds;
import com.spenego.Obidos.server.model.Model;
import com.spenego.Obidos.server.model.ObidosCriteria;
import com.spenego.Obidos.server.model.ObidosExample;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.HasName;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.exceptions.DuplicateRecordException;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;
import com.spenego.Obidos.shared.exceptions.UnableToCreateRecordException;
import com.spenego.Obidos.shared.exceptions.ValueTooLongException;

public abstract class ObidosOperations<T extends Model> implements Operations<T> {
	protected abstract Logger getLogger();

	protected abstract Mapper<T> getMapper();

	/**
	 * A method used when displaying debug messages or exception messages.
	 * 
	 * @return A string that defines what type of object caused the error.
	 */
	protected abstract String getModelName();

	//private final Pattern dataTooLongPattern = Pattern.compile(".*Data too long for column '(.*)' at row.*");

	protected static ObidosRowBounds createRowBounds(final Integer skip, final Integer count) {
		return new ObidosRowBounds(skip, count);
	}

	// If a db call is causing a Deadlock exception, use this wrapper.
	protected final <Z, R> R attemptOp(final Function<Z, R> op, final Supplier<Z> supplier)
			throws NoSuchRecordException {
		final Z example = supplier == null ? null : supplier.get();

		for (int i = 0; i < 8; i++) {
			try {
				return op.apply(example);
			} catch (final DeadlockLoserDataAccessException ex) {
				getLogger().warn(() -> "Caught " + ex);
				if (i == 7) {
					throw ex;
				}
			}
		}
		return null;
	}

	protected final <Z, K extends ObidosExample<Z>> K createExample(final Supplier<K> supplier,
			final List<OrderBy> orderBy) {
		final K example = supplier.get();

		if (orderBy != null) {
			example.setOrderByClause(generateOrderByString(orderBy));
		}

		return example;
	}

	protected final <Z> Integer countByExample(final ObidosExample<Z> example) {
		return getMapper().countByExample(example);
	}

	private final void handleEx(final DataIntegrityViolationException ex, final Long id)
			throws DuplicateRecordException, ValueTooLongException {
		/*
		if (cause.getClass().equals(MysqlDataTruncation.class)) {
			final Matcher matcher = dataTooLongPattern.matcher(cause.getMessage());
			throw matcher.matches() ? new ValueTooLongException(matcher.group(1)) : new ValueTooLongException();
		}
		*/
		final Logger logger = getLogger();
		if (getMapper().selectByPrimaryKey(id) == null) {
			logger.exception(ex);
			logger.exception(ex.getRootCause());
			throw new DuplicateRecordException("Object is not unique");
		}
		logger.error(() -> "duplicate record with ID = " + id);
		logger.exception(ex);
	}

	/**
	 * This method specifies a throws clause so that classes that override this
	 * method
	 * can do it if required.
	 *
	 * @param record
	 * @return
	 */
	@Override
	public Long create(final T record) {
		final Date now = new Date();
		record.setCreatedAt(now);
		record.setUpdatedAt(now);
		record.setVersion(1);

		int ret = 0;

		try {
			ret = getMapper().insert(record);
		} catch (final DuplicateKeyException ex) {
			throw new DuplicateRecordException("System with name " + record.getName() + " already exists");
		} catch (final DataIntegrityViolationException ex) {
			handleEx(ex, record.getId());
			throw ex;
		} catch (final Exception ex) {
			getLogger().error(() -> "Caught " + ex);
		}
		if (ret != 1) {
			throw new ServerSideException("Unable to create " + getModelName() + ": " + record);
		}

		return record.getId();
	}

	private static final long nextId() {
		while (true) { // prevents us from duping IDs with records that do not use randomized IDs
			final long id = ServerUtils.nextRandomLong(Long.MAX_VALUE);
			if (id >= MIN_DYNAMIC_ID) {
				return id;
			}
		}
	}

	/**
	 * IDs are randomized to make them unique. Since several methods only
	 * take a Long ID as an argument, swapping args may be a common coding error.
	 * Making a call with swapped args would lead to undesirable results if the
	 * IDs existed for both args. This is *much* less likely to occur if we
	 * randomize
	 * the IDs over a 64bit space.
	 *
	 * @return
	 * @throws UnableToCreateRecordException Unable to generate a unique ID. This is
	 *                                       a very
	 *                                       unexpected error. If it is noticed,
	 *                                       examine the code to see why the random
	 *                                       number
	 *                                       generator is duplicating IDs.
	 * @throws DuplicateRecordException
	 */
	protected final Long createWithRandomID(final T o) throws ValueTooLongException, UnableToCreateRecordException, DuplicateRecordException {
		final Date now = new Date();
		o.setCreatedAt(now);
		o.setUpdatedAt(now);
		o.setVersion(1);

		for (int i = 0; i < 3; i++) {
			final long id = nextId();

			try {
				o.setId(id);
				getMapper().insert(o);
				return id;
			} catch (final DataIntegrityViolationException ex) {
				getLogger().error(() -> "Could not create " + ((HasName) o).getName() + ": " + ex);
				getLogger().exception(ex);
				handleEx(ex, id);
			} catch (final Exception ex) {
				throw new UnableToCreateRecordException("Caught " + ex + " when attempting to create record.");
			}
		}
		throw new UnableToCreateRecordException("Unable to create a new " + getModelName() + " at this time");
	}

	protected String containerOrderByName() {
		return "name";
	}

	protected String createdAtName() {
		return "created_at";
	}

	private final String generateOrderBy(final OrderBy orderBy) {
		if (orderBy == null) {
			return null;
		}
		switch (orderBy) {
			case SMTP_SERVER_NAME_ASC:
				return "smtp_server asc";
			case USERNAME_ASC:
				return "username asc";
			case FULLNAME_ASC:
				return "fullname asc";
			case EMAIL1_ASC:
				return "email1 asc";
			case EMAIL2_ASC:
				return "email2 asc";
			case EMAIL3_ASC:
				return "email3 asc";
			case GROUP_NAME_ASC:
				return "name asc";
			case CREATE_TIME_ASC:
				return createdAtName() + " asc";
			case UPDATE_TIME_ASC:
				return "updated_at asc";
			case USER_GROUP_COMBO_TYPE_ASC:
				return "type asc";
			case USER_GROUP_COMBO_NAME_ASC:
			case ITEM_NAME_ASC:
				return "name asc";
			case CONTAINER_NAME_ASC:
				return containerOrderByName() + " asc";
			case UDF_POSITION_ASC:
				return "position asc";
			case UNREAD_ASC:
				return "unread asc";

			case SMTP_SERVER_NAME_DESC:
				return "smtp_server desc";
			case USERNAME_DESC:
				return "username desc";
			case FULLNAME_DESC:
				return "fullname desc";
			case EMAIL1_DESC:
				return "email1 desc";
			case EMAIL2_DESC:
				return "email2 desc";
			case EMAIL3_DESC:
				return "email3 desc";
			case CREATE_TIME_DESC:
				return createdAtName() + " desc";
			case UPDATE_TIME_DESC:
				return "updated_at desc";
			case UNREAD_DESC:
				return "unread desc";
			case USER_GROUP_COMBO_TYPE_DESC:
				return "type desc";
			case USER_GROUP_COMBO_NAME_DESC:
			case ITEM_NAME_DESC:
			case GROUP_NAME_DESC:
				return "name desc";
			case CONTAINER_NAME_DESC:
				return containerOrderByName() + " desc";
			case UDF_POSITION_DESC:
				return "position desc";
			default:
				return "username asc";
		}
	}

	protected List<OrderBy> getComboTypeOrderByList(final List<OrderBy> orderByList) {
		if (orderByList != null) {
			orderByList.add(0, OrderBy.USER_GROUP_COMBO_TYPE_ASC);
		}
		return orderByList;
	}

	private final String generateOrderByString(final List<OrderBy> orderByList, final StringBuilder psb) {
		if (orderByList == null) {
			return null;
		}
		if (orderByList.size() == 1) {
			final String v = generateOrderBy(orderByList.iterator().next());
			return psb == null ? v : psb.append(v).toString();
		}

		final StringBuilder sb = (psb == null) ? new StringBuilder(128) : psb;
		boolean firstPass = true;

		for (final OrderBy orderBy : orderByList) {
			if (!firstPass) {
				sb.append(',');
			}
			sb.append(generateOrderBy(orderBy));
			firstPass = false;
		}

		return sb.toString();
	}

	protected final String generateOrderByString(final List<OrderBy> orderByList) {
		return generateOrderByString(orderByList, new StringBuilder());
	}

	protected final String generatePreSelectedOrderClause(final Collection<Long> preSelectedSet, final String idAlias, final List<OrderBy> orderByList) {
		StringBuilder sb = null;
		final boolean orderByEmpty = orderByList == null || orderByList.isEmpty();

		if (preSelectedSet != null && !preSelectedSet.isEmpty()) {
			sb = new StringBuilder(128);
			sb.append("field(").append(idAlias);
			for (final Long u : preSelectedSet) {
				sb.append(',').append(u);
			}
			sb.append(')');
			if (orderByEmpty) {
				return sb.toString();
			}
			sb.append(" desc, ");
		}

		return orderByEmpty ? null : generateOrderByString(orderByList, sb);
	}

	@Override
	public T get(final Long id) throws NoSuchRecordException {
		if (id == null) {
			return null;
		}
		final T o = getMapper().selectByPrimaryKey(id);
		if (o == null) {
			throw new NoSuchRecordException(getModelName(), id);
		}

		return o;
	}

	@Override
	public Void update(final T o) throws NoSuchRecordException {
		o.setUpdatedAt(new Date());
		o.setCreatedAt(get(o.getId()).getCreatedAt());
		if (getMapper().updateByPrimaryKey(o) != 1) {
			throw new NoSuchRecordException(getModelName(), o.getId());
		}
		return null;
	}

	private void sleepRandom(final int max) {
		try {
			final int s = ServerUtils.nextRandomInt(max);
			getLogger().info(() -> "Sleeping for " + s + " milliseconds");
			Thread.sleep(s);
		} catch (final Exception ex) { // NOSONAR -- the entire point is to provide a sleep function that does not
										// generate exceptions
			getLogger().warn(() -> "caught " + ex + " while sleeping");
		}
	}

	@Override
	public Void updateSelective(final T o, final Date updateTime) {
		o.setUpdatedAt(updateTime);
		o.setCreatedAt(null);
		o.setVersion(null);

		if (o.getId() == null) {
			getLogger().error(() -> getModelName() + " object has a null id");
			throw new ServerSideException("Object had no id.");
		}

		int failedTransactionCount = 0;

		while (true) {
			try {
				if (getMapper().updateByPrimaryKeySelective(o) != 1) {
					throw new NoSuchRecordException(getModelName(), o.getId());
				}
				return null;
			} catch (final DeadlockLoserDataAccessException ex) {
				if (++failedTransactionCount == 5) {
					throw ex;
				}
				final int count = failedTransactionCount;
				getLogger().warn(() -> "Caught DeadlockLoserDataAccessException on attempt " + count);
				sleepRandom(count * 10);
			} catch (final CannotAcquireLockException ex) {
				if (++failedTransactionCount == 5) {
					throw ex;
				}
				final int count = failedTransactionCount;
				getLogger().error(() -> "Caught CannotAcquireLockException on attempt " + count, ex);
				sleepRandom(count * 10);
			}
		}
	}

	@Override
	public Void updateSelective(final T o) {
		return updateSelective(o, new Date());
	}


	@Override
	public Void delete(final Long id) throws NoSuchRecordException {
		if (getMapper().deleteByPrimaryKey(id) != 1) {
			throw new NoSuchRecordException(getModelName(), id);
		}
		return null;
	}

	protected static final <A> ObidosCriteria<A> gen(final User caller, final ObidosCriteria<A> criteria, final User template) {
		if (template != null) {
			if (caller != null && caller.isAdmin()) {
				criteria.andFullnameOrUsernameLike(template.getUsername());
			}

			criteria.andAnyEmailLike(template.getEmail1());
			final String fn = template.getFullname();
			if (fn != null) {
				if (fn.length() > 1 && fn.substring(0, 1).equals("!")) {
					criteria.andFullnameNotLike(fn.substring(1));
				} else {
					criteria.andFullnameLike(fn);
				}
			}
			criteria.andPhoneLike(template.getPhone());
			criteria.andAdministratorEqualTo(template.getAdministrator());

			if (template.getDeleted() != null && caller != null) {
				criteria.andDeletedEqualTo(caller.isAdmin() ? template.getDeleted() : Boolean.FALSE);
			}

			criteria.andLockedEqualTo(template.getLocked());
			criteria.andPasswordChangeRequiredEqualTo(template.getPasswordChangeRequired());
		}
		return criteria;
	}
}
