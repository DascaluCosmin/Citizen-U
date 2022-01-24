package com.ubb.citizen_u.data.repositories

import com.ubb.citizen_u.data.model.events.Event
import com.ubb.citizen_u.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface EventRepository {

    suspend fun getAllEvents(): Flow<Response<List<Event?>>>
}