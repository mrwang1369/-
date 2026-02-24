package com.pethealth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.dto.DewormingRecordCreateDTO;
import com.pethealth.dto.DewormingRecordResponseDTO;
import com.pethealth.dto.DewormingRecordUpdateDTO;
import com.pethealth.entity.DewormingRecord;
import com.pethealth.entity.Pet;
import com.pethealth.handler.BusinessException;
import com.pethealth.handler.ResourceNotFoundException;
import com.pethealth.mapper.DewormingRecordMapper;
import com.pethealth.mapper.PetMapper;
import com.pethealth.service.impl.DewormingRecordServiceImpl;
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
class DewormingRecordServiceTest {

    @Mock
    private DewormingRecordMapper dewormingRecordMapper;

    @Mock
    private PetMapper petMapper;

    @InjectMocks
    private DewormingRecordServiceImpl dewormingRecordService;

    private DewormingRecord sampleRecord;
    private Pet samplePet;

    @BeforeEach
    void setUp() {
        // 创建测试用的驱虫记录
        sampleRecord = new DewormingRecord();
        sampleRecord.setDewormingId(1);
        sampleRecord.setPetId(1);
        sampleRecord.setDewormingType("体内驱虫");
        sampleRecord.setDrugName("拜耳内虫逃");
        sampleRecord.setDate(LocalDate.now().minusDays(30));
        sampleRecord.setNextDate(LocalDate.now().plusDays(60));
        sampleRecord.setNotes("按体重给药");
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
        DewormingRecordCreateDTO createDTO = new DewormingRecordCreateDTO();
        createDTO.setPetId(1);
        createDTO.setDewormingType("体内驱虫");
        createDTO.setDrugName("拜耳内虫逃");
        createDTO.setDate(LocalDate.now());
        createDTO.setNextDate(LocalDate.now().plusDays(90));
        createDTO.setNotes("按体重给药");

        when(petMapper.selectById(1)).thenReturn(samplePet);
        when(dewormingRecordMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(dewormingRecordMapper.insert(any(DewormingRecord.class))).thenReturn(1);

        // When
        DewormingRecordResponseDTO result = dewormingRecordService.createRecord(createDTO);

        // Then
        assertNotNull(result);
        assertEquals("体内驱虫", result.getDewormingType());
        assertEquals("拜耳内虫逃", result.getDrugName());
        assertEquals(1, result.getPetId());
        verify(petMapper).selectById(1);
        verify(dewormingRecordMapper).insert(any(DewormingRecord.class));
    }

    @Test
    void createRecord_PetNotFound() {
        // Given
        DewormingRecordCreateDTO createDTO = new DewormingRecordCreateDTO();
        createDTO.setPetId(999);

        when(petMapper.selectById(999)).thenReturn(null);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            dewormingRecordService.createRecord(createDTO);
        });
    }

    @Test
    void createRecord_DuplicateDeworming() {
        // Given
        DewormingRecordCreateDTO createDTO = new DewormingRecordCreateDTO();
        createDTO.setPetId(1);
        createDTO.setDewormingType("体内驱虫");
        createDTO.setDrugName("拜耳内虫逃");
        createDTO.setDate(LocalDate.now());

        when(petMapper.selectById(1)).thenReturn(samplePet);
        when(dewormingRecordMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            dewormingRecordService.createRecord(createDTO);
        });
    }

    @Test
    void updateRecord_Success() {
        // Given
        DewormingRecordUpdateDTO updateDTO = new DewormingRecordUpdateDTO();
        updateDTO.setDewormingType("体外驱虫");
        updateDTO.setDrugName("福来恩");

        when(dewormingRecordMapper.selectById(1)).thenReturn(sampleRecord);
        when(dewormingRecordMapper.updateById(any(DewormingRecord.class))).thenReturn(1);

        // When
        DewormingRecordResponseDTO result = dewormingRecordService.updateRecord(1, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("体外驱虫", result.getDewormingType());
        assertEquals("福来恩", result.getDrugName());
        verify(dewormingRecordMapper).updateById(any(DewormingRecord.class));
    }

    @Test
    void updateRecord_NotFound() {
        // Given
        DewormingRecordUpdateDTO updateDTO = new DewormingRecordUpdateDTO();

        when(dewormingRecordMapper.selectById(999)).thenReturn(null);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            dewormingRecordService.updateRecord(999, updateDTO);
        });
    }

    @Test
    void deleteRecord_Success() {
        // Given
        when(dewormingRecordMapper.selectById(1)).thenReturn(sampleRecord);
        when(dewormingRecordMapper.updateById(any(DewormingRecord.class))).thenReturn(1);

        // When
        dewormingRecordService.deleteRecord(1);

        // Then
        verify(dewormingRecordMapper).updateById(any(DewormingRecord.class));
        assertEquals((byte) 1, sampleRecord.getDeleted());
    }

    @Test
    void getRecordsByPetId_Success() {
        // Given
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);

        Page<DewormingRecord> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(sampleRecord));
        page.setTotal(1L);

        when(petMapper.selectById(1)).thenReturn(samplePet);
        when(dewormingRecordMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

        // When
        PageResult<DewormingRecordResponseDTO> result = dewormingRecordService.getRecordsByPetId(1, pageRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
        assertEquals("体内驱虫", result.getList().get(0).getDewormingType());
    }

    @Test
    void getUpcomingDewormings_Success() {
        // Given
        LocalDate futureDate = LocalDate.now().plusDays(60);
        when(dewormingRecordMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(sampleRecord));

        // When
        List<DewormingRecordResponseDTO> result = dewormingRecordService.getUpcomingDewormings(futureDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("体内驱虫", result.get(0).getDewormingType());
    }

    @Test
    void getExpiredDewormings_Success() {
        // Given
        LocalDate currentDate = LocalDate.now();
        when(dewormingRecordMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(sampleRecord));

        // When
        List<DewormingRecordResponseDTO> result = dewormingRecordService.getExpiredDewormings(currentDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void calculateNextDewormingDate_Success() {
        // Given
        LocalDate lastDate = LocalDate.now();
        int cycleDays = 90;

        // When
        LocalDate result = dewormingRecordService.calculateNextDewormingDate(lastDate, cycleDays);

        // Then
        assertNotNull(result);
        assertEquals(lastDate.plusDays(cycleDays), result);
    }
}