package com.jdktomcat.showcase.spring.boot.x3.jasypt;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class JasyptApplication implements CommandLineRunner {

    /**
     * 服务入口方法
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        SpringApplication.run(JasyptApplication.class, args);
    }

    @Autowired
    private StringEncryptor stringEncryptor;


    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        String source = "targetpassword";
        String encryptValue = stringEncryptor.encrypt(source);
        System.out.println("加密结果: " + encryptValue);
        String plain = stringEncryptor.decrypt(encryptValue);
        System.out.println("解密结果: " + plain);
    }
}
