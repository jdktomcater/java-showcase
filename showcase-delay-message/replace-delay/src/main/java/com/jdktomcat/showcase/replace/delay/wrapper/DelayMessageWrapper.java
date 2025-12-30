package com.jdktomcat.showcase.replace.delay.wrapper;

import lombok.Data;

@Data
public class DelayMessageWrapper {

    /**
     * 消息原始创建时间
     */
    private long createTime;

    /**
     * 总时间
     */
    private int totalTimeout;

    /**
     * 剩余时间
     */
    private int expireTimeout;

    /**
     * 包装数据
     */
    private String data;

    /**
     * 构造
     *
     * @param createTime    消息原始创建时间
     * @param totalTimeout  总时间
     * @param expireTimeout 剩余时间
     * @param data          包装数据
     */
    public DelayMessageWrapper(long createTime, int totalTimeout, int expireTimeout, String data) {
        this.createTime = createTime;
        this.totalTimeout = totalTimeout;
        this.expireTimeout = expireTimeout;
        this.data = data;
    }
}
