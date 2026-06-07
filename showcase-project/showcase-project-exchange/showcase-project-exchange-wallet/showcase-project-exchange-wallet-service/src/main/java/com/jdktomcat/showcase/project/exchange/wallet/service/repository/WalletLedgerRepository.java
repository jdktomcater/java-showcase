package com.jdktomcat.showcase.project.exchange.wallet.service.repository;

import com.jdktomcat.showcase.project.exchange.wallet.service.domain.WalletLedgerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletLedgerRepository extends JpaRepository<WalletLedgerEntity, Long> {

    Page<WalletLedgerEntity> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    Page<WalletLedgerEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
