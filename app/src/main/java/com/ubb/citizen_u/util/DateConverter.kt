package com.ubb.citizen_u.util

import java.util.*

object DateConverter {

    fun convertToStartDate(date: Date) =
        date.apply {
            seconds = 0
            minutes = 0
            hours = 0
        }

    fun convertToEndDate(date: Date) {
        date.apply {
            seconds = 59
            minutes = 59
            hours = 23
        }
    }

    fun convertToFormattedDateString(date: Date?): String {
        if (date == null) {
            return "null"
        }

        val calendar = Calendar.getInstance()
        calendar.time = date
        return "${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH) + 1}/${
            calendar.get(Calendar.DAY_OF_MONTH)
        }"
    }
}