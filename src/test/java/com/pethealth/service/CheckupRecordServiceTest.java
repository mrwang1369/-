package com.pethealth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.dto.CheckupRecordCreateDTO;
import com.pethealth.dto.CheckupRecordResponseDTO;
import com.pethealth.dto.CheckupRecordUpdateDTO;
import com.pethealth.entity.CheckupRecord;
import com.pethealth.entity.Pet;
import com.pethealth.handler.BusinessException;
import com.pethealth.handler.ResourceNotFoundException;
import com.pethealth.mapper.CheckupRecordMapper;
import com.pethealth.mapper.PetMapper;
import com.pethealth.service.impl.CheckupRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckupRecordServiceTest {

    @Mock
    private CheckupRecordMapper checkupRecordMapper;

    @Mock
    private PetMapper petMapper;

    @InjectMocks
    private CheckupRecordServiceImpl checkupRecordService;

    private CheckupRecord sampleRecord;
    private Pet samplePet;

    @BeforeEach
    void setUp() {
        // 创建测试用的体检记录
        sampleRecord = new CheckupRecord();
        sampleRecord.setCheckupId(1);
        sampleRecord.setPetId(1);
        sampleRecord.setCheckupDate(LocalDate.now().minusDays(60));
        sampleRecord.setInstitution("宠物医院");
        sampleRecord.setResultSummary("体检结果正常");
        sampleRecord.setReportImageUrl("http://example.com/report.jpg");
        sampleRecord.setDeleted((byte) 0);

        // 创建测试用的宠物
        samplePet = new Pet();
        samplePet.setPetId(1);
        samplePet.setUserId(1);
        samplePet.setName("小白");
        samplePet.setSpecies("狗");
        samplePet.setDeleted((byte) 0);
    }

    @Test
    void createRecord_Success() {
        // Given
        CheckupRecordCreateDTO createDTO = new CheckupRecordCreateDTO();
        createDTO.setPetId(1);
        createDTO.setCheckupDate(LocalDate.now());
        createDTO.setInstitution("宠物医院");
        createDTO.setResultSummary("体检结果正常");
        createDTO.setReportImageUrl("http://example.com/report.jpg");

        when(petMapper.selectById(1)).thenReturn(samplePet);
        when(checkupRecordMapper.insert(any(CheckupRecord.class))).thenReturn(1);

        // When
        CheckupRecordResponseDTO result = checkupRecordService.createRecord(createDTO);

        // Then
        assertNotNull(result);
        assertEquals("宠物医院", result.getInstitution());
        assertEquals("体检结果正常", result.getResultSummary());
        assertEquals(1, result.getPetId());
        verify(petMapper).selectById(1);
        verify(checkupRecordMapper).insert(any(CheckupRecord.class));
    }

    @Test
    void createRecord_PetNotFound() {
        // Given
        CheckupRecordCreateDTO createDTO = new CheckupRecordCreateDTO();
        createDTO.setPetId(999);

        when(petMapper.selectById(999)).thenReturn(null);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            checkupRecordService.createRecord(createDTO);
        });
    }

    @Test
    void updateRecord_Success() {
        // Given
        CheckupRecordUpdateDTO updateDTO = new CheckupRecordUpdateDTO();
        updateDTO.setInstitution("新宠物医院");
        updateDTO.setResultSummary("更新后的体检结果");

        when(checkupRecordMapper.selectById(1)).thenReturn(sampleRecord);
        when(checkupRecordMapper.updateById(any(CheckupRecord.class))).thenReturn(1);

        // When
        CheckupRecordResponseDTO result = checkupRecordService.updateRecord(1, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("新宠物医院", result.getInstitution());
        assertEquals("更新后的体检结果", result.getResultSummary());
        verify(checkupRecordMapper).updateById(any(CheckupRecord.class));
    }

    @Test
    void updateRecord_NotFound() {
        // Given
        CheckupRecordUpdateDTO updateDTO = new CheckupRecordUpdateDTO();

        when(checkupRecordMapper.selectById(999)).thenReturn(null);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            checkupRecordService.updateRecord(999, updateDTO);
        });
    }

    @Test
    void deleteRecord_Success() {
        // Given
        when(checkupRecordMapper.selectById(1)).thenReturn(sampleRecord);
        when(checkupRecordMapper.updateById(any(CheckupRecord.class))).thenReturn(1);

        // When
        checkupRecordService.deleteRecord(1);

        // Then
        verify(checkupRecordMapper).updateById(any(CheckupRecord.class));
        assertEquals((byte) 1, sampleRecord.getDeleted());
    }

    @Test
    void getRecordsByPetId_Success() {
        // Given
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);

        Page<CheckupRecord> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(sampleRecord));
        page.setTotal(1L);

        when(petMapper.selectById(1)).thenReturn(samplePet);
        when(checkupRecordMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

        // When
        PageResult<CheckupRecordResponseDTO> result = checkupRecordService.getRecordsByPetId(1, pageRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
        assertEquals("宠物医院", result.getList().get(0).getInstitution());
    }

    @Test
    void getRecentCheckups_Success() {
        // Given
        LocalDate referenceDate = LocalDate.now();
        when(checkupRecordMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(sampleRecord));

        // When
        List<CheckupRecordResponseDTO> result = checkupRecordService.getRecentCheckups(referenceDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("宠物医院", result.get(0).getInstitution());
    }

    @Test
    void getOverdueCheckups_Success() {
        // Given
        int months = 6;
        when(checkupRecordMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(sampleRecord));

        // When
        List<CheckupRecordResponseDTO> result = checkupRecordService.getOverdueCheckups(months);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}