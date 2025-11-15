package com.ovvium.services.app.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.ovvium.services.app.config.properties.MailProperties;
import com.ovvium.services.app.config.properties.PaycometCommissionProperties;
import com.ovvium.services.app.config.properties.PictureProperties;
import com.ovvium.services.repository.client.social.FacebookClient;
import com.ovvium.services.repository.client.social.GoogleClient;
import com.ovvium.services.util.mail.MailHelper;
import com.ovvium.services.util.mail.gmail.GmailMailHelper;
import com.ovvium.services.util.ovvium.spring.AppWrapper;
import com.ovvium.services.util.util.container.Pair;
import com.ovvium.services.util.util.xprops.SpringXPropsConverter;
import com.ovvium.services.util.util.xprops.XProps;
import com.ovvium.services.util.util.xson.Xson;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Configuration
@EnableConfigurationProperties
@ComponentScan(basePackageClasses = PictureProperties.class)
public class BaseConfig implements EnvironmentAware {

	private Environment environment;

	@Autowired
	private MailProperties mailProperties;

	@Bean(name = { "p", "properties" })
	public XProps props() {
		return SpringXPropsConverter.convert((ConfigurableEnvironment) environment);
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper()
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule())
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Bean
	public FacebookClient facebookClient(XProps props) {
		return new FacebookClient(props.sub("social.facebook"));
	}

	@Bean
	public GoogleClient googleClient(XProps props) {
		return new GoogleClient(props.sub("social.google"));
	}

	@Bean
	public AppWrapper appWrapper(Environment props) {
		return new AppWrapper(props);
	}

	@Bean
	@SneakyThrows
	public MailHelper mailHelper(AppWrapper appWrapper) {
		val enabled = !appWrapper.isLocal();
		Credential credentials = null;
		if (enabled) {
			credentials = createGmailCredentials();
		}
		return new GmailMailHelper(
				enabled,
				mailProperties.getFrom(),
				credentials
		);
	}

	@Bean
	public Map<Integer, String> paycometErrorsMap() {
		return Xson.ofResource("paycomet/PAYCOMET-ERRORS.json")
				.asList().parallelStream()
				.map(i -> Pair.makePair(i.get("código").asInt(), i.get("descripcióndelerror").asString()))
				.collect(toMap(Pair::getFirst, Pair::getSecond));
	}

	@Bean
	public PaycometCommissionProperties paycometCommissions() {
		return Xson.ofResource("paycomet/paycomet_commissions.json").as(PaycometCommissionProperties.class);
	}

	private GoogleCredential createGmailCredentials() throws GeneralSecurityException, IOException {
		return new GoogleCredential.Builder()//
				.setTransport(GoogleNetHttpTransport.newTrustedTransport())//
				.setJsonFactory(JacksonFactory.getDefaultInstance())//
				.setClientSecrets(
						environment.getRequiredProperty("mail.gmail.id"),
						environment.getRequiredProperty("mail.gmail.secret"))//
				.build()//
				.setAccessToken(environment.getRequiredProperty("mail.gmail.accessToken"))//
				.setRefreshToken(environment.getRequiredProperty("mail.gmail.refreshToken"));
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
