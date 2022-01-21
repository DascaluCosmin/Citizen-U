package com.ubb.citizen_u.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.ubb.citizen_u.domain.model.Response
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthenticationRepository {

    override fun signIn(email: String, password: String) =
        flow {
            emit(Response.Loading)
            firebaseAuth.signInWithEmailAndPassword(email, password)
            emit(Response.Success(true))
        }
}