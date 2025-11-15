package com.ovvium.services.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovvium.services.app.config.properties.PaycometProperties;
import com.ovvium.services.repository.client.payment.LocalPaymentClientImpl;
import com.ovvium.services.repository.client.payment.PaycometClientImpl;
import com.ovvium.services.repository.client.payment.PaymentClient;
import com.ovvium.services.repository.client.payment.PaymentClientDecider;
import com.ovvium.services.repository.client.payment.ws.PaycometWsClient;
import com.ovvium.services.util.ws.WsClientBuilder;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.util.Map;

@Configuration
@ComponentScan(basePackageClasses = PaycometClientImpl.class)
public class WsConfig {

	@Autowired
	private Environment props;

	@Bean
	public Bus bus() {
		return new SpringBus();
	}

	@Bean
	@SneakyThrows
	public PaycometWsClient paycometWsClient() {
		val wsdl = URI.create(props.getRequiredProperty("paycomet.wsdl"));
		return new WsClientBuilder<>(bus(), wsdl, PaycometWsClient.class)
				.setLogging(true)
				.setAllowNotMappedAttributes(true)
				.build();
	}

	@Bean
	@Profile("!production")
	public PaymentClient paymentClient(PaycometWsClient client, PaycometProperties paycometProperties, Map<Integer, String> paycometErrorsMap, ObjectMapper objectMapper) {
		return new PaycometClientImpl(client, paycometProperties, paycometErrorsMap, objectMapper);
	}

	@Bean
	@Profile("production")
	public PaymentClient paymentClientDecider(PaycometWsClient client, PaycometProperties paycometProperties, Map<Integer, String> paycometErrorsMap, ObjectMapper objectMapper) {
		return new PaymentClientDecider(new LocalPaymentClientImpl(), new PaycometClientImpl(client, paycometProperties, paycometErrorsMap, objectMapper));
	}

}
