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

import com.spenego.Obidos.server.operations.AuditOperations;
import com.spenego.Obidos.server.operations.CapabilityOperations;
import com.spenego.Obidos.server.operations.ComplexityRequirementsOperations;
import com.spenego.Obidos.server.operations.ContainerAssignmentOperations;
import com.spenego.Obidos.server.operations.ContainerGroupAssignmentOperations;
import com.spenego.Obidos.server.operations.ContainerOperations;
import com.spenego.Obidos.server.operations.DocumentOperations;
import com.spenego.Obidos.server.operations.GroupMemberOperations;
import com.spenego.Obidos.server.operations.GroupOperations;
import com.spenego.Obidos.server.operations.ItemAssignmentOperations;
import com.spenego.Obidos.server.operations.ItemGroupOperations;
import com.spenego.Obidos.server.operations.ItemOperations;
import com.spenego.Obidos.server.operations.LdapConfigOperations;
import com.spenego.Obidos.server.operations.NotificationOperations;
import com.spenego.Obidos.server.operations.NotificationTemplateOperations;
import com.spenego.Obidos.server.operations.PasswordResetOperations;
import com.spenego.Obidos.server.operations.SmtpConfigOperations;
import com.spenego.Obidos.server.operations.SystemConfigOperations;
import com.spenego.Obidos.server.operations.UserDefinedBlobOperations;
import com.spenego.Obidos.server.operations.UserDefinedFieldOperations;
import com.spenego.Obidos.server.operations.UserDefinedFieldValueOperations;
import com.spenego.Obidos.server.operations.UserDefinedTypeOperations;
import com.spenego.Obidos.server.operations.UserDefinedTypeValueOperations;
import com.spenego.Obidos.server.operations.UserOperations;
import com.spenego.Obidos.server.operations.impl.AuditOperationsImpl;
import com.spenego.Obidos.server.operations.impl.CapabilityOperationsImpl;
import com.spenego.Obidos.server.operations.impl.ComplexityRequirementsOperationsImpl;
import com.spenego.Obidos.server.operations.impl.ContainerAssignmentOperationsImpl;
import com.spenego.Obidos.server.operations.impl.ContainerGroupAssignmentOperationsImpl;
import com.spenego.Obidos.server.operations.impl.ContainerOperationsImpl;
import com.spenego.Obidos.server.operations.impl.DocumentOperationsImpl;
import com.spenego.Obidos.server.operations.impl.GroupMemberOperationsImpl;
import com.spenego.Obidos.server.operations.impl.GroupOperationsImpl;
import com.spenego.Obidos.server.operations.impl.ItemAssignmentOperationsImpl;
import com.spenego.Obidos.server.operations.impl.ItemGroupOperationsImpl;
import com.spenego.Obidos.server.operations.impl.ItemOperationsImpl;
import com.spenego.Obidos.server.operations.impl.LdapConfigOperationsImpl;
import com.spenego.Obidos.server.operations.impl.NotificationOperationsImpl;
import com.spenego.Obidos.server.operations.impl.NotificationTemplateOperationsImpl;
import com.spenego.Obidos.server.operations.impl.PasswordResetOperationsImpl;
import com.spenego.Obidos.server.operations.impl.SmtpConfigOperationsImpl;
import com.spenego.Obidos.server.operations.impl.SystemConfigOperationsImpl;
import com.spenego.Obidos.server.operations.impl.UserDefinedBlobOperationsImpl;
import com.spenego.Obidos.server.operations.impl.UserDefinedFieldOperationsImpl;
import com.spenego.Obidos.server.operations.impl.UserDefinedFieldValueOperationsImpl;
import com.spenego.Obidos.server.operations.impl.UserDefinedTypeOperationsImpl;
import com.spenego.Obidos.server.operations.impl.UserDefinedTypeValueOperationsImpl;
import com.spenego.Obidos.server.operations.impl.UserOperationsImpl;

