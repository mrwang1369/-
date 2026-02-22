package com.pethealth.service;

import com.pethealth.entity.User;
import com.pethealth.dto.LoginRequestDTO;
import com.pethealth.dto.RegisterRequestDTO;
import com.pethealth.dto.AuthResponseDTO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 存储用户基本信息，支持微信和手机号登�?服务�? * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求参数
     * @return 认证响应信息
     */
    AuthResponseDTO login(LoginRequestDTO loginRequest);

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求参数
     * @return 认证响应信息
     */
    AuthResponseDTO register(RegisterRequestDTO registerRequest);

    /**
     * 根据手机号查询用�?     *
     * @param phone 手机�?     * @return 用户信息
     */
    User findByPhone(String phone);

    /**
     * 根据微信OpenID查询用户
     *
     * @param openid 微信OpenID
     * @return 用户信息
     */
    User findByOpenid(String openid);

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserInfo(Long userId);

    /**
     * 更新用户最后登录时�?     *
     * @param userId 用户ID
     */
    void updateLastLoginTime(Long userId);
}
