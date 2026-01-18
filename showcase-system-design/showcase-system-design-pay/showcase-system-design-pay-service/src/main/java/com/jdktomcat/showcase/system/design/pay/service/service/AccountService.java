package com.jdktomcat.showcase.system.design.pay.service.service;

import com.jdktomcat.showcase.system.design.pay.service.domain.Account;
import com.jdktomcat.showcase.system.design.pay.service.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * find user account
     *
     * @param userId account id
     * @return user account
     */
    public Optional<Account> find(Long userId) {
        return accountRepository.findById(userId);
    }

    @Transactional
    public void freeze(Long userId, BigDecimal amount) {
        Optional<Account> acc = find(userId);
        if (acc.isEmpty()) {
            throw new RuntimeException("account not exist!");
        }
        Account account = acc.get();
        if (account.getAvailable().compareTo(amount) < 0) {
            throw new RuntimeException("insufficient balance");
        }
        account.setAvailable(account.getAvailable().subtract(amount));
        account.setFrozen(account.getFrozen().add(amount));
        // save ledger log
        
    }

    @Transactional
    public void commit(Long userId, Long amount) {
//        Account acc = find(userId);
//        acc.setFrozenBalance(acc.getFrozenBalance() - amount);
//        acc.setTotalBalance(acc.getTotalBalance() - amount);
    }

    @Transactional
    public void rollback(Long userId, Long amount) {
//        Account acc = find(userId);
//        acc.setFrozenBalance(acc.getFrozenBalance() - amount);
//        acc.setAvailableBalance(acc.getAvailableBalance() + amount);
    }
}

