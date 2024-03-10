package com.jdktomcat.demo.rocketmq.producer.message;

import lombok.Data;

import java.util.List;

@Data
public class DistributeMessage {

    private String type;

    private String username;

    private List<WithdrawOrderDto> withdrawOrderList;

    @Data
    public static class WithdrawOrderDto {
        private Long orderIndex;
        private String orderId;
        private String merchantOrderId;
    }
}
