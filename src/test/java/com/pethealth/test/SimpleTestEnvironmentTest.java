package com.pethealth.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 最简化的测试环境验证
 * 用来确认Spring Boot测试环境是否能正常启动
 */
@SpringBootTest
@ActiveProfiles("test")
public class SimpleTestEnvironmentTest {

    @Test
    void testSpringContextLoads() {
        System.out.println("=== Spring上下文加载测试 ===");
        System.out.println("✅ Spring上下文成功加载！");
        assertTrue(true, "这是一个总是通过的测试");
    }

    @Test
    void testBasicFunctionality() {
        System.out.println("=== 基础功能测试 ===");
        String message = "Hello PetHealth!";
        System.out.println("测试消息: " + message);
        assertTrue(message.contains("PetHealth"), "消息应该包含PetHealth");
        System.out.println("✅ 基础功能测试通过！");
    }
}