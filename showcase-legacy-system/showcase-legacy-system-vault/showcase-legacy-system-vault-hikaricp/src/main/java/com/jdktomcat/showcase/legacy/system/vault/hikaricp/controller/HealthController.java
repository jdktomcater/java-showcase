package com.jdktomcat.showcase.legacy.system.vault.hikaricp.controller;

import com.jdktomcat.showcase.legacy.system.vault.simple.mapper.HealthMapper;
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

    @GetMapping("/current")
    public String current() {
        return mapper.currentUser();
    }

    @GetMapping("/current-user")
    public String currentUser() {
        return mapper.currentUser();
    }
}
