package com.jdktomcat.showcase.legacy.system.component.httpclient.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Configuration;

/**
 */
@Configuration
public class RequestFactoryHelper {

    private final HttpClientBuilder httpClientBuilder;

    public RequestFactoryHelper(RestTemplatePoolConfig restTemplatePoolConfig) {
        httpClientBuilder = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(restTemplatePoolConfig.connectTimeout())
                        .setSocketTimeout(restTemplatePoolConfig.socketTimeout())
                        .setConnectionRequestTimeout(restTemplatePoolConfig.connectionRequestTimeout())
                        .build())
                .setRetryHandler(new DefaultHttpRequestRetryHandler(restTemplatePoolConfig.retryCount(), false));
    }

    public HttpClientBuilder getHttpClientBuilder() {
        return httpClientBuilder;
    }

}
