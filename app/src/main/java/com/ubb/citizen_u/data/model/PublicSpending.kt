package com.ubb.citizen_u.data.model

import com.google.firebase.firestore.DocumentId
import com.ubb.citizen_u.util.DateConverter
import com.ubb.citizen_u.util.SettingsConstants.DEFAULT_LANGUAGE
import java.util.*

class PublicSpending(
    @DocumentId var id: String = "",
    var title: Map<String, String> = mapOf(),
    var description: String? = null,
    var status: Map<String, String> = mapOf(),
    var value: Double? = null,
    var sourceOfFunding: Map<String, String> = mapOf(),
    var category: Map<String, String> = mapOf(),
    var startDate: Date? = null,
    var endDate: Date? = null,
) {

    fun getFormattedValue(): String {
        return value?.toBigDecimal()?.toPlainString() ?: "N/A"
    }

    override fun toString(): String {
        val endDateFormatted = DateConverter.convertToFormattedDateString(endDate)
        return "Title: ${title[DEFAULT_LANGUAGE]}, status: ${status[DEFAULT_LANGUAGE]}, value: ${
            value?.toBigDecimal()?.toPlainString()
        }, $endDateFormatted, sourceOfIncome: ${
            sourceOfFunding[DEFAULT_LANGUAGE]
        }, category: ${category[DEFAULT_LANGUAGE]}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PublicSpending

        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (status != other.status) return false
        if (value != other.value) return false
        if (sourceOfFunding != other.sourceOfFunding) return false
        if (category != other.category) return false
        if (startDate != other.startDate) return false
        if (endDate != other.endDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + status.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + sourceOfFunding.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + (startDate?.hashCode() ?: 0)
        result = 31 * result + (endDate?.hashCode() ?: 0)
        return result
    }
}