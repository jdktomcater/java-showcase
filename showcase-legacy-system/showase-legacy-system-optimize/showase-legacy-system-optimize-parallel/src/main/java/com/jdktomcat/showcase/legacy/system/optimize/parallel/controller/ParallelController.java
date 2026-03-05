package com.jdktomcat.showcase.legacy.system.optimize.parallel.controller;

import com.jdktomcat.showcase.legacy.system.optimize.parallel.service.ParallelService;
import com.jdktomcat.showcase.legacy.system.vault.simple.mapper.HealthMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParallelController {

    private final ParallelService parallelService;

    public ParallelController(ParallelService parallelService) {
        this.parallelService = parallelService;
    }

    @GetMapping("/serial")
    public String serial() {
        return mapper.currentUser();
    }

    @GetMapping("/parallel")
    public String serial() {
        return mapper.currentUser();
    }
}
