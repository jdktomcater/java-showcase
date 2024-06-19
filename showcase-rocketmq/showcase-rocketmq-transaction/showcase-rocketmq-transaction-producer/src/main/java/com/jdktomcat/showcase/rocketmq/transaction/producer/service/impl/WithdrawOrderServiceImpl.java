package com.jdktomcat.showcase.rocketmq.transaction.producer.service.impl;

import com.jdktomcat.showcase.rocketmq.transaction.producer.mapper.MacRecordMapper;
import com.jdktomcat.showcase.rocketmq.transaction.producer.mapper.MerchantMapper;
import com.jdktomcat.showcase.rocketmq.transaction.producer.mapper.WithdrawOrderMapper;
import com.jdktomcat.showcase.rocketmq.transaction.producer.model.Merchant;
import com.jdktomcat.showcase.rocketmq.transaction.producer.model.WithdrawOrder;
import com.jdktomcat.showcase.rocketmq.transaction.producer.service.IWithdrawOrderService;
import com.jdktomcat.showcase.rocketmq.transaction.producer.util.OrderUtil;
import com.jdktomcat.showcase.rocketmq.transaction.producer.web.OrderTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private static final String ORDER_PRE = "JSTWZYC";

    @Override
    @Transactional
    public WithdrawOrder createOrder(OrderTO orderTO) {
        Merchant merchant = merchantMapper.selectById(orderTO.getMerchantId());
        WithdrawOrder withdrawOrder = new WithdrawOrder();
        withdrawOrder.setOrderId(OrderUtil.getPltOrderID(ORDER_PRE,merchant.getCode()));
        withdrawOrder.setMerchantOrderId(orderTO.getMerchantOrderId());
        withdrawOrder.setPayAmount(orderTO.getAmount());
        withdrawOrder.setFee(merchant.getFee());
        withdrawOrder.setOrderStatus(1);
        withdrawOrder.setOrderType(1);
        withdrawOrder.setPayType(912);
        withdrawOrder.setCreateTime(new Date());
        
        return withdrawOrder;
    }
}
