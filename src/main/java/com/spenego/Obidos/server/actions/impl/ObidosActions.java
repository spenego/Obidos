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

package com.spenego.Obidos.server.actions.impl;

import static com.spenego.Obidos.server.model.Audit.SHARE_ITEM;
import static com.spenego.Obidos.server.model.Audit.TWO_FACTOR_CODE_NOT_SUPPLIED;
import static com.spenego.Obidos.server.model.Audit.TWO_FACTOR_NOT_ENABLED;
import static com.spenego.Obidos.server.utils.ServerUtils.validateLicense;
import static com.spenego.Obidos.shared.dto.NotificationDTO.ITEM_UPDATED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.NOTE_UPDATED;
import static java.lang.Thread.MIN_PRIORITY;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dozer.Mapper;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.AuditActions;
import com.spenego.Obidos.server.actions.NotificationActions;
import com.spenego.Obidos.server.actions.TwoFactorAuthenticationActions;
import com.spenego.Obidos.server.model.Assignable;
import com.spenego.Obidos.server.model.Audit;
import com.spenego.Obidos.server.model.Capability;
import com.spenego.Obidos.server.model.Container;
import com.spenego.Obidos.server.model.ContainerAssignment;
import com.spenego.Obidos.server.model.Group;
import com.spenego.Obidos.server.model.ItemAssignment;
import com.spenego.Obidos.server.model.LimitedUser;
import com.spenego.Obidos.server.model.Model;
import com.spenego.Obidos.server.model.Notification;
import com.spenego.Obidos.server.model.SharedItemUser;
import com.spenego.Obidos.server.model.SystemConfig;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.CapabilityOperations;
import com.spenego.Obidos.server.operations.ContainerAssignmentOperations;
import com.spenego.Obidos.server.operations.ContainerOperations;
import com.spenego.Obidos.server.operations.GroupMemberOperations;
import com.spenego.Obidos.server.operations.GroupOperations;
import com.spenego.Obidos.server.operations.ItemAssignmentOperations;
import com.spenego.Obidos.server.operations.ItemOperations;
import com.spenego.Obidos.server.operations.NotificationOperations;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.operations.SystemConfigOperations;
import com.spenego.Obidos.server.operations.UserOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.MemoryWiper;
import com.spenego.Obidos.server.utils.NotificationEngine;
import com.spenego.Obidos.server.utils.NotificationEngine.NotificationType;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.HasName;
import com.spenego.Obidos.shared.dto.Clearable;
import com.spenego.Obidos.shared.dto.HasId;
import com.spenego.Obidos.shared.dto.LicenseKeyDTO;
import com.spenego.Obidos.shared.dto.NotificationDTO;
import com.spenego.Obidos.shared.exceptions.LicenseKeyException;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * This is the abstract class for building all actions. All actions will,
 * almost certainly, use most of the methods provided by this base class.
 *
 *
 * @author	Mike Morgan
 * @since	Obidos 1.0
 *
 */
public abstract class ObidosActions<O extends Model> implements Runnable, Executor {
	protected static final String NULLSTRING = null;

	protected static final long MS_PER_MIN = 1000L * 60;
	protected static final long MS_PER_DAY = MS_PER_MIN * 60 * 24;

	private static final Random									random = new SecureRandom();
	@Autowired private final AuditActions						auditActions = null;
	@Autowired private final NotificationOperations				notificationOperations = null;
	@Autowired private final NotificationActions				notificationActions = null;
	@Autowired private final SystemConfigOperations				systemConfigOperations = null;
	@Autowired private final TwoFactorAuthenticationActions		twoFactorAuthenticationActions = null;
	@Autowired protected final CapabilityOperations				capabilityOperations = null;
	@Autowired protected final ContainerAssignmentOperations	containerAssignmentOperations = null;
	@Autowired protected final ContainerOperations				containerOperations = null;
	@Autowired protected final GroupMemberOperations			groupMemberOperations = null;
	@Autowired protected final GroupOperations					groupOperations = null;
	@Autowired protected final ItemAssignmentOperations			itemAssignmentOperations = null;
	@Autowired protected final ItemOperations					itemOperations = null;
	@Autowired protected final UserOperations					userOperations = null;
	@Autowired protected ObjectFactory<NotificationEngine<ItemAssignment>> itemNotificationEngineFactory;
	@Autowired private Mapper									dtoMapper;
	@Autowired private final MemoryWiper						memoryWiper = null;
	protected boolean											initialized = false;
	protected boolean											blockAudit = false;
	private boolean												terminateThread = false;

