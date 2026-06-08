package com.jdktomcat.showcase.mq.rocketmq.transaction.producer.service.impl;

import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.component.MqProducerComponent;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.component.RedisComponent;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.mapper.MacRecordMapper;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.mapper.MerchantMapper;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.mapper.WithdrawOrderMapper;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.model.MacRecord;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.model.Merchant;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.model.WithdrawOrder;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.service.IWithdrawOrderService;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.util.OrderUtil;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.web.to.OrderTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;

@Slf4j
@Service
public class WithdrawOrderServiceImpl implements IWithdrawOrderService {

    @Autowired
    private WithdrawOrderMapper withdrawOrderMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private MacRecordMapper macRecordMapper;

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private MqProducerComponent mqProducerComponent;

    private static final String ORDER_PRE = "JSTWZYC";

    private static final String TABLE_INDEX_LOCK = "t_withdraw_order_index_lock";

    private static final String MERCHANT_BALANCE_LOCK = "merchant:balance:lock:";

    @Override
    @Transactional
    public WithdrawOrder createOrder(OrderTO orderTO) throws InterruptedException {
        Integer merchantId = orderTO.getMerchantId();
        redisComponent.exeDistSync(MERCHANT_BALANCE_LOCK + merchantId, 8, 10, orderTO, (order) -> {
            Merchant preMerchant = merchantMapper.selectById(order.getMerchantId());
            // 扣款
            int update = merchantMapper.subtract(merchantId, order.getAmount().add(preMerchant.getFee()));
            Assert.isTrue(update > 0, String.format("预扣费失败！merchantId: %s", order.getMerchantId()));
            // 扣款后商户信息
            Merchant postMerchant = merchantMapper.selectById(order.getMerchantId());
            // 保存账变记录信息
            MacRecord macRecord = new MacRecord();
            macRecord.setMerchantOrderId(order.getMerchantOrderId());
            macRecord.setMerchantId(order.getMerchantId());
            macRecord.setPre(preMerchant.getBalance());
            macRecord.setPreAvailable(preMerchant.getAvailable());
            macRecord.setPreFrozen(preMerchant.getFrozen());
            macRecord.setPost(postMerchant.getBalance());
            macRecord.setPostAvailable(postMerchant.getAvailable());
            macRecord.setPostFrozen(postMerchant.getFrozen());
            macRecord.setType(1);
            int insertRecord = macRecordMapper.insert(macRecord);
            Assert.isTrue(insertRecord > 0, String.format("保存商户帐变失败！merchantId: %s", order.getMerchantId()));
        });
        Merchant merchant = merchantMapper.selectById(orderTO.getMerchantId());
        WithdrawOrder withdrawOrder = new WithdrawOrder();
        withdrawOrder.setOrderIndex(redisComponent.getIncrementSeq(TABLE_INDEX_LOCK));
        withdrawOrder.setOrderId(OrderUtil.getPltOrderID(ORDER_PRE, merchant.getCode()));
        withdrawOrder.setMerchantOrderId(orderTO.getMerchantOrderId());
        withdrawOrder.setMerchantId(orderTO.getMerchantId());
        withdrawOrder.setAmount(orderTO.getAmount());
        withdrawOrder.setFee(merchant.getFee());
        withdrawOrder.setStatus(1);
        withdrawOrder.setOrderType(1);
        withdrawOrder.setPayType(912);
        withdrawOrder.setCreateTime(new Date());
        int insertOrder = withdrawOrderMapper.insert(withdrawOrder);
        Assert.isTrue(insertOrder > 0, String.format("保存订单失败！merchantOrderId: %s", orderTO.getMerchantOrderId()));
        return withdrawOrder;
    }


    @Override
    @Transactional
    public WithdrawOrder createOrderWithoutLock(OrderTO orderTO) {
        return mqProducerComponent.sendTransactionMessage(orderTO, (order)->{
            Integer merchantId = order.getMerchantId();
            Merchant merchant = merchantMapper.selectById(merchantId);
            // 扣款
            int update = merchantMapper.subtract(merchantId, order.getAmount().add(merchant.getFee()));
            Assert.isTrue(update > 0, String.format("预扣费失败！merchantId: %s", order.getMerchantId()));
            WithdrawOrder withdrawOrder = new WithdrawOrder();
            withdrawOrder.setOrderIndex(redisComponent.getIncrementSeq(TABLE_INDEX_LOCK));
            withdrawOrder.setOrderId(OrderUtil.getPltOrderID(ORDER_PRE, merchant.getCode()));
            withdrawOrder.setMerchantOrderId(order.getMerchantOrderId());
            withdrawOrder.setMerchantId(order.getMerchantId());
            withdrawOrder.setAmount(order.getAmount());
            withdrawOrder.setFee(merchant.getFee());
            withdrawOrder.setStatus(1);
            withdrawOrder.setOrderType(1);
            withdrawOrder.setPayType(912);
            withdrawOrder.setCreateTime(new Date());
            int insertOrder = withdrawOrderMapper.insert(withdrawOrder);
            Assert.isTrue(insertOrder > 0, String.format("保存订单失败！merchantOrderId: %s", order.getMerchantOrderId()));
            return withdrawOrder;
        });
    }
}
