package com.pethealth.service;

import com.pethealth.entity.SymptomRecord;
import com.pethealth.mapper.SymptomRecordMapper;
import com.pethealth.service.impl.SymptomRecordServiceImpl;
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
 * 症状记录服务测试
 * 完成最后一轮Service测试推广
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SymptomRecordServiceTest {

    @MockBean
    private SymptomRecordMapper symptomRecordMapper;

    @Autowired
    private SymptomRecordServiceImpl symptomRecordService;

    private SymptomRecord sampleRecord;

    @BeforeEach
    void setUp() {
        reset(symptomRecordMapper);
        
        sampleRecord = new SymptomRecord();
        sampleRecord.setSymptomId(1);
        sampleRecord.setPetId(1);
        sampleRecord.setUserId(1);
        sampleRecord.setSymptomsText("呕吐、腹泻");
        sampleRecord.setAnalysisResult("可能是肠胃炎");
        sampleRecord.setEmergencyLevel("中等");
        sampleRecord.setSuggestions("建议尽快就医检查");
        sampleRecord.setDeleted((byte) 0);
        sampleRecord.setCreateTime(LocalDateTime.now());
    }

    @Test
    void testGetById_Success() {
        // Arrange
        doReturn(sampleRecord).when(symptomRecordMapper).selectById(1);
        
        // Act
        SymptomRecord result = symptomRecordService.getById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals("呕吐、腹泻", result.getSymptomsText());
        assertEquals("可能是肠胃炎", result.getAnalysisResult());
        assertEquals("中等", result.getEmergencyLevel());
        verify(symptomRecordMapper).selectById(1);
    }

    @Test
    void testSave_Success() {
        // Arrange
        doReturn(1).when(symptomRecordMapper).insert(any(SymptomRecord.class));
        
        // Act
        boolean result = symptomRecordService.save(sampleRecord);
        
        // Assert
        assertTrue(result);
        verify(symptomRecordMapper).insert(any(SymptomRecord.class));
    }
}