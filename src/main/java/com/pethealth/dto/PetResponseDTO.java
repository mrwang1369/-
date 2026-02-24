package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 宠物信息响应DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "宠物信息响应")
public class PetResponseDTO {

    @Schema(description = "宠物ID", example = "1")
    private Integer petId;

    @Schema(description = "用户ID", example = "1")
    private Integer userId;

    @Schema(description = "宠物姓名", example = "小白")
    private String name;

    @Schema(description = "宠物种类", example = "狗")
    private String species;

    @Schema(description = "宠物品种", example = "金毛寻回犬")
    private String breed;

    @Schema(description = "出生日期", example = "2023-01-01")
    private LocalDate birthDate;

    @Schema(description = "性别", example = "公")
    private String gender;

    @Schema(description = "体重(kg)", example = "25.5")
    private BigDecimal weight;

    @Schema(description = "过敏史", example = "无")
    private String allergyHistory;

    @Schema(description = "绝育状态", example = "true")
    private Boolean neuteredStatus;

    @Schema(description = "宠物头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updateTime;

    @Schema(description = "宠物年龄(计算得出)", example = "1岁")
    private String age;
}
