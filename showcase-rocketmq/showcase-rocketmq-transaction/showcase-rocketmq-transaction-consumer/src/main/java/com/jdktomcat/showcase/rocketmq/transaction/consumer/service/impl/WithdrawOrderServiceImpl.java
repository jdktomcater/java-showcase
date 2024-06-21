package com.jdktomcat.showcase.rocketmq.transaction.consumer.service.impl;

import com.jdktomcat.showcase.rocketmq.transaction.consumer.component.RedisComponent;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.entry.Order;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.mapper.MacRecordMapper;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.mapper.MerchantMapper;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.model.MacRecord;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.model.Merchant;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.service.IWithdrawOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
public class WithdrawOrderServiceImpl implements IWithdrawOrderService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private MacRecordMapper macRecordMapper;

    @Autowired
    private RedisComponent redisComponent;

    private static final String MERCHANT_BALANCE_LOCK = "merchant:balance:lock:";

    @Override
    public void saveMac(Order order) {
        Integer merchantId = order.getMerchantId();
        try {
            redisComponent.exeDistSync(MERCHANT_BALANCE_LOCK + merchantId, 8, 10, () -> {
                Merchant preMerchant = merchantMapper.selectById(order.getMerchantId());
                log.info("商户：{} 账变前账户余额记录,总余额：{} 可用余额：{} 冻结余额：{} 商户服务费：{}", merchantId, preMerchant.getBalance(), preMerchant.getAvailable(), preMerchant.getFrozen(), preMerchant.getFee());
                // 保存账变记录信息
                MacRecord macRecord = new MacRecord();
                macRecord.setMerchantOrderId(order.getMerchantOrderId());
                macRecord.setMerchantId(order.getMerchantId());
                macRecord.setPre(preMerchant.getBalance());
                macRecord.setPreAvailable(preMerchant.getAvailable());
                macRecord.setPreFrozen(preMerchant.getFrozen());
                macRecord.setPost(preMerchant.getBalance());
                macRecord.setPostAvailable(preMerchant.getAvailable().subtract(order.getAmount()));
                macRecord.setPostFrozen(preMerchant.getFrozen().add(order.getAmount()));
                macRecord.setType(1);
                int insertRecord = macRecordMapper.insert(macRecord);
                Assert.isTrue(insertRecord > 0, String.format("保存商户帐变失败！merchantId: %s", merchantId));
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
