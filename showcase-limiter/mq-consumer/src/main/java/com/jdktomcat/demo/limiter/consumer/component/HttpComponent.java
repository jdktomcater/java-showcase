package com.jdktomcat.demo.limiter.consumer.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：http请求组件
 * @date 2023/11/8
 */
@Slf4j
@Component
public class HttpComponent {

    @Resource
    private RestTemplate restTemplate;


    public  String postForJson(String url, Map<String, Object> header, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");
        for (String key : header.keySet()) {
            headers.add(key, header.get(key).toString());
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(param, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        return response.getBody();
    }

}
