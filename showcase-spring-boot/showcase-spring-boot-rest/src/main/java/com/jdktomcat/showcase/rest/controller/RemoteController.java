package com.jdktomcat.showcase.rest.controller;

import com.jdktomcat.showcase.rest.vo.Content;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/remote")
public class RemoteController {

    @PostMapping(value = "/say")
    public String say(Content content) {
        return "hello " + content.getWord();
    }
}