	// We can't do things, like parallel processing, while running within JUnit
	protected boolean											runningInUnitTest = false;
	private final Collection<Runnable>							runnableQueue = new ArrayList<>();

	private final class ObidosThreadFactory implements ThreadFactory {
		final String name;

		ObidosThreadFactory(final String name) {
			this.name = name;
		}

		@Override
		public Thread newThread(final Runnable r) {
			getLogger().info(() -> "ObidosThreadFactory; creating new thread: " + name);
			final Thread t = new Thread(r, name);
			t.setDaemon(true);
			t.setPriority(MIN_PRIORITY);
			return t;
		}
	}

	protected abstract Operations<O> getOperations();
	protected abstract Logger getLogger();

	protected final ThreadFactory getThreadFactory(final String name) {
		return new ObidosThreadFactory(name);
	}

	public void setRunningInUnitTest() {
		runningInUnitTest = true;
	}

	protected enum ActionType {
		CREATE, UPDATE, READ, DELETE
	}

	protected synchronized void terminateThread() {
		terminateThread = true;
	}

	protected static String getString(final Supplier<String> supplier) {
		return supplier == null ? "" : supplier.get();
	}

	protected static class ClosureExceptionWrapper {
		ServerSideException ex;
		final String name;
		ClosureExceptionWrapper(final String name) { this.name = name; }
	}

	protected static final boolean isTrue(final Boolean b) {
		return Boolean.TRUE.equals(b);
	}

	protected static final boolean isFalse(final Boolean b) {
		return Boolean.FALSE.equals(b);
	}

	private static <T> String exceptionName(T obj) {
		return (obj instanceof HasName) ? ((HasName) obj).getName() : "Unnamed object";
	}

	protected final O getModel(final Long id) {
		return getOperations().get(id);
	}

	protected final void updateUser(final User u) {
		userOperations.updateSelective(u);
	}

	protected final User getUser(final Long userId) {
		if (userId == null) { throw new ServerSideException("You must specify a user ID."); }

		return userOperations.get(userId);
	}

	protected final Capability getCapability(final HasId user) {
		return capabilityOperations.getByUserId(user.getId());
	}

	protected final boolean userIsTombstoned(final Long userId) {
		return getUser(userId).getDeleted();
	}

	protected final Group getGroup(final Long groupId) {
		if (groupId == null) { throw new ServerSideException("You must specify a group."); }

		return groupOperations.get(groupId);
	}

	protected final Stream<SharedItemUser> getUsersSharingItem(final Long callerId, final Long itemId) {
		return userOperations.getUsersSharingItem(callerId, itemId);
	}

	protected final Stream<Long> getUserIdsSharingItem(final Long callerId, final Long itemId) {
		return userOperations.getUserIdsSharingItem(callerId, itemId);
	}

	protected final Collection<LimitedUser> getUsersSharingContainer(final Long callerId, final Long containerId) {
		return userOperations.getUsersSharingContainer(callerId, containerId);
	}

	protected final Stream<Long> getUserIdsSharingContainer(final Long callerId, final Long containerId) {
		return userOperations.getUserIdsSharingContainer(callerId, containerId);
	}

	protected final Stream<LimitedUser> getUsersSharingContainerViaGroups(final Long containerId, final Collection<Long> groupIdCollection) {
		return userOperations.getUsersSharingContainerViaGroups(containerId, groupIdCollection);
	}

	/**
	 * Get the users in the group except for the caller.
	 */
	protected final Stream<LimitedUser> getOthersInGroup(final User caller, final Long groupId) {
		return groupMemberOperations.getUsersInGroup(groupId).filter(user -> !caller.self(user.getId()));
	}

