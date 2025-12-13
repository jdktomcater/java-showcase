package com.jdktomcat.showcase.common.threadpool.controller;

import com.jdktomcat.showcase.common.threadpool.service.ThreadPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/tp")
public class ThreadPoolController {

    @Autowired
    private ThreadPoolService threadPoolService;

    @GetMapping("/say")
    public String say(String name) {
        threadPoolService.say();
        return "hello" + name;
    }
}
