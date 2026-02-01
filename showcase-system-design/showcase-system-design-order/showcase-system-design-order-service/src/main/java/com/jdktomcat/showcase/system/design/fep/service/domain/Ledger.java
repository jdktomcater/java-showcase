package com.jdktomcat.showcase.system.design.fep.service.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "ledger")
public class Ledger {
    @Id
    private Long id;

    private Long paymentId;

    private Long userId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Direction direction;

    private BigDecimal totalBefore;

    private BigDecimal totalAfter;

    private BigDecimal availableBefore;

    private BigDecimal availableAfter;

    private BigDecimal frozenBefore;

    private BigDecimal frozenAfter;

    public enum Direction {
        DEBIT, FREEZE, COMMIT, ROLLBACK
    }
}
