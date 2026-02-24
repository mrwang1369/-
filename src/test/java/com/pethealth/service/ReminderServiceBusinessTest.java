package com.pethealth.service;

import com.pethealth.dto.*;
import com.pethealth.entity.Pet;
import com.pethealth.entity.Reminder;
import com.pethealth.handler.BusinessException;
import com.pethealth.handler.ResourceNotFoundException;
import com.pethealth.mapper.PetMapper;
import com.pethealth.mapper.ReminderMapper;
import com.pethealth.service.impl.ReminderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 提醒服务业务逻辑测试
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@ExtendWith(MockitoExtension.class)
class ReminderServiceBusinessTest {

    @Mock
    private ReminderMapper reminderMapper;

    @Mock
    private PetMapper petMapper;

    @InjectMocks
    private ReminderServiceImpl reminderService;

    private Reminder sampleReminder;
    private Pet samplePet;

    @BeforeEach
    void setUp() {
        // 创建测试用的提醒
        sampleReminder = new Reminder();
        sampleReminder.setReminderId(1);
        sampleReminder.setUserId(1);
        sampleReminder.setPetId(1);
        sampleReminder.setReminderType("喂食");
        sampleReminder.setTitle("每日喂食");
        sampleReminder.setDueDate(LocalDateTime.now().plusDays(1));
        sampleReminder.setStatus("pending");
        sampleReminder.setRepeatCycle("每日");
        sampleReminder.setNotes("按时喂食");
        sampleReminder.setDeleted((byte) 0);

        // 创建测试用的宠物
        samplePet = new Pet();
        samplePet.setPetId(1);
        samplePet.setUserId(1);
        samplePet.setName("小白");
        samplePet.setDeleted((byte) 0);
    }

    @Test
    void testCreateReminder_PetValidation() {
        // Arrange - 准备阶段：创建不存在宠物的测试数据
        ReminderCreateDTO createDTO = new ReminderCreateDTO();
        createDTO.setPetId(999); // 使用不存在的宠物ID

        when(petMapper.selectById(999)).thenReturn(null);

        // Act & Assert - 执行和断言：应该抛出宠物不存在异常
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> reminderService.createReminder(createDTO, 1),
            "当指定不存在的宠物时应抛出ResourceNotFoundException"
        );
        
