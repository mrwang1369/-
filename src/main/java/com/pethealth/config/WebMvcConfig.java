package com.pethealth.config;

import com.pethealth.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加认证拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**") // 认证API路径
                .excludePathPatterns(
                        "/api/auth/**",         // 认证接口
                        "/api/public/**",       // 公共接口
                        "/swagger-ui/**",       // Swagger UI
                        "/v3/api-docs/**",      // OpenAPI文档
                        "/webjars/**",          // WebJars资源
                        "/doc.html"             // API文档页面
                );
    }
}
