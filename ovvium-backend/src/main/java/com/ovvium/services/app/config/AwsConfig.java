package com.ovvium.services.app.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!local")
public class AwsConfig {

	static  {
		// Set JVM caching ttl to allow reconnecting to DB on failover
		java.security.Security.setProperty("networkaddress.cache.ttl", "60");
	}

	@Bean
	public AmazonS3Client amazonS3Client() {
		return (AmazonS3Client) AmazonS3ClientBuilder.standard()
				.withRegion(new DefaultAwsRegionProviderChain().getRegion())
				.withCredentials(new DefaultAWSCredentialsProviderChain())
				.build();
	}

	@Bean
	public TransferManager transferManager(AmazonS3 amazonS3Client) {
		return TransferManagerBuilder.standard()
				.withS3Client(amazonS3Client)
				.build();
	}

}