@Configuration
public class OperationAppConfig {
	@Bean(name="auditOperations")						public AuditOperations						getAuditOperations()					{ return new AuditOperationsImpl(); }
	@Bean(name="capabilityOperations")					public CapabilityOperations					getCapabilityOperations()				{ return new CapabilityOperationsImpl(); }
	@Bean(name="notificationTemplateOperations")		public NotificationTemplateOperations		getNotificationTemplateOperations()		{ return new NotificationTemplateOperationsImpl(); }
	@Bean(name="complexityRequirementsOperations")		public ComplexityRequirementsOperations		getComplexityRequirementsOperations()	{ return new ComplexityRequirementsOperationsImpl(); }
	@Bean(name="containerOperations")					public ContainerOperations					getContainerOperations()				{ return new ContainerOperationsImpl(); }
	@Bean(name="containerAssignmentOperations")			public ContainerAssignmentOperations		getContainerAssignmentOperations()		{ return new ContainerAssignmentOperationsImpl(); }
	@Bean(name="containerGroupAssignmentOperations")	public ContainerGroupAssignmentOperations	getContainerGroupAssignmentOperations() { return new ContainerGroupAssignmentOperationsImpl(); }
	@Bean(name="documentOperations")					public DocumentOperations					getDocumentOperations()					{ return new DocumentOperationsImpl(); }
	@Bean(name="groupMemberOperations")					public GroupMemberOperations				getGroupMemberOperations()				{ return new GroupMemberOperationsImpl(); }
	@Bean(name="groupOperations")						public GroupOperations						getGroupOperations()					{ return new GroupOperationsImpl(); }
	@Bean(name="itemOperations")						public ItemOperations						getItemOperations()						{ return new ItemOperationsImpl(); }
	@Bean(name="itemAssignmentOperations")				public ItemAssignmentOperations				getItemAssignmentOperations()			{ return new ItemAssignmentOperationsImpl(); }
	@Bean(name="itemGroupOperations")					public ItemGroupOperations					getItemGroupOperations()				{ return new ItemGroupOperationsImpl(); }
	@Bean(name="ldapConfigOperations")					public LdapConfigOperations					getLdapOperations()						{ return new LdapConfigOperationsImpl(); }
	@Bean(name="notificationOperations")				public NotificationOperations				getNotificationOperations()				{ return new NotificationOperationsImpl(); }
	@Bean(name="passwordResetOperations")				public PasswordResetOperations				getPasswordResetOperations()			{ return new PasswordResetOperationsImpl(); }
	@Bean(name="smtpConfigOperations")					public SmtpConfigOperations					getSmtpConfigOperations()				{ return new SmtpConfigOperationsImpl(); }
	@Bean(name="systemConfigOperations")				public SystemConfigOperations				getSystemConfigOperations()				{ return new SystemConfigOperationsImpl(); }
	@Bean(name="userDefinedFieldOperations")			public UserDefinedFieldOperations			getUserDefinedFieldOperations()			{ return new UserDefinedFieldOperationsImpl(); }
	@Bean(name="userDefinedFieldValueOperations")		public UserDefinedFieldValueOperations		getUserDefinedFieldValueOperations()	{ return new UserDefinedFieldValueOperationsImpl(); }
	@Bean(name="userDefinedBlobOperations")				public UserDefinedBlobOperations			getUserDefinedBlobOperations()			{ return new UserDefinedBlobOperationsImpl(); }
	@Bean(name="userDefinedTypeOperations")				public UserDefinedTypeOperations			getUserDefinedTypeOperations()			{ return new UserDefinedTypeOperationsImpl(); }
	@Bean(name="userDefinedTypeValueOperations")		public UserDefinedTypeValueOperations		getUserDefinedTypeValueOperations()		{ return new UserDefinedTypeValueOperationsImpl(); }
	@Bean(name="userOperations")						public UserOperations						getUserOperations()						{ return new UserOperationsImpl(); }
}
