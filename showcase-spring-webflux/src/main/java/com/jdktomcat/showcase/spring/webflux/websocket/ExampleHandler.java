package com.jdktomcat.showcase.spring.webflux.websocket;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

public class ExampleHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.receive().doOnNext(message -> {
                }).concatMap(message -> {
            return null;
        }).then();
    }
}
