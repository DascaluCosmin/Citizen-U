package com.ubb.citizen_u.data.model.events

import com.google.firebase.firestore.DocumentId
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.util.SettingsConstants.DEFAULT_LANGUAGE
import java.util.*

abstract class Event(
    @DocumentId var id: String = "",
    var title: Map<String, String> = mapOf(),
    var category: Map<String, String> = mapOf(),
    var content: Map<String, String> = mapOf(),
    var photos: MutableList<Photo?> = mutableListOf(),
) {

    override fun toString(): String {
        return "ID = $id, Title = $title"
    }

    fun chooseRandomEventPhoto(): Photo? {
        val randomIndex = Random().nextInt(photos.size)
        return photos[randomIndex]
    }
}

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
                "Address = ${address[DEFAULT_LANGUAGE]}, " +
                "websiteUrl: $websiteUrl"
    }
}

open class PublicReleaseEvent(
    var publicationDate: Date? = null,
    var headline: Map<String, String> = mapOf(),
) : Event() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PublicReleaseEvent

        if (publicationDate != other.publicationDate) return false
        if (headline != other.headline) return false

        return true
    }

    override fun hashCode(): Int {
        var result = publicationDate?.hashCode() ?: 0
        result = 31 * result + headline.hashCode()
        return result
    }

    override fun toString(): String {
        return "${super.toString()}, " +
                "Publication Date = $publicationDate, " +
                "Headline = ${headline[DEFAULT_LANGUAGE]}"
    }
}

data class PeriodicEvent(
    var frequency: PeriodicEventFrequency? = null,
    var happeningDay: PeriodicEventHappeningDay? = null,
    var happeningMonth: PeriodicEventHappeningMonth? = null,
) : PublicReleaseEvent() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as PeriodicEvent

        if (frequency != other.frequency) return false
        if (happeningDay != other.happeningDay) return false
        if (happeningMonth != other.happeningMonth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (frequency?.hashCode() ?: 0)
        result = 31 * result + (happeningDay?.hashCode() ?: 0)
        result = 31 * result + (happeningMonth?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "${super.toString()}, " +
                "Frequency = $frequency, " +
                "Happening Day = $happeningDay, " +
                "Happening Month = $happeningMonth"
    }
}


enum class PeriodicEventFrequency {
    WEEKLY, MONTHLY, ANNUALLY
}

enum class PeriodicEventHappeningDay {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

enum class PeriodicEventHappeningMonth {
    JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
}