package com.jdktomcat.demo.snowflake.redis.service;

import com.jdktomcat.demo.snowflake.redis.component.RedisComponent;
import com.jdktomcat.demo.snowflake.redis.util.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class SnowflakeService {
    @Autowired
    private ExecutorService executors;
    @Autowired
    private RedisComponent redisComponent;
    public void generate() {
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
    }
}
