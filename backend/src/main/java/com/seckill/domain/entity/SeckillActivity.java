package com.seckill.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seckill.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀活动：管理员创建，限定开始/结束时间与活动库存。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("seckill_activity")
public class SeckillActivity extends BaseEntity {

    private String title;

    private Long productId;

    /** 活动秒杀价 */
    private BigDecimal seckillPrice;

    /** 活动库存 */
    private Integer activityStock;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    /** NOT_STARTED 未开始 / ONGOING 进行中 / ENDED 已结束 */
    private String status;
}
