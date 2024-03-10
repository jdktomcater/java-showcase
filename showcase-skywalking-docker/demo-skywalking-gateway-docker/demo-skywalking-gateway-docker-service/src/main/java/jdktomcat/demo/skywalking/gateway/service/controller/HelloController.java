package jdktomcat.demo.skywalking.gateway.service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制器
 */

@RestController
@Slf4j
public class HelloController {

    @GetMapping("/reset")
    public String say(String name) {
        log.info("reset {}", name);
        return "reset " + name;
    }

    @GetMapping("/keep")
    public String keep(String name) {
        log.info("keep {}", name);
        return "keep " + name;
    }
}
