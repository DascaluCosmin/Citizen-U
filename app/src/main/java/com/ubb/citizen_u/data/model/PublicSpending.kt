package com.ubb.citizen_u.data.model

import com.google.firebase.firestore.DocumentId
import java.util.*

class PublicSpending(
    @DocumentId var id: String = "",
    var title: String? = null,
    var description: String? = null,
    var status: String? = null,
    var value: String? = null,
    var category: String? = null,
    var startDate: Date? = null,
    var endDate: Date? = null,
){

}