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

import static com.spenego.Obidos.server.model.Audit.CREATE_USER_DEFINED_TYPE;
import static com.spenego.Obidos.server.model.Audit.DELETE_USER_DEFINED_TYPE;
import static com.spenego.Obidos.server.model.Audit.UPDATE_USER_DEFINED_TYPE;
import static com.spenego.Obidos.server.utils.ServerUtils.getUuidString;
import static com.spenego.Obidos.shared.dto.UserDefinedTypeDTO.MAX_SPENEGO_DEFINED_TYPE;
import static java.lang.Boolean.FALSE;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.LoginActions;
import com.spenego.Obidos.server.actions.UserDefinedFieldActions;
import com.spenego.Obidos.server.actions.UserDefinedTypeActions;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.model.UserDefinedField;
import com.spenego.Obidos.server.model.UserDefinedType;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.operations.UserDefinedFieldOperations;
import com.spenego.Obidos.server.operations.UserDefinedTypeOperations;
import com.spenego.Obidos.server.operations.UserDefinedTypeValueOperations;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.UserDefinedFieldDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeResult;
import com.spenego.Obidos.shared.exceptions.DuplicateRecordException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;


public final class UserDefinedTypeActionsImpl extends CryptoActions<UserDefinedType> implements UserDefinedTypeActions {
	private static final Logger logger = LoggerFactory.getLogger(UserDefinedTypeActionsImpl.class);

	@Autowired private final LoginActions						loginActions = null;
	@Autowired private final UserDefinedTypeOperations			userDefinedTypeOperations = null;
	@Autowired private final UserDefinedFieldActions			userDefinedFieldActions = null;
	@Autowired private final UserDefinedFieldOperations			userDefinedFieldOperations = null;
	@Autowired private final UserDefinedTypeValueOperations		userDefinedTypeValueOperations = null;

	@Override
	protected final Operations<UserDefinedType> getOperations() {
		return userDefinedTypeOperations;
	}

	@Override
	protected final Logger getLogger() {
		return logger;
	}

	@Override
	protected final Integer getAuditDeleteAction() {
		return DELETE_USER_DEFINED_TYPE;
	}

	@Override
	protected final String elementName() {
		return "template";
	}

	public UserDefinedTypeActionsImpl() {
		super(null, null);
	}

	public UserDefinedTypeActionsImpl(final String propertiesFilename, final String name) {
		super(propertiesFilename, name);
	}

	private Long createUserDefinedField(final User caller, final Long id, final UserDefinedFieldDTO field, final boolean isAdHoc) {
		return userDefinedFieldActions.create(id, field, caller.getId(), caller.getUsername(), isAdHoc);
	}

	private Long createNewField(final User caller, final Long id, final UserDefinedFieldDTO field, final boolean isAdHoc) {
		field.setTypeId(id);
		return createUserDefinedField(caller, id, field, isAdHoc);
	}

	private static ServerSideException genException(final UserDefinedFieldDTO field, final String msg) {
		return new ServerSideException("Position for field " + field.getName() + msg);
	}

	private void createFields(final User caller, final UserDefinedTypeDTO dto, final Long typeId, final boolean isAdHoc) {
		final int fieldCount = dto.getFields().size();
		final HashSet<Integer> positionSet = new HashSet<>(fieldCount);

		for(final UserDefinedFieldDTO field : dto.getFields()) {
			final Integer position = field.getPosition();

			if (position == null)				{ throw genException(field, " was not specified. Fields must have a unique position."); }
			if (positionSet.contains(position))	{ throw genException(field, " was duplicated. Fields must have a unique position."); }
			final int pos = position;
			if (pos < 0 || pos > fieldCount)	{ throw genException(field, " is invalid."); }
			positionSet.add(position);
		}

		processStream(() -> caller.getFullname() + " is creating fields", () -> dto.getFields().stream(), field -> createNewField(caller, typeId, field, isAdHoc), ce -> exceptionLoggerThrower("creating new field", ce));
	}

