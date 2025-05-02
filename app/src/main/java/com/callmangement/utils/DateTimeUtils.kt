package com.callmangement.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class DateTimeUtils {
    public static String FMT_UTC = "yyyy-MM-dd HH:mm:ss";
    public static String FMT_UTC_DATE = "yyyy-MM-dd";
    public static String FMT_LOCAL = "EEE, d MMM HH:mm";
    public static String FMT_LOCAL_DATE = "EEE, d MMM yyyy";
    public static String DATE_SERVER = "yyyy-MM-dd";
    public static String DATE_LOCAL = "yyyy/MM/dd";
    public static String TIME = "HH:mm:ss";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String fromSec(int sec) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("d MMMM yyyy");
        String dateString = formatter.format(new Date(sec * 1000L));
        return dateString;
    }

    public static String toString(int sec) {
        if (sec < 60) return sec + "SEC";
        else {
            int min = sec / 60;
            sec %= 60;
            return min + "MIN " + sec + "SEC";
        }
    }

    public static String getUTCTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(FMT_UTC);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    public static String getLocalTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(FMT_LOCAL);
        return sdf.format(new Date());
    }

    public static String getCurrentTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(FMT_UTC);
        return sdf.format(new Date());
    }

    public static String getDateString(String format) {
        return getDateString(format, new Date());
    }

    public static String getDate(int year, int month, int day) {
        String fmt = DATE_SERVER;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(fmt);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return sdf.format(calendar.getTime());
    }

    public static Date getDateForLocal(String strDate) {
        SimpleDateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            return srcDf.parse(strDate);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getUTCDate(int year, int month, int day) {
        String fmt = DATE_SERVER;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return sdf.format(calendar.getTime());
    }


    public static String getDateString(String format, Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String getUTCDateString(String format, Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    @SuppressLint("DefaultLocale")
    public static String utcYearMonth(int localYear, int localMonth) {
        return local2utc(String.format("%d-%2d-01 00:00:00", localYear, localMonth));
    }

    public static Calendar utc2LocalCalendar(String dateTime) {
        Calendar cal = Calendar.getInstance();
        if (!TextUtils.isEmpty(dateTime) && !dateTime.contains(" ")) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(FMT_UTC_DATE);
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                cal.setTime(Objects.requireNonNull(sdf.parse(dateTime)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(FMT_UTC);
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                cal.setTime(Objects.requireNonNull(sdf.parse(dateTime)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return cal;
    }

    @SuppressLint("SimpleDateFormat")
    public static Calendar utc2LocalCalendar(String dateTime, boolean isEnd) {
        Calendar cal = Calendar.getInstance();
        if (!TextUtils.isEmpty(dateTime) && !dateTime.contains(" ")) {
            SimpleDateFormat sdf;
            if (isEnd) {
                dateTime += " 18:00:00";
                sdf = new SimpleDateFormat(FMT_UTC);
            } else sdf = new SimpleDateFormat(FMT_UTC_DATE);
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                cal.setTime(Objects.requireNonNull(sdf.parse(dateTime)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(FMT_UTC);
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                cal.setTime(Objects.requireNonNull(sdf.parse(dateTime)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return cal;
    }


    public static String utc2localDateTime(String time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(FMT_UTC);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat newSdf = new SimpleDateFormat(FMT_LOCAL);
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        newSdf.setTimeZone(calendar.getTimeZone());

        try {
            Date date = sdf.parse(time);
            return newSdf.format(Objects.requireNonNull(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String utc2localDate(String time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(FMT_UTC);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat newSdf = new SimpleDateFormat(FMT_LOCAL_DATE);
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        newSdf.setTimeZone(calendar.getTimeZone());

        try {
            Date date = sdf.parse(time);
            return newSdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String toLocalDate(String time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(FMT_UTC_DATE);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat newSdf = new SimpleDateFormat(FMT_LOCAL_DATE);
        try {
            Date date = sdf.parse(time);
            return newSdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String toLocalDateTime(String time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(FMT_UTC);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat newSdf = new SimpleDateFormat(FMT_LOCAL);
        try {
            Date date = sdf.parse(time);
            return newSdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String local2utc(String time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(FMT_UTC);
        Calendar calendar = Calendar.getInstance();
        sdf.setTimeZone(calendar.getTimeZone());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat newSdf = new SimpleDateFormat(FMT_UTC);
//        newSdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = sdf.parse(time);
            return newSdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }


    public static long getInterval(String time, String now) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(FMT_UTC);
        try {
            Date d1 = format.parse(time);
            Date d2 = format.parse(now);
            return ((Objects.requireNonNull(d2).getTime() - Objects.requireNonNull(d1).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getDiffTimeString(String time, String now) {
        long diff = getInterval(time, now);

        if (diff < MINUTE_MILLIS) {
            return "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "A minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "An hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String getDateStringForServer(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return dateFormat.format(date);
    }

    public static String getDateStringForServer(String strDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        try {
            Date date = dateFormat.parse(strDate);
            SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            return serverFormat.format(date);
        } catch (ParseException e) {
            return strDate;
        }
    }

    public static String getDateStringForLocal(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        return dateFormat.format(date);
    }

    public static String getDateStringForLocal(String strDate) {
        if (TextUtils.equals(strDate, "0000-00-00")) return "";

        SimpleDateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = null;
        try {
            date = srcDf.parse(strDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return strDate;
        }
    }

    public static String getDateTimeStringForLocal(String strDate) {
        if (TextUtils.equals(strDate, "0000-00-00 00:00:00")) return "";
        SimpleDateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = null;
        try {
            date = srcDf.parse(strDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.US);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return strDate;
        }
    }

    public static String getCurrentDataTime(){
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(calendar.getTime());
    }

    public static long getTimeStamp(String dateStr){
        if (!dateStr.equals("")) {
            @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date date = null;
            try {
                date = formatter.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return Objects.requireNonNull(date).getTime();
        }else return 0;
    }

    public static String getCurrentDate() {
        //"2020-02-04T00:00:00"
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getCurrentTimeServer() {
        //"2020-02-04T00:00:00"
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        Date currentTime = Calendar.getInstance().getTime();
        return dateFormat.format(currentTime);
    }

}
