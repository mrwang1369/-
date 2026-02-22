# 宠物健康管家 - 高德地图集成说明

## 概述
本项目已成功集成高德地图API，提供地理位置服务功能，包括：
- 地理编码（地址转坐标）
- 逆地理编码（坐标转地址）
- 周边POI搜索
- 距离计算

## 配置步骤

### 1. 获取高德地图API Key
1. 访问 [高德开放平台](https://lbs.amap.com/)
2. 注册账号并创建应用
3. 获取Web服务API的Key

### 2. 配置API Key
在 `application.properties` 文件中配置：

```properties
# 高德地图配置
amap.key=your-amap-api-key-here
amap.base-url=https://restapi.amap.com/v3
amap.geocode-url=${amap.base-url}/geocode/geo
amap.regeocode-url=${amap.base-url}/geocode/regeo
amap.poi-around-url=${amap.base-url}/place/around
amap.poi-text-url=${amap.base-url}/place/text
```

或者通过环境变量设置：
```bash
export AMAP_KEY=your-amap-api-key-here
```

## 主要功能接口

### 1. 地理编码接口
```
POST /api/service-points/geocode
参数: address (地址)
```

### 2. 逆地理编码接口
```
POST /api/service-points/regeocode
参数: longitude (经度), latitude (纬度)
```

### 3. 周边搜索接口
```
GET /api/service-points/nearby
参数: longitude (经度), latitude (纬度), radius (半径), type (类型)
```

### 4. 文本搜索接口
```
GET /api/service-points/search
参数: keywords (关键词), city (城市), types (类型)
```

### 5. 距离计算接口
```
POST /api/service-points/distance
参数: startLon, startLat, endLon, endLat
```

## 测试方法

### 运行测试应用
```bash
# 编译项目
./mvnw clean compile

# 运行测试
java -cp target/classes com.pethealth.test.AmapTestApplication
```

### 使用Postman测试API
1. 启动主应用：`./mvnw spring-boot:run`
2. 使用Postman调用上述接口进行测试

## 错误处理
- API调用失败会返回相应的错误信息
- 网络异常会有重试机制
- 日志记录详细的错误信息便于排查

## 注意事项
1. 请妥善保管API Key，不要泄露
2. 高德地图API有调用次数限制，请合理使用
3. 生产环境建议配置HTTPS
4. 建议添加API调用频率限制防止滥用

## 技术实现
- 使用RestTemplate进行HTTP请求
- FastJSON2进行JSON解析
- Spring Boot ConfigurationProperties管理配置
- 统一异常处理和日志记录