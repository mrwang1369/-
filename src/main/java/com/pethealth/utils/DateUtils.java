package com.pethealth.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期工具�?- 提供日期格式化、计算间隔、解析等常用方法
 */
public class DateUtils {

    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 格式化日期为字符�?(yyyy-MM-dd)
     */
    public static String formatDate(LocalDate date) {
        return formatDate(date, DEFAULT_DATE_PATTERN);
    }

    /**
     * 格式化日期为字符�?(自定义格�?
     */
    public static String formatDate(LocalDate date, String pattern) {
        if (date == null) return null;
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化日期时间为字符�?(yyyy-MM-dd HH:mm:ss)
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, DEFAULT_DATETIME_PATTERN);
    }

    /**
     * 格式化日期时间为字符�?(自定义格�?
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 解析字符串为日期 (yyyy-MM-dd)
     */
    public static LocalDate parseDate(String dateStr) {
        return parseDate(dateStr, DEFAULT_DATE_PATTERN);
    }

    /**
     * 解析字符串为日期 (自定义格�?
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        if (StringUtils.isBlank(dateStr)) return null;
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 解析字符串为日期时间 (yyyy-MM-dd HH:mm:ss)
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return parseDateTime(dateTimeStr, DEFAULT_DATETIME_PATTERN);
    }

    /**
     * 解析字符串为日期时间 (自定义格�?
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (StringUtils.isBlank(dateTimeStr)) return null;
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 计算两个日期之间的天数差
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个日期时间之间的分钟差
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * 计算下次提醒日期 (用于驱虫、疫苗等周期提醒)
     *
     * @param lastDate 上次执行日期
     * @param cycleDays 周期天数
     * @return 下次提醒日期
     */
    public static LocalDate calculateNextReminderDate(LocalDate lastDate, int cycleDays) {
        if (lastDate == null) {
            return LocalDate.now();
        }
        return lastDate.plusDays(cycleDays);
    }

    /**
     * 计算宠物年龄 (根据出生日期)
     *
     * @param birthDate 出生日期
     * @return 年龄描述 (�?"2�?个月")
     */
    public static String calculatePetAge(LocalDate birthDate) {
        if (birthDate == null) return "未知";

        Period period = Period.between(birthDate, LocalDate.now());
        int years = period.getYears();
        int months = period.getMonths();

        if (years == 0 && months == 0) {
            return "小于1个月";
        }

        StringBuilder age = new StringBuilder();
        if (years > 0) {
            age.append(years).append("�?);
        }
        if (months > 0) {
            if (years > 0) age.append("");
            age.append(months).append("个月");
        }
        return age.toString();
    }

    /**
     * 将Date转换为LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将Date转换为LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 获取当天的开始时�?(00:00:00)
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * 获取当天的结束时�?(23:59:59.999)
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }
}
