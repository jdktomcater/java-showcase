package com.jdktomcat.showcase.swagger.service.impl;

import com.jdktomcat.showcase.swagger.service.IRemoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RemoteServiceImpl implements IRemoteService {

    @Override
    public String say(String word) {
        return "hello " + word;
    }
}
