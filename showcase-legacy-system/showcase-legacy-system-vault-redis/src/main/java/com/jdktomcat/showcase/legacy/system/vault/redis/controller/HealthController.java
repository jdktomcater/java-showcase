package com.jdktomcat.showcase.legacy.system.vault.redis.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final StringRedisTemplate stringRedisTemplate;

    public HealthController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @GetMapping("/ping")
    public boolean ping() {
        return stringRedisTemplate.hasKey("noexistkey");
    }
}