	private boolean licensePermitsQRCodeValues() {
		return loginActions.currentLicenseStats().getSupportQRCodeUpload();
	}

	private boolean licensePermitsDocumentValues() {
		return loginActions.currentLicenseStats().getSupportsDocumentUpload();
	}

	private void checkIfLicenseSupportsFieldTypes(final UserDefinedTypeDTO template) {
		if (!licensePermitsQRCodeValues() && template.containsQRCode()) {
			throw new ServerSideException("Your license does not permit QR Code fields.");
		}

		if (!licensePermitsDocumentValues() && template.containsDocument()) {
			throw new ServerSideException("Your license does not permit Document fields.");
		}
	}

	private static void ensureCallerCanActionGlobalTemplate(final User caller, final String action) {
		if (!caller.getCreateGlobalTemplates()) {
			throw new PermissionDeniedException("You do not have sufficient privelge to " + action + " a global template.");
		}
	}

	private void auditTemplateCreate(final User caller, final UserDefinedTypeDTO template, final Long id) {
		final Long callerId = caller.getId();

	    if ( template.getPersonal() != null && template.getPersonal() )
	    {
	    // TODO: CREATE_USER_DEFINED_TYPE has to be changed to CREATE_PERSONAL_TEMPLATE
	        audit(CREATE_USER_DEFINED_TYPE, caller.getUsername(), callerId, template, id);
	    }
	    else
	    {
	    // TODO: CREATE_USER_DEFINED_TYPE has to be changed to CREATE_GLOBAL_TEMPLATE
	        audit(CREATE_USER_DEFINED_TYPE, caller.getUsername(), callerId, template, id);
	    }
	}

	private Long create(final User caller, final UserDefinedTypeDTO template) {
		final Long callerId = caller.getId();

		if (template.getPersonal() == null || !template.getPersonal()) {
			ensureCallerCanActionGlobalTemplate(caller, "create");
		}

		checkIfLicenseSupportsFieldTypes(template);

		final Long id = userDefinedTypeOperations.create(new UserDefinedType(callerId, template.getName(), template.getPersonal(), template.getAdHoc()));
		final boolean isAdHoc = template.getAdHoc() != null && template.getAdHoc();

		if (!isAdHoc) {
			auditTemplateCreate(caller, template, id);
		}

		if (template.getFields() != null && !template.getFields().isEmpty()) {
			createFields(caller, template, id, isAdHoc);
		}

		return id;
	}

	@Override
	public Long create(final User caller, final UserDefinedTypeDTO dto, final PassphraseHash passphraseHash) {
		if (dto == null) {
			throw new ServerSideException("User Defined Type may not be null");
		}

		validatePassphraseHash(caller, passphraseHash); // ensures user has active public key as an additional level of authentication

		return create(caller, dto);
	}

	private static String getAdHocTypeName() {
		return getUuidString();
	}

	@Override
	public Long createAdHoc(final User caller, final UserDefinedTypeDTO template, final PassphraseHash passphraseHash) {
		if (template == null) {
			throw new ServerSideException("User Defined Type may not be null");
		}

		validatePassphraseHash(caller, passphraseHash); // ensures user has active public key as an additional level of authentication

		template.setAdHoc(true);
		template.setName(getAdHocTypeName());

		for(int i=0; i < 5; i++) {
			try {
				return create(caller, template);
			} catch(final DuplicateRecordException ex) {
				template.setName(getAdHocTypeName());
			}
		}
		throw new ServerSideException("Unable to create unique name for ad-hoc type");
	}

	@Override
	public UserDefinedTypeDTO get(final User caller, final Long id) {
		final UserDefinedTypeDTO udt = convert(getModel(id), UserDefinedTypeDTO.class);
		final Collection<UserDefinedField> fields = userDefinedFieldOperations.getTypeFields(id);
		if (fields != null) {
			fields.stream().forEach(field -> udt.addField(convert(field, UserDefinedFieldDTO.class)));
		}
		return udt;
	}

	private int referencesToTemplate(final Long typeId, final Long userId) {
		return userDefinedTypeValueOperations.numberOfItemReferencesUsingType(typeId, userId);
	}

