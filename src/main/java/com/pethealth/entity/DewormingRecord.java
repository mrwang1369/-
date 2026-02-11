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
 * 记录驱虫信息，支持周期提醒
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Getter
@Setter
@TableName("deworming_record")
public class DewormingRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "deworming_id", type = IdType.AUTO)
    private Integer dewormingId;

    /**
     * 关联宠物ID
     */
    private Integer petId;

    /**
     * 驱虫类型
     */
    private String dewormingType;

    /**
     * 药物名称
     */
    private String drugName;

    /**
     * 驱虫日期
     */
    private LocalDate date;

    /**
     * 下次驱虫日期
     */
    private LocalDate nextDate;

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
