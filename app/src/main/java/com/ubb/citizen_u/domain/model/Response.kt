package com.ubb.citizen_u.domain.model

sealed class Response<out T> {

    object Loading : Response<Nothing>() {
        override fun toString(): String {
            return "Loading"
        }
    }

    data class Success<out T>(
        val data: T
    ) : Response<T>()

    data class Error(
        val message: String
    ) : Response<Nothing>()
}
