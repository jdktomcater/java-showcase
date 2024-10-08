package com.jdktomcat.showcase.p1688.controller;


import com.jdktomcat.showcase.p1688.component.FactoryInfoComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/factory")
@Slf4j
public class FactoryController {

    @Autowired
    private FactoryInfoComponent factoryInfoComponent;

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public String export(String keyword) {
        return factoryInfoComponent.export(keyword);
    }
}
