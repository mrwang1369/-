package com.pethealth.handler;

/**
 * 宠物不存在异常
 *
 * @author Mr wang
 * @since 2026-02-24
 */
public class PetNotFoundException extends RuntimeException {
    
    public PetNotFoundException() {
        super("宠物不存在");
    }
    
    public PetNotFoundException(String message) {
        super(message);
    }
    
    public PetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}