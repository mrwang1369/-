package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询提醒列表请求DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "查询提醒列表请求")
public class ReminderQueryDTO {

    @Schema(description = "宠物ID（可为空，查询所有提醒）", example = "1")
    private Integer petId;

    @Schema(description = "提醒类型", example = "喂食")
    private String reminderType;

    @Schema(description = "状态", example = "pending", allowableValues = {"pending", "completed", "cancelled", "all"})
    private String status = "all";

    @Schema(description = "是否只查询今日提醒", example = "false")
    private Boolean todayOnly = false;

    @Schema(description = "是否只查询逾期提醒", example = "false")
    private Boolean overdueOnly = false;
}