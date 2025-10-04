package com.jdktomcat.showcase.caffeine.entry;

import java.util.Arrays;

public class ByteArrayValue {

    /**
     * 缓存key
     */
    private final byte[] key;

    public ByteArrayValue(byte[] key) {
        this.key = key;
    }

    public byte[] getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteArrayValue that = (ByteArrayValue) o;
        return Arrays.equals(this.key, that.key);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(key);
    }

}
