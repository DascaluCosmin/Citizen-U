package com.ubb.citizen_u.domain.usescases.authentication

import com.google.firebase.auth.FirebaseUser
import com.ubb.citizen_u.data.repositories.AuthenticationRepository
import com.ubb.citizen_u.domain.model.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {

    suspend operator fun invoke(email: String, password: String): Flow<Response<FirebaseUser?>> =
        authenticationRepository.signIn(email, password)
}