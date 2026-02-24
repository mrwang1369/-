package com.pethealth;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 测试基类
 * 提供统一的测试配置和工具方法
 * 
 * @author Mr wang
 * @since 2026-02-24
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseTest {
    
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