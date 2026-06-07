package com.jdktomcat.showcase.project.exchange.wallet.service.repository;

import com.jdktomcat.showcase.project.exchange.wallet.service.domain.WalletAccountEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WalletAccountRepository extends JpaRepository<WalletAccountEntity, Long> {

    Optional<WalletAccountEntity> findByUserIdAndCurrency(Long userId, String currency);

    List<WalletAccountEntity> findByUserIdOrderByCurrencyAsc(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from WalletAccountEntity a where a.userId = :userId and a.currency = :currency")
    Optional<WalletAccountEntity> findForUpdate(@Param("userId") Long userId, @Param("currency") String currency);
}
