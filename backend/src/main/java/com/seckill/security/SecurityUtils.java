package com.seckill.security;

import com.seckill.common.BusinessException;
import com.seckill.common.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全上下文工具：获取当前登录主体。
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return loginUser;
    }

    public static Long getUserId() {
        return getLoginUser().getUserId();
    }

    public static String getRole() {
        return getLoginUser().getRole();
    }
}
