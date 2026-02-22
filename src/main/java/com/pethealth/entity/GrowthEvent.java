package com.pethealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 聚合多源数据生成成长时光�? * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Getter
@Setter
@TableName("growth_event")
public class GrowthEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "event_id", type = IdType.AUTO)
    private Integer eventId;

    /**
     * 关联宠物ID
     */
    private Integer petId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件日期
     */
    private LocalDateTime eventDate;

    /**
     * 描述（如疫苗名称�?     */
    private String description;

    /**
     * 相关图片URL
     */
    private String imageUrl;

    /**
     * 体重值（仅体重记录时使用�?     */
    private BigDecimal weightValue;

    /**
     * 逻辑删除标志(0-未删�?1-已删�?
     */
    @TableLogic
    private Byte deleted;

    private LocalDateTime createTime;
}
