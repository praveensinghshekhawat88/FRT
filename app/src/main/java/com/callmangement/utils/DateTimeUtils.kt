package com.callmangement.utils

import android.annotation.SuppressLint
import android.text.TextUtils
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.TimeZone

object DateTimeUtils {
    var FMT_UTC: String = "yyyy-MM-dd HH:mm:ss"
    var FMT_UTC_DATE: String = "yyyy-MM-dd"
    var FMT_LOCAL: String = "EEE, d MMM HH:mm"
    var FMT_LOCAL_DATE: String = "EEE, d MMM yyyy"
    var DATE_SERVER: String = "yyyy-MM-dd"
    var DATE_LOCAL: String = "yyyy/MM/dd"
    var TIME: String = "HH:mm:ss"
    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS

    fun fromSec(sec: Int): String {
        @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat("d MMMM yyyy")
        val dateString = formatter.format(Date(sec * 1000L))
        return dateString
    }

    fun toString(sec: Int): String {
        var sec = sec
        if (sec < 60) return sec.toString() + "SEC"
        else {
            val min = sec / 60
            sec %= 60
            return min.toString() + "MIN " + sec + "SEC"
        }
    }

    val uTCTime: String
        get() {
            @SuppressLint("SimpleDateFormat") val sdf =
                SimpleDateFormat(FMT_UTC)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(Date())
        }

    val localTime: String
        get() {
            @SuppressLint("SimpleDateFormat") val sdf =
                SimpleDateFormat(FMT_LOCAL)
            return sdf.format(Date())
        }

    @JvmStatic
    val currentTime: String
        get() {
            @SuppressLint("SimpleDateFormat") val sdf =
                SimpleDateFormat(FMT_UTC)
            return sdf.format(Date())
        }

    fun getDateString(format: String?): String {
        return getDateString(format, Date())
    }

    fun getDate(year: Int, month: Int, day: Int): String {
        val fmt = DATE_SERVER
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat(fmt)

        val calendar = Calendar.getInstance()
        calendar[year, month] = day
        return sdf.format(calendar.time)
    }

    fun getDateForLocal(strDate: String): Date? {
        val srcDf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return try {
            srcDf.parse(strDate)
        } catch (e: ParseException) {
            null
        }
    }

    fun getUTCDate(year: Int, month: Int, day: Int): String {
        val fmt = DATE_SERVER
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat(fmt)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val calendar = Calendar.getInstance()
        calendar[year, month] = day
        return sdf.format(calendar.time)
    }


    fun getDateString(format: String?, date: Date): String {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat(format)
        return sdf.format(date)
    }

    fun getUTCDateString(format: String?, date: Date): String {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat(format)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(date)
    }

    @SuppressLint("DefaultLocale")
    fun utcYearMonth(localYear: Int, localMonth: Int): String {
        return local2utc(String.format("%d-%2d-01 00:00:00", localYear, localMonth))
    }

