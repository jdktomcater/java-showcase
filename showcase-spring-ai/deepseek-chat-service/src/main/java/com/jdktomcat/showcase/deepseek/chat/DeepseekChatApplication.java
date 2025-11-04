package com.jdktomcat.showcase.deepseek.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class DeepseekChatApplication implements CommandLineRunner {



    public static void main(String[] args) {
        SpringApplication.run(DeepseekChatApplication.class, args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {

        log.info("bot long polling registered successfully!");
    }
}



