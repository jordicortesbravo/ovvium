
package com.ovvium.services.app.config;

import com.google.common.base.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.ovvium.services.web.controller.bff.v1.WebConstants.BASE_URI_API_V1;
import static springfox.documentation.builders.PathSelectors.regex;

@Slf4j
@EnableSwagger2
@Profile("local")
@Configuration
public class SwaggerConfig {

	@Bean
	public Docket ovviumApi() {
		log.info("Initializing Swagger... Check /swagger-ui.html");
		return new Docket(DocumentationType.SWAGGER_2)//
				.apiInfo(ovviumApiInfo())//
				.select().paths(allowedPaths())//
				.apis(RequestHandlerSelectors.any())//
				.build()//
				.useDefaultResponseMessages(false);
	}

	private ApiInfo ovviumApiInfo() {
		return new ApiInfoBuilder() //
				.title("Ovvium Services")//
				.version("1.0")//
				.build();
	}

	private Predicate<String> allowedPaths() {
		return regex(BASE_URI_API_V1 + "/.*");
	}
}