package com.ovvium.services.util.ovvium.spring;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

import static com.ovvium.services.util.ovvium.spring.AppWrapper.OvviumProfile.LOCAL;
import static com.ovvium.services.util.ovvium.spring.AppWrapper.OvviumProfile.TEST;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class AppWrapper {

	public enum OvviumProfile {
		LOCAL, TEST, DEVELOPMENT, STAGING, PRODUCTION
	}

	private final List<OvviumProfile> activeProfiles;

	public AppWrapper(Environment environment) {
		this.activeProfiles = Arrays.stream(environment.getActiveProfiles())
				.map(String::toUpperCase)
				.map(OvviumProfile::valueOf)
				.collect(toList());
	}

	public boolean isLocal() {
		return this.activeProfiles.stream().anyMatch(asList(TEST, LOCAL)::contains);
	}

}
