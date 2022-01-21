package com.ubb.citizen_u.data.repositories

import com.google.firebase.auth.FirebaseUser
import com.ubb.citizen_u.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {

    suspend fun signIn(email: String, password: String): Flow<Response<FirebaseUser?>>

    suspend fun getCurrentUser(): Flow<FirebaseUser?>
}