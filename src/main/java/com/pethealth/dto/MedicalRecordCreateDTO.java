package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

/**
 * 创建病历记录请求DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "创建病历记录请求")
public class MedicalRecordCreateDTO {

    @NotNull(message = "宠物ID不能为空")
    @Schema(description = "宠物ID", example = "1")
    private Integer petId;

    @NotBlank(message = "就诊医院不能为空")
    @Length(max = 100, message = "就诊医院名称长度不能超过100个字符")
    @Schema(description = "就诊医院", example = "爱心宠物医院")
    private String hospital;

    @NotBlank(message = "诊断结果不能为空")
    @Length(max = 1000, message = "诊断结果长度不能超过1000个字符")
    @Schema(description = "诊断结果", example = "轻微皮肤病")
    private String diagnosis;

    @Schema(description = "用药清单", example = "消炎药膏，每日涂抹两次")
    @Length(max = 500, message = "用药清单长度不能超过500个字符")
    private String medicationList;

    @NotNull(message = "就诊日期不能为空")
    @PastOrPresent(message = "就诊日期不能晚于今天")
    @Schema(description = "就诊日期", example = "2024-09-05")
    private LocalDate treatmentDate;

    @Schema(description = "备注", example = "按时复查")
    @Length(max = 500, message = "备注长度不能超过500个字符")
    private String notes;
}
