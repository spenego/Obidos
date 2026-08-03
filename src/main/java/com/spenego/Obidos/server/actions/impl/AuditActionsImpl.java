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

import static com.spenego.Obidos.server.model.Audit.CONTAINER_OWNERSHIP_TRANSFER;
import static com.spenego.Obidos.server.model.Audit.CREATE_USER;
import static com.spenego.Obidos.server.model.Audit.EXCEPTION;
import static com.spenego.Obidos.server.model.Audit.FAILED_LOGIN_ATTEMPT;
import static com.spenego.Obidos.server.model.Audit.ITEM_OWNERSHIP_TRANSFER;
import static com.spenego.Obidos.server.model.Audit.ITEM_RELINQUISHED;
import static com.spenego.Obidos.server.model.Audit.LOGIN;
import static com.spenego.Obidos.server.model.Audit.LOGOUT;
import static com.spenego.Obidos.server.model.Audit.META_EXCEPTION;
import static com.spenego.Obidos.server.model.Audit.PASS_RESET_EXCEPTION;
import static com.spenego.Obidos.server.model.Audit.REVOKE_CONTAINER_FROM_GROUP;
import static com.spenego.Obidos.server.model.Audit.REVOKE_ITEM;
import static com.spenego.Obidos.server.model.Audit.REVOKE_ITEM_FROM_GROUP;
import static com.spenego.Obidos.server.model.Audit.SHARE_CONTAINER_WITH_GROUP;
import static com.spenego.Obidos.server.model.Audit.SUBVERSIVE_CONTAINER_VIEW;
import static com.spenego.Obidos.server.model.Audit.SUBVERSIVE_ITEM_VIEW;
import static com.spenego.Obidos.server.utils.ServerUtils.validateLicense;
import static java.lang.Thread.MIN_PRIORITY;
import static java.util.Arrays.asList;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.AuditActions;
import com.spenego.Obidos.server.actions.LoginActions;
import com.spenego.Obidos.server.actions.UserManagementActions;
import com.spenego.Obidos.server.model.Audit;
import com.spenego.Obidos.server.model.Model;
import com.spenego.Obidos.server.model.SystemConfig;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.AuditOperations;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.security.Authenticator;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuditDTO;
import com.spenego.Obidos.shared.dto.AuditReport;
import com.spenego.Obidos.shared.dto.AuditResult;
import com.spenego.Obidos.shared.dto.DateRange;
import com.spenego.Obidos.shared.dto.HasAuditInfo;
import com.spenego.Obidos.shared.dto.LicenseKeyDTO;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;

public final class AuditActionsImpl extends ObidosActions<Audit> implements AuditActions, Runnable {
	private static final Logger logger = LoggerFactory.getLogger(AuditActionsImpl.class);

	@Autowired private final AuditOperations		auditOperations = null;
	@Autowired private final Authenticator			authenticator = null;
	@Autowired private final UserManagementActions	userManagementActions = null;
	@Autowired private final LoginActions			loginActions = null;

	private final Thread auditerThread = getThreadFactory("Auditor").newThread(this);

	@Override
	protected final String messageType() { return "Audit"; }

	@Override
	protected final Logger getLogger() { return logger; }

	protected final Operations<Audit> getOperations() {
		return auditOperations;
	}

	protected final Integer getAuditDeleteAction() {
		return Audit.EXCEPTION;
	}

	@PostConstruct
	@Override
	protected final void init() {
		logger.info(() -> "Starting Auditor Thread");
		auditerThread.start();
		initialized = true;
	}

	private Long lookupUserId(final String username) {
		try {
			return userOperations.getUserByUsername(username).getId();
		} catch(final Throwable t) {
			return null;
		}
	}

	private String displayName(final String username, final Long id) {
		final Long userId = id == null ? lookupUserId(username) : id;
		return userId == null ? username : getUser(userId).getUsername();
	}

