package com.pethealth.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 瀵嗙爜鍔犲瘑宸ュ叿绫? */
@Component
public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 鍔犲瘑瀵嗙爜
     *
     * @param rawPassword 鏄庢枃瀵嗙爜
     * @return 鍔犲瘑鍚庣殑瀵嗙爜
     */
    public static String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("瀵嗙爜涓嶈兘涓虹┖");
        }
        return encoder.encode(rawPassword);
    }

    /**
     * 楠岃瘉瀵嗙爜
     *
     * @param rawPassword 鏄庢枃瀵嗙爜
     * @param encodedPassword 鍔犲瘑鍚庣殑瀵嗙爜
     * @return 鏄�惁鍖归厤
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return encoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 妫€鏌ュ瘑鐮佸己搴?     *
     * @param password 瀵嗙爜
     * @return 瀵嗙爜寮哄害璇勫垎锛?-5鍒嗭級
     */
    public static int checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return 1; // 寮卞瘑鐮?        }

        int score = 0;
        
        // 闀垮害妫€鏌?        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        
        // 瀛楃�绫诲瀷妫€鏌?        if (password.matches(".*[a-z].*")) score++; // 灏忓啓瀛楁瘝
        if (password.matches(".*[A-Z].*")) score++; // 澶у啓瀛楁瘝
        if (password.matches(".*\\d.*")) score++;   // 鏁板瓧
        if (password.matches(".*[^a-zA-Z0-9].*")) score++; // 鐗规畩瀛楃�
        
        return Math.min(score, 5); // 鏈€楂?鍒?    }
}
package com.pethealth.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密工具类
 */
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
     * 检查密码强度
     *
     * @param password 密码
     * @return 密码强度评分（1-5分）
     */
    public static int checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return 1; // 弱密码
        }

        int score = 0;
        
        // 长度检查
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        
        // 字符类型检查
        if (password.matches(".*[a-z].*")) score++; // 小写字母
        if (password.matches(".*[A-Z].*")) score++; // 大写字母
        if (password.matches(".*\\d.*")) score++;   // 数字
        if