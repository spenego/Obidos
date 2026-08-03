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

package com.spenego.Obidos.client.rpc;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeResult;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * Templates allow users to define the type of data that they share.
 *
 * Templates are composed of User Defined Fields.  Each User Defined Field
 * is either a string, int, boolean, encrypted data or another User Defined Type.
 *
 * User Defined Fields are similar to columns in a database, just like each User
 * Defined Type is similar to a database table.
 *
 * @author mmorgan
 *
 */
public interface TemplateServiceAsync {
	/**
	 * Create a new UserDefinedType. You are able to specify the name and the fields of the type.
	 *
	 * @param creds
	 * @param udt You may specify the name of the type and all fields associated with this type.
	 * @return the new UserDefinedType id
	 * @throws ServerSideException
	 */
	void create(AuthCredsDTO creds, UserDefinedTypeDTO udt, AsyncCallback<Long> callback);

	/**
	 * Get a UserDefinedType.  All fields that are part of the type are returned as well.
	 *
	 * @param creds
	 * @param id
	 * @return the specified UserDefinedType
	 * @throws ServerSideException
	 */
	void getUserDefinedType(AuthCredsDTO creds, Long id, AsyncCallback<UserDefinedTypeDTO> callback);

	/**
	 * Update a UserDefinedType.
	 *
	 * @param creds
	 * @param udt You may change the name of the UserDefinedType or update fields.
	 * You must set the ID field when you intend on updating a field.
	 * If you wish to delete the field, you must set the ID and the delete flag.
	 * If you pass fields without an ID, a new field will be created.
	 * @throws ServerSideException
	 */
	void update(AuthCredsDTO creds, UserDefinedTypeDTO udt, Boolean modifyGlobalTemplateInstances, AsyncCallback<UserDefinedTypeDTO> callback);

	/**
	 * Get a list of users that match the specified search criteria. Users can currently search via
	 * username, full name or email address.
	 *
	 * @param creds
	 * @param personal If true, return only the personal templates. If false, return the global templates. If null, return all templates.
	 *                 Personal templates of other users are not returned.
	 * @param userId Search for types that are owned by userId. Pass null to not search via user.
	 * @param search Search for types that have a similar name.
	 * You can pass null if you do not wish to search and just get all types.
	 * @param preSelectedTemplates a list of templates to return in the result set.
	 * @param first Start the results with this offset.
	 * @param count Retrieve at most this many users.
	 * @param orderby Order the results by this field.
	 * @return A UserDefinedTypeResult object that contains a list of all matching types. The types do
	 * not contain the fields.
	 * @throws ServerSideException
	 */
	void getUserDefinedTypes(AuthCredsDTO creds, Boolean personal, Long userId, String search, List<Long> preSelectedTemplates, Integer first, Integer count, ArrayList<OrderBy> orderby, AsyncCallback<UserDefinedTypeResult> callback);

	/**
	 * Delete a template.
	 *
	 * @param creds
	 * @param id
	 * @throws ServerSideException
	 */
	void deleteUserDefinedType(AuthCredsDTO creds, List<Long> ids, AsyncCallback<Void> callback);

	/**
	 * Duplicates a User Defined Type. The type must either be global or a private type owned by the caller.
	 *
	 * @param creds
	 * @param sourceTypeId An id of a template.
	 * @param newTypeName Name the new type with this name.
	 * @throws ServerSideException
	 */
	void duplicateType(AuthCredsDTO creds, Long sourceTypeId, String newTypeName, AsyncCallback<Long> callback);
}
