package com.pethealth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 应用配置类
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Configuration
public class AppConfig {

    /**
     * 配置RestTemplate Bean
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}