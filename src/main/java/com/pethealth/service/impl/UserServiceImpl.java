package com.pethealth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethealth.entity.User;
import com.pethealth.mapper.UserMapper;
import com.pethealth.dto.LoginRequestDTO;
import com.pethealth.dto.RegisterRequestDTO;
import com.pethealth.dto.AuthResponseDTO;
import com.pethealth.utils.JwtTokenUtil;
import com.pethealth.utils.PasswordUtil;
import com.pethealth.handler.BusinessException;
import com.pethealth.handler.ResourceNotFoundException;
import com.pethealth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 存储用户基本信息，支持微信和手机号登录 服务实现类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Override
    @Transactional
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        User user = null;

        // 根据登录方式处理不同逻辑
        if ("phone".equals(loginRequest.getLoginType())) {
            // 手机号登录
            user = handlePhoneLogin(loginRequest);
        } else if ("wx".equals(loginRequest.getLoginType())) {
            // 微信登录
            user = handleWechatLogin(loginRequest);
        } else {
            throw new BusinessException("不支持的登录方式");
        }

        // 生成JWT Token
        String token = jwtTokenUtil.generateToken(user.getUserId().longValue());

        // 更新最后登录时间
        updateLastLoginTime(user.getUserId().longValue());

        // 构造响应数据
        return buildAuthResponse(user, token);
    }

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO registerRequest) {
        // 验证密码一致性
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 检查手机号是否已注册
        if (findByPhone(registerRequest.getPhone()) != null) {
            throw new BusinessException("手机号已被注册");
        }

        // 创建用户
        User user = new User();
        user.setPhone(registerRequest.getPhone());
        user.setPassword(PasswordUtil.encode(registerRequest.getPassword()));
        user.setNickname(registerRequest.getNickname() != null ? 
                         registerRequest.getNickname() : "用户" + registerRequest.getPhone().substring(7));
        user.setAvatarUrl(registerRequest.getAvatarUrl());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setDeleted((byte) 0);

        // 保存用户
        if (!save(user)) {
            throw new BusinessException("用户注册失败");
        }

        log.info("用户注册成功: userId=" + user.getUserId() + ", phone=" + user.getPhone());

        // 生成JWT Token
        String token = jwtTokenUtil.generateToken(user.getUserId().longValue());

        // 构造响应数据
        return buildAuthResponse(user, token);
    }

    @Override
    public User findByPhone(String phone) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone)
                   .eq("deleted", 0);
        return getOne(queryWrapper);
    }

    @Override
    public User findByOpenid(String openid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid)
                   .eq("deleted", 0);
        return getOne(queryWrapper);
    }

    @Override
    public User getUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new ResourceNotFoundException("用户不存在");
        }
        return user;
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        User user = new User();
        user.setUserId(userId.intValue());
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
    }

    /**
     * 处理手机号登录
     */
    private User handlePhoneLogin(LoginRequestDTO loginRequest) {
        if (loginRequest.getPhone() == null || loginRequest.getPassword() == null) {
            throw new BusinessException("手机号和密码不能为空");
        }

        User user = findByPhone(loginRequest.getPhone());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getPassword() == null) {
            throw new BusinessException("用户未设置密码，请使用微信登录");
        }

        if (!PasswordUtil.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        return user;
    }

    /**
     * 处理微信登录（简化版本，实际项目需要调用微信接口验证code）
     */
    private User handleWechatLogin(LoginRequestDTO loginRequest) {
        if (loginRequest.getWxCode() == null) {
            throw new BusinessException("微信登录code不能为空");
        }

        // TODO: 实际项目中需要调用微信接口验证code并获取用户信息
        // 这里简化处理，假设code就是openid
        String openid = loginRequest.getWxCode();
        
        User user = findByOpenid(openid);
        
        if (user == null) {
            // 新用户，自动注册
            user = new User();
            user.setOpenid(openid);
            user.setNickname(loginRequest.getWxUserInfo() != null ? 
                           loginRequest.getWxUserInfo().getNickName() : "微信用户");
            user.setAvatarUrl(loginRequest.getWxUserInfo() != null ? 
                            loginRequest.getWxUserInfo().getAvatarUrl() : "");
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setDeleted((byte) 0);
            
            if (!save(user)) {
                throw new BusinessException("微信用户注册失败");
            }
            
            log.info("微信用户注册成功: userId=" + user.getUserId() + ", openid=" + user.getOpenid());
        }

        return user;
    }

    /**
     * 构造认证响应数据
     */
    private AuthResponseDTO buildAuthResponse(User user, String token) {
        AuthResponseDTO.UserInfo userInfo = AuthResponseDTO.UserInfo.builder()
                .userId(user.getUserId().longValue())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .openid(user.getOpenid())
                .build();

        return AuthResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration)
                .userInfo(userInfo)
                .build();
    }
}