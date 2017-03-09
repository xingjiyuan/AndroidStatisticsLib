package com.dylanxing.statistics.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {
    private static final long INTERVAL_IN_MILLISECONDS = 30000L;

    public DateUtils() {
    }

    public static String getTimestampString(Date date) {
        String timpstamp = null;
        long time = date.getTime();
        if (isSameDay(time)) {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);
            int hours = calendar.get(11);
            if (hours > 17) {
                timpstamp = "晚上 hh:mm";
            } else if (hours >= 0 && hours <= 6) {
                timpstamp = "凌晨 hh:mm";
            } else if (hours > 11 && hours <= 17) {
                timpstamp = "下午 hh:mm";
            } else {
                timpstamp = "上午 hh:mm";
            }
        } else if (isYesterday(time)) {
            timpstamp = "昨天 HH:mm";
        } else {
            timpstamp = "M月d日 HH:mm";
        }

        return (new SimpleDateFormat(timpstamp, Locale.CHINA)).format(date);
    }

    public static boolean isCloseEnough(long time1, long time2) {
        long closeTime = time1 - time2;
        if (closeTime < 0L) {
            closeTime = -closeTime;
        }

        return closeTime < 30000L;
    }

    public static boolean isSameDay(long time) {
        TimeInfo timeInfo = getTodayStartAndEndTime();
        return time > timeInfo.getStartTime() && time < timeInfo.getEndTime();
    }

    public static boolean isYesterday(long var0) {
        TimeInfo var2 = getYesterdayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    private static boolean isSameHour(long time) {
        long localTime = System.currentTimeMillis();
        return localTime - time < 1000 * 60 * 60;
    }

    private static boolean isSameMinute(long time) {
        long localTime = System.currentTimeMillis();
        return localTime - time < 1000 * 60;
    }

    public static Date StringToDate(String timeString, String timePattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timePattern);
        Date date = null;
        try {
            date = simpleDateFormat.parse(timeString);
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        return date;
    }

    public static String toTime(int var0) {
        var0 /= 1000;
        int var1 = var0 / 60;
        boolean var2 = false;
        if (var1 >= 60) {
            int var4 = var1 / 60;
            var1 %= 60;
        }

        int var3 = var0 % 60;
        return String.format("%02d:%02d", Integer.valueOf(var1), Integer.valueOf(var3));
    }

    public static String toTimeBySecond(int var0) {
        int var1 = var0 / 60;
        boolean var2 = false;
        if (var1 >= 60) {
            int var4 = var1 / 60;
            var1 %= 60;
        }

        int var3 = var0 % 60;
        return String.format("%02d:%02d", Integer.valueOf(var1), Integer.valueOf(var3));
    }

    public static TimeInfo getYesterdayStartAndEndTime() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(5, -1);
        startCalendar.set(11, 0);
        startCalendar.set(12, 0);
        startCalendar.set(13, 0);
        startCalendar.set(14, 0);
        Date startDate = startCalendar.getTime();
        long startTime = startDate.getTime();
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(5, -1);
        endCalendar.set(11, 23);
        endCalendar.set(12, 59);
        endCalendar.set(13, 59);
        endCalendar.set(14, 999);
        Date endDate = endCalendar.getTime();
        long endTime = endDate.getTime();
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setStartTime(startTime);
        timeInfo.setEndTime(endTime);
        return timeInfo;
    }

    public static TimeInfo getTodayStartAndEndTime() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(11, 0);
        startCalendar.set(12, 0);
        startCalendar.set(13, 0);
        startCalendar.set(14, 0);
        Date startDate = startCalendar.getTime();
        long startTime = startDate.getTime();
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(11, 23);
        endCalendar.set(12, 59);
        endCalendar.set(13, 59);
        endCalendar.set(14, 999);
        Date endDate = endCalendar.getTime();
        long endTime = endDate.getTime();
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setStartTime(startTime);
        timeInfo.setEndTime(endTime);
        return timeInfo;
    }

    public static TimeInfo getBeforeYesterdayStartAndEndTime() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(5, -2);
        startCalendar.set(11, 0);
        startCalendar.set(12, 0);
        startCalendar.set(13, 0);
        startCalendar.set(14, 0);
        Date startDate = startCalendar.getTime();
        long startTime = startDate.getTime();
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(5, -2);
        endCalendar.set(11, 23);
        endCalendar.set(12, 59);
        endCalendar.set(13, 59);
        endCalendar.set(14, 999);
        Date endDate = endCalendar.getTime();
        long endTime = endDate.getTime();
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setStartTime(startTime);
        timeInfo.setEndTime(endTime);
        return timeInfo;
    }

    public static TimeInfo getCurrentMonthStartAndEndTime() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(5, 1);
        startCalendar.set(11, 0);
        startCalendar.set(12, 0);
        startCalendar.set(13, 0);
        startCalendar.set(14, 0);
        Date startDate = startCalendar.getTime();
        long startTime = startDate.getTime();
        Calendar endCalendar = Calendar.getInstance();
        Date endDate = endCalendar.getTime();
        long endTime = endDate.getTime();
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setStartTime(startTime);
        timeInfo.setEndTime(endTime);
        return timeInfo;
    }

    public static TimeInfo getLastMonthStartAndEndTime() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(2, -1);
        startCalendar.set(5, 1);
        startCalendar.set(11, 0);
        startCalendar.set(12, 0);
        startCalendar.set(13, 0);
        startCalendar.set(14, 0);
        Date startDate = startCalendar.getTime();
        long startTime = startDate.getTime();
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(2, -1);
        endCalendar.set(5, 1);
        endCalendar.set(11, 23);
        endCalendar.set(12, 59);
        endCalendar.set(13, 59);
        endCalendar.set(14, 999);
        endCalendar.roll(5, -1);
        Date endDate = endCalendar.getTime();
        long endTime = endDate.getTime();
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setStartTime(startTime);
        timeInfo.setEndTime(endTime);
        return timeInfo;
    }

    public static String getTimestampStr() {
        return Long.toString(System.currentTimeMillis());
    }

    public static String getStringDate(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String formatData(long timeStamp, String dataFormat) {
        if (timeStamp == 0) {
            return "";
        }
        timeStamp = timeStamp * 1000;
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat(dataFormat);
        result = format.format(new Date(timeStamp));
        return result;
    }

    public static String getTimeStamp(Date date) {
        String timeStamp;
        long time = date.getTime();
        long localTime = System.currentTimeMillis();
        if (isSameMinute(time)) {
            int second = (int) ((localTime - time) / 1000l);
            second = second > 0 ? second : 1;
            return new StringBuilder(String.valueOf(second)).append("秒钟前").toString();
        } else if (isSameHour(time)) {
            int min = (int) ((localTime - time) / 1000l / 60l);
            return new StringBuilder(String.valueOf(min)).append("分钟前").toString();
        } else if (isSameDay(time)) {
            int hour = (int) ((localTime - time) / 1000l / 60l / 60l);
            return new StringBuilder(String.valueOf(hour)).append("小时前").toString();
        } else if (isYesterday(time)) {
            timeStamp = "昨天 HH:mm";
        } else {
            timeStamp = "M月d日 HH:mm";
        }
        return (new SimpleDateFormat(timeStamp, Locale.CHINA)).format(date);
    }
}