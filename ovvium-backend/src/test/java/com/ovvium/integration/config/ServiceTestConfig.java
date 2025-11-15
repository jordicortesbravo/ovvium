package com.ovvium.integration.config;

import com.ovvium.services.app.config.HazelcastConfig;
import com.ovvium.services.service.UserService;
import com.ovvium.services.web.controller.bff.v1.transfer.response.factory.ProductResponseFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import(value = {RepositoryTestConfig.class, HazelcastConfig.class})
@ComponentScan(basePackageClasses = {UserService.class, ProductResponseFactory.class})
public class ServiceTestConfig {
}
