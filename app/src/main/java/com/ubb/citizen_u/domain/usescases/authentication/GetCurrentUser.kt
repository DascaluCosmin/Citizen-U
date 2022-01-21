package com.ubb.citizen_u.domain.usescases.authentication

import com.ubb.citizen_u.data.repositories.AuthenticationRepository
import javax.inject.Inject

class GetCurrentUser @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {

    suspend operator fun invoke() = authenticationRepository.getCurrentUser()
}