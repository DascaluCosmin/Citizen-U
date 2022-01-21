package com.ubb.citizen_u.data.model

import com.ubb.citizen_u.util.UNDEFINED

data class Citizen(
    val firstName: String,
    val lastName: String,
) {
    constructor() : this(firstName = UNDEFINED, lastName = UNDEFINED)
}