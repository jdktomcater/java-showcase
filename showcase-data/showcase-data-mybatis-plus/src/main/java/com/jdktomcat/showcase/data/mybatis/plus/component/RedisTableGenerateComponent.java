package com.jdktomcat.showcase.data.mybatis.plus.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 说明见接口
 */
@Service
@Slf4j
public class RedisTableGenerateComponent{

    /**
     * 前缀
     */
    private static final String LOCK_TABLE_ID_GENERATE = "yuhata:typay:lock:generate:table";

    /**
     * redis相关服务接口
     */
    @Autowired
    private RedisComponent redisComponent;

    /**
     * 主键生成
     *
     * @param tableName 表名
     * @return 主键
     */
    public Long tableIdGenerate(String tableName) {
        String reqKey = LOCK_TABLE_ID_GENERATE.concat(tableName);
        return redisComponent.getIncrementSeq(reqKey);
    }
}
