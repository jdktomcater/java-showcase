package com.jdktomcat.demo.snowflake.generate.service.impl;

import com.jdktomcat.demo.snowflake.generate.component.RedisComponent;
import com.jdktomcat.demo.snowflake.generate.mapper.WithdrawOrderMapper;
import com.jdktomcat.demo.snowflake.generate.model.WithdrawOrder;
import com.jdktomcat.demo.snowflake.generate.service.IWithdrawOrderService;
import com.jdktomcat.demo.snowflake.generate.util.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class WithdrawOrderServiceImpl implements IWithdrawOrderService {
    @Autowired
    private ExecutorService executors;
    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private WithdrawOrderMapper withdrawOrderMapper;


    public static String[] TARGETS = {"MW120240131205423765682300", "MW120240131205423773682300", "MW120240131205432593682300", "MW120240131205432601682300", "MW120240131205432608682300", "H3000054240131205437745628f00", "H3000074240131205444642629100", "H3000054240131205457361629100", "MW120240131205457394681100", "MW120240131205457402681100", "MW120240131205457410681100", "MW120240131205457420681400", "MW120240131205457413681400", "MW120240131205457428681401", "MW120240131205457420681401", "MW120240131205457428681400", "H3000054240131205457814629000", "H3002054240131205458285629100", "H3007404240131205459194629000", "H3001704240131205501708629100"};

    @Override
    public String insert() {
        for (int i = 0; i < 5; i++) {
            executors.submit(() -> {
                for (int index = 0; index < 10000; index++) {
                    Long id = SnowflakeIdWorker.createTableId();
                    if (!redisComponent.addSet(id)) {
                        log.error("生成id异常！：{}", id);
                    }
                }
            });
        }
        return "OK";
    }

    @Override
    public String generate() {
        for (String orderId : TARGETS) {
            WithdrawOrder entry = new WithdrawOrder();
            entry.setId(SnowflakeIdWorker.createTableId());
            entry.setOrderId(orderId);
            entry.setOrderStatus(0);
            entry.setParentOrderStatus(0);
            entry.setMerchantOrderId("merchant-" + orderId);
            entry.setPriorityMatchStatus(1);
            int result = withdrawOrderMapper.insert(entry);
            log.info("insert orderId:{} result:{}", orderId, result);
        }
        return "OK";
    }
}
