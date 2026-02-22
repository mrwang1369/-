package com.pethealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 存储宠物基本信息，支持多只宠物管�? * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Getter
@Setter
public class Pet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "pet_id", type = IdType.AUTO)
    private Integer petId;

    /**
     * 关联用户ID
     */
    private Integer userId;

    /**
     * 宠物姓名
     */
    private String name;

    /**
     * 宠物类型
     */
    private String species;

    /**
     * 品种（可搜索选择�?     */
    private String breed;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 性别
     */
    private String gender;

    /**
     * 体重（kg�?     */
    private BigDecimal weight;

    /**
     * 过敏�?     */
    private String allergyHistory;

    /**
     * 绝育状�?     */
    private Boolean neuteredStatus;

    /**
     * 宠物头像URL
     */
    private String avatarUrl;

    /**
     * 逻辑删除标志(0-未删�?1-已删�?
     */
    @TableLogic
    private Byte deleted;

    private LocalDateTime createTime;
}
