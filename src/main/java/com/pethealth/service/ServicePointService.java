package com.pethealth.service;

import com.pethealth.entity.ServicePoint;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 存储周边服务信息，支持地图集�?服务�? * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
public interface ServicePointService extends IService<ServicePoint> {
    
    /**
     * 获取附近的服务点
     * 
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 搜索半径（米�?     * @param type 服务点类�?     * @return 附近的服务点列表
     */
    List<ServicePoint> getNearbyServicePoints(BigDecimal longitude, BigDecimal latitude, 
                                            Integer radius, String type);
}
