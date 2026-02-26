package com.pethealth.test;

import com.pethealth.BaseTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 基础测试环境验证
 * 验证测试环境是否正确配置
 */
@SpringBootTest
public class BasicTestEnvironmentTest extends BaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(BasicTestEnvironmentTest.class);
    
    @Test
    void testApplicationContextLoads() {
        logger.info("=== 基础测试环境验证 ===");
        logger.info("✅ 应用上下文加载成功");
        assertTrue(true, "应用上下文应该能够正常加载");
    }
    
    @Test
    void testMockMvcAvailable() {
        logger.info("=== MockMvc可用性测试 ===");
        assertNotNull(mockMvc, "MockMvc应该被正确注入");
        logger.info("✅ MockMvc注入成功");
    }
    
    @Test
    void testDataSourceAvailable() {
        logger.info("=== 数据源可用性测试 ===");
        assertNotNull(objectMapper, "ObjectMapper应该被正确注入");
        logger.info("✅ ObjectMapper注入成功");
    }
}