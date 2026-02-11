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
 * 记录病历和用药信息，可设置用药提醒
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Getter
@Setter
@TableName("medical_record")
public class MedicalRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "medical_id", type = IdType.AUTO)
    private Integer medicalId;

    /**
     * 关联宠物ID
     */
    private Integer petId;

    /**
     * 就诊医院
     */
    private String hospital;

    /**
     * 诊断结果
     */
    private String diagnosis;

    /**
     * 用药清单
     */
    private String medicationList;

    /**
     * 就诊日期
     */
    private LocalDate treatmentDate;

    /**
     * 备注
     */
    private String notes;

    /**
     * 逻辑删除标志(0-未删除,1-已删除)
     */
    @TableLogic
    private Byte deleted;

    private LocalDateTime createTime;
}
