package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

/**
 * 更新体检记录请求DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "更新体检记录请求")
public class CheckupRecordUpdateDTO {

    @PastOrPresent(message = "体检日期不能晚于今天")
    @Schema(description = "体检日期", example = "2024-11-10")
    private LocalDate checkupDate;

    @NotBlank(message = "体检机构不能为空")
    @Length(max = 100, message = "体检机构名称长度不能超过100个字符")
    @Schema(description = "体检机构", example = "爱心宠物医院")
    private String institution;

    @Schema(description = "体检结果摘要", example = "身体健康，各项指标正常")
    @Length(max = 1000, message = "体检结果摘要长度不能超过1000个字符")
    private String resultSummary;

    @Schema(description = "报告图片URL", example = "https://example.com/checkup_report.jpg")
    @Length(max = 200, message = "图片URL长度不能超过200个字符")
    private String reportImageUrl;

    @Schema(description = "备注", example = "建议保持当前饮食和运动量")
    @Length(max = 500, message = "备注长度不能超过500个字符")
    private String notes;
}
