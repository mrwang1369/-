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
 * 记录疫苗接种信息，用于健康计划和提醒
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Getter
@Setter
@TableName("vaccination_record")
public class VaccinationRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "vaccination_id", type = IdType.AUTO)
    private Integer vaccinationId;

    /**
     * 宠物ID
     */
    private Integer petId;

    /**
     * 疫苗名称
     */
    private String vaccineName;

    /**
     * 接种日期
     */
    private LocalDate vaccinationDate;

    /**
     * 下次接种日期
     */
    private LocalDate nextDueDate;

    /**
     * 医生信息
     */
    private String vetInfo;

    /**
     * 接种证明图片URL
     */
    private String proofImageUrl;

    /**
     * 备注
     */
    private String notes;

    /**
     * 逻辑删除标志(0-未删除 1-已删除)
     */
    @TableLogic
    private Byte deleted;

    private LocalDateTime createTime;
}
