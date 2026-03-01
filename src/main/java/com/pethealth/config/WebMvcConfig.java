package com.pethealth.config;

import com.pethealth.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Value("${file.upload.path:${user.home}/pethealth/uploads}")
    private String uploadPath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加认证拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns(
                    "/auth/login",      // 认证相关接口
                    "/auth/register",
                    "/auth/wxlogin",
                    "/auth/refresh",
                    "/auth/profile",
                    "/auth/logout",
                    "/health",          // 健康检查
                    "/swagger-ui/**",   // Swagger UI
                    "/v3/api-docs/**",  // API文档
                    "/webjars/**",      // 静态资源
                    "/doc.html",        // Knife4j文档
                    "/favicon.ico"       // 图标
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件访问静态资源映射
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadPath + "/")
                .setCachePeriod(3600); // 缓存1小时
    }
}
