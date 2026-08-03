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

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.sql.DataSource;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;

@Configuration
public class DataSourceAppConfig {
	private static final Logger logger = LoggerFactory.getLogger(DataSourceAppConfig.class);
	private static final File configHome = new File("config");

	private final DataSource dataSource;

	public static File getConfigDirectory() {
		return configHome;
	}

	public DataSourceAppConfig() throws IOException, PropertyVetoException, ConfigurationException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
		final ComboPooledDataSource ds = new ComboPooledDataSource();

		// Issue #625
		final Properties props = ServerUtils.getJdbcProperties();

		ds.setDriverClass(props.getProperty("jdbc.driverClassName"));
		ds.setJdbcUrl(props.getProperty("jdbc.url"));
		ds.setUser(props.getProperty("jdbc.username"));
		ds.setPassword(props.getProperty("jdbc.password"));
		ds.setIdleConnectionTestPeriod(Integer.valueOf(props.getProperty("jdbc.idleTestPeriod", "3511")));
		ds.setAutomaticTestTable(props.getProperty("jdbc.automaticTestTable", "c3p0Test"));
		ds.setMaxIdleTime(Integer.valueOf(props.getProperty("jdbc.maxIdleTime", "21555")));
		logger.info(() -> "jdbc.driverClassName: " + ds.getDriverClass());
		logger.info(() -> "jdbc.url: " + ds.getJdbcUrl());
		logger.info(() -> "jdbc.username: " + ds.getUser());
		logger.info(() -> "Max Pool Size: " + ds.getMaxPoolSize());
		this.dataSource = ds;
	}

	@Bean
	public SqlSessionFactory getSqlSessionFactory() throws Exception {
		final SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
		ssfb.setDataSource(dataSource);
		return ssfb.getObject();
	}

	@Bean(name="transactionManager")
	public PlatformTransactionManager getTransactionManager() throws IOException {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public DataSource getDataSource()  {
		return dataSource;
	}
}
