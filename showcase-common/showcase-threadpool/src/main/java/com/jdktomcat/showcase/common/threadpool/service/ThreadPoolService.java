package com.jdktomcat.showcase.common.threadpool.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ThreadPoolService {

    @Async
    public void say(){
        log.info("say");
    }
}
