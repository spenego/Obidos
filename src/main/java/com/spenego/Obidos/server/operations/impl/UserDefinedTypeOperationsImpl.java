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

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.UserDefinedTypeMapper;
import com.spenego.Obidos.server.model.UserDefinedType;
import com.spenego.Obidos.server.model.UserDefinedTypeExample;
import com.spenego.Obidos.server.operations.UserDefinedTypeOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class UserDefinedTypeOperationsImpl extends ObidosOperations<UserDefinedType> implements UserDefinedTypeOperations {
	private static final Logger	logger = LoggerFactory.getLogger(UserDefinedTypeOperationsImpl.class);

	@Autowired
	private final UserDefinedTypeMapper			userDefinedTypeMapper = null;

	@Override
	protected Logger getLogger()				{ return logger; }
	@Override
	protected UserDefinedTypeMapper getMapper()	{ return userDefinedTypeMapper; }
	@Override
	protected String getModelName()				{ return "userDefinedType"; }

	@Override
	public Long create(final UserDefinedType udt) {
		if (udt.getUserId() == null) {
			throw new ServerSideException("user id was not specified");
		}
		return udt.getId() != null ? super.create(udt) : createWithRandomID(udt);
	}

	private UserDefinedTypeExample createExample(final Long userId, final String search, final Boolean personal, final List<OrderBy> order) {
		// ad-hoc types are never desired in a list
		// we do not want to return the notes type, it's just confusing
		return createExample(() -> new UserDefinedTypeExample(c -> c.andAdHocEqualTo(false).andIdNotEqualTo(UserDefinedTypeDTO.NOTES_ID).andUserIdEqualTo(userId).andNameLike(search).andPersonalEqualTo(personal)), order);
	}

	@Override
	public Stream<UserDefinedType> getTypes(final Long userId, final String search, final Collection<Long> preSelectedTemplates, final Boolean personal, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return userDefinedTypeMapper.selectByExample(createExample(userId, search, personal, orderBy), createRowBounds(first, count)).stream();
	}

	@Override
	public Integer getUserDefinedTypeCount(final Long userId, final String search, final Collection<Long> preSelectedTemplates, final Boolean personal) {
		return countByExample(createExample(userId, search, personal, null));
	}

	@Override
	public Integer updateSelectiveForVersion(final Long id, final Integer version, final UserDefinedType t) {
		return (int) userDefinedTypeMapper.updateByExampleSelective(t, new UserDefinedTypeExample(c -> c.andVersionEqualTo(version).andIdEqualTo(id)));
	}
}
