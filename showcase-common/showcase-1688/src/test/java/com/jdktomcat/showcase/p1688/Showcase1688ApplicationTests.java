package com.jdktomcat.showcase.p1688;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@SpringBootTest
class Showcase1688ApplicationTests {

    @Test
    void contextLoads() throws UnsupportedEncodingException {
        String keywords = "金属";
        String uri = URLEncoder.encode(new String(keywords.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        System.out.println(uri);
    }

}
