package com.pethealth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethealth.entity.ServicePoint;
import com.pethealth.mapper.ServicePointMapper;
import com.pethealth.service.MapService;
import com.pethealth.service.ServicePointService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 存储周边服务信息，支持地图集�?服务实现�? * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Service
public class ServicePointServiceImpl extends ServiceImpl<ServicePointMapper, ServicePoint> implements ServicePointService {
    
    private static final Logger log = LoggerFactory.getLogger(ServicePointServiceImpl.class);
    
    @Autowired
    private MapService mapService;
    
    @Override
    public List<ServicePoint> getNearbyServicePoints(BigDecimal longitude, BigDecimal latitude, 
                                                   Integer radius, String type) {
        log.debug("查询附近服务�? longitude={}, latitude={}, radius={}, type={}", 
                longitude, latitude, radius, type);
        
        // 构建查询条件
        QueryWrapper<ServicePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0); // 只查询未删除的记�?        
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq("type", type);
        }
        
        // 查询所有符合条件的服务�?        List<ServicePoint> allServicePoints = this.list(queryWrapper);
        
        // 根据距离过滤
        List<ServicePoint> nearbyServicePoints = new ArrayList<>();
        for (ServicePoint servicePoint : allServicePoints) {
            double distance = mapService.calculateDistance(
                    longitude, latitude, 
                    servicePoint.getLongitude(), servicePoint.getLatitude());
            
            if (distance <= radius) {
                servicePoint.setRating(servicePoint.getRating() != null ? 
                    servicePoint.getRating() : new BigDecimal("0.00"));
                nearbyServicePoints.add(servicePoint);
            }
        }
        
        log.debug("找到{}个附近服务点", nearbyServicePoints.size());
        return nearbyServicePoints;
    }
}
