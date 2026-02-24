package com.pethealth.test.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 测试基类 - 提供通用的测试配置和工具方法
 * 
 * 测试规则：
 * 1. 所有测试类必须继承此类
 * 2. 使用@SpringBootTest启动完整Spring上下文
 * 3. 使用@AutoConfigureMockMvc进行Web层测试
 * 4. 测试数据使用内存数据库(H2)
 * 5. 每个测试方法独立运行，互不影响
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@ExtendWith(MockitoExtension.class)
public abstract class BaseTest {
    
    @Autowired
    protected MockMvc mockMvc;
    
    @Autowired
    protected ObjectMapper objectMapper;
    
    @BeforeEach
    void setUpBase() {
        // 通用的测试初始化逻辑
        setupTestData();
        // 重置Mock对象状态
        Mockito.reset();
    }
    
    /**
     * 子类可以重写此方法来设置特定的测试数据
     */
    protected void setupTestData() {
        // 默认实现为空，子类根据需要重写
    }
    
    /**
     * 生成随机测试数据
     */
    protected String generateRandomPhone() {
        return "138" + System.currentTimeMillis() % 10000000;
    }
    
    protected String generateRandomNickname() {
        return "测试用户_" + System.currentTimeMillis() % 10000;
    }
    
    protected String generateRandomEmail() {
        return "test" + System.currentTimeMillis() % 10000 + "@example.com";
    }
    
    /**
     * 断言工具方法
     */
    protected void assertNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new AssertionError(fieldName + " 不能为空");
        }
    }
    
    protected void assertPositive(Integer value, String fieldName) {
        if (value == null || value <= 0) {
            throw new AssertionError(fieldName + " 必须为正数");
        }
    }
}