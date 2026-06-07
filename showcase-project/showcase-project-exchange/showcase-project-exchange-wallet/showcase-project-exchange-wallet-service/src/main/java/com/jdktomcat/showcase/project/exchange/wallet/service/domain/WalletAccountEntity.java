package com.jdktomcat.showcase.project.exchange.wallet.service.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * User wallet account per currency.
 * Invariant: total = available + frozen, all balances >= 0.
 */
@Getter
@Setter
@Entity
@Table(name = "wallet_account", indexes = {
        @Index(name = "idx_wallet_user_currency", columnList = "user_id, currency", unique = true)
})
public class WalletAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "currency", nullable = false, length = 16)
    private String currency;

    @Column(name = "total", nullable = false, precision = 30, scale = 8)
    private BigDecimal total;

    @Column(name = "available", nullable = false, precision = 30, scale = 8)
    private BigDecimal available;

    @Column(name = "frozen", nullable = false, precision = 30, scale = 8)
    private BigDecimal frozen;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