	protected final ContainerAssignment getContainerAssignment(final Long containerAssignmentId) {
		try {
			return containerAssignmentOperations.get(containerAssignmentId);
		} catch(final NoSuchRecordException ex) {
			throw new NoSuchRecordException("You no longer have access to this container.");
		}
	}

	protected final ContainerAssignment getContainerAssignment(final Long containerId, final Long userId) {
		return containerAssignmentOperations.get(containerId, userId);
	}

	protected Long getContainerAssignmentId(final Long containerId, final Long userId) {
		return getContainerAssignment(containerId, userId).getId();
	}

	protected final String getContainerName(final Long containerId) {
		return getContainer(containerId).getName();
	}

	protected final Container getContainer(final Long containerId) {
		return containerOperations.get(containerId);
	}

	protected final Supplier<Collection<ItemAssignment>> getItemAssignmentSupplierViaGroupId(final Long groupId)  {
		return () -> itemAssignmentOperations.getAssignmentsViaGroupId(groupId);
	}

	protected static final <T extends Assignable> NotificationEngine<T> getNotificationEngine(final NotificationType type, final ObjectFactory<NotificationEngine<T>> engineFactory)  {
		return engineFactory.getObject().setType(type);
	}

	protected static final <T extends Assignable> NotificationEngine<T> getNotificationEngine(final NotificationType type, final ObjectFactory<NotificationEngine<T>> engineFactory, final Supplier<Collection<T>> supplier)  {
		return getNotificationEngine(type, engineFactory).addSupplier(supplier);
	}

	/**
	 * Certain actions, like sending notifications and auditing, are not part of the Transaction and a failure in them should
	 * not cause a failure of the primary call. They should also not be attempted should an exception be generated during the
	 * transaction.  Actions outside the scope of the transaction are performed post-commit by calling this method.
	 *
	 * This method should also be used when the action should only be taken once the transaction is complete.
	 *
	 * @param action an action that is run after the transaction is complete
	 * @return
	 */
	public final void postCommitAction(final Runnable action) {
		ServerUtils.postCommitAction(action, getLogger());
	}

	/**
	 * Queue a notification to be sent in a separate thread. This prevents notification generation from delaying the return from the Action layer.
	 * Notifications do not need to be performed prior to the call to the Action layer returning.
	 *
	 * @param runnable
	 */
	protected final void queueNotification(final Runnable runnable) {
		notificationActions.runInNotificationThread(runnable);
	}

	/**
	 * Queue a notification to be sent in a separate thread. This prevents notification generation from delaying the return from the Action layer.
	 * Notifications do not need to be performed prior to the call to the Action layer returning.
	 *
	 * @param runnable
	 */
	protected final void postCommitQueue(final Runnable runnable) {
		postCommitAction(() -> queueNotification(runnable));
	}

	/**
	 * Queues audit actions. The audit processing is done in the auditor thread.
	 *
	 * @param supplier
	 *				An AuditAction closure. This will be invoked by the audit
	 *				thread in the near future. This allows the primary service
	 *				action to complete as soon as possible.
	 */
	private final void queue(final Supplier<Audit> supplier) {
		if (!blockAudit) postCommitAction(() -> auditActions.queueAuditAction(supplier));
	}

	/**
	 * With the Stream supplier, process each element via the supplied processor.  Catch exceptions in a closure for processing after we process the stream.
	 *
	 * @param action A string that describes what the processor will do.
	 * @param supplier This supplies a stream of T
	 * @param processor Invoked for each T element supplied by the supplier.
	 * @param exceptionProcessor you may supply your own exception processor which will be invoked for every exception. If null, exceptions will be logged.
	 * @param parallel If true, operations will be run in parallel. You will want to pass false when running unit tests.
	 *
	 * @return
	 */
	protected final <T,R> Void processStream(final Supplier<String> nameSupplier, final Supplier<Stream<T>> supplier, final Consumer<T> consumer, final Consumer<ClosureExceptionWrapper> exceptionProcessor, final Boolean parallel) {
		final ExWrapper<ClosureExceptionWrapper, R> wrapper = new ExWrapper<>();

		if (nameSupplier != null) {
			getLogger().info(nameSupplier::get);
		}

		(isTrue(parallel) ? supplier.get().parallel() : supplier.get()).forEach(t -> wrapper.apply(new ClosureExceptionWrapper(exceptionName(t)), cc -> { consumer.accept(t); return null; }));

		if (exceptionProcessor == null) {
			if (nameSupplier != null) {
				wrapper.logExceptions(nameSupplier);
			}
		} else {
			wrapper.processExceptions(exceptionProcessor);
		}

		return null;
	}

