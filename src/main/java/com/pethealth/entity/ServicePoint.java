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
 * 存储周边服务信息，支持地图集成
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Getter
@Setter
@TableName("service_point")
public class ServicePoint implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "point_id", type = IdType.AUTO)
    private Integer pointId;

    /**
     * 服务点名称
     */
    private String name;

    /**
     * 服务类型
     */
    private String type;

    /**
     * 地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 纬度（用于LBS）
     */
    private BigDecimal latitude;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 用户评分
     */
    private BigDecimal rating;

    /**
     * 营业时间
     */
    private String businessHours;

    /**
     * 逻辑删除标志(0-未删除,1-已删除)
     */
    @TableLogic
    private Byte deleted;

    private LocalDateTime createTime;
}
