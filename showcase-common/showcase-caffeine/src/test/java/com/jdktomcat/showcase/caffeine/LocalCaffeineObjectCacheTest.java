package com.jdktomcat.showcase.caffeine;

import com.jdktomcat.showcase.caffeine.entry.ByteArrayKey;
import com.jdktomcat.showcase.caffeine.entry.SysUser;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.shiro.subject.SimplePrincipalCollection;

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


    public void testObjectLocalCache() throws InterruptedException {
        String userName = "timmy";
        Long userId = 3693L;
        SimplePrincipalCollection[] simplePrincipalCollections = new SimplePrincipalCollection[2];
        simplePrincipalCollections[0] =  new SimplePrincipalCollection();
        SysUser sysUser1 = new SysUser();
        sysUser1.setUserId(userId);
        sysUser1.setUserName(userName);
        simplePrincipalCollections[0].add(sysUser1,"user_realm");

        byte[] keyBytes1 = SerializationUtils.serialize(simplePrincipalCollections[0]);
        LocalCaffeineObjectCache.localSessionCache.put(new ByteArrayKey(keyBytes1),"value111111111111");

        TimeUnit.MILLISECONDS.sleep(50);

        simplePrincipalCollections[1] =  new SimplePrincipalCollection();
        SysUser sysUser2 = new SysUser();
        sysUser2.setUserId(userId);
        sysUser2.setUserName(userName);
        simplePrincipalCollections[1].add(sysUser2,"user_realm");
        byte[] keyBytes2 = SerializationUtils.serialize(simplePrincipalCollections[1]);
        Object value = LocalCaffeineObjectCache.localSessionCache.get(new ByteArrayKey(keyBytes2));
        log.info("value:{}",value);
        log.info("key1:{}",keyBytes1);
        log.info("key2:{}",keyBytes2);
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