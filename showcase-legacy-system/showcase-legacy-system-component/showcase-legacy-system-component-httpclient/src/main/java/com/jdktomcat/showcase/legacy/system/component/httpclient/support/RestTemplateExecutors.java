package com.jdktomcat.showcase.legacy.system.component.httpclient.support;

import com.jdktomcat.showcase.legacy.system.component.httpclient.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 请求重试机制
 */
@Slf4j
@Component
public class RestTemplateExecutors {

    @Autowired
    private RestTemplate restTemplate;
    /**
     * 重试间隔及次數,单位为毫秒
     */
    private static int[] interval = new int[]{1000, 2000};

    /**
     * 支持循序间隔时间，重试调用的rest请求。
     *
     * @param url           请求地址
     * @param requestEntity 请求参数
     * @return 返回值
     */
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) {
        ResponseEntity<T> responseEntity = null;
        String message = null;
        // 设置代理IP相关
        HttpUtils.setProxy(restTemplate);
        for (int i = 0; i < interval.length; i++) {
            try {
                Thread.sleep(interval[i]);
                responseEntity = restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
                break;
            } catch (Exception e) {
                message = e.getMessage();
                log.error("调用失败", e);
                log.info("最大重试{}次,间隔{}毫秒，开始第{}次请求重试,URL:{}", interval.length, interval[i], i + 1, url);
            }
        }

        if (responseEntity == null) {
            if (message == null) {
                message = "null";
            }
            message = "查询异常，请和商户确认是否成功，异常信息：".concat(message);
            responseEntity = new ResponseEntity(message, HttpStatus.OK);
        }
        return responseEntity;
    }
}
