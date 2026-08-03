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

package com.spenego.Obidos.server.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import com.spenego.Obidos.shared.ObidosConstants;
import com.sun.jna.Platform;

/**
 * Singleton class for obidos properties file Issue #624
 *
 * @author spgdev@spenego.com - Feb 4, 2020
 */
public class ObidosConfiguration {
	private String path = null;
	private PropertiesConfiguration propConfig = null;
	private static ObidosConfiguration singleInstance = null;

	private static String getPath() {
		return Platform.isWindows() ? ObidosConstants.CONFIG_FILE_PATH_WINDOWS : ObidosConstants.CONFIG_FILE_PATH;
	}

	private ObidosConfiguration() throws ConfigurationException {
		path = getPath();
		propConfig = new PropertiesConfiguration();
		propConfig.load(path);
		propConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
	}

	public static ObidosConfiguration getInstance() throws ConfigurationException {
		if (singleInstance == null) {
			singleInstance = new ObidosConfiguration();
		}
		return singleInstance;
	}

	public PropertiesConfiguration getPropConfig() {
		return propConfig;
	}
}
