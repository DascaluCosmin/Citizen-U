package com.ubb.citizen_u.data.model.events

data class PeriodicEvent(
    var frequency: PeriodicEventFrequency? = null,
    var happeningDay: PeriodicEventHappeningDay? = null,
    var happeningMonth: PeriodicEventHappeningMonth? = null,
) : PublicReleaseEvent() {

    enum class PeriodicEventFrequency {
        WEEKLY, MONTHLY, ANNUALLY
    }

    enum class PeriodicEventHappeningDay {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    enum class PeriodicEventHappeningMonth {
        JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
    }

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