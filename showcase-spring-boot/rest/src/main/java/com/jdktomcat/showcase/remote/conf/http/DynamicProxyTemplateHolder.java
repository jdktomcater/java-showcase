package com.jdktomcat.showcase.remote.conf.http;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 动态切换RequestWrapper使用的RestTemplate实例达到隔离不同代理的目的
 * <p>
 * 目前存在的问题：
 * 与{@link RestTemplatePoolConfig}使用不同{@link HttpClientConnectionManager}实例，导致连接池实际配置为预期的两倍
 * <p>
 * 可以使用{@link MultiProxyHttpComponentsClientRequestFactoryDelegate}重写，提供{@link org.apache.http.client.HttpClient}层面的配置隔离
 *
 */
@Slf4j
public final class DynamicProxyTemplateHolder {

    private static final Object TEMPLATE_GENERATOR_LOCK = new Object();

    private static final String PROXY_DELIMITER = ":";

    private static final Map<HttpProxy, RestTemplate> TEMPLATE_HOLDER = Maps.newHashMap();

    private static DynamicProxyTemplateGenerator templateGenerator;

    private static final Map<String, HttpProxy> NAMED_PROXY_MAP = Maps.newHashMap();

    private static RestTemplateConfiguration templateConfiguration;

    private DynamicProxyTemplateHolder() {
    }

    public static HttpProxy getProxy(String name) {
        HttpProxy proxy = NAMED_PROXY_MAP.get(name);
        if (Objects.isNull(proxy)) {
            if (!name.contains(PROXY_DELIMITER)) {
                log.warn("未找到当前代理 {} 非预设代理请使用${ip}:${port}格式查询 本次请求启动默认代理", name);
                proxy = chooseDefaultProxy();
            } else {
                String[] split = name.split(PROXY_DELIMITER);
                return NAMED_PROXY_MAP.values().stream()
                        .filter(p -> p.identity().equals(name))
                        .findFirst()
                        .orElse(HttpProxy.of(split[0], Integer.valueOf(split[1])));
            }
        }
        return proxy;
    }

    public static HttpProxy chooseDefaultProxy() {
        return getProxy(getDefaultProxyName());
    }

    public static HttpProxy getInternalProxy() {
        return getProxy(RestTemplateConfiguration.INTERNAL);
    }

    private static String getDefaultProxyName() {
        return templateConfiguration.proxyActive() ? RestTemplateConfiguration.DEFAULT_PROXY : RestTemplateConfiguration.INTERNAL;
    }

    public static RestTemplate get(HttpProxy proxy) {
        HttpProxy curProxy = ObjectUtils.defaultIfNull(proxy, chooseDefaultProxy());
        if (!curProxy.isEnabled()) {
            log.warn("代理未开启 {} 切换到默认代理", curProxy);
            curProxy = chooseDefaultProxy();
        }
        RestTemplate template = TEMPLATE_HOLDER.get(curProxy);
        if (template == null) {
            // double check
            synchronized (TEMPLATE_GENERATOR_LOCK) {
                if ((template = TEMPLATE_HOLDER.get(curProxy)) == null) {
                    log.info("创建代理服务器连接 {}", curProxy);
                    template = templateGenerator.create(curProxy);
                    TEMPLATE_HOLDER.put(curProxy, template);
                    NAMED_PROXY_MAP.putIfAbsent(curProxy.name(), curProxy);
                    NAMED_PROXY_MAP.putIfAbsent(curProxy.identity(), curProxy);
                }
            }
        }
        return template;
    }

    static synchronized void init(RestTemplateBuilder restTemplateBuilder, StringHttpMessageConverter stringHttpMessageConverter,
                                  RestTemplateConfiguration restTemplateConfiguration) {
        templateGenerator = new DynamicProxyTemplateGenerator(restTemplateBuilder, stringHttpMessageConverter, restTemplateConfiguration);
        templateConfiguration = restTemplateConfiguration;
        List<HttpProxy> proxyList = templateConfiguration.getProxyList();
        NAMED_PROXY_MAP.putAll(proxyList.stream().collect(Collectors.toMap(HttpProxy::name, Function.identity())));
        proxyList.forEach(DynamicProxyTemplateHolder::get);
    }

