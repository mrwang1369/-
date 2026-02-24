package com.pethealth.service;

import com.pethealth.dto.ReminderResponseDTO;

/**
 * 微信消息推送服务接口
 *
 * @author Mr wang
 * @since 2026-02-24
 */
public interface WechatMessageService {

    /**
     * 发送提醒通知
     * @param userId 用户ID
     * @param reminder 提醒信息
     * @param templateId 模板ID
     * @return 是否发送成功
     */
    boolean sendReminderNotification(Integer userId, ReminderResponseDTO reminder, String templateId);

    /**
     * 发送即将到期提醒
     * @param userId 用户ID
     * @param reminder 提醒信息
     * @return 是否发送成功
     */
    boolean sendUpcomingReminder(Integer userId, ReminderResponseDTO reminder);

    /**
     * 发送逾期提醒
     * @param userId 用户ID
     * @param reminder 提醒信息
     * @return 是否发送成功
     */
    boolean sendOverdueReminder(Integer userId, ReminderResponseDTO reminder);

    /**
     * 发送完成提醒确认
     * @param userId 用户ID
     * @param reminder 提醒信息
     * @return 是否发送成功
     */
    boolean sendCompletionConfirmation(Integer userId, ReminderResponseDTO reminder);
}