package com.jdktomcat.showcase.rest.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdktomcat.showcase.rest.conf.http.DynamicProxyTemplateHolder;
import com.jdktomcat.showcase.rest.conf.http.HttpProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;

import java.io.Serializable;
import java.net.URI;
import java.util.Objects;

/**
 * 请求包装类-充血模型
 * 调用以下方法执行请求
 * {@link this#exec()} {@link this#exec(Class)}
 */
@Slf4j
public class RequestWrapper implements Serializable {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String url;

    private final HttpMethod method;

    private HttpProxy proxy;

    private HttpHeaders contentType;

    private Object body;


    protected RequestWrapper(String url, HttpMethod method) {
        this.url = url;
        this.method = method;
    }

    public static RequestWrapper of(String url, HttpMethod method) {
        return new RequestWrapper(url, method);
    }


    public RequestWrapper proxy(HttpProxy proxy) {
        this.proxy = proxy;
        return this;
    }

    protected RequestWrapper contentType(HttpHeaders contentType) {
        this.contentType = contentType;
        return this;
    }
    public RequestWrapper body(Object body) {
        this.body = body;
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
            HttpHeaders headersCopy = contentType;
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
            RequestEntity<?> request = new RequestEntity<>(bodyCopy, headersCopy, method, URI.create(url));
            ResponseEntity<T> response = DynamicProxyTemplateHolder.get(proxyCopy).exchange(request, responseType);
            log.info("RequestWrapper#exec\nproxy: {}\nrequest: {}\nresponse{}", proxyCopy, request, response);
            return response;
        } catch (RuntimeException e) {
            log.error("RequestWrapper#exec catch RuntimeException.", e);
            throw e;
        } catch (Exception e) {
            log.error("RequestWrapper#exec catch {}.", e.getClass().getName(), e);
            throw new RuntimeException(e);
        }
    }
}
