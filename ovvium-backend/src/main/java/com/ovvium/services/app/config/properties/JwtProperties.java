package com.ovvium.services.app.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	@NotBlank
	private String secret;
	@NotNull
	private Duration refreshDuration;
	@NotNull
	private Duration accessDuration;

}
