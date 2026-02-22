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
 * 存储提醒事项，支持微信推送
 * </p>
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
     * 用户ID
     */
    private Integer userId;

    /**
     * 宠物ID，也可以为空，表示全局提醒
     */
    private Integer petId;

    /**
     * 类型，例如：喂食、散步、体检级
     */
    private String reminderType;

    /**
     * 提醒标题
     */
    private String title;

    /**
     * 截止日期
     */
    private LocalDateTime dueDate;

    /**
     * 状态
     */
    private String status;

    /**
     * 重复周期，例如：每日、每周、每月级
     */
    private String repeatCycle;

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

    /**
     * 完成时间
     */
    private LocalDateTime completedTime;
}
