package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 病历记录响应DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "病历记录响应")
public class MedicalRecordResponseDTO {

    @Schema(description = "病历记录ID", example = "1")
    private Integer medicalId;

    @Schema(description = "宠物ID", example = "1")
    private Integer petId;

    @Schema(description = "就诊医院", example = "爱心宠物医院")
    private String hospital;

    @Schema(description = "诊断结果", example = "轻微皮肤病")
    private String diagnosis;

    @Schema(description = "用药清单", example = "消炎药膏，每日涂抹两次")
    private String medicationList;

    @Schema(description = "就诊日期", example = "2024-09-05")
    private LocalDate treatmentDate;

    @Schema(description = "备注", example = "按时复查")
    private String notes;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "就诊距今天数", example = "45")
    private Integer daysAgo;

    @Schema(description = "是否为近期就诊", example = "false")
    private Boolean isRecent;
}