	/**
	 * Spring Framework Transaction === NO THREADED STREAMS
	 *
	 * We are passing false as last arg to processStream (the parallel option).  Spring Framework Transactions
	 * do not work well with threads. Each thread ends up running in a separate transaction.  This results in countless deadlocks.
	 * Very large operations end up being twice as slow, however, in the tests I ran, it was a difference of 500ms vs. 955ms.
	 * One way around this would be to create a consumer thread that all DB operations are sent to, so that most work outside of the
	 * DB could be done in separate threads, but the final DB operation would be done by the consumer thread. How much benefit left
	 * seems insignificant since most time spent is in insert operations on the DB.
	 *
	 * So, for now, we do not use parallel streams.
	 */
	protected final <T> Void processStream(final Supplier<String> nameSupplier, final Supplier<Stream<T>> supplier, final Consumer<T> processor, final Consumer<ClosureExceptionWrapper> exceptionProcessor) {
		return processStream(nameSupplier, supplier, processor, exceptionProcessor, false);
	}

	protected final <T> Void processStream(final Supplier<String> nameSupplier, final Supplier<Stream<T>> supplier, final Consumer<T> processor) {
		return processStream(nameSupplier, supplier, processor, (Consumer<ClosureExceptionWrapper>) null);
	}

	protected final <T> Void processStream(final Supplier<String> nameSupplier, final Supplier<Stream<T>> supplier, final Consumer<T> processor, final Supplier<String> exs) {
		return processStream(nameSupplier, supplier, processor, ce -> exceptionLoggerThrower(exs.get(), ce));
	}

	protected static final <T> List<T> packageItem(final T t) {
		return Collections.singletonList(t);
	}

	protected static final byte[] sanitize(final byte[] bytes) {
		return ServerUtils.sanitize(new String(bytes)).getBytes();
	}

	protected final ItemAssignment getItemAssignment(final Long itemAssigmentId) throws NoSuchRecordException {
		try {
			return itemAssignmentOperations.get(itemAssigmentId);
		} catch(final NoSuchRecordException ex) {
			throw new NoSuchRecordException("You no longer have access to this item.");
		}
	}

	public final Long getItemId(final User caller, final Long itemAssignmentId) throws NoSuchRecordException {
		if (itemAssignmentId == null) {
			throw new ServerSideException("You must specify an item.");
		}

		final ItemAssignment ia = getItemAssignment(itemAssignmentId);

		if (caller != null && !caller.self(ia.getUserId())) {
			throw new PermissionDeniedException("You do not own that item.");
		}

		return ia.getItemId();
	}

	void logException(final ClosureExceptionWrapper ce, final Supplier<String> supplier) {
		getLogger().error(() -> "Caught " + ce.ex + " while " + supplier.get() + " " + ce.name);
		getLogger().exception(ce.ex);
	}

	protected final void exceptionLoggerThrower(final String op, final ClosureExceptionWrapper ce) {
		logException(ce, () -> op);
		throw ce.ex;
	}

	protected static final <T> Stream<T> toStream(final T o) {
		return Arrays.asList(o).stream();
	}

	/**
	 * A class used to catch and store exceptions thrown while invoking functional interface methods.
	 * The exceptions can be acted upon after the apply method has been invoked.
	 *
	 * @author mmorgan
	 *
	 * @param <T>
	 * @param <R>
	 */
	private final class ExWrapper<T extends ClosureExceptionWrapper, R> {
		public ExWrapper() {
			exceptions = null;
		}
		private Collection<T> exceptions;

