package com.jdktomcat.showcase.caffeine.entry;

import java.util.Arrays;

/**
 * 本地缓存key重载
 */
public class ByteArrayKey {

    /**
     * 缓存key
     */
    private final byte[] key;

    public ByteArrayKey(byte[] key) {
        this.key = key;
    }

    public byte[] getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteArrayKey that = (ByteArrayKey) o;
        return Arrays.equals(this.key, that.key);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(key);
    }
}