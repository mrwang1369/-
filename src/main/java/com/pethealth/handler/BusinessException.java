package com.pethealth.handler;

import com.pethealth.common.Result; /**
 * 业务异常基类
 */
public class BusinessException extends RuntimeException {
    private Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = Result.BAD_REQUEST_CODE;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
