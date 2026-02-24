package com.pethealth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Slf4j
public class HealthController {

    @GetMapping
    public Map<String, Object> healthCheck() {
        log.info("健康检查接口被调用");
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("service", "宠物健康管家后端服务");
        healthInfo.put("timestamp", System.currentTimeMillis());
        healthInfo.put("version", "1.0.0");
        return healthInfo;
    }
}
