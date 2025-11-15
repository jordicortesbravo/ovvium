package com.ovvium.integration.config;

import com.ovvium.services.app.config.RepositoryConfig;
import com.ovvium.services.app.config.properties.DatabaseProperties;
import com.ovvium.utils.TransactionalHelper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.orm.jpa.vendor.Database.POSTGRESQL;

@TestConfiguration
public class RepositoryTestConfig extends RepositoryConfig {

	private final static PostgreSQLContainer<?> postgreSQLContainer= new PostgreSQLContainer<>("postgres:12.6-alpine")
			.withReuse(true)
			.withDatabaseName("ovvium-test")
			.withUsername("ovviumtest")
			.withPassword("ovviumtest");

	static {
		postgreSQLContainer.start();
	}

	public RepositoryTestConfig(DatabaseProperties databaseProperties) {
		super(databaseProperties);
		databaseProperties.setUrl(postgreSQLContainer.getJdbcUrl());
		databaseProperties.setUsername(postgreSQLContainer.getUsername());
		databaseProperties.setPassword(postgreSQLContainer.getPassword());
		databaseProperties.setType(POSTGRESQL);
	}

	@Bean
	public TransactionalHelper transactionalHelper() {
		return new TransactionalHelper();
	}

}
