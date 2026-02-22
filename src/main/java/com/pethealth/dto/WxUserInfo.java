package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 微信用户信息DTO
 */
@Schema(description = "微信用户信息")
public class WxUserInfo {

    @Schema(description = "用户昵称", example = "微信用户")
    private String nickName;

    @Schema(description = "用户头像URL", example = "https://thirdwx.qlogo.cn/mmopen/xxx")
    private String avatarUrl;

    @Schema(description = "用户性别�?-未知�?-男性，2-女�?, example = "1")
    private Integer gender;

    @Schema(description = "用户所在国�?, example = "中国")
    private String country;

    @Schema(description = "用户所在省�?, example = "北京")
    private String province;

    @Schema(description = "用户所在城�?, example = "北京")
    private String city;
    
    // 手动添加getter/setter方法
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
