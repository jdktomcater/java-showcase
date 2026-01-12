
package com.jdktomcat.showcase.system.design.pay.domain;

import jakarta.persistence.*;

@Entity
public class PaymentOrder {

    @Id
    @GeneratedValue
    private Long id;

    private String requestId;
    private Long userId;
    private Long amount;
    private String status;

    public PaymentOrder() {}

    public PaymentOrder(String requestId, Long userId, Long amount, String status) {
        this.requestId = requestId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
    }

    // getters and setters omitted for brevity
}
