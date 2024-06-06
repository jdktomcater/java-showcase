package com.jdktomcat.showcase.webflux.controller;

import com.alibaba.fastjson.JSON;
import com.jdktomcat.showcase.webflux.entry.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello, Reactive");
    }

    @PostMapping("/save")
    public Mono<Boolean> saveUser(@RequestBody User user) {
        ReactiveHashOperations<String, Object, Object> hashOperations = reactiveStringRedisTemplate.opsForHash();
        return hashOperations.put("USER_HS", String.valueOf(user.getId()), JSON.toJSONString(user));
    }

    @GetMapping("/info/{id}")
    public Mono<User> info(@PathVariable Integer id) {
        ReactiveHashOperations<String, Object, Object> reactiveHashOperations = reactiveStringRedisTemplate.opsForHash();
        Mono<Object> hval = reactiveHashOperations.get("USER_HS", String.valueOf(id));
        return hval.map(e -> JSON.parseObject((String) e, User.class));
    }
}
