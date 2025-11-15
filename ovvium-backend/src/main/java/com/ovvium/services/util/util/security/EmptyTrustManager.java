package com.ovvium.services.util.util.security;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class EmptyTrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) {
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

	public static X509TrustManager[] getInArray() {
		return new X509TrustManager[] { new EmptyTrustManager() };
	}

}
