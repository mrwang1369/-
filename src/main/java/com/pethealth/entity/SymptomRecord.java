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
 * 记录症状输入和AI分析，用于就医引�? * </p>
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
     * 关联宠物ID
     */
    private Integer petId;

    /**
     * 关联用户ID
     */
    private Integer userId;

    /**
     * 症状关键词输�?     */
    private String symptomsText;

    /**
     * AI分析结果（可能病因）
     */
    private String analysisResult;

    /**
     * 紧急程�?     */
    private String emergencyLevel;

    /**
     * 处理建议
     */
    private String suggestions;

    /**
     * 逻辑删除标志(0-未删�?1-已删�?
     */
    @TableLogic
    private Byte deleted;

    private LocalDateTime createTime;
}
