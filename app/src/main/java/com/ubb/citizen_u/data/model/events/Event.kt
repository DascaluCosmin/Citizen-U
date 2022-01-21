package com.ubb.citizen_u.data.model.events

import com.ubb.citizen_u.util.UNDEFINED

data class Event(val title: String) {
    constructor() : this(title = UNDEFINED)
}
