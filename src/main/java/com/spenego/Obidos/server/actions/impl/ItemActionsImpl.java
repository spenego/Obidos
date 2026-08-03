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

import static com.spenego.Obidos.server.model.Audit.CREATE_ITEM;
import static com.spenego.Obidos.server.model.Audit.DELETE_ITEM;
import static com.spenego.Obidos.server.model.Audit.ITEM_GROUP_EXCEPTION;
import static com.spenego.Obidos.server.model.Audit.RENAMED_ITEM;
import static com.spenego.Obidos.server.model.Audit.SUBVERSIVE_ITEM_VIEW;
import static com.spenego.Obidos.server.model.Audit.UPDATE_ITEM;
import static com.spenego.Obidos.server.model.Audit.VIEW_ITEM;
import static com.spenego.Obidos.server.utils.NotificationEngine.NotificationType.REVOKE_ITEM;
import static com.spenego.Obidos.shared.dto.ContainerDTO.NOTEBOOK_ID;
import static com.spenego.Obidos.shared.dto.NotificationDTO.ITEM_DELETED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.ITEM_DEPOSITED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.ITEM_OWNED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.ITEM_PERMISSION_GRANTED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.ITEM_RELINQUISHED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.ITEM_RENAMED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.ITEM_REVOKED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.ITEM_SHARED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.ITEM_UPDATED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.NOTE_DELETED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.NOTE_PERMISSION_GRANTED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.NOTE_RELINQUISHED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.NOTE_RENAMED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.NOTE_REVOKED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.NOTE_SHARED;
import static com.spenego.Obidos.shared.dto.NotificationDTO.NOTE_UPDATED;
import static com.spenego.Obidos.shared.dto.UserDefinedFieldDTO.TYPE_NOTES_FIELD_ID;
import static com.spenego.Obidos.shared.dto.UserDefinedTypeDTO.NOTES_ID;
import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import com.spenego.Obidos.server.actions.ContainerActions;
import com.spenego.Obidos.server.actions.DocumentActions;
import com.spenego.Obidos.server.actions.EmailActions;
import com.spenego.Obidos.server.actions.ItemActions;
import com.spenego.Obidos.server.actions.LoginActions;
import com.spenego.Obidos.server.actions.SMSActions;
import com.spenego.Obidos.server.actions.UserDefinedTypeActions;
import com.spenego.Obidos.server.actions.UserDefinedTypeValueActions;
import com.spenego.Obidos.server.model.Audit;
import com.spenego.Obidos.server.model.BaseModel;
import com.spenego.Obidos.server.model.Container;
import com.spenego.Obidos.server.model.ContainerAssignment;
import com.spenego.Obidos.server.model.Document;
import com.spenego.Obidos.server.model.Item;
import com.spenego.Obidos.server.model.ItemAssignment;
import com.spenego.Obidos.server.model.ItemGroup;
import com.spenego.Obidos.server.model.LimitedItem;
import com.spenego.Obidos.server.model.LimitedUser;
import com.spenego.Obidos.server.model.SharedItem;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.model.UserDefinedType;
import com.spenego.Obidos.server.operations.ContainerGroupAssignmentOperations;
import com.spenego.Obidos.server.operations.ItemGroupOperations;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.operations.UserDefinedFieldValueOperations;
import com.spenego.Obidos.server.operations.UserDefinedTypeOperations;
import com.spenego.Obidos.server.operations.UserDefinedTypeValueOperations;
import com.spenego.Obidos.server.security.Encryption.PublicKeyDecryptor;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ObidosExecutor;
import com.spenego.Obidos.server.utils.StreamSupplier;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.dto.ItemExpiration;
import com.spenego.Obidos.shared.dto.ItemGroupsResult;
import com.spenego.Obidos.shared.dto.ItemsResult;
import com.spenego.Obidos.shared.dto.LimitedItemDTO;
import com.spenego.Obidos.shared.dto.NotesResult;
import com.spenego.Obidos.shared.dto.NotificationDTO;
import com.spenego.Obidos.shared.dto.ObidosResult;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.dto.SecurityClassificationDTO;
import com.spenego.Obidos.shared.dto.SharedItemDTO;
import com.spenego.Obidos.shared.dto.SharedItemsResult;
import com.spenego.Obidos.shared.dto.SharedNotesResult;
import com.spenego.Obidos.shared.dto.UserDefinedFieldDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldValueDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeValueDTO;
import com.spenego.Obidos.shared.exceptions.ItemOwnershipException;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;
import com.spenego.Obidos.shared.exceptions.SharingProhibitedException;


/**
 * The Obidos business logic layer for dealing with the fundamental unit in Obidos; Items.
 *
 *
 * @author	Mike Morgan
 * @see		com.spenego.Obidos.server.actions.impl.CryptoActions
 * @since	Obidos1.0
 *
 */
public final class ItemActionsImpl extends CryptoActions<Item> implements ItemActions {
	private static final Logger logger = LoggerFactory.getLogger(ItemActionsImpl.class);

	@Autowired private final SMSActions							smsActions = null;
	@Autowired private final EmailActions						emailActions = null;
	@Autowired private final LoginActions						loginActions = null;
	@Autowired private final ContainerActions					containerActions = null;
	@Autowired private final DocumentActions					documentActions = null;
	@Autowired private final ItemGroupOperations				itemGroupOperations = null;
	@Autowired private final UserDefinedTypeActions				userDefinedTypeActions = null;
	@Autowired private final UserDefinedTypeOperations			userDefinedTypeOperations = null;
	@Autowired private final UserDefinedTypeValueActions		userDefinedTypeValueActions = null;
	@Autowired private final UserDefinedTypeValueOperations		userDefinedTypeValueOperations = null;
	@Autowired private final UserDefinedFieldValueOperations	userDefinedFieldValueOperations = null;
	@Autowired private final ContainerGroupAssignmentOperations	containerGroupAssignmentOperations = null;

	public ItemActionsImpl()									{ super(null, null); }

	@Override protected final Logger			getLogger()				{ return logger; }
	@Override protected final Operations<Item>	getOperations()			{ return itemOperations; }
	@Override protected final Integer			getAuditDeleteAction()	{ return DELETE_ITEM; }
	@Override protected final String			elementName()			{ return "item"; }

	private Boolean sharingIsPermitted(final Long containerAssignmentId) { return !containerActions.containerIsPrivate(containerAssignmentId); }

	private ContainerAssignment getCallerContainerAssignment(final User caller, final Long containerAssignmentId) {
		if (containerAssignmentId == null)	{ throw new ServerSideException("You must specify a destination container."); }

		try {
			return getContainerAssignment(getContainer(getContainerAssignment(containerAssignmentId).getContainerId()).getId(), caller.getId());
		} catch(final NoSuchRecordException ex) {
			throw new PermissionDeniedException("You do not own this container");
		}
	}

	private Long getContainerId(final Long containerAssignmentId) {
		return (containerAssignmentId != null) ? getContainerAssignment(containerAssignmentId).getContainerId() : null;
	}

	private Stream<ContainerAssignment> getContainerAssignments(final Long containerId, final User user) {
		return containerAssignmentOperations.getAll(containerId).filter(ca -> !user.self(ca.getUserId()));
	}

	@Override
	public ItemAssignment	getItemAssignment(final Long itemId, final Long userId)			{ return itemAssignmentOperations.get(itemId, userId); }
	private Long			getItemAssignmentId(final Long itemId, final Long userId)		{ return getItemAssignment(itemId, userId).getId(); }
	private int				getItemAssignmentCount(final Long itemId, final Long userId)	{ return itemAssignmentOperations.count(itemId, userId); }

	private final class UserRefCount {
		int count;
		UserRefCount() {
			this.count = 1;
		}
	}

	private synchronized Void addUserToMap(final Map<Long, UserRefCount> users, final Long userId) {
		final UserRefCount existing = users.get(userId);

		if (existing != null) {
			++existing.count;
		} else {
			users.put(userId, new UserRefCount());
		}
		return null;
	}

	private static boolean caRefCountMatches(final Map<Long, UserRefCount> users, final ContainerAssignment ca) {
		final UserRefCount urc = users.get(ca.getUserId());
		return urc != null && urc.count == ca.getCount().intValue();
	}

	@Override
	public Supplier<Stream<UserDefinedTypeValueDTO>> createUDTValueListSupplier(final User caller, final Long itemId, final Supplier<PublicKeyDecryptor> pkdSupplier) {
		return StreamSupplier.create(() -> getUDTValueList(caller, itemId, pkdSupplier, null));
	}

	private Supplier<Stream<UserDefinedTypeValueDTO>> createUDTValueListSupplier(final User caller, final Long itemId, final PassphraseHash passphraseHash) {
		return createUDTValueListSupplier(caller, itemId, getPKDSupplier(caller, passphraseHash));
	}

