package com.example.clockapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

import java.util.Locale

// formatHour to return hour in 24h or 12h format

fun formatHour(time: LocalTime, is24hFormat: Boolean): String {
    return if (is24hFormat) {
        String.format("%02d", time.hour) // 00 - 23
    } else {
        val hour = if (time.hour % 12 == 0) 12 else time.hour % 12
        String.format("%02d", hour) // 01 - 12
    }
}


//String.format("%02d", time.minute) ensures the minute is formatted as a two-digit number

fun formatMinute(time: LocalTime): String {
    return String.format("%02d", time.minute)
}


fun formatSecond(time: LocalTime): String {
    return String.format("%02d", time.second)
}

//retrieves the date value from the LocalDate object.
fun formatDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM", Locale.ENGLISH) // "Wed, 04 Oct"
    return date.format(formatter)
}


    fun formatFullDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("EEE,\ndd MMM", Locale.ENGLISH)
    return date.format(formatter)
    // Example:
    // Thu,
    // 20 Mar
}

fun formatTimerDuration(millis: Long): String {
    val totalSeconds = millis.coerceAtLeast(0L) / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}