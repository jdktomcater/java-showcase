package com.jdktomcat.showcase.system.design.pay.service;

import com.jdktomcat.showcase.system.design.pay.domain.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    @Transactional
    public void freeze(Long userId, Long amount) {
        Account acc = find(userId);
        if (acc.getAvailableBalance() < amount) {
            throw new RuntimeException("insufficient balance");
        }
        acc.setAvailableBalance(acc.getAvailableBalance() - amount);
        acc.setFrozenBalance(acc.getFrozenBalance() + amount);
    }

    @Transactional
    public void commit(Long userId, Long amount) {
        Account acc = find(userId);
        acc.setFrozenBalance(acc.getFrozenBalance() - amount);
        acc.setTotalBalance(acc.getTotalBalance() - amount);
    }

    @Transactional
    public void rollback(Long userId, Long amount) {
        Account acc = find(userId);
        acc.setFrozenBalance(acc.getFrozenBalance() - amount);
        acc.setAvailableBalance(acc.getAvailableBalance() + amount);
    }
}

