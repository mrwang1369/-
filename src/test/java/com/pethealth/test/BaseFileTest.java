package com.pethealth.test;

import com.pethealth.BackendApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文件服务测试基类
 *
 * @author pethealth
 * @since 2026-02-27
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BackendApplication.class)
@Transactional
public abstract class BaseFileTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
}