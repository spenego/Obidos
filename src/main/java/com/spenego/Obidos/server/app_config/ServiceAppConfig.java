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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.spenego.Obidos.client.rpc.AuditService;
import com.spenego.Obidos.client.rpc.EmailMessageConfigService;
import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.client.rpc.LoginService;
import com.spenego.Obidos.client.rpc.NotebookService;
import com.spenego.Obidos.client.rpc.NotificationService;
import com.spenego.Obidos.client.rpc.SystemConfigService;
import com.spenego.Obidos.client.rpc.TemplateService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.server.services.impl.AuditServiceImpl;
import com.spenego.Obidos.server.services.impl.EmailMessageConfigServiceImpl;
import com.spenego.Obidos.server.services.impl.ItemServiceImpl;
import com.spenego.Obidos.server.services.impl.LoginServiceImpl;
import com.spenego.Obidos.server.services.impl.NotebookServiceImpl;
import com.spenego.Obidos.server.services.impl.NotificationServiceImpl;
import com.spenego.Obidos.server.services.impl.SystemConfigServiceImpl;
import com.spenego.Obidos.server.services.impl.TemplateServiceImpl;
import com.spenego.Obidos.server.services.impl.UserServiceImpl;
import com.spenego.Obidos.server.utils.MemoryWiper;

@EnableTransactionManagement
@Configuration
public class ServiceAppConfig {
	@Bean(name="memoryWiper")				public MemoryWiper					getMemoryWiper()				{ return new com.spenego.Obidos.server.utils.MemoryWiperImpl(); }
	@Bean(name="auditService")				public AuditService					getAuditService()				{ return new AuditServiceImpl(); }
	@Bean(name="emailMessageConfigService")	public EmailMessageConfigService	getEmailMessageConfigService()	{ return new EmailMessageConfigServiceImpl(); }
	@Bean(name="itemService")				public ItemService					getItemService()				{ return new ItemServiceImpl(); }
	@Bean(name="loginService")				public LoginService					getLoginService()				{ return new LoginServiceImpl(); }
	@Bean(name="notebookService")			public NotebookService				getNotebookService()			{ return new NotebookServiceImpl(); }
	@Bean(name="notificationService")		public NotificationService			getNotificationService()		{ return new NotificationServiceImpl(); }
	@Bean(name="systemConfigService")		public SystemConfigService			getSystemConfigService()		{ return new SystemConfigServiceImpl(); }
	@Bean(name="templateService")			public TemplateService				getTemplateService()			{ return new TemplateServiceImpl(); }
	@Bean(name="userService")				public UserService					getUserService()				{ return new UserServiceImpl(); }
}

