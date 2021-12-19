package com.ubb.citizen_u.model.events

import com.ubb.citizen_u.util.UNDEFINED

data class Event(val title: String) {
    constructor() : this(title = UNDEFINED)
}
