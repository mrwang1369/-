package com.pethealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 记录体检信息，支持图片上传
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Getter
@Setter
@TableName("checkup_record")
public class CheckupRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "checkup_id", type = IdType.AUTO)
    private Integer checkupId;

    /**
     * 关联宠物ID
     */
    private Integer petId;

    /**
     * 体检日期
     */
    private LocalDate checkupDate;

    /**
     * 体检机构
     */
    private String institution;

    /**
     * 结果摘要
     */
    private String resultSummary;

    /**
     * 报告照片或PDF URL
     */
    private String reportImageUrl;

    /**
     * 逻辑删除标志(0-未删除,1-已删除)
     */
    @TableLogic
    private Byte deleted;

    private LocalDateTime createTime;
}
