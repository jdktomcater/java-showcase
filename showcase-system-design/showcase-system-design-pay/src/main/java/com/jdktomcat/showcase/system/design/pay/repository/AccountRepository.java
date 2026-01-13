package com.jdktomcat.showcase.system.design.pay.repository;

import com.jdktomcat.showcase.system.design.pay.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * account data repository
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
