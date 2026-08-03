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

import static com.spenego.Obidos.server.model.Audit.DELETE_USER_DEFINED_TYPE_VALUE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.UserDefinedFieldValueActions;
import com.spenego.Obidos.server.actions.UserDefinedTypeValueActions;
import com.spenego.Obidos.server.model.LimitedUser;
import com.spenego.Obidos.server.model.SharedItemUser;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.model.UserDefinedField;
import com.spenego.Obidos.server.model.UserDefinedFieldValue;
import com.spenego.Obidos.server.model.UserDefinedTypeValue;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.operations.UserDefinedFieldOperations;
import com.spenego.Obidos.server.operations.UserDefinedFieldValueOperations;
import com.spenego.Obidos.server.operations.UserDefinedTypeOperations;
import com.spenego.Obidos.server.operations.UserDefinedTypeValueOperations;
import com.spenego.Obidos.server.security.Encryption.PublicKeyDecryptor;
import com.spenego.Obidos.server.security.Encryption.PublicKeyEncryptor;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.UserDefinedFieldValueDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeValueDTO;
import com.spenego.Obidos.shared.exceptions.DataIntegrityException;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class UserDefinedTypeValueActionsImpl extends CryptoActions<UserDefinedTypeValue> implements UserDefinedTypeValueActions {
	private static final Logger logger = LoggerFactory.getLogger(UserDefinedTypeValueActionsImpl.class);

	@Autowired private final UserDefinedFieldOperations			userDefinedFieldOperations = null;
	@Autowired private final UserDefinedFieldValueActions		userDefinedFieldValueActions = null;
	@Autowired private final UserDefinedFieldValueOperations	userDefinedFieldValueOperations = null;
	@Autowired private final UserDefinedTypeOperations			userDefinedTypeOperations = null;
	@Autowired private final UserDefinedTypeValueOperations		userDefinedTypeValueOperations = null;

	public UserDefinedTypeValueActionsImpl() {
		super(null, null);
	}

	@Override
	protected final Operations<UserDefinedTypeValue> getOperations() {
		return userDefinedTypeValueOperations;
	}

	@Override
	protected final Logger getLogger() {
		return logger;
	}

	@Override
	protected final Integer getAuditDeleteAction() {
		return DELETE_USER_DEFINED_TYPE_VALUE;
	}

	@Override
	protected final String elementName() {
		return "template value";
	}

	private String getTypeName(final Long typeId) throws NoSuchRecordException {
		return userDefinedTypeOperations.get(typeId).getName();
	}

	private Collection<UserDefinedField> getTypeFields(final Long typeId) {
		return userDefinedFieldOperations.getTypeFields(typeId);
	}

	@Override
	public Long create(final User caller, final Long itemAssignmentId, final UserDefinedTypeValueDTO value) {
		if (itemAssignmentId == null)	{ throw new ServerSideException("You must specify an item that holds these values."); }
		if (value == null)				{ throw new ServerSideException("You must specify the values to create."); }

		final Long typeId = value.getUserDefinedTypeId();
		if (typeId == null)				{ throw new ServerSideException("A template id was not specified while creating value."); }

		final int fieldCount = value.getFieldValues().size();
		if (fieldCount == 0)			{ throw new ServerSideException("Field value list needs at least one value."); }

		final Collection<UserDefinedField> fields = getTypeFields(typeId);
		if (fieldCount > fields.size())	{ throw new ServerSideException("Field value list has more values than types of fields."); }

		// This integrity check is performed by the database, but rather than create it and tear it all down in the event of an error, we do some checks here.
		for(final UserDefinedFieldValueDTO val : value.getFieldValues()) {
			final Long valId = val.getUserDefinedFieldId();
			boolean fieldIdFound = false;

			for(final UserDefinedField f : fields) {
				if (f.getId().equals(valId)) {
					fieldIdFound = true;
				}
			}
			if (!fieldIdFound) { throw new ServerSideException("Field " + val.getName() + " uses a field that is not part of type " + getTypeName(typeId)); }
		}

		// the rowId is essentially the typeValueId. A type value defines a row in the user-defined-type meta-table.
		final Long rowId = userDefinedTypeValueOperations.create(new UserDefinedTypeValue(value.getUserDefinedTypeId(), caller.getId(), itemAssignmentId));

		userDefinedFieldValueActions.create(caller, value.getFieldValues(), rowId);

		return rowId;
	}

	@Override
	public Collection<UserDefinedTypeValueDTO> getList(final User caller, final Long itemId, final Supplier<PublicKeyDecryptor> decryptorSupplier, final List<OrderBy> orderby) {
		final Collection<UserDefinedTypeValueDTO> values = convert((Supplier<Stream<UserDefinedTypeValue>>) () -> userDefinedTypeValueOperations.getUserDefinedTypeValues(caller.getId(), itemId), UserDefinedTypeValueDTO.class);
		for(final UserDefinedTypeValueDTO udt : values) {
			udt.setFieldValues(userDefinedFieldValueActions.getUDFValues(caller, udt.getId(), decryptorSupplier, orderby));
		}
		return values;
	}

	/**
	 * We need to load the list of field values for each user that has an item shared with them so that we can update those values.
	 *
	 * The list that comes back is very limited, populated with only the ID fields and not the value or other fields.
	 *
	 * @param user
	 * @param itemId
	 * @param fields
	 * @return
	 */
	private Collection<UserDefinedFieldValueDTO> loadValues(final LimitedUser user, final Long itemId, final Collection<Long> fields) {
		return convert((Supplier<Stream<UserDefinedFieldValue>>) () -> userDefinedFieldValueOperations.getFieldsOfItem(user.getId(), itemId, fields), UserDefinedFieldValueDTO.class);
	}

	/**
	 * The client passed a list of values to be updated. This list may be a subset of fields.
	 * The template contains a list of all fields for a particular user. For each template item, we update
	 * the value in the template if we find a corresponding value in values.
	 *
	 * @param values
	 * @param template
	 * @return
	 */
	private static Collection<UserDefinedFieldValueDTO> injectValues(final Collection<UserDefinedFieldValueDTO> values, final Collection<UserDefinedFieldValueDTO> template) {
		final List<UserDefinedFieldValueDTO> result = new ArrayList<>(values.size());

		for(final UserDefinedFieldValueDTO v : values) {
			for(final UserDefinedFieldValueDTO t : template) {
				if (t.getUserDefinedFieldId().equals(v.getUserDefinedFieldId()) && t.getPosition().equals(v.getPosition())) {
					t.setBlobValue(v.getBlobValue());
					result.add(t);
					break;
				}
			}
		}

		return result;
	}

	private Collection<UserDefinedFieldValueDTO> getList(final LimitedUser user, final Long itemId, final List<UserDefinedFieldValueDTO> fields) {
		return injectValues(fields, loadValues(user, itemId, map(fields::stream, UserDefinedFieldValueDTO::getUserDefinedFieldId)));
	}

	/**
	 * We will eventually update the systems that this field refers to.
	 */
	private static void performExternalUpdate(final Long itemId, final Collection<UserDefinedField> fieldList, final List<UserDefinedFieldValueDTO> values) {
		logger.info(() -> "Attempting Auto-Update of External system for item " + itemId + ", field list size = " + fieldList.size() + ", value list size = " + values.size());
	}

	private static boolean supportsAutoExternalUpdate(final Collection<UserDefinedField> fieldList) {
		for(final UserDefinedField field : fieldList) {
			if (field.getName().equals("AutoUpdateURL")) {
				return true;
			}
		}
		return false;
	}

	private Void updateUserFields(final SharedItemUser user, final Long itemId, final UserDefinedTypeValueDTO dto) {
		logger.debug(() -> "Updating user fields for user " + user.getId() + ", user is " + (user.self(dto.getUserId()) ? "the caller" : "not the caller") + "(" + dto.getUserId() + ")");

		final Collection<UserDefinedField> fieldList = getTypeFields(dto.getUserDefinedTypeId());
		if (supportsAutoExternalUpdate(fieldList)) {
			performExternalUpdate(itemId, fieldList, dto.getFieldValues());
		}

		final Supplier<PublicKeyEncryptor> supplier = getPKESupplier(user);

		for(final UserDefinedFieldValueDTO f : user.self(dto.getUserId()) ? dto.getFieldValues() : getList(user, itemId, dto.getFieldValues())) {
			userDefinedFieldValueActions.update(user, f, supplier);
		}
		return null;
	}

	private Void updateSharedItem(final Long itemId, final UserDefinedTypeValueDTO dto) {
		logger.info(() -> "Item " + itemId + " is shared, we need to update shared values.");
		return processStream(() -> "updating shared item for user", () -> getUsersSharingItem(null, itemId), user -> updateUserFields(user, itemId, dto));
	}

	private boolean userGrantedPermission(final User caller, final Long itemId) {
		return itemAssignmentOperations.get(itemId, caller.getId()).getUpdatePermitted();
	}

	@Override
	public Void update(final User caller, final UserDefinedTypeValueDTO dto, final Long itemId, final Boolean shared) {
		final UserDefinedTypeValue udtval = getModel(dto.getId());

		if (!caller.self(udtval.getUserId()) && !userGrantedPermission(caller, itemId)) {
			throw new PermissionDeniedException("You do not own this item.");
		}

		// ensure data integrity and that the user is not attempting to covertly update fields not owned by them
		for(final UserDefinedFieldValueDTO fv : dto.getFieldValues()) {
			if (fv.getUserDefinedTypeValueId() == null) {
				fv.setUserDefinedTypeValueId(dto.getId());
			} else if (!fv.getUserDefinedTypeValueId().equals(dto.getId())) {
				throw new DataIntegrityException("A mis-match was detected between the Template ID and the Fields");
			}
		}

		dto.setUserId(udtval.getUserId()); // caller will typically not set, even if they did, do not trust them

		final Long localItemId = (itemId == null) ? getItemId(caller, udtval.getItemAssignmentId()) : itemId;
		final Boolean localShared = (shared == null) ? itemOperations.get(localItemId).getShared() : shared;

		return isTrue(localShared) ? updateSharedItem(localItemId, dto) : updateUserFields(caller, localItemId, dto);
	}
}
