package com.seckill.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seckill.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 秒杀商品。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
public class Product extends BaseEntity {

    private String name;

    private String description;

    private String imageUrl;

    /** 原价 */
    private BigDecimal originalPrice;

    /** 秒杀价 */
    private BigDecimal seckillPrice;

    /** 剩余库存 */
    private Integer stock;

    /** 关联秒杀活动 ID */
    private Long activityId;

    /** ON 上架 / OFF 下架 */
    private String status;
}
