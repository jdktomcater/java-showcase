package com.jdktomcat.showcase.project.exchange.wallet.service.support;

import com.jdktomcat.showcase.project.exchange.wallet.common.dto.WalletBalanceDTO;
import com.jdktomcat.showcase.project.exchange.wallet.common.dto.WalletLedgerDTO;
import com.jdktomcat.showcase.project.exchange.wallet.common.dto.WalletOperationResultDTO;
import com.jdktomcat.showcase.project.exchange.wallet.common.enums.LedgerEntryType;
import com.jdktomcat.showcase.project.exchange.wallet.common.enums.OperationStatus;
import com.jdktomcat.showcase.project.exchange.wallet.common.enums.WalletOperationType;
import com.jdktomcat.showcase.project.exchange.wallet.service.domain.WalletAccountEntity;
import com.jdktomcat.showcase.project.exchange.wallet.service.domain.WalletLedgerEntity;
import com.jdktomcat.showcase.project.exchange.wallet.service.domain.WalletOperationEntity;
import com.jdktomcat.showcase.project.exchange.wallet.service.exception.WalletBusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class WalletConverter {

    private static final int SCALE = 8;

    private WalletConverter() {
    }

    public static WalletBalanceDTO toBalanceDTO(WalletAccountEntity entity) {
        return WalletBalanceDTO.builder()
                .accountId(entity.getId())
                .userId(entity.getUserId())
                .currency(entity.getCurrency())
                .total(entity.getTotal())
                .available(entity.getAvailable())
                .frozen(entity.getFrozen())
                .version(entity.getVersion())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static WalletLedgerDTO toLedgerDTO(WalletLedgerEntity entity) {
        return WalletLedgerDTO.builder()
                .id(entity.getId())
                .accountId(entity.getAccountId())
                .userId(entity.getUserId())
                .currency(entity.getCurrency())
                .bizRequestId(entity.getBizRequestId())
                .operationType(entity.getOperationType())
                .entryType(entity.getEntryType())
                .amount(entity.getAmount())
                .totalBefore(entity.getTotalBefore())
                .totalAfter(entity.getTotalAfter())
                .availableBefore(entity.getAvailableBefore())
                .availableAfter(entity.getAvailableAfter())
                .frozenBefore(entity.getFrozenBefore())
                .frozenAfter(entity.getFrozenAfter())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static WalletOperationResultDTO toOperationResult(WalletOperationEntity operation,
                                                             WalletAccountEntity account,
                                                             boolean idempotentReplay) {
        return WalletOperationResultDTO.builder()
                .operationId(operation.getId())
                .bizRequestId(operation.getBizRequestId())
                .operationType(operation.getOperationType())
                .status(operation.getStatus())
                .userId(operation.getUserId())
                .currency(operation.getCurrency())
                .amount(operation.getAmount())
                .balanceAfter(account == null ? null : toBalanceDTO(account))
                .idempotentReplay(idempotentReplay)
                .createdAt(operation.getCreatedAt())
                .build();
    }

    public static BigDecimal zeroAmount() {
        return BigDecimal.ZERO.setScale(SCALE, RoundingMode.UNNECESSARY);
    }

    public static BigDecimal normalizeAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new WalletBusinessException("amount must be positive");
        }
        return amount.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static void assertInvariant(WalletAccountEntity account) {
        if (account.getTotal().compareTo(BigDecimal.ZERO) < 0
                || account.getAvailable().compareTo(BigDecimal.ZERO) < 0
                || account.getFrozen().compareTo(BigDecimal.ZERO) < 0) {
            throw new WalletBusinessException("balance cannot be negative");
        }
        BigDecimal sum = account.getAvailable().add(account.getFrozen());
        if (account.getTotal().compareTo(sum) != 0) {
            throw new WalletBusinessException("balance invariant violated: total != available + frozen");
        }
    }

    public static WalletLedgerEntity buildLedger(WalletAccountEntity account,
                                                 String bizRequestId,
                                                 WalletOperationType operationType,
                                                 LedgerEntryType entryType,
                                                 BigDecimal amount,
                                                 BalanceSnapshot before,
                                                 String remark) {
        WalletLedgerEntity ledger = new WalletLedgerEntity();
        ledger.setAccountId(account.getId());
        ledger.setUserId(account.getUserId());
        ledger.setCurrency(account.getCurrency());
        ledger.setBizRequestId(bizRequestId);
        ledger.setOperationType(operationType);
        ledger.setEntryType(entryType);
        ledger.setAmount(amount);
        ledger.setTotalBefore(before.total());
        ledger.setTotalAfter(account.getTotal());
        ledger.setAvailableBefore(before.available());
        ledger.setAvailableAfter(account.getAvailable());
        ledger.setFrozenBefore(before.frozen());
        ledger.setFrozenAfter(account.getFrozen());
        ledger.setRemark(remark);
        return ledger;
    }

    public record BalanceSnapshot(BigDecimal total, BigDecimal available, BigDecimal frozen) {

        public static BalanceSnapshot of(WalletAccountEntity account) {
            return new BalanceSnapshot(account.getTotal(), account.getAvailable(), account.getFrozen());
        }
    }
}
