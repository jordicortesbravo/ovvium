package com.ovvium.services.app.config;

import com.ovvium.services.app.config.listener.StartupPopulator;
import com.ovvium.services.security.UserAuthorizationChecker;
import com.ovvium.services.service.PopulatorService;
import com.ovvium.services.web.controller.CheckController;
import com.ovvium.services.web.filter.MDCLoggerFilter;
import com.ovvium.services.web.filter.StatsFilter;
import com.ovvium.services.web.handler.ControllerExceptionHandler;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Import({SecurityConfig.class, ServiceConfig.class, SwaggerConfig.class, AwsConfig.class})
@ComponentScan(basePackageClasses = {CheckController.class, ControllerExceptionHandler.class,
		UserAuthorizationChecker.class, StatsFilter.class, MDCLoggerFilter.class})
@ImportAutoConfiguration({WebMvcAutoConfiguration.class,
		HttpMessageConvertersAutoConfiguration.class,
		DispatcherServletAutoConfiguration.class,
		ServletWebServerFactoryAutoConfiguration.class})
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**");
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		return new CommonsMultipartResolver();
	}

	/**
	 * Register a request context listener so we can use RequestContextHolder to get current request.
	 */
	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}

	@Bean
	public StartupPopulator startupPopulator(PopulatorService populatorService) {
		return new StartupPopulator(populatorService);
	}

	// Allow CORS for REST webapp consumer
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*");
	}

}
