package com.pethealth.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 高德地图API响应DTO
 *
 * @author Mr wang
 * @since 2026-02-13
 */
@Data
public class AmapResponseDTO {
    
    /**
     * 返回状态码 1表示成功，0表示失败
     */
    private String status;
    
    /**
     * 返回的状态信息
     */
    private String info;
    
    /**
     * 状态码
     */
    private String infocode;
    
    /**
     * 地理编码结果
     */
    private List<Geocode> geocodes;
    
    /**
     * 逆地理编码结果
     */
    private Regeocode regeocode;
    
    /**
     * POI存储结果
     */
    private List<Poi> pois;
    
    /**
     * 条目数
     */
    private String count;
    
    /**
     * 地理编码信息
     */
    @Data
    public static class Geocode {
        private String formatted_address;
        private String province;
        private String city;
        private String citycode;
        private String district;
        private String street;
        private String number;
        private String adcode;
        private String location;
        private String level;
    }
    
    /**
     * 逆地理编码信息
     */
    @Data
    public static class Regeocode {
        private String formatted_address;
        private AddressComponent addressComponent;
        private List<Road> roads;
        private List<Poi> pois;
        private List<Aoi> aois;
    }
    
    /**
     * 街道门牌号
     */
    @Data
    public static class AddressComponent {
        private String country;
        private String province;
        private String city;
        private String citycode;
        private String district;
        private String adcode;
        private String township;
        private String towncode;
        private StreetNumber streetNumber;
    }
    
    /**
     * 门牌号
     */
    @Data
    public static class StreetNumber {
        private String street;
        private String number;
        private String location;
        private String direction;
        private String distance;
    }
    
    /**
     * 道路信息
     */
    @Data
    public static class Road {
        private String id;
        private String name;
        private String distance;
        private String location;
        private String direction;
    }
    
    /**
     * POI信息
     */
    @Data
    public static class Poi {
        private String id;
        private String name;
        private String type;
        private String typecode;
        private String biz_type;
        private String address;
        private String location;
        private String tel;
        private String distance;
        private String business_area;
        private String postcode;
        private String website;
        private String email;
        private String entr_location;
        private String exit_location;
        private String match;
        private String recommend;
        private String timestamp;
        private String alias;
        private String indoor_map;
        private String indoor_data;
        private String groupbuy_num;
        private String discount_num;
        private BizExt biz_ext;
        private String event;
        private Children children;
        private String photos;
    }
    
    /**
     * 附加信息
     */
    @Data
    public static class BizExt {
        private String rating;
        private String cost;
    }
    
    /**
     * 子POI信息
     */
    @Data
    public static class Children {
        private String[] childtype;
        private String[] industries;
    }
    
    /**
     * AOI信息
     */
    @Data
    public static class Aoi {
        private String id;
        private String name;
        private String adcode;
        private String location;
        private String area;
        private String distance;
        private String type;
    }
}
