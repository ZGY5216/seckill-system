package com.seckill;

import com.seckill.engine.OrderStateMachine;
import com.seckill.engine.OrderStatus;
import com.seckill.common.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 订单状态机纯逻辑测试（不依赖数据库与 Spring 容器）。
 */
class OrderStateMachineTest {

    @Test
    void pendingCanPayOrCancel() {
        assertTrue(OrderStateMachine.canTransition(OrderStatus.PENDING_PAYMENT, OrderStatus.PAID));
        assertTrue(OrderStateMachine.canTransition(OrderStatus.PENDING_PAYMENT, OrderStatus.CANCELLED));
    }

    @Test
    void paidCanCompleteOnly() {
        assertTrue(OrderStateMachine.canTransition(OrderStatus.PAID, OrderStatus.COMPLETED));
        assertFalse(OrderStateMachine.canTransition(OrderStatus.PAID, OrderStatus.CANCELLED));
    }

    @Test
    void terminalStatusCannotMove() {
        assertFalse(OrderStateMachine.canTransition(OrderStatus.COMPLETED, OrderStatus.PENDING_PAYMENT));
        assertFalse(OrderStateMachine.canTransition(OrderStatus.CANCELLED, OrderStatus.PENDING_PAYMENT));
        assertTrue(OrderStateMachine.isTerminal(OrderStatus.COMPLETED));
        assertTrue(OrderStateMachine.isTerminal(OrderStatus.CANCELLED));
    }

    @Test
    void illegalTransitionThrows() {
        assertThrows(BusinessException.class,
                () -> OrderStateMachine.checkTransition(OrderStatus.COMPLETED, OrderStatus.PAID));
        assertDoesNotThrow(() ->
                OrderStateMachine.checkTransition(OrderStatus.PENDING_PAYMENT, OrderStatus.PAID));
    }
}
