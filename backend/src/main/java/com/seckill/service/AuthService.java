package com.seckill.service;

import com.seckill.domain.dto.LoginDTO;
import com.seckill.domain.dto.RegisterDTO;
import com.seckill.domain.vo.UserVO;

/**
 * 认证服务。
 */
public interface AuthService {

    void register(RegisterDTO dto);

    UserVO login(LoginDTO dto);

    UserVO currentUser();

    void logout();
}
