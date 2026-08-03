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

package com.spenego.Obidos.server.actions;

import java.util.Collection;

import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.shared.dto.LdapConfigurationResult;
import com.spenego.Obidos.shared.dto.LdapDTO;
import com.spenego.Obidos.shared.exceptions.LdapNotConfiguredException;
import com.spenego.Obidos.shared.exceptions.RecordModifiedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public interface LdapConfigActions {
	LdapDTO get(String name) throws LdapNotConfiguredException, ServerSideException;
	LdapDTO get(Long id) throws LdapNotConfiguredException, ServerSideException;
	Long create(User admin, LdapDTO config) throws RecordModifiedException, ServerSideException;
	Void update(User admin, LdapDTO config) throws RecordModifiedException, ServerSideException;

	/**
	 * Attempts to connect to the specified LDAP server.
	 *
	 * @param config
	 * @return true if the server can connect to the LDAP server, false otherwise.
	 */
	Boolean testConnection(Long id);

	/**
	 *
	 * @param ldapDTO
	 * @return true, throws exception if connectin fails
	 * <p>
	 * @author spgdev@spenego.com - Apr 16, 2017
	 */
	Void testConnection(LdapDTO ldapDTO) throws ServerSideException;
	Void testAuthentication(LdapDTO ldapDTO, String username, String password) throws ServerSideException;

	/**
	 * Get a list of LDAP configurations that have a name that contains the search string.
	 *
	 * @param searchString
	 * @param first
	 * @param count
	 * @return
	 */
	LdapConfigurationResult getAllConfigs(User admin, String searchString, Integer first, Integer count);

	Void delete(User admin, Collection<Long> ldapConfigIDs);
}
