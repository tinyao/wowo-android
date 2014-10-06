package com.wowo.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tinyao on 9/19/14.
 */
public class TimeUtils {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int WEEK_MILLS = 7 * DAY_MILLIS;
    private static final int MONTH_MILLS = 30 * DAY_MILLIS;


    public static String getTimeAgo(Date date) {
        long time = date.getTime();
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = getCurrentTime();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "刚刚";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1分钟前";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "分钟前";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1小时前";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + "小时前";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "昨天";
        } else if (diff < WEEK_MILLS) {
            return diff / DAY_MILLIS + "天前";
        } else if (diff < MONTH_MILLS) {
            return diff / WEEK_MILLS + "周前";
        } else {
            return diff / MONTH_MILLS + "月前";
        }
    }

    public static long getCurrentTime() {
        Calendar cc = Calendar.getInstance();
        return cc.getTimeInMillis();
    }

}
