package com.pethealth.controller;

import com.pethealth.common.Result;
import com.pethealth.dto.LoginRequestDTO;
import com.pethealth.dto.RegisterRequestDTO;
import com.pethealth.dto.AuthResponseDTO;
import com.pethealth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 用户认证控制器
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "用户认证", description = "用户登录、注册、获取用户信息、登出、刷新Token、微信登录")
@Validated
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过手机号/密码登录")
    public Result<AuthResponseDTO> login(
            @Parameter(description = "登录请求参数", required = true)
            @Valid @RequestBody LoginRequestDTO loginRequest) {
        
        log.info("用户登录请求: loginType={}", loginRequest.getLoginType());
        
        AuthResponseDTO authResponse = userService.login(loginRequest);
        
        return Result.success("登录成功", authResponse);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "通过手机号注册新用户")
    public Result<AuthResponseDTO> register(
            @Parameter(description = "注册请求参数", required = true)
            @Valid @RequestBody RegisterRequestDTO registerRequest) {
        
        log.info("用户注册请求: phone={}", registerRequest.getPhone());
        
        AuthResponseDTO authResponse = userService.register(registerRequest);
        
        return Result.success("注册成功", authResponse);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    public Result<AuthResponseDTO.UserInfo> getCurrentUserInfo(HttpServletRequest request) {
        
        // 从请求属性中获取用户ID（由AuthInterceptor设置）
        Long userId = (Long) request.getAttribute("currentUserId");
        log.debug("从request获取到的userId: {}", userId);
        
        if (userId == null) {
            log.warn("userId为null，返回未授权");
            return Result.unauthorized("用户未登录");
        }
        
        log.info("获取用户信息: userId={}", userId);
        
        // 获取用户完整信息
        var user = userService.getUserInfo(userId);
        
        // 构造用户信息响应
        AuthResponseDTO.UserInfo userInfo = AuthResponseDTO.UserInfo.builder()
                .userId(user.getUserId().longValue())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .openid(user.getOpenid())
                .build();
        
        return Result.success("获取成功", userInfo);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录")
    public Result<Void> logout(HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("用户登出: userId={}", userId);
        
        // JWT是无状态的，服务端不需要特殊处理
        // 客户端只需要删除本地存储的token即可
        
        return Result.success("登出成功", null);
    }

    /**
     * 刷新Token，非强制级
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "刷新用户认证令牌")
    public Result<AuthResponseDTO> refreshToken(HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }
        
        log.info("刷新Token: userId={}", userId);
        
        // 重新生成Token
        var user = userService.getUserInfo(userId);
        String newToken = ((com.pethealth.utils.JwtTokenUtil) 
                          request.getAttribute("jwtTokenUtil")).generateToken(userId);
        
        // 构造响应
        AuthResponseDTO.UserInfo userInfo = AuthResponseDTO.UserInfo.builder()
                .userId(user.getUserId().longValue())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .openid(user.getOpenid())
                .build();
        
        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .accessToken(newToken)
                .tokenType("Bearer")
                .expiresIn(86400L) // 24小时
                .userInfo(userInfo)
                .build();
        
        return Result.success("Token刷新成功", authResponse);
    }

    /**
     * 微信登录，强制级
     */
    @PostMapping("/wxlogin")
    @Operation(summary = "微信登录", description = "通过微信code登录")
    public Result<AuthResponseDTO> wechatLogin(
            @Parameter(description = "微信登录参数", required = true)
            @Valid @RequestBody LoginRequestDTO loginRequest) {
        
        // 强制设置登录类型为微信
        loginRequest.setLoginType("wx");
        
        log.info("微信登录请求: code={}", loginRequest.getWxCode());
        
        AuthResponseDTO authResponse = userService.login(loginRequest);
        
        return Result.success("微信登录成功", authResponse);
    }
}