		void processExceptions(final Consumer<T> ep) {
			if (exceptions != null) {
				for(final T ex : exceptions) {
					ep.accept(ex);
				}
			}
		}

		/**
		 *
		 * @param op -- the operation that was being attempted
		 */
		void logExceptions(final Supplier<String> op) {
			try {
				processExceptions(t -> logException(t, op));
			} catch (final ServerSideException e) { /* should not be possible, but if it happens, just ignore it */ }
		}

		R apply(final T t, final Function<T, R> f) {
			try {
				return f.apply(t);
			} catch(final ServerSideException ex) { // multiple threads can be invoking apply()
				synchronized(this) {
					if (this.exceptions == null) {
						this.exceptions = new ArrayList<>();
					}
					t.ex = ex;
					exceptions.add(t);
				}
				return null;
			}
		}
	}

	/**
	 * Override this method and have it return the proper DELETE field from the
	 * Audit Model.
	 *
	 * @return The Audit delete code appropriate for the implementing class.
	 */
	protected abstract Integer getAuditDeleteAction();

	protected final void auditItem(final Integer action, final Long userId, final Long itemId) {
		queue(() -> auditActions.createAuditEntry(action, userId, itemOperations.get(itemId), itemId));
	}

	protected final void audit(final Integer action, final String username, final Long userId, final Object object, final Long objectId) {
		queue(() -> auditActions.createAuditEntry(action, userId, object, objectId));
	}

	protected final void audit(final Integer action, final String username, final Long userId, final Object object, final Long objectId, final Long newObjectId) {
		queue(() -> auditActions.createAuditEntry(action, userId, object, objectId, newObjectId));
	}

	protected final void audit(final Integer action, final String username, final Long userId, final Object object, final Long objectId, final Long newObjectId, final Object details) {
		queue(() -> auditActions.createAuditEntry(action, userId, object, objectId, newObjectId, details.toString()));
	}

	/**
	 * When making calls from daemon threads, we can not call queue() since it uses a postCommit mechanism and there are no transactions for the daemon threads.
	 */
	protected final void immediateAudit(final Integer action, final String username, final Long userId, final String objectName, final Long objectId, final Long newObjectId, final String details) {
		auditActions.queueAuditAction(() -> auditActions.createAuditEntry(action, userId, objectName, objectId, newObjectId, details));
	}

	/**
	 * Exception actions need to be queued immediately. They can not use the postCommitAction method because he exception causes a rollback, not a commit.
	 */
	protected final void auditExceptionAction(final Integer action, final String username, final Long userId, final String objectName, final Long objectId, final Long newObjectId, final String details) {
		auditActions.queueAuditAction(() -> auditActions.createAuditEntry(action, userId, objectName, objectId, newObjectId, details));
	}

	protected final void audit(final Integer action, final String username, final Long userId, final String details) {
		queue(() -> auditActions.createAuditEntry(action, userId, null, null, null, details));
	}

	protected final void auditContainer(final Integer action, final User caller, final Container c, final Long newObjectId, final String details) {
		audit(action, caller.getUsername(), caller.getId(), c, c.getId(), newObjectId, details);
	}

	protected final void auditGroupAction(final Integer action, final String username, final Long userId, final Long objectId, final Long recipientId, final Object object) {
		queue(() -> auditActions.createAuditEntry(action, userId, objectId, recipientId, object));
	}

	protected final void auditContainerShare(final Integer action, final String username, final Long userId, final Long recipientId, final Long containerId) {
		queue(() -> auditActions.createContainerShareAuditEntry(action, userId, recipientId, containerId));
	}

	protected final void auditShare(final String username, final Long userId, final Long recipientId, final Long itemId) {
		queue(() -> auditActions.createItemShareAuditEntry(SHARE_ITEM, userId, recipientId, itemId));
	}

	protected final void auditGrant(final String username, final Long userId, final Long recipientId, final Long itemId, final int action) {
		queue(() -> auditActions.createAuditEntry(action, userId, itemId, recipientId, (Object) null));
	}

