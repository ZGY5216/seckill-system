package com.seckill.engine;

/**
 * 订单状态常量。
 */
public final class OrderStatus {

    public static final String PENDING_PAYMENT = "PENDING_PAYMENT";
    public static final String PAID = "PAID";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELLED = "CANCELLED";

    private OrderStatus() {
    }
}
