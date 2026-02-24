package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 疫苗记录响应DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "疫苗记录响应")
public class VaccinationRecordResponseDTO {

    @Schema(description = "疫苗记录ID", example = "1")
    private Integer vaccinationId;

    @Schema(description = "宠物ID", example = "1")
    private Integer petId;

    @Schema(description = "疫苗名称", example = "狂犬疫苗")
    private String vaccineName;

    @Schema(description = "接种日期", example = "2024-12-01")
    private LocalDate vaccinationDate;

    @Schema(description = "下次接种日期", example = "2025-12-01")
    private LocalDate nextDueDate;

    @Schema(description = "兽医信息", example = "张医生-爱心宠物医院")
    private String vetInfo;

    @Schema(description = "接种证明图片URL", example = "https://example.com/vaccine_proof.jpg")
    private String proofImageUrl;

    @Schema(description = "备注", example = "按时复查")
    private String notes;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "距离下次接种天数", example = "365")
    private Integer daysUntilNext;

    @Schema(description = "是否即将到期", example = "false")
    private Boolean isExpiringSoon;
}
