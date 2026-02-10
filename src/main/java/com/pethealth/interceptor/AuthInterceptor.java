package com.pethealth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethealth.common.Result;
import com.pethealth.handler.BusinessException;
import com.pethealth.handler.UnauthorizedException;
import com.pethealth.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

/**
 * 认证拦截器 - 基于JWT验证用户令牌
 * 拦截未登录请求，保护API接口
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    // 公开路径列表（不需要认证的接口）
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/wxlogin",
            "/api/auth/refresh",
            "/api/public/",
            "/swagger-ui/",
            "/v3/api-docs/",
            "/webjars/",
            "/doc.html",
            "/favicon.ico"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        log.debug("认证拦截器: {} {} - 开始验证", method, requestUri);

        // 1. 检查是否为公开路径
        if (isPublicPath(requestUri)) {
            log.debug("公开路径，跳过认证: {}", requestUri);
            return true;
        }

        // 2. 从请求头获取Token
        String token = getTokenFromRequest(request);
        if (token == null) {
            log.warn("缺少认证令牌: {} {}", method, requestUri);
            // 抛出异常，交由 GlobalExceptionHandler 处理
            throw new UnauthorizedException("缺少认证令牌");
        }

        try {
            // 3. 验证Token
            if (!jwtTokenUtil.validateToken(token)) {
                log.warn("认证令牌无效: {} {}", method, requestUri);
                // 抛出异常，交由 GlobalExceptionHandler 处理
                throw new UnauthorizedException("认证令牌已过期或无效");
            }

            // 4. 解析用户ID并设置到请求属性中
            Long userId = jwtTokenUtil.getUserIdFromToken(token);
            request.setAttribute("currentUserId", userId);

            // 5. 检查用户状态（可选）
            if (!jwtTokenUtil.isUserActive(userId)) {
                log.warn("用户账户已被禁用: userId={}, uri={}", userId, requestUri);
                // 抛出异常，交由 GlobalExceptionHandler 处理
                throw new UnauthorizedException("用户账户已被禁用");
            }

            // 记录认证成功日志
            log.debug("用户认证成功: userId={}, uri={}", userId, requestUri);
            return true;

        } catch (Exception e) {
            log.error("认证令牌解析失败: {} {} - {}", method, requestUri, e.getMessage());
            // 抛出异常，交由 GlobalExceptionHandler 处理
            throw new UnauthorizedException("认证令牌解析失败");
        }
    }


    /**
     * 判断是否为公开路径
     */
    private boolean isPublicPath(String requestUri) {
        return PUBLIC_PATHS.stream().anyMatch(requestUri::startsWith) ||
                requestUri.equals("/") ||
                requestUri.equals("/api") ||
                requestUri.startsWith("/api/health");
    }

    /**
     * 从请求头获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 1. 从Authorization头获取
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. 从X-Auth-Token头获取（备用方案）
        String xAuthToken = request.getHeader("X-Auth-Token");
        if (xAuthToken != null && !xAuthToken.trim().isEmpty()) {
            return xAuthToken;
        }

        // 3. 从查询参数获取（用于WebSocket等场景）
        String tokenParam = request.getParameter("token");
        if (tokenParam != null && !tokenParam.trim().isEmpty()) {
            return tokenParam;
        }

        return null;
    }

    /**
     * 发送未授权响应
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message, String path) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 使用Result类创建未授权响应
        Result<?> result = Result.unauthorized(message).path(path);

        // 将Result对象转换为JSON字符串
        String jsonResponse = objectMapper.writeValueAsString(result);

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}