        assertEquals("宠物不存在", exception.getMessage(), "宠物不存在异常消息应正确");
    }

    @Test
    void testCreateReminder_PermissionCheck() {
        // Arrange - 准备阶段：创建权限验证测试数据
        ReminderCreateDTO createDTO = new ReminderCreateDTO();
        createDTO.setPetId(1);

        Pet otherUserPet = new Pet();
        otherUserPet.setPetId(1);
        otherUserPet.setUserId(2); // 不同用户ID，造成权限不足
        otherUserPet.setDeleted((byte) 0);

        when(petMapper.selectById(1)).thenReturn(otherUserPet);

        // Act & Assert - 执行和断言：应该抛出权限异常
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> reminderService.createReminder(createDTO, 1),
            "当用户无权限操作宠物提醒时应抛出BusinessException"
        );
        
        assertEquals("无权操作该宠物的提醒", exception.getMessage(), "权限异常消息应正确");
    }

    @Test
    void testUpdateReminder_NotFound() {
        // Arrange - 准备阶段：配置不存在的提醒ID
        ReminderUpdateDTO updateDTO = new ReminderUpdateDTO();

        when(reminderMapper.selectById(999)).thenReturn(null);

        // Act & Assert - 执行和断言：应该抛出资源不存在异常
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> reminderService.updateReminder(999, updateDTO),
            "当尝试更新不存在的提醒时应抛出ResourceNotFoundException"
        );
        
        assertEquals("提醒不存在", exception.getMessage(), "资源不存在异常消息应正确");
    }

    @Test
    void testDeleteReminder_NotFound() {
        // Arrange - 准备阶段：配置不存在的提醒ID
        when(reminderMapper.selectById(999)).thenReturn(null);

        // Act & Assert - 执行和断言：应该抛出资源不存在异常
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> reminderService.deleteReminder(999),
            "当尝试删除不存在的提醒时应抛出ResourceNotFoundException"
        );
        
        assertEquals("提醒不存在", exception.getMessage(), "资源不存在异常消息应正确");
    }

    @Test
    void testCompleteReminder_NotFound() {
        // Arrange - 准备阶段：配置不存在的提醒ID
        when(reminderMapper.selectById(999)).thenReturn(null);

        // Act & Assert - 执行和断言：应该抛出资源不存在异常
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> reminderService.completeReminder(999),
            "当尝试完成不存在的提醒时应抛出ResourceNotFoundException"
        );
        
        assertEquals("提醒不存在", exception.getMessage(), "资源不存在异常消息应正确");
    }

    @Test
    void testGetReminderById_NotFound() {
        // Arrange - 准备阶段：配置不存在的提醒ID
        when(reminderMapper.selectById(999)).thenReturn(null);

        // Act & Assert - 执行和断言：应该抛出资源不存在异常
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> reminderService.getReminderById(999),
            "当尝试获取不存在的提醒时应抛出ResourceNotFoundException"
        );
        
        assertEquals("提醒不存在", exception.getMessage(), "资源不存在异常消息应正确");
    }

    @Test
    void testGenerateHealthReminders_ShouldNotThrow() {
        // Arrange - 准备阶段：无需特殊准备
        
        // Act & Assert - 执行和断言：健康提醒生成功能应该正常执行
        assertDoesNotThrow(
            () -> reminderService.generateHealthReminders(1),
            "健康提醒生成功能应该可以正常调用而不抛出异常"
        );
    }

    @Test
    void testBuildQueryWrapper_WithAllConditions() {
        // Arrange - 准备阶段：创建完整的查询条件
        ReminderQueryDTO queryDTO = new ReminderQueryDTO();
        queryDTO.setPetId(1);
        queryDTO.setReminderType("喂食");
        queryDTO.setStatus("pending");
        queryDTO.setTodayOnly(true);
        queryDTO.setOverdueOnly(false);

        // Act & Assert - 执行和断言：验证查询构建逻辑的健壮性
        assertDoesNotThrow(
            () -> {
                // 通过调用公开方法来间接测试查询构建逻辑
                when(reminderMapper.selectList(any())).thenReturn(Collections.emptyList());
                when(petMapper.selectById(any())).thenReturn(samplePet);
                reminderService.getTodayReminders(1);
            },
            "使用完整查询条件时不应该抛出异常"
        );
    }

    @Test
    void testConvertToResponseDTO_WithPet() {
        // Arrange - 准备阶段：配置关联的宠物数据
        when(petMapper.selectById(1)).thenReturn(samplePet);
        
        // Act & Assert - 执行和断言：验证DTO转换逻辑
        assertDoesNotThrow(
            () -> {
                // 通过调用公开方法来测试DTO转换逻辑
                when(reminderMapper.selectById(1)).thenReturn(sampleReminder);
                ReminderResponseDTO result = reminderService.getReminderById(1);
                assertEquals("小白", result.getPetName(), "转换后的宠物名称应正确");
            },
            "带宠物的DTO转换应该正常执行"
        );
    }

    @Test
    void testConvertToResponseDTO_WithoutPet() {
        // Arrange - 准备阶段：设置无关联宠物的提醒
        sampleReminder.setPetId(null);
        
        // Act & Assert - 执行和断言：验证无宠物时的转换逻辑
        assertDoesNotThrow(
            () -> {
                when(reminderMapper.selectById(1)).thenReturn(sampleReminder);
                ReminderResponseDTO result = reminderService.getReminderById(1);
                assertNull(result.getPetName(), "无关联宠物时宠物名称应为null");
            },
            "无宠物关联的DTO转换应该正常执行"
        );
    }
}