package com.seckill.controller;

import com.seckill.common.Result;
import com.seckill.domain.dto.ActivitySaveDTO;
import com.seckill.domain.entity.SeckillActivity;
import com.seckill.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 秒杀活动管理接口（管理员）。
 */
@RestController
@RequestMapping("/api/admin/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public Result<List<SeckillActivity>> list() {
        return Result.ok(activityService.listAll());
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody ActivitySaveDTO dto) {
        activityService.createActivity(dto);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody ActivitySaveDTO dto) {
        activityService.updateActivity(dto);
        return Result.ok();
    }
}
