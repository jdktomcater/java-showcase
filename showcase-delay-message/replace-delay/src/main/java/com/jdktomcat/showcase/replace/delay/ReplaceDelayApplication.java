package com.jdktomcat.showcase.replace.delay;

import com.jdktomcat.showcase.replace.delay.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@SpringBootApplication
public class ReplaceDelayApplication implements CommandLineRunner {

    /**
     * 业务逻辑
     */
    @Autowired
    private BusinessService businessService;

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
        String merchantId = "merchantId";
        String userId = "userId";
        businessService.lock(merchantId,userId, new BigDecimal(100));
    }
}
