
package com.jdktomcat.showcase.system.design.fef.service.service;

import com.jdktomcat.showcase.system.design.channel.service.api.client.domain.Channel;
import com.jdktomcat.showcase.system.design.common.web.response.Response;
import com.jdktomcat.showcase.system.design.fef.service.domain.PaymentOrder;
import com.jdktomcat.showcase.system.design.fef.service.feign.channel.ChannelFeignClient;
import com.jdktomcat.showcase.system.design.fef.service.repository.PaymentOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class PaymentService {

    private final PaymentOrderRepository orderRepo;

    private final AccountService accountService;

    private final ChannelFeignClient channelFeignClient;

    public PaymentService(PaymentOrderRepository orderRepo,
                          AccountService accountService,
                          ChannelFeignClient channelFeignClient) {
        this.orderRepo = orderRepo;
        this.accountService = accountService;
        this.channelFeignClient = channelFeignClient;
    }

    @Transactional
    public String pay(String requestId, Long userId, BigDecimal amount) {
        // idempotent：return fail if exist
        orderRepo.findByRequestId(requestId).ifPresent(o -> {
            throw new RuntimeException("duplicate request");
        });
        // check user account exist
        if(accountService.find(userId).isEmpty()){
            throw new RuntimeException("user account not exist!");
        }
        // create payment order
        PaymentOrder order = new PaymentOrder();
        order.setRequestId(requestId);
        order.setUserId(userId);
        order.setAmount(amount);
        // status:init
        order.setStatus(PaymentOrder.Status.INIT);
        // save order
        orderRepo.save(order);
        // freeze account
        accountService.freeze(userId, amount);
        // change order status
        order.setStatus(PaymentOrder.Status.PROCESSING);
//        // 调用下游（模拟）
//        boolean success = channelService.pay();
//
//        if (success) {
//            accountService.commit(userId, amount);
//            order.setStatus(PaymentOrder.Status.SUCCESS);
//            publisher.publishEvent(new PaymentResultEvent(order.getId(), true));
//        } else {
//            accountService.rollback(userId, amount);
//            order.setStatus(PaymentOrder.Status.FAIL);
//        }
        Response<Channel> response = channelFeignClient.getChannel(1L);
        log.info("channel feign response:{}", response);
        return "success";
    }
}

