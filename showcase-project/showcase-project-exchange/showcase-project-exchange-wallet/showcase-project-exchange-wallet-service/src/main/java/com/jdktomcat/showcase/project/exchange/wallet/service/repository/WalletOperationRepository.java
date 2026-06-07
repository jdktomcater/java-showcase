package com.jdktomcat.showcase.project.exchange.wallet.service.repository;

import com.jdktomcat.showcase.project.exchange.wallet.common.enums.WalletOperationType;
import com.jdktomcat.showcase.project.exchange.wallet.service.domain.WalletOperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletOperationRepository extends JpaRepository<WalletOperationEntity, Long> {

    Optional<WalletOperationEntity> findByBizRequestIdAndOperationType(String bizRequestId, WalletOperationType operationType);
}
