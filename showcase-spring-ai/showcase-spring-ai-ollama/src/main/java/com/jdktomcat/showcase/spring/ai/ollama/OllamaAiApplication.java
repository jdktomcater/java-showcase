package com.jdktomcat.showcase.spring.ai.ollama;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class OllamaAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(OllamaAiApplication.class, args);
    }
}
