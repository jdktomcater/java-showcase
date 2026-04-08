package com.jdktomcat.showcase.legacy.system.component.httpclient.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * spring boot的RestTemplate连接池相关配置
 */
@Configuration
@Slf4j
public class RestTemplatePoolConfig {

    @Value("${rest.http.connectionRequestTimeout}")
    private int connectionRequestTimeout;

    @Value("${rest.http.connectTimeout}")
    private int connectTimeout;

    @Value("${rest.http.socketTimeout}")
    private int socketTimeout;

    @Value("${rest.http.retryCount}")
    private int retryCount;

    @Value("${rest.pool.maxTotal}")
    private int maxTotal;

    @Value("${rest.pool.defaultMaxPerRoute}")
    private int defaultMaxPerRoute;

    @Value("${rest.pool.validateAfterInactivity}")
    private int validateAfterInactivity;

    // region 代理出口IP相关事项

    @Value("${third.proxy.ip:127.0.0.1}")
    private String proxyIp = "127.0.0.1";
    @Value("${third.proxy.port:13080}")
    private int proxyPort = 13080;
    @Value("${third.proxy.active:false}")
    private boolean proxyActive = false;

    // 全民代理开关，默认：关
    @Value("${third.qmproxy.active:false}")
    private boolean qmProxyActive = false;

    /**
     * 设置代理请求
     *
     * @param restTemplate
     */
//    public void setProxy(RestTemplate restTemplate, boolean enabledProxy) {
//        if (proxyActive) {
//            log.debug("请求代理服务  {}:{}", proxyIp, proxyPort);
//            //设置代理服务
//            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//            // 连接超时时间：毫秒
//            requestFactory.setConnectTimeout(connectTimeout);
//            // 读写超时时间：毫秒
//            requestFactory.setReadTimeout(socketTimeout);
//            requestFactory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIp, proxyPort)));
//            restTemplate.setRequestFactory(requestFactory);
//        } else {
//            restTemplate.setRequestFactory(clientHttpRequestFactory(enabledProxy));
//        }

//        restTemplate.setRequestFactory(clientHttpRequestFactory(enabledProxy));
//    }

    // endregion

    /**
     * 让spring管理RestTemplate,参数相关配置
     * <p>
     *
     * @param builder
     * @return
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // 生成一个RestTemplate实例
        RestTemplate restTemplate = builder.requestFactory(
                () -> proxyActive ? clientHttpRequestFactory() : clientHttpRequestFactoryWithoutProxy())
                .build();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

    /**
     * 让spring管理RestTemplate,参数相关配置（不走代理）
     * <p>
     *
     * @param builder
     * @return
     */
    @Bean
    public RestTemplate restNoProxyeTemplat(RestTemplateBuilder builder) {
        // 生成一个RestTemplate实例
        RestTemplate restTemplate = builder.requestFactory(() ->
                qmProxyActive && proxyActive ? clientHttpRequestFactory() : clientHttpRequestFactoryWithoutProxy())
                .build();
//        if (qmProxyActive) {
//            log.info("=====>qmProxyActive全民支付代理开关：开启");
//            setProxy(restTemplate, qmProxyActive && proxyActive);
//        } else {
//            log.info("=====>qmProxyActive全民支付代理开关：关闭");
//            restTemplate.setRequestFactory(clientHttpRequestFactory());
//        }
//        restTemplate.setRequestFactory();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

    /**
     * 客户端请求链接策略
     *
     * @return
     */
    @Bean
    @Primary
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpClientBuilder().setRetryHandler(new DefaultHttpRequestRetryHandler(this.retryCount, false));
        clientHttpRequestFactory.setHttpClient(httpClientBuilder().build());
        // 连接超时时间：毫秒
        clientHttpRequestFactory.setConnectTimeout(connectTimeout);
        // 读写超时时间：毫秒
        clientHttpRequestFactory.setReadTimeout(socketTimeout);
        // 请求超时时间：毫秒
        clientHttpRequestFactory.setConnectionRequestTimeout(connectionRequestTimeout);
        return clientHttpRequestFactory;
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactoryWithoutProxy() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpClientBuilder().setRetryHandler(new DefaultHttpRequestRetryHandler(this.retryCount, false));
        clientHttpRequestFactory.setHttpClient(httpClientBuilder().build());
        // 连接超时时间：毫秒
        clientHttpRequestFactory.setConnectTimeout(connectTimeout);
        // 读写超时时间：毫秒
        clientHttpRequestFactory.setReadTimeout(socketTimeout);
        // 请求超时时间：毫秒
        clientHttpRequestFactory.setConnectionRequestTimeout(connectionRequestTimeout);
        return clientHttpRequestFactory;
    }

    /**
     * 设置HTTP连接管理器,连接池相关配置管理
     *
     * @return 客户端链接管理器
     */
    @Bean
    @Primary
    public HttpClientBuilder httpClientBuilder() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(poolingConnectionManager())
                .setProxy(getProxy());
        return httpClientBuilder;
    }

    public RequestConfig.Builder getDefaultRequestConfigBuilder() {
        return RequestConfig.custom()
                .setConnectTimeout(this.connectTimeout)
                .setSocketTimeout(this.socketTimeout)
                .setConnectionRequestTimeout(this.connectionRequestTimeout);
    }

    @Bean
    public HttpClientBuilder httpClientBuilderWithoutProxy() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(poolingConnectionManager());
        return httpClientBuilder;
    }

    private HttpHost getProxy() {
        return new HttpHost(proxyIp, proxyPort);
    }

    /**
     * 链接线程池管理,可以keep-alive不断开链接请求,这样速度会更快
     * MaxTotal 连接池最大连接数
     * DefaultMaxPerRoute 每个主机的并发
     * ValidateAfterInactivity 可用空闲连接过期时间,重用空闲连接时会先检查是否空闲时间超过这个时间，如果超过，释放socket重新建立
     *
     * @return
     */
    @Bean
    public HttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        poolingConnectionManager.setMaxTotal(maxTotal);
        poolingConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        poolingConnectionManager.setValidateAfterInactivity(validateAfterInactivity);
        return poolingConnectionManager;
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

    public int retryCount() {
        return retryCount;
    }

    public int maxTotal() {
        return maxTotal;
    }

    public int defaultMaxPerRoute() {
        return defaultMaxPerRoute;
    }

    public int validateAfterInactivity() {
        return validateAfterInactivity;
    }
}
