package com.jdktomcat.showcase.system.design.risk.service.repository;

import com.jdktomcat.showcase.system.design.risk.client.enums.RiskLevel;
import com.jdktomcat.showcase.system.design.risk.service.domain.RiskCheckRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RiskCheckRecordRepository extends JpaRepository<RiskCheckRecord, Long> {

    Optional<RiskCheckRecord> findByRequestId(String requestId);

    List<RiskCheckRecord> findByUserIdAndCreatedAtAfter(Long userId, LocalDateTime since);

    @Query("SELECT r FROM RiskCheckRecord r WHERE r.userId = :userId " +
           "AND r.riskLevel IN (:levels) AND r.createdAt > :since " +
           "ORDER BY r.createdAt DESC")
    List<RiskCheckRecord> findRecentByUserAndLevel(
            @Param("userId") Long userId,
            @Param("levels") List<RiskLevel> levels,
            @Param("since") LocalDateTime since);
}
