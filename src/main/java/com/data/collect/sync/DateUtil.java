package com.data.collect.sync;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static final String FORMAT_DEFAULT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE = "yyyy-MM-dd";

    /**
     * getTomorrowDate
     * @return
     */
    public static Date getTomorrowDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DATE, 1);

        return cal.getTime();
    }

    public static String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return sdf.format(cal.getTime());
    }

    /**
     * getTomorrowDate
     * @return
     */
    public static Date getNextHour() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return cal.getTime();
    }

    public static String getThisHour() {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DEFAULT_DATE_TIME);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return sdf.format(cal.getTime());
    }

    public static String getNowTime() {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_DEFAULT_DATE_TIME);
        String dateStr = df.format(new Date());
        return dateStr;
    }

    public static String format(Date date, String format) {
        if(date == null){
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }


    public static String formatDate(Date date) {
        return format(date, FORMAT_DATE);
    }

    public static String formatDateTime(Date date) {
        return format(date, FORMAT_DEFAULT_DATE_TIME);
    }
}
