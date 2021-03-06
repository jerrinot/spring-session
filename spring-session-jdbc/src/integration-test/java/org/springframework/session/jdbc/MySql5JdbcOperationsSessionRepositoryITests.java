/*
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.session.jdbc;

import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MySQLContainer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Integration tests for {@link JdbcOperationsSessionRepository} using MySQL 5.x database.
 *
 * @author Vedran Pavic
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration
public class MySql5JdbcOperationsSessionRepositoryITests
		extends AbstractJdbcOperationsSessionRepositoryITests {

	private static MySQLContainer container = new MySql5Container();

	@BeforeClass
	public static void setUpClass() {
		container.start();
	}

	@AfterClass
	public static void tearDownClass() {
		container.stop();
	}

	@Configuration
	static class Config extends BaseConfig {

		@Bean
		public DataSource dataSource() {
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setUrl(container.getJdbcUrl());
			dataSource.setUser(container.getUsername());
			dataSource.setPassword(container.getPassword());
			return dataSource;
		}

		@Bean
		public DataSourceInitializer initializer(DataSource dataSource,
				ResourceLoader resourceLoader) {
			DataSourceInitializer initializer = new DataSourceInitializer();
			initializer.setDataSource(dataSource);
			initializer.setDatabasePopulator(
					new ResourceDatabasePopulator(resourceLoader.getResource(
							"classpath:org/springframework/session/jdbc/schema-mysql.sql")));
			return initializer;
		}

	}

	private static class MySql5Container extends MySQLContainer<MySql5Container> {

		MySql5Container() {
			super("mysql:5.7.22");
		}

		@Override
		protected void configure() {
			super.configure();
			setCommand("mysqld", "--character-set-server=utf8mb4",
					"--collation-server=utf8mb4_unicode_ci");
		}

	}

}
