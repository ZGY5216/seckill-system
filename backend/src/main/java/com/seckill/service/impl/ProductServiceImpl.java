package com.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.BusinessException;
import com.seckill.common.ErrorCode;
import com.seckill.common.PageResult;
import com.seckill.domain.entity.Product;
import com.seckill.domain.entity.SeckillActivity;
import com.seckill.domain.vo.ProductDetailVO;
import com.seckill.domain.vo.ProductVO;
import com.seckill.mapper.ProductMapper;
import com.seckill.mapper.SeckillActivityMapper;
import com.seckill.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品浏览服务实现。
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final SeckillActivityMapper activityMapper;

    public ProductServiceImpl(ProductMapper productMapper, SeckillActivityMapper activityMapper) {
        this.productMapper = productMapper;
        this.activityMapper = activityMapper;
    }

    @Override
    public PageResult<ProductVO> listProducts(int pageNum, int pageSize, String keyword) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, "ON")
                .eq(Product::getDeleted, 0)
                .like(StringUtils.hasText(keyword), Product::getName, keyword)
                .orderByDesc(Product::getCreateTime);
        Page<Product> result = productMapper.selectPage(page, wrapper);

        List<Long> activityIds = result.getRecords().stream()
                .map(Product::getActivityId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, SeckillActivity> activityMap = activityIds.isEmpty()
                ? Collections.emptyMap()
                : activityMapper.selectBatchIds(activityIds).stream()
                .collect(Collectors.toMap(SeckillActivity::getId, a -> a));

        List<ProductVO> records = result.getRecords().stream()
                .map(p -> toVO(p, activityMap.get(p.getActivityId())))
                .collect(Collectors.toList());
        return PageResult.of(records, result.getTotal(), pageNum, pageSize);
    }

    @Override
    public ProductDetailVO getDetail(Long id) {
        Product product = productMapper.selectOne(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getId, id)
                        .eq(Product::getDeleted, 0)
                        .last("limit 1"));
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在或已下架");
        }
        SeckillActivity activity = product.getActivityId() == null
                ? null : activityMapper.selectById(product.getActivityId());

        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setDescription(product.getDescription());
        vo.setImageUrl(product.getImageUrl());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setSeckillPrice(product.getSeckillPrice());
        vo.setStock(product.getStock());
        vo.setStatus(product.getStatus());
        vo.setActivityId(product.getActivityId());
        if (activity != null) {
            vo.setActivityTitle(activity.getTitle());
            vo.setActivityStatus(activityStatus(activity));
            vo.setStartAt(activity.getStartAt());
            vo.setEndAt(activity.getEndAt());
        }
        return vo;
    }

    private ProductVO toVO(Product product, SeckillActivity activity) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setImageUrl(product.getImageUrl());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setSeckillPrice(product.getSeckillPrice());
        vo.setStock(product.getStock());
        vo.setStatus(product.getStatus());
        vo.setActivityId(product.getActivityId());
        if (activity != null) {
            vo.setActivityTitle(activity.getTitle());
            vo.setActivityStatus(activityStatus(activity));
        }
        return vo;
    }

    private String activityStatus(SeckillActivity activity) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartAt())) {
            return "NOT_STARTED";
        }
        if (now.isAfter(activity.getEndAt())) {
            return "ENDED";
        }
        return "ONGOING";
    }
}
