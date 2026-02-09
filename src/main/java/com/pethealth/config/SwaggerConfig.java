package com.pethealth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI petHealthOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("宠物健康管家小程序API")
                        .description("基于Spring Boot 3的宠物健康管理系统后端API")
                        .version("1.0.0"));
    }
}