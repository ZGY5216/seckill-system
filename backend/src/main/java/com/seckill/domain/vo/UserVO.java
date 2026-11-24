package com.seckill.domain.vo;

import lombok.Data;

/**
 * 登录用户视图对象（含 Token）。
 */
@Data
public class UserVO {

    private Long userId;

    private String username;

    private String role;

    private String realName;

    private String phone;

    private String email;

    private String avatar;

    private Integer status;

    private String token;
}
