package com.jdktomcat.showcase.project.exchange.wallet.service.web;

import com.jdktomcat.showcase.project.exchange.wallet.common.dto.*;
import com.jdktomcat.showcase.project.exchange.wallet.service.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/users/{userId}/balances")
    public ApiResult<List<WalletBalanceDTO>> listBalances(@PathVariable Long userId) {
        return ApiResult.ok(walletService.listBalances(userId));
    }

    @GetMapping("/users/{userId}/balances/{currency}")
    public ApiResult<WalletBalanceDTO> getBalance(@PathVariable Long userId,
                                                  @PathVariable String currency) {
        return ApiResult.ok(walletService.getBalance(userId, currency));
    }

    @GetMapping("/users/{userId}/ledgers")
    public ApiResult<PageResult<WalletLedgerDTO>> listLedgers(@PathVariable Long userId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "20") int size) {
        return ApiResult.ok(walletService.listLedgers(userId, page, size));
    }

    @PostMapping("/accounts")
    public ApiResult<WalletOperationResultDTO> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ApiResult.ok(walletService.createAccount(request));
    }

    @PostMapping("/deposit")
    public ApiResult<WalletOperationResultDTO> deposit(@Valid @RequestBody WalletAmountRequest request) {
        return ApiResult.ok(walletService.deposit(request));
    }

    @PostMapping("/withdraw/freeze")
    public ApiResult<WalletOperationResultDTO> withdrawFreeze(@Valid @RequestBody WalletAmountRequest request) {
        return ApiResult.ok(walletService.withdrawFreeze(request));
    }

    @PostMapping("/withdraw/confirm")
    public ApiResult<WalletOperationResultDTO> withdrawConfirm(@Valid @RequestBody WalletAmountRequest request) {
        return ApiResult.ok(walletService.withdrawConfirm(request));
    }

    @PostMapping("/withdraw/cancel")
    public ApiResult<WalletOperationResultDTO> withdrawCancel(@Valid @RequestBody WalletAmountRequest request) {
        return ApiResult.ok(walletService.withdrawCancel(request));
    }

    @PostMapping("/freeze")
    public ApiResult<WalletOperationResultDTO> freeze(@Valid @RequestBody WalletAmountRequest request) {
        return ApiResult.ok(walletService.freeze(request));
    }

    @PostMapping("/unfreeze")
    public ApiResult<WalletOperationResultDTO> unfreeze(@Valid @RequestBody WalletAmountRequest request) {
        return ApiResult.ok(walletService.unfreeze(request));
    }

    @PostMapping("/settle/debit")
    public ApiResult<WalletOperationResultDTO> settleDebit(@Valid @RequestBody WalletAmountRequest request) {
        return ApiResult.ok(walletService.settleDebit(request));
    }

    @PostMapping("/settle/credit")
    public ApiResult<WalletOperationResultDTO> settleCredit(@Valid @RequestBody WalletAmountRequest request) {
        return ApiResult.ok(walletService.settleCredit(request));
    }

    @PostMapping("/transfer")
    public ApiResult<WalletOperationResultDTO> transfer(@Valid @RequestBody TransferRequest request) {
        return ApiResult.ok(walletService.transfer(request));
    }
}
