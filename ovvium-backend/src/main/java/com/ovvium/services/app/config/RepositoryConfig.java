package com.ovvium.services.app.config;

import com.jolbox.bonecp.BoneCPDataSource;
import com.ovvium.services.app.config.properties.DatabaseProperties;
import com.ovvium.services.repository.UserRepository;
import com.ovvium.services.util.jpa.converter.LocalDateTimeConverter;
import com.ovvium.services.util.jpa.util.Sql;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hibernate.FlushMode;
import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.dialect.PostgreSQL10Dialect;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

import static com.google.common.base.MoreObjects.firstNonNull;

@Slf4j
@RequiredArgsConstructor
@Configuration
@Import({BaseConfig.class, WsConfig.class})
@EnableTransactionManagement
@ComponentScan(basePackageClasses = {UserRepository.class})
public class RepositoryConfig {

	private final DatabaseProperties databaseProperties;

	@SneakyThrows
	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		val dataSource = new BoneCPDataSource();
		dataSource.setDriverClass(Sql.getDriver(databaseProperties.getType()));
		dataSource.setJdbcUrl(databaseProperties.getUrl());
		dataSource.setUser(databaseProperties.getUsername());
		dataSource.setPassword(databaseProperties.getPassword());
		if (databaseProperties.getIdleTestSeconds() != null) {
			dataSource.setConnectionTestStatement(Sql.getTestStatement(databaseProperties.getType()));
			dataSource.setIdleConnectionTestPeriodInSeconds(databaseProperties.getIdleTestSeconds());
		}
		return dataSource;
	}

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setPackagesToScan(
				"com.ovvium.services.model",
				LocalDateTimeConverter.class.getPackage().getName()
		);
		sessionFactory.setPhysicalNamingStrategy(new SpringPhysicalNamingStrategy());
		sessionFactory.setImplicitNamingStrategy(new SpringImplicitNamingStrategy());
		sessionFactory.setHibernateProperties(new Properties() {
			{
				setProperty("hibernate.dialect", PostgreSQL10Dialect.class.getName());
				setProperty("hibernate.connection.driver_class", PostgreSQL10Dialect.class.getName());
				setProperty("hibernate.connection.url", databaseProperties.getUrl());
				setProperty("hibernate.connection.username", databaseProperties.getUsername());
				setProperty("hibernate.connection.password", firstNonNull(databaseProperties.getPassword(), ""));
				setProperty("hibernate.globally_quoted_identifiers", "true");
				setProperty("hibernate.auto_quote_keyword", "true");
				setProperty("hibernate.max_fetch_depth", "3");
				setProperty("org.hibernate.flushMode", FlushMode.ALWAYS.name());
				setProperty("hibernate.temp.use_jdbc_metadata_defaults","false"); // If true, Postgres slows down startup

				// C3P0 properties
				setProperty("hibernate.connection.provider_class", C3P0ConnectionProvider.class.getName());
				setProperty("hibernate.c3p0.min_size", databaseProperties.getMinPoolSize().toString());
				setProperty("hibernate.c3p0.max_size", databaseProperties.getMaxPoolSize().toString());
				if (databaseProperties.getIdleTestSeconds() != null) {
					setProperty("hibernate.c3p0.idle_test_period", databaseProperties.getIdleTestSeconds().toString());
				}
			}
		});
		return sessionFactory;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(sessionFactory().getObject());
		return transactionManager;
	}

	@Bean
	public SpringLiquibase liquibase() {
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setChangeLog("classpath:database/liquibase-changelog.xml");
		liquibase.setDataSource(dataSource());
		liquibase.setDefaultSchema("public");
		return liquibase;
	}

}
