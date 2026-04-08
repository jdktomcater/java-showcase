package com.jdktomcat.showcase.legacy.system.component.httpclient.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author luxun
 */
@Configuration
public class RestTemplateConfiguration {

    public static final String INTERNAL = "defaultInternal";

    public static final String DEFAULT_PROXY = "defaultProxy";

    public static final String SPECIAL_PROXY = "specialProxy";

    @Value("${rest.http.connectionRequestTimeout}")
    private int connectionRequestTimeout;

    @Value("${rest.http.connectTimeout}")
    private int connectTimeout;

    @Value("${rest.http.socketTimeout}")
    private int socketTimeout;

    @Value("${rest.http.retryCount}")
    private int retryCount;

    @Value("${rest.http.keepAlive:55}")
    private int keepAlive;

    @Value("${rest.pool.maxTotal}")
    private int maxTotal;

    @Value("${rest.pool.defaultMaxPerRoute}")
    private int defaultMaxPerRoute;

    @Value("${rest.pool.validateAfterInactivity}")
    private int validateAfterInactivity;

    @Value("${third.proxy.active:false}")
    private boolean proxyActive;

    @Value("${third.proxy.ip:127.0.0.1}")
    private String proxyIp;

    @Value("${third.proxy.port:13080}")
    private int proxyPort;

    @Value("${third.proxy.special.active:false}")
    private boolean specialProxyActive;

    @Value("${third.proxy.special.ip:127.0.0.1}")
    private String specialProxyIp;

    @Value("${third.proxy.special.port:13080}")
    private int specialProxyPort;

    public int getKeepAlive() {
        return keepAlive;
    }

    int retryCount() {
        return retryCount;
    }

    int connectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    int connectTimeout() {
        return connectTimeout;
    }

    int socketTimeout() {
        return socketTimeout;
    }

    int maxTotal() {
        return maxTotal;
    }

    int defaultMaxPerRoute() {
        return defaultMaxPerRoute;
    }

    int validateAfterInactivity() {
        return validateAfterInactivity;
    }

    public boolean proxyActive() {
        return proxyActive;
    }

    public boolean specialProxyActive() {
        return specialProxyActive;
    }

    public List<HttpProxy> getProxyList() {
        return Lists.newArrayList(
                HttpProxy.of(INTERNAL, null, null, true),
                HttpProxy.of(DEFAULT_PROXY, proxyIp, proxyPort, proxyActive),
                HttpProxy.of(SPECIAL_PROXY, specialProxyIp, specialProxyPort, specialProxyActive)
        );
    }

}
