package com.pethealth.handler;

import com.pethealth.common.Result; /**
 * 资源不存在异�?
 */
public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message) {
        super(Result.NOT_FOUND_CODE, message);
    }
}
