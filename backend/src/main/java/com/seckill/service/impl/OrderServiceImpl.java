package com.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.BusinessException;
import com.seckill.common.ErrorCode;
import com.seckill.common.PageResult;
import com.seckill.domain.dto.OrderCreateDTO;
import com.seckill.domain.entity.OrderItem;
import com.seckill.domain.entity.Orders;
import com.seckill.domain.entity.Product;
import com.seckill.domain.entity.SeckillActivity;
import com.seckill.domain.entity.StockLog;
import com.seckill.domain.vo.OrderItemVO;
import com.seckill.domain.vo.OrderVO;
import com.seckill.engine.OrderStateMachine;
import com.seckill.engine.OrderStatus;
import com.seckill.mapper.OrderItemMapper;
import com.seckill.mapper.OrdersMapper;
import com.seckill.mapper.ProductMapper;
import com.seckill.mapper.SeckillActivityMapper;
import com.seckill.mapper.StockLogMapper;
import com.seckill.security.SecurityUtils;
import com.seckill.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 秒杀订单服务实现。
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final DateTimeFormatter NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrdersMapper ordersMapper;
    private final OrderItemMapper orderItemMapper;
    private final StockLogMapper stockLogMapper;
    private final ProductMapper productMapper;
    private final SeckillActivityMapper activityMapper;

    public OrderServiceImpl(OrdersMapper ordersMapper,
                            OrderItemMapper orderItemMapper,
                            StockLogMapper stockLogMapper,
                            ProductMapper productMapper,
                            SeckillActivityMapper activityMapper) {
        this.ordersMapper = ordersMapper;
        this.orderItemMapper = orderItemMapper;
        this.stockLogMapper = stockLogMapper;
        this.productMapper = productMapper;
        this.activityMapper = activityMapper;
    }

    @Override
    @Transactional
    public OrderVO createOrder(OrderCreateDTO dto) {
        Long userId = SecurityUtils.getUserId();

        // 1. 校验活动
        SeckillActivity activity = activityMapper.selectById(dto.getActivityId());
        if (activity == null || activity.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "秒杀活动不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartAt())) {
            throw new BusinessException(ErrorCode.ACTIVITY_NOT_STARTED);
        }
        if (now.isAfter(activity.getEndAt())) {
            throw new BusinessException(ErrorCode.ACTIVITY_ENDED);
        }

        // 2. 校验商品
        Product product = productMapper.selectById(activity.getProductId());
        if (product == null || !"ON".equals(product.getStatus())) {
            throw new BusinessException(ErrorCode.PRODUCT_OFF_SHELF);
        }

        // 3. 限购：同一活动存在未取消订单即视为已购
        Long bought = ordersMapper.selectCount(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getUserId, userId)
                        .eq(Orders::getActivityId, activity.getId())
                        .ne(Orders::getStatus, OrderStatus.CANCELLED)
                        .eq(Orders::getDeleted, 0));
        if (bought != null && bought > 0) {
            throw new BusinessException(ErrorCode.REPEAT_PURCHASE);
        }

        // 4. 条件扣减库存（数据库层防超卖）
        int rows = productMapper.reduceStock(product.getId(), dto.getQuantity());
        if (rows == 0) {
            throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH);
        }

        // 5. 创建订单
        BigDecimal totalAmount = activity.getSeckillPrice()
                .multiply(BigDecimal.valueOf(dto.getQuantity()));
        Orders order = new Orders();
        order.setOrderNo(generateOrderNo(userId));
        order.setUserId(userId);
        order.setActivityId(activity.getId());
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setQuantity(dto.getQuantity());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setDeleted(0);
        ordersMapper.insert(order);

        // 6. 订单明细
        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setPrice(activity.getSeckillPrice());
        item.setQuantity(dto.getQuantity());
        item.setCreateTime(LocalDateTime.now());
        orderItemMapper.insert(item);

        // 7. 库存流水
        StockLog stockLog = new StockLog();
        stockLog.setActivityId(activity.getId());
        stockLog.setProductId(product.getId());
        stockLog.setChangeType("DEDUCT");
        stockLog.setChangeCount(dto.getQuantity());
        product = productMapper.selectById(product.getId());
        stockLog.setRemainStock(product == null ? 0 : product.getStock());
        stockLog.setCreateTime(LocalDateTime.now());
        stockLogMapper.insert(stockLog);

        return buildVO(order, List.of(item));
    }

    @Override
    public PageResult<OrderVO> listMyOrders(int pageNum, int pageSize) {
        Long userId = SecurityUtils.getUserId();
        Page<Orders> page = new Page<>(pageNum, pageSize);
        Page<Orders> result = ordersMapper.selectPage(page,
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getUserId, userId)
                        .eq(Orders::getDeleted, 0)
                        .orderByDesc(Orders::getCreateTime));
        List<OrderVO> records = result.getRecords().stream().map(this::buildVO).collect(Collectors.toList());
        return PageResult.of(records, result.getTotal(), pageNum, pageSize);
    }

    @Override
    public OrderVO getOrderDetail(Long id) {
        Orders order = requireOwnedOrder(id);
        return buildVO(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        Orders order = requireOwnedOrder(id);
        OrderStateMachine.checkTransition(order.getStatus(), OrderStatus.CANCELLED);

        order.setStatus(OrderStatus.CANCELLED);
        ordersMapper.updateById(order);

        // 回补库存并留流水
        productMapper.returnStock(order.getProductId(), order.getQuantity());
        StockLog stockLog = new StockLog();
        stockLog.setActivityId(order.getActivityId());
        stockLog.setProductId(order.getProductId());
        stockLog.setChangeType("RETURN");
        stockLog.setChangeCount(order.getQuantity());
        Product product = productMapper.selectById(order.getProductId());
        stockLog.setRemainStock(product == null ? 0 : product.getStock());
        stockLog.setCreateTime(LocalDateTime.now());
        stockLogMapper.insert(stockLog);
    }

    @Override
    @Transactional
    public void payOrder(Long id) {
        Orders order = requireOwnedOrder(id);
        OrderStateMachine.checkTransition(order.getStatus(), OrderStatus.PAID);
        order.setStatus(OrderStatus.PAID);
        ordersMapper.updateById(order);
    }

    @Override
    @Transactional
    public void completeOrder(Long id) {
        Orders order = requireOwnedOrder(id);
        OrderStateMachine.checkTransition(order.getStatus(), OrderStatus.COMPLETED);
        order.setStatus(OrderStatus.COMPLETED);
        ordersMapper.updateById(order);
    }

    /** 校验订单存在且属于当前用户。 */
    private Orders requireOwnedOrder(Long id) {
        Long userId = SecurityUtils.getUserId();
        Orders order = ordersMapper.selectOne(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getId, id)
                        .eq(Orders::getDeleted, 0)
                        .last("limit 1"));
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权查看该订单");
        }
        return order;
    }

    private OrderVO buildVO(Orders order) {
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        return buildVO(order, items);
    }

    private OrderVO buildVO(Orders order, List<OrderItem> items) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setActivityId(order.getActivityId());
        vo.setProductId(order.getProductId());
        vo.setProductName(order.getProductName());
        vo.setQuantity(order.getQuantity());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setStatus(order.getStatus());
        vo.setCreateTime(order.getCreateTime());

        Product product = productMapper.selectById(order.getProductId());
        vo.setProductImage(product == null ? null : product.getImageUrl());

        vo.setItems(items.stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            itemVO.setId(item.getId());
            itemVO.setProductId(item.getProductId());
            itemVO.setProductName(item.getProductName());
            itemVO.setPrice(item.getPrice());
            itemVO.setQuantity(item.getQuantity());
            return itemVO;
        }).collect(Collectors.toList()));
        return vo;
    }

    private String generateOrderNo(Long userId) {
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "SK" + LocalDateTime.now().format(NO_FORMAT) + userId + random;
    }
}
