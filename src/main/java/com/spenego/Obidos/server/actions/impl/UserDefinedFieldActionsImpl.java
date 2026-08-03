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

import static com.spenego.Obidos.server.model.Audit.CREATE_USER_DEFINED_FIELD;
import static com.spenego.Obidos.server.model.Audit.DELETE_USER_DEFINED_FIELD;
import static com.spenego.Obidos.server.model.Audit.UPDATE_USER_DEFINED_FIELD;
import static com.spenego.Obidos.shared.dto.UserDefinedFieldDTO.TYPE_DOCUMENT;
import static com.spenego.Obidos.shared.dto.UserDefinedFieldDTO.TYPE_ENCRYPTED;
import static com.spenego.Obidos.shared.dto.UserDefinedFieldDTO.TYPE_QRCODE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.UserDefinedFieldActions;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.model.UserDefinedField;
import com.spenego.Obidos.server.model.UserDefinedType;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.operations.UserDefinedFieldOperations;
import com.spenego.Obidos.server.operations.UserDefinedTypeOperations;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.UserDefinedFieldDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldResult;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;

public final class UserDefinedFieldActionsImpl extends CryptoActions<UserDefinedField> implements UserDefinedFieldActions {
	private static final Logger logger = LoggerFactory.getLogger(UserDefinedFieldActionsImpl.class);

	@Autowired private final UserDefinedFieldOperations	userDefinedFieldOperations = null;
	@Autowired private final UserDefinedTypeOperations		userDefinedTypeOperations = null;

	@Override
	protected final Operations<UserDefinedField> getOperations() {
		return userDefinedFieldOperations;
	}

	@Override
	protected final Logger getLogger() {
		return logger;
	}

	@Override
	protected final Integer getAuditDeleteAction() {
		return DELETE_USER_DEFINED_FIELD;
	}

	@Override
	protected final String elementName() {
		return "user defined field";
	}

	public UserDefinedFieldActionsImpl() {
		super(null, null);
	}

	public UserDefinedFieldActionsImpl(final String propertiesFilename, final String name) {
		super(propertiesFilename, name);
	}

	private UserDefinedType getUserDefinedType(final Long userDefinedTypeId) {
		return userDefinedTypeOperations.get(userDefinedTypeId);
	}

	private String getTypeName(final Long userDefinedTypeId) {
		return getUserDefinedType(userDefinedTypeId).getName();
	}

	@Override
	public Long create(final Long typeId, final UserDefinedFieldDTO dto, final Long userId, final String username, final boolean isAdHoc) {
		final long type = dto.getType();

		if (type != TYPE_ENCRYPTED && type != TYPE_DOCUMENT && type != TYPE_QRCODE) {
			throw new PermissionDeniedException("Currently, only encrypted, document, or qr-code types are supported.");
		}

		final Long id = userDefinedFieldOperations.create(new UserDefinedField(dto.getId(), typeId, dto.getType(), dto.getName(), dto.getPosition()));

		if (!isAdHoc) {
			audit(CREATE_USER_DEFINED_FIELD, username, userId, dto.getName(), id, null, getTypeName(dto.getTypeId()));
		}

		return id;
	}

	private static void checkPermission(final User caller, final UserDefinedType udt) throws PermissionDeniedException {
		if (!udt.ownerIs(caller)) {
			throw new PermissionDeniedException("You do not own that type.");
		}
	}

	private Long commonCreate(final User caller, final Long typeId, final UserDefinedFieldDTO dto, final PassphraseHash passphraseHash, final boolean isAdHoc) {
		validatePassphraseHash(caller, passphraseHash); // ensures user has active public key as an additional level of authentication
		checkPermission(caller, getUserDefinedType(typeId));

		return create(typeId, dto, caller.getId(), caller.getUsername(), isAdHoc);
	}

	@Override
	public Long create(final User caller, final UserDefinedFieldDTO dto, final PassphraseHash passphraseHash, final boolean isAdHoc) {
		return commonCreate(caller, dto.getTypeId(), dto, passphraseHash, isAdHoc);
	}

	@Override
	public Long create(final User caller, final Long typeId, final UserDefinedFieldDTO dto, final PassphraseHash passphraseHash, final boolean isAdHoc) {
		return commonCreate(caller, typeId, dto, passphraseHash, isAdHoc);
	}

	@Override
	public Long clone(final User caller, final Long fieldId, final Long typeId) {
		final UserDefinedFieldDTO f = get(fieldId);
		f.setType(typeId);
		f.setId(null);

		return create(caller, f, null, true);
	}

	@Override
	public UserDefinedFieldResult getUserDefinedFields(final Long userId, final String search, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return new UserDefinedFieldResult(first, count, null, (a,b) -> convert(a,b),
				() -> userDefinedFieldOperations.getUserDefinedFieldCount(userId, search),
				() -> userDefinedFieldOperations.getTypeFields(userId, search, first, count, orderBy).stream());
	}

	@Override
	public UserDefinedFieldDTO get(final Long id) throws NoSuchRecordException {
		return convert(getModel(id), UserDefinedFieldDTO.class);
	}

	@Override
	public Void delete(final Long id, final String objName, final Long userId, final String username) {
		audit(DELETE_USER_DEFINED_FIELD, username, userId, (objName == null) ? getModel(id).getName() : objName, id);
		return userDefinedFieldOperations.delete(id);
	}

	@Override
	// Field Values are deleted via database foreign key cascade
	public Void delete(final User caller, final Long id) {
		final UserDefinedFieldDTO udf = get(id);
		checkPermission(caller, getUserDefinedType(udf.getTypeId()));

		return delete(id, udf.getName(), caller.getId(), caller.getUsername());
	}

	@Override
	public Void update(final UserDefinedFieldDTO dto, final Long userId, final String username) {
		audit(UPDATE_USER_DEFINED_FIELD, username, userId, dto, dto.getId());
		return userDefinedFieldOperations.updateSelective(new UserDefinedField(dto.getId(), null, dto.getType(), dto.getName(), dto.getPosition()));
	}

	@Override
	public Void update(final User caller, final UserDefinedFieldDTO dto, final PassphraseHash passphraseHash) {
		final UserDefinedFieldDTO udf = get(dto.getId());
		checkPermission(caller, getUserDefinedType(udf.getTypeId()));

		return update(dto, caller.getId(), caller.getUsername());
	}
}
