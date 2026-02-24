package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 驱虫记录响应DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "驱虫记录响应")
public class DewormingRecordResponseDTO {

    @Schema(description = "驱虫记录ID", example = "1")
    private Integer dewormingId;

    @Schema(description = "宠物ID", example = "1")
    private Integer petId;

    @Schema(description = "驱虫类型", example = "体内")
    private String dewormingType;

    @Schema(description = "药物名称", example = "拜宠清")
    private String drugName;

    @Schema(description = "驱虫日期", example = "2024-12-01")
    private LocalDate date;

    @Schema(description = "下次驱虫日期", example = "2025-01-01")
    private LocalDate nextDate;

    @Schema(description = "备注", example = "按时复查")
    private String notes;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "距离下次驱虫天数", example = "30")
    private Integer daysUntilNext;

    @Schema(description = "是否即将到期", example = "false")
    private Boolean isExpiringSoon;
}
