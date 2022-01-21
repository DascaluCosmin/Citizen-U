package com.ubb.citizen_u.data.repositories

import com.ubb.citizen_u.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {

    fun signIn(email: String, password: String): Flow<Response<Boolean>>
}