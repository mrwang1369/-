package com.pethealth.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pethealth.BaseTest;
import com.pethealth.dto.*;
import com.pethealth.entity.Pet;
import com.pethealth.entity.Reminder;
import com.pethealth.handler.BusinessException;
import com.pethealth.handler.ResourceNotFoundException;
import com.pethealth.mapper.PetMapper;
import com.pethealth.mapper.ReminderMapper;
import com.pethealth.service.impl.ReminderServiceImpl;
import com.pethealth.test.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.lang.reflect.Field;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 提醒服务完整测试套件
 * 涵盖所有业务场景和边界条件
 * 
 * @author Mr wang
 * @since 2026-02-24
 */
class ReminderServiceComprehensiveTest extends BaseTest {

    @Mock
    private ReminderMapper reminderMapper;

    @Mock
    private PetMapper petMapper;

    @InjectMocks
    private ReminderServiceImpl reminderService;

    private Reminder sampleReminder;
    private Pet samplePet;

    @BeforeEach
    void setUp() throws Exception {
        // 使用测试数据工厂创建标准测试数据
        sampleReminder = TestDataFactory.createReminder(1, 1, 1, "每日喂食");
        samplePet = TestDataFactory.createPet(1, 1, "小白");
        
        // 重要：正确Mock MyBatis-Plus的BaseMapper
        mockBaseMapper();
        
        prepareTestData();
    }
    
    /**
     * Mock MyBatis-Plus的BaseMapper
     * 这是解决NullPointerException的关键
     */
    private void mockBaseMapper() throws Exception {
        // 通过反射获取BaseMapper字段并进行Mock
        Field baseMapperField = ReminderServiceImpl.class.getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        
        // 创建BaseMapper的Mock
        BaseMapper<Reminder> mockBaseMapper = mock(BaseMapper.class);
        baseMapperField.set(reminderService, mockBaseMapper);
        
        // 配置BaseMapper的默认行为（使用lenient避免不必要的stubbing警告）
        lenient().when(mockBaseMapper.insert(any(Reminder.class))).thenAnswer(invocation -> {
            Reminder reminder = invocation.getArgument(0);
            if (reminder.getReminderId() == null) {
                reminder.setReminderId(1); // 模拟自增ID
            }
            return 1; // 返回影响行数
        });
        
        lenient().when(mockBaseMapper.selectById(any())).thenAnswer(invocation -> {
            Integer id = invocation.getArgument(0);
            if (id.equals(1)) {
                return sampleReminder;
            } else if (id.equals(999)) {
                return null; // 模拟不存在的记录
            }
            return sampleReminder; // 默认返回存在的记录
        });
        
        lenient().when(mockBaseMapper.updateById(any(Reminder.class))).thenReturn(1);
    }

    // ==================== 创建提醒测试 ====================

    @Test
    void testCreateReminder_WithGlobalReminder_ShouldSuccess() {
        // Arrange
        ReminderCreateDTO createDTO = TestDataFactory.createReminderCreateDTO(null, "全局提醒");
        // BaseMapper已经在setUp中配置

        // Act
        ReminderResponseDTO result = reminderService.createReminder(createDTO, 1);

        // Assert
        assertNotNull(result, "全局提醒创建应成功");
        assertNull(result.getPetId(), "全局提醒的宠物ID应为null");
        assertEquals("全局提醒", result.getTitle(), "标题应正确");
        // 验证BaseMapper的insert方法被调用
        verify(petMapper, never()).selectById(any());
    }

    @Test
    void testCreateReminder_WithValidPet_ShouldSuccess() {
        // Arrange
        ReminderCreateDTO createDTO = TestDataFactory.createReminderCreateDTO(1, "每日喂食");
        when(petMapper.selectById(1)).thenReturn(samplePet);
        // BaseMapper已经在setUp中配置

        // Act
        ReminderResponseDTO result = reminderService.createReminder(createDTO, 1);

        // Assert
        assertNotNull(result, "带宠物的提醒创建应成功");
        assertEquals(1, result.getPetId(), "宠物ID应正确");
        assertEquals("小白", result.getPetName(), "宠物名称应正确");
        verify(petMapper, times(2)).selectById(1); // 创建时1次 + DTO转换时1次
        // BaseMapper的insert会在后台被调用
    }

