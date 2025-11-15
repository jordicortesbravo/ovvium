package com.ovvium.services.app.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Configuration
@ConfigurationProperties(prefix = "hazelcast")
public class HazelcastProperties {

	@Data
	public static class HazelcastCacheProperties {

		@Positive
		private int timeToLiveSeconds = 7200;

		@Positive
		private int maxEntriesSize = 20000;

	}

	@Data
	public static class HazelcastQueueProperties {

		@Positive
		private int maxSize = 1000;

	}

	@Data
	public static class HazelcastLockProperties {

		@Positive
		private int ttl = 60;

		@Positive
		private int maxIdle = 60;

	}

	@NotNull
	private HazelcastCacheProperties cache = new HazelcastCacheProperties();
	@NotNull
	private HazelcastQueueProperties queue = new HazelcastQueueProperties();
	@NotNull
	private HazelcastLockProperties lock = new HazelcastLockProperties();

}
