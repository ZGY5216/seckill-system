package com.seckill.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seckill.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户：普通用户 / 管理员。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private String username;

    private String password;

    private String realName;

    private String phone;

    private String email;

    private String avatar;

    /** USER / ADMIN */
    private String role;

    /** 1 启用 0 禁用 */
    private Integer status;
}
