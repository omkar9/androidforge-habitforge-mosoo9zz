package com.androidforge.habitforge.core.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

object DateUtils {

    fun today(): LocalDate = LocalDate.now()

    fun formatLocalDate(date: LocalDate, style: FormatStyle = FormatStyle.MEDIUM): String {
        return date.format(DateTimeFormatter.ofLocalizedDate(style))
    }

    fun formatLocalDateShort(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()))
    }

    fun isSameDay(date1: LocalDate, date2: LocalDate): Boolean {
        return date1.isEqual(date2)
    }

    fun getDaysBetween(startDate: LocalDate, endDate: LocalDate): Long {
        return ChronoUnit.DAYS.between(startDate, endDate)
    }

    /**
     * Returns a list of dates for the last 'days' days, including today.
     * The list is ordered from oldest to newest.
     */
    fun getPastDates(days: Int): List<LocalDate> {
        val today = today()
        return (0 until days).map { today.minusDays(it.toLong()) }.reversed()
    }

    /**
     * Returns a list of dates for the last 'days' days, including today, in reverse order (newest to oldest).
     */
    fun getRecentDates(days: Int): List<LocalDate> {
        val today = today()
        return (0 until days).map { today.minusDays(it.toLong()) }
    }

    fun getDayOfWeekAbbreviation(date: LocalDate): String {
        return date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())
    }
}