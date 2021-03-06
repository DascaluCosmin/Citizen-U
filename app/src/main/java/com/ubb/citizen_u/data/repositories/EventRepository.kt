package com.ubb.citizen_u.data.repositories

import com.ubb.citizen_u.data.model.events.PeriodicEvent
import com.ubb.citizen_u.data.model.events.PublicReleaseEvent
import com.ubb.citizen_u.data.model.events.PublicEvent
import com.ubb.citizen_u.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface EventRepository {

    suspend fun getAllPublicEvents(): Flow<Response<List<PublicEvent?>>>

    suspend fun getAllEventsOrderedByDate(): Flow<Response<List<PublicEvent?>>>

    suspend fun getPublicEvent(eventId: String): Flow<Response<PublicEvent?>>

    suspend fun getAllPublicReleaseEvents(): Flow<Response<List<PublicReleaseEvent?>>>

    suspend fun getAllPublicReleaseEventsOrderedByDate(): Flow<Response<List<PublicReleaseEvent?>>>

    suspend fun getPublicReleaseEvent(eventId: String): Flow<Response<PublicReleaseEvent?>>

    suspend fun getAllPeriodicEvents(): Flow<Response<List<PeriodicEvent?>>>
}