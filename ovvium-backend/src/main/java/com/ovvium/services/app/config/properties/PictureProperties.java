package com.ovvium.services.app.config.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "image")
public class PictureProperties {

	@Data
	@Accessors(chain = true)
	public static class Crop {

		@NotNull
		private Integer quality = 1;
		@NotEmpty
		private Map<String, String> cropSizes = new HashMap<>();

	}

	@NotEmpty
	private String staticsUrl;

	@NotEmpty
	private String basePath;

	@NotNull
	private Crop crop;

}
