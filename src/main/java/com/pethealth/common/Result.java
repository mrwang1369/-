package com.pethealth.common;

import lombok.Data;
import java.io.Serializable;

/**
 * 统一API响应封装�?
 * 用于标准化前后端数据交互格式
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间�?
     */
    private Long timestamp;

    /**
     * 请求路径（可选，用于调试�?
     */
    private String path;

    // 常用状态码定义
    public static final Integer SUCCESS_CODE = 200;
    public static final Integer BAD_REQUEST_CODE = 400;
    public static final Integer UNAUTHORIZED_CODE = 401;
    public static final Integer FORBIDDEN_CODE = 403;
    public static final Integer NOT_FOUND_CODE = 404;
    public static final Integer INTERNAL_SERVER_ERROR_CODE = 500;

    // 常用消息定义
    public static final String SUCCESS_MESSAGE = "操作成功";
    public static final String ERROR_MESSAGE = "操作失败";
    public static final String UNAUTHORIZED_MESSAGE = "未授权访�?;
    public static final String FORBIDDEN_MESSAGE = "禁止访问";
    public static final String NOT_FOUND_MESSAGE = "资源不存�?;

    /**
     * 无参构造器
     */
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 全参构造器
     */
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应 - 无数�?
     */
    public static <T> Result<T> success() {
        return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, null);
    }

    /**
     * 成功响应 - 带数�?
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    /**
     * 成功响应 - 自定义消息和数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(SUCCESS_CODE, message, data);
    }

    /**
     * 失败响应 - 默认错误
     */
    public static <T> Result<T> error() {
        return new Result<>(INTERNAL_SERVER_ERROR_CODE, ERROR_MESSAGE, null);
    }

    /**
     * 失败响应 - 自定义错误消�?
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(INTERNAL_SERVER_ERROR_CODE, message, null);
    }

    /**
     * 失败响应 - 自定义状态码和消�?
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败响应 - 自定义状态码、消息和数据
     */
    public static <T> Result<T> error(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

    /**
     * 参数错误响应
     */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(BAD_REQUEST_CODE, message, null);
    }

    /**
     * 未授权响�?
     */
    public static <T> Result<T> unauthorized() {
        return new Result<>(UNAUTHORIZED_CODE, UNAUTHORIZED_MESSAGE, null);
    }

    /**
     * 未授权响�?- 自定义消�?
     */
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(UNAUTHORIZED_CODE, message, null);
    }

    /**
     * 禁止访问响应
     */
    public static <T> Result<T> forbidden() {
        return new Result<>(FORBIDDEN_CODE, FORBIDDEN_MESSAGE, null);
    }

    /**
     * 资源不存在响�?
     */
    public static <T> Result<T> notFound() {
        return new Result<>(NOT_FOUND_CODE, NOT_FOUND_MESSAGE, null);
    }

    /**
     * 资源不存在响�?- 自定义消�?
     */
    public static <T> Result<T> notFound(String message) {
        return new Result<>(NOT_FOUND_CODE, message, null);
    }

    /**
     * 设置请求路径
     */
    public Result<T> path(String path) {
        this.path = path;
        return this;
    }

    /**
     * 判断响应是否成功
     */
    public boolean isSuccess() {
        return SUCCESS_CODE.equals(this.code);
    }

    /**
     * 链式调用设置数据
     */
    public Result<T> data(T data) {
        this.data = data;
        return this;
    }

    /**
     * 链式调用设置消息
     */
    public Result<T> message(String message) {
        this.message = message;
        return this;
    }

    /**
     * 链式调用设置状态码
     */
    public Result<T> code(Integer code) {
        this.code = code;
        return this;
    }
    
    // 手动添加getter方法以确保编译通过
    public Integer getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public T getData() {
        return data;
    }
}
