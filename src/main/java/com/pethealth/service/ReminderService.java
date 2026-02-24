package com.pethealth.service;

import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.dto.*;
import com.pethealth.entity.Reminder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 存储提醒事项，支持微信推送 服务类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
public interface ReminderService extends IService<Reminder> {

    /**
     * 创建提醒
     * @param createDTO 创建请求DTO
     * @param userId 用户ID
     * @return 提醒响应DTO
     */
    ReminderResponseDTO createReminder(ReminderCreateDTO createDTO, Integer userId);

    /**
     * 更新提醒
     * @param reminderId 提醒ID
     * @param updateDTO 更新请求DTO
     * @return 提醒响应DTO
     */
    ReminderResponseDTO updateReminder(Integer reminderId, ReminderUpdateDTO updateDTO);

    /**
     * 删除提醒（逻辑删除）
     * @param reminderId 提醒ID
     */
    void deleteReminder(Integer reminderId);

    /**
     * 标记提醒为已完成
     * @param reminderId 提醒ID
     * @return 提醒响应DTO
     */
    ReminderResponseDTO completeReminder(Integer reminderId);

    /**
     * 获取提醒详情
     * @param reminderId 提醒ID
     * @return 提醒响应DTO
     */
    ReminderResponseDTO getReminderById(Integer reminderId);

    /**
     * 根据用户ID获取提醒列表（分页）
     * @param userId 用户ID
     * @param queryDTO 查询条件
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<ReminderResponseDTO> getRemindersByUserId(Integer userId, ReminderQueryDTO queryDTO, PageRequest pageRequest);

    /**
     * 获取即将到期的提醒
     * @param userId 用户ID
     * @param hours 时间范围（小时）
     * @return 提醒列表
     */
    List<ReminderResponseDTO> getUpcomingReminders(Integer userId, int hours);

    /**
     * 获取逾期未完成的提醒
     * @param userId 用户ID
     * @return 提醒列表
     */
    List<ReminderResponseDTO> getOverdueReminders(Integer userId);

    /**
     * 获取今日提醒
     * @param userId 用户ID
     * @return 提醒列表
     */
    List<ReminderResponseDTO> getTodayReminders(Integer userId);

    /**
     * 自动生成健康相关的提醒
     * @param userId 用户ID
     */
    void generateHealthReminders(Integer userId);
}
