package com.pethealth.service;

import com.pethealth.dto.AmapResponseDTO;

import java.math.BigDecimal;

/**
 * 地图服务接口
 *
 * @author Mr wang
 * @since 2026-02-13
 */
public interface MapService {
    
    /**
     * 地理编码，即地址转经纬度
     *
     * @param address 地址
     * @return 高德地图响应结果
     */
    AmapResponseDTO geocode(String address);
    
    /**
     * 逆地理编码，即经纬度转地址
     *
     * @param longitude 经度
     * @param latitude 纬度
     * @return 高德地图响应结果
     */
    AmapResponseDTO regeocode(BigDecimal longitude, BigDecimal latitude);
    
    /**
     * 搜索周边POI
     *
     * @param longitude 经度
     * @param latitude 纬度
     * @param keywords 搜索关键词
     * @param radius 搜索半径（米）
     * @param types POI类型
     * @return 高德地图响应结果
     */
    AmapResponseDTO searchNearby(BigDecimal longitude, BigDecimal latitude, String keywords, 
                               Integer radius, String types);
    
    /**
     * 文本搜索POI
     *
     * @param keywords 搜索关键词
     * @param city 城市
     * @param types POI类型
     * @return 高德地图响应结果
     */
    AmapResponseDTO searchText(String keywords, String city, String types);
    
    /**
     * 计算两点之间的距离
     *
     * @param lon1 起点经度
     * @param lat1 起点纬度
     * @param lon2 终点经度
     * @param lat2 终点纬度
     * @return 距离（米）
     */
    double calculateDistance(BigDecimal lon1, BigDecimal lat1, BigDecimal lon2, BigDecimal lat2);
}
