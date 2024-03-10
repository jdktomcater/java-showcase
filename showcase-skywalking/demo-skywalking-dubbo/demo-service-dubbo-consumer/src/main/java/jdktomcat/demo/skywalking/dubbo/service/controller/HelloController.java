package jdktomcat.demo.skywalking.dubbo.service.controller;

import jdktomcat.demo.skywalking.dubbo.service.api.HelloService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制器
 */

@RestController
public class HelloController {

    @DubboReference
    private HelloService helloService;


    @GetMapping("/say")
    public String say(String name){
        return helloService.say(name);
    }

}
