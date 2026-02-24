package com.pethealth.handler;

/**
 * 宠物归属权异常
 *
 * @author Mr wang
 * @since 2026-02-24
 */
public class PetOwnershipException extends RuntimeException {
    
    public PetOwnershipException() {
        super("无权操作该宠物");
    }
    
    public PetOwnershipException(String message) {
        super(message);
    }
    
    public PetOwnershipException(String message, Throwable cause) {
        super(message, cause);
    }
}