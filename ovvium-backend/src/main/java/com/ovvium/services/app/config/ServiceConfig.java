package com.ovvium.services.app.config;

import com.ovvium.services.app.config.schedulers.ScheduledTasks;
import com.ovvium.services.service.UserService;
import com.ovvium.services.web.controller.bff.v1.transfer.response.factory.ProductResponseFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@EnableAsync
@EnableScheduling
@Configuration
@Import(value = {RepositoryConfig.class, HazelcastConfig.class, ScheduledTasks.class})
@ComponentScan(basePackageClasses = {UserService.class, ProductResponseFactory.class})
public class ServiceConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

}
