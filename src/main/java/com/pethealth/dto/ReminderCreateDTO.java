package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

/**
 * 创建提醒请求DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "创建提醒请求")
public class ReminderCreateDTO {

    @Schema(description = "宠物ID（可为空，表示全局提醒）", example = "1")
    private Integer petId;

    @NotBlank(message = "提醒类型不能为空")
    @Schema(description = "提醒类型", example = "喂食", allowableValues = {"喂食", "散步", "体检", "疫苗", "驱虫", "用药", "其他"})
    private String reminderType;

    @NotBlank(message = "提醒标题不能为空")
    @Length(max = 100, message = "提醒标题长度不能超过100个字符")
    @Schema(description = "提醒标题", example = "每日喂食")
    private String title;

    @NotNull(message = "截止日期不能为空")
    @Schema(description = "截止日期", example = "2024-12-01T08:00:00")
    private LocalDateTime dueDate;

    @Schema(description = "重复周期", example = "每日", allowableValues = {"不重复", "每日", "每周", "每月"})
    private String repeatCycle;

    @Schema(description = "备注", example = "按时喂食")
    @Length(max = 500, message = "备注长度不能超过500个字符")
    private String notes;
}