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
}