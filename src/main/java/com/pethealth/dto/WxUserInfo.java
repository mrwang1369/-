package com.pethealth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 微信用户信息
 */
@Schema(description = "微信用户信息")
public class WxUserInfo {

    @Schema(description = "用户昵称", example = "微信用户")
    @JsonProperty("nickName")
    private String nickName;

    @Schema(description = "用户头像URL", example = "https://thirdwx.qlogo.cn/mmopen/xxx")
    @JsonProperty("avatarUrl")
    private String avatarUrl;

    @Schema(description = "用户性别；-1未知，1男，2女", example = "1")
    @JsonProperty("gender")
    private Integer gender;

    @Schema(description = "用户所在国家", example = "中国")
    @JsonProperty("country")
    private String country;

    @Schema(description = "用户所在省份", example = "广东")
    private String province;

    @Schema(description = "用户所在城市", example = "广州")
    private String city;
    
    // 自动生成getter/setter方法
    public String getNickName() {
        return nickName;
    }
    
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public Integer getGender() {
        return gender;
    }
    
    public void setGender(Integer gender) {
        this.gender = gender;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getProvince() {
        return province;
    }
    
    public void setProvince(String province) {
        this.province = province;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
}
