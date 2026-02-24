package com.pethealth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.dto.VaccinationRecordCreateDTO;
import com.pethealth.dto.VaccinationRecordResponseDTO;
import com.pethealth.dto.VaccinationRecordUpdateDTO;
import com.pethealth.entity.Pet;
import com.pethealth.entity.VaccinationRecord;
import com.pethealth.handler.BusinessException;
import com.pethealth.handler.ResourceNotFoundException;
import com.pethealth.mapper.PetMapper;
import com.pethealth.mapper.VaccinationRecordMapper;
import com.pethealth.service.VaccinationRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 记录疫苗接种信息，用于健康计划和提醒 服务实现类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Service
public class VaccinationRecordServiceImpl extends ServiceImpl<VaccinationRecordMapper, VaccinationRecord> implements VaccinationRecordService {

    @Autowired
    private PetMapper petMapper;

    @Override
    @Transactional
    public VaccinationRecordResponseDTO createRecord(VaccinationRecordCreateDTO createDTO) {
        // 验证宠物存在且属于当前用户
        Pet pet = petMapper.selectById(createDTO.getPetId());
        if (pet == null || pet.getDeleted() == 1) {
            throw new ResourceNotFoundException("宠物不存在");
        }

        // 检查是否重复接种同一种疫苗
        QueryWrapper<VaccinationRecord> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("pet_id", createDTO.getPetId())
                   .eq("vaccine_name", createDTO.getVaccineName())
                   .eq("vaccination_date", createDTO.getVaccinationDate())
                   .eq("deleted", 0);
        
        if (count(checkWrapper) > 0) {
            throw new BusinessException("该宠物当天已接种相同疫苗");
        }

        // 创建疫苗记录
        VaccinationRecord record = new VaccinationRecord();
        BeanUtils.copyProperties(createDTO, record);
        record.setCreateTime(LocalDateTime.now());
        record.setDeleted((byte) 0);

        if (!save(record)) {
            throw new BusinessException("创建疫苗记录失败");
        }

        return convertToResponseDTO(record);
    }

    @Override
    @Transactional
    public VaccinationRecordResponseDTO updateRecord(Integer recordId, VaccinationRecordUpdateDTO updateDTO) {
        // 检查记录是否存在
        VaccinationRecord record = getById(recordId);
        if (record == null || record.getDeleted() == 1) {
            throw new ResourceNotFoundException("疫苗记录不存在");
        }

        // 更新记录
        BeanUtils.copyProperties(updateDTO, record);
        record.setCreateTime(null); // 避免更新创建时间

        if (!updateById(record)) {
            throw new BusinessException("更新疫苗记录失败");
        }

        return convertToResponseDTO(record);
    }

    @Override
    @Transactional
    public void deleteRecord(Integer recordId) {
        VaccinationRecord record = getById(recordId);
        if (record == null || record.getDeleted() == 1) {
            throw new ResourceNotFoundException("疫苗记录不存在");
        }

        // 逻辑删除
        record.setDeleted((byte) 1);
        if (!updateById(record)) {
            throw new BusinessException("删除疫苗记录失败");
        }
    }

    @Override
    public PageResult<VaccinationRecordResponseDTO> getRecordsByPetId(Integer petId, PageRequest pageRequest) {
        // 验证宠物存在
        Pet pet = petMapper.selectById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new ResourceNotFoundException("宠物不存在");
        }

        // 分页查询
        QueryWrapper<VaccinationRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId)
                   .eq("deleted", 0)
                   .orderByDesc("vaccination_date");

        IPage<VaccinationRecord> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        IPage<VaccinationRecord> resultPage = page(page, queryWrapper);

        // 转换为DTO
        List<VaccinationRecordResponseDTO> dtoList = resultPage.getRecords().stream()
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
    public List<VaccinationRecordResponseDTO> getUpcomingVaccinations(LocalDate date) {
        QueryWrapper<VaccinationRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("next_due_date", date)
                   .le("next_due_date", date.plusDays(30))
                   .eq("deleted", 0)
                   .orderByAsc("next_due_date");

        return list(queryWrapper).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VaccinationRecordResponseDTO> getExpiredVaccinations(LocalDate date) {
        QueryWrapper<VaccinationRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("next_due_date", date)
                   .eq("deleted", 0)
                   .orderByAsc("next_due_date");

        return list(queryWrapper).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将实体转换为响应DTO
     */
    private VaccinationRecordResponseDTO convertToResponseDTO(VaccinationRecord record) {
        VaccinationRecordResponseDTO dto = new VaccinationRecordResponseDTO();
        BeanUtils.copyProperties(record, dto);
        
        // 计算距离下次接种天数
        if (record.getNextDueDate() != null) {
            long daysUntilNext = ChronoUnit.DAYS.between(LocalDate.now(), record.getNextDueDate());
            dto.setDaysUntilNext(Math.toIntExact(daysUntilNext));
            dto.setIsExpiringSoon(daysUntilNext <= 7 && daysUntilNext >= 0);
        }
        
        return dto;
    }
}