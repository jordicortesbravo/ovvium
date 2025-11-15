package com.ovvium.services.app.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.vendor.Database;

import javax.validation.constraints.Positive;

@Data
@Configuration
@ConfigurationProperties(prefix = "db")
public class DatabaseProperties {

	private Database type;
	private String url;
	private String username;
	private String password;
	@Positive
	private Integer idleTestSeconds;
	@Positive
	private Integer minPoolSize = 5;
	@Positive
	private Integer maxPoolSize= 20;

}