	protected final void auditRevoke(final String username, final Long userId, final Long recipientId, final Model item) {
		queue(() -> auditActions.createRevokeAuditEntry(userId, recipientId, item));
	}

	protected final void auditRevokeFromGroup(final String username, final Long userId, final Long groupId, final Model item) {
		queue(() -> auditActions.createGroupRevokeAuditEntry(userId, groupId, item));
	}

	protected final void auditOwned(final String username, final Long userId, final Long recipientId, final Long itemId) {
		queue(() -> auditActions.createItemOwnedAuditEntry(userId, recipientId, itemId));
	}

	protected final void auditContainerOwned(final String username, final Long userId, final Long recipientId, final Long containerId) {
		queue(() -> auditActions.createContainerOwnedAuditEntry(userId, recipientId, containerId));
	}

	protected final void auditItemRelinquish(final String username, final Long userId, final Long recipientId, final String objectName, final Long objectId) {
		queue(() -> auditActions.createItemRelinquishAuditEntry(userId, recipientId, objectName, objectId));
	}

	private <T extends Clearable> T queueForWipe(final T c) {
		memoryWiper.addReferent(c);
		return c;
	}

	protected final <T extends Clearable, Z> Z convert(final T c, final Class<Z> clazz) {
		return dtoMapper.map(queueForWipe(c), clazz);
	}

	/**
	 * Converts the objects supplied by the supplier to a list of objects returned by the converter.
	 *
	 * @param supplier
	 * @param converter
	 * @return
	 */
	protected <T,R> List<R> map(final Supplier<Stream<T>> supplier, final Function<T,R> converter) {
		return supplier.get().map(converter::apply).collect(Collectors.toList());
	}

	/**
	 * Converts the objects supplied by the supplier to type clazz. In an effort to limit the duration that
	 * potentially sensitive data resides in memory, every object supplied by the supplier is queued to be
	 * wiped sometime in the future.  Unfortunately, if the values are Strings, they will persist in memory.
	 *
	 * @param supplier
	 * @param clazz
	 * @return
	 */
	protected final <T extends Clearable, Z> List<Z> convert(final Supplier<Stream<T>> supplier, final Class<Z> clazz) {
		return map(supplier, c -> convert(c, clazz));
	}

	/**
	 * A method that will wrap the specified operation in a try/catch block that
	 * ensures only ServerSideExceptions are thrown.
	 *
	 * @param op
	 * @return
	 */
	protected final <R> R wrapAction(final Supplier<R> op) {
		try {
			return op.get();
		} catch (final ServerSideException ex) {
			throw ex;
		} catch (final Throwable t) {
			getLogger().exception(t);
			throw new ServerSideException(t);
		}
	}

	protected Void delete(final User caller, final Long id) {
		audit(getAuditDeleteAction(), caller.getUsername(), caller.getId(), getModel(id).getName(), id);
		return getOperations().delete(id);
	}

	protected final SystemConfig getSystemConfig() {
		return systemConfigOperations.get(1L);
	}

	protected LicenseKeyDTO getCurrentLicense() {
		final SystemConfig sc = getSystemConfig();
		if (sc.getLicense() == null) {
			throw new LicenseKeyException("No license has been installed yet.");
		}
		return validateLicense(sc.getLicense(), sc.getLicensePublicKey());
	}

	protected static final <T,R> void ensureElementWasNotYetShared(final Function<T,R> f, final String element, final Supplier<String> nameSupplier) {
		try {
			f.apply(null);
			throw new ServerSideException("You have already shared this " + element + " with " + nameSupplier.get() + ".");
		} catch (final NoSuchRecordException ex) { /* We expect this to happen */ }
	}

	private void createNotification(final Notification notification) {
		notificationOperations.create(notification);
	}

	protected final Integer getNotificationCount(final Long userId) {
		return notificationOperations.getNotificationCount(userId, true, null);
	}

	protected final void createNotification(final Long ownerId, final Long recipientId, final Long targetId, final String name, final int action, final String shareComment) {
		if (!userIsTombstoned(recipientId)) {
			createNotification(new Notification(ownerId, recipientId, targetId, name, action, shareComment));
		}
	}

