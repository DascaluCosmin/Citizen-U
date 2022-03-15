package com.ubb.citizen_u.data.model.events

import com.ubb.citizen_u.util.SettingsConstants
import java.util.*

data class PublicEvent(
    var startDate: Date? = null,
    var endDate: Date? = null,
    var address: Map<String, String> = mapOf(),
    var location: String? = null,
    var websiteUrl: String? = null,
) : Event() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PublicEvent

        if (startDate != other.startDate) return false
        if (endDate != other.endDate) return false
        if (address != other.address) return false
        if (location != other.location) return false
        if (websiteUrl != other.websiteUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = startDate?.hashCode() ?: 0
        result = 31 * result + (endDate?.hashCode() ?: 0)
        result = 31 * result + address.hashCode()
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + (websiteUrl?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "${super.toString()}, " +
                "Location = $location, " +
                "Address = ${address[SettingsConstants.DEFAULT_LANGUAGE]}, " +
                "websiteUrl: $websiteUrl"
    }
}