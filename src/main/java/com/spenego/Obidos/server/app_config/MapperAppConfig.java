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

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spenego.Obidos.server.dao.AssignedContainerMapper;
import com.spenego.Obidos.server.dao.AuditMapper;
import com.spenego.Obidos.server.dao.CapabilityMapper;
import com.spenego.Obidos.server.dao.ComplexityRequirementsMapper;
import com.spenego.Obidos.server.dao.ContainerAssignmentMapper;
import com.spenego.Obidos.server.dao.ContainerGroupAssignmentMapper;
import com.spenego.Obidos.server.dao.ContainerMapper;
import com.spenego.Obidos.server.dao.DocumentMapper;
import com.spenego.Obidos.server.dao.GroupMapper;
import com.spenego.Obidos.server.dao.GroupMemberMapper;
import com.spenego.Obidos.server.dao.GroupUserMapper;
import com.spenego.Obidos.server.dao.ItemAssignmentMapper;
import com.spenego.Obidos.server.dao.ItemGroupMapper;
import com.spenego.Obidos.server.dao.ItemMapper;
import com.spenego.Obidos.server.dao.ItemRecipientsMapper;
import com.spenego.Obidos.server.dao.LdapMapper;
import com.spenego.Obidos.server.dao.NotificationMapper;
import com.spenego.Obidos.server.dao.NotificationTemplateMapper;
import com.spenego.Obidos.server.dao.PasswordResetMapper;
import com.spenego.Obidos.server.dao.SharedItemMapper;
import com.spenego.Obidos.server.dao.SmtpConfigMapper;
import com.spenego.Obidos.server.dao.SystemConfigMapper;
import com.spenego.Obidos.server.dao.UserDefinedBlobMapper;
import com.spenego.Obidos.server.dao.UserDefinedFieldMapper;
import com.spenego.Obidos.server.dao.UserDefinedFieldValueMapper;
import com.spenego.Obidos.server.dao.UserDefinedTypeMapper;
import com.spenego.Obidos.server.dao.UserDefinedTypeValueMapper;
import com.spenego.Obidos.server.dao.UserMapper;

@Configuration
public class MapperAppConfig {
	@Autowired private final SqlSessionFactory ssf = null; // NOSONAR - Spring Framework managed member variable can not be static

	private <T> MapperFactoryBean<T> createMapperFactory(final Class<T> mapperInterface) {
		final MapperFactoryBean<T> mfb = new MapperFactoryBean<>(mapperInterface);
		mfb.setSqlSessionFactory(ssf);
		return mfb;
	}

