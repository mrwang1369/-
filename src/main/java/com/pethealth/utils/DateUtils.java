package com.pethealth.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 鏃ユ湡宸ュ叿绫?- 鎻愪緵鏃ユ湡鏍煎紡鍖栥€佽�绠楅棿闅斻€佽В鏋愮瓑甯哥敤鏂规硶
 */
public class DateUtils {

    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 鏍煎紡鍖栨棩鏈熶负瀛楃�涓?(yyyy-MM-dd)
     */
    public static String formatDate(LocalDate date) {
        return formatDate(date, DEFAULT_DATE_PATTERN);
    }

    /**
     * 鏍煎紡鍖栨棩鏈熶负瀛楃�涓?(鑷�畾涔夋牸寮?
     */
    public static String formatDate(LocalDate date, String pattern) {
        if (date == null) return null;
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 鏍煎紡鍖栨棩鏈熸椂闂翠负瀛楃�涓?(yyyy-MM-dd HH:mm:ss)
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, DEFAULT_DATETIME_PATTERN);
    }

    /**
     * 鏍煎紡鍖栨棩鏈熸椂闂翠负瀛楃�涓?(鑷�畾涔夋牸寮?
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 瑙ｆ瀽瀛楃�涓蹭负鏃ユ湡 (yyyy-MM-dd)
     */
    public static LocalDate parseDate(String dateStr) {
        return parseDate(dateStr, DEFAULT_DATE_PATTERN);
    }

    /**
     * 瑙ｆ瀽瀛楃�涓蹭负鏃ユ湡 (鑷�畾涔夋牸寮?
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        if (StringUtils.isBlank(dateStr)) return null;
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 瑙ｆ瀽瀛楃�涓蹭负鏃ユ湡鏃堕棿 (yyyy-MM-dd HH:mm:ss)
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return parseDateTime(dateTimeStr, DEFAULT_DATETIME_PATTERN);
    }

    /**
     * 瑙ｆ瀽瀛楃�涓蹭负鏃ユ湡鏃堕棿 (鑷�畾涔夋牸寮?
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (StringUtils.isBlank(dateTimeStr)) return null;
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 璁＄畻涓や釜鏃ユ湡涔嬮棿鐨勫ぉ鏁板樊
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 璁＄畻涓や釜鏃ユ湡鏃堕棿涔嬮棿鐨勫垎閽熷樊
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * 璁＄畻涓嬫�鎻愰啋鏃ユ湡 (鐢ㄤ簬椹辫櫕銆佺柅鑻楃瓑鍛ㄦ湡鎻愰啋)
     *
     * @param lastDate 涓婃�鎵ц�鏃ユ湡
     * @param cycleDays 鍛ㄦ湡澶╂暟
     * @return 涓嬫�鎻愰啋鏃ユ湡
     */
    public static LocalDate calculateNextReminderDate(LocalDate lastDate, int cycleDays) {
        if (lastDate == null) {
            return LocalDate.now();
        }
        return lastDate.plusDays(cycleDays);
    }

    /**
     * 璁＄畻瀹犵墿骞撮緞 (鏍规嵁鍑虹敓鏃ユ湡)
     *
     * @param birthDate 鍑虹敓鏃ユ湡
     * @return 骞撮緞鎻忚堪 (濡?"2宀?涓�湀")
     */
    public static String calculatePetAge(LocalDate birthDate) {
        if (birthDate == null) return "鏈�煡";

        Period period = Period.between(birthDate, LocalDate.now());
        int years = period.getYears();
        int months = period.getMonths();

        if (years == 0 && months == 0) {
            return "灏忎簬1涓�湀";
        }

        StringBuilder age = new StringBuilder();
        if (years > 0) {
            age.append(years).append("宀?);
        }
        if (months > 0) {
            if (years > 0) age.append("");
            age.append(months).append("涓�湀");
        }
        return age.toString();
    }

    /**
     * 灏咲ate杞�崲涓篖ocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 灏咲ate杞�崲涓篖ocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 鑾峰彇褰撳ぉ鐨勫紑濮嬫椂闂?(00:00:00)
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * 鑾峰彇褰撳ぉ鐨勭粨鏉熸椂闂?(23:59:59.999)
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }
}
