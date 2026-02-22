package com.pethealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 存储提醒事项，支持微信推�? * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Getter
@Setter
public class Reminder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "reminder_id", type = IdType.AUTO)
    private Integer reminderId;

    /**
     * 关联用户ID
     */
    private Integer userId;

    /**
     * 关联宠物ID（可为NULL表示自定义提醒）
     */
    private Integer petId;

    /**
     * 类型（如疫苗、驱虫、喂食）
     */
    private String reminderType;

    /**
     * 提醒标题
     */
    private String title;

    /**
     * 到期时间
     */
    private LocalDateTime dueDate;

    /**
     * 状�?     */
    private String status;

    /**
     * 重复周期（如每日、每周）
     */
    private String repeatCycle;

    /**
     * 备注
     */
    private String notes;

    /**
     * 逻辑删除标志(0-未删�?1-已删�?
     */
    @TableLogic
    private Byte deleted;

    private LocalDateTime createTime;

    /**
     * 完成时间
     */
    private LocalDateTime completedTime;
}
