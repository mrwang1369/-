package com.pethealth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息实体类
 * 对应数据库表：file_info
 *
 * @author pethealth
 * @since 2026-02-27
 */
@Data
@TableName("file_info")
public class FileInfo {

    /**
     * 文件ID
     */
    @TableId(value = "file_id", type = IdType.AUTO)
    private Long fileId;

    /**
     * 原始文件名
     */
    @TableField("original_name")
    private String originalName;

    /**
     * 存储文件名(UUID)
     */
    @TableField("stored_name")
    private String storedName;

    /**
     * 文件存储路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件大小(字节)
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 文件类型(MIME类型)
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 文件扩展名
     */
    @TableField("file_extension")
    private String fileExtension;

    /**
     * 模块类型
     * pet_avatar: 宠物头像
     * medical_record: 病历记录
     * vaccination_record: 疫苗记录
     * checkup_record: 体检记录
     * deworming_record: 驱虫记录
     * general: 通用文件
     */
    @TableField("module_type")
    private String moduleType;

    /**
     * 关联业务ID
     */
    @TableField("business_id")
    private Long businessId;

    /**
     * 上传用户ID
     */
    @TableField("uploader_id")
    private Integer uploaderId;

    /**
     * 上传时间
     */
    @TableField("upload_time")
    private LocalDateTime uploadTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 逻辑删除标志 (0-未删除 1-已删除)
     */
    @TableLogic(value = "0", delval = "1")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;
}