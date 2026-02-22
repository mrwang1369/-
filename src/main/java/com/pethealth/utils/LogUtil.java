package com.pethealth.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具�? * 提供统一的日志记录功能，避免Lombok注解处理器问�? *
 * @author Mr wang
 * @since 2026-02-13
 */
public class LogUtil {
    
    /**
     * 获取指定类的日志记录�?     * 
     * @param clazz �?     * @return Logger实例
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    
    /**
     * 获取当前类的日志记录�?     * 注意：此方法需要在具体类中调用才能正确获取类名
     * 
     * @return Logger实例
     */
    public static Logger getCurrentLogger() {
        // 获取调用者的类名
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // stackTrace[0] �?getStackTrace
        // stackTrace[1] �?getCurrentLogger
        // stackTrace[2] 是调�?getCurrentLogger 的方�?        // stackTrace[3] 是实际调用的�?        if (stackTrace.length > 3) {
            try {
                Class<?> callerClass = Class.forName(stackTrace[3].getClassName());
                return LoggerFactory.getLogger(callerClass);
            } catch (ClassNotFoundException e) {
                return LoggerFactory.getLogger(LogUtil.class);
            }
        }
        return LoggerFactory.getLogger(LogUtil.class);
    }
}
