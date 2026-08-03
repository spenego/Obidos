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

package com.spenego.Obidos.server.validators;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.validator.routines.UrlValidator;

import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * Validate LDAP or any other URL
 * 
 * @author spgdev@spenego.com - Apr 8, 2017
 */
public class ValidateUrl {
	protected static final Logger logger = LoggerFactory.getLogger(ValidateUrl.class);

	public ValidateUrl() {
	}

	public static void validateUrl(String url) {
		if (!UrlValidator.getInstance().isValid(url)) {
			throw new ServerSideException("Invalid Url");
		}
	}

	public static void validateLdapUrlOld(String url) {
		String[] schemes = { "ldap", "ldaps" };
		// Bug #107 Add ALLOW_ALL_SCHEMES to the options parameter
		UrlValidator validator = new UrlValidator(schemes, UrlValidator.ALLOW_ALL_SCHEMES);
		if (!validator.isValid(url)) {
			throw new ServerSideException("Invalid LDAP URI");
		}
	}

	public static void validateLdapUrl(String url) {
		String[] schemes = { "ldap", "ldaps" };
		// Bug #107 add checks in uril parsing
		UrlValidator validator = new UrlValidator(schemes, UrlValidator.ALLOW_ALL_SCHEMES | UrlValidator.ALLOW_LOCAL_URLS) {
			/**
			 * @author spgdev@spenego.com - Feb 13, 2025
			 */
			private static final long serialVersionUID = 190796654730688918L;

			@Override
			public boolean isValid(String value) {
				if (value == null) {
					logger.error(() -> "LDAP URL is null");
					return false;
				}

				URI uri;
				try {
					uri = new URI(value);
				} catch (URISyntaxException e) {
					String msg = "LDAP URIL Syntax error: " + e.getMessage();
					logger.error(() -> msg);
					throw new ServerSideException(msg);
				}

				String scheme = uri.getScheme();
				if (!isValidScheme(scheme)) {
					String msg = "Invalid LDAP scheme: " + scheme;
					logger.error(() -> msg);
					throw new ServerSideException(msg);
				}

				String authority = uri.getRawAuthority();
				if (!isValidAuthority(authority)) {
					String msg = "Invalid authority in LDAP URI, use a valid TLD: " + authority;
					logger.error(() -> msg);
					throw new ServerSideException(msg);
				}

				if (!isValidPath(uri.getRawPath())) {
					String msg = "Invalid path in LDAP URI: " + uri.getRawPath();
					logger.error(() -> msg);
					throw new ServerSideException(msg);
				}

				if (!isValidQuery(uri.getRawQuery())) {
					String msg = "Invalid query in LDAP URI: " + uri.getRawQuery();
					logger.error(() -> msg);
					throw new ServerSideException(msg);
				}

				if (!isValidFragment(uri.getRawFragment())) {
					String msg = "Invalid fragment in LDAP URI: " + uri.getRawFragment();
					logger.error(() -> msg);
					throw new ServerSideException(msg);
				}

				return true;
			}
		};

		if (!validator.isValid(url)) {
			throw new ServerSideException("Invalid LDAP URI");
		}
	}
}