	private StringBuilder getAction(final HasAuditInfo a, final StringBuilder sb, final UnaryOperator<String> replace, final boolean isAdmin, final Long callerId) {
		if (!isAdmin && a.getUserId() != null && !a.getUserId().equals(callerId)) {
			sb.append(getUser(a.getUserId())).append(' ');
		}
		return sb.append(replace.apply(a.getActionStr())).append(' ');
	}

	private StringBuilder getAction(final HasAuditInfo a, final StringBuilder sb, final boolean isAdmin, final Long callerId) {
		return getAction(a, sb, s -> s, isAdmin, callerId);
	}

	private String getGroupName(final Long groupId) {
		try {
			return groupId == null ? "Unspecified" : getGroup(groupId).getName();
		} catch(final Throwable t) {
			return groupId.toString();
		}
	}

	private static String populateItem(final String format, final String itemName) {
		return format.replace("%i", itemName);
	}

	private static String populateUser(final String format, final String name) {
		return name == null ? name : format.replace("%u", name);
	}

	private static String populateContainer(final String format, final String containerName) {
		return format.replace("%c", containerName);
	}

	/**
	 * Each audit type needs to be treated differently since the meanings of
	 * each column are slightly different. Only a handful of Audit types are
	 * sent to syslog.
	 *
	 * @param a
	 * @return
	 */
	private StringBuilder buildAuditSyslogMessage(final Audit a) {
		switch (a.getAction()) {
		case META_EXCEPTION:
		case EXCEPTION:				return new StringBuilder(128).append(a.getDetails());
		case FAILED_LOGIN_ATTEMPT:	return getAction(a, new StringBuilder(128), true, null).append(" from ").append(a.getObjectName()).append(a.getDetails());
		case LOGIN:
		case LOGOUT:				return getAction(a, new StringBuilder(128), true, null).append(" from ").append(a.getObjectName());
		default:					return null;
		}
	}

	private boolean auditingEnabledInLicense() {
		return isTrue(loginActions.currentLicenseStats().getSupportsAudit());
	}

	private void processAuditRecord(final Supplier<Audit> supplier) {
		if (auditingEnabledInLicense()) {
			final Audit a = supplier.get();
			auditOperations.create(a);
			final StringBuilder sb = buildAuditSyslogMessage(a);
			if (sb != null) {
				logger.syslogInfo(sb::toString);
			}
			a.clear();
		}
	}

	@Override
	public final void queueAuditAction(final Supplier<Audit> supplier) {
		execute(() -> processAuditRecord(supplier));
	}

	@Override
	public Audit createAuditEntry(final Integer action, final Long userId, final Object object, final Long objectId, final Long newObjectId, final String details) {
		return new Audit(action, userOperations.get(userId), object, objectId, newObjectId, details);
	}

	@Override
	public Audit createAuditEntry(final Integer action, final Long userId, final Object object, final Long objectId, final Long newObjectId) {
		return new Audit(action, userOperations.get(userId), object, objectId, newObjectId, null);
	}

	@Override
	public Audit createAuditEntry(final Integer action, final Long userId, final Object object, final Long objectId) {
		return new Audit(action, userOperations.get(userId), object, objectId, null, null);
	}

	@Override
	public Audit createAuditEntry(final Integer action, final Long userId, final Long objectId, final Long recipientId, final Object object) {
		return new Audit(action, userOperations.get(userId), objectId, userOperations.get(recipientId), object);
	}

	@Override
	public Audit exception(final String details) {
		return new Audit(EXCEPTION, null, NULLSTRING, null, null, details);
	}

	private String itemName(final Long id) {
		try {
			return itemOperations.get(id).getName();
		} catch(final Exception ex) {
			return "-- deleted item --";
		}
	}

	private String groupName(final Long id) {
		try {
			return groupOperations.get(id).getName();
		} catch(final Exception ex) {
			return "-- deleted group --";
		}
	}

	private String containerName(final Long id) {
		try {
			return getContainerName(id);
		} catch(final Exception ex) {
			return "-- deleted container --";
		}
	}

	private String containerShareDetails(final int shareType, final Long recipientId) {
		return (shareType == SHARE_CONTAINER_WITH_GROUP || shareType == REVOKE_CONTAINER_FROM_GROUP) ? groupName(recipientId) : null;
	}

