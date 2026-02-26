package com.pethealth.test;

import com.baomidou.mybatisplus.extension.service.IService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

/**
 * Service层测试基类
 * 自动处理MyBatis-Plus Service的Mapper Mock注入
 * 
 * @param <T> Service类型
 * @param <M> Mapper类型
 */
public abstract class BaseServiceTest<T extends IService<?>, M> {
    
    @Mock
    protected M baseMapper;
    
    protected T service;
    
    /**
     * 获取要测试的Service实例
     * 子类必须实现此方法返回具体的Service实例
     */
    protected abstract T getService();
    
    /**
     * 获取Mapper的实际类型
     * 子类必须实现此方法返回Mapper的具体类型
     */
    protected abstract Class<M> getMapperClass();
    
    /**
     * 设置Mock对象到Service中
     */
    @BeforeEach
    void setUpServiceTest() throws Exception {
        MockitoAnnotations.openMocks(this);
        service = getService();
        
        // 使用反射将Mock的Mapper注入到Service中
        injectMapperIntoService();
    }
    
    /**
     * 通过反射注入Mapper
     */
    @SuppressWarnings("unchecked")
    private void injectMapperIntoService() throws Exception {
        // MyBatis-Plus ServiceImpl中的mapper字段名就是"mapper"
        Field mapperField = service.getClass().getSuperclass().getDeclaredField("mapper");
        mapperField.setAccessible(true);
        mapperField.set(service, baseMapper);
    }
}