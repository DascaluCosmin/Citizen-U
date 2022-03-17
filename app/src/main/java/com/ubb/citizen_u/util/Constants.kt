package com.ubb.citizen_u.util

object ValidationConstants {
    const val INVALID_EMAIL_ERROR_MESSAGE = "Please enter a valid email!"
    const val INVALID_PASSWORD_ERROR_MESSAGE = "Please enter a valid password!"
    const val INVALID_FIRST_NAME_ERROR_MESSAGE = "Please enter a valid first name!"
    const val INVALID_LAST_NAME_ERROR_MESSAGE = "Please enter a valid last name!"
    const val INVALID_REPORT_INCIDENT_DESCRIPTION_ERROR_MESSAGE = "Please describe the incident!"
    const val INVALID_REPORT_INCIDENT_HEADLINE_ERROR_MESSAGE =
        "Please provide the incident headline!"
    const val INVALID_REPORT_INCIDENT_PHOTO_ERROR_MESSAGE =
        "Please provide at least one photo of the incident!"
}

object AuthenticationConstants {

    // region Success
    const val SUCCESSFUL_REGISTER_MESSAGE =
        "You have been successfully registered! Please verify your email!"
    const val SUCCESSFUL_RESET_PASSWORD_EMAIL_SENT =
        "The reset password email has been sent. Please check your email!"
    // endregion

    // region Failure
    const val FAILED_REGISTER_MESSAGE = "The registration has failed! Please try again!"
    const val FAILED_SIGN_IN_UNVERIFIED_EMAIL_MESSAGE =
        "Please verify your email before signing in!"
    const val FAILED_SIGN_IN_MESSAGE = "The sign in has failed! Please try again!"
    const val FAILED_RESET_PASSWORD_EMAIL_NOT_SENT =
        "An error has occurred! The email reset password has not been sent."
    //endregion
}

object CitizenRequestConstants {

    // region Success
    const val SUCCESSFUL_REPORT_INCIDENT = "The incident has been reported successfully!"

    // endregion
}

object LocationConstants {
    const val FAILED_ADDRESS_COMPUTING = "The address cannot be determined at the moment!"
}

object DatabaseConstants {
    const val USERS_COL = "users"
    const val USER_REQUESTS_INCIDENTS_COL = "request_incidents"
    const val PUBLIC_EVENTS_COL = "events"
    const val PUBLIC_RELEASE_EVENTS_COL = "council_events"
    const val EVENTS_PHOTOS_COL = "photos"
    const val INCIDENTS_COMMENTS_COL = "comments"
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

object NotificationsConstants {
    const val CHANNEL_ID = "NotificationsChannelId"
    const val CHANNEL_NAME = "NotificationsChannel"
    const val CHANNEL_DESCRIPTION = "Citizen-U Periodic Events Notifications"

    const val NOTIFICATION_ID = 1
    const val NOTIFICATION_WORKER_TAG = "NotificationWork"

    const val NOTIFICATION_PUBLIC_RELEASE_EVENT_ID_KEY = "publicReleaseEventDetailsId"
    const val NOTIFICATION_PERIODIC_EVENT_EVENT_ID_KEY = "periodicEventDetailsId"
}

object CalendarConstants {
    const val LAST_MONTH_OF_YEAR = "DECEMBER"
    const val UNKNOWN = "UNKNOWN_DATE"
}

const val UNDEFINED = "undefined"

const val DEFAULT_ERROR_MESSAGE = "An unexpected error has occurred"
const val DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN =
    "An unexpected error has occurred! Please try again!"
const val DEFAULT_DATE_ERROR_MESSAGE = "UNKNOWN DATE"
const val UNKNOWN = "UNKNOWN"