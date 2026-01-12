
package com.jdktomcat.showcase.system.design.pay.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "payment_order", uniqueConstraints = @UniqueConstraint(columnNames = "requestId"))
public class PaymentOrder {

    @Id
    @GeneratedValue
    private Long id;

    private String requestId;
    private Long userId;
    private Long amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Version
    private Long version;

    public enum Status {
        INIT, PROCESSING, SUCCESS, FAIL
    }
}
