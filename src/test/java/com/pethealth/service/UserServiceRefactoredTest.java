package com.pethealth.service;

import com.pethealth.dto.LoginRequestDTO;
import com.pethealth.dto.RegisterRequestDTO;
import com.pethealth.dto.AuthResponseDTO;
import com.pethealth.entity.User;
import com.pethealth.mapper.UserMapper;
import com.pethealth.service.impl.UserServiceImpl;
import com.pethealth.utils.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试 - 展示推广模式的应用
 * 遵循AAA测试模式和命名规范
 *
 * @author Mr wang
 * @since 2026-02-26
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceRefactoredTest {

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserServiceImpl userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        reset(userMapper, jwtTokenUtil);

        sampleUser = new User();
        sampleUser.setUserId(1);
        sampleUser.setPhone("13800138001");
        sampleUser.setPassword("$2a$10$encrypted_password_hash"); // 加密后的密码
        sampleUser.setNickname("测试用户");
        sampleUser.setDeleted((byte) 0);
        sampleUser.setCreateTime(LocalDateTime.now());
        sampleUser.setUpdateTime(LocalDateTime.now());
    }

    @Test
    void testFindByPhone_Success() {
        // Arrange - 准备阶段
        String phone = "13800138001";
        doReturn(sampleUser).when(userMapper).selectOne(any());

        // Act - 执行阶段
        User result = userService.findByPhone(phone);

        // Assert - 断言阶段
        assertNotNull(result, "根据手机号查询的用户不应为null");
        assertEquals("13800138001", result.getPhone(), "手机号应正确");
        assertEquals("测试用户", result.getNickname(), "用户昵称应正确");
        verify(userMapper).selectOne(any());
    }

    @Test
    void testGetUserInfo_Success() {
        // Arrange - 准备阶段
        Long userId = 1L;
        doReturn(sampleUser).when(userMapper).selectById(1);

        // Act - 执行阶段
        User result = userService.getUserInfo(userId);

        // Assert - 断言阶段
        assertNotNull(result, "获取的用户信息不应为null");
        assertEquals("13800138001", result.getPhone(), "手机号应正确");
        verify(userMapper).selectById(1);
    }

    @Test
    void testRegister_Success() {
        // Arrange - 准备阶段
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setPhone("13800138002");
        registerRequest.setPassword("123456");
        registerRequest.setConfirmPassword("123456");
        registerRequest.setNickname("新用户");

        doReturn(null).when(userMapper).selectOne(any()); // 手机号未被注册
        doReturn(true).when(userMapper).insert(any(User.class));
        doReturn("fake-jwt-token").when(jwtTokenUtil).generateToken(anyLong());

        // Act - 执行阶段
        AuthResponseDTO result = userService.register(registerRequest);

        // Assert - 断言阶段
        assertNotNull(result, "注册响应不应为null");
        assertEquals("13800138002", result.getUserInfo().getPhone(), "注册手机号应正确");
        assertEquals("新用户", result.getUserInfo().getNickname(), "用户昵称应正确");
        assertNotNull(result.getAccessToken(), "应生成访问令牌");
        verify(userMapper).selectOne(any());
        verify(userMapper).insert(any(User.class));
        verify(jwtTokenUtil).generateToken(anyLong());
    }

    @Test
    void testRegister_PhoneAlreadyExists() {
        // Arrange - 准备阶段
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setPhone("13800138001");
        registerRequest.setPassword("123456");
        registerRequest.setConfirmPassword("123456");

        doReturn(sampleUser).when(userMapper).selectOne(any()); // 手机号已存在

        // Act & Assert - 执行和断言
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.register(registerRequest);
        });

        assertTrue(exception.getMessage().contains("手机号已被注册"), "应提示手机号已被注册");
        verify(userMapper).selectOne(any());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void testUpdateLastLoginTime_Success() {
        // Arrange - 准备阶段
        Long userId = 1L;
        doReturn(true).when(userMapper).updateById(any(User.class));

        // Act - 执行阶段
        userService.updateLastLoginTime(userId);

        // Assert - 断言阶段
        verify(userMapper).updateById(any(User.class));
    }
}