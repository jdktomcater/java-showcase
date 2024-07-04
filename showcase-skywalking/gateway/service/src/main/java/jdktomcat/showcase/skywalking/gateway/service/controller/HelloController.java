package jdktomcat.showcase.skywalking.gateway.service.controller;

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
        log.warn("request host:{} ip:{} port:{}", request.getRemoteHost(), request.getRemoteAddr(),request.getRemotePort());
        log.warn("header:{}", request.getHeaderNames());
        log.info("keep name:{}", name);
        log.warn("request host:{} ip:{} port:{}", request.getRemoteHost(), request.getRemoteAddr(),request.getRemotePort());
        log.warn("header:{}", request.getHeaderNames());
        log.info("keep name:{}", name);
        log.warn("request host:{} ip:{} port:{}", request.getRemoteHost(), request.getRemoteAddr(),request.getRemotePort());
        log.warn("header:{}", request.getHeaderNames());
        log.info("keep name:{}", name);
        log.warn("request host:{} ip:{} port:{}", request.getRemoteHost(), request.getRemoteAddr(),request.getRemotePort());
        log.warn("header:{}", request.getHeaderNames());
        log.info("keep name:{}", name);
        log.warn("request host:{} ip:{} port:{}", request.getRemoteHost(), request.getRemoteAddr(),request.getRemotePort());
        log.warn("header:{}", request.getHeaderNames());
        log.info("keep name:{}", name);
        log.warn("request host:{} ip:{} port:{}", request.getRemoteHost(), request.getRemoteAddr(),request.getRemotePort());
        log.warn("header:{}", request.getHeaderNames());
        log.info("keep name:{}", name);
        log.warn("request host:{} ip:{} port:{}", request.getRemoteHost(), request.getRemoteAddr(),request.getRemotePort());
        log.warn("header:{}", request.getHeaderNames());
        log.info("keep name:{}", name);
        log.warn("request host:{} ip:{} port:{}", request.getRemoteHost(), request.getRemoteAddr(),request.getRemotePort());
        log.warn("header:{}", request.getHeaderNames());
        log.info("keep name:{}", name);
        return "keep " + name;
    }
}
