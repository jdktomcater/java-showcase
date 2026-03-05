package com.jdktomcat.showcase.legacy.system.optimize.parallel.controller;

import com.jdktomcat.showcase.legacy.system.optimize.parallel.service.ParallelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ParallelController {

    private final ParallelService parallelService;

    public ParallelController(ParallelService parallelService) {
        this.parallelService = parallelService;
    }

    @GetMapping("/serial")
    public List<String> serial() {
        return parallelService.serial(null);
    }

    @GetMapping("/parallel")
    public List<String> parallel() {
        return parallelService.serial(null);
    }
}
