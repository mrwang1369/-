package com.pethealth.service.impl;

import com.pethealth.entity.User;
import com.pethealth.mapper.UserMapper;
import com.pethealth.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储用户基本信息，支持微信和手机号登录 服务实现类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
