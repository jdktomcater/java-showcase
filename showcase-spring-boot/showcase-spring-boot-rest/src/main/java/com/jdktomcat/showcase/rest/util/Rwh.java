package com.jdktomcat.showcase.rest.util;

import com.google.common.collect.Lists;
import com.jdktomcat.showcase.rest.conf.http.DynamicProxyTemplateHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.jdktomcat.showcase.rest.util.Rwh.Headers.json;


/**
 * RequestWrapperHelper
 * 三方项目大量发送http请求，使用此class频率较高
 * 因此className简写为Rwh，名称短且为特殊简写提示较为方便
 */
public final class Rwh {

    private Rwh() {
    }
    public static RequestWrapper internalPostJson(String url) {
        return internal(buildPostJsonWrapper(url));
    }

    private static RequestWrapper buildPostJsonWrapper(String url) {
        return createRequestWrapper(url, HttpMethod.POST, json());
    }

    private static RequestWrapper createRequestWrapper(String url, HttpMethod method, HttpHeaders contentType) {
        return RequestWrapper.of(url, method).contentType(contentType);
    }

    private static RequestWrapper internal(RequestWrapper ctx) {
        return ctx.proxy(DynamicProxyTemplateHolder.getInternalProxy());
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