	protected final void createNotification(final Long userId, final int action) {
		if (!userIsTombstoned(userId)) {
			createNotification(new Notification(userId, action));
		}
	}

	private boolean updateNotification(final int action, final Long ownerId, final Long recipientId, final Long targetId) { // NOSONAR -- bug in SonarQube, same value is not returned
		if (action == NOTE_UPDATED || action == ITEM_UPDATED) {
			try {
				Long id = notificationActions.getNotification(action, ownerId, recipientId, targetId).getId();
				notificationActions.update(new Notification(id, new Date()));
				return true;	// Updated the 'Created At' timestamp on the notification.
			} catch(final NoSuchRecordException ex) { /* In this case, updated is still false */ }
		}

		return false;
	}

	/**
	 * This method performs the actual notification creation. It should not be called from within the primary user thread which originated the action since it
	 * is an ancillary action that can be done in the background. It's better to have the primary action thread return as soon as possible.
	 *
	 * @param action one of (ITEM/NOTE)_(CREATED/SHARED/UPDATED/REVOKED) when value is ITEM_UPDATED or NOTE_UPDATED, and a notification already exists, no new notification
	 *               is created, only the update time is refreshed. This avoids spamming the recipient about changes.
	 * @param owner
	 * @param recipientId
	 * @param targetId			Must be either null, item Assignment Id, or Container Assignment Id
	 * @param sharedObjectName
	 * @param name
	 * @param shareComment
	 * @param notifier
	 * @return
	 */
	public final void notifyRecipient(final int action, final User owner, final Long recipientId, final Long targetId, final String sharedObjectName, final String name, final String comment, final Consumer<User> notifier) {
		final User u = getUser(recipientId);
		final Logger logger = getLogger();
		final Long ownerId = owner.getId();

		if (updateNotification(action, ownerId, recipientId, targetId)) {
			logger.info(() -> "Updated notification for " + u + " for " + sharedObjectName + " '" + name + "': " + comment);
		} else {
			logger.info(() -> "Sending notification '" + NotificationDTO.getNotificationTypeString(action) + "' to " + u + " for " + sharedObjectName + " '" + name + "': " + comment);
			createNotification(ownerId, recipientId, targetId, name, action, comment);
		}

		if (notifier != null && isFalse(u.getLocked()) && u.getAcceptExternalNotification()) {
			try {
				logger.info(() -> "Sending email notification to " + u + " regarding " + sharedObjectName + " '" + name + "'");
				notifier.accept(u);
			} catch (final ServerSideException ex) {
				logger.error(() -> "Failed to send the notification to user " + u + ":" + recipientId + " for '" + sharedObjectName + "' " + name + "'");
				logger.exception(ex);
			}
		}
	}

	protected void init() {
		getLogger().info(() -> "In base init");
	}

	/**
	 * Runs the runnable and catches and logs any Throwable objects that are thrown.
	 * @param runnable
	 */
	private final Void run(final Runnable runnable) {
		try {
			runnable.run();
		} catch(final Throwable t) {
			getLogger().error(() -> "Caught ", t);
			getLogger().exception(t);
		}
		return null;
	}

	private void queueRunnable(final Runnable r) {
		synchronized (runnableQueue) {
			if (!initialized) {
				getLogger().info(() -> "PostConstruct FAILED to be called, invoking init manually.");
				init();
			}
			runnableQueue.add(r);
			runnableQueue.notifyAll();
		}
	}

	/**
	 * Put the runnable on a queue so that it can be run within a thread dedicated to processing a particular Runnables.
	 * Currently there are two threads, an Audit processor and a Notification processor.
	 * The Audit thread sends Audit messages to the database.
	 * The Notification thread sends Notification messages to users; a table in the database too.
	 * The TombstonedUserEmbalmer thread cleans up tombstoned users by relinquishing shared items, deleting all notifications of that user, ...
	 *
	 * @param runnable
	 */
	@Override
	public final void execute(final Runnable r) {
		run(()-> queueRunnable(r));
	}

