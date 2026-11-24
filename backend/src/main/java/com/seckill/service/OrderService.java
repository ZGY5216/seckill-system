package com.seckill.service;

import com.seckill.common.PageResult;
import com.seckill.domain.dto.OrderCreateDTO;
import com.seckill.domain.vo.OrderVO;

/**
 * 秒杀订单服务。
 */
public interface OrderService {

    OrderVO createOrder(OrderCreateDTO dto);

    PageResult<OrderVO> listMyOrders(int pageNum, int pageSize);

    OrderVO getOrderDetail(Long id);

    void cancelOrder(Long id);

    void payOrder(Long id);

    void completeOrder(Long id);
}
