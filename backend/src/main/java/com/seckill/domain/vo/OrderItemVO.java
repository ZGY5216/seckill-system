package com.seckill.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细视图。
 */
@Data
public class OrderItemVO {

    private Long id;

    private Long productId;

    private String productName;

    private BigDecimal price;

    private Integer quantity;
}
