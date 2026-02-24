package com.pethealth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.dto.*;
import com.pethealth.entity.Pet;
import com.pethealth.entity.Reminder;
import com.pethealth.handler.BusinessException;
import com.pethealth.handler.ResourceNotFoundException;
import com.pethealth.mapper.PetMapper;
import com.pethealth.mapper.ReminderMapper;
import com.pethealth.service.ReminderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
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

// 已移除重复导入

/**
 * <p>
 * 存储提醒事项，支持微信推送 服务实现类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Service
@Slf4j
public class ReminderServiceImpl extends ServiceImpl<ReminderMapper, Reminder> implements ReminderService {

    // 提醒状态常量
    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_COMPLETED = "completed";
    
    // 删除状态常量
    private static final byte DELETED_NO = (byte) 0;
    private static final byte DELETED_YES = (byte) 1;

    @Autowired
    private PetMapper petMapper;

    /**
     * 创建新的提醒事项
     * 
     * @param createDTO 创建请求DTO，包含提醒的基本信息
     * @param userId 用户ID，用于关联提醒的所有者
     * @return 创建成功的提醒响应DTO
     * @throws ResourceNotFoundException 当指定的宠物不存在时抛出
     * @throws BusinessException 当创建提醒失败时抛出
     */
    @Override
    @Transactional
    public ReminderResponseDTO createReminder(ReminderCreateDTO createDTO, Integer userId) {
        // 验证宠物存在（如果指定了宠物ID）
        if (createDTO.getPetId() != null) {
            Pet pet = petMapper.selectById(createDTO.getPetId());
            if (pet == null || pet.getDeleted() == 1) {
                throw new ResourceNotFoundException("宠物不存在");
            }
            // 验证宠物归属
            if (!pet.getUserId().equals(userId)) {
                throw new BusinessException("无权操作该宠物的提醒");
            }
        }

        // 创建提醒
        Reminder reminder = new Reminder();
        BeanUtils.copyProperties(createDTO, reminder);
        reminder.setUserId(userId);
        reminder.setStatus(STATUS_PENDING);
        reminder.setCreateTime(LocalDateTime.now());
        reminder.setDeleted(DELETED_NO);

        if (!save(reminder)) {
            throw new BusinessException("创建提醒失败");
        }

        log.info("创建提醒成功: reminderId={}, userId={}, title={}", 
                reminder.getReminderId(), userId, reminder.getTitle());

        return convertToResponseDTO(reminder);
    }

    /**
     * 更新提醒事项
     * 
     * @param reminderId 要更新的提醒ID
     * @param updateDTO 更新请求DTO，包含要修改的字段
     * @return 更新后的提醒响应DTO
     * @throws ResourceNotFoundException 当提醒不存在时抛出
     * @throws BusinessException 当更新失败时抛出
     */
    @Override
    @Transactional
    public ReminderResponseDTO updateReminder(Integer reminderId, ReminderUpdateDTO updateDTO) {
        Reminder reminder = getById(reminderId);
        if (reminder == null || reminder.getDeleted() == DELETED_YES) {
            throw new ResourceNotFoundException("提醒不存在");
        }

        // 如果更新了宠物ID，需要验证宠物存在
        if (updateDTO.getPetId() != null && !updateDTO.getPetId().equals(reminder.getPetId())) {
            Pet pet = petMapper.selectById(updateDTO.getPetId());
            if (pet == null || pet.getDeleted() == DELETED_YES) {
                throw new ResourceNotFoundException("宠物不存在");
            }
        }

        // 更新提醒
        BeanUtils.copyProperties(updateDTO, reminder);
        reminder.setCreateTime(null); // 避免更新创建时间

        if (!updateById(reminder)) {
            throw new BusinessException("更新提醒失败");
        }

        log.info("更新提醒成功: reminderId={}", reminderId);

        return convertToResponseDTO(reminder);
    }

    @Override
    @Transactional
    public void deleteReminder(Integer reminderId) {
        Reminder reminder = getById(reminderId);
        if (reminder == null || reminder.getDeleted() == DELETED_YES) {
            throw new ResourceNotFoundException("提醒不存在");
        }

        // 逻辑删除
        reminder.setDeleted(DELETED_YES);
        if (!updateById(reminder)) {
            throw new BusinessException("删除提醒失败");
        }

        log.info("删除提醒成功: reminderId={}", reminderId);
    }

    @Override
    @Transactional
    public ReminderResponseDTO completeReminder(Integer reminderId) {
        Reminder reminder = getById(reminderId);
        if (reminder == null || reminder.getDeleted() == DELETED_YES) {
            throw new ResourceNotFoundException("提醒不存在");
        }

        // 更新状态为已完成
        reminder.setStatus(STATUS_COMPLETED);
        reminder.setCompletedTime(LocalDateTime.now());

        if (!updateById(reminder)) {
            throw new BusinessException("更新提醒状态失败");
        }

        log.info("完成提醒: reminderId={}", reminderId);

        return convertToResponseDTO(reminder);
    }

    @Override
    public ReminderResponseDTO getReminderById(Integer reminderId) {
        Reminder reminder = getById(reminderId);
        if (reminder == null || reminder.getDeleted() == 1) {
            throw new ResourceNotFoundException("提醒不存在");
        }

        return convertToResponseDTO(reminder);
    }

    @Override
    public PageResult<ReminderResponseDTO> getRemindersByUserId(Integer userId, ReminderQueryDTO queryDTO, PageRequest pageRequest) {
        QueryWrapper<Reminder> queryWrapper = buildQueryWrapper(userId, queryDTO);
        queryWrapper.orderByDesc("due_date");

        IPage<Reminder> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        IPage<Reminder> resultPage = page(page, queryWrapper);

        List<ReminderResponseDTO> dtoList = resultPage.getRecords().stream()
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
    public List<ReminderResponseDTO> getUpcomingReminders(Integer userId, int hours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusHours(hours);

        QueryWrapper<Reminder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("status", STATUS_PENDING)
                .eq("deleted", DELETED_NO)
                .between("due_date", now, endTime)
                .orderByAsc("due_date");

        return list(queryWrapper).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReminderResponseDTO> getOverdueReminders(Integer userId) {
        QueryWrapper<Reminder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("status", STATUS_PENDING)
                .eq("deleted", DELETED_NO)
                .lt("due_date", LocalDateTime.now())
                .orderByAsc("due_date");

        return list(queryWrapper).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReminderResponseDTO> getTodayReminders(Integer userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        QueryWrapper<Reminder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("deleted", 0)
                .between("due_date", startOfDay, endOfDay)
                .orderByAsc("due_date");

        return list(queryWrapper).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void generateHealthReminders(Integer userId) {
        // TODO: 实现自动生成健康提醒逻辑
        // 可以根据宠物的疫苗记录、驱虫记录等自动生成提醒
        log.info("开始为用户{}生成健康提醒", userId);
    }

    /**
     * 构建查询条件
     */
    private QueryWrapper<Reminder> buildQueryWrapper(Integer userId, ReminderQueryDTO queryDTO) {
        QueryWrapper<Reminder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("deleted", DELETED_NO);

        if (queryDTO.getPetId() != null) {
            queryWrapper.eq("pet_id", queryDTO.getPetId());
        }

        if (StringUtils.hasText(queryDTO.getReminderType())) {
            queryWrapper.eq("reminder_type", queryDTO.getReminderType());
        }

        if (StringUtils.hasText(queryDTO.getStatus()) && !"all".equals(queryDTO.getStatus())) {
            queryWrapper.eq("status", queryDTO.getStatus());
        }

        if (Boolean.TRUE.equals(queryDTO.getTodayOnly())) {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(23, 59, 59);
            queryWrapper.between("due_date", startOfDay, endOfDay);
        }

        if (Boolean.TRUE.equals(queryDTO.getOverdueOnly())) {
            queryWrapper.lt("due_date", LocalDateTime.now());
        }

        return queryWrapper;
    }

    /**
     * 将实体转换为响应DTO
     */
    private ReminderResponseDTO convertToResponseDTO(Reminder reminder) {
        ReminderResponseDTO dto = new ReminderResponseDTO();
        BeanUtils.copyProperties(reminder, dto);

        // 设置宠物名称
        if (reminder.getPetId() != null) {
            Pet pet = petMapper.selectById(reminder.getPetId());
            if (pet != null) {
                dto.setPetName(pet.getName());
            }
        }

        // 计算是否逾期
        if (reminder.getDueDate() != null) {
            dto.setIsOverdue(reminder.getDueDate().isBefore(LocalDateTime.now()) 
                    && STATUS_PENDING.equals(reminder.getStatus()));
            
            // 计算距离截止时间天数
            long daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), reminder.getDueDate().toLocalDate());
            dto.setDaysUntilDue(Math.toIntExact(daysUntilDue));
        }

        return dto;
    }
}
