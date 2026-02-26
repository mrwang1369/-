package com.pethealth.service;

import com.pethealth.entity.Pet;
import com.pethealth.mapper.PetMapper;
import com.pethealth.service.impl.PetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 宠物服务简化测试示例
 * 验证新的测试模式是否有效
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PetServiceSimpleTest {

    @MockBean
    private PetMapper petMapper;

    @Autowired
    private PetServiceImpl petService;

    private Pet samplePet;

    @BeforeEach
    void setUp() {
        reset(petMapper);
        
        samplePet = new Pet();
        samplePet.setPetId(1);
        samplePet.setUserId(1);
        samplePet.setName("测试宠物");
        samplePet.setSpecies("狗");
        samplePet.setDeleted((byte) 0);
    }

    @Test
    void testBasicFunctionality() {
        // Arrange
        doReturn(samplePet).when(petMapper).selectById(1);
        
        // Act
        Pet result = petService.getById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals("测试宠物", result.getName());
        verify(petMapper).selectById(1);
    }
}