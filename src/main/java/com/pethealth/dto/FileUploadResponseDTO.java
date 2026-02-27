package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件上传响应DTO
 *
 * @author pethealth
 * @since 2026-02-27
 */
@Data
@Schema(description = "文件上传响应DTO")
public class FileUploadResponseDTO {

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private Long fileId;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名")
    private String originalName;

    /**
     * 文件访问URL
     */
    @Schema(description = "文件访问URL")
    private String fileUrl;

    /**
     * 文件大小(字节)
     */
    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型")
    private String fileType;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名")
    private String fileExtension;

    /**
     * 模块类型
     */
    @Schema(description = "模块类型")
    private String moduleType;

    /**
     * 关联业务ID
     */
    @Schema(description = "关联业务ID")
    private Long businessId;

    /**
     * 上传时间
     */
    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;
}