	/**
	 * An item can be shared both via group and individually. Either type of share creates a container_assignment record. There is a counter on this record to handle
	 * situations where an item is shared multiple times (either by a user being in multiple shared groups, or if the item was shared via group and individually).
	 * When sharing the item, we adjust for group shares and individual shares. Since the container assignments table contains an aggregate of individual and group shares (items
	 * shared via group create regular container_assignment records), we need to know if we should, or should not, call shareItemWithUsers again. If an item is shared just via
	 * a group for each user, we should not invoke shareItemWithUsers since the count has already been adjusted. However, an item can be both shared via group AND shared
	 * individually.
	 * We create a reference count list (userRefCounts) so that we can filter the users out the list of recipients that have already had the item shared appropriately.
	 *
	 * Not taking this into account caused Bug#217.
	 * @param caller
	 * @param itemId
	 * @param itemAssignmentId
	 * @param groupIdSupplier
	 * @param passphraseHash
	 * @param shareComment
	 * @param containerId
	 */
	private void shareItemWithUsersOfContainer(final User caller, final Long itemId, final Long containerId, final Supplier<Stream<Long>> groupIdSupplier, final Supplier<Stream<UserDefinedTypeValueDTO>> valueSupplier, final String shareComment, final PostOpActions postOpActions) {
		final Map<Long, UserRefCount> userRefCounts = new HashMap<>(32);
		shareItemWithGroupsInternal(caller, itemId, valueSupplier, groupIdSupplier, u -> addUserToMap(userRefCounts, u.getId()), Boolean.FALSE, shareComment, postOpActions);
		notifyShareRecipients(caller, () -> userRefCounts.keySet().stream(), itemId, shareComment, postOpActions);

		// We can now do this more efficiently by simply getting list of users that the container was shared with.  That list of users now ONLY contains users with whom the
		// container what shared explicitly.  See Bug #750
		final Supplier<Stream<Long>> recipientSupplier = StreamSupplier.create(() -> getContainerAssignments(containerId, caller).filter(ca -> !caRefCountMatches(userRefCounts, ca)).map(BaseModel::getUserId).collect(toList()));
		shareItemWithUsers(caller, itemId, valueSupplier, postOpActions, recipientSupplier, shareComment, Boolean.TRUE);
	}

	private void createItemGroup(final Long itemId, final Long groupId, final Boolean sharedExplicitly) {
		try {
			itemGroupOperations.create(new ItemGroup(itemId, groupId, sharedExplicitly));
		} catch(final DuplicateKeyException ex) { /* some other process already created the mapping for us */ }
	}

	private Long createItemAssignment(final Long itemId, final Long userId, final Boolean isOwner, final Boolean sharedExplicitly) {
		return itemAssignmentOperations.create(new ItemAssignment(itemId, userId, isOwner, sharedExplicitly));
	}

	// Search for any notes and sanitize data
	private List<UserDefinedTypeValueDTO> sanitizeNotes(final List<UserDefinedTypeValueDTO> values, final boolean update) {
		for(final UserDefinedTypeValueDTO value:values) {
			// We need to sanitize the input data when accepting a Note type. However, we can not let the client specify the type on an update
			// since it may be trying to trick us and avoid being sanitized. During a create, the type must be specified, so we just use that.
			final long typeId = update ? userDefinedTypeValueOperations.get(value.getId()).getUserDefinedTypeId() : value.getUserDefinedTypeId();
			if (typeId == NOTES_ID) {
				final UserDefinedFieldValueDTO field = value.getFieldValues().get(0);
				field.setBlobValue(sanitize(field.getBlobValue()));
			}
		}
		return values;
	}

	/**
	 * Returns a container that is guaranteed to be owned by the caller.
	 */
	private Container getOwnedContainer(final User caller, final Long containerAssignmentId) {
		final Container c = getContainer(getCallerContainerAssignment(caller, containerAssignmentId).getContainerId());
		if (!containerIsNotebook(c.getId()) && !caller.self(c.getUserId())) { throw new PermissionDeniedException("You do not own this container."); }
		return c;
	}

	private Collection<Long> getGroupIdsSharingContainer(final Long containerId) {
		return groupOperations.getGroupIdsSharingContainer(containerId);
	}

	/**
	 * If we are eventually able to make multi-threaded calls to DB operations, you'll need to return a StreamSupplier here.
	 */
	private Supplier<Stream<Long>> getGroupIdSupplier(final Long containerId) {
		return () -> getGroupIdsSharingContainer(containerId).stream();
	}

	private boolean licensePermitsQRCodeValues() {
		return loginActions.currentLicenseStats().getSupportQRCodeUpload();
	}

	private void newItemChecks(final User caller, final String name, final Long containerAssignmentId, final SecurityClassificationDTO securityClassificationDTO, final List<UserDefinedTypeValueDTO> values) {
		if (name == null)						{ throw new ServerSideException("You must specify a name for this item."); }
		if (containerAssignmentId == null)		{ throw new ServerSideException("You must specify a container for this item."); }
		if (values == null || values.isEmpty()) { throw new ServerSideException("You must specify a list of values for this item."); }
		if (!caller.hasSufficientSecurityClearance(securityClassificationDTO)) { throw new ServerSideException("You may not create items whose security classification exceeds your security clearance."); }
		if (!licensePermitsQRCodeValues() && ItemDTO.containsQRCode(values)) { throw new ServerSideException("Your license does not permit values of type QR Code.");}
	}

	private static void setItemExpiration(final ItemExpiration itemExpiration) {
		// set exact date client has specified, now date and time can be
		// specified. -- spgdev, Jun-4-2010
		logger.info(() -> "Item expires at: " + itemExpiration.getExpiresAt().toString());
		itemExpiration.setExpiresAt(itemExpiration.getExpiresAt());
	}

	/**
	 * This method grants the caller permission to Update/Take Ownership of the Item.
	 * This does not make sense if the caller owns the Item since those permissions are given by default; this
	 * method is called when the caller does not own the Container that the Item was placed into.  Putting an
	 * item into a shared container owned by another user results in the Item being owned by the owner of the
	 * container.  Since the caller created the Item, we bestow them Update/Take Ownership on the Item. This
	 * allows the caller to make subsequent updates on the Item and even delete it (by first taking ownership).
	 * @param caller
	 * @param itemId
	 */
	private void grantCallerItemPermission(final User caller, final Long itemId) {
		final ArrayList<Long> callerList =  new ArrayList<>(1);
		callerList.add(caller.getId());
		itemAssignmentOperations.grantPermissions(itemId, new PermissionDTO(true, true), callerList);
	}

	// Throws an exception if the user is not permitted to add Items to the specified container.
	private void mayAddItemToContainer(final User caller, final ContainerAssignment ca) {
		if (isFalse(ca.getAddPermitted()) && !containerGroupAssignmentOperations.mayAddItems(caller.getId(), ca.getContainerId())) {
			throw new PermissionDeniedException("You do not have permission to add items to this container (Adds to container are neither granted explicitly nor via any groups).");
		}
	}

	@Override
	public ItemDTO create(final User caller, final String name, final ItemExpiration itemExpiration, final Long containerAssignmentId, final PassphraseHash passphraseHash, final PostOpActions postOpActions, final Boolean shareable, final SecurityClassificationDTO securityClassificationDTO, final List<UserDefinedTypeValueDTO> values) {
		newItemChecks(caller, name, containerAssignmentId, securityClassificationDTO, values);
		final ContainerAssignment ca = getCallerContainerAssignment(caller, containerAssignmentId);
		final Container container    = getContainer(ca.getContainerId());
		final Long containerOwnerId  = container.getUserId();
		final boolean containerIsNotebook = containerIsNotebook(container.getId());
		final boolean callerOwnsContainer = !containerIsNotebook && caller.self(containerOwnerId);
		final boolean sharedContainerDeposit = !containerIsNotebook && !callerOwnsContainer; // caller is attempting to put Item into Container not owned by them
		final Long realContainerAssignmentId = sharedContainerDeposit ? getContainerAssignment(container.getId(), containerOwnerId).getId() : containerAssignmentId;

		if (sharedContainerDeposit) {
			logger.info(() -> caller + " is attempting to deposit an Item into a shared container " + container);
			mayAddItemToContainer(caller, ca);
			if (isFalse(shareable)) { throw new PermissionDeniedException("You may not create a private item within a shared container."); }
		}

		if (isTrue(shareable) && isTrue(container.getIsPrivate())) { throw new PermissionDeniedException("You may not create a shareable item within a private container."); }

		logger.info(() -> caller + " is creating item '" + name + "' in container '" + container + "'");
		if (itemExpiration != null && itemExpiration.getExpiresAt() != null) {
			setItemExpiration(itemExpiration);
		}

		final Long newItemOwnerId = sharedContainerDeposit ? containerOwnerId : caller.getId();
		final Long itemId = itemOperations.create(setExpire(new Item(newItemOwnerId, name, realContainerAssignmentId, (shareable == null || !shareable) ? Boolean.FALSE : sharingIsPermitted(containerAssignmentId)), itemExpiration));
		auditItem(CREATE_ITEM, caller.getId(), itemId);

		final Long itemAssignmentId = createItemAssignment(itemId, newItemOwnerId, Boolean.TRUE, Boolean.TRUE);
		final Supplier<Stream<UserDefinedTypeValueDTO>> valueSupplier = StreamSupplier.create(() -> sanitizeNotes(values, false));
		final User containerOwner = sharedContainerDeposit ? getUser(containerOwnerId) : caller;

		processStream(() -> containerOwner + " is creating item assignments for item " + itemId, valueSupplier, value -> userDefinedTypeValueActions.create(containerOwner, itemAssignmentId, value), () -> "creating type value");
		if (isTrue(container.getShared()) && isTrue(shareable)) {
			shareItemWithUsersOfContainer(containerOwner, itemId, container.getId(), getGroupIdSupplier(container.getId()), valueSupplier, "I created item: " + name, postOpActions);
		}

		if (sharedContainerDeposit) { // Caller does not own container, they were given Update permission on Container. We'll grant them Update on Item by default.
			grantCallerItemPermission(caller, itemId);
			postCommitQueue(() -> notifyRecipient(ITEM_DEPOSITED, caller, newItemOwnerId, realContainerAssignmentId, "container", name, " into your container " + container.getName(), null));
		}

		final Long iaId = sharedContainerDeposit ? getItemAssignmentId(itemId, caller.getId()) : itemAssignmentId;
		return get(caller, iaId, passphraseHash, null);
	}

	private static Integer getFieldPosition(final UserDefinedFieldDTO field) {
		final Integer position = field.getPosition();

		if (position == null) {
			throw new ServerSideException("Position omitted for item field " + field.getName());
		}
		return position;
	}