	@Override
	public Audit createContainerShareAuditEntry(final int shareType, final Long userId, final Long recipientId, final Long containerId) {
		return new Audit(shareType, userOperations.get(userId), containerId, userOperations.get(recipientId), containerName(containerId), containerShareDetails(shareType, recipientId));
	}

	@Override
	public Audit createItemShareAuditEntry(final int shareType, final Long userId, final Long recipientId, final Long itemId) {
		return new Audit(shareType, userOperations.get(userId), itemId, userOperations.get(recipientId), itemName(itemId), null);
	}

	@Override
	public Audit createItemRelinquishAuditEntry(Long userId, Long recipientId, String itemName, Long itemId) {
		return new Audit(ITEM_RELINQUISHED, userOperations.get(userId), itemId, userOperations.get(recipientId), itemName, null);
	}

	@Override
	public Audit createRevokeAuditEntry(final Long userId, final Long recipientId, final Model item) {
		return new Audit(REVOKE_ITEM, userOperations.get(userId), item.getId(), userOperations.get(recipientId), item);
	}

	@Override
	public Audit createGroupRevokeAuditEntry(final Long userId, final Long groupId, final Model item) {
		return new Audit(REVOKE_ITEM_FROM_GROUP, userOperations.get(userId), item.getId(), groupOperations.get(groupId), item);
	}

	@Override
	public Audit createItemOwnedAuditEntry(final Long userId, final Long previousOwnerId, final Long itemId) {
		return new Audit(ITEM_OWNERSHIP_TRANSFER, userOperations.get(userId), itemId, userOperations.get(previousOwnerId), itemName(itemId), null);
	}

	@Override
	public Audit createContainerOwnedAuditEntry(final Long userId, final Long previousOwnerId, final Long containerId) {
		return new Audit(CONTAINER_OWNERSHIP_TRANSFER, userOperations.get(userId), containerId, userOperations.get(previousOwnerId), containerName(containerId), null);
	}

	private StringBuilder createRecipientMessage(final StringBuilder sb, final boolean isAdmin, final Long callerId, final AuditDTO a, final String s) {
		getAction(a, sb, isAdmin, callerId).append(a.getObjectName()).append(s);

		if (isAdmin || !callerId.equals(a.getRecipientId())) {
			if (a.getAction().equals(REVOKE_ITEM_FROM_GROUP)) {
				sb.append(" group ").append(getGroup(a.getRecipientId()));
			} else {
				sb.append(" user ").append(a.getRecipientFullname());
			}
		} else {
			sb.append(" you.");
		}

		return sb;
	}

	private StringBuilder genFromString(final StringBuilder sb, final boolean isAdmin, final Long callerId, final AuditDTO a) {
		return getAction(a, sb, isAdmin, callerId).append("from ").append(a.getObjectName());
	}

