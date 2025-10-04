package com.jdktomcat.showcase.caffeine;

import com.jdktomcat.showcase.caffeine.entry.ByteArrayKey;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LocalCaffeineObjectCacheTest extends TestCase {

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testLocalCache() throws InterruptedException {
        String[] sessionIds = {
                "0000000000000000000000000000",
                "1111111111111111111111111111",
                "2222222222222222222222222222",
                "3333333333333333333333333333",
                "4444444444444444444444444444",
                "5555555555555555555555555555",
                "6666666666666666666666666666",
                "7777777777777777777777777777",
                "8888888888888888888888888888",
                "9999999999999999999999999999"
        };
        for (int i = 0; i < 100; i++) {
            String key = sessionIds[(i % 10)];
            byte[] keyBytes = key.getBytes();
            ByteArrayKey byteArrayKey = new ByteArrayKey(keyBytes);
            Object value = LocalCaffeineObjectCache.localSessionCache.get(byteArrayKey);
            Object valueBackup = LocalCaffeineObjectCache.getSessionObject(SerializationUtils.serialize(keyBytes));
            log.info("StringKey:{} ObjectKey:{}, value:{} backupValue:{}", key, byteArrayKey, value, valueBackup);
            TimeUnit.MILLISECONDS.sleep(10);
        }
        String[] sessionIdBackup = {
                "0000000000000000000000000000",
                "1111111111111111111111111111",
                "2222222222222222222222222222",
                "3333333333333333333333333333",
                "4444444444444444444444444444",
                "5555555555555555555555555555",
                "6666666666666666666666666666",
                "7777777777777777777777777777",
                "8888888888888888888888888888",
                "9999999999999999999999999999"
        };
        for (int i = 0; i < 100; i++) {
            String key = sessionIdBackup[(i % 10)];
            byte[] keyBytes = key.getBytes();
            ByteArrayKey byteArrayKey = new ByteArrayKey(keyBytes);
            Object value = LocalCaffeineObjectCache.localSessionCache.get(byteArrayKey);
            Object valueBackup = LocalCaffeineObjectCache.getSessionObject(SerializationUtils.serialize(keyBytes));
            log.info("StringKey:{} ObjectKey:{}, value:{} backupValue:{}", key, byteArrayKey, value, valueBackup);
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }

    public void testAsyncCache() throws InterruptedException {
        String key = "0000000000000000000000000000";
        byte[] keyBytes = key.getBytes();
        ByteArrayKey byteArrayKey = new ByteArrayKey(keyBytes);
        Object value = LocalCaffeineObjectCache.localSessionCache.get(byteArrayKey);
        log.info("StringKey:{} ObjectKey:{}, value:{}", key, byteArrayKey, value);
        TimeUnit.MILLISECONDS.sleep(10);
    }
}