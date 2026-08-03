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

import com.spenego.Obidos.server.mail.SmtpEmailSender;

/**
 * Bean to send smtp mail asynchronously
 * @author spgdev@spenego.com - Apr 22, 2018
 */
@Configuration
public class SmtpMailSenderConfig {
	@Bean(name="smtpEmailSender")	public SmtpEmailSender getSmtpEmailSender() { return new SmtpEmailSender(); }
}
