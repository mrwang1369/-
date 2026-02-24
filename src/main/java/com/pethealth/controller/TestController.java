package com.pethealth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        log.info("测试接口被调用");
        return "宠物健康管家后端服务已启动！当前时间：" + System.currentTimeMillis();
    }
}
