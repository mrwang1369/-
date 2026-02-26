package com.pethealth.service;

import com.pethealth.entity.GrowthEvent;
import com.pethealth.mapper.GrowthEventMapper;
import com.pethealth.service.impl.GrowthEventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 成长事件服务测试
 * 验证最简Service的测试模式
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GrowthEventServiceTest {

    @MockBean
    private GrowthEventMapper growthEventMapper;

    @Autowired
    private GrowthEventServiceImpl growthEventService;

    private GrowthEvent sampleEvent;

    @BeforeEach
    void setUp() {
        reset(growthEventMapper);
        
        sampleEvent = new GrowthEvent();
        sampleEvent.setEventId(1);
        sampleEvent.setPetId(1);
        sampleEvent.setEventType("疫苗接种");
        sampleEvent.setEventDate(LocalDateTime.now());
        sampleEvent.setDescription("狂犬疫苗第一针");
        sampleEvent.setImageUrl("http://example.com/image.jpg");
        sampleEvent.setWeightValue(new BigDecimal("5.5"));
        sampleEvent.setDeleted((byte) 0);
        sampleEvent.setCreateTime(LocalDateTime.now());
    }

    @Test
    void testGetById_Success() {
        // Arrange
        doReturn(sampleEvent).when(growthEventMapper).selectById(1);
        
        // Act
        GrowthEvent result = growthEventService.getById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals("疫苗接种", result.getEventType());
        assertEquals("狂犬疫苗第一针", result.getDescription());
        verify(growthEventMapper).selectById(1);
    }

    @Test
    void testSave_Success() {
        // Arrange
        doReturn(1).when(growthEventMapper).insert(any(GrowthEvent.class));
        
        // Act
        boolean result = growthEventService.save(sampleEvent);
        
        // Assert
        assertTrue(result);
        verify(growthEventMapper).insert(any(GrowthEvent.class));
    }
}