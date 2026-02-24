package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 提醒响应DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "提醒响应")
public class ReminderResponseDTO {

    @Schema(description = "提醒ID", example = "1")
    private Integer reminderId;

    @Schema(description = "用户ID", example = "1")
    private Integer userId;

    @Schema(description = "宠物ID", example = "1")
    private Integer petId;

    @Schema(description = "宠物名称", example = "小白")
    private String petName;

    @Schema(description = "提醒类型", example = "喂食")
    private String reminderType;

    @Schema(description = "提醒标题", example = "每日喂食")
    private String title;

    @Schema(description = "截止日期", example = "2024-12-01T08:00:00")
    private LocalDateTime dueDate;

    @Schema(description = "状态", example = "pending")
    private String status;

    @Schema(description = "重复周期", example = "每日")
    private String repeatCycle;

    @Schema(description = "备注", example = "按时喂食")
    private String notes;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "完成时间", example = "2024-12-01T08:00:00")
    private LocalDateTime completedTime;

    @Schema(description = "是否逾期", example = "false")
    private Boolean isOverdue;

    @Schema(description = "距离截止时间天数", example = "5")
    private Integer daysUntilDue;
}