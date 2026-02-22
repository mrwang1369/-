package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户认证DTO
 */
@Schema(description = "用户认证数据")
public class AuthResponseDTO {

    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType;

    @Schema(description = "过期时间（秒）", example = "86400")
    private Long expiresIn;

    @Schema(description = "用户信息")
    private UserInfo userInfo;

    @Schema(description = "用户角色信息")
    public static class UserInfo {
        @Schema(description = "用户ID", example = "1")
        private Long userId;

        @Schema(description = "用户昵称", example = "宠物医生")
        private String nickname;

        @Schema(description = "手机号", example = "13800138001")
        private String phone;

        @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
        private String avatarUrl;

        @Schema(description = "微信OpenID", example = "oQv9G5HmN8KpL2JrS7TzY4XwV1U")
        private String openid;
        
        // 构造函数
        public UserInfo() {}
        
        public UserInfo(Long userId, String nickname, String phone, String avatarUrl, String openid) {
            this.userId = userId;
            this.nickname = nickname;
            this.phone = phone;
            this.avatarUrl = avatarUrl;
            this.openid = openid;
        }
        
        // Getter/Setter方法
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        
        public String getOpenid() { return openid; }
        public void setOpenid(String openid) { this.openid = openid; }
        
        // Builder模式
        public static UserInfoBuilder builder() {
            return new UserInfoBuilder();
        }
        
        public static class UserInfoBuilder {
            private Long userId;
            private String nickname;
            private String phone;
            private String avatarUrl;
            private String openid;
            
            public UserInfoBuilder userId(Long userId) {
                this.userId = userId;
                return this;
            }
            
            public UserInfoBuilder nickname(String nickname) {
                this.nickname = nickname;
                return this;
            }
            
            public UserInfoBuilder phone(String phone) {
                this.phone = phone;
                return this;
            }
            
            public UserInfoBuilder avatarUrl(String avatarUrl) {
                this.avatarUrl = avatarUrl;
                return this;
            }
            
            public UserInfoBuilder openid(String openid) {
                this.openid = openid;
                return this;
            }
            
            public UserInfo build() {
                return new UserInfo(userId, nickname, phone, avatarUrl, openid);
            }
        }
    }
    
    // AuthResponseDTO的默认构造函数getter/setter
    public AuthResponseDTO() {}
    
    public AuthResponseDTO(String accessToken, String tokenType, Long expiresIn, UserInfo userInfo) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.userInfo = userInfo;
    }
    
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    
    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
    
    public UserInfo getUserInfo() { return userInfo; }
    public void setUserInfo(UserInfo userInfo) { this.userInfo = userInfo; }
    
    // Builder模式
    public static AuthResponseDTOBuilder builder() {
        return new AuthResponseDTOBuilder();
    }
    
    public static class AuthResponseDTOBuilder {
        private String accessToken;
        private String tokenType;
        private Long expiresIn;
        private UserInfo userInfo;
        
        public AuthResponseDTOBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }
        
        public AuthResponseDTOBuilder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }
        
        public AuthResponseDTOBuilder expiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }
        
        public AuthResponseDTOBuilder userInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
            return this;
        }
        
        public AuthResponseDTO build() {
            return new AuthResponseDTO(accessToken, tokenType, expiresIn, userInfo);
        }
    }
}
