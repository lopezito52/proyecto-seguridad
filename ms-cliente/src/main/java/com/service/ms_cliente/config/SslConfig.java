package com.service.ms_cliente.config;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

@Configuration
public class SslConfig {

    @Value("${client.ssl.trust-store}")
    private Resource trustStore;

    @Value("${client.ssl.trust-store-password}")
    private String trustStorePassword;

    @Value("${client.ssl.key-store}")
    private Resource keyStore;

    @Value("${client.ssl.key-store-password}")
    private String keyStorePassword;

    // Solo TLS: valida el servidor pero NO presenta certificado propio
    @Bean("restTemplateTLS")
    public RestTemplate restTemplateTLS() throws Exception {
        SSLContext ssl = SSLContextBuilder.create()
            .loadTrustMaterial(
                trustStore.getURL(),
                trustStorePassword.toCharArray())
            .build();

        var factory = new HttpComponentsClientHttpRequestFactory(
            HttpClients.custom()
                .setConnectionManager(
                    PoolingHttpClientConnectionManagerBuilder.create()
                        .setSSLSocketFactory(
                            SSLConnectionSocketFactoryBuilder.create()
                                .setSslContext(ssl)
                                .build())
                        .build())
                .build());

        return new RestTemplate(factory);
    }

    // mTLS: valida el servidor Y presenta su propio certificado
    @Bean("restTemplateMTLS")
    public RestTemplate restTemplateMTLS() throws Exception {
        SSLContext ssl = SSLContextBuilder.create()
            .loadTrustMaterial(
                trustStore.getURL(),
                trustStorePassword.toCharArray())
            .loadKeyMaterial(
                keyStore.getURL(),
                keyStorePassword.toCharArray(),
                keyStorePassword.toCharArray())
            .build();

        var factory = new HttpComponentsClientHttpRequestFactory(
            HttpClients.custom()
                .setConnectionManager(
                    PoolingHttpClientConnectionManagerBuilder.create()
                        .setSSLSocketFactory(
                            SSLConnectionSocketFactoryBuilder.create()
                                .setSslContext(ssl)
                                .build())
                        .build())
                .build());

        return new RestTemplate(factory);
    }
}
