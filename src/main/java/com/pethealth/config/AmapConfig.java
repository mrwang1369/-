package com.pethealth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 高德地图配置类
 *
 * @author Mr wang
 * @since 2026-02-13
 */
@Data
@Component
@ConfigurationProperties(prefix = "amap")
public class AmapConfig {
    
    /**
     * 高德地图API密钥
     */
    private String key;
    
    /**
     * API请求URL
     */
    private String baseUrl;
    
    /**
     * 地理编码URL
     */
    private String geocodeUrl;
    
    /**
     * 逆地理编码URL
     */
    private String regeocodeUrl;
    
    /**
     * 周边POI搜索URL
     */
    private String poiAroundUrl;
    
    /**
     * 关键字POI搜索URL
     */
    private String poiTextUrl;
    
    // Getter和Setter方法
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getGeocodeUrl() {
        return geocodeUrl;
    }
    
    public void setGeocodeUrl(String geocodeUrl) {
        this.geocodeUrl = geocodeUrl;
    }
    
    public String getRegeocodeUrl() {
        return regeocodeUrl;
    }
    
    public void setRegeocodeUrl(String regeocodeUrl) {
        this.regeocodeUrl = regeocodeUrl;
    }
    
    public String getPoiAroundUrl() {
        return poiAroundUrl;
    }
    
    public void setPoiAroundUrl(String poiAroundUrl) {
        this.poiAroundUrl = poiAroundUrl;
    }
    
    public String getPoiTextUrl() {
        return poiTextUrl;
    }
    
    public void setPoiTextUrl(String poiTextUrl) {
        this.poiTextUrl = poiTextUrl;
    }
}
