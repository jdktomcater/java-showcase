package com.jdktomcat.showcase.project.exchange.risk.admin.repository;

import com.jdktomcat.showcase.project.exchange.risk.admin.domain.RiskRuleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RiskRuleRepository extends JpaRepository<RiskRuleEntity, Long>, JpaSpecificationExecutor<RiskRuleEntity> {

    Optional<RiskRuleEntity> findByRuleCode(String ruleCode);

    boolean existsByRuleCode(String ruleCode);

    Page<RiskRuleEntity> findByStatusOrderByPriorityAsc(String status, Pageable pageable);
}
