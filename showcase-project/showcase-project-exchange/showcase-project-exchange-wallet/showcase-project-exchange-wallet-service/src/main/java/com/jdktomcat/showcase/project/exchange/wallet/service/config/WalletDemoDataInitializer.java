package com.jdktomcat.showcase.project.exchange.wallet.service.config;

import com.jdktomcat.showcase.project.exchange.wallet.common.dto.CreateAccountRequest;
import com.jdktomcat.showcase.project.exchange.wallet.common.dto.WalletAmountRequest;
import com.jdktomcat.showcase.project.exchange.wallet.service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
public class WalletDemoDataInitializer {

    private final WalletService walletService;

    @Bean
    @Profile("demo")
    CommandLineRunner seedDemoAccounts() {
        return args -> {
            createIfAbsent(10001L, "USDT", "demo-create-10001-usdt");
            createIfAbsent(10001L, "BTC", "demo-create-10001-btc");
            createIfAbsent(10002L, "USDT", "demo-create-10002-usdt");

            walletService.deposit(buildDeposit(10001L, "USDT", "10000", "demo-deposit-10001-usdt"));
            walletService.deposit(buildDeposit(10001L, "BTC", "1.5", "demo-deposit-10001-btc"));
            walletService.deposit(buildDeposit(10002L, "USDT", "5000", "demo-deposit-10002-usdt"));
        };
    }

    private void createIfAbsent(Long userId, String currency, String bizRequestId) {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setUserId(userId);
        request.setCurrency(currency);
        request.setBizRequestId(bizRequestId);
        try {
            walletService.createAccount(request);
        } catch (Exception ignored) {
            // demo profile may restart with existing in-memory data
        }
    }

    private WalletAmountRequest buildDeposit(Long userId, String currency, String amount, String bizRequestId) {
        WalletAmountRequest request = new WalletAmountRequest();
        request.setUserId(userId);
        request.setCurrency(currency);
        request.setAmount(new java.math.BigDecimal(amount));
        request.setBizRequestId(bizRequestId);
        request.setRemark("demo seed deposit");
        return request;
    }
}
