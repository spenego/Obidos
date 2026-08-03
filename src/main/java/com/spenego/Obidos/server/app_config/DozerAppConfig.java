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

package com.spenego.Obidos.server.app_config;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;

import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spenego.Obidos.server.utils.CustomDozerConverter;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;

@Configuration
public class DozerAppConfig {
	private static final Logger logger = LoggerFactory.getLogger(DozerAppConfig.class);

	@Autowired
	private static Map<String, CustomConverter> getCustomConverter() throws NoSuchAlgorithmException, NoSuchPaddingException {
		final Map<String, CustomConverter> cc = new HashMap<>();

		cc.put("ItemShareExpirationCustomConverter", new CustomDozerConverter());

		return cc;
	}

	private static List<String> getMappingFileUrls() {
		return Arrays.asList("dozerMapping.xml");
	}

	@Bean
	public DozerBeanMapper getDtoMapper() {
		final DozerBeanMapper dbm = new DozerBeanMapper();
		try {
			dbm.setCustomConvertersWithId(getCustomConverter());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			logger.error(() -> "Unable to create EncrypterConverter", e);
		}
		dbm.setMappingFiles(getMappingFileUrls());
		return dbm;
	}
}