	private static void createUserDefinedFieldValues(final UserDefinedTypeValueDTO typeValue, final UserDefinedFieldDTO field) {
		final Integer fieldPosition = getFieldPosition(field);

		for(final UserDefinedFieldValueDTO v : typeValue.getFieldValues()) {
			final Integer position = v.getPosition();
			if (position == null) {
					throw new ServerSideException("Position omitted for item value");
			}
			if (position.equals(fieldPosition)) {
				v.setUserDefinedFieldId(field.getId());
				break;
			}
		}
	}

	private static void createUserDefinedTypeValues(final UserDefinedTypeDTO template, final Long typeId, final Collection<UserDefinedFieldDTO> fields, final UserDefinedTypeValueDTO v) {
		if (template.getFields().size() != v.getFieldValues().size()) { throw new ServerSideException("Not all fields have values."); }
		v.setUserDefinedTypeId(typeId);
		fields.forEach(field -> createUserDefinedFieldValues(v, field));
	}

	/**
	 * Ad-Hoc Item create.
	 */
	@Override
	public ItemDTO create(final User caller, final String name, final ItemExpiration itemExpiration, final Long containerAssignmentId, final PassphraseHash passphraseHash, final PostOpActions postOpActions, final Boolean sharable, final SecurityClassificationDTO securityClassificationDTO, final UserDefinedTypeDTO template, final List<UserDefinedTypeValueDTO> values) {
		template.setAdHoc(true);
		template.setPersonal(true);		// Should we always make ad-hoc items composed from personal templates?
		final Long typeId = userDefinedTypeActions.createAdHoc(caller, template, passphraseHash);
		final Collection<UserDefinedFieldDTO> fields = userDefinedTypeActions.get(caller, typeId).getFields();
		values.forEach(v -> createUserDefinedTypeValues(template, typeId, fields, v));
		final ItemDTO item = create(caller, name, itemExpiration, containerAssignmentId, passphraseHash, postOpActions, sharable, securityClassificationDTO, values);
		userDefinedTypeOperations.updateSelective(new UserDefinedType(typeId, getItemAssignment(item.getId(), caller.getId()).getItemId())); // allows cascade delete when Item is deleted, freeFormItemId field is not used otherwise
		return item;
	}

	private Collection<UserDefinedTypeValueDTO> getUDTValueList(final User user, final Long itemId, final Supplier<PublicKeyDecryptor> pkdSupplier, final List<OrderBy> orderBy) {
		return userDefinedTypeValueActions.getList(user, itemId, pkdSupplier, orderBy);
	}

	private static boolean itemHasExpired(final Item item) {
		final Date expireTime = item.getSharesExpireAt();

		return expireTime != null && expireTime.getTime() <= System.currentTimeMillis();
	}

	/**
	 * Most of the info in DocumentDTO objects does not need to be sent to the client. Unfortunately, we use DTOs
	 * for intra-action layer communication where other action methods (sharing) require this data.
	 */
	private static <T extends ItemDTO> T redact(T dto) {
		dto.getValues().forEach(t -> t.getFieldValues().forEach(v -> { if (v.getDocument() != null) { v.getDocument().redact();}}));
		return dto;
	}

	private <T extends ItemDTO> T get(final User caller, final Long itemAssignmentId, final PassphraseHash passphraseHash, final Class<T> clazz, final List<OrderBy> orderby, final LongFunction<Item> itemProvider) {
		final ItemAssignment ia = getItemAssignment(itemAssignmentId);

		ensureCallerOwnsAssignment(ia, caller, itemAssignmentId, SUBVERSIVE_ITEM_VIEW, "view item");

		final Long itemId = ia.getItemId();
		final Item item = itemProvider.apply(itemId);
		final boolean callerOwnsItem = item.ownerIs(caller);

		if (!callerOwnsItem && itemHasExpired(item)) {
			throw new ServerSideException("This item has passed its expiration date. It's stale.");
		}

		final T dto = convert(item, clazz);
		dto.setValues(getUDTValueList(caller, itemId, getPKDSupplier(caller, passphraseHash), orderby));
		dto.setPermissions(new PermissionDTO(ia.getUpdatePermitted(), ia.getOwnershipControl()));
		dto.ensureIdIsSet(itemId);
		dto.setId(itemId); // When loading items individually, the ItemID is really the item ID and not the assigned item ID

		if (!callerOwnsItem) {
			audit(VIEW_ITEM, caller.getUsername(), caller.getId(), item, itemId, item.getUserId());
		}

		return redact(dto);
	}

	@Override
	public <T extends ItemDTO> T get(final User caller, final Long itemAssignmentId, final PassphraseHash passphraseHash, final Class<T> clazz, final List<OrderBy> orderby) {
		return get(caller, itemAssignmentId, passphraseHash, clazz, orderby, this::getModel);
	}

	@Override
	public ItemDTO get(final User caller, final Long itemAssignmentId, final PassphraseHash passphraseHash, final List<OrderBy> orderby) {
		return get(caller, itemAssignmentId, passphraseHash, ItemDTO.class, orderby);
	}

	private SharedItemDTO setSharedAt(final Long itemAssignmentId, final SharedItemDTO dto) {
		dto.setSharedAt(getItemAssignment(itemAssignmentId).getCreatedAt());
		return dto;
	}

	final class BooleanReference {
		Boolean b;
	}

	private static SharedItem secureValue(final SharedItem si, final BooleanReference b) {
		b.b = si.getProfilePictureEnabled();
		return si;
	}

	private static SharedItemDTO setProfilePicEnabled(final SharedItemDTO si, final BooleanReference b) {
		si.setProfilePictureEnabled(b.b);
		return si;
	}

	// Probably one of the most frequently called methods; returns an item
	@Override
	public SharedItemDTO getSharedItem(final User caller, final Long itemAssignmentId, final PassphraseHash passphraseHash, final List<OrderBy> orderby) {
		final BooleanReference br = new BooleanReference();
		logger.info(() -> "getSharedItem: caller = " + caller.getUsername() + ", itemAssignmentId = " + itemAssignmentId);
		return setProfilePicEnabled(setSharedAt(itemAssignmentId, get(caller, itemAssignmentId, passphraseHash, SharedItemDTO.class, orderby, id -> secureValue(itemOperations.getSharedItem(caller.getId(), id), br))), br);
	}

	private Integer shareCount(final Long id) {
		return itemAssignmentOperations.getItemShareCount(id);
	}

	// TODO: This should be done in the database by the getUsersItems function.
	private <T extends ObidosResult<LimitedItemDTO>> T setShareCounts(final T result) {
		if (!result.noResults()) {
			result.getElements().forEach(item -> item.setShareCount(shareCount(item.getId())));
		}
		return result;
	}

	private Long getNotebookContainerAssignmentId(final User user) {
		return containerActions.getContainerAssignmentId(user, NOTEBOOK_ID);
	}

	@Override
	public ItemsResult getMyItems(final User caller, final Long containerAssignmentId, final Boolean shared, final String nameSearch, final Collection<Long> preSelectedItems, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		final Long notContainerAssignmentId = getNotebookContainerAssignmentId(caller);

		return setShareCounts(new ItemsResult(first, count, preSelectedItems, (a,b) -> convert(a,b),
				() -> itemOperations.getUsersItemsCount(caller.getId(), containerAssignmentId, notContainerAssignmentId, shared, nameSearch, preSelectedItems),
				() -> itemOperations.getUsersItems(caller.getId(), containerAssignmentId, notContainerAssignmentId, shared, nameSearch, preSelectedItems, first, count, orderBy)));
	}

	@Override
	public NotesResult getMyNotes(final User caller, final Boolean shared, final String nameSearch, final Collection<Long> preSelectedItems, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		final Long notContainerAssignmentId = getNotebookContainerAssignmentId(caller);

		return setShareCounts(new NotesResult(first, count, preSelectedItems, (a,b) -> convert(a,b),
				() -> itemOperations.getUsersItemsCount(caller.getId(), notContainerAssignmentId, null, shared, nameSearch, preSelectedItems),
				() -> itemOperations.getUsersItems(caller.getId(), notContainerAssignmentId, null, shared, nameSearch, preSelectedItems, first, count, orderBy)));
	}

	private void auditUpdate(final Long itemId, final Long userId, final String username, final Object object) {
		audit(UPDATE_ITEM, username, userId, object, null, itemId);
	}

	private void auditRename(final Long itemId, final Long userId, final String username, final Object oldName, final String newName) {
		audit(RENAMED_ITEM, username, userId, oldName, null, itemId, newName);
	}

	private void auditShare(final Long itemId, final Long recipientId, final Long ownerId) {
		auditShare(getUser(ownerId).getUsername(), ownerId, recipientId, itemId);
	}

	private void updateSelective(final Item i) {
		itemOperations.updateSelective(i);
	}

	// Some actions should not update the 'Last Updated' field of the Item: (un)Sharing an item, and moving to a different container -- Bug #102
	private void updateSelectiveStealthy(final Item i) {
		itemOperations.updateSelective(i, null);
	}

	private Void updateSelective(final ItemAssignment ia) {
		return itemAssignmentOperations.updateSelective(ia);
	}

	private Void updateItemValues(final User caller, final StreamSupplier<UserDefinedTypeValueDTO> valueSupplier, final Long itemId, final Boolean shared) {
		if (valueSupplier == null) {
			return null;
		}

		for(final UserDefinedTypeValueDTO value : valueSupplier.collection()) { // can't process as stream since we throw an exception
			if (value.getId() == null) { throw new ServerSideException("User Defined Type Value ID is required but was not supplied for update."); }
			for(final UserDefinedFieldValueDTO v : value.getFieldValues()) {
				if (v.getId() == null) { throw new ServerSideException("User Defined Field Value ID is required but was not supplied for update to field as position " + v.getPosition()); }
			}
		}

		auditUpdate(itemId, caller.getId(), caller.getUsername(), getModel(itemId));

		return processStream(() -> caller + " is updating values for item " + itemId, valueSupplier, v -> userDefinedTypeValueActions.update(caller, v, itemId, shared));
	}

