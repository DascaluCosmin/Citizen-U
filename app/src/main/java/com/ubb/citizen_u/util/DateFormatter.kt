package com.ubb.citizen_u.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

    private const val TAG = "UBB-DateFormatter"

    private val EVENT_FORMATTER = SimpleDateFormat("dd/MM/yy", Locale.ENGLISH)

    fun toEventFormat(date: Date): String {
        return try {
            val calendar = Calendar.getInstance()
            calendar.time = date

            EVENT_FORMATTER.format(calendar.time)
        } catch (exception: Exception) {
            Log.d(
                TAG,
                "toEventFormat: An error has occurred while formatting the date to event format ${exception.message}"
            )
            DEFAULT_DATE_ERROR_MESSAGE
        }
    }

}