package com.pethealth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.dto.VaccinationRecordCreateDTO;
import com.pethealth.dto.VaccinationRecordResponseDTO;
import com.pethealth.dto.VaccinationRecordUpdateDTO;
import com.pethealth.entity.Pet;
import com.pethealth.entity.VaccinationRecord;
import com.pethealth.mapper.PetMapper;
import com.pethealth.mapper.VaccinationRecordMapper;
import com.pethealth.service.impl.VaccinationRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 疫苗记录服务测试
 * 压轴大戏 - 最复杂的Service测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class VaccinationRecordServiceTest {

    @MockBean
    private VaccinationRecordMapper vaccinationRecordMapper;

    @MockBean
    private PetMapper petMapper;

    @Autowired
    private VaccinationRecordServiceImpl vaccinationRecordService;

    private VaccinationRecord sampleRecord;
    private Pet samplePet;

    @BeforeEach
    void setUp() {
        reset(vaccinationRecordMapper, petMapper);

        // 创建测试用的宠物
        samplePet = new Pet();
        samplePet.setPetId(1);
        samplePet.setUserId(1);
        samplePet.setName("测试宠物");
        samplePet.setSpecies("狗");
        samplePet.setDeleted((byte) 0);

        // 创建测试用的疫苗记录
        sampleRecord = new VaccinationRecord();
        sampleRecord.setVaccinationId(1);
        sampleRecord.setPetId(1);
        sampleRecord.setVaccineName("狂犬疫苗");
        sampleRecord.setVaccinationDate(LocalDate.now().minusMonths(6));
        sampleRecord.setNextDueDate(LocalDate.now().plusMonths(6));
        sampleRecord.setVetInfo("张医生");
        sampleRecord.setProofImageUrl("http://example.com/certificate.jpg");
        sampleRecord.setNotes("按时接种");
        sampleRecord.setDeleted((byte) 0);
        sampleRecord.setCreateTime(LocalDateTime.now());
    }

    @Test
    void testGetById_Success() {
        // Arrange
        doReturn(sampleRecord).when(vaccinationRecordMapper).selectById(1);
        
        // Act
        VaccinationRecord result = vaccinationRecordService.getById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals("狂犬疫苗", result.getVaccineName());
        assertEquals("张医生", result.getVetInfo());
        verify(vaccinationRecordMapper).selectById(1);
    }

    @Test
    void testSave_Success() {
        // Arrange
        doReturn(1).when(vaccinationRecordMapper).insert(any(VaccinationRecord.class));
        
        // Act
        boolean result = vaccinationRecordService.save(sampleRecord);
        
        // Assert
        assertTrue(result);
        verify(vaccinationRecordMapper).insert(any(VaccinationRecord.class));
    }

    @Test
    void testGetRecordsByPetId_Success() {
        // Arrange
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);

        Page<VaccinationRecord> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(sampleRecord));
        page.setTotal(1L);

        doReturn(samplePet).when(petMapper).selectById(1);
        doReturn(page).when(vaccinationRecordMapper).selectPage(any(Page.class), any(QueryWrapper.class));

        // Act
        PageResult<VaccinationRecordResponseDTO> result = vaccinationRecordService.getRecordsByPetId(1, pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getList().size());
        assertEquals("狂犬疫苗", result.getList().get(0).getVaccineName());
        verify(petMapper).selectById(1);
        verify(vaccinationRecordMapper).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    void testGetUpcomingVaccinations_Success() {
        // Arrange
        LocalDate referenceDate = LocalDate.now();
        doReturn(Arrays.asList(sampleRecord)).when(vaccinationRecordMapper).selectList(any(QueryWrapper.class));

        // Act
        List<VaccinationRecordResponseDTO> result = vaccinationRecordService.getUpcomingVaccinations(referenceDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("狂犬疫苗", result.get(0).getVaccineName());
        verify(vaccinationRecordMapper).selectList(any(QueryWrapper.class));
    }

    @Test
    void testGetExpiredVaccinations_Success() {
        // Arrange
        LocalDate referenceDate = LocalDate.now();
        doReturn(Arrays.asList(sampleRecord)).when(vaccinationRecordMapper).selectList(any(QueryWrapper.class));

        // Act
        List<VaccinationRecordResponseDTO> result = vaccinationRecordService.getExpiredVaccinations(referenceDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(vaccinationRecordMapper).selectList(any(QueryWrapper.class));
    }
}