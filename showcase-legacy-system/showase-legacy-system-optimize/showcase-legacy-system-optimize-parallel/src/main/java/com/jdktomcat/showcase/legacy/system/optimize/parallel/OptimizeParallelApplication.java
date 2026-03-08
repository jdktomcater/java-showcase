package com.jdktomcat.showcase.legacy.system.optimize.parallel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class OptimizeParallelApplication {

    /**
     * application entry
     *
     * @param args params
     */
    public static void main(String[] args) {
        SpringApplication.run(OptimizeParallelApplication.class, args);
    }
}
