package com.jdktomcat.showcase.system.design.pay.api.repository;

import com.jdktomcat.showcase.system.design.pay.api.domain.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * pay order data repository
 */
@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    Optional<PaymentOrder> findByRequestId(String requestId);
}
