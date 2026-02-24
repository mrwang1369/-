package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

/**
 * 创建疫苗记录请求DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "创建疫苗记录请求")
public class VaccinationRecordCreateDTO {

    @NotNull(message = "宠物ID不能为空")
    @Schema(description = "宠物ID", example = "1")
    private Integer petId;

    @NotBlank(message = "疫苗名称不能为空")
    @Length(max = 50, message = "疫苗名称长度不能超过50个字符")
    @Schema(description = "疫苗名称", example = "狂犬疫苗")
    private String vaccineName;

    @NotNull(message = "接种日期不能为空")
    @FutureOrPresent(message = "接种日期不能晚于今天")
    @Schema(description = "接种日期", example = "2024-12-01")
    private LocalDate vaccinationDate;

    @NotNull(message = "下次接种日期不能为空")
    @Future(message = "下次接种日期必须是未来日期")
    @Schema(description = "下次接种日期", example = "2025-12-01")
    private LocalDate nextDueDate;

    @Schema(description = "兽医信息", example = "张医生-爱心宠物医院")
    @Length(max = 100, message = "兽医信息长度不能超过100个字符")
    private String vetInfo;

    @Schema(description = "接种证明图片URL", example = "https://example.com/vaccine_proof.jpg")
    @Length(max = 200, message = "图片URL长度不能超过200个字符")
    private String proofImageUrl;

    @Schema(description = "备注", example = "按时复查")
    @Length(max = 500, message = "备注长度不能超过500个字符")
    private String notes;
}