	private Long createDuplicateField(final UserDefinedField udf, final Long typeId) {
		udf.setId(null);
		udf.setTypeId(typeId);
		return userDefinedFieldOperations.create(udf);
	}

	private void createDuplicateFields(final Long newUserDefinedTypeId, final List<UserDefinedFieldDTO> updates, final UserDefinedField udf) {
		if (updates != null) {
			for(final UserDefinedFieldDTO field : updates) {
				if (udf.getId().equals(field.getId())) {
					if (isTrue(field.getDelete())) {
						field.setId(null); // sneaky hack here. This tells update to ignore the delete (since we never added the field during the duplicate).
					} else {
						field.setTypeId(newUserDefinedTypeId);
						field.setId(createDuplicateField(udf, newUserDefinedTypeId));
					}
					return;
				}
			}
		}
		createDuplicateField(udf, newUserDefinedTypeId);
	}

	// To avoid unnecessary work, if a field is set for deletion (getDelete is true), this method will set the id on the field to null.
	// This way we can avoid duplicating the field and then deleting it in the update method. To indicate to the update method that
	// this field is special, the id = null (an add) and getDelete is true (a delete), so we just skip the entry.
	private Long duplicateFields(final Long currentUserDefinedTypeId, final Long newUserDefinedTypeId, final List<UserDefinedFieldDTO> updates) {
		userDefinedFieldOperations.getTypeFields(currentUserDefinedTypeId).forEach(udf -> createDuplicateFields(newUserDefinedTypeId, updates, udf));
		return newUserDefinedTypeId;
	}

	@Override
	public Long duplicate(final User caller, final Long typeId, final String newName) {
		final UserDefinedType udt = getModel(typeId);

		if (isTrue(udt.getPersonal()) && !caller.self(udt.getUserId())) {
			throw new PermissionDeniedException("You do not have access to this Template");
		}

		if (newName == null || newName.isEmpty()) {
			throw new ServerSideException("You must specify a template name.");
		}

		try {
			logger.info(() -> "Creating new template: " + newName);

			final Long newTypeId = userDefinedTypeOperations.create(new UserDefinedType(caller.getId(), newName));
			logger.info(() -> "Creating fields for template ");
			return duplicateFields(typeId, newTypeId, null); // need to duplicate UserDefinedFields and the fields for update in the dto
		} catch(final DuplicateRecordException ex) {
			throw new DuplicateRecordException("There is already a template named " + newName + ".");
		}
	}

	/*
	private static boolean fieldsAreIdentical(final UserDefinedField orig, final UserDefinedFieldDTO mod) {
		return (mod.getName() == null || orig.getName().equals(mod.getName())) &&
					(mod.getType() == null || orig.getType().equals(mod.getType())) &&
					(mod.getPosition() == null || orig.getPosition().equals(mod.getPosition()));
	}

	private final class PkeSupplierCache {
		private final Map<Long, Supplier<PublicKeyEncryptor>> cache;

		PkeSupplierCache(Long userId, Supplier<PublicKeyEncryptor> supplier) {
			cache = new HashMap<>();
			cache.put(userId, supplier);
		}

		Supplier<PublicKeyEncryptor> get(final Long userId) {
			return cache.computeIfAbsent(userId, u -> getPKESupplier(getUser(u)));
		}
	}
	*/

