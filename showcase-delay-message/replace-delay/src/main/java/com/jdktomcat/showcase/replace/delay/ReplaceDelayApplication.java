package com.jdktomcat.showcase.replace.delay;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReplaceDelayApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ReplaceDelayApplication.class, args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {

    }
}
