package com.seckill.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单视图对象。
 */
@Data
public class OrderVO {

    private Long id;

    private String orderNo;

    private Long userId;

    private Long activityId;

    private Long productId;

    private String productName;

    private String productImage;

    private Integer quantity;

    private BigDecimal totalAmount;

    /** PENDING_PAYMENT / PAID / COMPLETED / CANCELLED */
    private String status;

    private LocalDateTime createTime;

    private List<OrderItemVO> items;
}
