package com.jdktomcat.showcase.rest.controller;

import com.jdktomcat.showcase.rest.service.IRemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/local")
public class LocalController {

    @Autowired
    private IRemoteService remoteService;

    @GetMapping(value = "/say")
    public String say(String word) {
        return remoteService.say(word);
    }
}


