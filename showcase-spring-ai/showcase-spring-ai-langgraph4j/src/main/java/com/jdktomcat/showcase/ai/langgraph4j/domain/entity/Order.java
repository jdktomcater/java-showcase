package com.jdktomcat.showcase.ai.langgraph4j.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 商品品类（如 电子产品、服装、食品）
    private String category;
    // 订单日期
    private LocalDate orderDate;
    // 订单金额（元）
    private BigDecimal amount;
    // 销售数量
    private Integer quantity;
    // 销售区域（如 华北、华东）
    private String region;
}
