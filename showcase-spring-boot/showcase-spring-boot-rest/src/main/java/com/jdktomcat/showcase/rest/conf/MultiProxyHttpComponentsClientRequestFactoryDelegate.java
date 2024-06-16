package com.jdktomcat.showcase.rest.conf;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * <p>
 * 兼容原有代码时才使用此类
 * 原有方式在每次使用RestTemplate前调用{@link HttpUtils#setProxy(RestTemplate)}，在并发情况下会产生线程安全问题
 * 导致实际使用的代理与预期不一致，使用此类替代spring提供的默认工厂类实现，提供{@link HttpClient}层面的配置隔离
 * </p>
 *
 * @author luxun
 */
@Slf4j
public class MultiProxyHttpComponentsClientRequestFactoryDelegate extends HttpComponentsClientHttpRequestFactory {

    @Override
    public HttpClient getHttpClient() {
        return super.getHttpClient();
    }
}
