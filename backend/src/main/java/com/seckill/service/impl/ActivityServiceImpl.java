package com.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seckill.common.BusinessException;
import com.seckill.common.ErrorCode;
import com.seckill.domain.dto.ActivitySaveDTO;
import com.seckill.domain.entity.Product;
import com.seckill.domain.entity.SeckillActivity;
import com.seckill.mapper.ProductMapper;
import com.seckill.mapper.SeckillActivityMapper;
import com.seckill.service.ActivityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 秒杀活动管理服务实现（管理员）。
 */
@Service
public class ActivityServiceImpl implements ActivityService {

    private final SeckillActivityMapper activityMapper;
    private final ProductMapper productMapper;

    public ActivityServiceImpl(SeckillActivityMapper activityMapper, ProductMapper productMapper) {
        this.activityMapper = activityMapper;
        this.productMapper = productMapper;
    }

    @Override
    public List<SeckillActivity> listAll() {
        return activityMapper.selectList(
                new LambdaQueryWrapper<SeckillActivity>()
                        .eq(SeckillActivity::getDeleted, 0)
                        .orderByDesc(SeckillActivity::getStartAt));
    }

    @Override
    @Transactional
    public void createActivity(ActivitySaveDTO dto) {
        validateTime(dto);
        Product product = requireProduct(dto.getProductId());

        SeckillActivity activity = new SeckillActivity();
        activity.setTitle(dto.getTitle());
        activity.setProductId(dto.getProductId());
        activity.setSeckillPrice(dto.getSeckillPrice());
        activity.setActivityStock(dto.getActivityStock());
        activity.setStartAt(dto.getStartAt());
        activity.setEndAt(dto.getEndAt());
        activity.setStatus("NOT_STARTED");
        activity.setDeleted(0);
        activityMapper.insert(activity);

        syncProduct(product, activity);
    }

    @Override
    @Transactional
    public void updateActivity(ActivitySaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "活动 ID 不能为空");
        }
        validateTime(dto);
        SeckillActivity activity = activityMapper.selectById(dto.getId());
        if (activity == null || activity.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "活动不存在");
        }
        Product product = requireProduct(dto.getProductId());

        activity.setTitle(dto.getTitle());
        activity.setProductId(dto.getProductId());
        activity.setSeckillPrice(dto.getSeckillPrice());
        activity.setActivityStock(dto.getActivityStock());
        activity.setStartAt(dto.getStartAt());
        activity.setEndAt(dto.getEndAt());
        activityMapper.updateById(activity);

        syncProduct(product, activity);
    }

    private void validateTime(ActivitySaveDTO dto) {
        if (!dto.getEndAt().isAfter(dto.getStartAt())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "结束时间必须晚于开始时间");
        }
    }

    private Product requireProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "关联商品不存在");
        }
        return product;
    }

    /** 活动信息回写商品：秒杀价、活动库存、上架状态、活动关联。 */
    private void syncProduct(Product product, SeckillActivity activity) {
        product.setSeckillPrice(activity.getSeckillPrice());
        product.setStock(activity.getActivityStock());
        product.setActivityId(activity.getId());
        product.setStatus("ON");
        productMapper.updateById(product);
    }
}
