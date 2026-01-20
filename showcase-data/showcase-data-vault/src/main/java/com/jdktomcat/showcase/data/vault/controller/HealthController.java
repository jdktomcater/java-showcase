package com.jdktomcat.showcase.data.vault.controller;

import com.jdktomcat.showcase.data.vault.mapper.HealthMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final HealthMapper mapper;

    public HealthController(HealthMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping("/ping")
    public int ping() {
        return mapper.ping();
    }
}