	private StringBuilder generateMessage(final StringBuilder sb, final boolean isAdmin, final Long callerId, final AuditDTO a) {
		switch(a.getAction()) {
		case EXCEPTION:
		case META_EXCEPTION:					return sb.append(a.getDetails());
		case PASS_RESET_EXCEPTION:				return getAction(a,sb, isAdmin, callerId).append("for email address ").append(a.getObjectName()).append(a.getDetails());
		case SUBVERSIVE_ITEM_VIEW:
		case SUBVERSIVE_CONTAINER_VIEW:			return getAction(a,sb, isAdmin, callerId).append(":" + a.getDetails());

		case LOGIN:								return genFromString(sb, isAdmin, callerId, a).append(", login number ").append(a.getObjectId());
		case LOGOUT:							return genFromString(sb, isAdmin, callerId, a);
		case FAILED_LOGIN_ATTEMPT:				return genFromString(sb, isAdmin, callerId, a).append(a.getDetails());
		case CREATE_USER:						return getAction(a,sb, isAdmin, callerId).append(a.getDetails()).append(' ').append(a.getObjectName());

		case Audit.DELETE_CONTAINER:
		case Audit.CREATE_CONTAINER:				return getAction(a,sb, isAdmin, callerId).append(a.getObjectName());
		case Audit.ADDED_USER_TO_GROUP:
		case Audit.REMOVED_USER_FROM_GROUP:			return getAction(a,sb, s -> populateUser(s, a.getRecipientFullname()), isAdmin, callerId).append(' ').append(getGroupName(a.getObjectId()));
		case Audit.CREATE_USER_DEFINED_FIELD:		return getAction(a,sb, isAdmin, callerId).append(a.getObjectName()).append(" for template: ").append(a.getDetails());
		case Audit.RENAMED_ITEM:					return getAction(a,sb, s -> populateItem(s, a.getObjectName()), isAdmin, callerId).append(' ').append(a.getDetails());
		case Audit.UPDATE_CONTAINER:				return getAction(a,sb, isAdmin, callerId).append(a.getObjectName()).append(a.getObjectName().equals(a.getDetails()) ? "" : " renamed to " + a.getDetails());
		case Audit.CREATE_LDAP:
		case Audit.CREATE_SMTP_CONFIG:
		case Audit.CREATE_PASSWORD_RESET:
		case Audit.CREATE_ITEM:
		case Audit.CREATE_USER_DEFINED_TYPE:
		case Audit.CREATE_USER_DEFINED_FIELD_VALUE:
		case Audit.UPDATE_GROUP:
		case Audit.USER_LOCKED:
		case Audit.DELETE_ITEM:
		case Audit.USER_UNLOCKED:
		case Audit.CREATE_GROUP:
		case Audit.VIEW_ITEM:
		case Audit.UPDATE_ITEM:
		case Audit.CONTAINER_RELINQUISHED:
		case Audit.USER_RESTORED:
		case Audit.UPDATE_LDAP:						return getAction(a,sb, isAdmin, callerId).append(a.getObjectName());
		case Audit.UPDATE_USER:						return getAction(a,sb, isAdmin, callerId).append("attributes ").append(a.getDetails()).append(" of ").append(displayName(a.getObjectName(), a.getObjectId()));
		case Audit.USER_TOMBSTONED:					return getAction(a,sb, isAdmin, callerId).append(displayName(a.getObjectName(), a.getObjectId()));
		case Audit.UPDATE_SMTP_CONFIG:				return getAction(a,sb, isAdmin, callerId).append(a.getObjectName()).append(" (").append(a.getObjectId()).append(')');
		case Audit.UPDATE_CAPABILITY:				return getAction(a,sb, isAdmin, callerId).append(displayName(a.getObjectName(), a.getObjectId())).append(": ").append(a.getDetails());
		case Audit.REVOKE_CONTAINER_FROM_USER:
		case Audit.SHARE_CONTAINER_WITH_USER:		return getAction(a,sb, s -> populateContainer(s, a.getObjectName()), isAdmin, callerId).append(a.getRecipientFullname());
		case Audit.SHARE_ITEM:						return createRecipientMessage(sb, isAdmin, callerId, a, " with");
		case Audit.REVOKE_ITEM:
		case Audit.REVOKE_ITEM_FROM_GROUP:
		case Audit.CONTAINER_OWNERSHIP_TRANSFER:
		case Audit.ITEM_OWNERSHIP_TRANSFER:			return createRecipientMessage(sb, isAdmin, callerId, a, " from");
		case Audit.ITEM_RELINQUISHED:				return createRecipientMessage(sb, isAdmin, callerId, a, " to");
		case Audit.REVOKE_CONTAINER_FROM_GROUP:
		case Audit.SHARE_CONTAINER_WITH_GROUP:		return getAction(a,sb, s -> populateContainer(s, a.getObjectName()), isAdmin, callerId).append(a.getDetails());
		case Audit.LICENSE_INSTALLED:				return getAction(a,sb, isAdmin, callerId).append("registered to ").append(a.getObjectName()).append(", ").append(a.getDetails());
		case Audit.CERTIFICATE_INSTALLED:			return getAction(a,sb, isAdmin, callerId).append(a.getObjectName()).append(", ").append(a.getDetails());
		case Audit.LICENSE_LIMIT_REACHED:
		case Audit.PASSWORD_EXPIRED:
		case Audit.PASSWORD_RESET_REQUEST:
		case Audit.SENT_PASSPHRASE_RESET_EMAIL:
		case Audit.SENT_PASSWORD_RESET_EMAIL:
		case Audit.PASSPHRASE_RESET_REQUEST:
		case Audit.UPDATE_SYSTEM_CONFIG:
		case Audit.PASSWORD_RESET_IGNORED:
		case Audit.PASSWORD_RESET_REQUEST_IGNORED:
		case Audit.ITEM_GROUP_EXCEPTION:			return getAction(a, sb, isAdmin, callerId).append(a.getDetails());
		case Audit.UPDATE_PASSWORD_RESET:
		case Audit.UPDATE_USER_DEFINED_TYPE:
		case Audit.UPDATE_USER_DEFINED_FIELD:
		case Audit.UPDATE_USER_DEFINED_FIELD_VALUE:
		case Audit.DELETE_GROUP:
		case Audit.DELETE_LDAP:
		case Audit.DELETE_USER:
		case Audit.DELETE_SMTP_CONFIG:
		case Audit.DELETE_CAPABILITY:
		case Audit.DELETE_PASSWORD_RESET:
		case Audit.DELETE_USER_DEFINED_TYPE:
		case Audit.DELETE_USER_DEFINED_FIELD:
		case Audit.DELETE_USER_DEFINED_FIELD_VALUE:
		case Audit.DELETE_USER_DEFINED_TYPE_VALUE:
		case Audit.DELETE_NOTIFICATION:
		case Audit.TWO_FACTOR_CODE_NOT_SUPPLIED:
		case Audit.TWO_FACTOR_NOT_ENABLED:
		case Audit.SENT_WARNING_EMAIL:
			default: return getAction(a, sb, isAdmin, callerId).append(": action code ").append(a.getAction());
		}
	}

