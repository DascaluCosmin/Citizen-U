package com.ubb.citizen_u.ui.util

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN

fun Fragment.toastMessage(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toastErrorMessage(errorMessage: String = DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN) {
    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
}