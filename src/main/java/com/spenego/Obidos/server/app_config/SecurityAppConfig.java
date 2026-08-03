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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spenego.Obidos.server.security.Authenticator;
import com.spenego.Obidos.server.security.AuthenticatorImpl;
import com.spenego.Obidos.server.security.Encryption;
import com.spenego.Obidos.server.security.LibSodiumEncryption;
import com.spenego.Obidos.server.security.LibSodiumPasswordSecurity;
import com.spenego.Obidos.server.security.PasswordSecurity;

@Configuration
public class SecurityAppConfig {
	@Bean(name="encryption")		public Encryption				getEncryption()			{ return new LibSodiumEncryption(); }
	@Bean(name="authenticator")		public Authenticator			getAuthenticator()		{ return new AuthenticatorImpl(); }
	@Bean(name="passwordSecurity")	public PasswordSecurity			getPasswordSecurity()	{ return new LibSodiumPasswordSecurity(); }
}
