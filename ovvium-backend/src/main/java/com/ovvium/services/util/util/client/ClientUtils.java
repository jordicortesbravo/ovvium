package com.ovvium.services.util.util.client;

import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.ovvium.services.util.util.security.EmptyTrustManager;

import lombok.SneakyThrows;
import lombok.val;

public class ClientUtils {

    @SneakyThrows
    public static SSLContext getUnsecureSslContext() {
        val sslCtx = SSLContext.getInstance("TLS");
        sslCtx.init(new KeyManager[0], EmptyTrustManager.getInArray(), new SecureRandom());
        return sslCtx;
    }

    /**
     * Sets an HTTP executor which accepts https connections without certificate validation. It accepts requests to domains other than the
     * ones in certificate. <br/>
     * It must be called before configuring any executor property. For example:
     * 
     * <pre>
     * httpApiClient.getExecutor().auth("john", "calamar")
     * </pre>
     * 
     * won't work as expected
     */
    @SneakyThrows
    public static void setUnsecureSsl(HttpClientBuilder builder) {

        // Allow self-signed certificates
        builder.setSSLContext(getUnsecureSslContext());

        // Allow unmatched hostname in certificate (avoids SSLException:
        // hostname in certificate didn't match)
        builder.setSSLHostnameVerifier(new NoopHostnameVerifier());
    }

    public static Registry<ConnectionSocketFactory> getSocketRegistry(boolean http, boolean https) {
        val reg = RegistryBuilder.<ConnectionSocketFactory> create();
        if (http) {
            reg.register("http", PlainConnectionSocketFactory.getSocketFactory());
        }
        if (https) {
            reg.register("https", SSLConnectionSocketFactory.getSocketFactory());
        }
        return reg.build();
    }

    public static void setPool(HttpClientBuilder builder, int maxTotal, int maxPerRoute,
            Registry<ConnectionSocketFactory> socketFactoryRegistry, DnsResolver dnsResolver) {
        if (socketFactoryRegistry == null) {
            socketFactoryRegistry = getSocketRegistry(true, true);
        }
        val pool = new PoolingHttpClientConnectionManager(socketFactoryRegistry, dnsResolver);
        pool.setMaxTotal(maxTotal);
        pool.setDefaultMaxPerRoute(maxPerRoute);
        builder.setConnectionManager(pool);
    }

    public static void setTimeouts(HttpClientBuilder builder, int connect, int socket) {
        val req = RequestConfig.custom() //
                .setConnectTimeout(connect) //
                .setSocketTimeout(socket);
        builder.setDefaultRequestConfig(req.build());
    }

}
