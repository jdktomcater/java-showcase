package com.jdktomcat.showcase.system.design.pay.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "account")
public class Account {

    @Id
    private Long userId;

    private BigDecimal total;

    private BigDecimal available;

    private BigDecimal frozen;
}