	private void fillAuditDTO(final StringBuilder sb, final boolean isAdmin, final Long callerId, final AuditDTO dto) {
		sb.setLength(0);
		if (dto.getRecipientId() != null && dto.getRecipientFullname() == null) {
			try {
				dto.setRecipientFullname(getUser(dto.getRecipientId()).getFullname());
			} catch(final Exception ex) {
				dto.setRecipientFullname("USER NO LONGER EXISTS");
			}
		}

		try {
			dto.setMessage(generateMessage(sb, isAdmin, callerId, dto).toString());
		} catch(final Exception ex) {
			dto.setMessage("Caught exception attempting to generate message: " + ex.getMessage());
		}
	}

	/**
	 * The audit table compacts entries by placing different types of entries into shared columns. In particular, the actual types of object_id and new item_id
	 * depend upon the action.
	 *
	 * @param result
	 * @return
	 */
	private AuditResult denormalizeResult(final User caller, final AuditResult result) {
		if (!result.noResults()) {
			final StringBuilder sb = new StringBuilder(128);
			final boolean isAdmin = caller.isAdmin();
			result.getElements().forEach(audit -> fillAuditDTO(sb, isAdmin, caller.getId(), audit));
		}
		return result;
	}

	private static Collection<Integer> convertActions(final Collection<String> actions) {
		final Collection<Integer> actionList = new ArrayList<>();

		actions.forEach(action -> {
			final Collection<Integer> col = Audit.getIntendedActions(action.toLowerCase());
			if (col != null && !col.isEmpty()) {
				actionList.addAll(col);
			}});

		return actionList.isEmpty() ? null : actionList;
	}

	private static Collection<Long> nonEmptyCollection(final Collection<Long> c) {
		return c.isEmpty() ? asList(1L) : c;
	}

	private Collection<Long> getUserIds(final Collection<String> usernames) {
		final Collection<Long> collection = usernames == null || usernames.isEmpty() ? null : userOperations.getUserIds(usernames);
		return (collection == null) ? null : nonEmptyCollection(collection);
	}

