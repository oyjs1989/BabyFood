package com.example.babyfood.presentation.util

import kotlinx.datetime.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * Utility for formatting dates according to system Locale.
 */
object DateTimeUtils {
    /**
     * Formats a LocalDate to a localized medium date string.
     * Example: "Mar 14, 2026" (English) or "2026年3月14日" (Chinese)
     */
    fun formatDate(date: LocalDate, locale: Locale = Locale.getDefault()): String {
        val javaDate = java.time.LocalDate.of(date.year, date.monthNumber, date.dayOfMonth)
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
        return javaDate.format(formatter)
    }

    /**
     * Formats a LocalDate to a localized Year-Month string.
     * Example: "March 2024" (English) or "2024年3月" (Chinese)
     */
    fun formatYearMonth(date: LocalDate, locale: Locale = Locale.getDefault()): String {
        val javaDate = java.time.LocalDate.of(date.year, date.monthNumber, date.dayOfMonth)
        val pattern = when (locale.language) {
            "zh" -> "yyyy年M月"
            else -> "MMMM yyyy"
        }
        val formatter = DateTimeFormatter.ofPattern(pattern).withLocale(locale)
        return javaDate.format(formatter)
    }
}
