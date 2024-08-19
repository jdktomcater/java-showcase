package com.jdktomcat.showcase.spring.webflux.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@RestController
@Slf4j
public class WebfluxController {

    @GetMapping("/sync")
    public String getSync(){
        log.info("sync start");
        String result = createStr();
        log.info("sync end");
        return result;
    }

    @GetMapping("/async")
    public Mono<String> getAsync(){
        log.info("async start");
        Mono<String> result = Mono.fromSupplier(this::createStr);
        log.info("async end");
        return result;
    }

    @GetMapping(value = "/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getFlux(){
        log.info("flux start");
        Flux<String> result = Flux.fromStream(IntStream.range(0, 10).mapToObj(i -> {
            try{
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
            return "Flux data--" + i;
        }));
        log.info("flux end");
        return result;
    }

    private String createStr(){
        try{
            TimeUnit.SECONDS.sleep(5);
        }catch (InterruptedException e) {
            log.error("Occur some exception",e);
        }
        return "Some String";
    }
}
