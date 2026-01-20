package com.jdktomcat.showcase.mybatis.plus.task;

import com.jdktomcat.showcase.mybatis.plus.entry.ChannelAccountRecord;
import com.jdktomcat.showcase.mybatis.plus.mapper.ChannelAccountRecordMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class InsertChannelTask implements Runnable{

    private ChannelAccountRecordMapper channelAccountRecordMapper;

    public InsertChannelTask(ChannelAccountRecordMapper channelAccountRecordMapper) {
        this.channelAccountRecordMapper = channelAccountRecordMapper;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        ChannelAccountRecord channelAccountRecord = new ChannelAccountRecord();
        channelAccountRecord.setPayChannelID(10L);
        channelAccountRecord.setOrderID("12345678");
        channelAccountRecord.setCreateTime(new Date());
        int insert = channelAccountRecordMapper.insert(channelAccountRecord);
        log.info("insert channelAccountRecord:{}", insert);
    }
}
