package com.jdktomcat.showcase.system.design.pay.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "account")
public class Account {

    @Id
    private Long userId;

    private Long totalBalance;

    private Long availableBalance;

    private Long frozenBalance;
}
