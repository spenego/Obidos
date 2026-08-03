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
import org.springframework.context.annotation.Scope;

import com.spenego.Obidos.server.actions.AuditActions;
import com.spenego.Obidos.server.actions.CapabilityActions;
import com.spenego.Obidos.server.actions.ContainerActions;
import com.spenego.Obidos.server.actions.DocumentActions;
import com.spenego.Obidos.server.actions.EmailActions;
import com.spenego.Obidos.server.actions.FernetActions;
import com.spenego.Obidos.server.actions.HaveIBeenPwnedActions;
import com.spenego.Obidos.server.actions.ItemActions;
import com.spenego.Obidos.server.actions.LdapConfigActions;
import com.spenego.Obidos.server.actions.LoginActions;
import com.spenego.Obidos.server.actions.NotificationActions;
import com.spenego.Obidos.server.actions.NotificationTemplateActions;
import com.spenego.Obidos.server.actions.PasswordResetActions;
import com.spenego.Obidos.server.actions.QRCodeActions;
import com.spenego.Obidos.server.actions.SMSActions;
import com.spenego.Obidos.server.actions.SmtpConfigActions;
import com.spenego.Obidos.server.actions.SystemConfigActions;
import com.spenego.Obidos.server.actions.TwoFactorAuthenticationActions;
import com.spenego.Obidos.server.actions.UserActions;
import com.spenego.Obidos.server.actions.UserDefinedFieldActions;
import com.spenego.Obidos.server.actions.UserDefinedFieldValueActions;
import com.spenego.Obidos.server.actions.UserDefinedTypeActions;
import com.spenego.Obidos.server.actions.UserDefinedTypeValueActions;
import com.spenego.Obidos.server.actions.UserManagementActions;
import com.spenego.Obidos.server.actions.impl.AuditActionsImpl;
import com.spenego.Obidos.server.actions.impl.CapabilityActionsImpl;
import com.spenego.Obidos.server.actions.impl.ContainerActionsImpl;
import com.spenego.Obidos.server.actions.impl.DocumentActionsImpl;
import com.spenego.Obidos.server.actions.impl.EmailActionsImpl;
import com.spenego.Obidos.server.actions.impl.FernetActionsImpl;
import com.spenego.Obidos.server.actions.impl.HaveIBeenPwnedActionsImpl;
import com.spenego.Obidos.server.actions.impl.ItemActionsImpl;
import com.spenego.Obidos.server.actions.impl.LdapConfigActionsImpl;
import com.spenego.Obidos.server.actions.impl.LoginActionsImpl;
import com.spenego.Obidos.server.actions.impl.NotificationActionsImpl;
import com.spenego.Obidos.server.actions.impl.NotificationTemplateActionsImpl;
import com.spenego.Obidos.server.actions.impl.PasswordResetActionsImpl;
import com.spenego.Obidos.server.actions.impl.QRCodeActionsImpl;
import com.spenego.Obidos.server.actions.impl.SMSActionsImpl;
import com.spenego.Obidos.server.actions.impl.SmtpConfigActionsImpl;
import com.spenego.Obidos.server.actions.impl.SystemConfigActionsImpl;
import com.spenego.Obidos.server.actions.impl.TwoFactorAuthenticationActionsImpl;
import com.spenego.Obidos.server.actions.impl.UserActionsImpl;
import com.spenego.Obidos.server.actions.impl.UserDefinedFieldActionsImpl;
import com.spenego.Obidos.server.actions.impl.UserDefinedFieldValueActionsImpl;
import com.spenego.Obidos.server.actions.impl.UserDefinedTypeActionsImpl;
import com.spenego.Obidos.server.actions.impl.UserDefinedTypeValueActionsImpl;
import com.spenego.Obidos.server.actions.impl.UserManagementActionsImpl;
import com.spenego.Obidos.server.model.Assignable;
import com.spenego.Obidos.server.utils.NotificationEngine;
import com.spenego.Obidos.server.utils.NotificationEngineImpl;

