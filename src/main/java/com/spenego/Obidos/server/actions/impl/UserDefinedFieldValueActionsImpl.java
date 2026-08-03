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

import static com.spenego.Obidos.server.model.Audit.DELETE_USER_DEFINED_FIELD;
import static com.spenego.Obidos.shared.ObidosConstants.ENCRYPTION_MODE_PADDED2;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.UserDefinedFieldValueActions;
import com.spenego.Obidos.server.model.Document;
import com.spenego.Obidos.server.model.LimitedUser;
import com.spenego.Obidos.server.model.SharedItemUser;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.model.UserDefinedBlob;
import com.spenego.Obidos.server.model.UserDefinedField;
import com.spenego.Obidos.server.model.UserDefinedFieldValue;
import com.spenego.Obidos.server.model.UserDefinedTypeValue;
import com.spenego.Obidos.server.operations.DocumentOperations;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.operations.UserDefinedBlobOperations;
import com.spenego.Obidos.server.operations.UserDefinedFieldOperations;
import com.spenego.Obidos.server.operations.UserDefinedFieldValueOperations;
import com.spenego.Obidos.server.operations.UserDefinedTypeValueOperations;
import com.spenego.Obidos.server.security.Encryption.PublicKeyDecryptor;
import com.spenego.Obidos.server.security.Encryption.PublicKeyEncryptor;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.server.utils.StreamSupplier;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.DocumentDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldValueDTO;
import com.spenego.Obidos.shared.exceptions.DuplicateRecordException;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class UserDefinedFieldValueActionsImpl extends CryptoActions<UserDefinedFieldValue> implements UserDefinedFieldValueActions {
	public UserDefinedFieldValueActionsImpl() {
		super(null, null);
	}

	private static final Logger logger = LoggerFactory.getLogger(UserDefinedFieldValueActionsImpl.class);

	@Autowired private final UserDefinedFieldValueOperations	userDefinedFieldValueOperations = null;
	@Autowired private final UserDefinedTypeValueOperations		userDefinedTypeValueOperations = null;
	@Autowired private final UserDefinedFieldOperations			userDefinedFieldOperations = null;
	@Autowired private final UserDefinedBlobOperations			userDefinedBlobOperations = null;
	@Autowired private final DocumentOperations					documentOperations = null;

	@Override
	protected final Operations<UserDefinedFieldValue> getOperations() {
		return userDefinedFieldValueOperations;
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
		return "user defined field value";
	}

	/**
	 * Creates a blob entry in the database for val. We store the blobs in a separate table
	 * for performance reasons since they can be arbitrarily large. The blobs are encrypted
	 * before they are stored.
	 *
	 * @param val the value to store.
	 * @param pkeSupplier
	 * @return
	 */
	private Long createBlobFieldValue(final byte[] val, final Supplier<PublicKeyEncryptor> pkeSupplier) {
		return val == null ? null : userDefinedBlobOperations.create(new UserDefinedBlob(pkeSupplier.get().encrypt(val), ENCRYPTION_MODE_PADDED2));
	}

	private UserDefinedTypeValue getUserDefinedTypeValue(final Long rowId) {
		return userDefinedTypeValueOperations.get(rowId);
	}

	private boolean rowOwner(final LimitedUser user, final Long rowId) throws NoSuchRecordException, PermissionDeniedException {
		return getUserDefinedTypeValue(rowId).ownerIs(user);
	}

	private UserDefinedFieldValue setUDFValue(final UserDefinedFieldValue udfVal, final byte[] value, final Supplier<PublicKeyEncryptor> pkeSupplier) {
		udfVal.setBlobValueId(createBlobFieldValue(value, pkeSupplier));
		return udfVal;
	}

	@Override
	public Long createUDFValue(final UserDefinedFieldValue udfVal, final byte[] value, final Supplier<PublicKeyEncryptor> pkeSupplier) {
		return userDefinedFieldValueOperations.create(setUDFValue(udfVal, value, pkeSupplier));
	}

	private static UserDefinedField findField(final Collection<UserDefinedField> fields, final Long id) {
		for(final UserDefinedField f:fields) { if (id.equals(f.getId())) { return f; }}
		throw new ServerSideException("Field ID " + id + " does not exist for this type.");
	}

	private static Document createDocument(final DocumentDTO dto, final Supplier<PublicKeyEncryptor> pkeSupplier) {
		final PublicKeyEncryptor pke = pkeSupplier.get();
		return new Document(null, pke.encrypt(dto.getFilename()), dto.getGuid(), pke.encrypt(dto.getDecryptionKey()), dto.getPublicKey(), pke.encrypt(dto.getFileLength()), pke.encrypt(dto.getCompressedFileLength()));
	}

	private Long createDocumentUDFValue(final Supplier<PublicKeyEncryptor> pkeSupplier, final UserDefinedFieldValueDTO value, final UserDefinedFieldValue udfVal) {
		if (udfVal.getDocument() == null) {
			udfVal.setDocument(new DocumentDTO());
			value.setDocument(udfVal.getDocument()); // save Document for subsequent calls for shared Items
		}

		final DocumentDTO d = udfVal.getDocument();
		if (d.getGuid() == null) {
			d.setGuid(DocumentDTO.UPLOAD_PENDING + ServerUtils.randomString());
		}
		udfVal.setDocumentId(documentOperations.create(createDocument(d, pkeSupplier)));

		return userDefinedFieldValueOperations.create(udfVal);
	}

	private Long create(final Long rowId, final Collection<UserDefinedField> fields, final Supplier<PublicKeyEncryptor> pkeSupplier, final UserDefinedFieldValueDTO value) {
		final UserDefinedFieldValue udfVal = convert(value, UserDefinedFieldValue.class);
		final UserDefinedField field = findField(fields, value.getUserDefinedFieldId());

		udfVal.setUserDefinedFieldId(field.getId());
		udfVal.setUserDefinedTypeValueId(rowId);

		return (field.getType().equals(UserDefinedFieldDTO.TYPE_DOCUMENT)) ? createDocumentUDFValue(pkeSupplier, value, udfVal) : createUDFValue(udfVal, value.getBlobValue(), pkeSupplier);
	}

	private static byte[] converted(final PublicKeyDecryptor pkd, final byte[] val) {
		return val == null ? null : pkd.decrypt(val, ENCRYPTION_MODE_PADDED2);
	}

	/**
	 * A UserDefinedFieldValue can refer to either a blob (an encrypted blob of data), or a Document (used to store files).  This method decrypts the appropriate
	 * values so the data can be shared.
	 *
	 * @param creator
	 * @param list
	 * @param pkd
	 * @return
	 */
	private List<UserDefinedFieldValue> decrypt(final List<UserDefinedFieldValue> list, final  Supplier<PublicKeyDecryptor> pkdSupplier) {
		for(final UserDefinedFieldValue v : list) {
			if (!v.isDecrypted()) {
				if (v.getBlobValue() != null) {
					v.setBlobValue(pkdSupplier.get().decrypt(v.getBlobValue(), v.getEncryptionMode()));
					v.setDecrypted(true);
				} else if (v.getDocumentId() != null) {
					final DocumentDTO dto = convert(documentOperations.get(v.getDocumentId()), DocumentDTO.class);
					final PublicKeyDecryptor pkd = pkdSupplier.get();
					dto.setDecryptionKey(converted(pkd, dto.getDecryptionKey()));
					dto.setFileLength(converted(pkd, dto.getFileLength()));
					dto.setCompressedFileLength(converted(pkd, dto.getCompressedFileLength()));
					dto.setFilename(converted(pkd, dto.getFilename()));
					v.setDocument(dto);
					v.setDecrypted(true);
				}
			}
		}
		return list;
	}

	@Override
	public Collection<UserDefinedFieldValueDTO> getUDFValues(final User creator, final Long userDefinedType, final Supplier<PublicKeyDecryptor> decryptor, final List<OrderBy> orderby) {
		return convert(StreamSupplier.create(() -> decrypt(userDefinedFieldValueOperations.getUserDefinedFieldValues(userDefinedType, orderby), decryptor)), UserDefinedFieldValueDTO.class);
	}

	private final Collection<UserDefinedField> getFields(final Long userDefinedTypeValueId) {
		return userDefinedFieldOperations.getTypeFields(getUserDefinedTypeValue(userDefinedTypeValueId).getUserDefinedTypeId());
	}

	@Override
	public Void create(final SharedItemUser creator, final List<UserDefinedFieldValueDTO> values, final Long rowId) {
		if (values.get(0).getRowId() != null) {
			logger.error(() -> "Usage error. Caller passed in UserDefinedFieldValueDTO values populated with a row ID (they already exist in DB)");
			throw new DuplicateRecordException("You may not create a row that already exists.");
		}

		final Supplier<PublicKeyEncryptor> supplier = getPKESupplier(creator);

		return processStream(() -> "Creating " + values.size() + " user defined fields for type value " + rowId + " for " + creator, values::stream, f -> create(rowId, getFields(rowId), supplier, f), ce -> exceptionLoggerThrower("creating field value", ce));
	}

	@Override
	public Long addUDFValue(final User creator, final UserDefinedFieldValueDTO value, final Long userDefinedTypeValueId) {
		if (!rowOwner(creator, value.getRowId())) {
			throw new PermissionDeniedException("You do not own this row.");
		}

		return create(value.getRowId(), getFields(userDefinedTypeValueId), getPKESupplier(creator), value);
	}

	/**
	 * You must have already ensured that the caller owns the fields you are passing in.
	 */
	@Override
	public Void update(final LimitedUser creator, final UserDefinedFieldValueDTO value, final Supplier<PublicKeyEncryptor> pkeSupplier) {
		return userDefinedFieldValueOperations.updateSelective(setUDFValue(convert(value, UserDefinedFieldValue.class), value.getBlobValue(), pkeSupplier));
	}
}
