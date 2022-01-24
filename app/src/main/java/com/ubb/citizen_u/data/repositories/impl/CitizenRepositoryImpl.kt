package com.ubb.citizen_u.data.repositories.impl

import com.google.firebase.firestore.CollectionReference
import com.ubb.citizen_u.data.model.Citizen
import com.ubb.citizen_u.data.repositories.CitizenRepository
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class CitizenRepositoryImpl @Inject constructor(
    private val usersRef: CollectionReference,
) : CitizenRepository {

    override suspend fun getCitizen(userId: String): Flow<Response<Citizen?>> =
        flow {
            try {
                emit(Response.Loading)
                val citizenSnapshot = usersRef.document(userId).get().await()
                val citizen = citizenSnapshot.toObject(Citizen::class.java)
                emit(Response.Success(citizen))
            } catch (exception: Exception) {
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }
}