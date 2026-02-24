package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

/**
 * 更新驱虫记录请求DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "更新驱虫记录请求")
public class DewormingRecordUpdateDTO {

    @NotBlank(message = "驱虫类型不能为空")
    @Schema(description = "驱虫类型", example = "体内", allowableValues = {"体内", "体外"})
    private String dewormingType;

    @NotBlank(message = "药物名称不能为空")
    @Length(max = 50, message = "药物名称长度不能超过50个字符")
    @Schema(description = "药物名称", example = "拜宠清")
    private String drugName;

    @FutureOrPresent(message = "驱虫日期不能晚于今天")
    @Schema(description = "驱虫日期", example = "2024-12-01")
    private LocalDate date;

    @Future(message = "下次驱虫日期必须是未来日期")
    @Schema(description = "下次驱虫日期", example = "2025-01-01")
    private LocalDate nextDate;

    @Schema(description = "备注", example = "按时复查")
    @Length(max = 500, message = "备注长度不能超过500个字符")
    private String notes;
}
