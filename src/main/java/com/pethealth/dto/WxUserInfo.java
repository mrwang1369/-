package com.pethealth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 微信用户信息
 */
@Data
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
}
