package com.jdktomcat.showcase.system.design.fep.service.repository;

import com.jdktomcat.showcase.system.design.fep.service.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * account data repository
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
