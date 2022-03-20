package com.ubb.citizen_u.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

    private const val TAG = "UBB-DateFormatter"

    private val EVENT_FORMATTER = SimpleDateFormat("dd/MM/yy", Locale.ENGLISH)

    fun format(date: Date): String {
        return try {
            val calendar = Calendar.getInstance()
            calendar.time = date

            EVENT_FORMATTER.format(calendar.time)
        } catch (exception: Exception) {
            Log.d(
                TAG,
                "An error has occurred while formatting the date: ${exception.message}"
            )
            DEFAULT_DATE_ERROR_MESSAGE
        }
    }

}