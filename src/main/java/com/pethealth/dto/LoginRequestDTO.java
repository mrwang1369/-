package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 登录请求DTO
 */
@Data
@Schema(description = "用户登录请求参数")
public class LoginRequestDTO {

    @Schema(description = "登录方式：phone(手机登录) 或 wx(微信)", example = "phone")
    @NotBlank(message = "登录方式不能为空")
    @Pattern(regexp = "^(phone|wx)$", message = "登录方式只能是phone或wx")
    private String loginType;

    @Schema(description = "手机号（手机号登录时必填）", example = "13800138001")
    private String phone;

    @Schema(description = "密码：手机号登录时必填", example = "123456")
    private String password;

    @Schema(description = "微信code（微信登录时必填）", example = "081kA00w3D123456")
    private String wxCode;

    @Schema(description = "微信用户信息（微信登录时必填）")
    private WxUserInfo wxUserInfo;
}
