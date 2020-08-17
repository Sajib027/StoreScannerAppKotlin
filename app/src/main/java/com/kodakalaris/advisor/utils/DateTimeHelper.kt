package com.kodakalaris.advisor.utils

import android.annotation.SuppressLint
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for parsing and formatting dates and times.
 */
@SuppressLint("SimpleDateFormat")
object DateTimeHelper {
    private val TAG = DateTimeHelper::class.java.simpleName
    private val DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd")
    private val TIME_FORMATTER = SimpleDateFormat("hh:mm a")
    private val VERBOSE_LOCALE_DATE_FORMATTER = SimpleDateFormat("MMM dd',' yyyy")
    private val VERBOSE_LOCALE_TIME_FORMATTER = SimpleDateFormat("hh:mm a z")
    private val VERBOSE_LOCALE_DATE_TIME_FORMATTER = SimpleDateFormat("MMM  dd',' yyyy hh:mm a z")
    private val ISO_8601_FORMATTER = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS")
    private val ALMOST_ISO_8601_FORMATTER = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS")

    private fun parseAlmostISO8601DateTimeWithTSeparator(datetime: String): Date? {
        Log.d(TAG, "datetime=$datetime")
        return try {
            ALMOST_ISO_8601_FORMATTER.parse(datetime)
        } catch (e: ParseException) {
            Log.e(TAG, "caught ParseException", e)
            null
        }
    }

    /**
     * Parses an ISO8601 formatted datetime and returns a
     * java.util.Date object for it, or NULL if parsing
     * the date fails.
     *
     * @param datetime
     * @return Date|null
     */
    private fun parseFromISO8601(datetime: String): Date? {
        Log.d(TAG, "datetime=$datetime")
        return try {
            ISO_8601_FORMATTER.parse(datetime)
        } catch (e: ParseException) {
            Log.e(TAG, "caught ParseException", e)
            null
        }
    }

    fun formatToIS08601(date: Date?): String {
        return ISO_8601_FORMATTER.format(date)
    }

    private fun parseOnlyDate(date: String): Date? {
        Log.d(TAG, "date=$date")
        return try {
            DATE_FORMATTER.parse(date)
        } catch (e: ParseException) {
            Log.e(TAG, "caught ParseException", e)
            null
        }
    }

    /**
     * Tries to parse most date times that it is passed, using
     * some heuristics.
     * @param
     * @return
     */
    fun parseDateTime(datetime: String): Date? {
        return if (datetime.contains("T")) {
            Log.d(TAG, "parseDateTime(): Trying ISO8601 with a T separator")
            parseAlmostISO8601DateTimeWithTSeparator(datetime)
        } else if (datetime.length == 10 && datetime.contains("-")) {
            Log.d(TAG, "parseDateTime(): Trying just yyyy-MM-dd date")
            parseOnlyDate(datetime)
        } else {
            Log.d(TAG, "parseDateTime(): Trying regular ISO8601")
            parseFromISO8601(datetime)
        }
    }

    private fun parseTime(time: String): Date? {
        return try {
            TIME_FORMATTER.parse(time)
        } catch (e: ParseException) {
            Log.d(TAG, "parseTime() caught ParseException", e)
            e.printStackTrace()
            null
        }
    }

    private fun parseDate(date: String): Date? {
        return try {
            DATE_FORMATTER.parse(date)
        } catch (e: ParseException) {
            Log.d(TAG, "parseDate() caught ParseException", e)
            e.printStackTrace()
            null
        }
    }

    fun parseDateAndTime(strDate: String, strTime: String): Date {
        val date = parseDate(strDate)
        val time = parseTime(strTime)
        val dateMillis = date!!.time
        val timeMillis = time!!.time
        return Date(dateMillis + timeMillis)
    }

    fun pad(value: Int): String {
        return if (value < 10) {
            "0$value"
        } else {
            "" + value
        }
    }

    fun toLocaleDateTime(date: Date?): String {
        return VERBOSE_LOCALE_DATE_TIME_FORMATTER.format(date)
    }

    fun toLocaleDate(date: Date?): String {
        return VERBOSE_LOCALE_DATE_FORMATTER.format(date)
    }

    fun toLocaleTime(date: Date?): String {
        return VERBOSE_LOCALE_TIME_FORMATTER.format(date)
    }

    fun toLocalTime(date: Date): Date {
        var millisUTC = date.time
        val tz = TimeZone.getDefault()
        val tzOffset = tz.getOffset(millisUTC)
        if (tz.inDaylightTime(Date(millisUTC))) {
            millisUTC -= tz.dstSavings.toLong()
        }
        return Date(millisUTC + tzOffset)
    }

    fun toLocalTime(millisUTC: Long): Long {
        // TODO: Refactor this and save it. Perfect DST code.
        var millisUTC = millisUTC
        val tz = TimeZone.getDefault()
        val tzOffset = tz.getOffset(millisUTC)
        if (tz.inDaylightTime(Date(millisUTC))) {
            millisUTC -= tz.dstSavings.toLong()
        }
        return millisUTC + tzOffset
    }
}