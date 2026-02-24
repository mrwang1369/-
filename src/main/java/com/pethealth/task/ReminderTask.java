package com.pethealth.task;

import com.pethealth.dto.ReminderResponseDTO;
import com.pethealth.service.ReminderService;
import com.pethealth.service.WechatMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 提醒定时任务
 * 定期检查即将到期和逾期的提醒
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Component
@Slf4j
public class ReminderTask {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private WechatMessageService wechatMessageService;

    /**
     * 每小时检查一次即将到期的提醒（未来24小时内）
     */
    @Scheduled(fixedRate = 60 * 60 * 1000) // 每小时执行一次
    public void checkUpcomingReminders() {
        try {
            log.info("开始检查即将到期的提醒...");
            
            // 获取所有用户ID（这里简化处理，实际项目中需要从用户表获取）
            // 暂时使用固定用户ID进行演示
            Integer[] userIds = {1}; // 示例用户ID
            
            for (Integer userId : userIds) {
                List<ReminderResponseDTO> upcomingReminders = reminderService.getUpcomingReminders(userId, 24);
                if (!upcomingReminders.isEmpty()) {
                    log.info("用户{}有{}个即将到期的提醒", userId, upcomingReminders.size());
                    // TODO: 这里可以调用微信推送服务发送提醒
                    processUpcomingReminders(upcomingReminders, userId);
                }
            }
            
            log.info("即将到期提醒检查完成");
        } catch (Exception e) {
            log.error("检查即将到期提醒时发生错误", e);
        }
    }

    /**
     * 每天凌晨检查逾期未完成的提醒
     */
    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨0点执行
    public void checkOverdueReminders() {
        try {
            log.info("开始检查逾期未完成的提醒...");
            
            // 获取所有用户ID（这里简化处理）
            Integer[] userIds = {1}; // 示例用户ID
            
            for (Integer userId : userIds) {
                List<ReminderResponseDTO> overdueReminders = reminderService.getOverdueReminders(userId);
                if (!overdueReminders.isEmpty()) {
                    log.info("用户{}有{}个逾期未完成的提醒", userId, overdueReminders.size());
                    // TODO: 这里可以调用微信推送服务发送逾期提醒
                    processOverdueReminders(overdueReminders, userId);
                }
            }
            
            log.info("逾期提醒检查完成");
        } catch (Exception e) {
            log.error("检查逾期提醒时发生错误", e);
        }
    }

    /**
     * 处理即将到期的提醒
     */
    private void processUpcomingReminders(List<ReminderResponseDTO> reminders, Integer userId) {
        for (ReminderResponseDTO reminder : reminders) {
            log.info("处理即将到期提醒: reminderId={}, title={}, dueDate={}", 
                    reminder.getReminderId(), reminder.getTitle(), reminder.getDueDate());
            
            // 发送微信提醒
            wechatMessageService.sendUpcomingReminder(userId, reminder);
        }
    }

    /**
     * 处理逾期未完成的提醒
     */
    private void processOverdueReminders(List<ReminderResponseDTO> reminders, Integer userId) {
        for (ReminderResponseDTO reminder : reminders) {
            log.info("处理逾期提醒: reminderId={}, title={}, dueDate={}", 
                    reminder.getReminderId(), reminder.getTitle(), reminder.getDueDate());
            
            // 发送微信逾期提醒
            wechatMessageService.sendOverdueReminder(userId, reminder);
        }
    }

    /**
     * 当用户完成提醒时发送确认消息
     */
    public void sendCompletionMessage(Integer userId, ReminderResponseDTO reminder) {
        wechatMessageService.sendCompletionConfirmation(userId, reminder);
        log.info("已发送完成确认消息: userId={}, reminderId={}", userId, reminder.getReminderId());
    }
}