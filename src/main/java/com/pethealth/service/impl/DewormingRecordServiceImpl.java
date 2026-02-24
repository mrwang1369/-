package com.pethealth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import com.pethealth.service.DewormingRecordService;
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
 * 记录驱虫信息，支持周期提醒 服务实现类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Service
public class DewormingRecordServiceImpl extends ServiceImpl<DewormingRecordMapper, DewormingRecord> implements DewormingRecordService {

    @Autowired
    private PetMapper petMapper;

    @Override
    @Transactional
    public DewormingRecordResponseDTO createRecord(DewormingRecordCreateDTO createDTO) {
        // 验证宠物存在
        Pet pet = petMapper.selectById(createDTO.getPetId());
        if (pet == null || pet.getDeleted() == 1) {
            throw new ResourceNotFoundException("宠物不存在");
        }

        // 检查是否重复记录
        QueryWrapper<DewormingRecord> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("pet_id", createDTO.getPetId())
                   .eq("deworming_type", createDTO.getDewormingType())
                   .eq("drug_name", createDTO.getDrugName())
                   .eq("date", createDTO.getDate())
                   .eq("deleted", 0);
        
        if (count(checkWrapper) > 0) {
            throw new BusinessException("该宠物当天已进行相同类型的驱虫");
        }

        // 创建驱虫记录
        DewormingRecord record = new DewormingRecord();
        BeanUtils.copyProperties(createDTO, record);
        record.setCreateTime(LocalDateTime.now());
        record.setDeleted((byte) 0);

        if (!save(record)) {
            throw new BusinessException("创建驱虫记录失败");
        }

        return convertToResponseDTO(record);
    }

    @Override
    @Transactional
    public DewormingRecordResponseDTO updateRecord(Integer recordId, DewormingRecordUpdateDTO updateDTO) {
        // 检查记录是否存在
        DewormingRecord record = getById(recordId);
        if (record == null || record.getDeleted() == 1) {
            throw new ResourceNotFoundException("驱虫记录不存在");
        }

        // 更新记录
        BeanUtils.copyProperties(updateDTO, record);
        record.setCreateTime(null); // 避免更新创建时间

        if (!updateById(record)) {
            throw new BusinessException("更新驱虫记录失败");
        }

        return convertToResponseDTO(record);
    }

    @Override
    @Transactional
    public void deleteRecord(Integer recordId) {
        DewormingRecord record = getById(recordId);
        if (record == null || record.getDeleted() == 1) {
            throw new ResourceNotFoundException("驱虫记录不存在");
        }

        // 逻辑删除
        record.setDeleted((byte) 1);
        if (!updateById(record)) {
            throw new BusinessException("删除驱虫记录失败");
        }
    }

    @Override
    public PageResult<DewormingRecordResponseDTO> getRecordsByPetId(Integer petId, PageRequest pageRequest) {
        // 验证宠物存在
        Pet pet = petMapper.selectById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new ResourceNotFoundException("宠物不存在");
        }

        // 分页查询
        QueryWrapper<DewormingRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId)
                   .eq("deleted", 0)
                   .orderByDesc("date");

        IPage<DewormingRecord> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        IPage<DewormingRecord> resultPage = page(page, queryWrapper);

        // 转换为DTO
        List<DewormingRecordResponseDTO> dtoList = resultPage.getRecords().stream()
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
    public List<DewormingRecordResponseDTO> getUpcomingDewormings(LocalDate date) {
        QueryWrapper<DewormingRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("next_date", date)
                   .le("next_date", date.plusDays(30))
                   .eq("deleted", 0)
                   .orderByAsc("next_date");

        return list(queryWrapper).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DewormingRecordResponseDTO> getExpiredDewormings(LocalDate date) {
        QueryWrapper<DewormingRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("next_date", date)
                   .isNotNull("next_date")
                   .eq("deleted", 0)
                   .orderByAsc("next_date");

        return list(queryWrapper).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LocalDate calculateNextDewormingDate(LocalDate lastDewormingDate, int cycleDays) {
        if (lastDewormingDate == null) {
            return LocalDate.now().plusDays(cycleDays);
        }
        return lastDewormingDate.plusDays(cycleDays);
    }

    /**
     * 将实体转换为响应DTO
     */
    private DewormingRecordResponseDTO convertToResponseDTO(DewormingRecord record) {
        DewormingRecordResponseDTO dto = new DewormingRecordResponseDTO();
        BeanUtils.copyProperties(record, dto);
        
        // 计算距离下次驱虫天数
        if (record.getNextDate() != null) {
            long daysUntilNext = ChronoUnit.DAYS.between(LocalDate.now(), record.getNextDate());
            dto.setDaysUntilNext(Math.toIntExact(daysUntilNext));
            dto.setIsExpiringSoon(daysUntilNext <= 7 && daysUntilNext >= 0);
        }
        
        return dto;
    }
}