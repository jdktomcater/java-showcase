package com.jdktomcat.showcase.swagger.controller;

import com.jdktomcat.showcase.swagger.service.IRemoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "本地服务接口")
@RestController
@RequestMapping(value = "/local")
public class LocalController {

    @Autowired
    private IRemoteService remoteService;

    @ApiOperation(value = "打招呼")
    @GetMapping(value = "/say")
    public String say(String word) {
        return remoteService.say(word);
    }
}


