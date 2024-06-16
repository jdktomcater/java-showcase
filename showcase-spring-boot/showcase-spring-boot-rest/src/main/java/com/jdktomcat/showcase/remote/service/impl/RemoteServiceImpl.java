package com.jdktomcat.showcase.remote.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.jdktomcat.showcase.remote.conf.RemoteConfig;
import com.jdktomcat.showcase.remote.service.IRemoteService;
import com.jdktomcat.showcase.remote.util.RequestWrapper;
import com.jdktomcat.showcase.remote.util.Rwh;
import com.jdktomcat.showcase.remote.vo.Content;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NoHttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RemoteServiceImpl implements IRemoteService {

    @Autowired
    private RemoteConfig remoteConfig;


    @Override
    public String say(String word) {
        Content content = new Content();
        content.setWord(word);
        String paramJson = JSONObject.toJSONString(content);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");
        RequestWrapper requestWrapper = Rwh.internalPostJson(remoteConfig.getRemoteUrl());
        // 设置请求代理
        ResponseEntity<String> response;
        try {
            response = requestWrapper.contentType(headers).body(paramJson).exec();
        } catch (Exception exception) {
            log.error("请求出现异常！",exception);
            Throwable cause = exception;
            while (cause != null && !(cause instanceof NoHttpResponseException)) {
                cause = cause.getCause();
            }
            if (cause != null) {
                log.warn("服务端主动断开连接导致connection不可用，请求重试！");
                response = requestWrapper.body(paramJson).exec();
            } else {
                throw exception;
            }
        }
        return response.getBody();
    }
}
