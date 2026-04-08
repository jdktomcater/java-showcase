package com.jdktomcat.showcase.legacy.system.component.httpclient.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.jdktomcat.showcase.legacy.system.component.httpclient.config.DynamicProxyTemplateHolder;
import com.jdktomcat.showcase.legacy.system.component.httpclient.config.HttpProxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 请求包装类-充血模型
 *
 * <p>
 * 调用以下方法执行请求
 * {@link this#exec()} {@link this#exec(Class)}
 * </p>
 *
 * <p>
 * example:
 * // <a href="http://localhost:8080/example?name=FirstName&age=18">...</a>
 * Rwh.postJson("http://localhost:8080/example").uriVar("name", "FirstName").uriVar("age", 18).exec()
 * </p>
 *
 * <p>
 * 以下方法会覆盖原有属性：
 * {@link this#uriVars(Map)}
 * {@link this#headers(HttpHeaders)}
 * {@link this#body(Object)}
 * {@link this#mBody(Map)}
 * </p>
 * <p>
 * 以下方法会在原有属性上追加：
 * {@link this#uriVar(String, Object)}
 * {@link this#header(String, String)}
 * {@link this#mBody(Map)}
 * </p>
 *
 * @author luxun
 */
@SuppressWarnings({"unused", "SameParameterValue"})
@Slf4j
public class RequestWrapper implements Serializable {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String url;

    private final HttpMethod method;

    private HttpProxy proxy;

    private HttpHeaders contentType;

    private Map<String, Object> uriVars;

    private HttpHeaders headers;

    private Object body;

    private transient final Clear clear;

    protected RequestWrapper(String url, HttpMethod method) {
        this.url = url;
        this.method = method;
        this.clear = new Clear(this);
    }

    public static RequestWrapper of(String url, HttpMethod method) {
        return new RequestWrapper(url, method);
    }

    public Clear clear() {
        return clear;
    }

    public RequestWrapper proxy(HttpProxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public RequestWrapper proxy(String proxyIp, int proxyPort) {
        this.proxy = DynamicProxyTemplateHolder.getProxy(proxyIp, proxyPort);
        return this;
    }

    public RequestWrapper internal() {
        return this.proxy(DynamicProxyTemplateHolder.getInternalProxy());
    }

    public RequestWrapper external() {
        return this.proxy(DynamicProxyTemplateHolder.chooseDefaultProxy());
    }

    protected RequestWrapper contentType(HttpHeaders contentType) {
        this.contentType = contentType;
        return this;
    }

    public RequestWrapper uriVars(Map<String, Object> uriVars) {
        this.uriVars = nullSafePayload(uriVars);
        return this;
    }

    public RequestWrapper appendUriVars(Map<String, Object> uriVars) {
        this.uriVars = nullSafePayload(this.uriVars);
        this.uriVars.putAll(nullSafePayload(uriVars));
        return this;
    }

    public RequestWrapper uriVar(String name, Object value) {
        this.uriVars = nullSafePayload(this.uriVars);
        this.uriVars.put(name, value);
        return this;
    }

    public RequestWrapper headers(HttpHeaders headers) {
        this.headers = nullSafeHeaders(headers);
        return this;
    }

    public RequestWrapper appendHeaders(HttpHeaders headers) {
        this.headers = nullSafeHeaders(this.headers);
        this.headers.addAll(nullSafeHeaders(headers));
        return this;
    }

    public RequestWrapper header(String name, String value) {
        this.headers = nullSafeHeaders(headers);
        this.headers.add(name, value);
        return this;
    }

    public RequestWrapper body(Object body) {
        this.body = body;
        return this;
    }

    public RequestWrapper mBody(Map<?, ?> body) {
        Map<?, ?> bodyCopy = nullSafePayload(body);
        if (!(bodyCopy instanceof MultiValueMap)) {
            bodyCopy = Rwh.toMultiMap(bodyCopy);
        }
        this.body = bodyCopy;
        return this;
    }

    public ResponseEntity<String> exec() {
        return exec(String.class);
    }

    public <T> ResponseEntity<T> exec(Class<T> responseType) {
        try {
            return doExec(responseType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> ResponseEntity<T> doExec(Class<T> responseType) {
        try {
            // build url
            String urlCopy = url;
            if (MapUtils.isNotEmpty(this.uriVars)) {
                urlCopy = urlCopy + "?" + Joiner.on("&").useForNull("").withKeyValueSeparator("=").join(this.uriVars);
            }
            // build headers
            HttpHeaders headersCopy = contentType;
            headersCopy.putAll(nullSafeHeaders(this.headers));
            // build body
            Object bodyCopy = body;
            if (MediaType.APPLICATION_JSON_UTF8.equals(contentType.getContentType()) && !(bodyCopy instanceof String)) {
                bodyCopy = OBJECT_MAPPER.writeValueAsString(bodyCopy);
            }
            // build proxy
            HttpProxy proxyCopy = this.proxy;
            if (Objects.isNull(proxyCopy)) {
                proxyCopy = DynamicProxyTemplateHolder.chooseDefaultProxy();
            }
            RequestEntity<?> request = new RequestEntity<>(bodyCopy, headersCopy, method, URI.create(urlCopy));
            ResponseEntity<T> response = DynamicProxyTemplateHolder.get(proxyCopy).exchange(request, responseType);
            log.info("RequestWrapper#exec\nproxy: {}\nrequest: {}\nresponse{}", proxyCopy, request, response);
            return response;
        } catch (RuntimeException e) {
            log.error("RequestWrapper#exec catch RuntimeException.", e);
            throw e;
        } catch (Exception e) {
            log.error("RequestWrapper#exec catch " + e.getClass().getName() + ".", e);
            throw new RuntimeException(e);
        }
    }

    protected HttpHeaders nullSafeHeaders(HttpHeaders headers) {
        return MapUtils.isNotEmpty(headers) ? headers : new HttpHeaders();
    }

    protected <K, V> Map<K, V> nullSafePayload(Map<K, V> payload) {
        return MapUtils.isNotEmpty(payload) ? payload : Collections.emptyMap();
    }

    static class Clear {

        private final RequestWrapper wrapper;

        Clear(RequestWrapper wrapper) {
            this.wrapper = wrapper;
        }

        public Clear headers() {
            wrapper.headers = null;
            return this;
        }

        public Clear uriVars() {
            wrapper.uriVars = null;
            return this;
        }

        public Clear body() {
            wrapper.body = null;
            return this;
        }

        public RequestWrapper all() {
            return headers().uriVars().body().wrapper();
        }

        public RequestWrapper wrapper() {
            return wrapper;
        }

    }

}
