package com.ovvium.services.app.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "mail")
public class MailProperties {

	@Data
	public static class MailFromProperties {
		@NotBlank
		private String email;
		@NotBlank
		private String name;
	}

	private MailFromProperties from = new MailFromProperties();

	@NotEmpty
	private List<String> to;


}
