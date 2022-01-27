package com.ubb.citizen_u.domain.usescases.authentication

import com.ubb.citizen_u.data.model.Citizen
import com.ubb.citizen_u.data.repositories.AuthenticationRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {

    suspend operator fun invoke(email: String, password: String, citizen: Citizen) =
        authenticationRepository.registerUser(email, password, citizen)
}