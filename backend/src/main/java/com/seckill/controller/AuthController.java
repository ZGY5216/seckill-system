package com.seckill.controller;

import com.seckill.common.Result;
import com.seckill.domain.dto.LoginDTO;
import com.seckill.domain.dto.RegisterDTO;
import com.seckill.domain.vo.UserVO;
import com.seckill.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口：注册、登录、当前用户、退出。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.ok();
    }

    @PostMapping("/login")
    public Result<UserVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.ok(authService.login(dto));
    }

    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.ok(authService.currentUser());
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }
}
