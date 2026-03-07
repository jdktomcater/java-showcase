package com.jdktomcat.showcase.legacy.system.vault.verify.controller;

import com.jdktomcat.showcase.legacy.system.vault.verify.mapper.HealthMapper;
import com.jdktomcat.showcase.legacy.system.vault.verify.service.VerifyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final HealthMapper mapper;

    private final VerifyService verifyService;

    public HealthController(HealthMapper mapper, VerifyService verifyService) {
        this.mapper = mapper;
        this.verifyService = verifyService;
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

    @GetMapping("/verify")
    public void verify() throws InterruptedException {
        verifyService.tranVerify();
    }

    @GetMapping("/exception")
    public void exception() throws InterruptedException {
        verifyService.exceptionVerify();
    }

    @GetMapping("/expand")
    public String expand(@RequestParam(defaultValue = "300") long holdMs) throws InterruptedException {
        return verifyService.expand(holdMs);
    }
}
