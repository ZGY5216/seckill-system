package com.seckill.service;

import com.seckill.domain.dto.ActivitySaveDTO;
import com.seckill.domain.entity.SeckillActivity;

import java.util.List;

/**
 * 秒杀活动管理服务（管理员）。
 */
public interface ActivityService {

    List<SeckillActivity> listAll();

    void createActivity(ActivitySaveDTO dto);

    void updateActivity(ActivitySaveDTO dto);
}
