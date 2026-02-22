package com.pethealth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethealth.dto.LoginRequestDTO;
import com.pethealth.dto.RegisterRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUserRegistration() throws Exception {
        // 准备注册数据
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setPhone("13800138001");
        registerRequest.setPassword("123456");
        registerRequest.setConfirmPassword("123456");
        registerRequest.setNickname("测试用户");

        // 发送注册请求
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.userInfo.phone").value("13800138001"));
    }

    @Test
    public void testUserLogin() throws Exception {
        // 准备登录数据
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setLoginType("phone");
        loginRequest.setPhone("13800138001");
        loginRequest.setPassword("123456");

        // 发送登录请求
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.userInfo.phone").value("13800138001"));
    }

    @Test
    public void testInvalidLogin() throws Exception {
        // 准备错误的登录数据
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setLoginType("phone");
        loginRequest.setPhone("13800138001");
        loginRequest.setPassword("wrongpassword");

        // 发送登录请求，应该返回错误
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("密码错误"));
    }
}