@Configuration
public class ActionAppConfig {
	@Bean(name="auditActions")					public AuditActions					getAuditActions()				{ return new AuditActionsImpl(); }
	@Bean(name="capabilityActions")				public CapabilityActions			getCapabilityActions()			{ return new CapabilityActionsImpl(); }
	@Bean(name="containerActions")				public ContainerActions				getContainerActions()			{ return new ContainerActionsImpl(); }
	@Bean(name="emailActions")					public EmailActions					getEmailActions()				{ return new EmailActionsImpl(); }
	@Bean(name="documentActions")				public DocumentActions				getDocumentActions()			{ return new DocumentActionsImpl(); }
	@Bean(name="fernetActions")					public FernetActions				getFernetActions()				{ return new FernetActionsImpl(); }
	@Bean(name="haveIBeenPwnedActions")			public HaveIBeenPwnedActions		getHaveIBeenPwnedActions()		{ return new HaveIBeenPwnedActionsImpl(); }
	@Bean(name="itemActions")					public ItemActions					getItemActions()				{ return new ItemActionsImpl(); }
	@Bean(name="ldapActions")					public LdapConfigActions			getLdapActions()				{ return new LdapConfigActionsImpl(); }
	@Bean(name="loginActions")					public LoginActions					getLoginActions()				{ return new LoginActionsImpl(); }
	@Bean(name="notificationActions")			public NotificationActions			getNotificationActions()		{ return new NotificationActionsImpl(); }
	@Bean(name="notificationTemplateActions")	public NotificationTemplateActions	getNotificationTemplateActions(){ return new NotificationTemplateActionsImpl(); }
	@Bean(name="passwordResetActions")			public PasswordResetActions			getPasswordResetActions()		{ return new PasswordResetActionsImpl(); }
	@Bean(name="smtpConfigActions")				public SmtpConfigActions			getSmtpConfigActions()			{ return new SmtpConfigActionsImpl(); }
	@Bean(name="systemConfigActions")			public SystemConfigActions			getSystemConfigActions()		{ return new SystemConfigActionsImpl(); }
	@Bean(name="smsActions")					public SMSActions					getSmsActions()					{ return new SMSActionsImpl(); }
	@Bean(name="userActions")					public UserActions					getUserActions()				{ return new UserActionsImpl(); }
	@Bean(name="userDefinedFieldActions")		public UserDefinedFieldActions		getUserDefinedFieldActions()	{ return new UserDefinedFieldActionsImpl(); }
	@Bean(name="userDefinedFieldValueActions")	public UserDefinedFieldValueActions getUserDefinedFieldValueActions(){return new UserDefinedFieldValueActionsImpl(); }
	@Bean(name="userDefinedTypeActions")		public UserDefinedTypeActions		getUserDefinedTypeActions()		{ return new UserDefinedTypeActionsImpl(); }
	@Bean(name="userDefinedTypeValueActions")	public UserDefinedTypeValueActions	getUserDefinedTypeValueActions(){ return new UserDefinedTypeValueActionsImpl(); }
	@Bean(name="userManagementActions")			public UserManagementActions		getUserManagementActions()		{ return new UserManagementActionsImpl(); }
	@Bean(name="twoFactorAuthenticationActions")public TwoFactorAuthenticationActions getTwoFactorAuthenticationActions() { return new TwoFactorAuthenticationActionsImpl(); }
	@Bean(name="qrCodeActions")                 public QRCodeActions getQRCodeActions() { return new QRCodeActionsImpl();}

	/**
	 * Once Spring provides an easy way to create a prototype object that uses Generics and whose constructors take arguments, we'll start using it.
	 *
	 * @return
	 */
	@Bean(name="NotificationEngine")
	@Scope("prototype")
	public <T extends Assignable> NotificationEngine<T> getNotificationEngine() { return new NotificationEngineImpl<>(); }
}