	@Override
	public UserDefinedTypeDTO update(final User caller, final UserDefinedTypeDTO dto, final PassphraseHash passphraseHash, final Boolean modifyGlobalTemplateInstances) {
		final Long typeId = dto.getId(); // This is the id of the original Template

		if (typeId == null) {
			throw new ServerSideException("You must specify the id of the User Defined Type to update.");
		}

		checkIfLicenseSupportsFieldTypes(dto);

		logger.info(() -> "modifyGlobalTemplateInstances = " + modifyGlobalTemplateInstances);

		final UserDefinedType udt = getModel(typeId); // This is the original Template

		final boolean globalTemplate = Boolean.TRUE.equals(udt.getGlobal());

		if (typeId <= MAX_SPENEGO_DEFINED_TYPE) {
			throw new ServerSideException("You may not modify Spenego supplied types.");
		}

		if (globalTemplate) {  // even if they own the template they could be denied access (gives us ability to lock a user out)
		    logger.info(() -> "Yes, this is global template");
			ensureCallerCanActionGlobalTemplate(caller, "update");
			logger.info(() -> "User " + caller.getId() + " is modifying Global Template " + typeId);
		}

		if ( referencesToTemplate(typeId, null) == 0) {
			getOperations().delete(typeId);
		} else {
			udt.setAdHoc(true);
			udt.setPersonal(false);
			udt.setGlobal(false);
			udt.setName(getUuidString());
			getOperations().update(udt);
		}

		/* When a new Template is created using a DTO, the DTO must not have an Id.
		 * The same goes to the individual fields.
		 * So we clear these fields before calling create()
		 */
	    dto.setId(null);  // clear Id of modified Template
	    for ( UserDefinedFieldDTO item : dto.getFields()) {
	         item.setId(null);	// clear id of each field
	    }

	    final Long newId = create(caller, dto); // Whenever a template is updated, it is not updated. A new template is created.

		audit(UPDATE_USER_DEFINED_TYPE, caller.getUsername(), caller.getId(), dto, newId);

		return get(caller, newId);
	}

	@Override
	public UserDefinedTypeResult getUserDefinedTypes(final User caller, final Boolean personal, final Long userId, final String search, final Collection<Long> preSelectedTemplates, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		final Boolean zpersonal = (userId == null || !caller.self(userId)) ? FALSE : personal; // ensure that the users can only see global templates of other users

		return new UserDefinedTypeResult(first, count, preSelectedTemplates, (a,b) -> convert(a,b),
				() -> userDefinedTypeOperations.getUserDefinedTypeCount(userId, search, preSelectedTemplates, zpersonal),
				() -> userDefinedTypeOperations.getTypes(userId, search, preSelectedTemplates, zpersonal, first, count, orderBy));
	}

	private Void delete(final User caller, final Long id, final Boolean deleteReferringItems) {
		final UserDefinedType udt = getModel(id);

		if (udt.getGlobal() != null && udt.getGlobal()) {  // even if they own the template they could be denied access (gives us ability to lock a user out)
			ensureCallerCanActionGlobalTemplate(caller, "delete");
		} else if (!caller.self(udt.getUserId())) {
			throw new PermissionDeniedException("You do not own this template.");
		}

		final Long callerId = caller.getId();

		if (deleteReferringItems != null && deleteReferringItems) {
			userDefinedTypeValueOperations.deleteItemReferencesUsingType(id, callerId); // we can not use DB cascading since item_assignments are independent
		}

		final int referenceCount = referencesToTemplate(id, deleteReferringItems ? callerId : null);

		// If user attempts to delete a personal template that still has items using it, just rename the type
		// and mark it as ad-hoc.
		if (referenceCount != 0) { // Bug #698 - do this for global too && udt.getState().equals(UserDefinedType.State.Private)) { // just rename
			udt.setPersonal(false);
			udt.setAdHoc(true);
			//udt.setName("ZZZDeleted-" + caller.getId() + "-" + nextRandomInt(100000));
			udt.setName(getAdHocTypeName());
			getOperations().update(udt);
			audit(UPDATE_USER_DEFINED_TYPE, caller.getUsername(), caller.getId(), udt, udt.getId());

			return null;
		}

		audit(getAuditDeleteAction(), caller.getUsername(), caller.getId(), udt, id);

		// Bug #698 - do not clone global templates when deleted
		return getOperations().delete(id);
	}

	@Override
	public Void delete(final User caller, final Collection<Long> ids, final Boolean deleteReferringItems, final PassphraseHash passphraseHash) {
		validatePassphraseHash(caller, passphraseHash); // ensures user has active public key as an additional level of authentication
		ids.forEach(id -> delete(caller, id, deleteReferringItems));
		return null;
	}
}
