package com.seckill.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品详情视图。
 */
@Data
public class ProductDetailVO {

    private Long id;

    private String name;

    private String description;

    private String imageUrl;

    private BigDecimal originalPrice;

    private BigDecimal seckillPrice;

    private Integer stock;

    private String status;

    private Long activityId;

    private String activityTitle;

    private String activityStatus;

    private LocalDateTime startAt;

    private LocalDateTime endAt;
}
