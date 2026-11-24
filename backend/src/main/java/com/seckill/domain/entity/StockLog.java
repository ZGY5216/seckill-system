package com.seckill.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 库存变动流水：扣减 / 回补留痕，便于排查超卖与对账。
 */
@Data
@TableName("stock_log")
public class StockLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long activityId;

    private Long productId;

    /** DEDUCT 扣减 / RETURN 回补 */
    private String changeType;

    private Integer changeCount;

    private Integer remainStock;

    private LocalDateTime createTime;
}
