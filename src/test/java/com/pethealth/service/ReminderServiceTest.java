package com.pethealth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pethealth.BaseTest;
import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.lang.reflect.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 提醒服务单元测试
 * 遵循AAA测试模式和命名规范
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReminderServiceTest {
    
    @MockBean
    private ReminderMapper reminderMapper;
    
    @MockBean
    private PetMapper petMapper;
    
    @Autowired
    private ReminderServiceImpl reminderService;

    private Reminder sampleReminder;
    private Pet samplePet;

    @BeforeEach
    void setUp() {
        // 使用测试数据工厂创建测试数据
        sampleReminder = TestDataFactory.createReminder(1, 1, 1, "每日喂食");
        samplePet = TestDataFactory.createPet(1, 1, "小白");
        
        // 准备测试数据
        // prepareTestData(); // 暂时注释掉，因为我们不再继承BaseTest
    }

    @Test
    void testCreateReminder_WithValidPet_ShouldSuccess() {
        // Arrange - 准备阶段
        ReminderCreateDTO createDTO = TestDataFactory.createReminderCreateDTO(1, "每日喂食");
        
        when(petMapper.selectById(1)).thenReturn(samplePet);
        when(reminderMapper.insert(any(Reminder.class))).thenReturn(1);

        // Act - 执行阶段
        ReminderResponseDTO result = reminderService.createReminder(createDTO, 1);

        // Assert - 断言阶段
        assertNotNull(result, "创建的提醒响应不应为null");
        assertEquals("喂食", result.getReminderType(), "提醒类型应正确");
        assertEquals("每日喂食", result.getTitle(), "提醒标题应正确");
        assertEquals(1, result.getUserId(), "用户ID应正确");
        verify(petMapper, times(2)).selectById(1);  // 修正验证次数为2次
        verify(reminderMapper).insert(any(Reminder.class));
    }

    @Test
    void testCreateReminder_WithNonExistentPet_ShouldThrowResourceNotFoundException() {
        // Arrange - 准备阶段
        ReminderCreateDTO createDTO = TestDataFactory.createReminderCreateDTO(999, "测试提醒");
        
        when(petMapper.selectById(999)).thenReturn(null);

        // Act & Assert - 执行和断言
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> reminderService.createReminder(createDTO, 1),
            "当宠物不存在时应抛出ResourceNotFoundException"
        );
        
        assertEquals("宠物不存在", exception.getMessage(), "异常消息应正确");
    }

    @Test
    void testCreateReminder_NoPermission() {
        // Arrange - 准备阶段：创建权限不足的测试数据
        ReminderCreateDTO createDTO = new ReminderCreateDTO();
        createDTO.setPetId(1);

        Pet otherPet = new Pet();
        otherPet.setPetId(1);
        otherPet.setUserId(2); // 不同的用户ID，造成权限不足
        otherPet.setDeleted((byte) 0);

        when(petMapper.selectById(1)).thenReturn(otherPet);

        // Act & Assert - 执行和断言：应该抛出权限异常
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> reminderService.createReminder(createDTO, 1),
            "当用户无权限操作宠物提醒时应抛出BusinessException"
        );
        
        assertEquals("无权操作该宠物的提醒", exception.getMessage(), "权限异常消息应正确");
    }

    @Test
    void testUpdateReminder_Success() {
        // Arrange - 准备阶段：创建更新数据和Mock配置
        ReminderUpdateDTO updateDTO = new ReminderUpdateDTO();
        updateDTO.setTitle("更新后的标题");
        updateDTO.setStatus("completed");

        when(reminderMapper.selectById(1)).thenReturn(sampleReminder);
        when(reminderMapper.updateById(any(Reminder.class))).thenReturn(1);

        // Act - 执行阶段：调用更新方法
        ReminderResponseDTO result = reminderService.updateReminder(1, updateDTO);

        // Assert - 断言阶段：验证更新结果
        assertNotNull(result, "更新后的提醒响应不应为null");
        assertEquals("更新后的标题", result.getTitle(), "提醒标题应更新成功");
        assertEquals("completed", result.getStatus(), "提醒状态应更新为completed");
        verify(reminderMapper).updateById(any(Reminder.class));
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
    void testDeleteReminder_Success() {
        // Arrange - 准备阶段：配置存在的提醒和Mock行为
        when(reminderMapper.selectById(1)).thenReturn(sampleReminder);
        when(reminderMapper.updateById(any(Reminder.class))).thenReturn(1);

        // Act - 执行阶段：调用删除方法
        reminderService.deleteReminder(1);

        // Assert - 断言阶段：验证删除操作和结果
        verify(reminderMapper).updateById(any(Reminder.class));
        assertEquals((byte) 1, sampleReminder.getDeleted(), "提醒的删除标记应设置为1");
    }

    @Test
    void testCompleteReminder_Success() {
        // Arrange - 准备阶段：配置待完成的提醒和Mock行为
        when(reminderMapper.selectById(1)).thenReturn(sampleReminder);
        when(reminderMapper.updateById(any(Reminder.class))).thenReturn(1);

        // Act - 执行阶段：调用完成提醒方法
        ReminderResponseDTO result = reminderService.completeReminder(1);

        // Assert - 断言阶段：验证完成状态和时间
        assertNotNull(result, "完成后的提醒响应不应为null");
        assertEquals("completed", result.getStatus(), "提醒状态应更新为completed");
        assertNotNull(result.getCompletedTime(), "完成时间不应为null");
        verify(reminderMapper).updateById(any(Reminder.class));
    }

    @Test
    void testGetReminderById_Success() {
        // Arrange - 准备阶段：配置存在的提醒和关联宠物
        doReturn(sampleReminder).when(reminderMapper).selectById(1);
        doReturn(samplePet).when(petMapper).selectById(1);

        // Act - 执行阶段：调用获取提醒详情方法
        ReminderResponseDTO result = reminderService.getReminderById(1);

        // Assert - 断言阶段：验证返回的提醒信息
        assertNotNull(result, "获取的提醒响应不应为null");
        assertEquals("喂食", result.getReminderType(), "提醒类型应正确");
        assertEquals("小白", result.getPetName(), "宠物名称应正确");
        verify(reminderMapper).selectById(1);  // 验证reminderMapper调用1次
        verify(petMapper).selectById(1);        // 验证petMapper调用1次
    }

    @Test
    void testGetRemindersByUserId_Success() {
        // Arrange - 准备阶段：创建查询条件和分页参数
        ReminderQueryDTO queryDTO = new ReminderQueryDTO();
        queryDTO.setStatus("pending");
        
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);

        Page<Reminder> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(sampleReminder));
        page.setTotal(1L);

        when(reminderMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        when(petMapper.selectById(1)).thenReturn(samplePet);

        // Act - 执行阶段：调用分页查询方法
        PageResult<ReminderResponseDTO> result = reminderService.getRemindersByUserId(1, queryDTO, pageRequest);

        // Assert - 断言阶段：验证分页结果
        assertNotNull(result, "分页查询结果不应为null");
        assertEquals(1, result.getTotal(), "总记录数应为1");
        assertEquals(1, result.getList().size(), "返回列表大小应为1");
        assertEquals("喂食", result.getList().get(0).getReminderType(), "第一条记录的提醒类型应正确");
    }

    @Test
    void testGetUpcomingReminders_Success() {
        // Arrange - 准备阶段：配置即将到期的提醒数据
        when(reminderMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(sampleReminder));
        when(petMapper.selectById(1)).thenReturn(samplePet);

        // Act - 执行阶段：调用获取即将到期提醒方法
        List<ReminderResponseDTO> result = reminderService.getUpcomingReminders(1, 24);

        // Assert - 断言阶段：验证返回的提醒列表
        assertNotNull(result, "即将到期提醒列表不应为null");
        assertEquals(1, result.size(), "返回的提醒数量应为1");
        assertEquals("喂食", result.get(0).getReminderType(), "提醒类型应正确");
    }

    @Test
    void testGetOverdueReminders_Success() {
        // Arrange - 准备阶段：设置逾期的提醒数据
        sampleReminder.setDueDate(LocalDateTime.now().minusDays(1)); // 设置为昨天，确保逾期
        
        when(reminderMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(sampleReminder));
        when(petMapper.selectById(1)).thenReturn(samplePet);

        // Act - 执行阶段：调用获取逾期提醒方法
        List<ReminderResponseDTO> result = reminderService.getOverdueReminders(1);

        // Assert - 断言阶段：验证逾期提醒识别
        assertNotNull(result, "逾期提醒列表不应为null");
        assertEquals(1, result.size(), "返回的逾期提醒数量应为1");
        assertTrue(result.get(0).getIsOverdue(), "提醒应被标记为逾期");
    }

    @Test
    void testGetTodayReminders_Success() {
        // Arrange - 准备阶段：设置今天的提醒数据
        sampleReminder.setDueDate(LocalDate.now().atTime(10, 0)); // 设置为今天上午10点
        
        when(reminderMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(sampleReminder));
        when(petMapper.selectById(1)).thenReturn(samplePet);

        // Act - 执行阶段：调用获取今日提醒方法
        List<ReminderResponseDTO> result = reminderService.getTodayReminders(1);

        // Assert - 断言阶段：验证今日提醒筛选
        assertNotNull(result, "今日提醒列表不应为null");
        assertEquals(1, result.size(), "返回的今日提醒数量应为1");
    }

    @Test
    void testGenerateHealthReminders_Success() {
        // Arrange - 准备阶段：无需特殊准备
        
        // Act - 执行阶段：调用健康提醒生成功能
        reminderService.generateHealthReminders(1);

        // Assert - 断言阶段：验证方法可以正常调用（目前是空实现）
        assertDoesNotThrow(
            () -> reminderService.generateHealthReminders(1),
            "健康提醒生成功能应该可以正常调用而不抛出异常"
        );
    }
}