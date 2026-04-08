package com.jdktomcat.showcase.legacy.system.component.httpclient.util;

import com.google.common.collect.Lists;
import com.jdktomcat.showcase.legacy.system.component.httpclient.config.DynamicProxyTemplateHolder;
import org.apache.commons.collections.MapUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * RequestWrapperHelper
 * 三方项目大量发送http请求，使用此class频率较高
 * 因此className简写为Rwh，名称短且为特殊简写提示较为方便
 *
 * @author luxun
 */
@SuppressWarnings("unused")
public final class Rwh {

    private Rwh() {
    }

    public static RequestWrapper internalGet(String url) {
        return internal(buildGetWrapper(url));
    }

    public static RequestWrapper get(String url) {
        return buildGetWrapper(url);
    }

    public static RequestWrapper specialGet(String url) {
        return specialProxy(buildGetWrapper(url));
    }

    public static RequestWrapper customGet(String url, String proxyIp, int proxyPort) {
        return customGet(url, proxyIp, proxyPort, true);
    }

    public static RequestWrapper customGet(String url, String proxyIp, int proxyPort, boolean proxyEnabled) {
        return customProxy(buildGetWrapper(url), proxyIp, proxyPort, proxyEnabled);
    }

    public static RequestWrapper internalPost(String url) {
        return internal(buildPostWrapper(url));
    }

    public static RequestWrapper post(String url) {
        return buildPostWrapper(url);
    }

    public static RequestWrapper specialPost(String url) {
        return specialProxy(buildPostWrapper(url));
    }

    public static RequestWrapper customPost(String url, String proxyIp, int proxyPort) {
        return customPost(url, proxyIp, proxyPort, true);
    }

    public static RequestWrapper customPost(String url, String proxyIp, int proxyPort, boolean proxyEnabled) {
        return customProxy(buildPostWrapper(url), proxyIp, proxyPort, proxyEnabled);
    }

    public static RequestWrapper internalPostJson(String url) {
        return internal(buildPostJsonWrapper(url));
    }

    public static RequestWrapper postJson(String url) {
        return buildPostJsonWrapper(url);
    }

    public static RequestWrapper specialPostJson(String url) {
        return specialProxy(buildPostJsonWrapper(url));
    }

    public static RequestWrapper customPostJson(String url, String proxyIp, int proxyPort) {
        return customPostJson(url, proxyIp, proxyPort, true);
    }

    public static RequestWrapper customPostJson(String url, String proxyIp, int proxyPort, boolean proxyEnabled) {
        return customProxy(buildPostJsonWrapper(url), proxyIp, proxyPort, proxyEnabled);
    }

    private static RequestWrapper buildGetWrapper(String url) {
        return createRequestWrapper(url, HttpMethod.GET, Headers.form());
    }

    private static RequestWrapper buildPostWrapper(String url) {
        return createRequestWrapper(url, HttpMethod.POST, Headers.form());
    }

    private static RequestWrapper buildPostJsonWrapper(String url) {
        return createRequestWrapper(url, HttpMethod.POST, Headers.json());
    }

    private static RequestWrapper createRequestWrapper(String url, HttpMethod method, HttpHeaders contentType) {
        return RequestWrapper.of(url, method).contentType(contentType);
    }

    private static RequestWrapper internal(RequestWrapper ctx) {
        return ctx.proxy(DynamicProxyTemplateHolder.getInternalProxy());
    }

    private static RequestWrapper specialProxy(RequestWrapper ctx) {
        return ctx.proxy(DynamicProxyTemplateHolder.chooseDefaultSpecialProxy());
    }

    private static RequestWrapper customProxy(RequestWrapper ctx, String proxyIp, int proxyPort) {
        return customProxy(ctx, proxyIp, proxyPort, true);
    }

    private static RequestWrapper customProxy(RequestWrapper ctx, String proxyIp, int proxyPort, boolean proxyEnabled) {
        return ctx.proxy(proxyEnabled ?
                DynamicProxyTemplateHolder.getProxy(proxyIp, proxyPort) : DynamicProxyTemplateHolder.chooseDefaultProxy());
    }

    public static HttpHeaders toHeaders(Map<?, ?> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        nullSafePayload(headers).forEach((k, v) -> httpHeaders.add(String.valueOf(k), String.valueOf(v)));
        return httpHeaders;
    }

    public static <K, V> MultiValueMap<K, V> toMultiMap(Map<K, V> map) {
        MultiValueMap<K, V> valueMap = new LinkedMultiValueMap<>();
        nullSafePayload(map).forEach(valueMap::add);
        return valueMap;
    }

    static HttpHeaders nullSafeHeaders(HttpHeaders headers) {
        return MapUtils.isNotEmpty(headers) ? headers : new HttpHeaders();
    }

    static <K, V> Map<K, V> nullSafePayload(Map<K, V> payload) {
        return MapUtils.isNotEmpty(payload) ? payload : Collections.emptyMap();
    }

    public static class Headers {

        private static final List<Charset> DEFAULT_CHARSET_LIST = Lists.newArrayList(StandardCharsets.UTF_8);

        public static HttpHeaders form() {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAcceptCharset(DEFAULT_CHARSET_LIST);
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            return httpHeaders;
        }

        public static HttpHeaders json() {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAcceptCharset(DEFAULT_CHARSET_LIST);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
            return httpHeaders;
        }

    }

}
