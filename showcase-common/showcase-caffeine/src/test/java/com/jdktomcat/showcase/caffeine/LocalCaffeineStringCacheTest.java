package com.jdktomcat.showcase.caffeine;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class LocalCaffeineStringCacheTest extends TestCase {

    public void tearDown() throws Exception {
        super.tearDown();
    }

//    public void setUp() throws Exception {
//        super.setUp();
//        for (int i = 0; i < 10; i++) {
//            LocalCaffeineStringCache.localSessionCache.put("" + i, "" + System.currentTimeMillis());
//            TimeUnit.SECONDS.sleep(1);
//        }
//    }

    public void testLocalCache() throws InterruptedException {
        for (int i = 0; i < 10000; i++) {
            String key = "" + (i % 10);
            String value = LocalCaffeineStringCache.localSessionCache.get(key);
            log.info("key:{}, value:{}", key, value);
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }

    public void testSplit() throws InterruptedException {
        String content = "UEW_JianTX_H234251106152035870df8700_WECHAT_19#0|||{\\\"r\\\":358,\\\"sl\\\":333,\\\"s\\\":5,\\\"n\\\":\\\"JianTX\\\",\\\"m\\\":23,\\\"l\\\":\\\"19_0\\\",\\\"a\\\":100.00,\\\"o\\\":\\\"H234251106152035870df8700\\\",\\\"k\\\":\\\"FWHKc202511061519560012407\\\",\\\"c\\\":1762413635919,\\\"b\\\":\\\"张三\\\",\\\"i\\\":\\\"16.162.145.136\\\",\\\"bt\\\":\\\"WECHAT\\\",\\\"ar\\\":\\\"HK\\\",\\\"cr\\\":\\\"0\\\",\\\"mrk\\\":[\\\"UEWN_134_100.00_918\\\"],\\\"pk\\\":null,\\\"mk\\\":0,\\\"p\\\":1,\\\"pt\\\":918,\\\"rt\\\":1,\\\"cm\\\":null}";
        String[] entry = content.split("\\|\\|\\|");
        System.out.println(entry[0]);

    }

}