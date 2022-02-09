package com.ubb.citizen_u.data.repositories

import com.ubb.citizen_u.data.model.citizens.Citizen
import com.ubb.citizen_u.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface CitizenRepository {

    suspend fun getCitizen(userId: String): Flow<Response<Citizen?>>
}