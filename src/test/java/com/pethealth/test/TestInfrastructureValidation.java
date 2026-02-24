package com.pethealth.test;

import com.pethealth.test.base.BaseTest;
import com.pethealth.test.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试基础设施验证测试
 * 验证新创建的测试基类和工具类是否正常工作
 */
public class TestInfrastructureValidation extends BaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(TestInfrastructureValidation.class);
    
    @Test
    public void testBaseTestConfiguration() {
        logger.info("=== 测试基础设施配置验证 ===");
        
        // 验证MockMvc是否正确注入
        assertNotNull(mockMvc, "MockMvc should be injected");
        logger.info("✅ MockMvc 注入成功");
        
        // 验证ObjectMapper是否正确注入
        assertNotNull(objectMapper, "ObjectMapper should be injected");
        logger.info("✅ ObjectMapper 注入成功");
        
        // 验证测试工具方法
        String randomPhone = generateRandomPhone();
        assertNotBlank(randomPhone, "随机手机号");
        assertTrue(randomPhone.startsWith("138"), "手机号应该以138开头");
        logger.info("✅ 测试工具方法工作正常: {}", randomPhone);
        
        String randomNickname = generateRandomNickname();
        assertNotBlank(randomNickname, "随机昵称");
        assertTrue(randomNickname.contains("测试用户_"), "昵称格式正确");
        logger.info("✅ 昵称生成方法工作正常: {}", randomNickname);
        
        logger.info("🎉 测试基础设施配置验证通过！");
    }
    
    @Test
    public void testTestDataFactory() {
        logger.info("=== 测试数据工厂验证 ===");
        
        // 验证用户数据创建
        var user = TestDataFactory.createUser();
        assertNotNull(user, "用户对象不应为空");
        assertNotBlank(user.getPhone(), "手机号");
        assertNotBlank(user.getNickname(), "昵称");
        logger.info("✅ 用户数据创建正常: {} ({})", user.getNickname(), user.getPhone());
        
        // 验证宠物数据创建
        var pet = TestDataFactory.createPet();
        assertNotNull(pet, "宠物对象不应为空");
        assertNotBlank(pet.getName(), "宠物名称");
        assertPositive(pet.getUserId(), "用户ID");
        logger.info("✅ 宠物数据创建正常: {}", pet.getName());
        
        // 验证健康记录数据创建
        var vaccination = TestDataFactory.createVaccinationRecord();
        assertNotNull(vaccination, "疫苗记录不应为空");
        assertPositive(vaccination.getPetId(), "宠物ID");
        assertNotBlank(vaccination.getVaccineName(), "疫苗名称");
        logger.info("✅ 疫苗记录创建正常: {}", vaccination.getVaccineName());
        
        logger.info("🎉 测试数据工厂验证通过！");
    }
    
    @Test
    public void testLombokAnnotations() {
        logger.info("=== Lombok注解验证 ===");
        
        // 验证实体类Lombok注解是否正常工作
        var pet = TestDataFactory.createPet();
        pet.setPetId(999);
        pet.setName("测试宠物");
        pet.setSpecies("测试物种");
        
        assertEquals(Integer.valueOf(999), pet.getPetId(), "PetId getter正常");
        assertEquals("测试宠物", pet.getName(), "Name getter正常");
        assertEquals("测试物种", pet.getSpecies(), "Species getter正常");
        logger.info("✅ Pet实体类Lombok注解正常工作");
        
        // 验证DTO类Lombok注解是否正常工作
        var createUserDTO = TestDataFactory.createRegisterRequest();
        createUserDTO.setPhone("13800138000");
        createUserDTO.setPassword("password123");
        createUserDTO.setNickname("测试用户");
        
        assertEquals("13800138000", createUserDTO.getPhone(), "Phone getter正常");
        assertEquals("password123", createUserDTO.getPassword(), "Password getter正常");
        assertEquals("测试用户", createUserDTO.getNickname(), "Nickname getter正常");
        logger.info("✅ RegisterRequestDTO Lombok注解正常工作");
        
        logger.info("🎉 Lombok注解验证通过！");
    }
}