package com.pethealth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 测试基类
 * 提供统一的测试配置和工具方法
 * 
 * @author Mr wang
 * @since 2026-02-24
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {BackendApplication.class}
)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseTest {
    
    @Autowired
    protected MockMvc mockMvc;
    
    @Autowired
    protected ObjectMapper objectMapper;
    
    @BeforeEach
    void setUpBase() {
        // 基础测试设置
        prepareTestData();
    }
    
    /**
     * 测试数据准备方法
     * 子类可以重写此方法来准备特定的测试数据
     */
    protected void prepareTestData() {
        // 默认实现为空，子类可根据需要重写
    }
    
    /**
     * 测试后清理方法
     * 子类可以重写此方法来进行测试后的清理工作
     */
    protected void cleanupTestData() {
        // 默认实现为空，子类可根据需要重写
    }
}