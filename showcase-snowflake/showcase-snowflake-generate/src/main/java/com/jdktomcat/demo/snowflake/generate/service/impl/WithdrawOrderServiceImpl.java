package com.jdktomcat.demo.snowflake.generate.service.impl;

import com.jdktomcat.demo.snowflake.generate.component.RedisComponent;
import com.jdktomcat.demo.snowflake.generate.service.IWithdrawOrderService;
import com.jdktomcat.demo.snowflake.generate.util.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class WithdrawOrderServiceImpl implements IWithdrawOrderService {
    @Autowired
    private ExecutorService executors;
    @Autowired
    private RedisComponent redisComponent;
    @Override
    public String insert() {
        for(int i=0;i<5;i++){
            executors.submit(() -> {
                for(int index=0;index<10000;index++){
                    Long id = SnowflakeIdWorker.createTableId();
                    if(!redisComponent.addSet(id)){
                        log.error("生成id异常！：{}",id);
                    }
                }
            });
        }
        return "OK";
    }
}
