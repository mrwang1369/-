package com.pethealth.service;

import com.pethealth.entity.PetBreed;
import com.pethealth.mapper.PetBreedMapper;
import com.pethealth.service.impl.PetBreedServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 宠物品种服务测试
 * 验证字典管理类Service的测试模式
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PetBreedServiceTest {

    @MockBean
    private PetBreedMapper petBreedMapper;

    @Autowired
    private PetBreedServiceImpl petBreedService;

    private PetBreed sampleBreed;

    @BeforeEach
    void setUp() {
        reset(petBreedMapper);
        
        sampleBreed = new PetBreed();
        sampleBreed.setBreedId(1);
        sampleBreed.setSpecies("狗");
        sampleBreed.setBreedName("金毛寻回犬");
        sampleBreed.setCreateTime(LocalDateTime.now());
    }

    @Test
    void testGetById_Success() {
        // Arrange
        doReturn(sampleBreed).when(petBreedMapper).selectById(1);
        
        // Act
        PetBreed result = petBreedService.getById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals("狗", result.getSpecies());
        assertEquals("金毛寻回犬", result.getBreedName());
        verify(petBreedMapper).selectById(1);
    }

    @Test
    void testSave_Success() {
        // Arrange
        doReturn(1).when(petBreedMapper).insert(any(PetBreed.class));
        
        // Act
        boolean result = petBreedService.save(sampleBreed);
        
        // Assert
        assertTrue(result);
        verify(petBreedMapper).insert(any(PetBreed.class));
    }
}