    /**
     * RestTemplate生成器
     * <p>
     * 主要解决原有使用场景下，不同代理共用同一RestTemplate实例时产生的线程安全问题
     * 一个代理对应一个RestTemplate实例
     * 除 HttpClientConnectionManager 以外均不复用实例避免不同RestTemplate实例的配置互相影响
     * 复用 HttpClientConnectionManager 实例主要是考虑到项目配置中对httpClient连接池配置的数值都是按照全局考虑
     * 避免代理过多时连接配置以代理数量为基数成倍放大
     */
    static final class DynamicProxyTemplateGenerator {

        private final RestTemplateBuilder restTemplateBuilder;

        private final StringHttpMessageConverter stringHttpMessageConverter;

        private final HttpClientConnectionManager poolingConnectionManager;

        private final RestTemplateConfiguration templateConfiguration;

        private static final Map<HttpProxy, HttpComponentsClientHttpRequestFactory> FACTORY_MAP = Maps.newHashMap();

        private static final Object REQUEST_FACTORY_GENERATOR_LOCK = new Object();

        private DynamicProxyTemplateGenerator(RestTemplateBuilder restTemplateBuilder,
                                              StringHttpMessageConverter stringHttpMessageConverter,
                                              RestTemplateConfiguration templateConfiguration) {
            this.restTemplateBuilder = restTemplateBuilder;
            this.stringHttpMessageConverter = stringHttpMessageConverter;
            this.templateConfiguration = templateConfiguration;
            this.poolingConnectionManager = poolingConnectionManager();
        }

        private RestTemplate create(HttpProxy proxy) {
            return restTemplateBuilder.additionalMessageConverters(stringHttpMessageConverter).requestFactory(() -> getRequestFactory(proxy)).build();
        }

        private HttpComponentsClientHttpRequestFactory getRequestFactory(HttpProxy proxy) {
            HttpComponentsClientHttpRequestFactory requestFactory = FACTORY_MAP.get(proxy);
            if (requestFactory == null) {
                synchronized (REQUEST_FACTORY_GENERATOR_LOCK) {
                    if ((requestFactory = FACTORY_MAP.get(proxy)) == null) {
                        requestFactory = buildRequestFactory(getDefaultHttpClientBuilder(poolingConnectionManager, getDefaultRequestConfig()).setProxy(proxy.host()));
                        FACTORY_MAP.put(proxy, requestFactory);
                    }
                }
            }
            return requestFactory;
        }

        private HttpComponentsClientHttpRequestFactory buildRequestFactory(HttpClientBuilder httpClientBuilder) {
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClientBuilder.build());
            return requestFactory;
        }

        private HttpClientBuilder getDefaultHttpClientBuilder(HttpClientConnectionManager poolingConnectionManager, RequestConfig requestConfig) {
            return HttpClients.custom()
                    .setConnectionManager(poolingConnectionManager)
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(templateConfiguration.retryCount(), false))
                    .setDefaultRequestConfig(requestConfig)
                    .evictIdleConnections(10, TimeUnit.SECONDS)
                    .evictExpiredConnections()
                    .setKeepAliveStrategy((response, context) -> {
                        final HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                        while (it.hasNext()) {
                            final HeaderElement he = it.nextElement();
                            final String param = he.getName();
                            final String value = he.getValue();
                            if (value != null && "timeout".equalsIgnoreCase(param)) {
                                try {
                                    return (Long.parseLong(value) - 5) * 1000;
                                } catch(final NumberFormatException ignore) {
                                }
                            }
                        }
                        return templateConfiguration.keepAlive() * 1000L;
                    });
        }

        private RequestConfig getDefaultRequestConfig() {
            return RequestConfig.custom()
                    .setConnectTimeout(templateConfiguration.connectTimeout())
                    .setSocketTimeout(templateConfiguration.socketTimeout())
                    .setConnectionRequestTimeout(templateConfiguration.connectionRequestTimeout())
                    .build();
        }

        private HttpClientConnectionManager poolingConnectionManager() {
            PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
            poolingConnectionManager.setMaxTotal(templateConfiguration.maxTotal());
            poolingConnectionManager.setDefaultMaxPerRoute(templateConfiguration.defaultMaxPerRoute());
            poolingConnectionManager.setValidateAfterInactivity(templateConfiguration.validateAfterInactivity());
            return poolingConnectionManager;
        }
    }
}
