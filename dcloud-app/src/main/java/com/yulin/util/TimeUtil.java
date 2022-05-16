package com.yulin.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/10
 * @Description:
 */
public class TimeUtil {

    /**
     * 默认⽇期格式
     */
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_PATTERN_WITH_TIME = "yyyy-MM-dd hh:mm:ss";
    /**
     * 默认⽇期格式
     */
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER_WITH_TIME = DateTimeFormatter.ofPattern(DEFAULT_PATTERN_WITH_TIME);

    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    /**
     * LocalDateTime 转 字符串，指定⽇期格式
     * @param localDateTime
     * @param pattern
     * @return
     */
    public static String formatWithTime(LocalDateTime localDateTime, String pattern){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        String timeStr = formatter.format(localDateTime.atZone(DEFAULT_ZONE_ID));
        return timeStr;
    }
    /**
     * Date 转 字符串, 指定⽇期格式
     * @param time
     * @param pattern
     * @return
     */
    public static String formatWithTime(Date time, String pattern){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        String timeStr = formatter.format(time.toInstant().atZone(DEFAULT_ZONE_ID));
        return timeStr;
    }
    /**
     * Date 转 字符串，默认⽇期格式
     * @param time
     * @return
     */
    public static String formatWithTime(Date time){
        String timeStr = DEFAULT_DATE_TIME_FORMATTER.format(time.toInstant().atZone(DEFAULT_ZONE_ID));
        return timeStr;
    }
    /**
     * timestamp 转 字符串，默认⽇期格式
     *
     * @param timestamp
     * @return
     */
    public static String format(long timestamp) {
        String timeStr = DEFAULT_DATE_TIME_FORMATTER.format(new Date(timestamp).toInstant().atZone(DEFAULT_ZONE_ID));
        return timeStr;
    }
    /**
     * timestamp 转 字符串，默认⽇期格式
     *
     * @param timestamp
     * @return
     */
    public static String formatWithTime(long timestamp) {
        String timeStr = DEFAULT_DATE_TIME_FORMATTER_WITH_TIME.format(new Date(timestamp).toInstant().atZone(DEFAULT_ZONE_ID));
        return timeStr;
    }
    /**
     * 字符串 转 Date
     *
     * @param time
     * @return
     */
    public static Date strToDate(String time) {
        LocalDateTime localDateTime = LocalDateTime.parse(time, DEFAULT_DATE_TIME_FORMATTER);
        return Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

}
