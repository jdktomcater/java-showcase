
package com.jdktomcat.showcase.system.design.pay.service;

import com.jdktomcat.showcase.system.design.pay.domain.PaymentOrder;
import com.jdktomcat.showcase.system.design.pay.repository.PaymentOrderRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PaymentService {

    private final PaymentOrderRepository orderRepo;
    private final AccountService accountService;
    private final ChannelService channelService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void pay(String requestId, Long userId, Long amount) {

        // 幂等：存在直接返回
        orderRepo.findByRequestId(requestId).ifPresent(o -> {
            throw new RuntimeException("duplicate request");
        });

        PaymentOrder order = new PaymentOrder();
        order.setRequestId(requestId);
        order.setUserId(userId);
        order.setAmount(amount);
        order.setStatus(PaymentOrder.Status.INIT);
        orderRepo.save(order);

        // 冻结余额
        accountService.freeze(userId, amount);

        order.setStatus(PaymentOrder.Status.PROCESSING);

        // 调用下游（模拟）
        boolean success = channelService.pay();

        if (success) {
            accountService.commit(userId, amount);
            order.setStatus(PaymentOrder.Status.SUCCESS);
            publisher.publishEvent(new PaymentResultEvent(order.getId(), true));
        } else {
            accountService.rollback(userId, amount);
            order.setStatus(PaymentOrder.Status.FAIL);
        }
    }
}

