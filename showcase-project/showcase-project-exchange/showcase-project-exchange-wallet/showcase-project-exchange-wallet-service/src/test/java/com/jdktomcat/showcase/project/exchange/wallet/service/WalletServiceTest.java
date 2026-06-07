package com.jdktomcat.showcase.project.exchange.wallet.service;

import com.jdktomcat.showcase.project.exchange.wallet.common.dto.*;
import com.jdktomcat.showcase.project.exchange.wallet.service.exception.WalletBusinessException;
import com.jdktomcat.showcase.project.exchange.wallet.service.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        walletService.createAccount(createAccountRequest(20001L, "USDT", "test-create-20001-usdt"));
        walletService.createAccount(createAccountRequest(20002L, "USDT", "test-create-20002-usdt"));
        walletService.deposit(amountRequest(20001L, "USDT", "1000", "test-deposit-20001"));
    }

    @Test
    void depositIncreasesAvailableBalance() {
        WalletBalanceDTO balance = walletService.getBalance(20001L, "USDT");
        assertEquals(new BigDecimal("1000.00000000"), balance.getAvailable());
        assertEquals(new BigDecimal("1000.00000000"), balance.getTotal());
        assertEquals(new BigDecimal("0.00000000"), balance.getFrozen());
    }

    @Test
    void freezeAndSettleSimulatesBuyOrder() {
        walletService.freeze(amountRequest(20001L, "USDT", "200", "test-freeze-order-1"));
        WalletBalanceDTO afterFreeze = walletService.getBalance(20001L, "USDT");
        assertEquals(new BigDecimal("800.00000000"), afterFreeze.getAvailable());
        assertEquals(new BigDecimal("200.00000000"), afterFreeze.getFrozen());

        walletService.settleDebit(amountRequest(20001L, "USDT", "200", "test-settle-debit-1"));
        WalletBalanceDTO afterSettle = walletService.getBalance(20001L, "USDT");
        assertEquals(new BigDecimal("800.00000000"), afterSettle.getTotal());
        assertEquals(new BigDecimal("800.00000000"), afterSettle.getAvailable());
        assertEquals(new BigDecimal("0.00000000"), afterSettle.getFrozen());
    }

    @Test
    void withdrawUsesTwoPhaseFlow() {
        walletService.withdrawFreeze(amountRequest(20001L, "USDT", "300", "test-withdraw-freeze-1"));
        WalletBalanceDTO frozen = walletService.getBalance(20001L, "USDT");
        assertEquals(new BigDecimal("700.00000000"), frozen.getAvailable());
        assertEquals(new BigDecimal("300.00000000"), frozen.getFrozen());

        walletService.withdrawConfirm(amountRequest(20001L, "USDT", "300", "test-withdraw-confirm-1"));
        WalletBalanceDTO confirmed = walletService.getBalance(20001L, "USDT");
        assertEquals(new BigDecimal("700.00000000"), confirmed.getTotal());
        assertEquals(new BigDecimal("0.00000000"), confirmed.getFrozen());
    }

    @Test
    void transferMovesBalanceBetweenUsers() {
        walletService.deposit(amountRequest(20002L, "USDT", "100", "test-deposit-20002"));

        TransferRequest transfer = new TransferRequest();
        transfer.setFromUserId(20001L);
        transfer.setToUserId(20002L);
        transfer.setCurrency("USDT");
        transfer.setAmount(new BigDecimal("150"));
        transfer.setBizRequestId("test-transfer-1");
        walletService.transfer(transfer);

        assertEquals(new BigDecimal("850.00000000"), walletService.getBalance(20001L, "USDT").getAvailable());
        assertEquals(new BigDecimal("250.00000000"), walletService.getBalance(20002L, "USDT").getAvailable());
    }

    @Test
    void duplicateRequestIsIdempotent() {
        WalletAmountRequest request = amountRequest(20001L, "USDT", "50", "test-idempotent-deposit");
        WalletOperationResultDTO first = walletService.deposit(request);
        WalletOperationResultDTO second = walletService.deposit(request);

        assertFalse(first.isIdempotentReplay());
        assertTrue(second.isIdempotentReplay());
        assertEquals(first.getOperationId(), second.getOperationId());
        assertEquals(new BigDecimal("1050.00000000"), walletService.getBalance(20001L, "USDT").getTotal());
    }

    @Test
    void insufficientBalanceIsRejected() {
        WalletAmountRequest request = amountRequest(20001L, "USDT", "2000", "test-insufficient-freeze");
        assertThrows(WalletBusinessException.class, () -> walletService.freeze(request));
    }

    private CreateAccountRequest createAccountRequest(Long userId, String currency, String bizRequestId) {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setUserId(userId);
        request.setCurrency(currency);
        request.setBizRequestId(bizRequestId);
        return request;
    }

    private WalletAmountRequest amountRequest(Long userId, String currency, String amount, String bizRequestId) {
        WalletAmountRequest request = new WalletAmountRequest();
        request.setUserId(userId);
        request.setCurrency(currency);
        request.setAmount(new BigDecimal(amount));
        request.setBizRequestId(bizRequestId);
        return request;
    }
}
