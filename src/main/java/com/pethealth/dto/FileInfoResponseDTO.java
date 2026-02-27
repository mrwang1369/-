package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息查询响应DTO
 *
 * @author pethealth
 * @since 2026-02-27
 */
@Data
@Schema(description = "文件信息查询响应DTO")
public class FileInfoResponseDTO {

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
     * 文件大小格式化显示
     */
    @Schema(description = "文件大小格式化显示")
    private String fileSizeDisplay;

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
     * 模块类型显示名称
     */
    @Schema(description = "模块类型显示名称")
    private String moduleTypeDisplay;

    /**
     * 关联业务ID
     */
    @Schema(description = "关联业务ID")
    private Long businessId;

    /**
     * 上传用户ID
     */
    @Schema(description = "上传用户ID")
    private Integer uploaderId;

    /**
     * 上传用户名
     */
    @Schema(description = "上传用户名")
    private String uploaderName;

    /**
     * 上传时间
     */
    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;
}