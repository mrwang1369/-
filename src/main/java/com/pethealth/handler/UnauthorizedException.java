package com.pethealth.handler;

import com.pethealth.common.Result; /**
 * 未授权异常
 */
public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super(Result.UNAUTHORIZED_CODE, message);
    }
}
