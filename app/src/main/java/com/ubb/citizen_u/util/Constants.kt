package com.ubb.citizen_u.util

object AuthenticationConstants {
    // region Failure
    const val FAILED_REGISTER_MESSAGE = "The registration has failed! Please try again!"
    //endregion
}

object CitizenRequestConstants {

    const val INCIDENT_GENERIC_HEADLINE = "Incident"
    const val DEFAULT_INCIDENT_CATEGORY = "other"

    // region Errors
    const val ERROR_IMAGE_NOT_LOADED = "The image could not have been loaded!"
    // endregion
}

object DatabaseConstants {
    const val USERS_COL = "users"
    const val USER_REQUESTS_INCIDENTS_COL = "request_incidents"
    const val INCIDENTS_CATEGORIES_COL = "reported_incidents_categories"
    const val PUBLIC_EVENTS_COL = "events"
    const val PUBLIC_RELEASE_EVENTS_COL = "council_events"
    const val PROPOSED_PROJECTS_COL = "proposed_projects"
    const val PUBLIC_SPENDING_COL = "public_spending"
    const val PHOTOS_COL = "photos"
    const val COMMENTS_COL = "comments"
    const val DOCUMENTS_COl = "documents"
    const val UNDEFINED_DOC = "undefined"
}

object SettingsConstants {
    //region Settings Keys
    const val LANGUAGE_SETTINGS_KEY = "language"
    const val NOTIFICATIONS_SETTINGS_KEY = "notifications"
    //endregion

    //region Settings Default Values
    const val DEFAULT_LANGUAGE = "en"
    const val DEFAULT_NOTIFICATIONS = false
    //endregion
}

object ConfigurationConstants {
    const val IMAGE_CAROUSEL_NUMBER_OF_SECONDS = 3000L
    const val SEPARATOR_VOTING_IDS = ","
}

object NotificationsConstants {
    const val CHANNEL_ID = "NotificationsChannelId"
    const val CHANNEL_NAME = "NotificationsChannel"
    const val CHANNEL_DESCRIPTION = "Citizen-U Periodic Events Notifications"

    const val NOTIFICATION_ID = 1
    const val EVENT_PUSH_NOTIFICATION_ID = 2
    const val NOTIFICATION_WORKER_TAG = "NotificationWork"

    const val NOTIFICATION_PUBLIC_RELEASE_EVENT_ID_KEY = "publicReleaseEventDetailsId"
    const val NOTIFICATION_PERIODIC_EVENT_EVENT_ID_KEY = "periodicEventDetailsId"

    const val EVENT_PUSH_NOTIFICATION_TOPIC_ID = "new_event"
    const val PUBLIC_RELEASE_PUSH_NOTIFICATION_TOPIC_ID = "council_event"
}

object CitizenConstants {
    const val CITIZEN_MISSING_ERROR_MESSAGE = "The citizen is missing"
}

object CalendarConstants {
    const val LAST_MONTH_OF_YEAR = "DECEMBER"
    const val UNKNOWN = "UNKNOWN_DATE"
}

object TownHallConstants {
    const val TOWN_HALL_LATITUDE_COORDINATE = 46.7687418
    const val TOWN_HALL_LONGITUDE_COORDINATE = 23.5876332
    const val ZOOM_WEIGHT = 14.0f
}

const val UNDEFINED = "undefined"

const val DEFAULT_ERROR_MESSAGE = "An unexpected error has occurred"
const val DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN =
    "An unexpected error has occurred! Please try again!"
const val DEFAULT_DATE_ERROR_MESSAGE = "UNKNOWN DATE"
const val UNKNOWN = "UNKNOWN"
const val DEFAULT_TESTING_ERROR = "Testing Error"

const val EMPTY_STRING = ""

object FileExtensions {
    const val PHOTO_FILE_EXTENSION = ".jpg"
    const val PDF_FILE_EXTENSION = ".pdf"
}
