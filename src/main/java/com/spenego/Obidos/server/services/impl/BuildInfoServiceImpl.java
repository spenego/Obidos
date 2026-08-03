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

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.spenego.Obidos.client.rpc.BuildInfoService;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.dto.BuildInfoDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@Service("buildInfoService")
public final class BuildInfoServiceImpl implements BuildInfoService {
	private String contentsOf(final String dirname, final String filename) throws IOException {
		return ServerUtils.readTextFile(getClass(), dirname, filename);
	}

	private static Properties getProperties(final String filename) throws IOException {
		try (final FileInputStream st = new FileInputStream(ResourceUtils.getFile(filename))) {
			final Properties prop = new Properties();
			prop.load(st);
			return prop;
		}
	}

	private static String getBuildTime(final String timestamp) {
		// convert epoch string to formatted time
		if (!timestamp.equals("${timestamp}")) { // happens during development with reload
			long buildEpoch = Long.parseLong(timestamp);
			final DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy hh:mm a z");
			dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
			return dateFormat.format(new Date(buildEpoch));
		}
		return "N/A";
	}

	@Override
	public BuildInfoDTO getBuildInfo() {
		try {
			final Properties prop = getProperties("classpath:buildinfo.properties");

			return new BuildInfoDTO(prop.getProperty("project.version"),
							prop.getProperty(prop.getProperty("build.number").equals("${buildNumber}") ? "N/A" : "build.number"),
							getBuildTime(prop.getProperty("build.time")),
							prop.getProperty("built.by"),
							prop.getProperty("built.with.java.version"),
							prop.getProperty("built.with.java.vendor"),
							prop.getProperty("built.on.os.name"),
							prop.getProperty("built.on.os.arch"),
							prop.getProperty("built.on.os.version"),
							contentsOf("schema_version", "current_schema_version")); // read from classpath. Copy current_schema_version to src/main/resources/schema_version/, compile.sh does that
		} catch (final IOException e) {
			throw new ServerSideException(e.getMessage());
		}
	}

	@Override
	public BuildInfoDTO getChangeLog() {
		try {
			// read from classpath. Copy ChangLog.txt to src/main/resources/changelog/
			return new BuildInfoDTO(contentsOf("changelog", "ChangeLog.html"));
		} catch (final IOException e) {
			throw new ServerSideException(e.getMessage());
		}
	}
}
