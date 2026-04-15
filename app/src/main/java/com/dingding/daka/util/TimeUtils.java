package com.dingding.daka.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
     * 时间工具类
     */
public class TimeUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.CHINA);
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("HH:mm", Locale.CHINA);

    private static final Random random = new Random();

    // 动态时间范围
    private static String morningStart = "08:30";
    private static String morningEnd = "09:00";
    private static String eveningStart = "18:00";
    private static String eveningEnd = "18:10";

    /**
     * 更新随机时间生成的范围
     */
    public static void updateTimeRange(String mStart, String mEnd, String eStart, String eEnd) {
        morningStart = mStart;
        morningEnd = mEnd;
        eveningStart = eStart;
        eveningEnd = eEnd;
    }

    /**
     * 早班时间范围：8:30 - 9:00
     */
    public static final int MORNING_START_HOUR = 8;
    public static final int MORNING_START_MIN = 30;
    public static final int MORNING_END_HOUR = 9;
    public static final int MORNING_END_MIN = 0;

    /**
     * 晚班时间范围：18:00 - 18:10
     */
    public static final int EVENING_START_HOUR = 18;
    public static final int EVENING_START_MIN = 0;
    public static final int EVENING_END_HOUR = 18;
    public static final int EVENING_END_MIN = 10;

    /**
     * 获取当前日期字符串
     */
    public static String getCurrentDate() {
        return DATE_FORMAT.format(new Date());
    }

    /**
     * 获取当前时间字符串
     */
    public static String getCurrentTime() {
        return TIME_FORMAT.format(new Date());
    }

    /**
     * 格式化日期
     */
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * 格式化时间
     */
    public static String formatTime(Date date) {
        return TIME_FORMAT.format(date);
    }

    /**
     * 解析日期字符串
     */
    public static Date parseDate(String dateStr) {
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 解析时间字符串
     */
    public static Date parseTime(String timeStr) {
        try {
            return TIME_FORMAT.parse(timeStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 在指定范围内生成随机时间
     * @param startHour 开始小时
     * @param startMin 开始分钟
     * @param endHour 结束小时
     * @param endMin 结束分钟
     * @return 随机生成的时间字符串 HH:mm
     */
    public static String generateRandomTime(int startHour, int startMin, int endHour, int endMin) {
        // 转换为分钟
        int startMinutes = startHour * 60 + startMin;
        int endMinutes = endHour * 60 + endMin;

        // 生成随机分钟数
        int randomMinutes = startMinutes + random.nextInt(endMinutes - startMinutes + 1);

        // 转换回小时和分钟
        int hour = randomMinutes / 60;
        int minute = randomMinutes % 60;

        return String.format(Locale.CHINA, "%02d:%02d", hour, minute);
    }

    /**
     * 生成早班随机时间
     */
    public static String generateMorningRandomTime() {
        int[] startParts = parseTimeToHourMinute(morningStart);
        int[] endParts = parseTimeToHourMinute(morningEnd);
        return generateRandomTime(startParts[0], startParts[1], endParts[0], endParts[1]);
    }

    /**
     * 生成晚班随机时间
     */
    public static String generateEveningRandomTime() {
        int[] startParts = parseTimeToHourMinute(eveningStart);
        int[] endParts = parseTimeToHourMinute(eveningEnd);
        return generateRandomTime(startParts[0], startParts[1], endParts[0], endParts[1]);
    }

    /**
     * 计算两个时间点之间的毫秒差
     * @param time1 时间1 HH:mm
     * @param time2 时间2 HH:mm
     * @return 毫秒差
     */
    public static long getTimeDiffMillis(String time1, String time2) {
        Date d1 = parseTime(time1);
        Date d2 = parseTime(time2);
        if (d1 == null || d2 == null) return 0;
        return d1.getTime() - d2.getTime();
    }

    /**
     * 计算指定时间距离现在的毫秒数
     * @param date 日期 yyyy-MM-dd
     * @param time 时间 HH:mm
     * @return 毫秒数，如果时间已过则返回负数
     */
    public static long getTimeDiffToNow(String date, String time) {
        try {
            String dateTimeStr = date + " " + time;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            Date targetTime = sdf.parse(dateTimeStr);
            if (targetTime == null) return 0;
            return targetTime.getTime() - System.currentTimeMillis();
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * 获取指定日期是星期几
     * @param dateStr 日期 yyyy-MM-dd
     * @return 1=周一, 2=周二, ..., 7=周日
     */
    public static int getDayOfWeek(String dateStr) {
        Date date = parseDate(dateStr);
        if (date == null) return 0;
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        // 转换：周日=1，周一=2，...，周六=7
        // 我们需要：周一=1，周二=2，...，周日=7
        return dayOfWeek == Calendar.SUNDAY ? 7 : dayOfWeek - 1;
    }

    /**
     * 获取指定月份的天数
     * @param year 年份
     * @param month 月份 (1-12)
     * @return 天数
     */
    public static int getDaysInMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取指定日期的日期部分
     * @param year 年份
     * @param month 月份 (1-12)
     * @param day 日期
     * @return 格式化后的日期字符串 yyyy-MM-dd
     */
    public static String formatDate(int year, int month, int day) {
        return String.format(Locale.CHINA, "%04d-%02d-%02d", year, month, day);
    }

    /**
     * 获取今天的日期字符串
     */
    public static String getToday() {
        return formatDate(new Date());
    }

    /**
     * 判断是否是今天
     */
    public static boolean isToday(String dateStr) {
        return getToday().equals(dateStr);
    }

    /**
     * 获取明天的日期
     */
    public static String getTomorrow() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return formatDate(cal.getTime());
    }

    /**
     * 格式化倒计时显示
     * @param millis 毫秒数
     * @return HH:mm:ss 格式
     */
    public static String formatCountdown(long millis) {
        if (millis <= 0) return "00:00:00";

        long seconds = millis / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format(Locale.CHINA, "%02d:%02d:%02d", hours, minutes, secs);
    }

    /**
     * 解析时间字符串为小时和分钟
     * @param timeStr HH:mm
     * @return int[2] = {hour, minute}
     */
    public static int[] parseTimeToHourMinute(String timeStr) {
        int[] result = new int[2];
        if (timeStr == null || !timeStr.contains(":")) {
            return result;
        }
        try {
            String[] parts = timeStr.split(":");
            result[0] = Integer.parseInt(parts[0]);
            result[1] = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 比较两个时间字符串
     * @return 0=相等, 正数=time1晚于time2, 负数=time1早于time2
     */
    public static int compareTime(String time1, String time2) {
        Date d1 = parseTime(time1);
        Date d2 = parseTime(time2);
        if (d1 == null || d2 == null) return 0;
        return d1.compareTo(d2);
    }

    /**
     * 将时间字符串转换为分钟数（从00:00开始计算）
     * @param time HH:mm 格式
     * @return 分钟数
     */
    public static int timeToMinutes(String time) {
        if (time == null || !time.contains(":")) return 0;
        try {
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours * 60 + minutes;
        } catch (Exception e) {
            return 0;
        }
    }
}
