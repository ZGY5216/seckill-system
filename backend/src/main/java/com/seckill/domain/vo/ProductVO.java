package com.seckill.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品列表项视图。
 */
@Data
public class ProductVO {

    private Long id;

    private String name;

    private String imageUrl;

    private BigDecimal originalPrice;

    private BigDecimal seckillPrice;

    private Integer stock;

    private String status;

    private Long activityId;

    private String activityTitle;

    /** NOT_STARTED / ONGOING / ENDED */
    private String activityStatus;
}
