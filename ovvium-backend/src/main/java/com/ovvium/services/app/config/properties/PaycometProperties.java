package com.ovvium.services.app.config.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotEmpty;

@Data
@Configuration
@Accessors(chain = true)
@ConfigurationProperties(prefix = "paycomet.client")
public class PaycometProperties {

	@NotEmpty
	private String terminal;
	@NotEmpty
	private String code;
	@NotEmpty
	private String password;
	@NotEmpty
	private String jetId;

}