	@Bean(name="assignedContainerMapper")		public MapperFactoryBean<AssignedContainerMapper>		getAssignedContainerMapper()	{ return createMapperFactory(AssignedContainerMapper.class); }
	@Bean(name="auditMapper")					public MapperFactoryBean<AuditMapper>					getAuditMapper()				{ return createMapperFactory(AuditMapper.class); }
	@Bean(name="capabilityMapper")				public MapperFactoryBean<CapabilityMapper>				getCapabilityMapper()			{ return createMapperFactory(CapabilityMapper.class); }
	@Bean(name="complexityRequirementsMapper")	public MapperFactoryBean<ComplexityRequirementsMapper>	getComplexityRequirementsMapper(){ return createMapperFactory(ComplexityRequirementsMapper.class); }
	@Bean(name="containerAssignmentMapper")		public MapperFactoryBean<ContainerAssignmentMapper>		getContainerAssignmentMapper()	{ return createMapperFactory(ContainerAssignmentMapper.class); }
	@Bean(name="containerGroupAssignmentMapper")public MapperFactoryBean<ContainerGroupAssignmentMapper>getContainerGroupAssignmentMapper() { return createMapperFactory(ContainerGroupAssignmentMapper.class); }
	@Bean(name="containerMapper")				public MapperFactoryBean<ContainerMapper>				getContainerMapper()			{ return createMapperFactory(ContainerMapper.class); }
	@Bean(name="documentMapper")				public MapperFactoryBean<DocumentMapper>				getDocumentMapper()				{ return createMapperFactory(DocumentMapper.class); }
	@Bean(name="notificationTemplateMapper")	public MapperFactoryBean<NotificationTemplateMapper>	getEmailMessageMapper()			{ return createMapperFactory(NotificationTemplateMapper.class); }
	@Bean(name="groupMapper")					public MapperFactoryBean<GroupMapper>					getGroupMapper()				{ return createMapperFactory(GroupMapper.class); }
	@Bean(name="groupMemberMapper")				public MapperFactoryBean<GroupMemberMapper>				getGroupMemberMapper()			{ return createMapperFactory(GroupMemberMapper.class); }
	@Bean(name="groupUserMapper")				public MapperFactoryBean<GroupUserMapper>				getGroupUserMapper()			{ return createMapperFactory(GroupUserMapper.class); }
	@Bean(name="itemAssignmentMapper")			public MapperFactoryBean<ItemAssignmentMapper>			getItemAssignmentMapper()		{ return createMapperFactory(ItemAssignmentMapper.class); }
	@Bean(name="itemGroupMapper")				public MapperFactoryBean<ItemGroupMapper>				getItemGroupMapper()			{ return createMapperFactory(ItemGroupMapper.class); }
	@Bean(name="itemMapper")					public MapperFactoryBean<ItemMapper>					getItemMapper()					{ return createMapperFactory(ItemMapper.class); }
	@Bean(name="itemRecipientsMapper")			public MapperFactoryBean<ItemRecipientsMapper>			getItemRecipientsMapper()		{ return createMapperFactory(ItemRecipientsMapper.class); }
	@Bean(name="ldapMapper")					public MapperFactoryBean<LdapMapper>					getLdapMapper()					{ return createMapperFactory(LdapMapper.class); }
	@Bean(name="notificationMapper")			public MapperFactoryBean<NotificationMapper>			getNotificationMapper()			{ return createMapperFactory(NotificationMapper.class); }
	@Bean(name="passwordResetMapper")			public MapperFactoryBean<PasswordResetMapper>			getPasswordResetMapper()		{ return createMapperFactory(PasswordResetMapper.class); }
	@Bean(name="sharedItemMapper")				public MapperFactoryBean<SharedItemMapper>				getSharedItemMapper()			{ return createMapperFactory(SharedItemMapper.class); }
	@Bean(name="smtpConfigMapper")				public MapperFactoryBean<SmtpConfigMapper>				getSmtpConfigMapper()			{ return createMapperFactory(SmtpConfigMapper.class); }
	@Bean(name="systemConfigMapper")			public MapperFactoryBean<SystemConfigMapper>			getSystemConfigMapper()			{ return createMapperFactory(SystemConfigMapper.class); }
	@Bean(name="userDefinedBlobMapper")			public MapperFactoryBean<UserDefinedBlobMapper>			getUserDefinedBlobMapper()		{ return createMapperFactory(UserDefinedBlobMapper.class); }
	@Bean(name="userDefinedFieldMapper")		public MapperFactoryBean<UserDefinedFieldMapper>		getUserDefinedFieldMapper()		{ return createMapperFactory(UserDefinedFieldMapper.class); }
	@Bean(name="userDefinedTypeMapper")			public MapperFactoryBean<UserDefinedTypeMapper>			getUserDefiniedTypeMapper()		{ return createMapperFactory(UserDefinedTypeMapper.class); }
	@Bean(name="userDefinedFieldValueMapper")	public MapperFactoryBean<UserDefinedFieldValueMapper>	getUserDefinedFieldValueMapper(){ return createMapperFactory(UserDefinedFieldValueMapper.class); }
	@Bean(name="userDefinedTypeValueMapper")	public MapperFactoryBean<UserDefinedTypeValueMapper>	getUserDefinedTypeValueMapper()	{ return createMapperFactory(UserDefinedTypeValueMapper.class); }
	@Bean(name="userMapper")					public MapperFactoryBean<UserMapper>					getUserMapper()					{ return createMapperFactory(UserMapper.class); }
}
