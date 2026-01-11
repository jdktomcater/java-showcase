package com.jdktomcat.showcase.limiter.redis.token.controller;

import com.jdktomcat.showcase.limiter.redis.token.annotation.Limiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description descript
 * @date 2023/11/13
 */
@RestController
public class TestController {

    /**
     * test point
     *
     * @param name param
     * @return result
     */
    @Limiter(token = 10, lease = 2)
    @GetMapping("/limiter")
    public String say(String name) {
        return "hello " + name;
    }
}
