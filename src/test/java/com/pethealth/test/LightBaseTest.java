package com.pethealth.test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 轻量级测试基类
 * 专门用于单元测试，避免加载Web相关组件
 * 
 * @author Mr wang
 * @since 2026-02-24
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest(properties = {
    "spring.main.web-application-type=none",  // 禁用Web环境
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
@ActiveProfiles("test")
public abstract class LightBaseTest {
    
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