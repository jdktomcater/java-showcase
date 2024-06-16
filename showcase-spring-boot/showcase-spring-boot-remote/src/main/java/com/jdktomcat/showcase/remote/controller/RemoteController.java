package com.jdktomcat.showcase.remote.controller;

import com.jdktomcat.showcase.remote.vo.Content;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/remote")
public class RemoteController {

    @PostMapping(value = "/say")
    public String say(@RequestBody Content content) {
        return "hello " + content.getWord();
    }
}
