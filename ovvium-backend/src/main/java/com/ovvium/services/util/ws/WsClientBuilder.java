package com.ovvium.services.util.ws;

import com.ovvium.services.util.util.security.EmptyTrustManager;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.apache.cxf.Bus;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;

import java.net.URI;
import java.util.HashMap;

@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class WsClientBuilder<T> {

	private final Bus bus;
	private final URI wsdl;
	private final Class<T> serviceClass;

	private boolean logging;
	private boolean allowNotMappedAttributes;

	public T build() {
		val factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(serviceClass);
		factory.setAddress(wsdl.toString());
		factory.setBus(bus);
		configureFactory(factory);
		@SuppressWarnings("unchecked")
		T proxy = (T) factory.create();

		val client = ClientProxy.getClient(proxy);
		if (wsdl.getScheme().startsWith("https")) {
			HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
			TLSClientParameters tls = new TLSClientParameters();
			tls.setDisableCNCheck(true);
			tls.setTrustManagers(EmptyTrustManager.getInArray());
			tls.setSecureSocketProtocol("TLSv1.2");
			httpConduit.setTlsClientParameters(tls);
		}

		return proxy;
	}

	private void configureFactory(JaxWsProxyFactoryBean factory) {
		val properties = new HashMap<String, Object>();
		if (logging) {
			factory.getInInterceptors().add(SOAPLoggerInterceptor.getInInterceptor());
			factory.getOutInterceptors().add(SOAPLoggerInterceptor.getOutInterceptor());
		}
		if (allowNotMappedAttributes) {
			properties.put("schema-validation-enabled", "false");
			properties.put("set-jaxb-validation-event-handler", "false");
		}
		factory.setProperties(properties);
	}

}
