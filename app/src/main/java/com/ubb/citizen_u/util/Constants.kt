package com.ubb.citizen_u.util

object ValidationConstants {
    const val INVALID_EMAIL_ERROR_MESSAGE = "Please enter a valid email!"
    const val INVALID_PASSWORD_ERROR_MESSAGE = "Please enter a valid password!"
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

object Collection {

    const val USERS = "users"
}