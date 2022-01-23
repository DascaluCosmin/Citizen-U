package com.ubb.citizen_u.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.ubb.citizen_u.data.model.Citizen
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.util.AuthenticationConstants
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val usersRef: CollectionReference,
) : AuthenticationRepository {

    companion object {
        const val TAG = "UBB-AuthenticationRepositoryImpl"
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

    override suspend fun registerUser(
        email: String,
        password: String,
        citizen: Citizen
    ): Flow<Response<Void>> {
        return flow {
            try {
                emit(Response.Loading)
                val authResult =
                    firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                if (authResult.user == null) {
                    emit(Response.Error(AuthenticationConstants.FAILED_REGISTER_MESSAGE))
                } else {
                    authResult.user?.apply {
                        sendEmailVerification()
                        val newUserResult = usersRef.document(uid).set(citizen).await()
                        signOut()
                        emit(Response.Success(newUserResult))
                    }
                }
            } catch (exception: Exception) {
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }
    }
}