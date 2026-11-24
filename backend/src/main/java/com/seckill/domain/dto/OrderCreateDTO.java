package com.seckill.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 秒杀下单入参。
 */
@Data
public class OrderCreateDTO {

    @NotNull(message = "活动 ID 不能为空")
    private Long activityId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "数量至少为 1")
    @Max(value = 1, message = "秒杀活动每人限购一件")
    private Integer quantity;
}
