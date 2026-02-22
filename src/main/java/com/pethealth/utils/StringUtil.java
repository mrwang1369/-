package com.pethealth.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 字符串工具类 - 依赖Apache Commons Lang3
 */
public class StringUtil extends StringUtils {

    /**
     * 判断字符串是否为空(包括空白字符)
     */
    public static boolean isBlank(CharSequence cs) {
        return StringUtils.isBlank(cs);
    }

    /**
     * 判断字符串是否非空
     */
    public static boolean isNotBlank(CharSequence cs) {
        return StringUtils.isNotBlank(cs);
    }

    /**
     * 安全截断字符串(防止溢出)
     *
     * @param str 原始字符串
     * @param maxLength 最大长度
     * @return 截断后的字符串
     */
    public static String safeTruncate(String str, int maxLength) {
        if (str == null) return null;
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }

    /**
     * 生成宠物编号 (PET-20260209-0001)
     *
     * @param date 日期
     * @param sequence 序列号
     * @return 宠物编号
     */
    public static String generatePetCode(LocalDate date, int sequence) {
        String datePart = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("PET-%s-%04d", datePart, sequence);
    }

    /**
     * 隐藏敏感信息 (如手机号、身份证)
     *
     * @param str 原始字符串
     * @param start 开始保留位数
     * @param end 结束保留位数
     * @return 脱敏后的字符串
     */
    public static String hideSensitiveInfo(String str, int start, int end) {
        if (isBlank(str)) return str;
        if (str.length() <= start + end) return str;

        int hideLength = str.length() - start - end;
        String stars = repeat('*', hideLength);
        return str.substring(0, start) + stars + str.substring(str.length() - end);
    }

    /**
     * 拼接路径 (自动处理斜杠)
     */
    public static String joinPath(String... paths) {
        if (paths == null || paths.length == 0) return "";

        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            if (isNotBlank(path)) {
                // 移除开头和结尾的斜杠
                String cleanPath = path.replaceAll("^/+|/+$", "");
                if (sb.length() > 0) {
                    sb.append("/");
                }
                sb.append(cleanPath);
            }
        }
        return sb.toString();
    }

    /**
     * 转换为数据库安全字符串(防止SQL注入)
     */
    public static String toDbSafeString(String input) {
        if (input == null) return null;
        return input.replace("'", "''");
    }
}
