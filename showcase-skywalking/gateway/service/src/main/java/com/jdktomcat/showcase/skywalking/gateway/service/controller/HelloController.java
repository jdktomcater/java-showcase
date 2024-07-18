package com.jdktomcat.showcase.skywalking.gateway.service.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制器
 */

@RestController
@RequestMapping
@Slf4j
public class HelloController {

    @GetMapping("/reset")
    public String say(String name) {
        log.info("reset {}", name);
        return "reset " + name;
    }

    @GetMapping(value = "/keep")
    public String keep(HttpServletRequest request, String name) {
        String host = request.getRemoteHost();
        String addr = request.getRemoteAddr();
        int port = request.getRemotePort();
        for (int i = 0; i < 10; i++) {
            log.warn("request host:{} ip:{} port:{} name:{}", host, addr, port, name);
        }
        return "keep " + name;
    }
}
