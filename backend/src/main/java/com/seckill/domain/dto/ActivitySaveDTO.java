package com.seckill.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员创建/编辑秒杀活动入参。
 */
@Data
public class ActivitySaveDTO {

    /** 编辑时传入，新增可空 */
    private Long id;

    @NotBlank(message = "活动标题不能为空")
    private String title;

    @NotNull(message = "商品 ID 不能为空")
    private Long productId;

    @NotNull(message = "秒杀价不能为空")
    @DecimalMin(value = "0.01", message = "秒杀价需大于 0")
    private BigDecimal seckillPrice;

    @NotNull(message = "活动库存不能为空")
    @Min(value = 1, message = "活动库存至少为 1")
    private Integer activityStock;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startAt;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endAt;
}
