package com.example.babyfood.util

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

/**
 * 日期时间工具类
 * 统一处理项目中所有日期时间相关操作
 */
object DateTimeUtils {

    private val timeZone = TimeZone.currentSystemDefault()

    // ==================== 获取当前时间 ====================

    /**
     * 获取当前日期
     */
    fun today(): LocalDate = Clock.System.todayIn(timeZone)

    /**
     * 获取当前时间戳（毫秒）
     */
    fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()

    /**
     * 获取当前时间戳（秒）
     */
    fun currentTimestamp(): Long = Clock.System.now().epochSeconds

    /**
     * 获取当前 LocalDateTime
     */
    fun now(): LocalDateTime = Clock.System.now().toLocalDateTime(timeZone)

    // ==================== 格式化 ====================

    /**
     * 格式化时间戳为显示字符串
     * @param timestamp 毫秒时间戳
     * @param format 格式类型，默认为 DEFAULT
     */
    fun formatTimestamp(timestamp: Long, format: DateFormat = DateFormat.DEFAULT): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(timeZone)
        return formatLocalDateTime(localDateTime, format)
    }

    /**
     * 格式化 LocalDate 为显示字符串
     */
    fun formatDate(date: LocalDate, format: DateFormat = DateFormat.DEFAULT): String {
        return when (format) {
            DateFormat.DEFAULT -> "${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}"
            DateFormat.CHINESE -> "${date.year}年${date.monthNumber}月${date.dayOfMonth}日"
            DateFormat.SHORT -> "${date.monthNumber}-${date.dayOfMonth}"
            DateFormat.YEAR_MONTH -> "${date.year}-${date.monthNumber.toString().padStart(2, '0')}"
            DateFormat.FULL_CHINESE -> "${date.year}年${date.monthNumber}月${date.dayOfMonth}日 星期${getChineseWeekDay(date)}"
        }
    }

    /**
     * 格式化 LocalDateTime 为显示字符串
     */
    fun formatLocalDateTime(dateTime: LocalDateTime, format: DateFormat = DateFormat.DEFAULT): String {
        val dateStr = formatDate(dateTime.date, format)
        val timeStr = "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
        return "$dateStr $timeStr"
    }

    // ==================== 解析 ====================

    /**
     * 解析日期字符串为 LocalDate
     * 支持格式：yyyy-MM-dd
     */
    fun parseDate(dateString: String): LocalDate? {
        return try {
            val parts = dateString.split("-")
            if (parts.size != 3) return null
            LocalDate(
                year = parts[0].toInt(),
                monthNumber = parts[1].toInt(),
                dayOfMonth = parts[2].toInt()
            )
        } catch (e: Exception) {
            null
        }
    }

    // ==================== 日期计算 ====================

    /**
     * 计算两个日期之间的天数差
     */
    fun daysBetween(start: LocalDate, end: LocalDate): Int {
        return end.toEpochDays() - start.toEpochDays()
    }

    /**
     * 计算日期加上指定天数后的日期
     */
    fun plusDays(date: LocalDate, days: Int): LocalDate {
        return date.plus(days, DateTimeUnit.DAY)
    }

    /**
     * 计算日期减去指定天数后的日期
     */
    fun minusDays(date: LocalDate, days: Int): LocalDate {
        return date.minus(days, DateTimeUnit.DAY)
    }

    /**
     * 计算日期加上指定月数后的日期
     */
    fun plusMonths(date: LocalDate, months: Int): LocalDate {
        return date.plus(months, DateTimeUnit.MONTH)
    }

    /**
     * 计算日期减去指定月数后的日期
     */
    fun minusMonths(date: LocalDate, months: Int): LocalDate {
        return date.minus(months, DateTimeUnit.MONTH)
    }

    // ==================== 日期比较 ====================

    /**
     * 检查日期是否在今天之前
     */
    fun isBeforeToday(date: LocalDate): Boolean {
        return date < today()
    }

    /**
     * 检查日期是否在今天之后
     */
    fun isAfterToday(date: LocalDate): Boolean {
        return date > today()
    }

    /**
     * 检查日期是否是今天
     */
    fun isToday(date: LocalDate): Boolean {
        return date == today()
    }

    /**
     * 检查日期是否过期（在今天之前）
     */
    fun isExpired(date: LocalDate): Boolean {
        return isBeforeToday(date)
    }

    /**
     * 计算剩余天数（从今天到目标日期）
     */
    fun daysRemaining(targetDate: LocalDate): Int {
        return daysBetween(today(), targetDate)
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取中文星期几
     */
    private fun getChineseWeekDay(date: LocalDate): String {
        return when (date.dayOfWeek.ordinal) {
            0 -> "一"
            1 -> "二"
            2 -> "三"
            3 -> "四"
            4 -> "五"
            5 -> "六"
            6 -> "日"
            else -> ""
        }
    }
}

/**
 * 日期格式枚举
 */
enum class DateFormat {
    /** 默认格式：yyyy-MM-dd */
    DEFAULT,

    /** 中文格式：yyyy年MM月dd日 */
    CHINESE,

    /** 短格式：MM-dd */
    SHORT,

    /** 年月格式：yyyy-MM */
    YEAR_MONTH,

    /** 完整中文格式：yyyy年MM月dd日 星期X */
    FULL_CHINESE
}

// ==================== 扩展函数 ====================

/**
 * LocalDate 转显示字符串
 */
fun LocalDate.format(format: DateFormat = DateFormat.DEFAULT): String {
    return DateTimeUtils.formatDate(this, format)
}

/**
 * LocalDateTime 转显示字符串
 */
fun LocalDateTime.format(format: DateFormat = DateFormat.DEFAULT): String {
    return DateTimeUtils.formatLocalDateTime(this, format)
}

/**
 * 时间戳转显示字符串
 */
fun Long.formatAsDate(format: DateFormat = DateFormat.DEFAULT): String {
    return DateTimeUtils.formatTimestamp(this, format)
}

/**
 * LocalDate 加上天数
 */
operator fun LocalDate.plus(days: Int): LocalDate {
    return DateTimeUtils.plusDays(this, days)
}

/**
 * LocalDate 减去天数
 */
operator fun LocalDate.minus(days: Int): LocalDate {
    return DateTimeUtils.minusDays(this, days)
}
