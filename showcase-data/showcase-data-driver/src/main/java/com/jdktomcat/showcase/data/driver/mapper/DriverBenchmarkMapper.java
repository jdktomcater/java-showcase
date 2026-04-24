package com.jdktomcat.showcase.data.driver.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jdktomcat.showcase.data.driver.domain.DriverOrder;

@Mapper
public interface DriverBenchmarkMapper {

    long countAll();

    int deleteAll();

    int batchInsert(@Param("items") List<DriverOrder> items);

    DriverOrder selectById(@Param("id") Long id);

    List<DriverOrder> selectByStatus(@Param("status") String status, @Param("limit") int limit);

    List<Long> scanIdsByCreatedAt(@Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end,
                                  @Param("limit") int limit);

    BigDecimal sumAmountByStatus(@Param("status") String status);
}
