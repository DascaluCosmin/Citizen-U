package com.ubb.citizen_u.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthenticationRepository {

    companion object {
        const val TAG = "AuthenticationRepositoryImpl"
    }

    override suspend fun signIn(email: String, password: String) =
        flow {
            try {
                emit(Response.Loading)
                val resultAuthentication = firebaseAuth
                    .signInWithEmailAndPassword(email, password)
                    .await()
                emit(
                    Response.Success(
                        resultAuthentication.user
                    )
                )
            } catch (exception: Exception) {
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun getCurrentUser() =
        flow {
            emit(firebaseAuth.currentUser)
        }

    override suspend fun sendEmailResetUserPassword(email: String) =
        flow {
            try {
                firebaseAuth.sendPasswordResetEmail(email)
                emit(true)
            } catch (exception: Exception) {
                emit(false)
            }
        }
}