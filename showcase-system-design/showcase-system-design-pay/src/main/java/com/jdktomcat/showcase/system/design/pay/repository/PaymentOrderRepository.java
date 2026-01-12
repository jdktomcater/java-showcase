
package com.jdktomcat.showcase.system.design.pay.repository;

import com.jdktomcat.showcase.system.design.pay.domain.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    Optional<PaymentOrder> findByRequestId(String requestId);
}
