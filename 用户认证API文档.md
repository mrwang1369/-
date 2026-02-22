# 用户认证模块API文档

## 概述
用户认证模块提供完整的用户注册、登录、登出等功能，支持手机号登录和微信登录两种方式。

## 基础配置
- **基础URL**: `http://localhost:8080/api`
- **认证方式**: JWT Token
- **请求头**: `Authorization: Bearer {token}`

## API接口说明

### 1. 用户注册 `/auth/register`

**请求方式**: POST  
**请求地址**: `/api/auth/register`  
**请求头**: `Content-Type: application/json`

#### 请求参数
```json
{
  "phone": "13800138001",
  "password": "123456",
  "confirmPassword": "123456",
  "nickname": "宠物主人小王",
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userInfo": {
      "userId": 1,
      "nickname": "宠物主人小王",
      "phone": "13800138001",
      "avatarUrl": "https://example.com/avatar.jpg",
      "openid": null
    }
  },
  "timestamp": 1708567890123
}
```

### 2. 用户登录 `/auth/login`

**请求方式**: POST  
**请求地址**: `/api/auth/login`  
**请求头**: `Content-Type: application/json`

#### 手机号登录
```json
{
  "loginType": "phone",
  "phone": "13800138001",
  "password": "123456"
}
```

#### 微信登录
```json
{
  "loginType": "wx",
  "wxCode": "081kA00w3D123456",
  "wxUserInfo": {
    "nickName": "微信用户",
    "avatarUrl": "https://thirdwx.qlogo.cn/mmopen/xxx",
    "gender": 1,
    "country": "中国",
    "province": "北京",
    "city": "北京"
  }
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userInfo": {
      "userId": 1,
      "nickname": "宠物主人小王",
      "phone": "13800138001",
      "avatarUrl": "https://example.com/avatar.jpg",
      "openid": "oQv9G5HmN8KpL2JrS7TzY4XwV1U"
    }
  },
  "timestamp": 1708567890123
}
```

### 3. 获取用户信息 `/auth/profile`

**请求方式**: GET  
**请求地址**: `/api/auth/profile`  
**请求头**: `Authorization: Bearer {token}`

#### 响应示例
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "userId": 1,
    "nickname": "宠物主人小王",
    "phone": "13800138001",
    "avatarUrl": "https://example.com/avatar.jpg",
    "openid": "oQv9G5HmN8KpL2JrS7TzY4XwV1U"
  },
  "timestamp": 1708567890123
}
```

### 4. 用户登出 `/auth/logout`

**请求方式**: POST  
**请求地址**: `/api/auth/logout`  
**请求头**: `Authorization: Bearer {token}`

#### 响应示例
```json
{
  "code": 200,
  "message": "登出成功",
  "data": null,
  "timestamp": 1708567890123
}
```

### 5. 刷新Token `/auth/refresh`

**请求方式**: POST  
**请求地址**: `/api/auth/refresh`  
**请求头**: `Authorization: Bearer {token}`

#### 响应示例
```json
{
  "code": 200,
  "message": "Token刷新成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.newtoken...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userInfo": {
      "userId": 1,
      "nickname": "宠物主人小王",
      "phone": "13800138001",
      "avatarUrl": "https://example.com/avatar.jpg",
      "openid": "oQv9G5HmN8KpL2JrS7TzY4XwV1U"
    }
  },
  "timestamp": 1708567890123
}
```

## 错误响应格式

### 通用错误响应
```json
{
  "code": 400,
  "message": "错误描述信息",
  "data": null,
  "timestamp": 1708567890123,
  "path": "/api/auth/login"
}
```

### 常见错误码
- `400`: 请求参数错误
- `401`: 未授权访问
- `404`: 资源不存在
- `500`: 服务器内部错误

## 安全说明

### JWT Token
- **有效期**: 24小时
- **刷新机制**: 支持Token刷新接口
- **存储建议**: 客户端安全存储（localStorage或secure cookie）

### 密码安全
- 使用BCrypt加密存储
- 密码强度要求：6-20位字符
- 支持大小写字母、数字、特殊字符

### 认证拦截
- 除认证相关接口外，其他接口都需要携带有效的JWT Token
- Token失效时返回401状态码

## 测试数据

### 测试用户
```sql
-- 测试用户数据已在数据库初始化脚本中提供
-- 用户名: 13800138001
-- 密码: 123456
-- 微信OpenID: test_openid_001
```

## 注意事项

1. **微信登录**: 实际项目中需要调用微信官方接口验证code
2. **密码重置**: 当前版本暂未实现密码找回功能
3. **并发控制**: 建议在生产环境中添加登录失败次数限制
4. **日志记录**: 所有认证操作都会记录详细日志便于审计