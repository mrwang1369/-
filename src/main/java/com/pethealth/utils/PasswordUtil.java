package com.pethealth.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密工具�? */
@Component
public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     *
     * @param rawPassword 明文密码
     * @return 加密后的密码
     */
    public static String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return encoder.encode(rawPassword);
    }

    /**
     * 验证密码
     *
     * @param rawPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return encoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 检查密码强�?     *
     * @param password 密码
     * @return 密码强度评分�?-5分）
     */
    public static int checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return 1; // 弱密�?        }

        int score = 0;
        
        // 长度检�?        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        
        // 字符类型检�?        if (password.matches(".*[a-z].*")) score++; // 小写字母
        if (password.matches(".*[A-Z].*")) score++; // 大写字母
        if (password.matches(".*\\d.*")) score++;   // 数字
        if (password.matches(".*[^a-zA-Z0-9].*")) score++; // 特殊字符
        
        return Math.min(score, 5); // 最�?�?    }
}
