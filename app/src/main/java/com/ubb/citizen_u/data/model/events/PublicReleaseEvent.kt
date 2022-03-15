package com.ubb.citizen_u.data.model.events

import com.ubb.citizen_u.util.SettingsConstants
import java.util.*

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
                "Headline = ${headline[SettingsConstants.DEFAULT_LANGUAGE]}"
    }
}