package com.pethealth.controller;

import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.common.Result;
import com.pethealth.dto.*;
import com.pethealth.service.ReminderService;
import com.pethealth.task.ReminderTask;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 提醒管理控制器
 * 提供宠物健康提醒的完整CRUD操作和查询功能
 * 支持定时任务自动提醒和微信消息推送
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 * @version 1.0
 */
@RestController
@RequestMapping("/reminder")
@Slf4j
@Tag(name = "提醒管理", description = "宠物健康提醒相关接口")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private ReminderTask reminderTask;

    /**
     * 创建新的提醒事项
     * 支持为指定宠物或全局创建提醒
     * 
     * @param createDTO 创建请求DTO，包含提醒基本信息
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 创建成功的提醒响应DTO
     * @throws ResourceNotFoundException 当指定的宠物不存在时抛出
     * @throws BusinessException 当创建提醒失败时抛出
     */
    @PostMapping
    @Operation(summary = "创建提醒", description = "为指定宠物或全局创建新的提醒事项")
    public Result<ReminderResponseDTO> createReminder(
            @Valid @RequestBody ReminderCreateDTO createDTO,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("创建提醒: userId={}, title={}", userId, createDTO.getTitle());
        
        ReminderResponseDTO response = reminderService.createReminder(createDTO, userId.intValue());
        return Result.success("创建成功", response);
    }

    @PutMapping("/{reminderId}")
    @Operation(summary = "更新提醒", description = "更新指定提醒的信息")
    public Result<ReminderResponseDTO> updateReminder(
            @Parameter(description = "提醒ID", example = "1") 
            @PathVariable Integer reminderId,
            @Valid @RequestBody ReminderUpdateDTO updateDTO,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("更新提醒: userId={}, reminderId={}", userId, reminderId);
        
        ReminderResponseDTO response = reminderService.updateReminder(reminderId, updateDTO);
        return Result.success("更新成功", response);
    }

    @DeleteMapping("/{reminderId}")
    @Operation(summary = "删除提醒", description = "删除指定的提醒（逻辑删除）")
    public Result<Void> deleteReminder(
            @Parameter(description = "提醒ID", example = "1") 
            @PathVariable Integer reminderId,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("删除提醒: userId={}, reminderId={}", userId, reminderId);
        
        reminderService.deleteReminder(reminderId);
        return Result.<Void>success("删除成功", null);
    }

    @PatchMapping("/{reminderId}/complete")
    @Operation(summary = "完成提醒", description = "标记提醒为已完成状态")
    public Result<ReminderResponseDTO> completeReminder(
            @Parameter(description = "提醒ID", example = "1") 
            @PathVariable Integer reminderId,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("完成提醒: userId={}, reminderId={}", userId, reminderId);
        
        ReminderResponseDTO response = reminderService.completeReminder(reminderId);
        
        // 发送完成确认微信消息
        reminderTask.sendCompletionMessage(userId.intValue(), response);
        
        return Result.success("标记完成成功", response);
    }

    @GetMapping("/{reminderId}")
    @Operation(summary = "获取提醒详情", description = "根据ID获取提醒的详细信息")
    public Result<ReminderResponseDTO> getReminderById(
            @Parameter(description = "提醒ID", example = "1") 
            @PathVariable Integer reminderId,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("获取提醒详情: userId={}, reminderId={}", userId, reminderId);
        
        ReminderResponseDTO response = reminderService.getReminderById(reminderId);
        return Result.success("获取成功", response);
    }

    @GetMapping
    @Operation(summary = "获取提醒列表", description = "根据条件获取用户的提醒列表（分页）")
    public Result<PageResult<ReminderResponseDTO>> getReminders(
            @Valid ReminderQueryDTO queryDTO,
            PageRequest pageRequest,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("获取提醒列表: userId={}, petId={}, status={}", userId, queryDTO.getPetId(), queryDTO.getStatus());
        
        PageResult<ReminderResponseDTO> result = reminderService.getRemindersByUserId(userId.intValue(), queryDTO, pageRequest);
        return Result.success("获取成功", result);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "获取即将到期提醒", description = "获取用户在未来指定小时内即将到期的提醒")
    public Result<List<ReminderResponseDTO>> getUpcomingReminders(
            @Parameter(description = "时间范围（小时）", example = "24") 
            @RequestParam(defaultValue = "24") Integer hours,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("获取即将到期提醒: userId={}, hours={}", userId, hours);
        
        List<ReminderResponseDTO> reminders = reminderService.getUpcomingReminders(userId.intValue(), hours);
        return Result.success("获取成功", reminders);
    }

    @GetMapping("/overdue")
    @Operation(summary = "获取逾期提醒", description = "获取用户逾期未完成的提醒")
    public Result<List<ReminderResponseDTO>> getOverdueReminders(HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("获取逾期提醒: userId={}", userId);
        
        List<ReminderResponseDTO> reminders = reminderService.getOverdueReminders(userId.intValue());
        return Result.success("获取成功", reminders);
    }

    @GetMapping("/today")
    @Operation(summary = "获取今日提醒", description = "获取用户今天的提醒")
    public Result<List<ReminderResponseDTO>> getTodayReminders(HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("获取今日提醒: userId={}", userId);
        
        List<ReminderResponseDTO> reminders = reminderService.getTodayReminders(userId.intValue());
        return Result.success("获取成功", reminders);
    }

    @PostMapping("/generate-health-reminders")
    @Operation(summary = "生成健康提醒", description = "根据宠物健康记录自动生成相关提醒")
    public Result<Void> generateHealthReminders(HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("生成健康提醒: userId={}", userId);
        
        reminderService.generateHealthReminders(userId.intValue());
        return Result.<Void>success("健康提醒生成成功", null);
    }
}
