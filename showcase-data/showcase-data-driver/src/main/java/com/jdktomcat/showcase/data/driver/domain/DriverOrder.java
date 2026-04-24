package com.jdktomcat.showcase.data.driver.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DriverOrder {

    private Long id;

    private String userName;

    private String status;

    private BigDecimal amount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
