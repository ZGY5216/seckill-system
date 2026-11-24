package com.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.domain.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 条件扣减库存：仅当库存充足时才扣减，返回受影响行数（防超卖）。
     */
    @Update("UPDATE product SET stock = stock - #{quantity} " +
            "WHERE id = #{id} AND stock >= #{quantity} AND deleted = 0")
    int reduceStock(@Param("id") Long id, @Param("quantity") int quantity);

    /**
     * 订单取消时回补库存。
     */
    @Update("UPDATE product SET stock = stock + #{quantity} " +
            "WHERE id = #{id} AND deleted = 0")
    int returnStock(@Param("id") Long id, @Param("quantity") int quantity);
}
