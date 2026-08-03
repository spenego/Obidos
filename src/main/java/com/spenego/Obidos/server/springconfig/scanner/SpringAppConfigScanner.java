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

package com.spenego.Obidos.server.springconfig.scanner;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configure spring beans magically. Define the beans in the files in server/app_config directory.
 * SmtpEmailSender was sending mail synchronously even tho Async annotation was used. Tried many things,
 * none worked but found a post of stackoverflow suggested to use proxyTargetClass=true and it
 * seems to work. https://stackoverflow.com/questions/46564587/proxy-error-when-creating-a-bean-with-an-async-method
 * @author spgdev@spenego.com - Dec 11, 2016
 */
@Configuration
@ComponentScan("com.spenego.Obidos.server")
@EnableAsync(proxyTargetClass=true)
public class SpringAppConfigScanner { /* we have no real implementation */ }