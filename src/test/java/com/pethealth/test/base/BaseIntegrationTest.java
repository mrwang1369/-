package com.pethealth.test.base;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * 集成测试基类 - 用于需要数据库交互的集成测试
 * 
 * 集成测试规则：
 * 1. 继承此类进行需要真实数据库操作的测试
 * 2. 使用@Transactional确保测试数据不会污染数据库
 * 3. 每个测试方法在一个事务中运行，结束后自动回滚
 * 4. 支持完整的Spring Boot上下文加载
 * 5. 使用H2内存数据库进行测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@Transactional
public abstract class BaseIntegrationTest extends BaseTest {
    
    /**
     * 集成测试专用的数据准备方法
     * 子类可以重写此方法来准备集成测试所需的数据
     */
    @Override
    protected void setupTestData() {
        super.setupTestData();
        // 集成测试特有的数据准备逻辑
        prepareIntegrationTestData();
    }
    
    /**
     * 子类重写此方法来准备集成测试数据
     */
    protected void prepareIntegrationTestData() {
        // 默认实现为空，子类根据需要重写
    }
    
    /**
     * 清理测试数据的方法
     * 在事务回滚前可以执行一些清理操作
     */
    protected void cleanupTestData() {
        // 默认实现为空，子类根据需要重写
    }
}