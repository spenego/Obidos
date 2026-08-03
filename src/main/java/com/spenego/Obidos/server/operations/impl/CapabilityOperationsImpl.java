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

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.CapabilityMapper;
import com.spenego.Obidos.server.dao.Mapper;
import com.spenego.Obidos.server.model.Capability;
import com.spenego.Obidos.server.model.CapabilityExample;
import com.spenego.Obidos.server.operations.CapabilityOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public final class CapabilityOperationsImpl extends ObidosOperations<Capability> implements CapabilityOperations {
	private static final Logger	logger = LoggerFactory.getLogger(CapabilityOperationsImpl.class);

	@Autowired
	private final CapabilityMapper mapper = null;

	@Override
	protected Logger getLogger() { return logger; }
	@Override
	protected Mapper<Capability> getMapper() { return mapper; }
	@Override
	protected String getModelName() { return "Capability"; }

	@Override
	public Capability getByUserId(final Long userId) {
		final List<Capability> capabilities = mapper.selectByExample(new CapabilityExample(c -> c.andUserIdEqualTo(userId)));
		if (capabilities.size() != 1) {
			logger.warn(() -> "User " + userId + " has " + capabilities.size() + " capabilities.");
			throw new NoSuchRecordException("Admin has no capabilities.");
		}
		return capabilities.get(0);
	}
}