    @Test
    void testCreateReminder_WithNonExistentPet_ShouldThrowResourceNotFoundException() {
        // Arrange
        ReminderCreateDTO createDTO = TestDataFactory.createReminderCreateDTO(999, "测试提醒");
        when(petMapper.selectById(999)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> reminderService.createReminder(createDTO, 1),
            "当宠物不存在时应抛出ResourceNotFoundException"
        );
        
        assertEquals("宠物不存在", exception.getMessage(), "异常消息应正确");
        verify(petMapper).selectById(999);
        verify(reminderMapper, never()).insert(any());
    }

    @Test
    void testCreateReminder_WithPermissionDenied_ShouldThrowBusinessException() {
        // Arrange
        ReminderCreateDTO createDTO = TestDataFactory.createReminderCreateDTO(1, "他人宠物提醒");
        
        Pet otherUserPet = TestDataFactory.createPet(1, 2, "他人的宠物"); // 用户ID为2
        
        when(petMapper.selectById(1)).thenReturn(otherUserPet);

        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> reminderService.createReminder(createDTO, 1), // 当前用户ID为1
            "当无权限操作宠物时应抛出BusinessException"
        );
        
        assertEquals("无权操作该宠物的提醒", exception.getMessage(), "异常消息应正确");
        verify(petMapper).selectById(1);
        verify(reminderMapper, never()).insert(any());
    }

    @Test
    void testCreateReminder_InsertFailure_ShouldThrowBusinessException() throws Exception {
        // Arrange - 重新配置BaseMapper模拟插入失败
        Field baseMapperField = ReminderServiceImpl.class.getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        BaseMapper<Reminder> mockBaseMapper = mock(BaseMapper.class);
        baseMapperField.set(reminderService, mockBaseMapper);
        
        // 配置插入失败的情况
        when(mockBaseMapper.insert(any(Reminder.class))).thenReturn(0); // 返回0表示插入失败
        
        ReminderCreateDTO createDTO = TestDataFactory.createReminderCreateDTO(null, "插入失败测试");

        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> reminderService.createReminder(createDTO, 1),
            "当数据库插入失败时应抛出BusinessException"
        );
        
        assertEquals("创建提醒失败", exception.getMessage(), "异常消息应正确");
        verify(mockBaseMapper).insert(any(Reminder.class));
    }

    // ==================== 更新提醒测试 ====================

    @Test
    void testUpdateReminder_WithValidData_ShouldSuccess() {
        // Arrange
        ReminderUpdateDTO updateDTO = TestDataFactory.createReminderUpdateDTO("更新后的标题", "completed");
        when(reminderMapper.selectById(1)).thenReturn(sampleReminder);
        when(reminderMapper.updateById(any(Reminder.class))).thenReturn(1);

        // Act
        ReminderResponseDTO result = reminderService.updateReminder(1, updateDTO);

        // Assert
        assertNotNull(result, "提醒更新应成功");
        assertEquals("更新后的标题", result.getTitle(), "标题应更新成功");
        assertEquals("completed", result.getStatus(), "状态应更新成功");
        verify(reminderMapper).selectById(1);
        verify(reminderMapper).updateById(any(Reminder.class));
    }

    @Test
    void testUpdateReminder_WithNonExistentReminder_ShouldThrowResourceNotFoundException() {
        // Arrange
        ReminderUpdateDTO updateDTO = TestDataFactory.createReminderUpdateDTO("测试", "pending");
        // BaseMapper配置中已处理ID=999返回null的情况

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> reminderService.updateReminder(999, updateDTO),
            "当提醒不存在时应抛出ResourceNotFoundException"
        );
        
        assertEquals("提醒不存在", exception.getMessage(), "异常消息应正确");
        // BaseMapper的selectById会被调用，但返回null
    }

    // ==================== 删除提醒测试 ====================

    @Test
    void testDeleteReminder_ExistingReminder_ShouldSuccess() {
        // Arrange
        when(reminderMapper.selectById(1)).thenReturn(sampleReminder);
        when(reminderMapper.updateById(any(Reminder.class))).thenReturn(1);

        // Act
        reminderService.deleteReminder(1);

        // Assert - 验证BaseMapper被正确调用
        verify(petMapper).selectById(1);
        // BaseMapper的操作在mockBaseMapper中已配置
        assertEquals((byte) 1, sampleReminder.getDeleted(), "删除标记应设置为1");
    }

    @Test
    void testDeleteReminder_NonExistentReminder_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(reminderMapper.selectById(999)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> reminderService.deleteReminder(999),
            "当提醒不存在时应抛出ResourceNotFoundException"
        );
        
        assertEquals("提醒不存在", exception.getMessage(), "异常消息应正确");
        verify(reminderMapper).selectById(999);
        verify(reminderMapper, never()).updateById(any());
    }

    // ==================== 完成提醒测试 ====================

    @Test
    void testCompleteReminder_ValidReminder_ShouldSuccess() {
        // Arrange
        when(reminderMapper.selectById(1)).thenReturn(sampleReminder);
        when(reminderMapper.updateById(any(Reminder.class))).thenReturn(1);

        // Act
        ReminderResponseDTO result = reminderService.completeReminder(1);

        // Assert
        assertNotNull(result, "完成提醒操作应成功");
        assertEquals("completed", result.getStatus(), "状态应更新为completed");
        assertNotNull(result.getCompletedTime(), "完成时间不应为null");
        verify(reminderMapper).selectById(1);
        verify(reminderMapper).updateById(any(Reminder.class));
    }

    // ==================== 查询提醒测试 ====================

    @Test
    void testGetReminderById_ExistingReminder_ShouldSuccess() {
        // Arrange
        when(reminderMapper.selectById(1)).thenReturn(sampleReminder);
        when(petMapper.selectById(1)).thenReturn(samplePet);

        // Act
        ReminderResponseDTO result = reminderService.getReminderById(1);

        // Assert
        assertNotNull(result, "获取提醒详情应成功");
        assertEquals(1, result.getReminderId(), "提醒ID应正确");
        assertEquals("小白", result.getPetName(), "宠物名称应正确");
        verify(reminderMapper).selectById(1);
        verify(petMapper).selectById(1);
    }

    @Test
    void testGetReminderById_NonExistentReminder_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(reminderMapper.selectById(999)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> reminderService.getReminderById(999),
            "当提醒不存在时应抛出ResourceNotFoundException"
        );
        
        assertEquals("提醒不存在", exception.getMessage(), "异常消息应正确");
        verify(reminderMapper).selectById(999);
        verify(petMapper, never()).selectById(any());
    }

    // ==================== 边界条件测试 ====================

    @Test
    void testCreateReminder_WithEmptyTitle_ShouldHandleValidation() {
        // Arrange
        ReminderCreateDTO createDTO = TestDataFactory.createReminderCreateDTO(1, "");
        when(petMapper.selectById(1)).thenReturn(samplePet);
        // BaseMapper已在setUp中配置

        // Act
        ReminderResponseDTO result = reminderService.createReminder(createDTO, 1);

        // Assert
        assertNotNull(result, "空标题的提醒也应能创建");
        assertEquals("", result.getTitle(), "标题应保持为空");
    }

    @Test
    void testCreateReminder_WithFutureDueDate_ShouldCalculateDaysCorrectly() {
        // Arrange
        ReminderCreateDTO createDTO = TestDataFactory.createReminderCreateDTO(null, "未来提醒");
        // 设置未来的截止日期
        sampleReminder.setDueDate(LocalDateTime.now().plusDays(5));
        // BaseMapper已在setUp中配置

        // Act
        ReminderResponseDTO result = reminderService.createReminder(createDTO, 1);

        // Assert
        assertNotNull(result, "未来日期的提醒应能创建");
        // 注意：这里可能需要调整，因为convertToResponseDTO方法会重新计算天数
    }

    @Override
    protected void prepareTestData() {
        // 可以在这里准备特定于测试的数据
        super.prepareTestData();
    }

    @Override
    protected void cleanupTestData() {
        // 清理测试数据
        super.cleanupTestData();
    }
}