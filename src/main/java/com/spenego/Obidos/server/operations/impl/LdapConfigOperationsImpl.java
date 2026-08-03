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

package com.spenego.Obidos.server.operations.impl;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.LdapMapper;
import com.spenego.Obidos.server.model.Ldap;
import com.spenego.Obidos.server.model.LdapExample;
import com.spenego.Obidos.server.operations.LdapConfigOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.exceptions.LdapNotConfiguredException;

public final class LdapConfigOperationsImpl extends ObidosOperations<Ldap> implements LdapConfigOperations {
	private static final Logger logger = LoggerFactory.getLogger(LdapConfigOperationsImpl.class);

	@Autowired
	private final LdapMapper ldapMapper = null;

	@Override
	protected Logger getLogger()		{ return logger; }
	@Override
	protected LdapMapper getMapper()	{ return ldapMapper; }
	@Override
	protected String getModelName()		{ return "user"; }


	@Override
	public Ldap get(final String name) throws LdapNotConfiguredException {
		final List<Ldap> list = ldapMapper.selectByExample(new LdapExample(c -> c.andNameEqualTo(name)));
		if (!list.isEmpty()) {
			return list.get(0);
		}
		throw new LdapNotConfiguredException("LDAP is not yet configured.");
	}

	private static LdapExample createExample(final String searchString) {
		return (searchString == null) ? null : new LdapExample(c -> c.andNameLike(searchString));
	}

	@Override
	public Stream<Ldap> getStream(final String searchString, final Integer first, final Integer count) {
		return ldapMapper.selectByExample(createExample(searchString), createRowBounds(first, count)).stream();
	}

	@Override
	public Integer getCount(String searchString) {
		return (int) ldapMapper.countByExample(createExample(searchString));
	}
}