	private static Item setExpire(final Item item, final ItemExpiration itemExpiration) {
		if (itemExpiration != null && itemExpiration.expirationSpecified()) {
			logger.info(()-> "Item expires at:" + itemExpiration.getExpiresAt());
			item.setSharesExpireAt(itemExpiration.getExpiresAt());
		}
		return item;
	}

	private void clearItemExpiration(final ItemDTO item) {
		final Item i = getModel(item.getId());
		i.setSharesExpireAt(null);
		itemOperations.update(i);
	}

	private void revoke(final User caller, final Long itemId, final String comment) {
		final Stream<Long> users = getUserIdsSharingItem(caller.getId(), itemId);
		itemAssignmentOperations.deleteOthers(itemId, caller.getId());
		itemGroupOperations.deleteAllGroups(itemId);
		userDefinedTypeValueOperations.deleteOtherUserItemReferences(itemId, caller.getId());
		notifyRevokeRecipients(caller, () -> users, itemId, comment, null);
	}

	private Supplier<Stream<Long>> getUsersSharingItemSupplier(final User caller, final Long itemId) {
		return () -> getUserIdsSharingItem(caller.getId(), itemId);
	}

	/**
	 *
	 * @param caller
	 * @param item
	 * @param oldItem
	 * @param passphraseHash
	 * @return A Runnable that will share an item with all users sharing the container of where the item exists. Typically invoked when an
	 * item changes state from private to sharable.  An object is only returned if the container in which the item now exists within is
	 * shared.
	 */
	private Runnable changeShareableState(final User caller, final ItemDTO item, final Item oldItem, final PassphraseHash passphraseHash) {
		final Container container = getOwnedContainer(caller, oldItem.getContainerAssignmentId());

		if (isTrue(container.getIsPrivate())) {
			item.setShareable(null);
		} else if (isFalse(oldItem.getShareable()) && isTrue(container.getShared())) { // Item made sharable if container is shared
			return () -> shareItemWithUsersOfContainer(caller, item.getId(), container.getId(), getGroupIdSupplier(container.getId()), createUDTValueListSupplier(caller, item.getId(), passphraseHash), caller.getFullname() + " has shared this item.", null);
		}
		return null;
	}

	private void updateItemShare(final User caller, final ItemDTO item, final Item oldItem) {
		if (item.getName() != null) {
			logger.info(() -> "item renamed to " + item.isExpirationSet());
			auditRename(item.getId(), caller.getId(), caller.getUsername(), oldItem, item.getName());
		}

		if (isTrue(oldItem.getShared()) && !oldItem.getName().equals(item.getName())) {
			notifyRenameRecipients(caller, getUsersSharingItemSupplier(caller, item.getId()), item.getId(), oldItem + " to: " + item);
		}

		updateSelective(setExpire(convert(item, Item.class), item.getItemExpiration()));
	}

	/**
	 * This method will update meta-information of the item (attributes other than the item value).
	 *
	 * @param caller
	 * @param item
	 * @param oldItem
	 */
	private void updateItemAttributes(final User caller, final ItemDTO item, final Item oldItem, final PassphraseHash passphraseHash) {
		final boolean shareableSet = item.getShareable() != null;
		final boolean itemMadeNonShareable = shareableSet && (oldItem.getShareable() && !item.getShareable());
		Runnable shareAction = null;

		if (shareableSet) {
			if (itemMadeNonShareable) { // user is making item private, revoke all shares
				revoke(caller, item.getId(), "The item has been made private.");
			}

			if (isTrue(item.getShareable())) {
				shareAction = changeShareableState(caller, item, oldItem, passphraseHash);
			}
		}

		item.setShared(itemMadeNonShareable ? Boolean.FALSE : shareAction != null ? Boolean.TRUE : null); // NOSONAR - making a separate method would increase complexity

		if (shareableSet || item.getName() != null || item.isExpirationSet()) {
			updateItemShare(caller, item, oldItem);
			if (shareAction != null) {
				shareAction.run();		// we can only share the item with users AFTER the item has been updated to be 'shareable'
			}
		}

		if (item.isExpireTimeCleared() != null && item.isExpireTimeCleared()) {
			clearItemExpiration(item);
		}
	}

	private boolean grantedUpdatePermissionViaGroup(final User caller, final ItemAssignment ia) {
		return itemOperations.mayUpdateItem(caller.getId(), ia.getItemId());
	}

	private boolean grantedUpdatePermissionViaContainerGroup(final User caller, final Long containerId) {
		return isTrue(containerGroupAssignmentOperations.mayUpdateItems(caller.getId(), containerId));
	}