	private void auditingLicenseCheck() {
		if (!auditingEnabledInLicense()) {
			throw new PermissionDeniedException("Auditing is not enabled in the current License.");
		}
	}

	@Override
	public AuditResult getAuditRecords(final User caller, final Collection<String> actionNames, final Collection<String> usernames, final Collection<String> objects, final DateRange dateRange, final Integer offset, final Integer count, final List<OrderBy> orderBy) {
		auditingLicenseCheck();

		final Thread currentThread = Thread.currentThread();
		int priority = currentThread.getPriority();

		try {
			currentThread.setPriority((priority + MIN_PRIORITY) / 2); // this operation can take a long time
			final Collection<Long> userIds = caller.isAdmin() ? getUserIds(usernames) : asList(caller.getId());
			final Collection<Integer> actions = actionNames == null ? null : convertActions(actionNames);

			return denormalizeResult(caller, new AuditResult(offset, count, null, (a,b) -> convert(a,b),
					() -> auditOperations.getCount(caller, actions, userIds, objects, dateRange),
					() -> auditOperations.getStream(caller, actions, userIds, objects, dateRange, offset, count, orderBy)));
		} finally {
			currentThread.setPriority(priority);
		}
	}

	@Override
	public AuditReport getAuditReport(final User admin) {
		auditingLicenseCheck();

		final AuditReport report = new AuditReport();
		final User template = new User();
		final SystemConfig sc = getSystemConfig();

		report.setFqdn(sc.getFqdn());
		final LicenseKeyDTO license = validateLicense(sc.getLicense(), sc.getLicensePublicKey());
		report.setCompanyName(license.getCompanyName());
		report.setCustomerId(license.getCustomerId());
		report.setLicenseType(license.getLicenseType());
		report.setTotalLicensedUsers(license.getMaxUsers());
		report.setNumberOfUsersInLicense(license.getMaxUsers());
		report.setLicenseExpirationEpoch(license.getExpirationEpoch());
		report.setDocumentStorageDirectory(sc.getDocumentStorageDirectory());
		report.setTotalAdmins(userOperations.getAdminCount(admin.getId(), null, null, null));
		report.setTotalUsers(userManagementActions.getTotalUserCount(admin));
		report.setTotalTombstonedUsers(userOperations.getCount(admin.getId(), null, true, null));
		template.setLocked(true);
		report.setTotalLockedUsers(userOperations.getCount(admin.getId(), template, null));
		report.setTotalItemAssignments(itemAssignmentOperations.getItemShareCount(null));
		report.setTotalItems(itemOperations.getUsersItemsCount(null, null, null, null, null, null));
		template.setTwoFAPasswordResetEnabled(true);
		report.setTotalUsersUsing2FAPasswordReset(userOperations.getCount(admin.getId(), template, null));
		report.setTotalAuditRecords(auditOperations.getCount(admin, null, null, null, null));

		final Collection<User> users = authenticator.getActiveUsers();
		int totalActiveUsersInLastFifteenMinutes = 0;
		int totalActiveUsersInLastHour = 0;
		int totalActiveUsersInLastMonth = 0;
		final long now = System.currentTimeMillis();
		final long fifteenMinutesAgo = now - 1000 * 60 * 15;
		final long oneHourAgo = now - 1000 * 60 * 15;

		for(final User u:users) {
			if (u.getLastActivityTime() > oneHourAgo) {
				totalActiveUsersInLastHour++;
				if (u.getLastActivityTime() > fifteenMinutesAgo) {
					totalActiveUsersInLastFifteenMinutes++;
				}
			}
		}

		final Date lastMonth = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

		for(final User u:users) {
			if (u.getLastLogin().after(lastMonth)) {
				totalActiveUsersInLastMonth++;
			}
		}

		report.setAverageNumberOfDailyUsersOverLastMonth(totalActiveUsersInLastMonth);
		report.setTotalActiveUsersInLastFifteenMinutes(totalActiveUsersInLastFifteenMinutes);
		report.setTotalActiveUsersInLastHour(totalActiveUsersInLastHour);

		return report;
	}
}
