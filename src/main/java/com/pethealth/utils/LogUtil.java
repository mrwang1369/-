package com.pethealth.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 */
public class LogUtil {
    
    /**
     * 获取日志记录器
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    
    /**
     * 获取当前调用者的日志记录器
     * 注意：该方法只能在方法内部使用，否则无法获取调用者类名
     * 
     * @return Logger实例
     */
    public static Logger getCurrentLogger() {
        // 获取调用者的类名
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // stackTrace[0] 是getStackTrace
        // stackTrace[1] 是getCurrentLogger
        // stackTrace[2] 是调用getCurrentLogger的方法
        // stackTrace[3] 是调用者的方法
        if (stackTrace.length > 3) {
            try {
                Class<?> callerClass = Class.forName(stackTrace[3].getClassName());
                return LoggerFactory.getLogger(callerClass);
            } catch (ClassNotFoundException e) {
                return LoggerFactory.getLogger(LogUtil.class);
            }
        }
        return LoggerFactory.getLogger(LogUtil.class);
    }
    
    /**
     * 记录调试信息
     */
    public static void debug(Class<?> clazz, String format, Object... arguments) {
        LoggerFactory.getLogger(clazz).debug(format, arguments);
    }
    
    /**
     * 记录普通信息
     */
    public static void info(Class<?> clazz, String format, Object... arguments) {
        LoggerFactory.getLogger(clazz).info(format, arguments);
    }
    
    /**
     * 记录警告信息
     */
    public static void warn(Class<?> clazz, String format, Object... arguments) {
        LoggerFactory.getLogger(clazz).warn(format, arguments);
    }
    
    /**
     * 记录错误信息
     */
    public static void error(Class<?> clazz, String format, Object... arguments) {
        LoggerFactory.getLogger(clazz).error(format, arguments);
    }
}
