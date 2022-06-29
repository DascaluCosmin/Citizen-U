package com.ubb.citizen_u.data.repositories.impl

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.ubb.citizen_u.data.model.PublicSpending
import com.ubb.citizen_u.data.repositories.PublicSpendingRepository
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PublicSpendingRepositoryImpl @Inject constructor(
    private val publicSpendingRef: CollectionReference,
) : PublicSpendingRepository {

    companion object {
        private const val TAG = "UBB-PublicSpendingRepositoryImpl"
    }

    override suspend fun getAllPublicSpending(): Flow<Response<List<PublicSpending?>>> =
        flow {
            try {
                emit(Response.Loading)
                val listPublicSpending = publicSpendingRef.get().await()
                    .documents
                    .map {
                        it.toObject(PublicSpending::class.java)
                    }.toMutableList()

                emit(Response.Success(listPublicSpending))
            } catch (exception: Exception) {
                Log.e(TAG,
                    "An error has occurred while getting all public spending: ${exception.message}")
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }
}