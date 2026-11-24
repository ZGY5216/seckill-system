package com.seckill.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seckill.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 秒杀订单。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("orders")
public class Orders extends BaseEntity {

    /** 订单编号（业务唯一） */
    private String orderNo;

    private Long userId;

    private Long activityId;

    private Long productId;

    private String productName;

    private Integer quantity;

    private BigDecimal totalAmount;

    /**
     * PENDING_PAYMENT 待支付 / PAID 已支付 / COMPLETED 已完成 / CANCELLED 已取消
     */
    private String status;
}
