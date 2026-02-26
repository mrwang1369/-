package com.pethealth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pethealth.entity.ServicePoint;
import com.pethealth.mapper.ServicePointMapper;
import com.pethealth.service.MapService;
import com.pethealth.service.impl.ServicePointServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 服务点服务测试
 * 验证带外部依赖的复杂Service测试模式
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ServicePointServiceTest {

    @MockBean
    private ServicePointMapper servicePointMapper;

    @MockBean
    private MapService mapService;

    @Autowired
    private ServicePointServiceImpl servicePointService;

    private ServicePoint samplePoint;

    @BeforeEach
    void setUp() {
        reset(servicePointMapper, mapService);
        
        samplePoint = new ServicePoint();
        samplePoint.setPointId(1);
        samplePoint.setName("宠物医院");
        samplePoint.setType("医院");
        samplePoint.setAddress("北京市朝阳区某某路123号");
        samplePoint.setPhone("010-12345678");
        samplePoint.setLatitude(new BigDecimal("39.9042"));
        samplePoint.setLongitude(new BigDecimal("116.4074"));
        samplePoint.setRating(new BigDecimal("4.5"));
        samplePoint.setBusinessHours("09:00-18:00");
        samplePoint.setDeleted((byte) 0);
        samplePoint.setCreateTime(LocalDateTime.now());
    }

    @Test
    void testGetById_Success() {
        // Arrange
        doReturn(samplePoint).when(servicePointMapper).selectById(1);
        
        // Act
        ServicePoint result = servicePointService.getById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals("宠物医院", result.getName());
        assertEquals("医院", result.getType());
        assertEquals("北京市朝阳区某某路123号", result.getAddress());
        verify(servicePointMapper).selectById(1);
    }

    @Test
    void testSave_Success() {
        // Arrange
        doReturn(1).when(servicePointMapper).insert(any(ServicePoint.class));
        
        // Act
        boolean result = servicePointService.save(samplePoint);
        
        // Assert
        assertTrue(result);
        verify(servicePointMapper).insert(any(ServicePoint.class));
    }

    @Test
    void testGetNearbyServicePoints_Success() {
        // Arrange
        BigDecimal longitude = new BigDecimal("116.4074");
        BigDecimal latitude = new BigDecimal("39.9042");
        Integer radius = 1000;
        String type = "医院";
        
        ServicePoint farPoint = new ServicePoint();
        farPoint.setPointId(2);
        farPoint.setName("远方宠物店");
        farPoint.setType("宠物店");
        farPoint.setLatitude(new BigDecimal("39.9100"));
        farPoint.setLongitude(new BigDecimal("116.4100"));
        farPoint.setRating(new BigDecimal("4.0"));
        farPoint.setDeleted((byte) 0);
        
        doReturn(Arrays.asList(samplePoint, farPoint)).when(servicePointMapper).selectList(any(QueryWrapper.class));
        doReturn(500.0).when(mapService).calculateDistance(longitude, latitude, 
            samplePoint.getLongitude(), samplePoint.getLatitude());
        doReturn(1500.0).when(mapService).calculateDistance(longitude, latitude, 
            farPoint.getLongitude(), farPoint.getLatitude());
        
        // Act
        List<ServicePoint> result = servicePointService.getNearbyServicePoints(longitude, latitude, radius, type);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("宠物医院", result.get(0).getName());
        verify(servicePointMapper).selectList(any(QueryWrapper.class));
        verify(mapService).calculateDistance(longitude, latitude, 
            samplePoint.getLongitude(), samplePoint.getLatitude());
    }
}