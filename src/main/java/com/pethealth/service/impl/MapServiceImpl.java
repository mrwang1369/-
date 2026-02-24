package com.pethealth.service.impl;

import com.alibaba.fastjson2.JSON;
import com.pethealth.config.AmapConfig;
import com.pethealth.dto.AmapResponseDTO;
import com.pethealth.handler.BusinessException;
import com.pethealth.service.MapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 地图服务实现类
 *
 * @author Mr wang
 * @since 2026-02-13
 */
@Service
@Slf4j
public class MapServiceImpl implements MapService {
    
    @Autowired
    private AmapConfig amapConfig;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public AmapResponseDTO geocode(String address) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(amapConfig.getGeocodeUrl())
                    .queryParam("key", amapConfig.getKey())
                    .queryParam("address", address);
            
            URI uri = builder.build().encode().toUri();
            log.debug("使用高德地理编码API: {}", uri.toString());
            
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                AmapResponseDTO result = JSON.parseObject(response.getBody(), AmapResponseDTO.class);
                log.debug("地理编码结果: status={}, info={}", result.getStatus(), result.getInfo());
                return result;
            } else {
                log.error("使用高德地理编码API调用失败: status={}", response.getStatusCode());
                throw new BusinessException("地理编码服务调用失败");
            }
        } catch (Exception e) {
            log.error("地理编码异常: ", e);
            throw new BusinessException("地理编码服务异常: " + e.getMessage());
        }
    }
    
    @Override
    public AmapResponseDTO regeocode(BigDecimal longitude, BigDecimal latitude) {
        try {
            String location = longitude + "," + latitude;
            
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(amapConfig.getRegeocodeUrl())
                    .queryParam("key", amapConfig.getKey())
                    .queryParam("location", location)
                    .queryParam("extensions", "all");
            
            URI uri = builder.build().encode().toUri();
            log.debug("使用高德逆地理编码API: {}", uri.toString());
            
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                AmapResponseDTO result = JSON.parseObject(response.getBody(), AmapResponseDTO.class);
                log.debug("逆地理编码结果: status={}, info={}", result.getStatus(), result.getInfo());
                return result;
            } else {
                log.error("使用高德逆地理编码API调用失败: status={}", response.getStatusCode());
                throw new BusinessException("逆地理编码服务调用失败");
            }
        } catch (Exception e) {
            log.error("逆地理编码异常: ", e);
            throw new BusinessException("逆地理编码服务异常: " + e.getMessage());
        }
    }
    
    @Override
    public AmapResponseDTO searchNearby(BigDecimal longitude, BigDecimal latitude, String keywords, 
                                      Integer radius, String types) {
        try {
            String location = longitude + "," + latitude;
            
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(amapConfig.getPoiAroundUrl())
                    .queryParam("key", amapConfig.getKey())
                    .queryParam("location", location)
                    .queryParam("keywords", keywords != null ? keywords : "")
                    .queryParam("radius", radius != null ? radius : 3000)
                    .queryParam("types", types != null ? types : "")
                    .queryParam("offset", 20)
                    .queryParam("page", 1)
                    .queryParam("extensions", "all");
            
            URI uri = builder.build().encode().toUri();
            log.debug("使用高德周边POI搜索API: {}", uri.toString());
            
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                AmapResponseDTO result = JSON.parseObject(response.getBody(), AmapResponseDTO.class);
                log.debug("周边POI搜索结果: status={}, count={}", result.getStatus(), result.getCount());
                return result;
            } else {
                log.error("使用高德周边POI搜索API调用失败: status={}", response.getStatusCode());
                throw new BusinessException("周边POI搜索服务调用失败");
            }
        } catch (Exception e) {
            log.error("周边POI搜索异常: ", e);
            throw new BusinessException("周边POI搜索服务异常: " + e.getMessage());
        }
    }
    
    @Override
    public AmapResponseDTO searchText(String keywords, String city, String types) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(amapConfig.getPoiTextUrl())
                    .queryParam("key", amapConfig.getKey())
                    .queryParam("keywords", keywords)
                    .queryParam("city", city != null ? city : "")
                    .queryParam("types", types != null ? types : "")
                    .queryParam("offset", 20)
                    .queryParam("page", 1)
                    .queryParam("extensions", "all");
            
            URI uri = builder.build().encode().toUri();
            log.debug("使用高德文本搜索API: {}", uri.toString());
            
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                AmapResponseDTO result = JSON.parseObject(response.getBody(), AmapResponseDTO.class);
                log.debug("文本搜索结果: status={}, count={}", result.getStatus(), result.getCount());
                return result;
            } else {
                log.error("使用高德文本搜索API调用失败: status={}", response.getStatusCode());
                throw new BusinessException("文本搜索服务调用失败");
            }
        } catch (Exception e) {
            log.error("文本搜索异常: ", e);
            throw new BusinessException("文本搜索服务异常: " + e.getMessage());
        }
    }
    
    @Override
    public double calculateDistance(BigDecimal lon1, BigDecimal lat1, BigDecimal lon2, BigDecimal lat2) {
        // 使用Haversine公式计算两点间直线距离
        double earthRadius = 6371000; // 地球半径（米）
        double dLat = Math.toRadians(lat2.subtract(lat1).doubleValue());
        double dLon = Math.toRadians(lon2.subtract(lon1).doubleValue());
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue())) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        double distance = earthRadius * c;
        
        // 保留2位小数
        return new BigDecimal(distance).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
