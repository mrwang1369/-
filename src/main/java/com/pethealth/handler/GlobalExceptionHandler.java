package com.pethealth.handler;

import com.pethealth.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理各种异常的返回格式
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("业务异常: {} - 请求路径: {}", ex.getMessage(), request.getRequestURI(), ex);
        return Result.error(ex.getCode(), ex.getMessage())
                .path(request.getRequestURI());
    }

    /**
     * 处理参数校验异常（@Validated 在方法参数上使用）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        String errorMsg = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("; "));

        log.warn("参数校验错误: {} - 请求路径: {}", errorMsg, request.getRequestURI());
        return Result.badRequest("参数校验错误: " + errorMsg)
                .path(request.getRequestURI())
                .data(errors);
    }

    /**
     * 处理参数校验异常（@ModelAttribute 在方法参数上使用）
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("参数校验错误: {} - 请求路径: {}", ex.getMessage(), request.getRequestURI());
        return Result.badRequest("参数校验错误")
                .path(request.getRequestURI())
                .data(errors);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String errorMsg = String.format("参数 '%s' 类型不匹配，需要类 %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "未知");

        log.warn("参数类型错误: {} - 请求路径: {}", errorMsg, request.getRequestURI());
        return Result.badRequest(errorMsg)
                .path(request.getRequestURI());
    }

    /**
     * 处理单个参数校验异常（@Validated 在方法参数上使用）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String errorMsg = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        log.warn("参数校验错误: {} - 请求路径: {}", errorMsg, request.getRequestURI());
        return Result.badRequest("参数校验错误: " + errorMsg)
                .path(request.getRequestURI());
    }

    /**
     * 处理认证授权异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<?> handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
        log.warn("未授权访问 {} - 请求路径: {}", ex.getMessage(), request.getRequestURI());
        return Result.unauthorized(ex.getMessage())
                .path(request.getRequestURI());
    }

    /**
     * 处理资源不存在异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("资源不存在 {} - 请求路径: {}", ex.getMessage(), request.getRequestURI());
        return Result.notFound(ex.getMessage())
                .path(request.getRequestURI());
    }

    /**
     * 处理宠物不存在异常
     */
    @ExceptionHandler(PetNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handlePetNotFound(PetNotFoundException ex, HttpServletRequest request) {
        log.warn("宠物不存在 {} - 请求路径: {}", ex.getMessage(), request.getRequestURI());
        return Result.notFound(ex.getMessage())
                .path(request.getRequestURI());
    }

    /**
     * 处理宠物归属权异常
     */
    @ExceptionHandler(PetOwnershipException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<?> handlePetOwnership(PetOwnershipException ex, HttpServletRequest request) {
        log.warn("宠物归属权异常 {} - 请求路径: {}", ex.getMessage(), request.getRequestURI());
        return Result.error(403, ex.getMessage())
                .path(request.getRequestURI());
    }

    /**
     * 处理文件上传异常
     */
    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleFileUploadException(FileUploadException ex, HttpServletRequest request) {
        log.warn("文件上传异常 [{}] {} - 请求路径: {}", ex.getErrorCode(), ex.getMessage(), request.getRequestURI());
        return Result.error(400, ex.getMessage())
                .path(request.getRequestURI());
    }

    /**
     * 处理文件大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public Result<?> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.warn("文件上传大小超限 - 请求路径: {}", request.getRequestURI());
        return Result.error(413, "上传文件大小超出限制")
                .path(request.getRequestURI());
    }

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleGlobalException(Exception ex, HttpServletRequest request) {
        log.error("系统异常: {} - 请求路径: {}", ex.getMessage(), request.getRequestURI(), ex);
        return Result.error("系统异常，请稍后重试")
                .path(request.getRequestURI());
    }
}

// ===== 以下为自定义异常类 =====

