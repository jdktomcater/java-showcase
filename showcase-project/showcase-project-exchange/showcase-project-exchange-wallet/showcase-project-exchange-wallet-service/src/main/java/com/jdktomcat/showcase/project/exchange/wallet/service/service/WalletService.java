package com.jdktomcat.showcase.project.exchange.wallet.service.service;

import com.jdktomcat.showcase.project.exchange.wallet.common.dto.*;
import com.jdktomcat.showcase.project.exchange.wallet.common.enums.LedgerEntryType;
import com.jdktomcat.showcase.project.exchange.wallet.common.enums.OperationStatus;
import com.jdktomcat.showcase.project.exchange.wallet.common.enums.WalletOperationType;
import com.jdktomcat.showcase.project.exchange.wallet.service.domain.WalletAccountEntity;
import com.jdktomcat.showcase.project.exchange.wallet.service.domain.WalletLedgerEntity;
import com.jdktomcat.showcase.project.exchange.wallet.service.domain.WalletOperationEntity;
import com.jdktomcat.showcase.project.exchange.wallet.service.exception.WalletBusinessException;
import com.jdktomcat.showcase.project.exchange.wallet.service.repository.WalletAccountRepository;
import com.jdktomcat.showcase.project.exchange.wallet.service.repository.WalletLedgerRepository;
import com.jdktomcat.showcase.project.exchange.wallet.service.repository.WalletOperationRepository;
import com.jdktomcat.showcase.project.exchange.wallet.service.support.WalletConverter;
import com.jdktomcat.showcase.project.exchange.wallet.service.support.WalletConverter.BalanceSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Exchange wallet core service.
 *
 * <p>Interview design highlights:
 * <ul>
 *   <li>Three-balance model: total = available + frozen</li>
 *   <li>Idempotency via (bizRequestId, operationType)</li>
 *   <li>Pessimistic row lock on account mutation</li>
 *   <li>Append-only ledger with before/after snapshots</li>
 *   <li>Two-phase withdraw: freeze -> confirm/cancel</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletAccountRepository accountRepository;
    private final WalletLedgerRepository ledgerRepository;
    private final WalletOperationRepository operationRepository;

    @Transactional(readOnly = true)
    public List<WalletBalanceDTO> listBalances(Long userId) {
        return accountRepository.findByUserIdOrderByCurrencyAsc(userId).stream()
                .map(WalletConverter::toBalanceDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public WalletBalanceDTO getBalance(Long userId, String currency) {
        WalletAccountEntity account = requireAccount(userId, currency);
        return WalletConverter.toBalanceDTO(account);
    }

    @Transactional(readOnly = true)
    public PageResult<WalletLedgerDTO> listLedgers(Long userId, int page, int size) {
        Page<WalletLedgerEntity> result = ledgerRepository.findByUserIdOrderByCreatedAtDesc(
                userId, PageRequest.of(page, size));
        return PageResult.of(
                result.getContent().stream().map(WalletConverter::toLedgerDTO).toList(),
                result.getTotalElements(),
                page,
                size);
    }

    @Transactional
    public WalletOperationResultDTO createAccount(CreateAccountRequest request) {
        return executeIdempotent(request.getBizRequestId(), WalletOperationType.CREATE_ACCOUNT, () -> {
            accountRepository.findByUserIdAndCurrency(request.getUserId(), request.getCurrency())
                    .ifPresent(account -> {
                        throw new WalletBusinessException("wallet account already exists");
                    });

            WalletAccountEntity account = newWalletAccount(request.getUserId(), request.getCurrency());
            account = accountRepository.save(account);

            WalletOperationEntity operation = saveOperation(
                    request.getBizRequestId(),
                    WalletOperationType.CREATE_ACCOUNT,
                    request.getUserId(),
                    null,
                    request.getCurrency(),
                    WalletConverter.zeroAmount(),
                    account.getId(),
                    null);

            return WalletConverter.toOperationResult(operation, account, false);
        });
    }

    @Transactional
    public WalletOperationResultDTO deposit(WalletAmountRequest request) {
        BigDecimal amount = WalletConverter.normalizeAmount(request.getAmount());
        return executeIdempotent(request.getBizRequestId(), WalletOperationType.DEPOSIT, () -> {
            WalletAccountEntity account = lockAccount(request.getUserId(), request.getCurrency());
            BalanceSnapshot before = BalanceSnapshot.of(account);
            account.setTotal(account.getTotal().add(amount));
            account.setAvailable(account.getAvailable().add(amount));
            WalletConverter.assertInvariant(account);
            account = accountRepository.save(account);
            appendLedger(account, request.getBizRequestId(), WalletOperationType.DEPOSIT, LedgerEntryType.CREDIT_AVAILABLE, amount, before, request.getRemark());
            WalletOperationEntity operation = saveOperation(
                    request.getBizRequestId(),
                    WalletOperationType.DEPOSIT,
                    request.getUserId(),
                    null,
                    request.getCurrency(),
                    amount,
                    account.getId(),
                    request.getRemark());
            return WalletConverter.toOperationResult(operation, account, false);
        });
    }

    @Transactional
    public WalletOperationResultDTO withdrawFreeze(WalletAmountRequest request) {
        BigDecimal amount = WalletConverter.normalizeAmount(request.getAmount());
        return executeIdempotent(request.getBizRequestId(), WalletOperationType.WITHDRAW_FREEZE, () -> {
            WalletAccountEntity account = lockAccount(request.getUserId(), request.getCurrency());
            ensureAvailable(account, amount);
            BalanceSnapshot before = BalanceSnapshot.of(account);

            account.setAvailable(account.getAvailable().subtract(amount));
            account.setFrozen(account.getFrozen().add(amount));
            WalletConverter.assertInvariant(account);
            account = accountRepository.save(account);

            appendLedger(account, request.getBizRequestId(), WalletOperationType.WITHDRAW_FREEZE,
                    LedgerEntryType.FREEZE, amount, before, request.getRemark());

            WalletOperationEntity operation = saveOperation(
                    request.getBizRequestId(),
                    WalletOperationType.WITHDRAW_FREEZE,
                    request.getUserId(),
                    null,
                    request.getCurrency(),
                    amount,
                    account.getId(),
                    request.getRemark());

            return WalletConverter.toOperationResult(operation, account, false);
        });
    }

    @Transactional
    public WalletOperationResultDTO withdrawConfirm(WalletAmountRequest request) {
        BigDecimal amount = WalletConverter.normalizeAmount(request.getAmount());
        return executeIdempotent(request.getBizRequestId(), WalletOperationType.WITHDRAW_CONFIRM, () -> {
            WalletAccountEntity account = lockAccount(request.getUserId(), request.getCurrency());
            ensureFrozen(account, amount);
            BalanceSnapshot before = BalanceSnapshot.of(account);

            account.setTotal(account.getTotal().subtract(amount));
            account.setFrozen(account.getFrozen().subtract(amount));
            WalletConverter.assertInvariant(account);
            account = accountRepository.save(account);

            appendLedger(account, request.getBizRequestId(), WalletOperationType.WITHDRAW_CONFIRM,
                    LedgerEntryType.DEBIT_FROZEN, amount, before, request.getRemark());

            WalletOperationEntity operation = saveOperation(
                    request.getBizRequestId(),
                    WalletOperationType.WITHDRAW_CONFIRM,
                    request.getUserId(),
                    null,
                    request.getCurrency(),
                    amount,
                    account.getId(),
                    request.getRemark());

            return WalletConverter.toOperationResult(operation, account, false);
        });
    }

    @Transactional
    public WalletOperationResultDTO withdrawCancel(WalletAmountRequest request) {
        BigDecimal amount = WalletConverter.normalizeAmount(request.getAmount());
        return executeIdempotent(request.getBizRequestId(), WalletOperationType.WITHDRAW_CANCEL, () -> {
            WalletAccountEntity account = lockAccount(request.getUserId(), request.getCurrency());
            ensureFrozen(account, amount);
            BalanceSnapshot before = BalanceSnapshot.of(account);

            account.setFrozen(account.getFrozen().subtract(amount));
            account.setAvailable(account.getAvailable().add(amount));
            WalletConverter.assertInvariant(account);
            account = accountRepository.save(account);

            appendLedger(account, request.getBizRequestId(), WalletOperationType.WITHDRAW_CANCEL,
                    LedgerEntryType.UNFREEZE, amount, before, request.getRemark());

            WalletOperationEntity operation = saveOperation(
                    request.getBizRequestId(),
                    WalletOperationType.WITHDRAW_CANCEL,
                    request.getUserId(),
                    null,
                    request.getCurrency(),
                    amount,
                    account.getId(),
                    request.getRemark());

            return WalletConverter.toOperationResult(operation, account, false);
        });
    }

    @Transactional
    public WalletOperationResultDTO freeze(WalletAmountRequest request) {
        BigDecimal amount = WalletConverter.normalizeAmount(request.getAmount());
        return executeIdempotent(request.getBizRequestId(), WalletOperationType.FREEZE, () -> {
            WalletAccountEntity account = lockAccount(request.getUserId(), request.getCurrency());
            ensureAvailable(account, amount);
            BalanceSnapshot before = BalanceSnapshot.of(account);

            account.setAvailable(account.getAvailable().subtract(amount));
            account.setFrozen(account.getFrozen().add(amount));
            WalletConverter.assertInvariant(account);
            account = accountRepository.save(account);

            appendLedger(account, request.getBizRequestId(), WalletOperationType.FREEZE,
                    LedgerEntryType.FREEZE, amount, before, request.getRemark());

            WalletOperationEntity operation = saveOperation(
                    request.getBizRequestId(),
                    WalletOperationType.FREEZE,
                    request.getUserId(),
                    null,
                    request.getCurrency(),
                    amount,
                    account.getId(),
                    request.getRemark());

            return WalletConverter.toOperationResult(operation, account, false);
        });
    }

    @Transactional
    public WalletOperationResultDTO unfreeze(WalletAmountRequest request) {
        BigDecimal amount = WalletConverter.normalizeAmount(request.getAmount());
        return executeIdempotent(request.getBizRequestId(), WalletOperationType.UNFREEZE, () -> {
            WalletAccountEntity account = lockAccount(request.getUserId(), request.getCurrency());
            ensureFrozen(account, amount);
            BalanceSnapshot before = BalanceSnapshot.of(account);

            account.setFrozen(account.getFrozen().subtract(amount));
            account.setAvailable(account.getAvailable().add(amount));
            WalletConverter.assertInvariant(account);
            account = accountRepository.save(account);

            appendLedger(account, request.getBizRequestId(), WalletOperationType.UNFREEZE,
                    LedgerEntryType.UNFREEZE, amount, before, request.getRemark());

            WalletOperationEntity operation = saveOperation(
                    request.getBizRequestId(),
                    WalletOperationType.UNFREEZE,
                    request.getUserId(),
                    null,
                    request.getCurrency(),
                    amount,
                    account.getId(),
                    request.getRemark());

            return WalletConverter.toOperationResult(operation, account, false);
        });
    }

    @Transactional
    public WalletOperationResultDTO settleDebit(WalletAmountRequest request) {
        BigDecimal amount = WalletConverter.normalizeAmount(request.getAmount());
        return executeIdempotent(request.getBizRequestId(), WalletOperationType.SETTLE_DEBIT, () -> {
            WalletAccountEntity account = lockAccount(request.getUserId(), request.getCurrency());
            ensureFrozen(account, amount);
            BalanceSnapshot before = BalanceSnapshot.of(account);

            account.setTotal(account.getTotal().subtract(amount));
            account.setFrozen(account.getFrozen().subtract(amount));
            WalletConverter.assertInvariant(account);
            account = accountRepository.save(account);

            appendLedger(account, request.getBizRequestId(), WalletOperationType.SETTLE_DEBIT,
                    LedgerEntryType.DEBIT_FROZEN, amount, before, request.getRemark());

            WalletOperationEntity operation = saveOperation(
                    request.getBizRequestId(),
                    WalletOperationType.SETTLE_DEBIT,
                    request.getUserId(),
                    null,
                    request.getCurrency(),
                    amount,
                    account.getId(),
                    request.getRemark());

            return WalletConverter.toOperationResult(operation, account, false);
        });
    }

    @Transactional
    public WalletOperationResultDTO settleCredit(WalletAmountRequest request) {
        BigDecimal amount = WalletConverter.normalizeAmount(request.getAmount());
        return executeIdempotent(request.getBizRequestId(), WalletOperationType.SETTLE_CREDIT, () -> {
            WalletAccountEntity account = lockAccount(request.getUserId(), request.getCurrency());
            BalanceSnapshot before = BalanceSnapshot.of(account);

            account.setTotal(account.getTotal().add(amount));
            account.setAvailable(account.getAvailable().add(amount));
            WalletConverter.assertInvariant(account);
            account = accountRepository.save(account);

            appendLedger(account, request.getBizRequestId(), WalletOperationType.SETTLE_CREDIT,
                    LedgerEntryType.CREDIT_AVAILABLE, amount, before, request.getRemark());

            WalletOperationEntity operation = saveOperation(
                    request.getBizRequestId(),
                    WalletOperationType.SETTLE_CREDIT,
                    request.getUserId(),
                    null,
                    request.getCurrency(),
                    amount,
                    account.getId(),
                    request.getRemark());

            return WalletConverter.toOperationResult(operation, account, false);
        });
    }

    @Transactional
    public WalletOperationResultDTO transfer(TransferRequest request) {
        if (request.getFromUserId().equals(request.getToUserId())) {
            throw new WalletBusinessException("transfer to self is not allowed");
        }
        BigDecimal amount = WalletConverter.normalizeAmount(request.getAmount());
        return executeIdempotent(request.getBizRequestId(), WalletOperationType.TRANSFER, () -> {
            AccountPair accounts = lockAccountPair(request.getFromUserId(), request.getToUserId(), request.getCurrency());
            WalletAccountEntity from = accounts.from();
            WalletAccountEntity to = accounts.to();

            ensureAvailable(from, amount);
            BalanceSnapshot fromBefore = BalanceSnapshot.of(from);
            BalanceSnapshot toBefore = BalanceSnapshot.of(to);

            from.setTotal(from.getTotal().subtract(amount));
            from.setAvailable(from.getAvailable().subtract(amount));
            to.setTotal(to.getTotal().add(amount));
            to.setAvailable(to.getAvailable().add(amount));

            WalletConverter.assertInvariant(from);
            WalletConverter.assertInvariant(to);

            from = accountRepository.save(from);
            to = accountRepository.save(to);

            appendLedger(from, request.getBizRequestId(), WalletOperationType.TRANSFER,
                    LedgerEntryType.TRANSFER_OUT, amount, fromBefore, request.getRemark());
            appendLedger(to, request.getBizRequestId(), WalletOperationType.TRANSFER,
                    LedgerEntryType.TRANSFER_IN, amount, toBefore, request.getRemark());

            WalletOperationEntity operation = saveOperation(
                    request.getBizRequestId(),
                    WalletOperationType.TRANSFER,
                    request.getFromUserId(),
                    request.getToUserId(),
                    request.getCurrency(),
                    amount,
                    from.getId(),
                    request.getRemark());

            return WalletConverter.toOperationResult(operation, from, false);
        });
    }

    private WalletOperationResultDTO executeIdempotent(String bizRequestId, WalletOperationType operationType, OperationExecutor executor) {
        var existing = operationRepository.findByBizRequestIdAndOperationType(bizRequestId, operationType);
        if (existing.isPresent()) {
            WalletOperationEntity operation = existing.get();
            WalletAccountEntity account = accountRepository.findByUserIdAndCurrency(operation.getUserId(), operation.getCurrency()).orElse(null);
            return WalletConverter.toOperationResult(operation, account, true);
        }
        try {
            return executor.execute();
        } catch (DataIntegrityViolationException ex) {
            var replay = operationRepository.findByBizRequestIdAndOperationType(bizRequestId, operationType).orElseThrow(() -> ex);
            WalletAccountEntity account = accountRepository.findByUserIdAndCurrency(replay.getUserId(), replay.getCurrency()).orElse(null);
            return WalletConverter.toOperationResult(replay, account, true);
        }
    }

    private WalletAccountEntity lockAccount(Long userId, String currency) {
        return accountRepository.findForUpdate(userId, currency).orElseThrow(() -> new WalletBusinessException("wallet account not found"));
    }

    /**
     * Lock two accounts in deterministic order to avoid transfer deadlocks.
     */
    private AccountPair lockAccountPair(Long fromUserId, Long toUserId, String currency) {
        long firstUserId = Math.min(fromUserId, toUserId);
        long secondUserId = Math.max(fromUserId, toUserId);
        WalletAccountEntity first = lockAccount(firstUserId, currency);
        WalletAccountEntity second = lockAccount(secondUserId, currency);
        if (fromUserId.equals(first.getUserId())) {
            return new AccountPair(first, second);
        }
        return new AccountPair(second, first);
    }

    private record AccountPair(WalletAccountEntity from, WalletAccountEntity to) {
    }

    private WalletAccountEntity requireAccount(Long userId, String currency) {
        return accountRepository.findByUserIdAndCurrency(userId, currency).orElseThrow(() -> new WalletBusinessException("wallet account not found"));
    }

    private WalletAccountEntity newWalletAccount(Long userId, String currency) {
        WalletAccountEntity account = new WalletAccountEntity();
        account.setUserId(userId);
        account.setCurrency(currency.toUpperCase());
        account.setTotal(WalletConverter.zeroAmount());
        account.setAvailable(WalletConverter.zeroAmount());
        account.setFrozen(WalletConverter.zeroAmount());
        account.setVersion(0L);
        return account;
    }

    private void ensureAvailable(WalletAccountEntity account, BigDecimal amount) {
        if (account.getAvailable().compareTo(amount) < 0) {
            throw new WalletBusinessException("insufficient available balance");
        }
    }

    private void ensureFrozen(WalletAccountEntity account, BigDecimal amount) {
        if (account.getFrozen().compareTo(amount) < 0) {
            throw new WalletBusinessException("insufficient frozen balance");
        }
    }

    private void appendLedger(WalletAccountEntity account,
                              String bizRequestId,
                              WalletOperationType operationType,
                              LedgerEntryType entryType,
                              BigDecimal amount,
                              BalanceSnapshot before,
                              String remark) {
        WalletLedgerEntity ledger = WalletConverter.buildLedger(account, bizRequestId, operationType, entryType, amount, before, remark);
        ledgerRepository.save(ledger);
    }

    private WalletOperationEntity saveOperation(String bizRequestId,
                                                WalletOperationType operationType,
                                                Long userId,
                                                Long counterpartyUserId,
                                                String currency,
                                                BigDecimal amount,
                                                Long accountId,
                                                String remark) {
        WalletOperationEntity operation = new WalletOperationEntity();
        operation.setBizRequestId(bizRequestId);
        operation.setOperationType(operationType);
        operation.setStatus(OperationStatus.SUCCESS);
        operation.setUserId(userId);
        operation.setCounterpartyUserId(counterpartyUserId);
        operation.setCurrency(currency.toUpperCase());
        operation.setAmount(amount);
        operation.setAccountId(accountId);
        operation.setRemark(remark);
        return operationRepository.save(operation);
    }

    @FunctionalInterface
    private interface OperationExecutor {
        WalletOperationResultDTO execute();
    }
}