	protected String messageType() { return "Unknown"; }  // NOSONAR -- subclasses override this

	protected static String currentThreadName() {
		return Thread.currentThread().getName();
	}

	/**
	 * Sleep some random period of time because there's a good chance that other messages will come in that also need to get processed.
	 * This lets the queue grow a bit and helps reduce some overhead. It also helps stop the log from being interspersed with mixed messages.
	 * @throws InterruptedException
	 */
	protected final void sleepRandomAmount(int min, int variance) throws InterruptedException {
		final Long sleeptime = (long) random.nextInt(variance) + min;

		getLogger().info(() -> currentThreadName() + " is sleeping for " + sleeptime + " milli-seconds...");
		Thread.sleep(sleeptime);
	}

	protected synchronized boolean shouldTerminateThread() {
		return terminateThread;
	}

	/**
	 * This runs threads used to generate Audits and Notifications asynchronously from the service calls that caused them.
	 */
	@Override
	public void run() {
		if (getLogger() != null) {
			getLogger().info(() -> currentThreadName() + " thread is running. runningInUnitTest = " + runningInUnitTest);
		}

		try {
			final String RUNNABLES = " runnables.";
			final Collection<Runnable> q = new ArrayList<>();
			final String type = messageType();
			final String waitingForRunnables = "Waiting for " + type + RUNNABLES;
			final String processingRunnables = "Processing " + type + RUNNABLES;
			final Logger logger = getLogger();
			final Thread currentThread = Thread.currentThread();
			int normalPriority = currentThread.getPriority(); // always wait for notifications at normal priority to avoid priority inversion

			while (!shouldTerminateThread()) {
				logger.info(() -> waitingForRunnables);
				synchronized (runnableQueue) {
					if (runnableQueue.isEmpty()) {
						runnableQueue.wait();
					}
				}

				if (!runningInUnitTest) {
					sleepRandomAmount(2000, 1500);
					synchronized (runnableQueue) {
						if (q.addAll(runnableQueue)) {
							runnableQueue.clear();
						}
					}

					currentThread.setPriority(MIN_PRIORITY); // only switch to MIN_PRIORITY when processing elements, avoids priority inversion
					logger.info(() -> "Loaded " + q.size() + " " + type + RUNNABLES);
					processStream(() -> processingRunnables, q::stream, this::run, null, false);
					q.clear();
					currentThread.setPriority(normalPriority);
				}
			}
		} catch (final InterruptedException e) {
			getLogger().error(() -> currentThreadName() + " thread interrupted.");
			getLogger().exception(e);
			Thread.currentThread().interrupt(); // Addresses SonarQube S2142; "InterruptedException" should not be ignored
		}
	}

	public final void authenticateTwoFactorCode(final User user, final byte[] twoFactorAuthCode) {
		if (isFalse(user.getTwoFAPasswordResetEnabled())) {
			getLogger().warn(() -> "User " + user.getUsername() + " has not enabled two-factor authentication but Two-Factor auth is required for password reset.");
			audit(TWO_FACTOR_NOT_ENABLED, user.getUsername(), user.getId(), user.getEmail1(), null, null, "Password Reset Request w/o Two-Factor Enabled");
			throw new PermissionDeniedException("Due to administrator requirements, you must enabled Two-Factor Authentication.");
		}
		if (twoFactorAuthCode == null || twoFactorAuthCode.length == 0) {
			getLogger().warn(() -> "User " + user.getUsername() + " has attempted to reset a password but did not enter two-factor authentication code.");
			audit(TWO_FACTOR_CODE_NOT_SUPPLIED, user.getUsername(), user.getId(), user.getEmail1(), null, null, "Password Reset Request w/o Two-Factor Code");
			throw new PermissionDeniedException("You must supply a Two-Factor Authentication code.");
		}
		twoFactorAuthenticationActions.authenticate2FA(user, twoFactorAuthCode);
	}

	/**
	 * When doing initial database population, we need to not make audit calls.
	 */
	public void setBlockAudit(boolean blockAudit) {
		this.blockAudit = blockAudit;
	}
}
