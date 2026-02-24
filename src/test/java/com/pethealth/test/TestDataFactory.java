package com.pethealth.test;

import com.pethealth.dto.*;
import com.pethealth.entity.Pet;
import com.pethealth.entity.Reminder;

import java.time.LocalDateTime;

/**
 * 测试数据工厂
 * 提供标准化的测试数据创建方法
 * 
 * @author Mr wang
 * @since 2026-02-24
 */
public class TestDataFactory {
    
    /**
     * 创建测试用的宠物对象
     * 
     * @param petId 宠物ID
     * @param userId 用户ID
     * @param name 宠物名称
     * @return Pet对象
     */
    public static Pet createPet(Integer petId, Integer userId, String name) {
        Pet pet = new Pet();
        pet.setPetId(petId);
        pet.setUserId(userId);
        pet.setName(name);
        pet.setDeleted((byte) 0);
        return pet;
    }
    
    /**
     * 创建测试用的提醒对象
     * 
     * @param reminderId 提醒ID
     * @param userId 用户ID
     * @param petId 宠物ID
     * @param title 提醒标题
     * @return Reminder对象
     */
    public static Reminder createReminder(Integer reminderId, Integer userId, Integer petId, String title) {
        Reminder reminder = new Reminder();
        reminder.setReminderId(reminderId);
        reminder.setUserId(userId);
        reminder.setPetId(petId);
        reminder.setReminderType("喂食");
        reminder.setTitle(title);
        reminder.setDueDate(LocalDateTime.now().plusDays(1));
        reminder.setStatus("pending");
        reminder.setRepeatCycle("每日");
        reminder.setNotes("按时喂食");
        reminder.setDeleted((byte) 0);
        return reminder;
    }
    
    /**
     * 创建提醒创建DTO
     * 
     * @param petId 宠物ID
     * @param title 提醒标题
     * @return ReminderCreateDTO对象
     */
    public static ReminderCreateDTO createReminderCreateDTO(Integer petId, String title) {
        ReminderCreateDTO dto = new ReminderCreateDTO();
        dto.setPetId(petId);
        dto.setReminderType("喂食");
        dto.setTitle(title);
        dto.setDueDate(LocalDateTime.now().plusDays(1));
        dto.setRepeatCycle("每日");
        dto.setNotes("按时喂食");
        return dto;
    }
    
    /**
     * 创建提醒更新DTO
     * 
     * @param title 更新后的标题
     * @param status 更新后的状态
     * @return ReminderUpdateDTO对象
     */
    public static ReminderUpdateDTO createReminderUpdateDTO(String title, String status) {
        ReminderUpdateDTO dto = new ReminderUpdateDTO();
        dto.setTitle(title);
        dto.setStatus(status);
        return dto;
    }
    
    /**
     * 创建提醒查询DTO
     * 
     * @param petId 宠物ID
     * @param reminderType 提醒类型
     * @param status 状态
     * @return ReminderQueryDTO对象
     */
    public static ReminderQueryDTO createReminderQueryDTO(Integer petId, String reminderType, String status) {
        ReminderQueryDTO dto = new ReminderQueryDTO();
        dto.setPetId(petId);
        dto.setReminderType(reminderType);
        dto.setStatus(status);
        return dto;
    }
}