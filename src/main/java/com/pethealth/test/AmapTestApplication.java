package com.pethealth.test;

import com.pethealth.BackendApplication;
import com.pethealth.config.AmapConfig;
import com.pethealth.dto.AmapResponseDTO;
import com.pethealth.service.MapService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;

/**
 * 高德地图服务测试�? * 用于验证地图服务功能是否正常工作
 *
 * @author Mr wang
 * @since 2026-02-13
 */
@SpringBootApplication
public class AmapTestApplication {
    
    public static void main(String[] args) {
        // 启动Spring Boot应用
        ConfigurableApplicationContext context = SpringApplication.run(BackendApplication.class, args);
        
        try {
            // 获取地图服务bean
            MapService mapService = context.getBean(MapService.class);
            AmapConfig amapConfig = context.getBean(AmapConfig.class);
            
            System.out.println("=== 高德地图服务测试 ===");
            System.out.println("API Key: " + amapConfig.getKey());
            System.out.println("Base URL: " + amapConfig.getBaseUrl());
            
            // 测试地理编码功能
            System.out.println("\n--- 测试地理编码 ---");
            try {
                AmapResponseDTO geoResult = mapService.geocode("北京市朝阳区建国�?00�?);
                System.out.println("地理编码结果状�? " + geoResult.getStatus());
                System.out.println("地理编码结果信息: " + geoResult.getInfo());
                if (geoResult.getGeocodes() != null && !geoResult.getGeocodes().isEmpty()) {
                    System.out.println("地址: " + geoResult.getGeocodes().get(0).getFormatted_address());
                    System.out.println("坐标: " + geoResult.getGeocodes().get(0).getLocation());
                }
            } catch (Exception e) {
                System.out.println("地理编码测试失败: " + e.getMessage());
            }
            
            // 测试逆地理编码功�?            System.out.println("\n--- 测试逆地理编�?---");
            try {
                AmapResponseDTO regeoResult = mapService.regeocode(
                    new BigDecimal("116.4074"), new BigDecimal("39.9042"));
                System.out.println("逆地理编码结果状�? " + regeoResult.getStatus());
                System.out.println("逆地理编码结果信�? " + regeoResult.getInfo());
                if (regeoResult.getRegeocode() != null) {
                    System.out.println("地址: " + regeoResult.getRegeocode().getFormatted_address());
                }
            } catch (Exception e) {
                System.out.println("逆地理编码测试失�? " + e.getMessage());
            }
            
            // 测试周边搜索功能
            System.out.println("\n--- 测试周边搜索 ---");
            try {
                AmapResponseDTO nearbyResult = mapService.searchNearby(
                    new BigDecimal("116.4074"), new BigDecimal("39.9042"), 
                    "宠物医院", 3000, "");
                System.out.println("周边搜索结果状�? " + nearbyResult.getStatus());
                System.out.println("周边搜索结果信息: " + nearbyResult.getInfo());
                System.out.println("找到POI数量: " + nearbyResult.getCount());
            } catch (Exception e) {
                System.out.println("周边搜索测试失败: " + e.getMessage());
            }
            
            // 测试距离计算功能
            System.out.println("\n--- 测试距离计算 ---");
            try {
                double distance = mapService.calculateDistance(
                    new BigDecimal("116.4074"), new BigDecimal("39.9042"),
                    new BigDecimal("116.4075"), new BigDecimal("39.9043"));
                System.out.println("两点间距�? " + distance + " �?);
            } catch (Exception e) {
                System.out.println("距离计算测试失败: " + e.getMessage());
            }
            
        } finally {
            // 关闭应用上下�?            context.close();
        }
    }
}
