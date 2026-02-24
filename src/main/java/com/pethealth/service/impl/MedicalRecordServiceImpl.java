package com.pethealth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.dto.MedicalRecordCreateDTO;
import com.pethealth.dto.MedicalRecordResponseDTO;
import com.pethealth.dto.MedicalRecordUpdateDTO;
import com.pethealth.entity.MedicalRecord;
import com.pethealth.entity.Pet;
import com.pethealth.handler.BusinessException;
import com.pethealth.handler.ResourceNotFoundException;
import com.pethealth.mapper.MedicalRecordMapper;
import com.pethealth.mapper.PetMapper;
import com.pethealth.service.MedicalRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 记录病历和用药信息，可设置用药提醒 服务实现类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Service
public class MedicalRecordServiceImpl extends ServiceImpl<MedicalRecordMapper, MedicalRecord> implements MedicalRecordService {

    @Autowired
    private PetMapper petMapper;

    @Override
    @Transactional
    public MedicalRecordResponseDTO createRecord(MedicalRecordCreateDTO createDTO) {
        // 验证宠物存在
        Pet pet = petMapper.selectById(createDTO.getPetId());
        if (pet == null || pet.getDeleted() == 1) {
            throw new ResourceNotFoundException("宠物不存在");
        }

        // 创建病历记录
        MedicalRecord record = new MedicalRecord();
        BeanUtils.copyProperties(createDTO, record);
        record.setCreateTime(LocalDateTime.now());
        record.setDeleted((byte) 0);

        if (!save(record)) {
            throw new BusinessException("创建病历记录失败");
        }

        return convertToResponseDTO(record);
    }

    @Override
    @Transactional
    public MedicalRecordResponseDTO updateRecord(Integer recordId, MedicalRecordUpdateDTO updateDTO) {
        // 检查记录是否存在
        MedicalRecord record = getById(recordId);
        if (record == null || record.getDeleted() == 1) {
            throw new ResourceNotFoundException("病历记录不存在");
        }

        // 更新记录
        BeanUtils.copyProperties(updateDTO, record);
        record.setCreateTime(null); // 避免更新创建时间

        if (!updateById(record)) {
            throw new BusinessException("更新病历记录失败");
        }

        return convertToResponseDTO(record);
    }

    @Override
    @Transactional
    public void deleteRecord(Integer recordId) {
        MedicalRecord record = getById(recordId);
        if (record == null || record.getDeleted() == 1) {
            throw new ResourceNotFoundException("病历记录不存在");
        }

        // 逻辑删除
        record.setDeleted((byte) 1);
        if (!updateById(record)) {
            throw new BusinessException("删除病历记录失败");
        }
    }

    @Override
    public PageResult<MedicalRecordResponseDTO> getRecordsByPetId(Integer petId, PageRequest pageRequest) {
        // 验证宠物存在
        Pet pet = petMapper.selectById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new ResourceNotFoundException("宠物不存在");
        }

        // 分页查询
        QueryWrapper<MedicalRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId)
                   .eq("deleted", 0)
                   .orderByDesc("treatment_date");

        IPage<MedicalRecord> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        IPage<MedicalRecord> resultPage = page(page, queryWrapper);

        // 转换为DTO
        List<MedicalRecordResponseDTO> dtoList = resultPage.getRecords().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new PageResult<>(
                resultPage.getTotal(),
                dtoList,
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize()
        );
    }

    @Override
    public List<MedicalRecordResponseDTO> getRecentMedicalRecords(LocalDate date) {
        QueryWrapper<MedicalRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("treatment_date", date.minusDays(30))
                   .eq("deleted", 0)
                   .orderByDesc("treatment_date");

        return list(queryWrapper).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecordResponseDTO> searchByDiagnosis(String diagnosisKeyword) {
        if (!StringUtils.hasText(diagnosisKeyword)) {
            return List.of();
        }

        QueryWrapper<MedicalRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("diagnosis", diagnosisKeyword)
                   .eq("deleted", 0)
                   .orderByDesc("treatment_date");

        return list(queryWrapper).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecordResponseDTO> getMedicationReminders() {
        QueryWrapper<MedicalRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("medication_list")
                   .ne("medication_list", "")
                   .ge("treatment_date", LocalDate.now().minusDays(7)) // 最近一周的用药记录
                   .eq("deleted", 0)
                   .orderByDesc("treatment_date");

        return list(queryWrapper).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将实体转换为响应DTO
     */
    private MedicalRecordResponseDTO convertToResponseDTO(MedicalRecord record) {
        MedicalRecordResponseDTO dto = new MedicalRecordResponseDTO();
        BeanUtils.copyProperties(record, dto);
        
        // 计算就诊距今天数
        if (record.getTreatmentDate() != null) {
            long daysAgo = ChronoUnit.DAYS.between(record.getTreatmentDate(), LocalDate.now());
            dto.setDaysAgo(Math.toIntExact(daysAgo));
            dto.setIsRecent(daysAgo <= 30);
        }
        
        return dto;
    }
}