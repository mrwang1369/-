package com.pethealth.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethealth.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 日志切面 - 使用AOP记录接口请求参数、响应结果和执行时间
 * 便于调试和监控系统运行状况
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 切点定义：拦截所有Controller层的方法
     */
    @Pointcut("execution(* com.pethealth.controller..*.*(..))")
    public void controllerPointcut() {}

    /**
     * 切点定义：拦截所有Service层的方法
     */
    @Pointcut("execution(* com.pethealth.service..*.*(..))")
    public void servicePointcut() {}

    /**
     * 环绕通知：记录Controller层接口日志
     */
    @Around("controllerPointcut()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // 获取请求信息
        HttpServletRequest request = getHttpServletRequest();
        String requestUri = request != null ? request.getRequestURI() : "N/A";
        String httpMethod = request != null ? request.getMethod() : "N/A";
        String clientIp = getClientIp(request);

        // 记录请求日志
        Object[] args = joinPoint.getArgs();
        String requestParams = getRequestParams(args);

        log.info("【请求开始】| URI: {} {} | IP: {} | 类: {} | 方法: {} | 参数: {}",
                httpMethod, requestUri, clientIp, className, methodName, requestParams);

        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            // 记录响应日志
            String responseResult = getResponseResult(result);
            log.info("【请求成功】| URI: {} {} | 耗时: {}ms | 响应: {}",
                    httpMethod, requestUri, executionTime, responseResult);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("【请求异常】| URI: {} {} | 耗时: {}ms | 异常: {}",
                    httpMethod, requestUri, executionTime, e.getMessage(), e);
            throw e;
        }

        return result;
    }

    /**
     * 环绕通知：记录Service层方法日志
     */
    @Around("servicePointcut()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // 记录方法开始日志
        Object[] args = joinPoint.getArgs();
        String methodParams = getMethodParams(args);

        if (log.isDebugEnabled()) {
            log.debug("【方法开始】| 类: {} | 方法: {} | 参数: {}",
                    className, methodName, methodParams);
        }

        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            // 记录方法结束日志（只记录耗时较长的方法）
            if (executionTime > 1000) { // 超过1秒的方法记录警告
                log.warn("【方法耗时】| 类: {} | 方法: {} | 耗时: {}ms | 参数: {}",
                        className, methodName, executionTime, methodParams);
            } else if (log.isDebugEnabled()) {
                log.debug("【方法结束】| 类: {} | 方法: {} | 耗时: {}ms",
                        className, methodName, executionTime);
            }

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("【方法异常】| 类: {} | 方法: {} | 耗时: {}ms | 异常: {} | 参数: {}",
                    className, methodName, executionTime, e.getMessage(), methodParams, e);
            throw e;
        }

        return result;
    }

    /**
     * 获取HTTP请求对象
     */
    private HttpServletRequest getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "N/A";
        }

        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * 获取请求参数（过滤敏感信息和文件参数）
     */
    private String getRequestParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) {
                        return "null";
                    }

                    // 过滤敏感信息
                    if (arg instanceof HttpServletRequest ||
                            arg instanceof HttpServletResponse) {
                        return arg.getClass().getSimpleName();
                    }

                    // 过滤文件上传参数
                    if (arg instanceof MultipartFile) {
                        MultipartFile file = (MultipartFile) arg;
                        return String.format("MultipartFile[name=%s, size=%d]",
                                file.getOriginalFilename(), file.getSize());
                    }

                    if (arg instanceof MultipartFile[]) {
                        MultipartFile[] files = (MultipartFile[]) arg;
                        return String.format("MultipartFile[%d files]", files.length);
                    }

                    // 敏感字段脱敏
                    String paramStr = arg.toString();
                    if (paramStr.toLowerCase().contains("password") ||
                            paramStr.toLowerCase().contains("token")) {
                        return "***敏感信息***";
                    }

                    // 限制参数长度，避免日志过大
                    if (paramStr.length() > 500) {
                        return paramStr.substring(0, 500) + "...[truncated]";
                    }

                    return paramStr;
                })
                .collect(Collectors.joining(", ", "[", "]"));
    }

    /**
     * 获取方法参数
     */
    private String getMethodParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) {
                        return "null";
                    }

                    // 限制参数长度
                    String paramStr = arg.toString();
                    if (paramStr.length() > 200) {
                        return paramStr.substring(0, 200) + "...[truncated]";
                    }

                    return paramStr;
                })
                .collect(Collectors.joining(", ", "[", "]"));
    }

    /**
     * 获取响应结果（格式化输出）
     */
    private String getResponseResult(Object result) {
        if (result == null) {
            return "null";
        }

        try {
            // 如果是Result对象，提取关键信息
            if (result instanceof Result) {
                Result<?> resultObj = (Result<?>) result;
                return String.format("Result[code=%d, message=%s, data=%s]",
                        resultObj.getCode(),
                        resultObj.getMessage(),
                        resultObj.getData() != null ?
                                objectMapper.writeValueAsString(resultObj.getData()) : "null");
            }

            // 限制响应结果长度
            String resultStr = result.toString();
            if (resultStr.length() > 1000) {
                return resultStr.substring(0, 1000) + "...[truncated]";
            }

            return resultStr;
        } catch (Exception e) {
            return "Error serializing result: " + e.getMessage();
        }
    }

    /**
     * 性能监控：记录慢查询
     */
    @Around("controllerPointcut() || servicePointcut()")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - startTime;

        // 记录慢查询（超过3秒）
        if (executionTime > 3000) {
            log.warn("【性能告警】| 方法: {} | 执行时间: {}ms", methodName, executionTime);
        }

        return result;
    }
}