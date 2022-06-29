package com.ubb.citizen_u.data.repositories

import com.ubb.citizen_u.data.model.PublicSpending
import com.ubb.citizen_u.domain.model.Response
import kotlinx.coroutines.flow.Flow


interface PublicSpendingRepository {

    suspend fun getAllPublicSpending(): Flow<Response<List<PublicSpending?>>>
}