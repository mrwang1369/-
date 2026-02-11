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
 * 存储用户基本信息，支持微信和手机号登录
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Getter
@Setter
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    /**
     * 微信唯一标识
     */
    private String openid;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 加密密码（手机号注册时使用）
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 逻辑删除标志(0-未删除,1-已删除)
     */
    @TableLogic
    private Byte deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
