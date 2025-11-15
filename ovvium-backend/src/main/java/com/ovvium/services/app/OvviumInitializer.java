package com.ovvium.services.app;

import com.ovvium.services.app.config.WebConfig;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import static org.springframework.boot.Banner.Mode.CONSOLE;

public class OvviumInitializer extends SpringBootServletInitializer {

	public static void main(String[] args) {
		new SpringApplicationBuilder()//
				.sources(WebConfig.class)//
				.bannerMode(CONSOLE)//
				.run();
	}
}
