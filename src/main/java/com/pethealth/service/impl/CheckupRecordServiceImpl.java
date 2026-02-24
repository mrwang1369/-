package com.pethealth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import com.pethealth.service.CheckupRecordService;
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
 * 记录体检信息，支持图片上传 服务实现类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Service
public class CheckupRecordServiceImpl extends ServiceImpl<CheckupRecordMapper, CheckupRecord> implements CheckupRecordService {

    @Autowired
    private PetMapper petMapper;

    @Override
    @Transactional
    public CheckupRecordResponseDTO createRecord(CheckupRecordCreateDTO createDTO) {
        // 验证宠物存在
        Pet pet = petMapper.selectById(createDTO.getPetId());
        if (pet == null || pet.getDeleted() == 1) {
            throw new ResourceNotFoundException("宠物不存在");
        }

        // 创建体检记录
        CheckupRecord record = new CheckupRecord();
        BeanUtils.copyProperties(createDTO, record);
        record.setCreateTime(LocalDateTime.now());
        record.setDeleted((byte) 0);

        if (!save(record)) {
            throw new BusinessException("创建体检记录失败");
        }

        return convertToResponseDTO(record);
    }

    @Override
    @Transactional
    public CheckupRecordResponseDTO updateRecord(Integer recordId, CheckupRecordUpdateDTO updateDTO) {
        // 检查记录是否存在
        CheckupRecord record = getById(recordId);
        if (record == null || record.getDeleted() == 1) {
            throw new ResourceNotFoundException("体检记录不存在");
        }

        // 更新记录
        BeanUtils.copyProperties(updateDTO, record);
        record.setCreateTime(null); // 避免更新创建时间

        if (!updateById(record)) {
            throw new BusinessException("更新体检记录失败");
        }

        return convertToResponseDTO(record);
    }

    @Override
    @Transactional
    public void deleteRecord(Integer recordId) {
        CheckupRecord record = getById(recordId);
        if (record == null || record.getDeleted() == 1) {
            throw new ResourceNotFoundException("体检记录不存在");
        }

        // 逻辑删除
        record.setDeleted((byte) 1);
        if (!updateById(record)) {
            throw new BusinessException("删除体检记录失败");
        }
    }

    @Override
    public PageResult<CheckupRecordResponseDTO> getRecordsByPetId(Integer petId, PageRequest pageRequest) {
        // 验证宠物存在
        Pet pet = petMapper.selectById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new ResourceNotFoundException("宠物不存在");
        }

        // 分页查询
        QueryWrapper<CheckupRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId)
                   .eq("deleted", 0)
                   .orderByDesc("checkup_date");

        IPage<CheckupRecord> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        IPage<CheckupRecord> resultPage = page(page, queryWrapper);

        // 转换为DTO
        List<CheckupRecordResponseDTO> dtoList = resultPage.getRecords().stream()
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
    public List<CheckupRecordResponseDTO> getRecentCheckups(LocalDate date) {
        QueryWrapper<CheckupRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("checkup_date", date.minusDays(30))
                   .eq("deleted", 0)
                   .orderByDesc("checkup_date");

        return list(queryWrapper).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CheckupRecordResponseDTO> getOverdueCheckups(int months) {
        LocalDate cutoffDate = LocalDate.now().minusMonths(months);
        
        QueryWrapper<CheckupRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("checkup_date", cutoffDate)
                   .eq("deleted", 0)
                   .orderByAsc("checkup_date");

        return list(queryWrapper).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将实体转换为响应DTO
     */
    private CheckupRecordResponseDTO convertToResponseDTO(CheckupRecord record) {
        CheckupRecordResponseDTO dto = new CheckupRecordResponseDTO();
        BeanUtils.copyProperties(record, dto);
        
        // 计算体检距今天数
        if (record.getCheckupDate() != null) {
            long daysAgo = ChronoUnit.DAYS.between(record.getCheckupDate(), LocalDate.now());
            dto.setDaysAgo(Math.toIntExact(daysAgo));
            dto.setIsRecent(daysAgo <= 30);
        }
        
        return dto;
    }
}