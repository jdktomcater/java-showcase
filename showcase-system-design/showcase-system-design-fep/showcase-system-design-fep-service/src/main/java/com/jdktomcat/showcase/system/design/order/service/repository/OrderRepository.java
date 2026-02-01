package com.jdktomcat.showcase.system.design.order.service.repository;

import com.jdktomcat.showcase.system.design.order.service.domain.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<PaymentOrder, Long> {

}
