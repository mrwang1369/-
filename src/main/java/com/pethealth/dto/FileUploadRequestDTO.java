package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 文件上传请求DTO
 *
 * @author pethealth
 * @since 2026-02-27
 */
@Data
@Schema(description = "文件上传请求DTO")
public class FileUploadRequestDTO {

    /**
     * 模块类型
     */
    @NotBlank(message = "模块类型不能为空")
    @Schema(description = "模块类型", example = "pet_avatar")
    private String moduleType;

    /**
     * 关联业务ID
     */
    @Schema(description = "关联业务ID", example = "1")
    private Long businessId;

    /**
     * 文件描述
     */
    @Schema(description = "文件描述", example = "宠物头像照片")
    private String description;
}