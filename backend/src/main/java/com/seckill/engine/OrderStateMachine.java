package com.seckill.engine;

import com.seckill.common.BusinessException;
import com.seckill.common.ErrorCode;

import java.util.Map;
import java.util.Set;

/**
 * 订单状态机：集中管理合法状态流转，非法跳转直接抛业务异常。
 *
 * <pre>
 * PENDING_PAYMENT --支付--> PAID --确认收货--> COMPLETED
 * PENDING_PAYMENT --取消/超时--> CANCELLED
 * </pre>
 */
public final class OrderStateMachine {

    private static final Map<String, Set<String>> ALLOWED = Map.of(
            OrderStatus.PENDING_PAYMENT, Set.of(OrderStatus.PAID, OrderStatus.CANCELLED),
            OrderStatus.PAID, Set.of(OrderStatus.COMPLETED),
            OrderStatus.COMPLETED, Set.of(),
            OrderStatus.CANCELLED, Set.of()
    );

    private OrderStateMachine() {
    }

    /** 判断 from -> to 是否合法 */
    public static boolean canTransition(String from, String to) {
        Set<String> targets = ALLOWED.get(from);
        return targets != null && targets.contains(to);
    }

    /** 校验流转，非法时抛业务异常 */
    public static void checkTransition(String from, String to) {
        if (!canTransition(from, to)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID,
                    "订单状态 " + from + " 不允许变更为 " + to);
        }
    }

    /** 是否终态（不可再流转） */
    public static boolean isTerminal(String status) {
        Set<String> targets = ALLOWED.get(status);
        return targets == null || targets.isEmpty();
    }
}
