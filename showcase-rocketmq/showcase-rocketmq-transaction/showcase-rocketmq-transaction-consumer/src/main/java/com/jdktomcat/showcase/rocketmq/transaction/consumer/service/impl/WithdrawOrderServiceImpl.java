package com.jdktomcat.showcase.rocketmq.transaction.consumer.service.impl;

import com.jdktomcat.showcase.rocketmq.transaction.consumer.mapper.MacRecordMapper;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.mapper.MerchantMapper;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.mapper.WithdrawOrderMapper;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.model.MacRecord;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.model.Merchant;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.model.WithdrawOrder;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.service.IWithdrawOrderService;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.util.OrderUtil;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.web.to.OrderTO;
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
        redisComponent.exeDistSync(MERCHANT_BALANCE_LOCK + merchantId, 8, 10, () -> {
            Merchant preMerchant = merchantMapper.selectById(orderTO.getMerchantId());
            log.info("商户：{} 账变前账户余额记录,总余额：{} 可用余额：{} 冻结余额：{} 商户服务费：{}", merchantId, preMerchant.getBalance(), preMerchant.getAvailable(), preMerchant.getFrozen(), preMerchant.getFee());
            // 扣款
            int update = merchantMapper.subtract(merchantId, orderTO.getAmount().add(preMerchant.getFee()));
            Assert.isTrue(update > 0, String.format("预扣费失败！merchantId: %s", orderTO.getMerchantId()));
            // 扣款后商户信息
            Merchant postMerchant = merchantMapper.selectById(orderTO.getMerchantId());
            log.info("商户：{} 账变后账户余额记录,总余额：{} 可用余额：{} 冻结余额：{} 商户服务费：{}", merchantId, postMerchant.getBalance(), postMerchant.getAvailable(), postMerchant.getFrozen(), postMerchant.getFee());
            // 保存账变记录信息
            MacRecord macRecord = new MacRecord();
            macRecord.setMerchantOrderId(orderTO.getMerchantOrderId());
            macRecord.setMerchantId(orderTO.getMerchantId());
            macRecord.setPre(preMerchant.getBalance());
            macRecord.setPreAvailable(preMerchant.getAvailable());
            macRecord.setPreFrozen(preMerchant.getFrozen());
            macRecord.setPost(postMerchant.getBalance());
            macRecord.setPostAvailable(postMerchant.getAvailable());
            macRecord.setPostFrozen(postMerchant.getFrozen());
            macRecord.setType(1);
            int insertRecord = macRecordMapper.insert(macRecord);
            Assert.isTrue(insertRecord > 0, String.format("保存商户帐变失败！merchantId: %s", orderTO.getMerchantId()));
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
