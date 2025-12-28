package com.jdktomcat.showcase.mybatis.plus.task;

import com.jdktomcat.showcase.mybatis.plus.entry.DepositOrderLog;
import com.jdktomcat.showcase.mybatis.plus.mapper.DepositOrderLogMapper;

import java.util.Date;

public class InsertDepositTask implements Runnable{

    private DepositOrderLogMapper depositOrderLogMapper;

    public InsertDepositTask(DepositOrderLogMapper depositOrderLogMapper) {
        this.depositOrderLogMapper = depositOrderLogMapper;
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
        DepositOrderLog depositOrderLog = new DepositOrderLog();
        depositOrderLog.setPlatformOrderId("Test000000000001");
        depositOrderLog.setOrderId("Test000000000002");
        depositOrderLog.setActionTime(new Date());
        depositOrderLog.setResume("Test000000000003");
        depositOrderLog.setScore(0L);
        depositOrderLog.setMatchResult(0);
        depositOrderLog.setPoolType(1);
        int insert = depositOrderLogMapper.insert(depositOrderLog);
        System.out.println(insert);
    }
}
