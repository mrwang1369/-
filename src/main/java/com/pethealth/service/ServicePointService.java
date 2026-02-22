package com.pethealth.service;

import com.pethealth.entity.ServicePoint;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 存储周边服务信息，支持地图集成 服务类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
public interface ServicePointService extends IService<ServicePoint> {
    
    /**
     * 获取附近的周边服务点
     * 
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 搜索半径（米）
     * @param type 服务点类型
     * @return 附近的周边服务点集合
     */
    List<ServicePoint> getNearbyServicePoints(BigDecimal longitude, BigDecimal latitude, 
                                            Integer radius, String type);
}
