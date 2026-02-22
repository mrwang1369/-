package com.pethealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 记录症状输入和AI分析，用于就医引导
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Getter
@Setter
@TableName("symptom_record")
public class SymptomRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "symptom_id", type = IdType.AUTO)
    private Integer symptomId;

    /**
     * 宠物ID
     */
    private Integer petId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 症状关键词输入
     */
    private String symptomsText;

    /**
     * AI分析结果，如果是多级
     */
    private String analysisResult;

    /**
     * 紧急程度
     */
    private String emergencyLevel;

    /**
     * 建议
     */
    private String suggestions;

    /**
     * 逻辑删除标志(0-未删除 1-已删除)
     */
    @TableLogic
    private Byte deleted;

    private LocalDateTime createTime;
}
