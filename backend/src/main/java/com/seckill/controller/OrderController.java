package com.seckill.controller;

import com.seckill.common.PageResult;
import com.seckill.common.Result;
import com.seckill.domain.dto.OrderCreateDTO;
import com.seckill.domain.vo.OrderVO;
import com.seckill.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 订单接口：秒杀下单、订单查询、取消、支付、收货。
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<OrderVO> create(@Valid @RequestBody OrderCreateDTO dto) {
        return Result.ok(orderService.createOrder(dto));
    }

    @GetMapping
    public Result<PageResult<OrderVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(orderService.listMyOrders(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        return Result.ok(orderService.getOrderDetail(id));
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.ok();
    }

    @PostMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        orderService.payOrder(id);
        return Result.ok();
    }

    @PostMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        orderService.completeOrder(id);
        return Result.ok();
    }
}