	// Throws an exception if the user was not granted explicit Item Update permission nor permission via a group or from the Container that the item resides in.
	private boolean mayUpdateItem(final User caller, final Item item) {
		final ItemAssignment ia = getItemAssignment(item.getId(), caller.getId());
		
		if (ia.getUpdatePermitted() || grantedUpdatePermissionViaGroup(caller, ia)) {
			return true;
		}

		final ContainerAssignment callerContainerAssignment = getCallerContainerAssignment(caller, item.getContainerAssignmentId());
		final Long containerId = callerContainerAssignment.getContainerId();

		if (!containerIsNotebook(containerId)) {
			if (callerContainerAssignment.getUpdatePermitted()) {
				return true; // Caller was granted permission via the Container (Owner granted permission directly to the caller)
			}

			if (grantedUpdatePermissionViaContainerGroup(caller, containerId)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Any user with appropriate permission may update an item (see ItemAttributes.updatePermitted).
	 *
	 * Each user has their own copy of the values. When values are updated, the values stored for each shared item are
	 * updated too.
	 * 
	 * An item may be updated if the user was granted direct permission via the Item or if the user was granted in-direct
	 * permission via the Container that the Item resides in.
	 */
	@Override
	public Void update(final User caller, final ItemDTO item, final PassphraseHash passphraseHash) {
		if (item == null) {
			return null;
		}

		validatePassphraseHash(caller, passphraseHash); // ensures user has active public key as an additional level of authentication

		final Long itemId = item.getId();
		final Item currentItem = getModel(itemId);

		if (!mayUpdateItem(caller, currentItem)) {
			throw new PermissionDeniedException("You do not have permission to update this item.");
		}

		if (!licensePermitsQRCodeValues() && item.containsQRCode()) { throw new ServerSideException("Your license does not permit values of type QR Code.");}

		final boolean attributesChanged = currentItem.ownerIs(caller) && currentItem.attributesDiffer(item);

		if (attributesChanged) {
			updateItemAttributes(caller, item, currentItem, passphraseHash);
		}

		if (item.containsValues()) {
			updateItemValues(caller, StreamSupplier.create(sanitizeNotes(item.getValues(), true)), itemId, currentItem.getShared()); // we don't care if the item is now sharable, only if it WAS
			if (!attributesChanged) { // touch updated_at on item
				updateSelective(new Item(itemId, (Long) null));
			}
		}

		if (isTrue(currentItem.getShared())) {
			notifyUpdateRecipients(caller, getUsersSharingItemSupplier(caller, itemId), itemId, null);
		}
		return null;
	}

	private void markItemShared(final Long itemId) {
		updateSelectiveStealthy(new Item(itemId, TRUE));
	}

	private void markItemShared(final LimitedItem item) {
		if (item.getShared() == null || !item.getShared()) {
			markItemShared(item.getItemId());
		}
	}

	private Long createUserDefinedTypeValue(final User recipient, final Long itemAssignmentId, final UserDefinedTypeValueDTO udt) {
		udt.setId(null);
		udt.setCreatedAt(null);
		udt.setName(NULLSTRING);
		udt.setUpdatedAt(null);
		udt.setUserId(recipient.getId());
		return userDefinedTypeValueActions.create(recipient, itemAssignmentId, udt);
	}

	/**
	 * Once the transaction has been committed, this function will send notifications to the users supplied by the recipientSupplier.
	 *
	 * @param owner
	 * @param recipientSupplier
	 * @param item
	 * @param comment
	 * @param message
	 * @param action  Actions are defined in NotificationDTO, see ITEM_SHARED/ITEM_REVOKED/etc.
	 * @param itemAssignmentIdSupplier
	 * @param notifier
	 */
	private void notifyRecipients(final User owner, final Supplier<Stream<Long>> recipientSupplier, final Item item, final String comment, final String message, final int action, final LongUnaryOperator itemAssignmentIdSupplier, final Consumer<User> notifier) {
		postCommitQueue(() -> processStream(() -> message, recipientSupplier, u -> notifyRecipient(action, owner, u, itemAssignmentIdSupplier == null ? null : itemAssignmentIdSupplier.applyAsLong(u), "item", item.getName(), comment, notifier)));
	}

	private void notifyRecipients(final User owner, final Supplier<Stream<Long>> recipientSupplier, final Item item, final String comment, final LongUnaryOperator itemAssignmentIdSupplier, final Consumer<User> notifier, final int action) {
		notifyRecipients(owner, recipientSupplier, item, comment, "notifying recipients of " + NotificationDTO.getNotificationTypeString(action), action, itemAssignmentIdSupplier, notifier);
	}

	@Override
	public Map<Long,Long> createItemAssignmentsLookupTable(final Long itemId) {
		return createUserModelMap(itemAssignmentOperations.getItemAssignmentsOfItem(itemId));
	}

	@FunctionalInterface
	private interface Notifier {
		void run(Item i, Map<Long,Long> map, boolean isNote);
	}

	private void itemAction(final Long itemId, final boolean createAssignmentTable, final Notifier n) {
		final Item i = getModel(itemId);

		n.run(i, createAssignmentTable ? createItemAssignmentsLookupTable(itemId) : null, isNote(i));
	}

	private void notifyRecipients(final User owner, final Supplier<Stream<Long>> recipientSupplier, final Long itemId, final String shareComment, final Consumer<User> notifier, final int noteNotification, final int itemNotification) {
		itemAction(itemId, true, (i,map,isNote) -> notifyRecipients(owner, recipientSupplier, i, shareComment, map::get, notifier, isNote ? noteNotification : itemNotification));
	}

	private void sendItemEmail(final User owner, final String comment, int action, Map<Long,Long> userToItemAssignmentMap, boolean isNote, final User recipient) {
		if (recipient.getAcceptEmailNotification()) {
			switch(action) {
			case NOTE_SHARED:
			case ITEM_SHARED:
				emailActions.sendItemSharedEmail(userToItemAssignmentMap.get(recipient.getId()), owner, recipient, comment, isNote);
				break;
			case NOTE_REVOKED:
			case ITEM_REVOKED:
				emailActions.sendItemRevokedEmail(owner, recipient, comment);
				break;

			case NOTE_DELETED:
			case ITEM_DELETED:
				emailActions.sendItemDeletedEmail(owner, recipient);
				break;
			}
		}
	}

	private void sendItemSMS(final User owner, final String comment, int action, Map<Long,Long> userToItemAssignmentMap, boolean isNote, final User recipient) {
		logger.info(() -> "In ItemActionsImpl sendItemSMS action: " + action);
		if (recipient.getAcceptSMSNotification())
		{
			switch(action) {
			case NOTE_SHARED:
			case ITEM_SHARED:
				logger.info(() -> "In ItemActionsImpl sendItemSMS item shared: send SMS to: " + recipient.getName());
				smsActions.sendItemSharedSms(userToItemAssignmentMap.get(recipient.getId()), owner, recipient, comment, isNote);
				break;
			case NOTE_REVOKED:
			case ITEM_REVOKED:
				logger.info(() -> "In ItemActionsImpl sendItemSMS item removed: send SMS to: " + recipient.getName());
				smsActions.sendItemRevokedSms(owner, recipient, comment, isNote);
				break;

			case NOTE_DELETED:
			case ITEM_DELETED:
				logger.info(() -> "In ItemActionsImpl sendItemSMS item deleted: send SMS to: " + recipient.getName());
				smsActions.sendItemDeletedSms(owner, recipient, comment, isNote);
				break;
			default:
				logger.info(() -> "In ItemActionsImpl unknown action " + action);
				break;
			}
		}
		else
		{
			logger.info(() -> "In ItemActinosImpl sendItemSMS SMS : recipient " + recipient.getName() + " does not accept SMS notification");

		}
	}

	private void notifyShareRecipients(final User owner, final Supplier<Stream<Long>> recipientSupplier, final Long itemId, final String shareComment, final PostOpActions postOpActions) {
		logger.info(() -> "In itemActionsImpl.java notifyShareRecipients");
		logger.info(() -> "In itemActionsImpl.java postActions: " + postOpActions);
		if (postOpActions != null)
		{
			logger.info(() -> "In itemActionsImpl.java postActions send SMS?: " + postOpActions.sendSMS());
		}
		else
		{
			logger.info(() -> "In itemActionsImpl.java postActions send SMS postOpeActions is null");
		}
		// Bug# 49
		// Even though send SMS is set before sharing and recipient accepts SMS, sendItemSMS() below was never called.
		itemAction(itemId, true, (i,userToItemAssignmentMap,isNote) -> { final int action = isNote ? NOTE_SHARED : ITEM_SHARED;
			notifyRecipients(owner, recipientSupplier, i, shareComment, userToItemAssignmentMap::get, recipient -> {
				if (postOpActions != null && postOpActions.sendEmail()) { sendItemEmail(owner, shareComment, action, userToItemAssignmentMap, isNote, recipient);}
				if (postOpActions != null && postOpActions.sendSMS())   { sendItemSMS(owner, shareComment, action, userToItemAssignmentMap, isNote, recipient);}}, action);});

	}

	private void notifyRevokeRecipients(final User owner, Item item, int action, final String revokeComment, final PostOpActions postRevokeActions, final User recipient) {
		auditRevoke(owner.getUsername(), owner.getId(), recipient.getId(), item);
		if (postRevokeActions != null && postRevokeActions.sendEmail()) { sendItemEmail(owner, revokeComment, action, null, false, recipient);}
		if (postRevokeActions != null && postRevokeActions.sendSMS()) { sendItemSMS(owner, revokeComment, action, null, false, recipient);}
	}

	private void notifyRevokeRecipients(final User owner, final Supplier<Stream<Long>> recipientSupplier, final Long itemId, final String revokeComment, final PostOpActions postRevokeActions) {
		itemAction(itemId, false, (item,unused,isNote) -> { final int action = isNote ? NOTE_REVOKED : ITEM_REVOKED;
			notifyRecipients(owner, recipientSupplier, item, revokeComment, "notifying revoke recipients", action, null, recipient -> notifyRevokeRecipients(owner, item, action, revokeComment, postRevokeActions, recipient));});
	}

	private void notifyDeleteRecipients(final User owner, final Supplier<Stream<Long>> recipientSupplier, final Long itemId) {
		itemAction(itemId, false, (item,unused,isNote) -> { final int action = isNote ? NOTE_DELETED : ITEM_DELETED;
			notifyRecipients(owner, recipientSupplier, item, null, "notifying recipients of deleted item", action, null, recipient -> sendItemEmail(owner, null, action, null, isNote, recipient));});
	}

	private void notifyRenameRecipients(final User owner, final Supplier<Stream<Long>> recipientSupplier, final Long itemId, final String comment) {
		notifyRecipients(owner, recipientSupplier, itemId, comment, null, NOTE_RENAMED, ITEM_RENAMED);
	}

	private void notifyGrantRecipients(final User owner, final Supplier<Stream<Long>> recipientSupplier, final Long itemId, final String comment) {
		notifyRecipients(owner, recipientSupplier, itemId, comment, null, NOTE_PERMISSION_GRANTED, ITEM_PERMISSION_GRANTED);
	}

	@Override
	public void notifyUpdateRecipients(final User owner, final Supplier<Stream<Long>> recipientSupplier, final Long itemId, final String comment) {
		notifyRecipients(owner, recipientSupplier, itemId, comment, null, NOTE_UPDATED, ITEM_UPDATED);
	}

	private void resetItemShareFlag(final Long itemId) {
		updateSelectiveStealthy(new Item(itemId, false));
	}

	private Supplier<String> nameSupplier(final User caller, final Long itemId, final String recipient) {
		return () -> caller + " is sharing item " + getModel(itemId) + " with " + recipient + ".";
	}

	private Long createTemplateValuesForItem(final Long recipientId, final Consumer<LimitedUser> consumer, final Long itemAssignmentId, final UserDefinedTypeValueDTO v) {
		final User user = getUser(recipientId);
		if (consumer != null) {
			consumer.accept(user);
		}
		return createUserDefinedTypeValue(user, itemAssignmentId, v);
	}

	private void shareItemWithUser(final LimitedItem item, final Long ownerId, final Long recipientId, final Supplier<Stream<UserDefinedTypeValueDTO>> itemValueSupplier, final boolean markShared, final Consumer<LimitedUser> consumer, final Boolean sharedExplicitly) {
		logger.info(() -> "in shareItemWithUser, recipientId = " + recipientId + ", item Id = " + item.getId() + ", shared explicitly = " + sharedExplicitly);

		try {
			final Long iaId = getItemAssignment(item.getId(), recipientId).getId(); // we only hit this when the item is shared via group or container
			itemAssignmentOperations.incrementShareCount(iaId);

			// This is invoked when an item is shared implicitly (via a group), and now the user is sharing
			// the item explicitly with the recipient.  We need to set the sharedExplicitly flag. Bug #750
			if (isTrue(sharedExplicitly)) {
				itemAssignmentOperations.updateSelective(new ItemAssignment(Boolean.TRUE, iaId));
			}
		} catch(final NoSuchRecordException ex) {
			final Long itemAssignmentId = createItemAssignment(item.getItemId(), recipientId, Boolean.FALSE, sharedExplicitly);
			processStream(() -> "Creating template values for Item " + item.getItemId() + " for recipient " + recipientId, itemValueSupplier, v -> createTemplateValuesForItem(recipientId, consumer, itemAssignmentId, v), ce -> exceptionLoggerThrower("sharing Item with user", ce));
			if (markShared) {
				markItemShared(item);
			}
		}

		auditShare(item.getItemId(), recipientId, ownerId);
	}

	@Override
	public Void shareItemWithUser(final LimitedItem item, final Long ownerId, final Long recipientId, final PostOpActions postOpActions, final Supplier<Stream<UserDefinedTypeValueDTO>> itemValueSupplier, final Consumer<LimitedUser> consumer, final Boolean sharedExplicitly) {
		shareItemWithUser(item, ownerId, recipientId, itemValueSupplier, true, consumer, sharedExplicitly);
		return null;
	}

	private Item imposeSharingRestrictions(final User caller, final Long itemId, final Long recipientId) {
		final Long callerId = caller.getId();

		if (getItemAssignmentCount(itemId, callerId) == 0) {
			throw new PermissionDeniedException("You do not have access to this item.");
		}

		final Item item = getModel(itemId);

		if (!caller.self(item.getUserId())) {
			throw new ItemOwnershipException();
		}

		if (isFalse(item.getShareable())) { throw new SharingProhibitedException("This item is currently marked as NOT shareable."); }

		if (recipientId != null && getSystemConfig().getUseSecurityClearances() && getUser(recipientId).getSecurityClearance() < item.getSecurityClassification()) {
			throw new SharingProhibitedException(getUser(recipientId).getFullname() + " has insufficient security clearance for this item. It may not be shared with them.");
		}

		return item;
	}

	private void ensureCallerOwnsItem(final User caller, final Long itemId) {
		if (!caller.self(getModel(itemId).getUserId())) {
			throw new ItemOwnershipException();
		}
	}

	private static void itemNotNull(final Long id) {
		if (id == null) { throw new ServerSideException("You must specify an item."); }
	}

	private Void shareItemWithUsers(final User caller, final Long itemId, final Supplier<Stream<UserDefinedTypeValueDTO>> valueSupplier, final PostOpActions postOpActions, final Supplier<Stream<Long>> recipientSupplier, final String shareComment, final Boolean sharedExplicitly) {
		final Collection<Long> recipients = new HashSet<>();
		processStream(nameSupplier(caller, itemId, "users"), recipientSupplier, r -> shareItemWithUser(imposeSharingRestrictions(caller, itemId, r), caller.getId(), r, valueSupplier, Boolean.FALSE, u -> recipients.add(u.getId()), sharedExplicitly), ce -> exceptionLoggerThrower("sharing item with user", ce));
		notifyShareRecipients(caller, recipients::stream, itemId, shareComment, postOpActions);
		markItemShared(itemId);

		return null;
	}

	@Override
	public Void shareItemWithUsers(final User caller, final Long itemId, final PassphraseHash passphraseHash, final PostOpActions postOpActions, final Supplier<Stream<Long>> recipientSupplier, final String shareComment, final Boolean sharedExplicitly) {
		itemNotNull(itemId);
		ensureCallerOwnsItem(caller, itemId);

		return shareItemWithUsers(caller, itemId, createUDTValueListSupplier(caller, itemId, passphraseHash), postOpActions, recipientSupplier, shareComment, sharedExplicitly);
	}

	private void logShareItemWithGroupException(final ClosureExceptionWrapper iec, final Long itemId) {
		logger.error(() -> "Caught " + iec.ex + " while sharing items");
		logger.exception(iec.ex);
		audit(ITEM_GROUP_EXCEPTION, iec.name, null, null, itemId, null, iec.ex.getMessage());
	}

	private String groupName(final Long groupId) {
		return getGroup(groupId).getName();
	}

	@Override
	public Void shareItemWithGroup(final User caller, final LimitedItem item, final Long groupId, final PostOpActions unused, final Supplier<Stream<UserDefinedTypeValueDTO>> valueSupplier, final Consumer<LimitedUser> consumer, final Boolean sharedExplicitly, final String shareComment) {
		createItemGroup(item.getId(), groupId, sharedExplicitly);
		processStream(nameSupplier(caller, item.getId(), "users in group '" + groupName(groupId) + "'"), () -> getOthersInGroup(caller, groupId), user -> shareItemWithUser(item, caller.getId(), user.getId(), valueSupplier, Boolean.FALSE, consumer, Boolean.FALSE), ce -> logShareItemWithGroupException(ce, item.getId()));
		markItemShared(item);

		return null;
	}

	private Void shareItemWithGroups(final User caller, final Long itemId, final PostOpActions postOpActions, final Supplier<Stream<Long>> groupIdSupplier, final GroupItemConsumer consumer, final Supplier<Stream<UserDefinedTypeValueDTO>> valueSupplier) {
		itemNotNull(itemId);
		final Item item = imposeSharingRestrictions(caller, itemId, null);
		final Collection<Long> itemGroups = groupOperations.getGroupIdsSharingItem(item.getId()); // we do numerous lookups, this method returns a set if there are more than 4 elements

		processStream(nameSupplier(caller, itemId, " groups"), () -> groupIdSupplier.get().filter(gid -> !itemGroups.contains(gid)), groupId -> consumer.apply(item, valueSupplier, groupId), () -> "Sharing item with group");
		markItemShared(item);

		return null;
	}

	@Override
	public Void shareItemWithGroups(final User caller, final Long itemId, final PostOpActions postOpActions, final Supplier<Stream<UserDefinedTypeValueDTO>> valueSupplier, final Supplier<Stream<Long>> groupIdSupplier, final Collection<Long> recipients) {
		final GroupItemConsumer groupConsumer = (item, values, groupId) -> shareItemWithGroup(caller, item, groupId, postOpActions, values, recipients == null ? null : u -> recipients.add(u.getId()), Boolean.FALSE, null);
		return shareItemWithGroups(caller, itemId, postOpActions, groupIdSupplier, groupConsumer, valueSupplier);
	}

	/**
	 * Shares an item with all the groups supplied by groupIdSupplier.
	 * @param caller
	 * @param itemId
	 * @param valueSupplier
	 * @param groupIdSupplier
	 * @param consumer
	 * @param sharedExplicitly
	 * @param shareComment
	 * @param postOpActions
	 */
	private void shareItemWithGroupsInternal(final User caller, final Long itemId, final Supplier<Stream<UserDefinedTypeValueDTO>> valueSupplier, final Supplier<Stream<Long>> groupIdSupplier, final Consumer<LimitedUser> consumer, final Boolean sharedExplicitly, final String shareComment, final PostOpActions postOpActions) {
		final Collection<Long> recipients = consumer == null ? new HashSet<>() : null;
		final Consumer<LimitedUser> localConsumer = consumer != null ? consumer : user -> recipients.add(user.getId());

		shareItemWithGroups(caller, itemId, postOpActions, groupIdSupplier, (item, valuesSupplier, groupId) -> shareItemWithGroup(caller, item, groupId, postOpActions, valuesSupplier, localConsumer, sharedExplicitly, shareComment), valueSupplier);
		if (recipients != null) { // if caller is not collecting users to notify, we do it here
			notifyShareRecipients(caller, recipients::stream, itemId, shareComment, postOpActions);
		}
	}

	@Override
	public Void shareItemWithGroups(final User caller, final Long itemAssignmentId, final PassphraseHash passphraseHash, final PostOpActions postOpActions, final Supplier<Stream<Long>> groupIdSupplier, final Consumer<LimitedUser> consumer, final Boolean sharedExplicitly, final String shareComment) {
		itemNotNull(itemAssignmentId);
		if (groupIdSupplier == null)	{ throw new ServerSideException("You must specify a group ID to share the item with"); }
		final Long itemId = getItemId(caller, itemAssignmentId);

		shareItemWithGroupsInternal(caller, itemId, createUDTValueListSupplier(caller, itemId, passphraseHash), groupIdSupplier, consumer, sharedExplicitly, shareComment, postOpActions);
		return null;
	}

	@Override
	public SharedItemsResult getItemsSharedWithUser(final User caller, final Long containerAssignmentId, final String nameSearch, final Collection<Long> preSelectedItems, final Integer first, final Integer count, List<OrderBy> orderBy) {
		final Long containerId = getContainerId(containerAssignmentId);

		return new SharedItemsResult(first, count, preSelectedItems, (a,b) -> convert(a,b),
				() -> itemOperations.getItemsSharedWithUserCount(caller.getId(), containerId, NOTEBOOK_ID, nameSearch, preSelectedItems),
				() -> itemOperations.getItemsSharedWithUser(caller.getId(), containerId, NOTEBOOK_ID, nameSearch, preSelectedItems, first, count, orderBy));
		}

	@Override
	public SharedNotesResult getNotesSharedWithUser(final User caller, final String nameSearch, final Collection<Long> preSelectedItems, final Integer first, final Integer count, List<OrderBy> orderBy) {
		return new SharedNotesResult(first, count, preSelectedItems, (a,b) -> convert(a,b),
				() -> itemOperations.getItemsSharedWithUserCount(caller.getId(), NOTEBOOK_ID, null, nameSearch, preSelectedItems),
				() -> itemOperations.getItemsSharedWithUser(caller.getId(), NOTEBOOK_ID, null, nameSearch, preSelectedItems, first, count, orderBy));
	}

	// With a note, all that is supplied is the item ID.  We need to lookup the UserDefinedTypeValue and
	// UserDefinedFieldValue IDs for that item. Since it is a note, there will only be one of each.
	private UserDefinedTypeValueDTO injectUDFIds(final Long userId, final Long itemId, final UserDefinedTypeValueDTO dto) throws NoSuchRecordException {
		if (itemId != null) {
			userDefinedTypeValueOperations.getUserDefinedTypeValues(userId, itemId).forEach(value -> {
				dto.setId(value.getId());
				userDefinedFieldValueOperations.getUserDefinedFieldValues(value.getId(), null).forEach(f -> dto.getFieldValues().get(0).setId(f.getId()));});
		}

		return dto;
	}

	@Override
	public List<UserDefinedTypeValueDTO> convertToItemValues(final Long userId, final Long itemId, final byte[] simpleNoteValue) {
		return packageItem(injectUDFIds(userId, itemId, new UserDefinedTypeValueDTO(NOTES_ID, packageItem(new UserDefinedFieldValueDTO(TYPE_NOTES_FIELD_ID, simpleNoteValue)))));
	}

	@Override
	public Void revokeItem(final User caller, final Long itemAssignmentId, final PassphraseHash passphraseHash, final String revokeComment, final PostOpActions postRevokeActions) {
		final Long itemId = getItemId(caller, itemAssignmentId);
		itemNotNull(itemId);
		// revoke all instances of an item share (revoke from all users)
		validatePassphraseHash(caller, passphraseHash);
		ensureCallerOwnsItem(caller, itemId);
		revoke(caller, itemId, revokeComment);
		resetItemShareFlag(itemId);

		return null;
	}

	private void deleteItemOfTombstonedUser(final Item i) {
		itemOperations.delete(i.getId());
		containerActions.deleteIfEmpty(i.getContainerAssignmentId());
	}

	private void checkShareCount(final Long itemId) {
		if (shareCount(itemId) <= 1) {
			try {
				final Item i = getModel(itemId);

				if (userIsTombstoned(i.getUserId())) {
					deleteItemOfTombstonedUser(i);
				} else {
					resetItemShareFlag(itemId);
				}
			} catch(final ServerSideException ex) {
				logger.exception(ex);
			}
		}
	}

	private void revokeFromUser(final User user, final Item i, final Long recipientId, final Boolean explicit) throws NoSuchRecordException {
		final ItemAssignment itemAssignment = getItemAssignment(i.getId(), recipientId);
		final int count = itemAssignment.getCount();

		if (count == 1) { // no more references left, delete the item assignment
			itemAssignmentOperations.delete(itemAssignment.getId());
			userDefinedTypeValueOperations.deleteUserItemReferences(i.getId(), recipientId);
		} else if (isTrue(explicit)) { // There are still shares left, but the explicit share has been revoked
			itemAssignment.setSharedExplicitly(Boolean.FALSE);
			itemAssignment.setCount(count - 1);
			itemAssignmentOperations.update(itemAssignment);
		} else { // A share via a container has been revoked
			itemAssignmentOperations.decrementShareCount(itemAssignment.getId());
		}

		auditRevoke(user.getUsername(), user.getId(), recipientId, i);
	}

	@Override
	public Void revokeItemFromUsers(final User caller, final Long itemId, final Supplier<Stream<Long>> recipientSupplier, final PassphraseHash passphraseHash, final Boolean explicit, final String revokeComment, final PostOpActions postRevokeActions) {
		if (itemId == null) { throw new ServerSideException("You must specify an item."); }
		ensureCallerOwnsItem(caller, itemId);

		// revoke all instances of an item share
		validatePassphraseHash(caller, passphraseHash);
		final Item i = getModel(itemId);
		processStream(() -> caller + " is revoking item " + itemId + " from users", recipientSupplier, r -> revokeFromUser(caller, i, r, explicit), () -> "revoking item from user");
		checkShareCount(itemId);
		notifyRevokeRecipients(caller, recipientSupplier, itemId, revokeComment, postRevokeActions);

		return null;
	}

	@Override
	public Void revokeItemFromUsers(final User caller, final Long itemAssignmentId, final Collection<Long> recipients, final PassphraseHash passphraseHash, final Boolean explicit, final String revokeComment, final PostOpActions postRevokeActions) {
		final Long itemId = getItemId(caller, itemAssignmentId);
		final Supplier<Stream<Long>> recipientSupplier = StreamSupplier.create(() -> recipients);

		return revokeItemFromUsers(caller, itemId, recipientSupplier, passphraseHash, explicit, revokeComment, postRevokeActions);
	}

	@Override
	public Void revokeItemsSharedWithUserViaGroup(final Long userId, final Long groupId) {
		itemAssignmentOperations.decrementShareCountForUser(userId, groupId);
		return processStream(() -> "checking share count for items", () -> itemAssignmentOperations.getArtifacts(userId), ia -> checkShareCount(ia.getItemId()), () -> "revoking item from user");
	}

	@Override
	public void deleteArtifacts(final User caller, final String revokeComment) {
		getNotificationEngine(REVOKE_ITEM, itemNotificationEngineFactory).notifyUsers(caller, revokeComment, itemAssignmentOperations.getAllArtifacts());
		itemAssignmentOperations.deleteArtifacts(null); // delete any assignments that have a share count of 0
	}

	private static void logDetails(final String type, final String msg, final Long id, final Collection<Long> preSelected) {
		if (logger.isDebugEnabled()) {
			logger.debug(() -> "Getting " + msg + " " + id);
			logger.debug(() -> "Pre-Selected " + type + " count = " + (preSelected == null ? "null" : Integer.toString(preSelected.size())));
		}
	}

	@Override
	public ItemGroupsResult getGroupsSharingItem(final User caller, final Long itemAssignmentId, final String nameSearch, final Collection<Long> preSelectedGroups, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		final Long itemId = getItemId(caller, itemAssignmentId);

		ensureCallerOwnsItem(caller, itemId);

		if (!getItemAssignment(itemAssignmentId).ownerIs(caller)) {
			throw new ItemOwnershipException();
		}
		logDetails("Group", "groups for item", itemId, preSelectedGroups);

		return new ItemGroupsResult(first, count, preSelectedGroups, (a,b) -> convert(a,b),
				() -> groupOperations.getGroupsSharingItemCount(itemId, nameSearch, preSelectedGroups, Boolean.TRUE),
				() -> groupOperations.getGroupsSharingItem(itemId, nameSearch, preSelectedGroups, Boolean.TRUE, first, count, orderBy));
	}

	private void expungeReferencesToItem(final Long itemAssignmentId, final Long itemId, final Long userId) {
		itemAssignmentOperations.delete(itemAssignmentId);
		userDefinedTypeValueOperations.deleteUserItemReferences(itemId, userId);
	}

	/**
	 * This method is called by the the User Management code when it has a tombstoned user relinquish
	 * all items that have been shared with that user.  We do not want to audit these actions nor
	 * have notifications sent to the tombstoned user.
	 * If the item shared with us is from a tombstoned user, we can delete the item if there are no more
	 * users that the item is shared with.
	 */
	public void relinquishItem(final User caller, final Long itemAssignmentId, final Long itemId) {
		expungeReferencesToItem(itemAssignmentId, itemId, caller.getId());
		checkShareCount(itemId);
	}

	private Void relinquishItem(final User caller, final Long itemAssignmentId, final Long ownerId, final Long itemId) {
		if (!itemId.equals(getItemId(caller, itemAssignmentId))) {
			throw new ServerSideException("Incorrect item specified.");
		}

		final Item i = getModel(itemId);
		final int action = isNote(i) ? NOTE_RELINQUISHED : ITEM_RELINQUISHED;
		relinquish(caller, ownerId, itemId, i.getName(), action, () -> expungeReferencesToItem(itemAssignmentId, itemId, caller.getId()));
		checkShareCount(itemId);
		auditItemRelinquish(caller.getUsername(), caller.getUserId(), i.getUserId(), i.getName(), itemId);

		return null;
	}

	// We need to notify users, that we shared the item with, that we are now deleting it.
	private Void deleteWithNotify(final User caller, final Long itemId) {
		final Stream<Long> s = getUserIdsSharingItem(caller.getId(), itemId); // need to generate the stream now, BEFORE delete occurs

		notifyDeleteRecipients(caller, () -> s, itemId);	// this method has the stream supplier being called after the commit (after the delete)

		final Stream<Document> documents = documentActions.getDocumentsOfItem(itemId);
		postCommitAction(() -> processStream(() -> " deleting documents associated with item " + itemId, () -> documents, documentActions::delete));

		return super.delete(caller, itemId);
	}

	@Override
	protected final Void delete(final User caller, final Long itemAssignmentId) {
		itemNotNull(itemAssignmentId);
		final Long itemId = getItemId(caller, itemAssignmentId);
		final Long ownerId = getModel(itemId).getUserId();
		final boolean userOwnsItem = caller.self(ownerId);

		return userOwnsItem ? deleteWithNotify(caller, itemId) : relinquishItem(caller, itemAssignmentId, ownerId, itemId);
	}

	@Override
	public Integer deleteAllUserItems(final User user) {
		return itemOperations.deleteAllUserItems(user.getId());
	}

	private void revokeItemFromGroup(final User caller, final Long itemId, final Long groupId) {
		logger.info(() -> "Revoking item " + itemId + " from group " + groupId);
		itemAssignmentOperations.decrementShareCountForGroup(itemId, groupId);
		itemGroupOperations.delete(itemId, groupId);
		auditRevokeFromGroup(caller.getUsername(), caller.getId(), groupId, getModel(itemId));
	}

	@Override
	public Void revokeItemFromGroups(final User caller, final Long itemId, final Supplier<Stream<Long>> groupIdSupplier, final ObidosExecutor onCompletion) {
		ensureCallerOwnsItem(caller, itemId);
		processStream(() -> caller + " is revoking access to item " + itemId + " from groups", groupIdSupplier, g -> revokeItemFromGroup(caller, itemId, g), () -> "revoking item from group");
		onCompletion.execute(() -> checkShareCount(itemId));
		return null;
	}

	@Override
	public Void revokeItemFromGroups(final User caller, final Long itemAssignmentId, final Supplier<Stream<Long>> groupIdSupplier, final PassphraseHash passphraseHash, final String revokeComment, final PostOpActions postRevokeActions) {
		if (groupIdSupplier == null)	{ throw new ServerSideException("You must specify a group supplier."); }

		itemNotNull(itemAssignmentId);
		validatePassphraseHash(caller, passphraseHash);
		final ObidosExecutor onCompletion = new ObidosExecutor();	// this is used to call the checkShared method which needs to be called after deleteArtifacts
		revokeItemFromGroups(caller, getItemId(caller, itemAssignmentId), groupIdSupplier, onCompletion);
		deleteArtifacts(caller, revokeComment); // delete any assignments that have a share count of 0
		return onCompletion.runAll();
	}

	private ItemAssignment secureGetItemAssignment(final User caller, final Long itemAssignmentId, final PassphraseHash passphraseHash) {
		itemNotNull(itemAssignmentId);
		validatePassphraseHash(caller, passphraseHash);

		final ItemAssignment itemAssignment = getItemAssignment(itemAssignmentId);
		if (!itemAssignment.ownerIs(caller)) { throw new ItemOwnershipException("This is not your Item assignment."); }

		return itemAssignment;
	}

	private void auditItemGrant(final User caller, final Long itemId, final PermissionDTO permission, final Collection<Long> recipients) {
		final boolean may_own = isTrue(permission.getHasOwnershipControl());
		final boolean may_update = isTrue(permission.getMayUpdate());
		final int action = may_own ? (may_update ? Audit.GRANT_ITEM_UPDATE_AND_OWNERSHIP : Audit.GRANT_ITEM_OWNERSHIP) : may_update ? Audit.GRANT_ITEM_UPDATE : Audit.GRANT_ITEM_READ_ONLY;
		final String username = caller.getUsername();
		final Long userId = caller.getId();

		recipients.forEach(recipientId -> auditGrant(username, userId, recipientId, itemId, action));
	}

	private void auditGroupItemGrant(final User caller, final Long itemId, final PermissionDTO permission, final Collection<Long> groups) {
		final boolean may_own = isTrue(permission.getHasOwnershipControl());
		final boolean may_update = isTrue(permission.getMayUpdate());
		final int action = may_own ? (may_update ? Audit.GRANT_ITEM_UPDATE_AND_OWNERSHIP : Audit.GRANT_ITEM_OWNERSHIP) : may_update ? Audit.GRANT_ITEM_UPDATE : Audit.GRANT_ITEM_READ_ONLY;
		final String username = caller.getUsername();
		final Long userId = caller.getId();
		final HashSet<Long> recipients = new HashSet<>();

		groups.forEach(gid -> recipients.addAll(groupMemberOperations.getUserIdsInGroup(gid)));
		recipients.forEach(recipientId -> auditGrant(username, userId, recipientId, itemId, action));
	}

	private Long verifiedItemId(final User caller, final Long itemAssignmentId, final PassphraseHash passphraseHash) {
		final Long itemId = secureGetItemAssignment(caller, itemAssignmentId, passphraseHash).getItemId();

		if (!getModel(itemId).ownerIs(caller)) { throw new ItemOwnershipException(); }

		return itemId;
	}

	@Override
	public Void grantUsersPermission(final User caller, final Long itemAssignmentId, final PermissionDTO permission, final Collection<Long> recipients, final PassphraseHash passphraseHash) {
		if (recipients == null || recipients.isEmpty())			{ throw new ServerSideException("Please specify recipients."); }
		if (permission == null || !permission.somethingIsSet())	{ throw new ServerSideException("Please specify permissions to grant."); }

		final Long itemId = verifiedItemId(caller, itemAssignmentId, passphraseHash);

		auditItemGrant(caller, itemId, permission, recipients);
		notifyGrantRecipients(caller, () -> recipients.stream(), itemId, permission.message());

		return itemAssignmentOperations.grantPermissions(itemId, permission, recipients);
	}

	private Stream<Long> usersInGroups(final Collection<Long> groups) {
		final Collection<Long> users = new HashSet<>();

		groups.forEach(groupId -> users.addAll(groupMemberOperations.getUserIdsInGroup(groupId)));

		return users.stream();
	}

	@Override
	public Void grantGroupsPermission(final User caller, final Long itemAssignmentId, final PermissionDTO permission, final Collection<Long> groups, final PassphraseHash passphraseHash) {
		if (groups == null || groups.isEmpty())					{ throw new ServerSideException("Please specify groups."); }
		if (permission == null || !permission.somethingIsSet())	{ throw new ServerSideException("Please specify permissions to grant."); }

		final Long itemId = verifiedItemId(caller, itemAssignmentId, passphraseHash);

		auditGroupItemGrant(caller, itemId, permission, groups);
		notifyGrantRecipients(caller, () -> usersInGroups(groups), itemId, caller.getFullname() + " has granted you " + permission.message() + " permission");

		return itemGroupOperations.grantPermissions(itemId, permission, groups);
	}

	private Long getMyNotesContanierAssignmentId(final User caller) {
		return containerActions.getContainerAssignmentId(caller, NOTEBOOK_ID);
	}

	private void changeItemOwnership(final User caller, final Long itemId, final Long containerAssignmentId) {
		updateSelective(new Item(itemId, caller.getId(), containerAssignmentId));
	}

	private void takeOwnershipOfItem(final User caller, final Long itemId, final String itemName, final Long containerAssignmentId, final PassphraseHash passphraseHash, final PostOpActions postOpActions) {
		final Long destinationContainerId = getCallerContainerAssignment(caller, containerAssignmentId).getContainerId();
		final Container destinationContainer = getContainer(destinationContainerId);

		if (isTrue(destinationContainer.getIsPrivate())) {
			throw new PermissionDeniedException("This container is Private. You may not put a shared Item in it.");
		}

		changeItemOwnership(caller, itemId, containerAssignmentId);

		if (isTrue(destinationContainer.getShared())) {
			shareItemWithUsersOfContainer(caller, itemId, destinationContainerId, getGroupIdSupplier(destinationContainerId), createUDTValueListSupplier(caller, itemId, passphraseHash), caller + " has assumed ownership of " + itemName, postOpActions);
		}

		// Groups that the item was shared with are not shared between users.
		itemGroupOperations.deleteAllGroups(itemId);
	}

	@Override
	public void takeOwnership(final User caller, final Item item, final ItemAssignment itemAssignment, final Long containerAssignmentId, final PassphraseHash passphraseHash, final PostOpActions postOpActions) {
		final Long itemId = item.getId();

		auditOwned(caller.getUsername(), caller.getId(), item.getUserId(), itemId);
		updateSelective(new ItemAssignment(itemAssignment.getId(), true));	// gives new user (full) control in case they did not have it before
		itemAssignmentOperations.resetShareCount(caller.getId(), itemId);

		if (containerIsNotebook(getContainerId(item.getContainerAssignmentId()))) { // item is a note
			changeItemOwnership(caller, itemId, getMyNotesContanierAssignmentId(caller));
		} else {
			takeOwnershipOfItem(caller, itemId, item.toString(), containerAssignmentId, passphraseHash, postOpActions);
		}
	}

	private void mayTakeOwnership(final User caller, final ItemAssignment ia) {
		if (isFalse(ia.getOwnershipControl()) && !itemOperations.mayTakeOwnership(caller.getId(), ia.getItemId())) {
			throw new PermissionDeniedException("You were not granted ownership control of this item.");
		}
	}

	/**
	 * A user wants to take ownership of an item. This item can be either a note or a regular item.
	 * Notes are special. They exist within a Notes container which is the same for everyone.
	 * The Notes container can not be shared, so this simplifies things.
	 *
	 * Regular items are then shared with anyone the destination container (containerAssignmentId) is shared with.
	 */
	public Void takeOwnership(final User caller, final Long itemAssignmentId, final Long containerAssignmentId, final PassphraseHash passphraseHash, final PostOpActions postOpActions) {
		final ItemAssignment itemAssignment = secureGetItemAssignment(caller, itemAssignmentId, passphraseHash);

		mayTakeOwnership(caller, itemAssignment);

		final Item item = getModel(itemAssignment.getItemId());
		if (item.ownerIs(caller)) { throw new PermissionDeniedException("You already own this item."); }
		logger.info(() -> "User " + caller.getUsername() + " (" + caller.getId() + ") is taking ownership of item " + item.getName() + " (" + item.getId() + ") ");

		takeOwnership(caller, item, itemAssignment, containerAssignmentId, passphraseHash, postOpActions);
		// We notify all the users who have access to the item after we return.
		notifyRecipients(caller, getUsersSharingItemSupplier(caller, item.getId()), item, null, item.getName(), ITEM_OWNED, u-> itemAssignmentOperations.get(item.getId(), u).getId(), null);
		return null;
	}

	private void moveToUnsharedContainer(final User caller, final PassphraseHash passphraseHash, final Item item, final Long sourceContainerId, final Long destContainerId) {
		final Collection<Long> removedGroups = new ArrayList<>();
		final Collection<Long> newGroups = new ArrayList<>();
		final Collection<Long> sourceGroups = getGroupIdsSharingContainer(sourceContainerId);
		final Collection<Long> destGroups   = getGroupIdsSharingContainer(destContainerId);
		for(final Long g : sourceGroups) {
			if (!destGroups.contains(g)) {
				newGroups.add(g);
			}
		}
		for(final Long g : destGroups) {
			if (!sourceGroups.contains(g)) {
				removedGroups.add(g);
			}
		}

		if (!removedGroups.isEmpty()) {
			containerActions.revokeContainerFromGroups(caller, item.getContainerAssignmentId(), removedGroups, passphraseHash, null);
		}
		if (!newGroups.isEmpty()) {
			containerActions.shareContainerWithGroups(caller, item.getContainerAssignmentId(), StreamSupplier.create(newGroups), passphraseHash, null, new PostOpActions());
		}
		// get groups and users sharing the item via old container
		// get groups and users sharing the item via new container
		// find groups and users that no longer have access (removed shares)
		// find groups and users that will now have access (added shares)
		//
		// remove item from origin container (if shared, remove all shares)
		// put item in destination container (
	}

	@Override
	public Void moveToContainer(final User caller, final Long itemAssignmentId, final Long containerAssignmentId, final PassphraseHash passphraseHash) {
		final ItemAssignment itemAssignment = secureGetItemAssignment(caller, itemAssignmentId, passphraseHash);
		final Long itemId = itemAssignment.getItemId();
		final Item item = getModel(itemId);
		if (!item.ownerIs(caller)) { throw new ItemOwnershipException(); }
		if (item.getContainerAssignmentId().equals(containerAssignmentId)) { throw new ServerSideException("That item is already in that container."); }
		final Long sourceContainerId			= getContainerAssignment(item.getContainerAssignmentId()).getContainerId();
		final Long destContainerId				= getContainerAssignment(containerAssignmentId).getContainerId();
		final Container sourceContainer			= getContainer(sourceContainerId);
		final Container destinationContainer	= getOwnedContainer(caller, containerAssignmentId);
		if (destinationContainer.getIsPrivate() && item.getShareable()) { throw new PermissionDeniedException("This item is shareable but this container is private."); }

		   	// if item is not even shared at the moment, we can avoid calculating the difference
		if (sourceContainer.getShared() && item.getShared()) {
			moveToUnsharedContainer(caller, passphraseHash, item, sourceContainerId, destContainerId);
		}

		updateSelectiveStealthy(new Item(item.getId(), null, containerAssignmentId));

		return null;
	}
}
