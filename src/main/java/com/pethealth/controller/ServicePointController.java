package com.pethealth.controller;

import com.pethealth.common.Result;
import com.pethealth.dto.AmapResponseDTO;
import com.pethealth.entity.ServicePoint;
import com.pethealth.service.MapService;
import com.pethealth.service.ServicePointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 存储周边服务信息，支持地图集�?前端控制�? * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Tag(name = "周边服务", description = "周边服务点管理和地图服务接口")
@RestController
@RequestMapping("/service-points")
public class ServicePointController {
    
    private static final Logger log = LoggerFactory.getLogger(ServicePointController.class);
    
    @Autowired
    private ServicePointService servicePointService;
    
    @Autowired
    private MapService mapService;
    
    /**
     * 获取附近服务�?     */
    @Operation(summary = "获取附近服务�?, description = "根据当前位置获取附近的宠物医院、宠物店等服务点")
    @GetMapping("/nearby")
    public Result<List<ServicePoint>> getNearbyServicePoints(
            @Parameter(description = "经度") @RequestParam BigDecimal longitude,
            @Parameter(description = "纬度") @RequestParam BigDecimal latitude,
            @Parameter(description = "搜索半径(�?，默�?000�?) @RequestParam(defaultValue = "3000") Integer radius,
            @Parameter(description = "服务点类型，如：医院,宠物�?) @RequestParam(required = false) String type) {
        
        log.info("获取附近服务�? longitude={}, latitude={}, radius={}, type={}", 
                longitude, latitude, radius, type);
        
        // 先从数据库查询已有的服务�?        List<ServicePoint> servicePoints = servicePointService.getNearbyServicePoints(
                longitude, latitude, radius, type);
        
        return Result.success(servicePoints);
    }
    
    /**
     * 搜索服务�?     */
    @Operation(summary = "搜索服务�?, description = "通过关键词搜索服务点")
    @GetMapping("/search")
    public Result<AmapResponseDTO> searchServicePoints(
            @Parameter(description = "搜索关键�?) @RequestParam String keywords,
            @Parameter(description = "城市名称") @RequestParam(required = false) String city,
            @Parameter(description = "服务点类�?) @RequestParam(required = false) String types) {
        
        log.info("搜索服务�? keywords={}, city={}, types={}", keywords, city, types);
        
        AmapResponseDTO response = mapService.searchText(keywords, city, types);
        return Result.success(response);
    }
    
    /**
     * 地理编码（地址转坐标）
     */
    @Operation(summary = "地理编码", description = "将地址转换为经纬度坐标")
    @PostMapping("/geocode")
    public Result<AmapResponseDTO> geocode(
            @Parameter(description = "地址") @RequestParam String address) {
        
        log.info("地理编码: address={}", address);
        
        AmapResponseDTO response = mapService.geocode(address);
        return Result.success(response);
    }
    
    /**
     * 逆地理编码（坐标转地址�?     */
    @Operation(summary = "逆地理编�?, description = "将经纬度坐标转换为地址")
    @PostMapping("/regeocode")
    public Result<AmapResponseDTO> regeocode(
            @Parameter(description = "经度") @RequestParam BigDecimal longitude,
            @Parameter(description = "纬度") @RequestParam BigDecimal latitude) {
        
        log.info("逆地理编�? longitude={}, latitude={}", longitude, latitude);
        
        AmapResponseDTO response = mapService.regeocode(longitude, latitude);
        return Result.success(response);
    }
    
    /**
     * 计算两点间距�?     */
    @Operation(summary = "计算距离", description = "计算两个坐标点之间的直线距离")
    @PostMapping("/distance")
    public Result<Double> calculateDistance(
            @Parameter(description = "起点经度") @RequestParam BigDecimal startLon,
            @Parameter(description = "起点纬度") @RequestParam BigDecimal startLat,
            @Parameter(description = "终点经度") @RequestParam BigDecimal endLon,
            @Parameter(description = "终点纬度") @RequestParam BigDecimal endLat) {
        
        log.info("计算距离: start({},{}) -> end({},{})", startLon, startLat, endLon, endLat);
        
        double distance = mapService.calculateDistance(startLon, startLat, endLon, endLat);
        return Result.success(distance);
    }
}
