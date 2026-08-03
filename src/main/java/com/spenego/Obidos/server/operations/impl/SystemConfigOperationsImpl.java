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

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.Mapper;
import com.spenego.Obidos.server.dao.SystemConfigMapper;
import com.spenego.Obidos.server.model.SystemConfig;
import com.spenego.Obidos.server.operations.SystemConfigOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;

public final class SystemConfigOperationsImpl extends ObidosOperations<SystemConfig> implements SystemConfigOperations {
	private static final Logger	logger = LoggerFactory.getLogger(SystemConfigOperationsImpl.class);

	@Autowired
	private final SystemConfigMapper	systemConfigMapper = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected Mapper<SystemConfig> getMapper() {
		return systemConfigMapper;
	}

	@Override
	protected String getModelName() {
		return "SystemConfig";
	}
}
