package com.ovvium.services.app.config;

import com.ovvium.services.app.config.properties.JwtProperties;
import com.ovvium.services.repository.ApiKeyRepository;
import com.ovvium.services.security.*;
import com.ovvium.services.security.dto.ApiKeyDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Map;

import static com.ovvium.services.security.ApiKeyAuthenticationFilter.API_KEY_HEADER;
import static com.ovvium.services.web.controller.bff.v1.WebConstants.BASE_URI_API_V1;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private static final String[] CORS_ALLOWED_HEADERS = {"Authorization", "Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers", API_KEY_HEADER};

	private final JwtProperties jwtProperties;
	private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
	private final FilterChainExceptionHandler filterChainExceptionHandler;

	public SecurityConfig(JwtProperties jwtProperties,
						  Map<String, ApiKeyDto> apiKeysByKey,
						  ApiKeyRepository apiKeyRepository,
						  HandlerExceptionResolver resolver) {
		this.jwtProperties = jwtProperties;
		this.apiKeyAuthenticationFilter = new ApiKeyAuthenticationFilter(apiKeysByKey, apiKeyRepository);
		this.filterChainExceptionHandler = new FilterChainExceptionHandler(resolver);
	}

	@Order(1)
	@Configuration
	public class AccountApiSecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.antMatcher(BASE_URI_API_V1 + "/account/**")//
					.headers()
					.and().csrf().disable()
					// FIXME Use addFilterBefore LogoutFilter when bug is fixed in SpringBoot 2.5.1
					// https://github.com/spring-projects/spring-security/issues/9787
					.addFilterAfter(filterChainExceptionHandler, HeaderWriterFilter.class)
					.addFilterAfter(apiKeyAuthenticationFilter, HeaderWriterFilter.class)
					.sessionManagement().sessionCreationPolicy(STATELESS) //
					.and().authorizeRequests()//
					.antMatchers(BASE_URI_API_V1 + "/account/**").permitAll();
		}

	}


	@Order(2)
	@Configuration
	@RequiredArgsConstructor
	public class JtwSecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.antMatcher(BASE_URI_API_V1+ "/**")//
					.headers()
					.and()
					// FIXME Use addFilterBefore LogoutFilter when bug is fixed in SpringBoot 2.5.1
					// https://github.com/spring-projects/spring-security/issues/9787
					.addFilterAfter(filterChainExceptionHandler, CorsFilter.class)
					.addFilterAfter(apiKeyAuthenticationFilter, CorsFilter.class)
					.addFilterAfter(jwtAuthenticationFilter(), CorsFilter.class)
					.csrf().disable()//
					.cors().configurationSource(corsConfigurationSource()).and()
					.sessionManagement().sessionCreationPolicy(STATELESS);
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.authenticationProvider(authenticationProvider());
		}

		@Bean
		public JwtAuthenticationProvider authenticationProvider() {
			return new JwtAuthenticationProvider(jwtProperties.getSecret());
		}

		@SneakyThrows
		private JwtAuthenticationFilter jwtAuthenticationFilter() {
			val filter = new JwtAuthenticationFilter(jwtProperties.getSecret());
			filter.setAuthenticationManager(authenticationManager());
			filter.setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler());
			filter.setAuthenticationFailureHandler(new JwtAuthenticationFailureHandler());
			return filter;
		}

		private CorsConfigurationSource corsConfigurationSource() {
			val configuration = new CorsConfiguration();
			configuration.setAllowCredentials(true);
			configuration.setAllowedHeaders(asList(CORS_ALLOWED_HEADERS));
			configuration.setAllowedOriginPatterns(singletonList("*"));
			configuration.setAllowedMethods(singletonList("*"));
			//configuration.setMaxAge(3600L); // Cachea las peticiones
			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", configuration);
			return source;
		}
	}

}
