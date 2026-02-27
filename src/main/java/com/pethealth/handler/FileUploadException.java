package com.pethealth.handler;

/**
 * 文件上传异常类
 *
 * @author pethealth
 * @since 2026-02-27
 */
public class FileUploadException extends RuntimeException {

    private final String errorCode;

    public FileUploadException(String message) {
        super(message);
        this.errorCode = "FILE_UPLOAD_ERROR";
    }

    public FileUploadException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "FILE_UPLOAD_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}