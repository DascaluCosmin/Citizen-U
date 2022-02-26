package com.ubb.citizen_u.ui.util

import com.ubb.citizen_u.util.CalendarConstants
import java.util.*

fun Calendar.getDayOfWeek(): String {
    return when (this.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "MONDAY"
        Calendar.TUESDAY -> "TUESDAY"
        Calendar.WEDNESDAY -> "WEDNESDAY"
        Calendar.THURSDAY -> "THURSDAY"
        Calendar.FRIDAY -> "FRIDAY"
        Calendar.SATURDAY -> "SATURDAY"
        Calendar.SUNDAY -> "SUNDAY"
        else -> CalendarConstants.UNKNOWN
    }
}

fun Calendar.getMonth(): String {
    return when (this.get(Calendar.MONTH)) {
        Calendar.JANUARY -> "JANUARY"
        Calendar.FEBRUARY -> "FEBRUARY"
        Calendar.MARCH -> "MARCH"
        Calendar.APRIL -> "APRIL"
        Calendar.MAY -> "MAY"
        Calendar.JUNE -> "JUNE"
        Calendar.JULY -> "JULY"
        Calendar.AUGUST -> "AUGUST"
        Calendar.SEPTEMBER -> "SEPTEMBER"
        Calendar.OCTOBER -> "OCTOBER"
        Calendar.NOVEMBER -> "NOVEMBER"
        Calendar.DECEMBER -> "DECEMBER"
        else -> CalendarConstants.UNKNOWN
    }
}