    fun utc2LocalCalendar(dateTime: String): Calendar {
        val cal = Calendar.getInstance()
        if (!TextUtils.isEmpty(dateTime) && !dateTime.contains(" ")) {
            @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat(FMT_UTC_DATE)
            //        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                cal.time = Objects.requireNonNull(sdf.parse(dateTime))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        } else {
            @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat(FMT_UTC)
            //        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                cal.time = Objects.requireNonNull(sdf.parse(dateTime))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return cal
    }

    @SuppressLint("SimpleDateFormat")
    fun utc2LocalCalendar(dateTime: String, isEnd: Boolean): Calendar {
        var dateTime = dateTime
        val cal = Calendar.getInstance()
        if (!TextUtils.isEmpty(dateTime) && !dateTime.contains(" ")) {
            val sdf: SimpleDateFormat
            if (isEnd) {
                dateTime += " 18:00:00"
                sdf = SimpleDateFormat(FMT_UTC)
            } else sdf = SimpleDateFormat(FMT_UTC_DATE)
            //        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                cal.time = Objects.requireNonNull(sdf.parse(dateTime))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        } else {
            val sdf = SimpleDateFormat(FMT_UTC)
            //        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                cal.time = Objects.requireNonNull(sdf.parse(dateTime))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return cal
    }


    fun utc2localDateTime(time: String): String {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat(FMT_UTC)
        @SuppressLint("SimpleDateFormat") val newSdf = SimpleDateFormat(FMT_LOCAL)
        //        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        val calendar = Calendar.getInstance()
        newSdf.timeZone = calendar.timeZone

        try {
            val date = sdf.parse(time)
            return newSdf.format(Objects.requireNonNull(date))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return time
    }

    fun utc2localDate(time: String): String {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat(FMT_UTC)
        @SuppressLint("SimpleDateFormat") val newSdf = SimpleDateFormat(FMT_LOCAL_DATE)
        //        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        val calendar = Calendar.getInstance()
        newSdf.timeZone = calendar.timeZone

        try {
            val date = sdf.parse(time)
            return newSdf.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return time
    }

    fun toLocalDate(time: String): String {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat(FMT_UTC_DATE)
        @SuppressLint("SimpleDateFormat") val newSdf = SimpleDateFormat(FMT_LOCAL_DATE)
        try {
            val date = sdf.parse(time)
            return newSdf.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return time
    }

    fun toLocalDateTime(time: String): String {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat(FMT_UTC)
        @SuppressLint("SimpleDateFormat") val newSdf = SimpleDateFormat(FMT_LOCAL)
        try {
            val date = sdf.parse(time)
            return newSdf.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return time
    }

    fun local2utc(time: String): String {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat(FMT_UTC)
        val calendar = Calendar.getInstance()
        sdf.timeZone = calendar.timeZone
        @SuppressLint("SimpleDateFormat") val newSdf = SimpleDateFormat(FMT_UTC)
        //        newSdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            val date = sdf.parse(time)
            return newSdf.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return time
    }


    fun getInterval(time: String, now: String): Long {
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat(FMT_UTC)
        try {
            val d1 = format.parse(time)
            val d2 = format.parse(now)
            return ((Objects.requireNonNull(d2).time - Objects.requireNonNull(d1).time))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    fun getDiffTimeString(time: String, now: String): String {
        val diff = getInterval(time, now)

        return if (diff < MINUTE_MILLIS) {
            "Just now"
        } else if (diff < 2 * MINUTE_MILLIS) {
            "A minute ago"
        } else if (diff < 50 * MINUTE_MILLIS) {
            (diff / MINUTE_MILLIS).toString() + " minutes ago"
        } else if (diff < 90 * MINUTE_MILLIS) {
            "An hour ago"
        } else if (diff < 24 * HOUR_MILLIS) {
            (diff / HOUR_MILLIS).toString() + " hours ago"
        } else if (diff < 48 * HOUR_MILLIS) {
            "Yesterday"
        } else {
            (diff / DAY_MILLIS).toString() + " days ago"
        }
    }

    fun getDateStringForServer(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return dateFormat.format(date)
    }

    fun getDateStringForServer(strDate: String): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
        try {
            val date = dateFormat.parse(strDate)
            val serverFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return serverFormat.format(date)
        } catch (e: ParseException) {
            return strDate
        }
    }

    fun getDateStringForLocal(date: Date): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
        return dateFormat.format(date)
    }

    fun getDateStringForLocal(strDate: String): String {
        if (TextUtils.equals(strDate, "0000-00-00")) return ""

        val srcDf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        var date: Date? = null
        try {
            date = srcDf.parse(strDate)
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
            return dateFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            return strDate
        }
    }

    fun getDateTimeStringForLocal(strDate: String): String {
        if (TextUtils.equals(strDate, "0000-00-00 00:00:00")) return ""
        val srcDf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        var date: Date? = null
        try {
            date = srcDf.parse(strDate)
            val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.US)
            return dateFormat.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            return strDate
        }
    }

    @JvmStatic
    val currentDataTime: String
        get() {
            val calendar = Calendar.getInstance()
            @SuppressLint("SimpleDateFormat") val simpleDateFormat =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return simpleDateFormat.format(calendar.time)
        }

    @JvmStatic
    fun getTimeStamp(dateStr: String): Long {
        if (dateStr != "") {
            @SuppressLint("SimpleDateFormat") val formatter: DateFormat =
                SimpleDateFormat("dd-MM-yyyy")
            var date: Date? = null
            try {
                date = formatter.parse(dateStr)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return date!!.time
        } else return 0
    }

    @JvmStatic
    val currentDate: String
        get() {
            //"2020-02-04T00:00:00"
            @SuppressLint("SimpleDateFormat") val dateFormat: DateFormat =
                SimpleDateFormat("yyyy-MM-dd")
            val date = Date()
            return dateFormat.format(date)
        }

    val currentTimeServer: String
        get() {
            //"2020-02-04T00:00:00"
            @SuppressLint("SimpleDateFormat") val dateFormat: DateFormat =
                SimpleDateFormat("hh:mm:ss")
            val currentTime = Calendar.getInstance().time
            return dateFormat.format(currentTime)
        }
}
