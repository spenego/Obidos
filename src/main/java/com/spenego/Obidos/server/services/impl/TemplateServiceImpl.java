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

package com.spenego.Obidos.server.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.spenego.Obidos.client.rpc.TemplateService;
import com.spenego.Obidos.server.actions.UserDefinedTypeActions;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeResult;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class TemplateServiceImpl extends ObidosService implements TemplateService {
	private static final Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);

	@Autowired private final UserDefinedTypeActions userDefinedTypeActions = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	/**
	 * As a convenience, create a list of user defined fields when the user creates the type.
	 */
 	@Transactional @Override
	public Long create(final AuthCredsDTO creds, final UserDefinedTypeDTO udt) throws ServerSideException {
		return userFunction(creds, "create a template", "createUserDefinedType", user -> userDefinedTypeActions.create(user, udt, summonPWHash()));
	}

 	@Transactional(readOnly=true) @Override
	public UserDefinedTypeDTO getUserDefinedType(final AuthCredsDTO creds, final Long id) throws ServerSideException {
		return userFunction(creds, "get a template", "getUserDefinedType", user -> userDefinedTypeActions.get(user, id));
	}

 	@Transactional @Override
	public UserDefinedTypeDTO update(final AuthCredsDTO creds, final UserDefinedTypeDTO udt, final Boolean modifyGlobalTemplateInstances) throws ServerSideException {
		return userFunction(creds, "update a template", "update", user -> userDefinedTypeActions.update(user, udt, summonPWHash(), modifyGlobalTemplateInstances));
	}

 	@Transactional(readOnly=true) @Override
	public UserDefinedTypeResult getUserDefinedTypes(final AuthCredsDTO creds, final Boolean personal, final Long userId, final String search, List<Long> preSelectedTemplates, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
		return userFunction(creds, "get templates", "getUserDefinedTypes", user -> userDefinedTypeActions.getUserDefinedTypes(user, personal, userId, search, preSelectedTemplates, first, count, orderBy));
	}

 	@Transactional @Override
	public Void deleteUserDefinedType(final AuthCredsDTO creds, final List<Long> ids) throws ServerSideException {
		return userFunction(creds, "delete a template", "deleteUserDefinedType", user -> userDefinedTypeActions.delete(user, ids, Boolean.FALSE, summonPWHash()));
	}

 	@Transactional @Override
	public Long duplicateType(final AuthCredsDTO creds, final Long sourceTypeId, final String newTypeName) throws ServerSideException {
		return userFunction(creds, "duplicate a template", "duplicateType", user -> userDefinedTypeActions.duplicate(user, sourceTypeId, newTypeName));
	}

 	/*******************     T h e   C o d e   G r a v e y a r d     ********************/
 	/*
 	@Transactional
	@Override
	public Long create(final AuthCredsDTO creds, final Long type_id, final UserDefinedFieldDTO field) throws ServerSideException {
		return userFunction(creds, "create a template field", "createUserDefinedField", user -> userDefinedFieldActions.create(user, type_id, field, summonPWHash()));
	}

 	@Transactional(readOnly=true)
	@Override
	public UserDefinedFieldDTO getUserDefinedField(final AuthCredsDTO creds, final Long fieldId) throws ServerSideException {
		return userFunction(creds, "get a template field", "getUserDefinedField", user -> userDefinedFieldActions.get(fieldId));
	}

 	@Transactional
	@Override
	public Void updateUserDefinedField(final AuthCredsDTO creds, final UserDefinedFieldDTO field) throws ServerSideException {
		return userFunction(creds, "update a template field", "updateUserDefinedField", user -> userDefinedFieldActions.update(user, field, summonPWHash()));
	}

 	@Transactional
	@Override
	public Void deleteUserDefinedFields(final AuthCredsDTO creds, final ArrayList<Long> fieldIds) throws ServerSideException {
		return userFunction(creds, "delete a template field", "deleteUserDefinedField", user -> userDefinedFieldActions.delete(user, fieldIds, summonPWHash()));
	} */
}
