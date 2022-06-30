package com.ubb.citizen_u.data.repositories

import com.google.firebase.auth.FirebaseUser
import com.ubb.citizen_u.data.model.citizens.Citizen
import com.ubb.citizen_u.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {

    suspend fun signIn(email: String, password: String): Flow<Response<FirebaseUser?>>

    suspend fun signOut(): Flow<Response<Boolean>>

    suspend fun getCurrentUser(): Flow<Response<Citizen?>>

    suspend fun sendEmailResetUserPassword(email: String): Flow<Boolean>

    suspend fun registerUser(
        email: String,
        password: String,
        citizen: Citizen
    ): Flow<Response<Void>>
}