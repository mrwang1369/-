package com.pethealth.service;

import com.pethealth.BaseTest;
import com.pethealth.dto.ReminderCreateDTO;
import com.pethealth.dto.ReminderResponseDTO;
import com.pethealth.dto.ReminderUpdateDTO;
import com.pethealth.entity.Reminder;
import com.pethealth.handler.BusinessException;
import com.pethealth.handler.ResourceNotFoundException;
import com.pethealth.mapper.ReminderMapper;
import com.pethealth.service.impl.ReminderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 提醒服务真实数据库测试
 * 使用真实的数据库连接进行集成测试
 * 避免Mockito版本兼容性问题
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional  // 测试完成后自动回滚，不影响数据库
class ReminderServiceRealDatabaseTest extends BaseTest {

    @Autowired
    private ReminderServiceImpl reminderService;

    @Autowired
    private ReminderMapper reminderMapper;

    private Reminder sampleReminder;

    @BeforeEach
    void setUp() {
        System.out.println("=== 初始化测试数据 ===");
        
        // 清理可能存在的测试数据
        reminderMapper.delete(null);
        
        // 准备测试数据
        sampleReminder = new Reminder();
        sampleReminder.setUserId(1);
        sampleReminder.setPetId(1);
        sampleReminder.setTitle("测试提醒");
        sampleReminder.setReminderType("喂食");
        sampleReminder.setStatus("待完成");  // 使用中文枚举值
        sampleReminder.setDueDate(LocalDateTime.now().plusDays(1));
        sampleReminder.setNotes("这是测试提醒");
        sampleReminder.setDeleted((byte) 0);
        
        // 插入测试数据
        reminderMapper.insert(sampleReminder);
        System.out.println("✅ 测试数据初始化完成");
    }

    @Test
    void testCreateReminder_Success() {
        System.out.println("\n=== 测试创建提醒功能 ===");
        
        // 准备测试数据
        ReminderCreateDTO createDTO = new ReminderCreateDTO();
        createDTO.setPetId(1);
        createDTO.setTitle("每日喂食");
        createDTO.setReminderType("喂食");
        createDTO.setDueDate(LocalDateTime.now().plusHours(2));
        createDTO.setNotes("按时喂食优质狗粮");

        // 执行测试
        ReminderResponseDTO result = reminderService.createReminder(createDTO, 1);

        // 验证结果
        assertNotNull(result, "创建的提醒不应该为null");
        assertEquals("喂食", result.getReminderType(), "提醒类型应该正确");
        assertEquals("每日喂食", result.getTitle(), "提醒标题应该正确");
        assertEquals(1, result.getUserId(), "用户ID应该正确");
        assertEquals("待完成", result.getStatus(), "默认状态应该是待完成");
        assertNotNull(result.getReminderId(), "提醒ID应该被生成");
        
        System.out.println("✅ 创建提醒测试通过");
        System.out.println("   创建的提醒ID: " + result.getReminderId());
        System.out.println("   提醒标题: " + result.getTitle());
    }

    @Test
    void testGetReminderById_Success() {
        System.out.println("\n=== 测试获取提醒详情功能 ===");
        
        // 执行测试
        ReminderResponseDTO result = reminderService.getReminderById(sampleReminder.getReminderId());

        // 验证结果
        assertNotNull(result, "获取的提醒不应该为null");
        assertEquals(sampleReminder.getReminderId(), result.getReminderId(), "提醒ID应该正确");
        assertEquals("测试提醒", result.getTitle(), "提醒标题应该正确");
        assertEquals("喂食", result.getReminderType(), "提醒类型应该正确");
        assertNotNull(result.getCreateTime(), "创建时间不应该为null");
        
        System.out.println("✅ 获取提醒详情测试通过");
        System.out.println("   获取到的提醒: " + result.getTitle());
    }

    @Test
    void testGetReminderById_NotFound() {
        System.out.println("\n=== 测试获取不存在提醒的异常处理 ===");
        
        // 执行测试并验证异常
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> reminderService.getReminderById(999999),
            "当提醒不存在时应该抛出ResourceNotFoundException"
        );
        
        assertEquals("提醒不存在", exception.getMessage(), "异常消息应该正确");
        System.out.println("✅ 异常处理测试通过");
        System.out.println("   正确捕获了异常: " + exception.getMessage());
    }

    @Test
    void testUpdateReminder_Success() {
        System.out.println("\n=== 测试更新提醒功能 ===");
        
        // 准备更新数据
        ReminderUpdateDTO updateDTO = new ReminderUpdateDTO();
        updateDTO.setTitle("更新后的标题");
        updateDTO.setNotes("更新后的备注");
        updateDTO.setStatus("completed");

        // 执行测试
        ReminderResponseDTO result = reminderService.updateReminder(sampleReminder.getReminderId(), updateDTO);

        // 验证结果
        assertNotNull(result, "更新后的提醒不应该为null");
        assertEquals("更新后的标题", result.getTitle(), "提醒标题应该更新成功");
        assertEquals("completed", result.getStatus(), "提醒状态应该更新为completed");
        assertEquals("更新后的备注", result.getNotes(), "备注应该更新成功");
        
        System.out.println("✅ 更新提醒测试通过");
        System.out.println("   更新后的标题: " + result.getTitle());
        System.out.println("   更新后的状态: " + result.getStatus());
    }

    @Test
    void testDeleteReminder_Success() {
        System.out.println("\n=== 测试删除提醒功能 ===");
        
        // 执行测试
        assertDoesNotThrow(() -> reminderService.deleteReminder(sampleReminder.getReminderId()), 
            "删除提醒不应该抛出异常");

        // 验证删除结果
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> reminderService.getReminderById(sampleReminder.getReminderId()),
            "删除后的提醒应该无法获取"
        );
        
        System.out.println("✅ 删除提醒测试通过");
        System.out.println("   成功删除提醒，ID: " + sampleReminder.getReminderId());
    }

    @Test
    void testCompleteReminder_Success() {
        System.out.println("\n=== 测试完成提醒功能 ===");
        
        // 执行测试
        ReminderResponseDTO result = reminderService.completeReminder(sampleReminder.getReminderId());

        // 验证结果
        assertNotNull(result, "完成后的提醒不应该为null");
        assertEquals("已完成", result.getStatus(), "提醒状态应该更新为已完成");
        assertNotNull(result.getCompletedTime(), "完成时间不应该为null");
        
        System.out.println("✅ 完成提醒测试通过");
        System.out.println("   提醒状态: " + result.getStatus());
        System.out.println("   完成时间: " + result.getCompletedTime());
    }

    @Test
    void testDataIsolation() {
        System.out.println("\n=== 测试数据隔离性 ===");
        
        // 查询所有提醒
        List<Reminder> allReminders = reminderMapper.selectList(null);
        
        // 验证只有一条测试数据（因为我们用了@Transactional）
        assertEquals(1, allReminders.size(), "应该只有1条测试数据");
        assertEquals(sampleReminder.getReminderId(), allReminders.get(0).getReminderId(), "ID应该匹配");
        
        System.out.println("✅ 数据隔离测试通过");
        System.out.println("   当前数据库中的提醒数量: " + allReminders.size());
    }
}