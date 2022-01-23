package com.ubb.citizen_u.domain.usescases.authentication

import com.ubb.citizen_u.data.repositories.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResetUserPassword @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {

    suspend operator fun invoke(email: String): Flow<Boolean> =
        authenticationRepository.sendEmailResetUserPassword(email)
}