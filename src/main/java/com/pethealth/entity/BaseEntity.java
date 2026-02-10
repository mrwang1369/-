package com.pethealth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 所有实体类的基类
 * 包含公共字段：id、createTime、updateTime、deleted（逻辑删除）
 */
@Data
public class BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志 (0-未删除, 1-已删除)
     */
    @TableLogic(value = "0", delval = "1")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;
}